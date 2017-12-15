package com.bettypower;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bettypower.adapters.SingleBetAdapter;
import com.bettypower.betMatchFinder.listeners.CompleteElaborationListener;
import com.bettypower.entities.HiddenResult;
import com.bettypower.entities.Match;
import com.bettypower.entities.PalimpsestMatch;
import com.bettypower.entities.ParcelableHiddenResult;
import com.bettypower.entities.ParcelableMatch;
import com.bettypower.entities.ParcelablePalimpsestMatch;
import com.bettypower.entities.ParcelableTeam;
import com.bettypower.listeners.ExpandCollapseClickListener;
import com.bettypower.listeners.HidingScrollListener;
import com.bettypower.listeners.PreLoadingLinearLayoutManager;
import com.bettypower.matchFinder.*;
import com.bettypower.matchFinder.MatchFinder;
import com.bettypower.threads.LoadImageThread;
import com.bettypower.threads.NormalFinderThread;
import com.bettypower.threads.RealTimeFinderThread;
import com.bettypower.threads.RefreshResultThread;
import com.bettypower.unpacker.AllMatchesByPalimpsestURLUnpacker;
import com.bettypower.unpacker.AllMatchesByPalimpsestUnpacker;
import com.bettypower.unpacker.Unpacker;
import com.bettypower.unpacker.goalServeUnpacker;
import com.bettypower.util.HashMatchUtil;
import com.renard.ocr.R;
import com.renard.ocr.TextFairyApplication;
import com.renard.ocr.documents.viewing.DocumentContentProvider;
import com.renard.ocr.documents.viewing.grid.DocumentGridActivity;
import com.renard.ocr.documents.viewing.single.DocumentActivity;

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

    private Toolbar toolbar;
    public ArrayList<Match> allMatch = new ArrayList<>();
    public ArrayList<Match> allMatchSelected = new ArrayList<>();
    public ArrayList<String> allLogosURL = new ArrayList<>();
    public ArrayList<PalimpsestMatch> allPalimpsestMatch = new ArrayList<>();
    private boolean isClicking = false;
    private boolean isAllLogoUploaded = true;
    private MatchesLoadListener allMatchLoadListener;
    private Uri uri;

    private String url = "http://www.goalserve.com/updaters/soccerupdate.aspx";
    private String image_url = "http://www.fishtagram.it/loghi.html";
    private static final String ALL_MATCH_STATE = "save_all_match";
    private static final String ALL_MATCH_SELECTED_STATE = "all_match_selected_state";
    private static final String EDIT_MODE = "edit_mode";
    private static final String IMAGE_URI = "image_uri";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_bet);
        setRecyclerAndManager();


        //AllMatchesByPalimpsestUnpacker allMatchesByPalimpsestUnpacker = new AllMatchesByPalimpsestUnpacker(getApplicationContext());
        if(savedInstanceState!=null && savedInstanceState.containsKey(ALL_MATCH_STATE)){
            allMatch = savedInstanceState.getParcelableArrayList(ALL_MATCH_STATE);
            if(savedInstanceState.containsKey(ALL_MATCH_SELECTED_STATE)){
                allMatchSelected = savedInstanceState.getParcelableArrayList(ALL_MATCH_SELECTED_STATE);
                singleBetAdapter = new SingleBetAdapter(getApplicationContext(),allMatch,allMatchSelected);
            }
            else{
                singleBetAdapter = new SingleBetAdapter(getApplicationContext(),allMatch);
            }
            if (savedInstanceState.containsKey(EDIT_MODE))
                editMode = savedInstanceState.getBoolean(EDIT_MODE);
            if(savedInstanceState.containsKey(EDIT_MODE))
                uri = savedInstanceState.getParcelable(IMAGE_URI);
             singleBetAdapter.setHasStableIds(true);
             rVSingleBet.setAdapter(singleBetAdapter);
             singleBetAdapter.setOutsideClickListener(new ActivityClickListener());
        } else {
            setMatchLoadListener(new MatchLoadListener());
            firstTimeMatchLoader();
         }
        //code to hide and show the toolbar
        rVSingleBet.setOnScrollListener(new HidingScrollListener());
        //code to refresh when scrolling up
        swipeRefreshLayout.setOnRefreshListener(new OnRefreshListener());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if(allMatch!=null) {
            outState.putParcelableArrayList(ALL_MATCH_STATE, allMatch);
        }
        if(singleBetAdapter!=null&&singleBetAdapter.getSelectedMatch()!=null) {
            outState.putParcelableArrayList(ALL_MATCH_SELECTED_STATE, singleBetAdapter.getSelectedMatch());
        }
        outState.putBoolean(EDIT_MODE,editMode);
        outState.putParcelable(IMAGE_URI,uri);
        super.onSaveInstanceState(outState);
    }

    /**
     * to set well the recycler view, the manager, and the swipeRefreshLayout
     */
    private void setRecyclerAndManager(){
        rVSingleBet = (RecyclerView) findViewById(R.id.rv_single_bet);
        rVSingleBet.setHasFixedSize(true);
        rVSingleBet.setItemViewCacheSize(25);
        rVSingleBet.setDrawingCacheEnabled(true);
        rVSingleBet.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        rVLayoutManager = new PreLoadingLinearLayoutManager(this);
        rVLayoutManager.setPages(0);
        rVLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rVSingleBet.setLayoutManager(rVLayoutManager);
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            //this will load all the item only if the window's position is in the beginning or in the end
            rVSingleBet.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    rVLayoutManager.setPages(0);

                    if(rVLayoutManager.findFirstVisibleItemPosition()==0||
                            rVLayoutManager.findLastVisibleItemPosition()==allMatch.size()-1){
                        rVLayoutManager.setPages(2);
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

    }

    MenuItem switchMenuItem;
    MenuItem shareMenuItem;
    MenuItem editMenuItem;
    MenuItem confirmMenuItem;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.single_bet_action_menu, menu);
        switchMenuItem = menu.findItem(R.id.switch_menu_item);
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
                enableEditStyle();
                return true;
            case R.id.confirm:
                HashMatchUtil utils = new HashMatchUtil();
                String stringArrayMatch = utils.fromArrayToString(allMatch);
                ContentValues values = new ContentValues();
                values.put(DocumentContentProvider.Columns.OCR_TEXT, stringArrayMatch);
                getApplicationContext().getContentResolver().update(uri,values,null,null);
                disableEditStyle();
                return true;
            case android.R.id.home:
                Cursor c = getContentResolver().query(uri, new String[]{DocumentContentProvider.Columns.OCR_TEXT}, null, null, null);
                c.moveToFirst();
                String text = c.getString(c.getPosition());
                HashMatchUtil util = new HashMatchUtil();
                allMatch = util.fromStringToArray(text);
                singleBetAdapter.setAllMatches(allMatch);
                disableEditStyle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void enableEditStyle(){
        toolbar.setNavigationIcon(R.drawable.cross);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        editMenuItem.setVisible(false);
        shareMenuItem.setVisible(false);
        confirmMenuItem.setVisible(true);
        editMode = true;
        singleBetAdapter.setEditMode(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.action_bar_edit_mode)));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.action_bar_edit_mode));
        }
    }

    private void disableEditStyle(){
        toolbar.setNavigationIcon(null);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        editMenuItem.setVisible(true);
        shareMenuItem.setVisible(true);
        confirmMenuItem.setVisible(false);
        editMode = false;
        singleBetAdapter.setEditMode(false);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.primary)));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.primary));
        }
    }

    //TODO


    private void firstTimeMatchLoader(){
        Intent intent = getIntent();
        final String result = intent.getStringExtra("testo_completo");
        final TextFairyApplication application = (TextFairyApplication) getApplicationContext();

        allMatch = intent.getParcelableArrayListExtra("all_match");
        uri = intent.getData();
        if(allMatch == null){
            Cursor c = getContentResolver().query(uri, new String[]{DocumentContentProvider.Columns.OCR_TEXT}, null, null, null);
            c.moveToFirst();
            String text = c.getString(c.getPosition());
            HashMatchUtil util = new HashMatchUtil();
            allMatch = util.fromStringToArray(text);
        }
        singleBetAdapter = new SingleBetAdapter(SingleBetActivity.this, allMatch);
        singleBetAdapter.setUri(uri);
        singleBetAdapter.setHasStableIds(true);
        rVSingleBet.setAdapter(singleBetAdapter);
        allMatchLoadListener.onMatchesLoaded();
        singleBetAdapter.setOutsideClickListener(new ActivityClickListener());
    }

    //TODO CLICK LISTENER CALLBACK
    /**
     * this is what doing during the animation click item:
     *
     */
    private class ActivityClickListener implements SingleBetAdapter.OutsideClicklistener{

        @Override
        public void onItemClicked(Context context, final SingleBetAdapter.ViewHolder holder, Match match, ArrayList<Match> isSelected) {
            final ExpandCollapseClickListener expandCollapseClickListener = new ExpandCollapseClickListener(context,holder,match,isSelected);
            holder.itemView.setOnClickListener(expandCollapseClickListener);
            expandCollapseClickListener.setCickStat(new ExpandCollapseClickListener.ClickStat() {
                @Override
                public void onClickStart() {
                    holder.itemView.setOnClickListener(null);
                    swipeRefreshLayout.setEnabled(false);
                    rVLayoutManager.setScrollEnabled(false);
                    isClicking = true;                         }

                @Override
                public void onClickEnds() {
                    holder.itemView.setOnClickListener(expandCollapseClickListener);
                    swipeRefreshLayout.setEnabled(true);
                    rVLayoutManager.setScrollEnabled(true);
                    isClicking = false;
                }
            });
        }
    }

    public void loadAllLogosExecutor(){
        RequestQueue imageQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest imageRequest = new StringRequest(Request.Method.GET, image_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        LoadImageThread loadImageThread = new
                                LoadImageThread(response,allMatch,allLogosURL,singleBetAdapter);
                        loadImageThread.setOnLoadLogoListener(new LoadImageThread.LoadLogoListener() {
                            @Override
                            public void onLoadLogoFinish(final Match match) {
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
                                            //DA QUI LANCIAVO VOLLEY DA QUESTA ACTIVITY
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

            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
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
                                            rVLayoutManager.setPages(2);
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
}
