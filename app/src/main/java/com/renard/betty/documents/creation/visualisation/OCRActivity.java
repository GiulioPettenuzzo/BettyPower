/*
 * Copyright (C) 2012, 2013, 2014, 2015 Renard Wellnitz.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.renard.betty.documents.creation.visualisation;

import com.bettypower.PalimpsestWaiting;
import com.bettypower.SingleBetActivity;
import com.bettypower.betMatchFinder.listeners.CompleteElaborationListener;
import com.bettypower.betMatchFinder.listeners.PalimpsestLoadListener;
import com.bettypower.entities.Bet;
import com.bettypower.entities.PalimpsestMatch;
import com.google.gson.Gson;
import com.googlecode.leptonica.android.Pix;
import com.googlecode.leptonica.android.Pixa;
import com.googlecode.tesseract.android.NativeBinding;
import com.googlecode.tesseract.android.OCR;
import com.renard.betty.MonitoredActivity;
import com.renard.betty.PermissionGrantedEvent;
import com.renard.betty.R;
import com.renard.betty.TextFairyApplication;
import com.renard.betty.documents.creation.crop.CropImageActivity;
import com.renard.betty.documents.creation.visualisation.LayoutQuestionDialog.LayoutChoseListener;
import com.renard.betty.documents.creation.visualisation.LayoutQuestionDialog.LayoutKind;
import com.renard.betty.documents.viewing.DocumentContentProvider;
import com.renard.betty.documents.viewing.DocumentContentProvider.Columns;
import com.renard.betty.documents.viewing.grid.DocumentGridActivity;
import com.renard.betty.documents.viewing.single.DocumentActivity;
import com.renard.betty.install.InstallActivity;
import com.renard.betty.util.ResourceUtils;
import com.renard.betty.util.Screen;
import com.renard.betty.util.Util;

import android.Manifest;
import android.app.Application;
import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.util.Pair;
import android.text.Html;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

/**
 * this activity is shown during the ocr process
 *
 * @author renard
 */
public class OCRActivity extends MonitoredActivity implements LayoutChoseListener {

    @SuppressWarnings("unused")
    private static final String TAG = OCRActivity.class.getSimpleName();

    public static final String EXTRA_PARENT_DOCUMENT_ID = "parent_id";
    private static final String OCR_LANGUAGE = "ocr_language";
    public static final String EXTRA_USE_ACCESSIBILITY_MODE = "ACCESSIBILTY_MODE";
    private static final String SCREEN_NAME = "Layout Question Dialog";


    @BindView(R.id.column_pick_completed)
    protected Button mButtonStartOCR;
    @BindView(R.id.progress_image)
    protected OCRImageView mImageView;

    private int mOriginalHeight = 0;
    private int mOriginalWidth = 0;
    private Pix mFinalPix;
    private String mOcrLanguage; // is set by dialog in
    private int mAccuracy;

    private OCR mOCR;
    // receives messages from background task
    private Messenger mMessageReceiver = new Messenger(new ProgressActivityHandler());
    // if >=0 its the id of the parent document to which the current page shall be added
    private int mParentId = -1;
    private static LayoutKind mLayout = LayoutKind.SIMPLE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        long nativePix = getIntent().getLongExtra(DocumentGridActivity.EXTRA_NATIVE_PIX, -1);
        mParentId = getIntent().getIntExtra(EXTRA_PARENT_DOCUMENT_ID, -1);
        if (nativePix == -1) {
            Intent intent = new Intent(this, DocumentGridActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        mOCR = new OCR(this, mMessageReceiver, new NativeBinding());
        Screen.lockOrientation(this);
        setContentView(R.layout.activity_ocr);
        ButterKnife.bind(this);
        initToolbar();
        ensurePermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, R.string.permission_explanation);

    }


    @SuppressWarnings("unused")
    public void onEventMainThread(final PermissionGrantedEvent event) {
        long nativePix = getIntent().getLongExtra(DocumentGridActivity.EXTRA_NATIVE_PIX, -1);
        if (nativePix != -1) {
            final Pix pixOrg = new Pix(nativePix);
            mOriginalHeight = pixOrg.getHeight();
            mOriginalWidth = pixOrg.getWidth();
            onLayoutChosen(mLayout,"Italian");
            //askUserAboutDocumentLayout();
        }
    }

    @Override
    public void onLayoutChosen(LayoutKind layoutKind, String ocrLanguage) {
        long nativePix = getIntent().getLongExtra(DocumentGridActivity.EXTRA_NATIVE_PIX, -1);
        if (nativePix != -1) {
            final Pix pixOrg = new Pix(nativePix);
            if (layoutKind == LayoutKind.DO_NOTHING) {
                saveDocument(pixOrg, null, null, 0);
            } else {
                mOcrLanguage = ocrLanguage;

                setToolbarMessage(R.string.progress_start);

                getIntent().removeExtra(DocumentGridActivity.EXTRA_NATIVE_PIX);
                if (layoutKind == LayoutKind.SIMPLE) {
                    mOCR.startOCRForSimpleLayout(OCRActivity.this, ocrLanguage, pixOrg, mImageView.getWidth(), mImageView.getHeight());
                } else if (layoutKind == LayoutKind.COMPLEX) {
                    mAccuracy = 0;
                    mOCR.startLayoutAnalysis(pixOrg, mImageView.getWidth(), mImageView.getHeight());
                }
            }
        }

    }

    /**
     * receives progress status messages from the background ocr task and
     * displays them in the current activity
     *
     * @author renard
     */
    private class ProgressActivityHandler extends Handler {

        private String hocrString;
        private String utf8String;
        //private long layoutPix;
        private int mPreviewWith;
        private int mPreviewHeight;

        private boolean mHasStartedOcr = false;

        public void handleMessage(Message msg) {
            switch (msg.what) {

                case OCR.MESSAGE_EXPLANATION_TEXT: {
                    setToolbarMessage(msg.arg1);
                    break;
                }
                case OCR.MESSAGE_TESSERACT_PROGRESS: {
                    if (!mHasStartedOcr) {
                        mAnalytics.sendScreenView("Ocr");
                        mHasStartedOcr = true;
                    }
                    int percent = msg.arg1;
                    Bundle data = msg.getData();
                    mImageView.setProgress(percent,
                            (RectF) data.getParcelable(OCR.EXTRA_WORD_BOX),
                            (RectF) data.getParcelable(OCR.EXTRA_OCR_BOX));
                    break;
                }
                case OCR.MESSAGE_PREVIEW_IMAGE: {
                    mPreviewHeight = ((Bitmap) msg.obj).getHeight();
                    mPreviewWith = ((Bitmap) msg.obj).getWidth();
                    mImageView.setImageBitmapResetBase((Bitmap) msg.obj, true, 0);
                    break;
                }
                case OCR.MESSAGE_FINAL_IMAGE: {
                    long nativePix = (long) msg.obj;

                    if (nativePix != 0) {
                        mFinalPix = new Pix(nativePix);
                    }
                    break;
                }

                case OCR.MESSAGE_LAYOUT_ELEMENTS: {
                    Pair<Long, Long> longLongPair = (Pair<Long, Long>) msg.obj;
                    long nativePixaText = longLongPair.first;
                    long nativePixaImages = longLongPair.second;
                    final Pixa texts = new Pixa(nativePixaText, 0, 0);
                    final Pixa images = new Pixa(nativePixaImages, 0, 0);
                    ArrayList<Rect> boxes = images.getBoxRects();
                    //ArrayList<Rect> boxes = images.get;
                    ArrayList<RectF> scaledBoxes = new ArrayList<>(boxes.size());
                    float xScale = (1.0f * mPreviewWith) / mOriginalWidth;
                    float yScale = (1.0f * mPreviewHeight) / mOriginalHeight;
                    // scale the to the preview image space
                    for (Rect r : boxes) {
                        scaledBoxes.add(new RectF(r.left * xScale, r.top * yScale,
                                r.right * xScale, r.bottom * yScale));
                    }
                    mImageView.setImageRects(scaledBoxes);
                    boxes = texts.getBoxRects();
                    scaledBoxes = new ArrayList<>(boxes.size());
                    for (Rect r : boxes) {
                        scaledBoxes.add(new RectF(r.left * xScale, r.top * yScale,
                                r.right * xScale, r.bottom * yScale));
                    }
                    mImageView.setTextRects(scaledBoxes);

                    mButtonStartOCR.setVisibility(View.VISIBLE);
                    mButtonStartOCR.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {

                            int[] selectedTexts = mImageView
                                    .getSelectedTextIndexes();
                            int[] selectedImages = mImageView
                                    .getSelectedImageIndexes();
                            if (selectedTexts.length > 0
                                    || selectedImages.length > 0) {
                                mImageView.clearAllProgressInfo();


                                mOCR.startOCRForComplexLayout(OCRActivity.this,
                                        mOcrLanguage, texts,
                                        images, selectedTexts, selectedImages);
                                mButtonStartOCR.setVisibility(View.GONE);
                            } else {
                                Toast.makeText(getApplicationContext(),
                                        R.string.please_tap_on_column,
                                        Toast.LENGTH_LONG).show();
                            }

                        }
                    });
                    mAnalytics.sendScreenView("Pick Columns");

                    setToolbarMessage(R.string.progress_choose_columns);

                    break;
                }
                case OCR.MESSAGE_HOCR_TEXT: {
                    this.hocrString = (String) msg.obj;
                    mAccuracy = msg.arg1;
                    break;
                }
                case OCR.MESSAGE_UTF8_TEXT: {
                    this.utf8String = (String) msg.obj;
                    break;
                }
                case OCR.MESSAGE_END: {
                    //TODO QUESTO CASE VIENE CHIAMATO QUANDO L'OCR è AL 99%, IL TESTO COMPLETO LO TROVI NELLA VARIABILE utf8String
                    saveDocument(mFinalPix, hocrString, utf8String, mAccuracy);
                    break;
                }
                case OCR.MESSAGE_ERROR: {
                    Toast.makeText(getApplicationContext(), getText(msg.arg1), Toast.LENGTH_LONG).show();
                    break;
                }
            }
        }

    }

    private ArrayList<PalimpsestMatch> allMatch = new ArrayList<>();
    File imageFile = null;
    Uri documentUri = null;

    private void saveDocument(final Pix pix, final String hocrString, final String utf8String, final int accuracy) {
        Util.startBackgroundJob(OCRActivity.this, "",
                getText(R.string.saving_document).toString(), new Runnable() {

                    @Override
                    public void run() {
                        imageFile = null;
                        documentUri = null;
                        try {
                            imageFile = saveImage(pix);
                        } catch (IOException e) {
                            e.printStackTrace();
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    Toast.makeText(
                                            getApplicationContext(),
                                            getText(R.string.error_create_file),
                                            Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                        TextFairyApplication application = (TextFairyApplication) getApplicationContext();
                        String correctResult = Html.fromHtml(utf8String).toString();
                        application.resolver.setNormalResponse(correctResult);
                        if(application.getAllPalimpsestMatch()==null) {
                            Intent intent = new Intent(OCRActivity.this, PalimpsestWaiting.class);
                            startActivity(intent);
                        }
                        application.resolver.setCompleteElaborationListener(new CompleteElaborationListener() {
                            @Override
                            public void onElaborationComplete(Bet bet) {
                                //allMatch = bet.getArrayMatch();

                                try {
                                    Gson gson = new Gson();
                                    String stringArrayMatch = gson.toJson(bet);
                                    documentUri = saveDocumentToDB(imageFile, hocrString, stringArrayMatch);
                                    if (imageFile != null) {
                                        Util.createThumbnail(OCRActivity.this, imageFile, Integer.valueOf(documentUri.getLastPathSegment()));
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();

                                    runOnUiThread(new Runnable() {

                                        @Override
                                        public void run() {
                                            Toast.makeText(
                                                    getApplicationContext(),
                                                    getText(R.string.error_create_file),
                                                    Toast.LENGTH_LONG).show();
                                        }
                                    });
                                } finally {
                                    recycleResultPix(pix);
                                    if (documentUri != null && !isFinishing()) {
                                        /**
                                         Intent i;
                                         i = new Intent(OCRActivity.this, DocumentActivity.class);
                                         i.putExtra(DocumentActivity.EXTRA_ACCURACY, accuracy);
                                         i.putExtra(DocumentActivity.EXTRA_LANGUAGE, mOcrLanguage);
                                         i.setData(documentUri);
                                         i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                         startActivity(i);
                                         */
                                        Intent intent = new Intent(OCRActivity.this, SingleBetActivity.class);
                                        //intent.putExtra("testo_completo",utf8String);
                                        intent.putExtra("all_match",bet);
                                        intent.setData(documentUri);
                                        startActivity(intent);
                                        finish();
                                        Screen.unlockOrientation(OCRActivity.this);
                                    }
                                }
                            }
                        });

                    }
                }, new Handler());

    }

    private void recycleResultPix(Pix pix) {
        if (pix != null) {
            pix.recycle();
        }
    }


    private File saveImage(Pix p) throws IOException {
        CharSequence id = DateFormat.format("ssmmhhddMMyy", new Date(System.currentTimeMillis()));
        return Util.savePixToSD(p, id.toString());
    }

    private Uri saveDocumentToDB(File imageFile, String hocr, String plainText)
            throws RemoteException {
        ContentProviderClient client = null;

        try {
            ContentValues v = new ContentValues();
            if (imageFile != null) {
                v.put(DocumentContentProvider.Columns.PHOTO_PATH,
                        imageFile.getPath());
            }
            if (hocr != null) {
                v.put(Columns.HOCR_TEXT, hocr);
            }
            if (plainText != null) {
                v.put(Columns.OCR_TEXT, plainText);
            }
            v.put(Columns.OCR_LANG, mOcrLanguage);

            if (mParentId > -1) {
                v.put(Columns.PARENT_ID, mParentId);
            }
            client = getContentResolver().acquireContentProviderClient(DocumentContentProvider.CONTENT_URI);
            return client.insert(DocumentContentProvider.CONTENT_URI, v);
        } finally {
            if (client != null) {
                client.release();
            }
        }
    }


    @Override
    protected int getHintDialogId() {
        return -1;
    }

    private void askUserAboutDocumentLayout() {
        LayoutQuestionDialog dialog = LayoutQuestionDialog.newInstance();
        dialog.show(getSupportFragmentManager(), LayoutQuestionDialog.TAG);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mOcrLanguage != null) {
            outState.putString(OCR_LANGUAGE, mOcrLanguage);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (mOcrLanguage == null) {
            mOcrLanguage = savedInstanceState.getString(OCR_LANGUAGE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public String getScreenName() {
        return "";
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (mFinalPix != null) {
            mFinalPix.recycle();
            mFinalPix = null;
        }
        mImageView.clear();

    }


}
