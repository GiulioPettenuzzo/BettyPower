package com.bettypower;

import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
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
import com.bettypower.entities.ParcelableHiddenResult;
import com.bettypower.entities.SingleBet;
import com.bettypower.listeners.PreLoadingLinearLayoutManager;
import com.bettypower.threads.LoadLogoThread;
import com.bettypower.threads.RefreshAllResultThread;
import com.bettypower.threads.RefreshResultThread;
import com.bettypower.util.HashMatchUtil;
import com.bettypower.util.touchHelper.ItemTouchHelperCallback;
import com.renard.ocr.R;
import com.renard.ocr.TextFairyApplication;
import com.renard.ocr.documents.viewing.DocumentContentProvider;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

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

    private Toolbar toolbar;
    private Bet bet;
    public ArrayList<PalimpsestMatch> allMatch = new ArrayList<>();
    public ArrayList<PalimpsestMatch> allMatchSelected = new ArrayList<>();
    private MatchesLoadListener allMatchLoadListener;
    private ItemTouchHelper itemTouchHelper;
    private Uri uri;

    private static final String ALL_MATCH_URL = "http://www.fishtagram.it/bettypower/result_data.php";
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

        if(savedInstanceState!=null && savedInstanceState.containsKey(ALL_MATCH_STATE)){
            bet = savedInstanceState.getParcelable(ALL_MATCH_STATE);
            if (bet != null) {
                allMatch = bet.getArrayMatch();
            }
            if(savedInstanceState.containsKey(ALL_MATCH_SELECTED_STATE)){
                allMatchSelected = savedInstanceState.getParcelableArrayList(ALL_MATCH_SELECTED_STATE);
                singleBetAdapter = new SingleBetAdapter(SingleBetActivity.this,bet,allMatchSelected);
            }
            else{
                singleBetAdapter = new SingleBetAdapter(SingleBetActivity.this,bet);
            }
            if (savedInstanceState.containsKey(EDIT_MODE)) {
                editMode = savedInstanceState.getBoolean(EDIT_MODE);
                singleBetAdapter.setEditMode(editMode);
            }
            if(savedInstanceState.containsKey(IMAGE_URI)) {
                uri = savedInstanceState.getParcelable(IMAGE_URI);
                singleBetAdapter.setUri(uri);
            }
            rVSingleBet.setAdapter(singleBetAdapter);
            singleBetAdapter.setExpandCollapseClickMode(new ExpandCollapseItemClickModeListener());
        } else {
            setMatchLoadListener(new MatchLoadListener());
            firstTimeMatchLoader();
         }
        rVSingleBet.setOnScrollListener(new HidingScrollListener());
        swipeRefreshLayout.setOnRefreshListener(new OnRefreshListener());
    }

    private boolean fromPictureActivity = false;
    @Override
    protected void onRestart() {
        super.onRestart();
        if(!fromPictureActivity) {
            swipeRefreshLayout.setRefreshing(true);
            makeVolleyForMatchInBet();
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
            Cursor c = getContentResolver().query(uri, new String[]{DocumentContentProvider.Columns.ID}, null, null, null);
            if (c != null) {
                c.moveToFirst();

                Integer id = c.getInt(c.getPosition());
                if (allMatch.size() == 0) {
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
                Log.i("share_menu_item","share_menu_item");
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
                        HashMatchUtil utils = new HashMatchUtil();
                        String stringArrayMatch = utils.fromArrayToString(allMatch);
                        bet.setSistema(false);
                        for (PalimpsestMatch currentMatch:allMatch
                                ) {
                            if(currentMatch.isFissa()) {
                                bet.setSistema(true);
                                break;
                            }
                        }
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
        rVSingleBet.setOnScrollListener(null);
        swipeRefreshLayout.setEnabled( false );
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
        rVSingleBet.setOnScrollListener(new HidingScrollListener());
        swipeRefreshLayout.setEnabled( true );
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
            HashMatchUtil util = new HashMatchUtil();
            allMatch = util.fromStringToArray(text);
            bet.setSistema(false);
            for (PalimpsestMatch currentMatch:allMatch
                    ) {
                if(currentMatch.isFissa()) {
                    bet.setSistema(true);
                    break;
                }
            }
            rVSingleBet.post(new Runnable() {
                @Override
                public void run() {
                    singleBetAdapter.setAllMatches(allMatch);
                    bet.setArrayMatch(allMatch);
                    disableEditStyle();
                }
            });
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
                            rVLayoutManager.findLastVisibleItemPosition()==allMatch.size()){
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
                initBetFromMemory();
            } else {
                initBetFromOCR();
            }
        }
    }

    //*********************************** INIT BET ADDED MANUALLY **********************************

    private void initManualBet(){
        allMatch = new ArrayList<>();
        bet = new SingleBet(allMatch);
        singleBetAdapter = new SingleBetAdapter(SingleBetActivity.this,bet);
        editMode = true;
        HashMatchUtil util = new HashMatchUtil();
        try {
            uri = saveDocumentToDB(util.fromArrayToString(bet.getArrayMatch()));
        }catch (Exception e){
            uri = null;
        }
        allMatchLoadListener.onMatchesLoaded();
        setAddMatchDialog();
        singleBetAdapter.setUri(uri);
        rVSingleBet.setAdapter(singleBetAdapter);
    }

    private Uri saveDocumentToDB(String plainText)
            throws RemoteException {
        ContentProviderClient client = null;

        try {
            ContentValues v = new ContentValues();
            if (plainText != null) {
                v.put(DocumentContentProvider.Columns.OCR_TEXT, plainText);
            }
            v.put(DocumentContentProvider.Columns.EVENT_NUMBER,String.valueOf(0));
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
                Cursor c = getContentResolver().query(uri, new String[]{DocumentContentProvider.Columns.OCR_TEXT, DocumentContentProvider.Columns.IMPORTO_PAGAMENTO, DocumentContentProvider.Columns.IMPORTO_SCOMMESSO, DocumentContentProvider.Columns.ERROR_NUMBER,DocumentContentProvider.Columns.ID}, null, null, null);
                String id = "";
                if (c != null) {
                    c.moveToFirst();
                    id = c.getString(c.getColumnIndex(DocumentContentProvider.Columns.ID));
                    bet = new SingleBet(allMatch);
                    if(!id.equals("91")) {
                        String text = c.getString(c.getPosition());
                        HashMatchUtil util = new HashMatchUtil();
                        allMatch = util.fromStringToArray(text);
                        bet = new SingleBet(allMatch);
                        TextFairyApplication application = (TextFairyApplication) getApplication();

                        if (application.isPalimpsestMatchLoaded) {
                            ArrayList<PalimpsestMatch> allPalimpsestMatch = application.getAllPalimpsestMatch();
                            checkForResult(allPalimpsestMatch);
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    swipeRefreshLayout.setRefreshing(true);
                                }
                            });
                            application.setAllMatchLoadListener(new TextFairyApplication.AllMatchLoadListener() {
                                @Override
                                public void onMatchLoaded(ArrayList<PalimpsestMatch> allPalimpsestMatch) {
                                    checkForResult(allPalimpsestMatch);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            swipeRefreshLayout.setRefreshing(false);
                                            singleBetAdapter.notifyDataSetChanged();
                                        }
                                    });
                                }
                            });
                        }

                        //NEXT LINE FOR TEST
                        bet.setArrayMatch(allMatch);
                        bet.setPuntata(c.getString(c.getColumnIndex(DocumentContentProvider.Columns.IMPORTO_SCOMMESSO)));
                        bet.setVincita(c.getString(c.getColumnIndex(DocumentContentProvider.Columns.IMPORTO_PAGAMENTO)));
                        bet.setErrors(c.getString(c.getColumnIndex(DocumentContentProvider.Columns.ERROR_NUMBER)));
                        Log.i("IIIIIDDDD", c.getString(c.getColumnIndex(DocumentContentProvider.Columns.ID)));
                    }
                    c.close();
                }
                if(id.equals("91")){
                    TextFairyApplication application = (TextFairyApplication) getApplication();

                    if(application.isPalimpsestMatchLoaded){
                        ArrayList<PalimpsestMatch> allPalimpsestMatch = application.getAllPalimpsestMatch();
                        ArrayList<PalimpsestMatch> thisDate = new ArrayList<>();
                        for (PalimpsestMatch currentPalimpses:allPalimpsestMatch
                             ) {
                            if(currentPalimpses.getDate().equals(giveDate())){
                                thisDate.add(currentPalimpses);
                            }
                        }
                        allMatch = thisDate;
                        bet.setArrayMatch(allMatch);
                    }else{
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                swipeRefreshLayout.setRefreshing(true);
                            }
                        });
                        application.setAllMatchLoadListener(new TextFairyApplication.AllMatchLoadListener() {
                            @Override
                            public void onMatchLoaded(ArrayList<PalimpsestMatch> allPalimpsestMatch) {
                                ArrayList<PalimpsestMatch> thisDate = new ArrayList<>();
                                for (PalimpsestMatch currentPalimpses:allPalimpsestMatch
                                        ) {
                                    if(currentPalimpses.getDate().equals(giveDate())){
                                        thisDate.add(currentPalimpses);
                                    }
                                }
                                allMatch = thisDate;
                                bet.setArrayMatch(allMatch);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(false);
                                        singleBetAdapter.notifyDataSetChanged();
                                    }
                                });
                            }
                        });
                    }
                }
                singleBetAdapter = new SingleBetAdapter(SingleBetActivity.this, bet);
                singleBetAdapter.setUri(uri);
                singleBetAdapter.setExpandCollapseClickMode(new ExpandCollapseItemClickModeListener());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        rVSingleBet.setAdapter(singleBetAdapter);
                    }
                });
            }
        });
        loadMatchesFromMemory.start();
        allMatchLoadListener.onMatchesLoaded();
    }

    private String giveDate() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdfh = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String getCurrentTime = sdfh.format(c.getTime());

        String midnight = "00:00";
        String oneOclock = "01:00";

        String date = "";
        if (getCurrentTime.compareTo(midnight) >= 0 && getCurrentTime.compareTo(oneOclock) <= 0)
        {
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM", Locale.getDefault());
            cal.add(Calendar.DATE, -1);
            date = sdf.format(cal.getTime());
        }
        else
        {
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM", Locale.getDefault());
            date = sdf.format(cal.getTime());
        }
        return date;
    }

    //*********************************** INIT NEW BET FROM OCR ************************************

    private void initBetFromOCR(){
        allMatch = bet.getArrayMatch();
        TextFairyApplication application = (TextFairyApplication) getApplication();
        checkForResult(application.getAllPalimpsestMatch());
        singleBetAdapter = new SingleBetAdapter(SingleBetActivity.this, bet);
        singleBetAdapter.setUri(uri);
        rVSingleBet.setAdapter(singleBetAdapter);
        allMatchLoadListener.onMatchesLoaded();
        singleBetAdapter.setExpandCollapseClickMode(new ExpandCollapseItemClickModeListener());
        ContentValues values = new ContentValues();
        values.put(DocumentContentProvider.Columns.EVENT_NUMBER, String.valueOf(allMatch.size()));
        getApplicationContext().getContentResolver().update(uri, values, null, null);
    }

    private ArrayList<HiddenResult> getHiddenResultForTest(){
        ArrayList<HiddenResult> result = new ArrayList<>();
        for(int i = 0;i<20;i++){
            HiddenResult hiddenResultHome = new ParcelableHiddenResult("Christiano Ronaldo",String.valueOf(i*10),ParcelableHiddenResult.ACTION_GOAL,ParcelableHiddenResult.HOME_TEAM);
            HiddenResult hiddenResultAway = new ParcelableHiddenResult("Lionel Messi",String.valueOf(i*10 + 5),ParcelableHiddenResult.ACTION_YELLOWCARD,ParcelableHiddenResult.AWAY_TEAM);
            hiddenResultHome.setResult("[1:1]");
            result.add(hiddenResultHome);
            result.add(hiddenResultAway);
        }
        return result;
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
            //swipeRefreshLayout.setEnabled(false);
            rVLayoutManager.setScrollEnabled(false);
        }

        @Override
        public void onClickEnds() {
            //swipeRefreshLayout.setEnabled(true);
            rVLayoutManager.setScrollEnabled(true);
        }
    }

    // ********************************** LOAD LOGO METHOD *****************************************


    public void loadAllLogos(){
        for (final PalimpsestMatch currentMatch:allMatch
             ) {
            LoadLogoThread homeThread = new LoadLogoThread(currentMatch.getHomeTeam().getName(), new LoadLogoThread.LoadLogoListener() {
                @Override
                public void onLoadLogoFinish(Bitmap bitmap) {
                    singleBetAdapter.notifyItemChanged(allMatch.lastIndexOf(currentMatch));
                }
            },SingleBetActivity.this);
            homeThread.start();
            LoadLogoThread awayThread = new LoadLogoThread(currentMatch.getAwayTeam().getName(), new LoadLogoThread.LoadLogoListener() {
                @Override
                public void onLoadLogoFinish(Bitmap bitmap) {
                    singleBetAdapter.notifyItemChanged(allMatch.lastIndexOf(currentMatch));
                }
            },SingleBetActivity.this);
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

    private void makeVolleyForMatchInBet(){
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, MATCH_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("response = ",response);
                        RefreshResultThread refreshResultThread = new RefreshResultThread(response, allMatch, new RefreshResultThread.LoadingListener() {
                            @Override
                            public void onFinishLoading(final ArrayList<PalimpsestMatch> allMatches) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        singleBetAdapter.setAllMatches(allMatches);
                                        bet.setArrayMatch(allMatches);
                                        swipeRefreshLayout.setRefreshing(false);
                                        singleBetAdapter.notifyDataSetChanged();
                                        HashMatchUtil utils = new HashMatchUtil();
                                        String stringArrayMatch = utils.fromArrayToString(allMatches);
                                        ContentValues values = new ContentValues();
                                        values.put(DocumentContentProvider.Columns.OCR_TEXT, stringArrayMatch);
                                        getApplicationContext().getContentResolver().update(uri,values,null,null);
                                        makeVolleyForAllPalimpsest(false);
                                    }
                                });
                            }
                        });
                        refreshResultThread.start();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                makeVolleyForAllPalimpsest(true);
            }
        });
        stringRequest.setShouldCache(false);
        queue.add(stringRequest);

    }

    private void makeVolleyForAllPalimpsest(final boolean goalServeError){
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, ALL_MATCH_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                RefreshAllResultThread refreshAllResultThread = new RefreshAllResultThread(response, allMatch, new RefreshAllResultThread.LoadingListener() {
                    @Override
                    public void onAllPalimpsestReady(final ArrayList<PalimpsestMatch> allMatches) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                TextFairyApplication application = (TextFairyApplication) getApplication();
                                application.setAllPalimpsestMatch(allMatches);
                            }
                        });
                    }

                    @Override
                    public void onBetPalimpsestReady(final ArrayList<PalimpsestMatch> allMatches) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(goalServeError){
                                    singleBetAdapter.setAllMatches(allMatches);
                                    bet.setArrayMatch(allMatches);
                                    swipeRefreshLayout.setRefreshing(false);
                                    singleBetAdapter.notifyDataSetChanged();
                                    HashMatchUtil utils = new HashMatchUtil();
                                    String stringArrayMatch = utils.fromArrayToString(allMatches);
                                    ContentValues values = new ContentValues();
                                    values.put(DocumentContentProvider.Columns.OCR_TEXT, stringArrayMatch);
                                    getApplicationContext().getContentResolver().update(uri,values,null,null);
                                }
                            }
                        });
                    }
                });
                refreshAllResultThread.start();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(goalServeError) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            swipeRefreshLayout.setRefreshing(false);
                            Toast toast = Toast.makeText(getApplicationContext(), R.string.no_connection, Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    });
                }
            }
        });
        stringRequest.setShouldCache(false);
        queue.add(stringRequest);
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
                addMatchDialog.dismiss();
                allMatch.add(match);
                bet.setArrayMatch(allMatch);
                singleBetAdapter.notifyItemInserted(allMatch.lastIndexOf(match));
                singleBetAdapter.setExpandCollapseClickMode(new ExpandCollapseItemClickModeListener());
                LoadLogoThread homeThread = new LoadLogoThread(match.getHomeTeam().getName(), new LoadLogoThread.LoadLogoListener() {
                    @Override
                    public void onLoadLogoFinish(Bitmap bitmap) {
                        singleBetAdapter.notifyItemChanged(allMatch.lastIndexOf(match));
                    }
                },SingleBetActivity.this);
                homeThread.start();
                LoadLogoThread awayThread = new LoadLogoThread(match.getAwayTeam().getName(), new LoadLogoThread.LoadLogoListener() {
                    @Override
                    public void onLoadLogoFinish(Bitmap bitmap) {
                        singleBetAdapter.notifyItemChanged(allMatch.lastIndexOf(match));
                    }
                },SingleBetActivity.this);
                awayThread.start();
            }
        });
    }

    private void checkForResult(ArrayList<PalimpsestMatch> allPalimpsestMatch){
        for (PalimpsestMatch currentMatch : allMatch
                ) {
            if (currentMatch.isFissa()) {
                bet.setSistema(true);
                //break;
            }
            for (PalimpsestMatch currentResultMatch:allPalimpsestMatch
                    ) {
                if(currentMatch.compareTo(currentResultMatch)){
                    if(!currentMatch.getHomeResult().equals("-") && !currentMatch.getAwayResult().equals("-")) {
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
        bet.setArrayMatch(allMatch);
        HashMatchUtil utils = new HashMatchUtil();
        String stringArrayMatch = utils.fromArrayToString(allMatch);
        ContentValues values = new ContentValues();
        values.put(DocumentContentProvider.Columns.OCR_TEXT, stringArrayMatch);
        getApplicationContext().getContentResolver().update(uri,values,null,null);
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
