/*
 * Copyright (C) 2012,2013 Renard Wellnitz.
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
package com.renard.ocr.documents.viewing.grid;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bettypower.SingleBetActivity;
import com.bettypower.dialog.DeleteBetDialog;
import com.bettypower.dialog.DeleteMatchDialog;
import com.bettypower.entities.PalimpsestMatch;
import com.bettypower.entities.ParcelablePalimpsestMatch;
import com.bettypower.entities.ParcelableTeam;
import com.bettypower.unpacker.AllMatchesByPalimpsestURLUnpacker;
import com.bettypower.unpacker.AllMatchesByPalimpsestUnpacker;
import com.bettypower.util.touchHelper.ItemTouchHelperCallback;
import com.renard.ocr.PermissionGrantedEvent;
import com.renard.ocr.R;
import com.renard.ocr.TextFairyApplication;
import com.renard.ocr.documents.creation.ImageSource;
import com.renard.ocr.documents.creation.NewDocumentActivity;
import com.renard.ocr.documents.creation.PixLoadStatus;
import com.renard.ocr.documents.viewing.DocumentContentProvider;
import com.renard.ocr.documents.viewing.single.DocumentActivity;
import com.renard.ocr.main_menu.AboutActivity;
import com.renard.ocr.main_menu.FeedbackActivity;
import com.renard.ocr.main_menu.TipsActivity;
import com.renard.ocr.main_menu.language.OCRLanguageActivity;
import com.renard.ocr.main_menu.language.OcrLanguage;
import com.renard.ocr.main_menu.language.OcrLanguageDataStore;
import com.renard.ocr.util.PreferencesUtils;
import com.renard.ocr.util.Util;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Application;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.greenrobot.event.EventBus;

/**
 * main activity of the app
 *
 * @author renard
 */
public class DocumentGridActivity extends NewDocumentActivity implements DocumentGridAdapter.OnCheckedChangeListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = DocumentGridActivity.class.getSimpleName();
    private DocumentGridAdapter mDocumentAdapter;
    private GridView mGridView;

    private static final int MESSAGE_UPDATE_THUMNAILS = 1;
    private static final int DELAY_SHOW_THUMBNAILS = 550;
    private static final String SAVE_STATE_KEY = "selection";
    private static final int JOIN_PROGRESS_DIALOG = 4;
    private ActionMode mActionMode;
    private static final int REQUEST_CODE_INSTALL = 234;
    private static boolean sIsInSelectionMode = false;

    private boolean mFingerUp = true;
    private int mScrollState = AbsListView.OnScrollListener.SCROLL_STATE_IDLE;
    private final Handler mScrollHandler = new ScrollHandler();
    private boolean mPendingThumbnailUpdate = false;
    private boolean mBusIsRegistered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sendVolleyForPalimpsestMatch();
        setContentView(R.layout.activity_document_grid);
        initToolbar();
        initNavigationDrawer();
        initGridView();
        setupAddLanguageButton();
        if (savedInstanceState == null) {
            checkForImageIntent(getIntent());
        }
    }

    private void sendVolleyForPalimpsestMatch(){
        final TextFairyApplication application = (TextFairyApplication) getApplicationContext();

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        final StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://www.betcalcio.it/home/home.asp?sid={592202F0-7763-4C6B-AFFD-02F9FEFD92B5}&idca=32&idpa=2",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String response) {
                        Log.i("primo volley", "uno");
                        AllMatchesByPalimpsestURLUnpacker allMatchesByPalimpsestURLUnpacker = new AllMatchesByPalimpsestURLUnpacker(response);
                        ArrayList<String> allUrl = allMatchesByPalimpsestURLUnpacker.getAllURL();
                        //AllMatchesByPalimpsestUnpacker allMatchesByPalimpsestUnpacker = new AllMatchesByPalimpsestUnpacker(getApplicationContext(), currentURL, new AllMatchesByPalimpsestUnpacker.ResponseLoaderListener() {
                        final AllMatchesByPalimpsestUnpacker allMatchesByPalimpsestUnpacker = new AllMatchesByPalimpsestUnpacker(getApplicationContext(), allUrl, new AllMatchesByPalimpsestUnpacker.ResponseLoaderListener() {

                            @Override
                            public void onFinishResponseLoading(ArrayList<PalimpsestMatch> allPalimpsestMatches) {
                                allPalimpsestMatches = addPalimpsestForDebug(allPalimpsestMatches);
                                allPalimpsestMatches = addSpanishPalimpsestForDebug(allPalimpsestMatches);
                                allPalimpsestMatches = addReplatz(allPalimpsestMatches);
                                allPalimpsestMatches = addFivePalimpsestForDebug(allPalimpsestMatches);
                                allPalimpsestMatches = addFifteen(allPalimpsestMatches);
                                allPalimpsestMatches = addBetter(allPalimpsestMatches);
                                application.setAllPalimpsestMatch(allPalimpsestMatches);
                                application.isPalimpsestMatchLoaded = true;
                                if(application.allMatchLoadListener!=null)
                                    application.allMatchLoadListener.onMatchLoaded(allPalimpsestMatches);

                                Log.i("PALINSESTO NUMERO " ,String.valueOf(allPalimpsestMatches.size()));
                                Toast toast = Toast.makeText(DocumentGridActivity.this, String.valueOf(allPalimpsestMatches.size()), Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        });

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("errore", error.toString());
                Toast toast = Toast.makeText(DocumentGridActivity.this, "ERRORE VOLLEY", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
        //stringRequest.setShouldCache(false);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 48,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(stringRequest);
    }

    private ArrayList<PalimpsestMatch> addPalimpsestForDebug(ArrayList<PalimpsestMatch> allPalimpsestMatch){
        PalimpsestMatch paliOne = new ParcelablePalimpsestMatch(new ParcelableTeam("AMIENS"),new ParcelableTeam("OLYMPIQUE MARSIGLIA"),"27371","1050","17/09 13:00");
        PalimpsestMatch paliTwo = new ParcelablePalimpsestMatch(new ParcelableTeam("RENNES"),new ParcelableTeam("NIZZA"),"27371","1034","17/09 16:15");
        PalimpsestMatch paliTheree = new ParcelablePalimpsestMatch(new ParcelableTeam("ANGERS"),new ParcelableTeam("METZ"),"27371","1037","17/09 18:30");
        PalimpsestMatch paliFour = new ParcelablePalimpsestMatch(new ParcelableTeam("PARIS SAINT-GERMAIN"),new ParcelableTeam("OLYMPIQUE LIONE"),"27371","1049","17/09 20:45");
        PalimpsestMatch paliFive = new ParcelablePalimpsestMatch(new ParcelableTeam("NAPOLI"),new ParcelableTeam("BENEVENTO"),"27371","2512","17/09 15:00");
        PalimpsestMatch paliSix = new ParcelablePalimpsestMatch(new ParcelableTeam("MILAN"),new ParcelableTeam("UDINESE"),"27371","2511","17/09 15:00");
        PalimpsestMatch paliSeven = new ParcelablePalimpsestMatch(new ParcelableTeam("SPAL"),new ParcelableTeam("CAGLIARI"),"27371","2515","17/09 15:00");
        PalimpsestMatch paliEight = new ParcelablePalimpsestMatch(new ParcelableTeam("TORINO"),new ParcelableTeam("SAMPDORIA"),"27371","2516","17/09 15:00");
        PalimpsestMatch paliNine = new ParcelablePalimpsestMatch(new ParcelableTeam("CHIEVO VERONA"),new ParcelableTeam("ATALANTA"),"27371","2507","17/09 20:45");
        PalimpsestMatch paliTen = new ParcelablePalimpsestMatch(new ParcelableTeam("GENOA"),new ParcelableTeam("LAZIO"),"27371","2510","17/09 12:00");

        allPalimpsestMatch.add(paliOne);
        allPalimpsestMatch.add(paliTwo);
        allPalimpsestMatch.add(paliTheree);
        allPalimpsestMatch.add(paliFour);
        allPalimpsestMatch.add(paliFive);
        allPalimpsestMatch.add(paliSix);
        allPalimpsestMatch.add(paliSeven);
        allPalimpsestMatch.add(paliEight);
        allPalimpsestMatch.add(paliNine);
        allPalimpsestMatch.add(paliTen);

        return allPalimpsestMatch;

    }

    private ArrayList<PalimpsestMatch> addSpanishPalimpsestForDebug(ArrayList<PalimpsestMatch> allPalimpsestMatch){
        PalimpsestMatch paliOne = new ParcelablePalimpsestMatch(new ParcelableTeam("HELLAS VERONA"),new ParcelableTeam("NAPOLES"),"1","33","19/08 20:45");
        PalimpsestMatch paliTwo = new ParcelablePalimpsestMatch(new ParcelableTeam("JUVENTUS"),new ParcelableTeam("CAGLIARI"),"1234","435764","19/08 18:00");
        PalimpsestMatch paliTheree = new ParcelablePalimpsestMatch(new ParcelableTeam("CROTONE"),new ParcelableTeam("AC MILAN"),"1","55","20/08 20:45");
        PalimpsestMatch paliFour = new ParcelablePalimpsestMatch(new ParcelableTeam("BOLONIA"),new ParcelableTeam("TORINO"),"11","66","20/08 20:45");
        PalimpsestMatch paliFive = new ParcelablePalimpsestMatch(new ParcelableTeam("INTER MILAN"),new ParcelableTeam("FIORENTINA"),"1111","77","20/08 20:45");
        PalimpsestMatch paliSix = new ParcelablePalimpsestMatch(new ParcelableTeam("LAZIO"),new ParcelableTeam("SPAL"),"1111111","88","20/08 20:45");
        PalimpsestMatch paliSeven = new ParcelablePalimpsestMatch(new ParcelableTeam("SAMPDORIA"),new ParcelableTeam("BENEVENTO"),"111","99","20/08 20:45");
        PalimpsestMatch paliEight = new ParcelablePalimpsestMatch(new ParcelableTeam("ATALANTA"),new ParcelableTeam("ROMA"),"111","1010","20/08 18:00");
        PalimpsestMatch paliNine = new ParcelablePalimpsestMatch(new ParcelableTeam("SASSUOLO"),new ParcelableTeam("GENOA"),"111","111","20/08 20:45");
        PalimpsestMatch paliTen = new ParcelablePalimpsestMatch(new ParcelableTeam("UDINESE"),new ParcelableTeam("CHIEVO"),"1111","1122","20/08 20:45");

        allPalimpsestMatch.add(paliOne);
        allPalimpsestMatch.add(paliTwo);
        allPalimpsestMatch.add(paliTheree);
        allPalimpsestMatch.add(paliFour);
        allPalimpsestMatch.add(paliFive);
        allPalimpsestMatch.add(paliSix);
        allPalimpsestMatch.add(paliSeven);
        allPalimpsestMatch.add(paliEight);
        allPalimpsestMatch.add(paliNine);
        allPalimpsestMatch.add(paliTen);

        return allPalimpsestMatch;

    }



    private ArrayList<PalimpsestMatch> addFivePalimpsestForDebug(ArrayList<PalimpsestMatch> allPalimpsestMatch){
        PalimpsestMatch paliOne = new ParcelablePalimpsestMatch(new ParcelableTeam("REAL MADRID"),new ParcelableTeam("LEVANTE"),"27361","2856","09/09 13:00");
        PalimpsestMatch paliTwo = new ParcelablePalimpsestMatch(new ParcelableTeam("VALENCIA"),new ParcelableTeam("ATLETICO MADRID"),"27361","2865","09/09 16:15");
        PalimpsestMatch paliTheree = new ParcelablePalimpsestMatch(new ParcelableTeam("SIVIGLIA FC"),new ParcelableTeam("SD EIBAR"),"27361","2862","09/09 18:30");
        PalimpsestMatch paliFour = new ParcelablePalimpsestMatch(new ParcelableTeam("BARCELLONA"),new ParcelableTeam("ESPANYOL BARCELLONA"),"27361","2860","09/09 20:45");
        PalimpsestMatch paliFive = new ParcelablePalimpsestMatch(new ParcelableTeam("DEPORTIVO LA CORUNA"),new ParcelableTeam("REAL SOCIEDAD"),"27361","2857","10/09 12:00");

        allPalimpsestMatch.add(paliOne);
        allPalimpsestMatch.add(paliTwo);
        allPalimpsestMatch.add(paliTheree);
        allPalimpsestMatch.add(paliFour);
        allPalimpsestMatch.add(paliFive);

        return allPalimpsestMatch;

    }

    private ArrayList<PalimpsestMatch> addReplatz(ArrayList<PalimpsestMatch> allPalimpsestMatch){
        PalimpsestMatch paliOne = new ParcelablePalimpsestMatch(new ParcelableTeam("JUVENTUS"),new ParcelableTeam("CAGLIARI"),"27331","4682","19/08 18:00");
        PalimpsestMatch paliTwo = new ParcelablePalimpsestMatch(new ParcelableTeam("HELLAS VERONA"),new ParcelableTeam("NAPOLI"),"27331","4680","19/08 20:45");
        PalimpsestMatch paliTheree = new ParcelablePalimpsestMatch(new ParcelableTeam("BOLOGNA"),new ParcelableTeam("TORINO"),"27331","4678","20/08 20:45");
        PalimpsestMatch paliFour = new ParcelablePalimpsestMatch(new ParcelableTeam("INTER"),new ParcelableTeam("ACF FIORENTINA"),"27331","4681","20/08 20:45");
        PalimpsestMatch paliFive = new ParcelablePalimpsestMatch(new ParcelableTeam("SASSUOLO"),new ParcelableTeam("GENOA"),"27331","4685","20/08 20:45");
        PalimpsestMatch paliSix = new ParcelablePalimpsestMatch(new ParcelableTeam("UDINESE"),new ParcelableTeam("CHIEVO VERONA"),"27331","4686","20/08 20:45");
        PalimpsestMatch paliSeven = new ParcelablePalimpsestMatch(new ParcelableTeam("SAMPDORIA"),new ParcelableTeam("BENEVENTO"),"27331","4712","20/08 20:45");
        PalimpsestMatch paliEight = new ParcelablePalimpsestMatch(new ParcelableTeam("LAZIO"),new ParcelableTeam("SPAL"),"27331","4713","20/08 20:45");
        PalimpsestMatch paliNine = new ParcelablePalimpsestMatch(new ParcelableTeam("ATALANTA"),new ParcelableTeam("ROMA"),"27331","4677","20/08 18:00");
        PalimpsestMatch paliTen = new ParcelablePalimpsestMatch(new ParcelableTeam("CROTONE"),new ParcelableTeam("MILAN"),"27331","4679","21/08 20:45");
        PalimpsestMatch paliEleven = new ParcelablePalimpsestMatch(new ParcelableTeam("BAYERN MONACO"),new ParcelableTeam("BAYERN LEVERKUSEN"),"27331","1760","18/08 20:30");
        PalimpsestMatch paliTwelve = new ParcelablePalimpsestMatch(new ParcelableTeam("HOFFENHEIM"),new ParcelableTeam("WARDER BREMA"),"27331","1751","19/08 15:30");
        PalimpsestMatch pailThirteen = new ParcelablePalimpsestMatch(new ParcelableTeam("AMBURGO"),new ParcelableTeam("AUGSBURG"),"27331","1769","19/08 15:30");
        PalimpsestMatch paliFourteen = new ParcelablePalimpsestMatch(new ParcelableTeam("MAINZ 05"),new ParcelableTeam("HANNOVER"),"27331","1761","19/08 15:30");
        PalimpsestMatch pailFifteen = new ParcelablePalimpsestMatch(new ParcelableTeam("WOLFSBURG"),new ParcelableTeam("BORUSSIA DORTMUND"),"27331","1763","19/08 15:30");


        allPalimpsestMatch.add(paliOne);
        allPalimpsestMatch.add(paliTwo);
        allPalimpsestMatch.add(paliTheree);
        allPalimpsestMatch.add(paliFour);
        allPalimpsestMatch.add(paliFive);
        allPalimpsestMatch.add(paliSix);
        allPalimpsestMatch.add(paliSeven);
        allPalimpsestMatch.add(paliEight);
        allPalimpsestMatch.add(paliNine);
        allPalimpsestMatch.add(paliTen);
        allPalimpsestMatch.add(paliEleven);
        allPalimpsestMatch.add(paliTwelve);
        allPalimpsestMatch.add(pailThirteen);
        allPalimpsestMatch.add(paliFourteen);
        allPalimpsestMatch.add(pailFifteen);

        return allPalimpsestMatch;
    }

    private ArrayList<PalimpsestMatch> addBetter(ArrayList<PalimpsestMatch> allPalimpsestMatch){
        PalimpsestMatch paliOne = new ParcelablePalimpsestMatch(new ParcelableTeam("MELBOURNE CITY FC"),new ParcelableTeam("WELLINGTON PHOENIX"),"27421","3739","21/10 08:35");
        PalimpsestMatch paliTwo = new ParcelablePalimpsestMatch(new ParcelableTeam("FC SKA-ENERGIYA KHABAROVSK"),new ParcelableTeam("UFA"),"27421","656","21/10 10:30");
        PalimpsestMatch paliTheree = new ParcelablePalimpsestMatch(new ParcelableTeam("SYDNEY"),new ParcelableTeam("WESTERN SYDNEY"),"27421","3737","21/10 10:50");
        PalimpsestMatch paliFour = new ParcelablePalimpsestMatch(new ParcelableTeam("FK TOSNO"),new ParcelableTeam("FC ROSTOV"),"27421","658","21/10 13:00");
        PalimpsestMatch paliFive = new ParcelablePalimpsestMatch(new ParcelableTeam("LEVANTE"),new ParcelableTeam("GETAFE"),"27421","1463","21/10 13:00");
        PalimpsestMatch paliSix = new ParcelablePalimpsestMatch(new ParcelableTeam("CHELSEA"),new ParcelableTeam("WATFORD FC"),"27421","554","21/10 13:30");
        PalimpsestMatch paliSeven = new ParcelablePalimpsestMatch(new ParcelableTeam("CITTADELLA"),new ParcelableTeam("CREMONESE"),"27474","8651","26/11 15:00");
        allPalimpsestMatch.add(paliOne);
        allPalimpsestMatch.add(paliTwo);
        allPalimpsestMatch.add(paliTheree);
        allPalimpsestMatch.add(paliFour);
        allPalimpsestMatch.add(paliFive);
        allPalimpsestMatch.add(paliSix);
        allPalimpsestMatch.add(paliSeven);
        return allPalimpsestMatch;
    }

    private ArrayList<PalimpsestMatch> addFifteen(ArrayList<PalimpsestMatch> allPalimpsestMatch){
        PalimpsestMatch paliOne = new ParcelablePalimpsestMatch(new ParcelableTeam("BAYER LEVERKUSEN"),new ParcelableTeam("FRIBURGO"),"27371","1533","17/09 15:30");
        PalimpsestMatch paliTwo = new ParcelablePalimpsestMatch(new ParcelableTeam("BORUSSIA DORTMUND"),new ParcelableTeam("COLONIA"),"27371","1532","17/09 18:00");
        PalimpsestMatch paliTheree = new ParcelablePalimpsestMatch(new ParcelableTeam("HOFFENHEIM"),new ParcelableTeam("HERTHA BERLINO"),"27371","1529","17/09 13:30");
        PalimpsestMatch paliFour = new ParcelablePalimpsestMatch(new ParcelableTeam("REAL SOCIEDAD"),new ParcelableTeam("REAL MADRID"),"27371","2368","17/09 20:45");
        PalimpsestMatch paliFive = new ParcelablePalimpsestMatch(new ParcelableTeam("GIRONA"),new ParcelableTeam("SIVIGLIA FC"),"27371","2374","17/09 16:15");
        PalimpsestMatch paliSix = new ParcelablePalimpsestMatch(new ParcelableTeam("LAZIO"),new ParcelableTeam("SPAL"),"1111111","88","20/08 20:45");
        PalimpsestMatch paliSeven = new ParcelablePalimpsestMatch(new ParcelableTeam("LAS PALMAS UD"),new ParcelableTeam("ATHLETIC BILBAO"),"27371","2375","17/09 18:30");
        PalimpsestMatch paliEight = new ParcelablePalimpsestMatch(new ParcelableTeam("MONACO"),new ParcelableTeam("RACING STRASBURGO"),"27371","1035","16/09 17:00");
        PalimpsestMatch paliNine = new ParcelablePalimpsestMatch(new ParcelableTeam("SASSUOLO"),new ParcelableTeam("JUVENTUS"),"27371","2514","17/09 12:30");
        PalimpsestMatch paliTen = new ParcelablePalimpsestMatch(new ParcelableTeam("UDINESE"),new ParcelableTeam("CHIEVO"),"1111","1122","20/08 20:45");
        PalimpsestMatch paliEleven = new ParcelablePalimpsestMatch(new ParcelableTeam("LAZIO"),new ParcelableTeam("SPAL"),"1111111","88","20/08 20:45");
        PalimpsestMatch paliTwelve = new ParcelablePalimpsestMatch(new ParcelableTeam("SAMPDORIA"),new ParcelableTeam("BENEVENTO"),"111","99","20/08 20:45");
        PalimpsestMatch pailThirteen = new ParcelablePalimpsestMatch(new ParcelableTeam("ATALANTA"),new ParcelableTeam("ROMA"),"111","1010","20/08 18:00");
        PalimpsestMatch paliFourteen = new ParcelablePalimpsestMatch(new ParcelableTeam("SASSUOLO"),new ParcelableTeam("GENOA"),"111","111","20/08 20:45");
        PalimpsestMatch pailFifteen = new ParcelablePalimpsestMatch(new ParcelableTeam("UDINESE"),new ParcelableTeam("CHIEVO"),"1111","1122","20/08 20:45");
        PalimpsestMatch paliSixteen = new ParcelablePalimpsestMatch(new ParcelableTeam("ATLETICO MADRID"),new ParcelableTeam("BARCELLONA"),"27411","1829","14/10 20:45");
        PalimpsestMatch pailSeventeen = new ParcelablePalimpsestMatch(new ParcelableTeam("ALAVES"),new ParcelableTeam("REAL SOCIEDAD"),"27411","1831","14/10 18:30");


        allPalimpsestMatch.add(paliOne);
        allPalimpsestMatch.add(paliTwo);
        allPalimpsestMatch.add(paliTheree);
        allPalimpsestMatch.add(paliFour);
        allPalimpsestMatch.add(paliFive);
        allPalimpsestMatch.add(paliSix);
        allPalimpsestMatch.add(paliSeven);
        allPalimpsestMatch.add(paliEight);
        allPalimpsestMatch.add(paliNine);
        allPalimpsestMatch.add(paliTen);
        allPalimpsestMatch.add(paliEleven);
        allPalimpsestMatch.add(paliTwelve);
        allPalimpsestMatch.add(pailThirteen);
        allPalimpsestMatch.add(paliFourteen);
        allPalimpsestMatch.add(pailFifteen);
        allPalimpsestMatch.add(paliSixteen);
        allPalimpsestMatch.add(pailSeventeen);

        return allPalimpsestMatch;
    }

    @Override
    protected void onResume() {
        // ViewServer.get(this).setFocusedWindow(this);
        super.onResume();
        if (!mBusIsRegistered) {
            EventBus.getDefault().register(this);
            mBusIsRegistered = true;
        }
        ensurePermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, R.string.permission_explanation);
    }


    @SuppressWarnings("unused")
    public void onEventMainThread(final PermissionGrantedEvent event) {
        Log.i(LOG_TAG, "Permission Granted");
        startInstallActivityIfNeeded();
        initThumbnailSize();
    }


    @Override
    protected int getHintDialogId() {
        return HINT_DIALOG_ID;
    }


    private void initThumbnailSize() {
        final int columnWidth = Util.determineThumbnailSize(this, null);
        Util.setThumbnailSize(columnWidth, columnWidth, this);
    }

    private void setupAddLanguageButton() {
        final View viewById = findViewById(R.id.install_language_button);
        viewById.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DocumentGridActivity.this, OCRLanguageActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        checkForImageIntent(intent);
    }

    private void checkForImageIntent(Intent intent) {

        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            Uri imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
            if (imageUri != null) {
                loadBitmapFromContentUri(imageUri, ImageSource.INTENT);
            } else {
                showFileError(PixLoadStatus.IMAGE_COULD_NOT_BE_READ, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
            }

        }
    }

    /**
     * Start the InstallActivity if possible and needed.
     */
    private void startInstallActivityIfNeeded() {
        final List<OcrLanguage> installedOCRLanguages = OcrLanguageDataStore.getInstalledOCRLanguages(this);
        final String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            if (installedOCRLanguages.isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setClassName(this, com.renard.ocr.install.InstallActivity.class.getName());
                startActivityForResult(intent, REQUEST_CODE_INSTALL);
            }
        } else {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setMessage(getString(R.string.no_sd_card));
            alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            alert.show();
        }
    }

    private void initNavigationDrawer() {
        final DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView view = (NavigationView) findViewById(R.id.left_drawer);
        handleNavigationMenuSelection(drawerLayout, view);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, (Toolbar) findViewById(R.id.toolbar), R.string.drawer_open, R.string.drawer_close) {
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
                syncState();
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
                syncState();
            }
        };
        drawerLayout.setDrawerListener(actionBarDrawerToggle);


        // Enable ActionBar app icon to behave as action to toggle nav drawer
        getSupportActionBar().setHomeButtonEnabled(true);
        actionBarDrawerToggle.syncState();
        if (checkForFirstStart()) {
            drawerLayout.openDrawer(GravityCompat.START);
        }

    }

    private boolean checkForFirstStart() {
        final boolean firstStart = PreferencesUtils.isFirstStart(getApplicationContext());
        if (firstStart) {
            PreferencesUtils.setFirstStart(getApplicationContext(), false);
        }
        return firstStart;
    }

    private void handleNavigationMenuSelection(final DrawerLayout drawerLayout, NavigationView view) {
        view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                drawerLayout.closeDrawers();
                switch (menuItem.getItemId()) {
                    case R.id.show_tips:
                        startActivity(new Intent(DocumentGridActivity.this, TipsActivity.class));
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        break;
                    case R.id.feedback:
                        startActivity(new Intent(DocumentGridActivity.this, FeedbackActivity.class));
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        break;
                    case R.id.about:
                        startActivity(new Intent(DocumentGridActivity.this, AboutActivity.class));
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        break;
                }
                return true;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_INSTALL) {
            if (RESULT_OK == resultCode) {
                // install successfull, show happy fairy or introduction text

            } else {
                // install failed, quit immediately
                finish();
            }

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public static boolean isInSelectionMode() {
        return sIsInSelectionMode;
    }



    @Override
    public void onCheckedChanged(Set<Integer> checkedIds) {
        if (mActionMode == null && checkedIds.size() > 0) {
            mActionMode = startSupportActionMode(new DocumentActionCallback());
        } else if (mActionMode != null && checkedIds.size() == 0) {
            mActionMode.finish();
            mActionMode = null;
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Set<Integer> selection = mDocumentAdapter.getSelectedDocumentIds();
        ArrayList<Integer> save = new ArrayList<Integer>(selection.size());
        save.addAll(selection);
        outState.putIntegerArrayList(SAVE_STATE_KEY, save);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        ArrayList<Integer> selection = savedInstanceState.getIntegerArrayList(SAVE_STATE_KEY);
        mDocumentAdapter.setSelectedDocumentIds(selection);
    }

    public class DocumentClickListener implements OnItemClickListener {
//TODO qui è quando seleziono un item per vederne il contenuto
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            DocumentGridAdapter.DocumentViewHolder holder = (DocumentGridAdapter.DocumentViewHolder) view.getTag();
            if (sIsInSelectionMode) {
                holder.gridElement.toggle();
            } else {
                Intent i = new Intent(DocumentGridActivity.this, SingleBetActivity.class);
                long documentId = mDocumentAdapter.getItemId(position);
                Uri uri = Uri.withAppendedPath(DocumentContentProvider.CONTENT_URI, String.valueOf(documentId));
                i.setData(uri);
                startActivity(i);
            }
        }
    }

    private class DocumentLongClickListener implements OnItemLongClickListener {

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            CheckableGridElement clicked = (CheckableGridElement) view;

            if (!sIsInSelectionMode) {
                sIsInSelectionMode = true;
                clicked.toggle();
                final int childCount = parent.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    CheckableGridElement element = (CheckableGridElement) parent.getChildAt(i);
                    if (element != view) {
                        element.setChecked(false);
                    }
                }
            } else {
                clicked.toggle();
            }
            return true;
        }
    }

    int getScrollState() {
        return mScrollState;
    }

    private class DocumentScrollListener implements AbsListView.OnScrollListener {
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if (mScrollState == SCROLL_STATE_FLING && scrollState != SCROLL_STATE_FLING) {
                final Handler handler = mScrollHandler;
                final Message message = handler.obtainMessage(MESSAGE_UPDATE_THUMNAILS, DocumentGridActivity.this);
                handler.removeMessages(MESSAGE_UPDATE_THUMNAILS);
                handler.sendMessageDelayed(message, mFingerUp ? 0 : DELAY_SHOW_THUMBNAILS);
                mPendingThumbnailUpdate = true;
            } else if (scrollState == SCROLL_STATE_FLING) {
                mPendingThumbnailUpdate = false;
                mScrollHandler.removeMessages(MESSAGE_UPDATE_THUMNAILS);
            }

            mScrollState = scrollState;
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        }
    }

    private static class ScrollHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_UPDATE_THUMNAILS:
                    ((DocumentGridActivity) msg.obj).updateDocumentThumbnails();
                    break;
            }
        }
    }

    private class FingerTracker implements View.OnTouchListener {
        public boolean onTouch(View view, MotionEvent event) {
            final int action = event.getAction();
            mFingerUp = action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL;
            if (mFingerUp && mScrollState != DocumentScrollListener.SCROLL_STATE_FLING) {
                postDocumentThumbnails();
            }
            return false;
        }
    }

    @Override
    public void onBackPressed() {
        if (mDocumentAdapter.getSelectedDocumentIds().size() > 0) {
            cancelMultiSelectionMode();
        } else {
            super.onBackPressed();

        }
    }

    public boolean isPendingThumbnailUpdate() {
        return mPendingThumbnailUpdate;
    }

    private void updateDocumentThumbnails() {
        mPendingThumbnailUpdate = false;

        final GridView grid = mGridView;
        final int count = grid.getChildCount();

        for (int i = 0; i < count; i++) {
            final View view = grid.getChildAt(i);
            final DocumentGridAdapter.DocumentViewHolder holder = (DocumentGridAdapter.DocumentViewHolder) view.getTag();
            if (holder.updateThumbnail) {
                final int documentId = holder.documentId;
                CrossFadeDrawable d = holder.transition;
                FastBitmapDrawable thumb = Util.getDocumentThumbnail(documentId);
                if (thumb.getBitmap() != null) {
                    d.setEnd(thumb.getBitmap());
                    holder.gridElement.setImage(d);
                    d.startTransition(375);
                }
                holder.updateThumbnail = false;
            }
        }

        grid.invalidate();
    }

    private void postDocumentThumbnails() {
        Handler handler = mScrollHandler;
        Message message = handler.obtainMessage(MESSAGE_UPDATE_THUMNAILS, DocumentGridActivity.this);
        handler.removeMessages(MESSAGE_UPDATE_THUMNAILS);
        mPendingThumbnailUpdate = true;
        handler.sendMessage(message);
    }

    private class DocumentActionCallback implements ActionMode.Callback {

        DeleteBetDialog deleteBetDialog;
        @Override
        public boolean onActionItemClicked(final ActionMode mode, MenuItem item) {
            int itemId = item.getItemId();
            if (itemId == R.id.item_delete) {
                 deleteBetDialog = new DeleteBetDialog(DocumentGridActivity.this, new DeleteBetDialog.DeleteBetDialogListener() {
                    @Override
                    public void onBetDelete() {
                        new DeleteDocumentTask(mDocumentAdapter.getSelectedDocumentIds(), false).execute();
                        cancelMultiSelectionMode();
                        mode.finish();
                        deleteBetDialog.dismiss();
                    }
                },mDocumentAdapter.getSelectedDocumentIds().size());
                deleteBetDialog.show();
                return true;
            }
            return true;
        }


        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            getMenuInflater().inflate(R.menu.grid_action_mode, menu);
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.action_bar_edit_mode)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(getResources().getColor(R.color.action_bar_edit_mode));
            }
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }


        @Override
        public void onDestroyActionMode(ActionMode mode) {
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.primary)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(getResources().getColor(R.color.primary));
            }
            if (mActionMode != null) {
                mActionMode = null;
                cancelMultiSelectionMode();
            }
            mActionMode = null;

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
        mBusIsRegistered = false;
    }

    @Override
    public String getScreenName() {
        return "Document Grid";
    }


    private void initGridView() {
        mGridView = (GridView) findViewById(R.id.gridview);
        mDocumentAdapter = new DocumentGridAdapter(this, R.layout.item_grid_document_element, this);
        registerForContextMenu(mGridView);
        mGridView.setAdapter(mDocumentAdapter);
        mGridView.setLongClickable(true);
        mGridView.setOnItemClickListener(new DocumentClickListener());
        mGridView.setOnItemLongClickListener(new DocumentLongClickListener());
        mGridView.setOnScrollListener(new DocumentScrollListener());
        mGridView.setOnTouchListener(new FingerTracker());
        final int[] outNum = new int[1];
        final int columnWidth = Util.determineThumbnailSize(this, outNum);
        mGridView.setColumnWidth(columnWidth);
       // mGridView.setNumColumns(outNum[0]);
        mGridView.setNumColumns(1);
        final View emptyView = findViewById(R.id.empty_view);
        mGridView.setEmptyView(emptyView);
    }

    @Override
    protected int getParentId() {
        return -1;
    }

    public void cancelMultiSelectionMode() {
        mDocumentAdapter.getSelectedDocumentIds().clear();
        sIsInSelectionMode = false;
        final int childCount = mGridView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View v = mGridView.getChildAt(i);
            final DocumentGridAdapter.DocumentViewHolder holder = (DocumentGridAdapter.DocumentViewHolder) v.getTag();
            holder.gridElement.setChecked(false);
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case JOIN_PROGRESS_DIALOG:
                ProgressDialog d = new ProgressDialog(this);
                d.setTitle(R.string.join_documents_title);
                d.setIndeterminate(true);
                return d;
        }
        return super.onCreateDialog(id);
    }

    @Override
    public Loader<Cursor> onCreateLoader(final int documentId, final Bundle bundle) {
        final Uri uri = Uri.withAppendedPath(DocumentContentProvider.CONTENT_URI, String.valueOf(documentId));
        return new CursorLoader(this, uri, new String[]{DocumentContentProvider.Columns.TITLE, DocumentContentProvider.Columns.ID}, null, null, "created ASC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor.moveToFirst()) {
            final int titleIndex = cursor.getColumnIndex(DocumentContentProvider.Columns.TITLE);
            final String oldTitle = cursor.getString(titleIndex);
            final int idIndex = cursor.getColumnIndex(DocumentContentProvider.Columns.ID);
            final String documentId = String.valueOf(cursor.getInt(idIndex));
            final Uri documentUri = Uri.withAppendedPath(DocumentContentProvider.CONTENT_URI, documentId);
            askUserForNewTitle(oldTitle, documentUri);
        }
        getSupportLoaderManager().destroyLoader(loader.getId());
    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
    }

    private void joinDocuments(final Set<Integer> selectedDocs) {
        new JoinDocumentsTask(selectedDocs, getApplicationContext()).execute();
    }

    protected class JoinDocumentsTask extends AsyncTask<Void, Integer, Integer> {

        private Set<Integer> mIds = new HashSet<Integer>();
        private final Context mContext;

        public JoinDocumentsTask(Set<Integer> ids, Context c) {
            mIds.addAll(ids);
            mContext = c;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog(JOIN_PROGRESS_DIALOG);
        }

        @Override
        protected void onPostExecute(Integer result) {
            String msg = mContext.getString(R.string.join_documents_result);
            msg = String.format(msg, result);
            Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
            dismissDialog(JOIN_PROGRESS_DIALOG);
        }

        private String buidlInExpr(final Collection<Integer> ids) {
            final int length = ids.size();
            int count = 0;
            StringBuilder builder = new StringBuilder();
            builder.append(" in (");
            for (@SuppressWarnings("unused")
            Integer id : ids) {
                builder.append("?");
                if (count < length - 1) {
                    builder.append(",");
                }
                count++;
            }
            builder.append(")");
            return builder.toString();
        }

        @Override
        protected Integer doInBackground(Void... params) {
            int count = 0;
            final Integer parentId = Collections.min(mIds);
            final int documentCount = mIds.size();
            mIds.remove(parentId);

            String[] selectionArgs = new String[mIds.size() * 2];
            for (Integer id : mIds) {
                selectionArgs[count++] = String.valueOf(id);
            }
            for (Integer id : mIds) {
                selectionArgs[count++] = String.valueOf(id);
            }

            StringBuilder builder = new StringBuilder();
            final String inExpr = buidlInExpr(mIds);
            builder.append(DocumentContentProvider.Columns.ID);
            builder.append(inExpr);
            builder.append(" OR ");
            builder.append(DocumentContentProvider.Columns.PARENT_ID);
            builder.append(inExpr);

            String selection = builder.toString();

            ContentValues values = new ContentValues(2);
            values.put(DocumentContentProvider.Columns.PARENT_ID, parentId);
            values.put(DocumentContentProvider.Columns.CHILD_COUNT, 0);

            int childCount = getContentResolver().update(DocumentContentProvider.CONTENT_URI, values, selection, selectionArgs);
            values.clear();
            values.put(DocumentContentProvider.Columns.CHILD_COUNT, childCount);
            Uri parentDocumentUri = Uri.withAppendedPath(DocumentContentProvider.CONTENT_URI, String.valueOf(parentId));
            getContentResolver().update(parentDocumentUri, values, null, null);
            return documentCount;
        }

    }

}
