package com.bettypower;

import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bettypower.adapters.SingleBetAdapter;
import com.bettypower.adapters.helpers.ExpandCollapseClickExecutor;
import com.bettypower.dialog.AddMatchDialog;
import com.bettypower.entities.Bet;
import com.bettypower.entities.HiddenResult;
import com.bettypower.entities.PalimpsestMatch;
import com.bettypower.entities.SingleBet;
import com.bettypower.entities.Team;
import com.bettypower.entities.deserialized.HiddenResultDeserialized;
import com.bettypower.entities.deserialized.PalimpsestMatchDeserialized;
import com.bettypower.entities.deserialized.TeamDeserialized;
import com.bettypower.listeners.PreLoadingLinearLayoutManager;
import com.bettypower.threads.LoadLogoThread;
import com.bettypower.threads.RefreshResultThread;
import com.bettypower.threads.ShareBetThread;
import com.bettypower.util.touchHelper.ItemTouchHelperCallback;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.renard.ocr.R;
import com.renard.ocr.TextFairyApplication;
import com.renard.ocr.documents.viewing.DocumentContentProvider;

import java.io.File;
import java.util.ArrayList;

/**
 * this is the activity that show the bet
 */
public class SingleBetActivity extends AppCompatActivity {

    private RecyclerView rVSingleBet;
    private PreLoadingLinearLayoutManager rVLayoutManager;
    private SingleBetAdapter singleBetAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FloatingActionButton refreshButton;
    private boolean editMode = false;

    private Bet bet;
    private Toolbar toolbar;
    public ArrayList<PalimpsestMatch> allMatchSelected = new ArrayList<>();
    private MatchesLoadListener allMatchLoadListener;
    private ItemTouchHelper itemTouchHelper;
    private Uri uri;

    private Gson gson = new Gson(); //to use tojson
    private Gson gsonGetter;//to use fromjson

    private static final String APPLICATION_LINK = "http://www.fishtagram.it/bettypower/app_link.php?";
    private static final String SHARING_LINK = "http://www.fishtagram.it/bettypower/bet_shering/sharing_executor.php";
    private static final String MATCH_URL = "http://www.goalserve.com/updaters/soccerupdate.aspx";
    private static final String ALL_MATCH_STATE = "save_all_match";
    private static final String ALL_MATCH_SELECTED_STATE = "all_match_selected_state";
    private static final String EDIT_MODE = "edit_mode";
    private static final String IMAGE_URI = "image_uri";
    private static final String MANUAL_CREATION = "manual_creation";

    /* *********************************************************************************************
     *********************************   ACTIVITY OVERRIDE METHOD   ********************************
     **********************************************************************************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_bet);
        setRecyclerAndManager();
        if (savedInstanceState != null && savedInstanceState.containsKey(ALL_MATCH_STATE)) {
            bet = savedInstanceState.getParcelable(ALL_MATCH_STATE);
            if (savedInstanceState.containsKey(ALL_MATCH_SELECTED_STATE)) {
                allMatchSelected = savedInstanceState.getParcelableArrayList(ALL_MATCH_SELECTED_STATE);
                singleBetAdapter = new SingleBetAdapter(SingleBetActivity.this, bet, allMatchSelected);
            } else {
                singleBetAdapter = new SingleBetAdapter(SingleBetActivity.this, bet);
            }
            if (savedInstanceState.containsKey(EDIT_MODE)) {
                editMode = savedInstanceState.getBoolean(EDIT_MODE);
                singleBetAdapter.setEditMode(editMode);
            }
            if (savedInstanceState.containsKey(IMAGE_URI)) {
                uri = savedInstanceState.getParcelable(IMAGE_URI);
                singleBetAdapter.setUri(uri);
            }
            rVSingleBet.setAdapter(singleBetAdapter);
            singleBetAdapter.setExpandCollapseClickMode(new ExpandCollapseItemClickModeListener());
            setRefreshLayoutEnabled(true);
            setHideScrollListener(true);
        } else {
            setMatchLoadListener(new MatchLoadListener());
            firstTimeMatchLoader();
        }
        swipeRefreshLayout.setOnRefreshListener(new OnRefreshListener());
        // ATTENTION: This was auto-generated to handle app links.
        handleIntent(getIntent());
    }

    private boolean fromPictureActivity = false;
    @Override
    protected void onRestart() {
        super.onRestart();
        if(!fromPictureActivity) {
            swipeRefreshLayout.setRefreshing(true);
            setRefreshLayoutEnabled(true);
            if(swipeRefreshLayout.isEnabled()) {
                makeVolleyForMatchInBet();
            }
        }else{
            fromPictureActivity = false;
        }
    }

    /**
     * Dispatch onPause() to fragments.
     */
    @Override
    protected void onPause() {
        super.onPause();
        try {
            String jsonBet = gson.toJson(bet);
            ContentValues values = new ContentValues();
            values.put(DocumentContentProvider.Columns.OCR_TEXT, jsonBet);
            getApplicationContext().getContentResolver().update(uri,values,null,null);

            Cursor c = getContentResolver().query(uri, new String[]{DocumentContentProvider.Columns.ID}, null, null, null);
            if (c != null) {
                c.moveToFirst();
                Integer id = c.getInt(c.getPosition());
                if (bet.getArrayMatch().size() == 0) {
                    getContentResolver().delete(uri, DocumentContentProvider.Columns.ID + "=" + id, null);
                }
                c.close();
            }
        }
        catch(Exception ignored){

        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(bet!=null) {
            outState.putParcelable(ALL_MATCH_STATE, bet);
        }
        if(singleBetAdapter!=null&&singleBetAdapter.getSelectedMatch()!=null) {
            outState.putParcelableArrayList(ALL_MATCH_SELECTED_STATE, singleBetAdapter.getSelectedMatch());
        }
        outState.putBoolean(EDIT_MODE,editMode);
        outState.putParcelable(IMAGE_URI,uri);
    }

    /**
     * Take care of popping the fragment back stack or finishing the activity
     * as appropriate.
     */
    @Override
    public void onBackPressed() {
        if(editMode){
            new Thread(new DelateModificationAndEditModeThread()).start();
        }else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }


    //*********************************** SET MENU *************************************************

    MenuItem switchMenuItem;
    MenuItem shareMenuItem;
    MenuItem editMenuItem;
    MenuItem confirmMenuItem;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.single_bet_action_menu, menu);
        Cursor c = getContentResolver().query(uri, new String[]{DocumentContentProvider.Columns.HOCR_TEXT}, null, null, null);
        String text = null;
        if (c != null) {
            c.moveToFirst();
            text = c.getString(c.getPosition());
            c.close();
        }
        switchMenuItem = menu.findItem(R.id.switch_menu_item);
        if(text==null){
            switchMenuItem.setVisible(false);
        }
        shareMenuItem = menu.findItem(R.id.share_menu_item);
        editMenuItem = menu.findItem(R.id.edit_menu_item);
        confirmMenuItem = menu.findItem(R.id.confirm);
        if(editMode)
            enableEditStyle();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.switch_menu_item:
                Intent intent = new Intent(SingleBetActivity.this,SingleBetImageActivity.class);
                intent.putExtra("image_uri",uri);
                intent.putExtra("edit_mode",editMode);
                fromPictureActivity = true;
                startActivity(intent);
                return true;
            case R.id.share_menu_item:

                if(isConnected()){
                    new ShareBetThread(SingleBetActivity.this,bet).execute(SHARING_LINK);
                }
                else{
                    Toast toast = Toast.makeText(getApplicationContext(), R.string.no_connection, Toast.LENGTH_SHORT);
                    toast.show();
                }

                return true;
            case R.id.edit_menu_item:
                if(!editMode) {
                    enableEditStyle();
                }else{
                    setAddMatchDialog();
                }
                return true;
            case R.id.confirm:
                Thread saveModification = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        singleBetAdapter.setEditModeConfirm();
                        bet.setSistema(false);
                        for (PalimpsestMatch currentMatch:bet.getArrayMatch()
                                ) {
                            if(currentMatch.isFissa()) {
                                bet.setSistema(true);
                                break;
                            }
                        }
                        String stringArrayMatch = gson.toJson(bet);
                        ContentValues values = new ContentValues();
                        values.put(DocumentContentProvider.Columns.OCR_TEXT, stringArrayMatch);
                        getApplicationContext().getContentResolver().update(uri,values,null,null);
                    }
                });
                saveModification.start();
                disableEditStyle();
                return true;
            case android.R.id.home:
                new Thread(new DelateModificationAndEditModeThread()).start();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }




   /* private void shareIt(String fileName) {
        Uri uri = Uri.fromFile(imagePath);
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("image/*");
        String shareBody = "http://www.fishtagram.it/bettypower/app_link.php?";
        //String text = gson.toJson(bet);
        shareBody = shareBody + fileName;
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "My Tweecher score");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);

        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }*/

    //*********************************** EDIT MOOD *************************************************

    private void enableEditStyle(){
        toolbar.setNavigationIcon(R.drawable.cross);
        if(getSupportActionBar()!=null)
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        //editMenuItem.setVisible(false);
        editMenuItem.setIcon(R.drawable.add_item);
        shareMenuItem.setVisible(false);
        confirmMenuItem.setVisible(true);
        editMode = true;
        singleBetAdapter.setEditMode(true);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.action_bar_edit_mode)));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.action_bar_edit_mode));
        }
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setHideScrollListener(false);
        setRefreshLayoutEnabled(false);
        ItemTouchHelperCallback myItemTouchHelper = new ItemTouchHelperCallback(singleBetAdapter);
        itemTouchHelper = new ItemTouchHelper(myItemTouchHelper);
        itemTouchHelper.attachToRecyclerView(rVSingleBet);
    }

    private void disableEditStyle(){
        toolbar.setNavigationIcon(null);
        if(getSupportActionBar()!=null)
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        // editMenuItem.setVisible(true);
        editMenuItem.setIcon(R.drawable.ic_mode_edit_white_48px);
        shareMenuItem.setVisible(true);
        confirmMenuItem.setVisible(false);
        editMode = false;
        singleBetAdapter.setEditMode(false);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.toolbar_lighter)));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.action_bar_lighter));
        }
        InputMethodManager imm = (InputMethodManager) SingleBetActivity.this.getSystemService(INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = SingleBetActivity.this.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(SingleBetActivity.this);
        }
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        setHideScrollListener(true);
        swipeRefreshLayout.setRefreshing(true);
        setRefreshLayoutEnabled(true);
        makeVolleyForMatchInBet();
        itemTouchHelper.attachToRecyclerView(null);
    }

    private class DelateModificationAndEditModeThread implements Runnable{
        @Override
        public void run() {
            Cursor c = getContentResolver().query(uri, new String[]{DocumentContentProvider.Columns.OCR_TEXT}, null, null, null);
            String text= "";
            if (c != null) {
                c.moveToFirst();
                text = c.getString(c.getPosition());
                c.close();
            }
            bet = gsonGetter.fromJson(text,SingleBet.class);
            bet.setSistema(false);
            for (PalimpsestMatch currentMatch:bet.getArrayMatch()
                    ) {
                if(currentMatch.isFissa()) {
                    bet.setSistema(true);
                    break;
                }
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    singleBetAdapter.setBet(bet);
                    disableEditStyle();
                }
            });
           /* rVSingleBet.post(new Runnable() {
                @Override
                public void run() {
                   // singleBetAdapter.setAllMatches(allMatch);
                   // bet.setArrayMatch(allMatch);
                    singleBetAdapter.notifyDataSetChanged();
                    disableEditStyle();
                }
            });*/
        }
    }

    //******************************** AUSILIAR METHOD FOR ON CREATE *******************************

    /**
     * to set well the recycler view, the manager, and the swipeRefreshLayout
     */
    private void setRecyclerAndManager(){
        rVSingleBet = (RecyclerView) findViewById(R.id.rv_single_bet);
        rVSingleBet.setHasFixedSize(true);
        rVSingleBet.setItemViewCacheSize(10);
        rVSingleBet.setDrawingCacheEnabled(true);
        rVSingleBet.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        rVLayoutManager = new PreLoadingLinearLayoutManager(this);
        rVLayoutManager.setPages(0);
        rVLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.bringToFront();
        refreshButton = (FloatingActionButton) findViewById(R.id.refresh_button);
        refreshButton.bringToFront();
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firstTimeMatchLoader();
                refreshButton.setVisibility(View.GONE);
            }
        });
        refreshButton.setVisibility(View.GONE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT <= Build.VERSION_CODES.N) {
            //this will load all the item only if the window's position is in the beginning or in the end
            rVSingleBet.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    rVLayoutManager.setPages(0);

                    if(rVLayoutManager.findFirstVisibleItemPosition()==0||
                            rVLayoutManager.findLastVisibleItemPosition()==bet.getArrayMatch().size()){
                        rVLayoutManager.setPages(3);
                    }

                }
            });
        }
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        swipeRefreshLayout.setProgressViewOffset(false, 250, 350);
        swipeRefreshLayout.setColorSchemeResources(
                R.color.toolbar_background_dark,
                R.color.toolbar_background,
                R.color.toolbar_background_light);
        rVSingleBet.setLayoutManager(rVLayoutManager);

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(HiddenResult.class,new HiddenResultDeserialized());
        gsonBuilder.registerTypeAdapter(Team.class,new TeamDeserialized());
        gsonBuilder.registerTypeAdapter(PalimpsestMatch.class,new PalimpsestMatchDeserialized());
        gsonGetter = gsonBuilder.create();
    }

    /* *********************************************************************************************
     *********************************   INIT BET LOADER   ****************************************
     **********************************************************************************************/

    private void firstTimeMatchLoader() {
        Intent intent = getIntent();
        String manualBet = intent.getStringExtra(MANUAL_CREATION);
        if(manualBet!=null){
           initManualBet();
        }else {
            bet = intent.getParcelableExtra("all_match");
            uri = intent.getData();
            if (bet == null) {
                if(!Intent.ACTION_VIEW.equals(uri)) {
                    initBetFromMemory();
                }
            } else {
                initBetFromOCR();
            }
        }
    }

    //*********************************** INIT BET ADDED MANUALLY **********************************

    private void initManualBet(){
        ArrayList<PalimpsestMatch> allMatch = new ArrayList<>();
        bet = new SingleBet(allMatch);
        singleBetAdapter = new SingleBetAdapter(SingleBetActivity.this,bet);
        editMode = true;
        try {
            String text = gson.toJson(bet);
            uri = saveDocumentToDB(text);
        }catch (Exception e){
            uri = null;
        }
        //allMatchLoadListener.onMatchesLoaded();
        setAddMatchDialog();
        singleBetAdapter.setUri(uri);
        rVSingleBet.setAdapter(singleBetAdapter);
        setHideScrollListener(true);
        setRefreshLayoutEnabled(true);
    }

    private Uri saveDocumentToDB(String plainText)
            throws RemoteException {
        ContentProviderClient client = null;

        try {
            ContentValues v = new ContentValues();
            if (plainText != null) {
                v.put(DocumentContentProvider.Columns.OCR_TEXT, plainText);
            }
            client = getContentResolver().acquireContentProviderClient(DocumentContentProvider.CONTENT_URI);
            if (client != null) {
                return client.insert(DocumentContentProvider.CONTENT_URI, v);
            }
            else{
                return null;
            }
        } finally {
            if (client != null) {
                client.release();
            }
        }
    }

    //*********************************** INIT TAKEN FROM MEMORY ***********************************

    private void initBetFromMemory(){
        Thread loadMatchesFromMemory = new Thread(new Runnable() {
            @Override
            public void run() {
                Cursor c = getContentResolver().query(uri, new String[]{DocumentContentProvider.Columns.OCR_TEXT}, null, null, null);
                if (c != null) {
                    c.moveToFirst();
                    String text = c.getString(c.getPosition());
                    bet = gsonGetter.fromJson(text, SingleBet.class);
                    TextFairyApplication application = (TextFairyApplication) getApplication();


                    if (application.getAllPalimpsestMatch()!=null) {
                        ArrayList<PalimpsestMatch> allPalimpsestMatch = application.getAllPalimpsestMatch();
                        checkForResult(allPalimpsestMatch);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                makeVolleyForMatchInBet();
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                makeVolleyForMatchInBet();
                            }
                        });
                        application.setAllMatchLoadListener(new TextFairyApplication.AllMatchLoadListener() {
                            @Override
                            public void onMatchLoaded(ArrayList<PalimpsestMatch> allPalimpsestMatch) {
                                checkForResult(allPalimpsestMatch);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        singleBetAdapter.notifyDataSetChanged();
                                    }
                                });
                            }
                        });
                    }
                    c.close();
                }
                singleBetAdapter = new SingleBetAdapter(SingleBetActivity.this, bet);
                singleBetAdapter.setUri(uri);
                singleBetAdapter.setExpandCollapseClickMode(new ExpandCollapseItemClickModeListener());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        rVSingleBet.setAdapter(singleBetAdapter);
                        allMatchLoadListener.onMatchesLoaded();
                        swipeRefreshLayout.setRefreshing(true);
                        setRefreshLayoutEnabled(true);
                        setHideScrollListener(true);
                    }
                });
            }
        });
        loadMatchesFromMemory.start();
    }

    //*********************************** INIT NEW BET FROM OCR ************************************

    private void initBetFromOCR(){
        TextFairyApplication application = (TextFairyApplication) getApplication();
        checkForResult(application.getAllPalimpsestMatch());
        singleBetAdapter = new SingleBetAdapter(SingleBetActivity.this, bet);
        singleBetAdapter.setUri(uri);
        rVSingleBet.setAdapter(singleBetAdapter);
        allMatchLoadListener.onMatchesLoaded();
        singleBetAdapter.setExpandCollapseClickMode(new ExpandCollapseItemClickModeListener());
        swipeRefreshLayout.setRefreshing(true);
        setRefreshLayoutEnabled(true);
        makeVolleyForMatchInBet();
        setHideScrollListener(true);
    }

    /* *********************************************************************************************
     *********************************   AUSILIAR ACTIONS  ****************************************
     **********************************************************************************************/

    //********************************** EXPAND - COLLAPSE VIEW *************************************

    /**
     * this is what doing during the animation click item:
     */
    private class ExpandCollapseItemClickModeListener implements ExpandCollapseClickExecutor.ClickStat{

        @Override
        public void onClickStart() {
            rVLayoutManager.setScrollEnabled(false);
        }

        @Override
        public void onClickEnds() {
            rVLayoutManager.setScrollEnabled(true);
        }
    }

    // ********************************** LOAD LOGO METHOD *****************************************


    public void loadAllLogos(){
        for (final PalimpsestMatch currentMatch:bet.getArrayMatch()
             ) {
            startImageThreadIfImageNotFound(currentMatch);
        }
    }

    private void startImageThreadIfImageNotFound(final PalimpsestMatch currentMatch){
        File fHome=new File(getDir("logo_images", Context.MODE_PRIVATE).getPath()+"/"+currentMatch.getHomeTeam().getName() + ".png");
        if(!fHome.exists()) {
            Log.i("thread image", "start");
            LoadLogoThread homeThread = new LoadLogoThread(currentMatch.getHomeTeam().getName(), new LoadLogoThread.LoadLogoListener() {
                @Override
                public void onLoadLogoFinish(Bitmap bitmap) {
                    singleBetAdapter.notifyItemChanged(bet.getArrayMatch().lastIndexOf(currentMatch));
                }
            }, SingleBetActivity.this);
            homeThread.start();
        }
        File fAway=new File(getDir("logo_images", Context.MODE_PRIVATE).getPath()+"/"+currentMatch.getAwayTeam().getName() + ".png");
        if(!fAway.exists()) {
            Log.i("thread image", "start");
            LoadLogoThread awayThread = new LoadLogoThread(currentMatch.getAwayTeam().getName(), new LoadLogoThread.LoadLogoListener() {
                @Override
                public void onLoadLogoFinish(Bitmap bitmap) {
                    singleBetAdapter.notifyItemChanged(bet.getArrayMatch().lastIndexOf(currentMatch));
                }
            }, SingleBetActivity.this);
            awayThread.start();
        }
    }




    // ********************************** SWIPE - REFRESH LISTENER **********************************

    private class OnRefreshListener implements SwipeRefreshLayout.OnRefreshListener{
        /**
         * Called when a swipe gesture triggers a refresh.
         */
        @Override
        public void onRefresh() {
            makeVolleyForMatchInBet();
        }
    }

    private void setRefreshLayoutEnabled(boolean value){
        if(value && !bet.areMatchesFinished()){
            swipeRefreshLayout.setEnabled(true);
        }else{
            swipeRefreshLayout.setEnabled(false);
        }
    }


    private void makeVolleyForMatchInBet(){
        if(!bet.areMatchesFinished()) {
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            StringRequest stringRequest = new StringRequest(Request.Method.GET, MATCH_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.i("response = ", response);
                            RefreshResultThread refreshResultThread = new RefreshResultThread(response, bet.getArrayMatch(), new RefreshResultThread.LoadingListener() {
                                @Override
                                public void onFinishLoading(final ArrayList<PalimpsestMatch> allMatches) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            bet.setArrayMatch(allMatches);
                                            swipeRefreshLayout.setRefreshing(false);
                                            singleBetAdapter.setBet(bet);

                                        }
                                    });
                                }
                            });
                            refreshResultThread.start();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    swipeRefreshLayout.setRefreshing(false);
                    Toast toast = Toast.makeText(getApplicationContext(), R.string.no_connection, Toast.LENGTH_SHORT);
                    toast.show();
                }
            });
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 48,
                    2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            stringRequest.setShouldCache(false);
            queue.add(stringRequest);
        }

    }

    //********************************** INSERT NEW MATCH ***************************************

    private void setAddMatchDialog(){
        final AddMatchDialog addMatchDialog = new AddMatchDialog(SingleBetActivity.this,SingleBetActivity.this);
        addMatchDialog.show();
        addMatchDialog.setCanceledOnTouchOutside(false);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = addMatchDialog.getWindow();
        if (window != null) {
            lp.copyFrom(window.getAttributes());
        }
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        if (window != null) {
            window.setAttributes(lp);
        }
        addMatchDialog.setOnAddNewItem(new AddMatchDialog.FinishEditDialog() {
            @Override
            public void onAddNewItem(final PalimpsestMatch match) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        addMatchDialog.dismiss();
                        bet.getArrayMatch().add(match);
                        singleBetAdapter.notifyItemInserted(bet.getArrayMatch().lastIndexOf(match));
                        singleBetAdapter.notifyItemChanged(bet.getArrayMatch().size());
                        singleBetAdapter.setExpandCollapseClickMode(new ExpandCollapseItemClickModeListener());
                        //makeVolleyForMatchInBet();
                        setRefreshLayoutEnabled(true);
                        startImageThreadIfImageNotFound(match);
                    }
                });
            }
        });
    }


    private void checkForResult(ArrayList<PalimpsestMatch> allPalimpsestMatch){
        for (PalimpsestMatch currentMatch : bet.getArrayMatch()
                ) {
            if (currentMatch.isFissa()) {
                bet.setSistema(true);
                //break;
            }
            for (PalimpsestMatch currentResultMatch:allPalimpsestMatch
                    ) {
                if(currentMatch.compareTo(currentResultMatch)){
                    //if(!currentResultMatch.getAwayResult().equals("-")&&!currentResultMatch.getHomeResult().equals("-")&& currentMatch.getHomeResult().equals("-") && currentMatch.getAwayResult().equals("-") && (currentMatch.getResultTime()==null || (!currentMatch.getResultTime().matches(".*\\d+.*") && !currentMatch.getResultTime().equalsIgnoreCase(getApplicationContext().getResources().getString(R.string.intervallo))))) {
                    if(!currentResultMatch.getAwayResult().equals("-")&&!currentResultMatch.getHomeResult().equals("-")){
                        currentMatch.setHomeResult(currentResultMatch.getHomeResult());
                        currentMatch.setAwayResult(currentResultMatch.getAwayResult());
                        currentMatch.setResultTime(currentResultMatch.getResultTime());
                        currentMatch.setAllHiddenResult(currentResultMatch.getAllHiddenResult());
                        Log.i("match", "match trovato!!");
                        break;
                    }
                }
            }

        }
    }

    //********************************** SHOW - HIDE TOOLBAR ***************************************
    /**
     * used to hide the toolbar when the user is scrolling down and show the toolbar when the user is scrolling up
     */
    private class HidingScrollListener extends com.bettypower.listeners.HidingScrollListener{

        @Override
        public void onHide() {
            toolbar.animate().translationY(-toolbar.getHeight()).setInterpolator(new AccelerateInterpolator(2));
        }

        @Override
        public void onShow() {
            toolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
        }
    }

    private void setHideScrollListener(boolean value){
        if(value && bet.getArrayMatch().size()>3){
            rVSingleBet.setOnScrollListener(new HidingScrollListener());
        }else{
            rVSingleBet.setOnScrollListener(null);
        }
    }
    /* *********************************************************************************************
     ***************************************   SHARING BET   **************************************
     **********************************************************************************************/

    private void handleIntent(Intent intent) {
        String appLinkAction = intent.getAction();
        Uri appLinkData = intent.getData();
        if (Intent.ACTION_VIEW.equals(appLinkAction) && appLinkData != null){
            //String recipeId = appLinkData.getLastPathSegment();
            int index = appLinkData.toString().lastIndexOf("?");
            String fileName = appLinkData.toString().substring(index+1);
            String completeUrl = "http://www.fishtagram.it/bettypower/bet_shering/bet_shared/"+fileName;
            bet = new SingleBet(new ArrayList<PalimpsestMatch>());

            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            StringRequest stringRequest = new StringRequest(Request.Method.GET, completeUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(final String response) {
                            Log.i("response=",response);
                            GsonBuilder gsonBuilder = new GsonBuilder();
                            gsonBuilder.registerTypeAdapter(HiddenResult.class,new HiddenResultDeserialized());
                            gsonBuilder.registerTypeAdapter(Team.class,new TeamDeserialized());
                            gsonBuilder.registerTypeAdapter(PalimpsestMatch.class,new PalimpsestMatchDeserialized());
                            gsonGetter = gsonBuilder.create();
                            bet = gsonGetter.fromJson(response,SingleBet.class);
                            singleBetAdapter.setBet(bet);
                            swipeRefreshLayout.setRefreshing(false);
                            Thread saveToDb = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        uri = saveDocumentToDB(response);
                                    } catch (RemoteException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            saveToDb.start();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    swipeRefreshLayout.setRefreshing(false);
                    Toast toast = Toast.makeText(getApplicationContext(), R.string.no_connection, Toast.LENGTH_SHORT);
                    toast.show();
                }
            });
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 48,
                    2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            stringRequest.setShouldCache(false);
            queue.add(stringRequest);
        }
    }

    public boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connMgr != null) {
            networkInfo = connMgr.getActiveNetworkInfo();
        }
        return networkInfo != null && networkInfo.isConnected();
    }

    /* *********************************************************************************************
     *********************************   LISTENER AND CLASS   **************************************
     **********************************************************************************************/

    /**
     * In order to know when a logo match is finish to upDate.
     */
    public interface MatchesLoadListener{
        void onMatchesLoaded();
    }

    public void setMatchLoadListener(MatchesLoadListener matchLoadListener){
        this.allMatchLoadListener = matchLoadListener;
    }

    private class MatchLoadListener implements  MatchesLoadListener{
        @Override
        public void onMatchesLoaded() {
            loadAllLogos();
        }
    }

}