package com.bettypower;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
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

    Unpacker unpacker;
    Toolbar toolbar;
    public ArrayList<Match> allMatch = new ArrayList<>();
    public ArrayList<Match> allMatchSelected = new ArrayList<>();
    public ArrayList<String> allLogosURL = new ArrayList<>();
    public ArrayList<PalimpsestMatch> allPalimpsestMatch = new ArrayList<>();
    private boolean isClicking = false;
    private boolean isAllLogoUploaded = true;
    private MatchesLoadListener allMatchLoadListener;

    private String url = "http://www.goalserve.com/updaters/soccerupdate.aspx";
    private String image_url = "http://www.fishtagram.it/loghi.html";
    private static final String ALL_MATCH_STATE = "save_all_match";
    private static final String ALL_MATCH_SELECTED_STATE = "all_match_selected_state";


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
/**
    private void firstTimeMatchLoader(){
        RequestQueue queue = Volley.newRequestQueue(this);
        final StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        unpacker = new goalServeUnpacker(response);
                        allMatch = unpacker.getAllMatches();

                        ArrayList<Match> allMatches = new ArrayList<>();
                        //da qui : SOLO LA PRIMA VOLTA CHE L'UTENTE REGISTRA LA SCHEDINA
                        for (int i = 0; i <= 5; i++) {*/
                            //TODO quando avrai inserito tutti i loghi non avrai piu bisogno di questo
                            /**
                             Team teamhome = new ParcelableTeam("barcellona");
                             allMatch.get(i).setHomeTeam(teamhome);
                             Team teamaway = new ParcelableTeam("realmadrid");
                             allMatch.get(i).setAwayTeam(teamaway);
                             */
    /**
     * Initialize the contents of the Activity's standard options menu.  You
     * should place your menu items in to <var>menu</var>.
     * <p>
     * <p>This is only called once, the first time the options menu is
     * displayed.  To update the menu every time it is displayed, see
     * {@link #onPrepareOptionsMenu}.
     * <p>
     * <p>The default implementation populates the menu with standard system
     * menu items.  These are placed in the {@link Menu#CATEGORY_SYSTEM} group so that
     * they will be correctly ordered with application-defined menu items.
     * Deriving classes should always call through to the base implementation.
     * <p>
     * <p>You can safely hold on to <var>menu</var> (and any items created
     * from it), making modifications to it as desired, until the next
     * time onCreateOptionsMenu() is called.
     * <p>
     * <p>When you add items to the menu, you can implement the Activity's
     * {@link #onOptionsItemSelected} method to handle them there.
     *
     * @param menu The options menu in which you place your items.
     * @return You must return true for the menu to be displayed;
     * if you return false it will not be shown.
     * @see #onPrepareOptionsMenu
     * @see #onOptionsItemSelected
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.single_bet_action_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //TODO
    /*
    @Override
public boolean onOptionsItemSelected(MenuItem item) {
    // Handle item selection
    switch (item.getItemId()) {
    case R.id.new_game:
        newGame();
        return true;
    case R.id.help:
        showHelp();
        return true;
    default:
        return super.onOptionsItemSelected(item);
    }
}
     */
    /**
                            allMatches.add(allMatch.get(i));

                        }
                        allMatch = allMatches;
                        singleBetAdapter = new SingleBetAdapter(getApplicationContext(), allMatch);
                        singleBetAdapter.setHasStableIds(true);
                        rVSingleBet.setAdapter(singleBetAdapter);
                        allMatchLoadListener.onMatchesLoaded();
                        singleBetAdapter.setOutsideClickListener(new ActivityClickListener());

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast toast = Toast.makeText(getApplicationContext(),R.string.check_connection, Toast.LENGTH_SHORT);
                toast.show();
                refreshButton.setVisibility(View.VISIBLE);
                Log.i("errore", error.toString());

            }
        });
        stringRequest.setShouldCache(false);

        queue.add(stringRequest);
    }*/

    private void firstTimeMatchLoader(){
        Intent intent = getIntent();
        final String result = intent.getStringExtra("testo_completo");
        final TextFairyApplication application = (TextFairyApplication) getApplicationContext();

        allMatch = intent.getParcelableArrayListExtra("all_match");
        Uri uri = intent.getData();
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
    private ArrayList<Match> addBetter(ArrayList<Match> allPalimpsestMatch){
        Match paliOne = new ParcelableMatch(new ParcelableTeam("Melbourne City FC"),new ParcelableTeam("Wellington Phoenix"));
        paliOne.setTime("21/10 08:35");
        paliOne.setHomeResult("1");
        paliOne.setAwayResult("0");
        paliOne.setBet("GOAL");
        HiddenResult hiddenResultOne = new ParcelableHiddenResult("Matthew Ridenton","68'","Yellowcard",1);
        HiddenResult hiddenResultTwo = new ParcelableHiddenResult("Ross McCormack","69'","Goal",0);
        HiddenResult hiddenResultThree = new ParcelableHiddenResult("Ross McCormack","81'","Yellowcard",0);
        ArrayList<HiddenResult> result = new ArrayList<>();
        result.add(hiddenResultOne);
        result.add(hiddenResultTwo);
        result.add(hiddenResultThree);
        paliOne.setAllHiddenResult(result);

        Match paliTwo = new ParcelableMatch(new ParcelableTeam("FC Ska-Energiya Khabarovsk"),new ParcelableTeam("UFA"));
        paliTwo.setTime("21/10 10:30");
        paliTwo.setHomeResult("2");
        paliTwo.setAwayResult("2");
        paliTwo.setBet("NO GOAL");
        HiddenResult hiddenResultFour = new ParcelableHiddenResult("Aleksandr Dimidko","11'","Goal",0);
        HiddenResult hiddenResultFive = new ParcelableHiddenResult("Pavel Alikin","30'","Yellowcard",1);
        HiddenResult hiddenResultSix = new ParcelableHiddenResult("Denys Dedechko","39'","Goal",0);
        HiddenResult hiddenResultSeven = new ParcelableHiddenResult("Denis Tumasyan","44'","Yellowcard",1);
        HiddenResult hiddenResultEight = new ParcelableHiddenResult("Vyacheslav Krotov","54'","Goal",1);
        HiddenResult hiddenResultNine = new ParcelableHiddenResult("Bojan Jokić","56'","Yellowcard",1);
        HiddenResult hiddenResultTen = new ParcelableHiddenResult("Giorgi Navalovski","63'","Yellowcard",0);
        HiddenResult hiddenResultEleven = new ParcelableHiddenResult("Vyacheslav Krotov","72'","Goal",1);
        HiddenResult hiddenResultTwelve = new ParcelableHiddenResult("Konstantin Savichev","89'","Redcard",0);
        ArrayList<HiddenResult> resultTwo = new ArrayList<>();
        result.add(hiddenResultFour);
        result.add(hiddenResultFive);
        result.add(hiddenResultSix);
        result.add(hiddenResultSeven);
        result.add(hiddenResultEight);
        result.add(hiddenResultNine);
        result.add(hiddenResultTen);
        result.add(hiddenResultEleven);
        result.add(hiddenResultTwelve);
        paliTwo.setAllHiddenResult(result);

        Match paliTheree = new ParcelableMatch(new ParcelableTeam("Sydney"),new ParcelableTeam("Western Sydney"));
        paliTheree.setTime("21/10 10:50");
        paliTheree.setHomeResult("2");
        paliTheree.setAwayResult("2");
        paliTheree.setBet("1X");
        ArrayList<HiddenResult> resultThree = new ArrayList<>();
        HiddenResult hiddenResultFourTeen = new ParcelableHiddenResult("Oriol Riera","3'","Goal",1);
        HiddenResult hiddenResultThirteen = new ParcelableHiddenResult("Bobô","38'","Goal",0);
        HiddenResult hiddenResultSixTeen = new ParcelableHiddenResult("Brendan Hamill","30'","Goal",1);
        HiddenResult hiddenResultFifteen = new ParcelableHiddenResult("Joshua Brillante","61'","Goal",0);
        resultThree.add(hiddenResultThirteen);
        resultThree.add(hiddenResultFourTeen);
        resultThree.add(hiddenResultSixTeen);
        resultThree.add(hiddenResultFifteen);
        paliTheree.setAllHiddenResult(resultThree);

        Match paliFour = new ParcelableMatch(new ParcelableTeam("FK Tonso"),new ParcelableTeam("FC Rostov"));
        paliFour.setTime("21/10 13:00");
        paliFour.setHomeResult("1");
        paliFour.setAwayResult("1");
        paliFour.setBet("OVER 2,5");
        ArrayList<HiddenResult> resultFour = new ArrayList<>();
        HiddenResult hiddenResultTwenty = new ParcelableHiddenResult("Yevgeni Markov","17'","Goal",0);
        HiddenResult hiddenResultTwentyOne = new ParcelableHiddenResult("Yevgeni Chernov","21'","Yellowcard",0);
        HiddenResult hiddenResultTwentyTwo = new ParcelableHiddenResult("Sergei Pesyakov","33'","Yellowcard",1);
        HiddenResult hiddenResultTwentyThree = new ParcelableHiddenResult("Nuno Miguel Monteiro Rocha","33'","Yellowcard",0);
        HiddenResult hiddenResultTwentyFour = new ParcelableHiddenResult("Sverrir Ingi Ingason","56'","Goal",1);
        HiddenResult hiddenResultTwentyFive = new ParcelableHiddenResult("Rade Dugalić","72'","Yellowcard",0);
        resultFour.add(hiddenResultTwenty);
        resultFour.add(hiddenResultTwentyOne);
        resultFour.add(hiddenResultTwentyTwo);
        resultFour.add(hiddenResultTwentyThree);
        resultFour.add(hiddenResultTwentyFour);
        resultFour.add(hiddenResultTwentyFive);
        paliFour.setAllHiddenResult(resultFour);

        Match paliFive = new ParcelableMatch(new ParcelableTeam("Levante"),new ParcelableTeam("Getafe"));
        paliFive.setTime("21/10 13:00");
        paliFive.setHomeResult("1");
        paliFive.setAwayResult("1");
        paliFive.setBet("2");
        ArrayList<HiddenResult> resultFive = new ArrayList<>();
        HiddenResult hiddenResultThirty = new ParcelableHiddenResult("Mauro Arambarri","5'","Yellowcard",1);
        HiddenResult hiddenResultThirtyOne = new ParcelableHiddenResult("Cala","14'","Yellowcard",1);
        HiddenResult hiddenResultThirtyTwo = new ParcelableHiddenResult("Jefferson Lerma","42'","Yellowcard",0);
        HiddenResult hiddenResultThirtyThree = new ParcelableHiddenResult("Fayçal Fajr","58'","Goal",1);
        HiddenResult hiddenResultThirtyFour = new ParcelableHiddenResult("José Luis Morales Nogales","62'","Goal",0);
        HiddenResult hiddenResultThirtySix = new ParcelableHiddenResult("Francisco Portillo","90'","Redcard",1);
        resultFive.add(hiddenResultThirty);
        resultFive.add(hiddenResultThirtyOne);
        resultFive.add(hiddenResultThirtyTwo);
        resultFive.add(hiddenResultThirtyThree);
        resultFive.add(hiddenResultThirtyFour);
        resultFive.add(hiddenResultThirtySix);
        paliFive.setAllHiddenResult(resultFive);



        Match paliSix = new ParcelableMatch(new ParcelableTeam("Chelsea"),new ParcelableTeam("Wataford FC"));
        paliSix.setTime("21/10 13:30");
        paliSix.setHomeResult("4");
        paliSix.setAwayResult("2");
        paliSix.setBet("1");
        ArrayList<HiddenResult> resultSix = new ArrayList<>();
        HiddenResult hiddenResultFourty = new ParcelableHiddenResult("José Holebas","12'","Yellowcard",1);
        HiddenResult hiddenResultFourtyOne = new ParcelableHiddenResult("Pedro","12'","Goal",0);
        HiddenResult hiddenResultFourtyTwo = new ParcelableHiddenResult("Adrian Mariappa","19'","Yellowcard",1);
        HiddenResult hiddenResultFourtyThree = new ParcelableHiddenResult("Antonio Rüdiger","23'","Yellowcard",0);
        HiddenResult hiddenResultFourtyFour = new ParcelableHiddenResult("Abdoulaye Doucouré","45+2'","Goal",1);
        HiddenResult hiddenResultFourtySix = new ParcelableHiddenResult("Roberto Pereyra","49'","Goal",1);
        HiddenResult hiddenResultFourtySeven = new ParcelableHiddenResult("Álvaro Morata","56'","Yellowcard",0);
        HiddenResult hiddenResultFourtyEight = new ParcelableHiddenResult("Michy Batshuayi","71'","Goal",0);
        HiddenResult hiddenResultFourtyNinet = new ParcelableHiddenResult("César Azpilicueta","87'","Goal",0);
        HiddenResult hiddenResultFourtyTen = new ParcelableHiddenResult("Michy Batshuayi","90'","Goal",0);
        resultSix.add(hiddenResultFourty);
        resultSix.add(hiddenResultFourtyOne);
        resultSix.add(hiddenResultFourtyTwo);
        resultSix.add(hiddenResultFourtyThree);
        resultSix.add(hiddenResultFourtyFour);
        resultSix.add(hiddenResultFourtySix);
        resultSix.add(hiddenResultFourtySeven);
        resultSix.add(hiddenResultFourtyEight);
        resultSix.add(hiddenResultFourtyNinet);
        resultSix.add(hiddenResultFourtyTen);
        paliSix.setAllHiddenResult(resultSix);


        allPalimpsestMatch.add(paliOne);
        allPalimpsestMatch.add(paliTwo);
        allPalimpsestMatch.add(paliTheree);
        allPalimpsestMatch.add(paliFour);
        allPalimpsestMatch.add(paliFive);
        allPalimpsestMatch.add(paliSix);
        return allPalimpsestMatch;
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
