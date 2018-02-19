package com.bettypower;

import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Path;
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
import android.widget.ArrayAdapter;
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
import com.bettypower.dialog.SetBetDialog;
import com.bettypower.entities.Bet;
import com.bettypower.entities.HiddenResult;
import com.bettypower.entities.Match;
import com.bettypower.entities.PalimpsestMatch;
import com.bettypower.entities.ParcelableHiddenResult;
import com.bettypower.entities.ParcelableMatch;
import com.bettypower.entities.ParcelableTeam;
import com.bettypower.entities.SingleBet;
import com.bettypower.listeners.PreLoadingLinearLayoutManager;
import com.bettypower.threads.LoadImageThread;
import com.bettypower.threads.RefreshResultThread;
import com.bettypower.util.HashMatchUtil;
import com.bettypower.util.Helper;
import com.bettypower.util.touchHelper.ItemTouchHelperCallback;
import com.renard.ocr.BuildConfig;
import com.renard.ocr.R;
import com.renard.ocr.TextFairyApplication;
import com.renard.ocr.documents.creation.NewDocumentActivity;
import com.renard.ocr.documents.creation.visualisation.OCRActivity;
import com.renard.ocr.documents.viewing.DocumentContentProvider;
import com.renard.ocr.documents.viewing.single.DocumentActivity;
import com.renard.ocr.util.Util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

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
    public ArrayList<String> allLogosURL = new ArrayList<>();
    private boolean isClicking = false;
    private boolean isAllLogoUploaded = true;
    private MatchesLoadListener allMatchLoadListener;
    private ItemTouchHelper itemTouchHelper;
    private Uri uri;

    private static final String MATCH_URL = "http://www.goalserve.com/updaters/soccerupdate.aspx";
    private static final String IMAGE_URL = "http://www.fishtagram.it/loghi.html";
    private static final String ALL_MATCH_STATE = "save_all_match";
    private static final String ALL_MATCH_SELECTED_STATE = "all_match_selected_state";
    private static final String EDIT_MODE = "edit_mode";
    private static final String IMAGE_URI = "image_uri";
    private static final String MANUAL_CREATION = "manual_creation";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_bet);
        setRecyclerAndManager();

        //AllMatchesByPalimpsestUnpacker allMatchesByPalimpsestUnpacker = new AllMatchesByPalimpsestUnpacker(getApplicationContext());
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
            //singleBetAdapter.setHasStableIds(true);
            rVSingleBet.setAdapter(singleBetAdapter);
            singleBetAdapter.setExpandCollapseClickMode(new ExpandCollapseItemClickModeListener());
            //singleBetAdapter.setDialogListener(new DialogClickListener());
        } else {
            setMatchLoadListener(new MatchLoadListener());
            firstTimeMatchLoader();
         }
        //code to hide and show the toolbar
        rVSingleBet.setOnScrollListener(new HidingScrollListener());
        //code to refresh when scrolling up
        swipeRefreshLayout.setOnRefreshListener(new OnRefreshListener());
    }

    /**
     * Dispatch onPause() to fragments.
     */
    @Override
    protected void onPause() {
        super.onPause();
        try {
            Cursor c = getContentResolver().query(uri, new String[]{DocumentContentProvider.Columns.ID}, null, null, null);
            c.moveToFirst();
            Integer id = c.getInt(c.getPosition());
            if (allMatch.size() == 0) {
                getContentResolver().delete(uri, DocumentContentProvider.Columns.ID + "=" + id, null);
            }
            c.close();
        }
        catch(Exception e){

        }
        //new NewDocumentActivity.DeleteDocumentTask(ids,false);
        //new NewDocumentActivity.DeleteDocumentTask(ids, false).execute();
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

    MenuItem switchMenuItem;
    MenuItem shareMenuItem;
    MenuItem editMenuItem;
    MenuItem confirmMenuItem;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.single_bet_action_menu, menu);
        Cursor c = getContentResolver().query(uri, new String[]{DocumentContentProvider.Columns.HOCR_TEXT}, null, null, null);
        c.moveToFirst();
        String text = c.getString(c.getPosition());
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

    private void enableEditStyle(){
        toolbar.setNavigationIcon(R.drawable.cross);
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
        getSupportActionBar().setDisplayShowTitleEnabled(true);
       // editMenuItem.setVisible(true);
        editMenuItem.setIcon(R.drawable.ic_mode_edit_white_48px);
        shareMenuItem.setVisible(true);
        confirmMenuItem.setVisible(false);
        editMode = false;
        singleBetAdapter.setEditMode(false);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.primary)));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.primary));
        }
        InputMethodManager imm = (InputMethodManager) SingleBetActivity.this.getSystemService(SingleBetActivity.this.INPUT_METHOD_SERVICE);
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

    //TODO


    private void firstTimeMatchLoader() {
        Intent intent = getIntent();
        //final String result = intent.getStringExtra("testo_completo");
       // final TextFairyApplication application = (TextFairyApplication) getApplicationContext();

        //allMatch = intent.getParcelableArrayListExtra("all_match");
       // bet = intent.getExtras().getParcelable("all_match");
        String manualBet = intent.getStringExtra(MANUAL_CREATION);
        if(manualBet!=null){
            allMatch = new ArrayList<>();
            bet = new SingleBet(allMatch);
            singleBetAdapter = new SingleBetAdapter(SingleBetActivity.this,bet);
            editMode = true;
            HashMatchUtil util = new HashMatchUtil();
            //TODO QUI
            try {
                uri = saveDocumentToDB(util.fromArrayToString(bet.getArrayMatch()));
            }catch (Exception e){

            }
            setAddMatchDialog();
           /* File imageFile = new File(("android.resource://"+ BuildConfig.APPLICATION_ID+"/" + R.drawable.ic_betty_power_green));
            if (imageFile != null) {
                Util.createThumbnail(SingleBetActivity.this, imageFile, Integer.valueOf(uri.getLastPathSegment()));
            }*/
            singleBetAdapter.setUri(uri);
            rVSingleBet.setAdapter(singleBetAdapter);
        }else {
            bet = intent.getParcelableExtra("all_match");
            uri = intent.getData();
            if (bet == null) {

                Thread loadMatchesFromMemory = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Cursor c = getContentResolver().query(uri, new String[]{DocumentContentProvider.Columns.OCR_TEXT, DocumentContentProvider.Columns.IMPORTO_PAGAMENTO, DocumentContentProvider.Columns.IMPORTO_SCOMMESSO, DocumentContentProvider.Columns.ERROR_NUMBER}, null, null, null);
                        c.moveToFirst();
                        String text = c.getString(c.getPosition());
                        HashMatchUtil util = new HashMatchUtil();
                        allMatch = util.fromStringToArray(text);
                        bet = new SingleBet(allMatch);
                        for (PalimpsestMatch currentMatch : allMatch
                                ) {
                            //NEXT LINE FOR TEST
                            if (currentMatch.isFissa()) {
                                bet.setSistema(true);
                                break;
                            }
                        }
                        //NEXT LINE FOR TEST
                        bet.setArrayMatch(allMatch);
                        bet.setPuntata(c.getString(c.getColumnIndex(DocumentContentProvider.Columns.IMPORTO_SCOMMESSO)));
                        bet.setVincita(c.getString(c.getColumnIndex(DocumentContentProvider.Columns.IMPORTO_PAGAMENTO)));
                        bet.setErrors(c.getString(c.getColumnIndex(DocumentContentProvider.Columns.ERROR_NUMBER)));
                        c.close();
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

          /*  bet = betForTest();
            allMatch = bet.getArrayMatch();
            for (Match currentMatch:allMatch) {
                currentMatch.setAllHiddenResult(getHiddenResultForTest());
            }
            singleBetAdapter = new SingleBetAdapter(SingleBetActivity.this, bet);
            singleBetAdapter.setHasStableIds(true);
            singleBetAdapter.setUri(uri);
            rVSingleBet.setAdapter(singleBetAdapter);
            allMatchLoadListener.onMatchesLoaded();
            singleBetAdapter.setOutsideClickListener(new ActivityClickListener());
            singleBetAdapter.setDialogListener(new DialogClickListener());*/
                //    ContentValues values = new ContentValues();
                //       values.put(DocumentContentProvider.Columns.EVENT_NUMBER,String.valueOf(allMatch.size()));
//            getApplicationContext().getContentResolver().update(uri,values,null,null);
            } else {
                allMatch = bet.getArrayMatch();
                for (PalimpsestMatch currentMatch : allMatch) {
                    currentMatch.setAllHiddenResult(getHiddenResultForTest());
                }
                singleBetAdapter = new SingleBetAdapter(SingleBetActivity.this, bet);
                singleBetAdapter.setUri(uri);
                rVSingleBet.setAdapter(singleBetAdapter);
                allMatchLoadListener.onMatchesLoaded();
                singleBetAdapter.setExpandCollapseClickMode(new ExpandCollapseItemClickModeListener());
                ContentValues values = new ContentValues();
                values.put(DocumentContentProvider.Columns.EVENT_NUMBER, String.valueOf(allMatch.size()));
                getApplicationContext().getContentResolver().update(uri, values, null, null);

            }
        }
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

    private Bet betForTest(){
        ArrayList<Match> allMatch = new ArrayList<>();
        for(int i = 0; i<20;i++){
            Match match = new ParcelableMatch(new ParcelableTeam("milan " + i),new ParcelableTeam(("milan " + i)));
            match.setHomeResult("1");
            match.setAwayResult("1");
            match.setTime("deso dio/can");
            allMatch.add(match);
        }

       // Bet bet = new SingleBet(allMatch);
        return bet;
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

    //TODO CLICK LISTENER CALLBACK
    /**
     * this is what doing during the animation click item:
     *
     */
    private class ExpandCollapseItemClickModeListener implements ExpandCollapseClickExecutor.ClickStat{

        @Override
        public void onClickStart() {
            swipeRefreshLayout.setEnabled(false);
            rVLayoutManager.setScrollEnabled(false);
            isClicking = true;
        }

        @Override
        public void onClickEnds() {
            swipeRefreshLayout.setEnabled(true);
            rVLayoutManager.setScrollEnabled(true);
            isClicking = false;
        }
    }

    public void loadAllLogosExecutor(){
        RequestQueue imageQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest imageRequest = new StringRequest(Request.Method.GET, IMAGE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        LoadImageThread loadImageThread = new
                                LoadImageThread(response,allMatch,allLogosURL,getApplicationContext());
                        loadImageThread.setOnLoadLogoListener(new LoadImageThread.LoadLogoListener() {
                            @Override
                            public void onLoadLogoFinish(final PalimpsestMatch match) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(!isClicking){
                                            if(allMatch.lastIndexOf(match)!=allMatch.size()-1){
                                                singleBetAdapter.notifyItemChanged(allMatch.lastIndexOf(match));
                                            }
                                            Log.i("item", String.valueOf(match.getHomeTeam().getName()));
                                        }
                                        if(allMatch.lastIndexOf(match)==allMatch.size()-1){
                                            singleBetAdapter.notifyDataSetChanged();
                                        }
                                    }
                                });

                            }

                            @Override
                            public void onAllLogosUploaded() {
                                isAllLogoUploaded = false;
                            }
                        });
                        loadImageThread.start();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("errore", error.toString());
            }
        });
        imageRequest.setShouldCache(false);
        imageQueue.add(imageRequest);
    }
    //TODO LOAD LOGO CLASS
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
            loadAllLogosExecutor();
        }
    }

    //TODO: SWIPE REFRESH LISTENER
    private class OnRefreshListener implements SwipeRefreshLayout.OnRefreshListener{
        /**
         * Called when a swipe gesture triggers a refresh.
         */
        @Override
        public void onRefresh() {
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            if(!isAllLogoUploaded){
                loadAllLogosExecutor();
                //TODO salvare questa variabile nello stesso posto delle schedine in modo che resti sempre salvata
                isAllLogoUploaded=true;
            }
            StringRequest stringRequest = new StringRequest(Request.Method.GET, MATCH_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.i("response = ",response);
                            RefreshResultThread refreshResultThread = new RefreshResultThread(response, allMatch, new RefreshResultThread.LoadingListener() {
                                @Override
                                public void onFinishLoading() {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            singleBetAdapter.setAllMatches(allMatch);
                                           // rVLayoutManager.setPages(2);
                                            swipeRefreshLayout.setRefreshing(false);
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
                    Toast toast = Toast.makeText(getApplicationContext(),R.string.no_connection, Toast.LENGTH_SHORT);
                    toast.show();
                }
            });
            stringRequest.setShouldCache(false);
            queue.add(stringRequest);

        }
    }

    private void setAddMatchDialog(){
        final AddMatchDialog addMatchDialog = new AddMatchDialog(SingleBetActivity.this,bet.getArrayMatch(),SingleBetActivity.this);
        addMatchDialog.show();
        addMatchDialog.setCanceledOnTouchOutside(false);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = addMatchDialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        //lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        addMatchDialog.setOnAddNewItem(new AddMatchDialog.FinishEditDialog() {
            @Override
            public void onAddNewItem(PalimpsestMatch match) {
                addMatchDialog.dismiss();
                allMatch.add(match);
                bet.setArrayMatch(allMatch);
                singleBetAdapter.notifyItemInserted(allMatch.lastIndexOf(match));
            }
        });
    }

    private Uri saveDocumentToDB(String plainText)
            throws RemoteException {
        ContentProviderClient client = null;

        try {
            ContentValues v = new ContentValues();
         //   v.put(DocumentContentProvider.Columns.PHOTO_PATH,
          //          Uri.parse("android.resource://"+ BuildConfig.APPLICATION_ID+"/" + R.drawable.ic_betty_power_green).toString());
            if (plainText != null) {
                v.put(DocumentContentProvider.Columns.OCR_TEXT, plainText);
            }
            v.put(DocumentContentProvider.Columns.EVENT_NUMBER,String.valueOf(0));
           /* if (mParentId > -1) {
                v.put(DocumentContentProvider.Columns.PARENT_ID, mParentId);
            }*/
            client = getContentResolver().acquireContentProviderClient(DocumentContentProvider.CONTENT_URI);
            return client.insert(DocumentContentProvider.CONTENT_URI, v);
        } finally {
            if (client != null) {
                client.release();
            }
        }
    }

    //TODO SHOW HIDE TOOLBAR
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
}
