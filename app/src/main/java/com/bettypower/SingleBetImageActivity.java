package com.bettypower;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;

import com.renard.ocr.R;
import com.renard.ocr.documents.viewing.DocumentContentProvider;

public class SingleBetImageActivity extends AppCompatActivity {

    private MenuItem shareImage;
    private boolean editMode;
    Bitmap myBitmap;
    Intent intent;
    ImageView imageView;

    private static final String EDIT_MODE = "edit_mode";
    private static final String IMAGE_URI = "image_uri";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_bet_image);
        imageView = (ImageView) findViewById(R.id.single_bet_image);
        intent = getIntent();
        if(savedInstanceState!=null && savedInstanceState.containsKey(EDIT_MODE)){
            editMode = savedInstanceState.getBoolean(EDIT_MODE);
        }
        else {
            editMode = intent.getBooleanExtra(EDIT_MODE, false);
        }
        Thread loadBitmap = new Thread(new Runnable() {
            @Override
            public void run() {
                Uri uri = intent.getParcelableExtra(IMAGE_URI);
                Cursor c = getContentResolver().query(uri, new String[]{DocumentContentProvider.Columns.PHOTO_PATH}, null, null, null);
                if (c != null) {
                    c.moveToFirst();
                    myBitmap = BitmapFactory.decodeFile(c.getString(c.getPosition()));
                    c.close();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            imageView.setImageBitmap(myBitmap);
                        }
                    });
                }
            }
        });
        loadBitmap.start();


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_48px);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.single_bet_image_action_menu, menu);
        shareImage = menu.findItem(R.id.share_image);
        if(editMode){
            enableEditModeStyle();
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                //finish();
                //return true;
                this.dispatchKeyEvent(new KeyEvent (KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
                this.dispatchKeyEvent(new KeyEvent (KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK));
                return true;

            case R.id.share_image:
                //TODO
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(EDIT_MODE, editMode);
        super.onSaveInstanceState(outState);
    }

    private void enableEditModeStyle(){
        shareImage.setVisible(false);
        if(getSupportActionBar()!=null)
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.action_bar_edit_mode)));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.action_bar_edit_mode));
        }
    }
}