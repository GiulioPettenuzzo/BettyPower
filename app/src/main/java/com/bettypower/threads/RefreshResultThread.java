package com.bettypower.threads;

import android.support.v4.widget.SwipeRefreshLayout;

import com.bettypower.adapters.SingleBetAdapter;
import com.bettypower.entities.HiddenResult;
import com.bettypower.entities.Match;
import com.bettypower.unpacker.Unpacker;
import com.bettypower.unpacker.goalServeUnpacker;

import java.util.ArrayList;

/**
 * Created by giuliopettenuzzo on 21/07/17.
 */

public class RefreshResultThread extends Thread{

    private ArrayList<Match> allSavedMatch = new ArrayList<>();
    private ArrayList<Match> allMatchOnGoalServe = new ArrayList<>();
    private String response;
    private LoadingListener loadingListener;

    SwipeRefreshLayout swipeRefreshLayout;
    SingleBetAdapter adapter;



    public RefreshResultThread(String response, ArrayList<Match> allSavedMatch, LoadingListener loadingListener){
        this.response = response;
        this.allSavedMatch = allSavedMatch;
        this.loadingListener = loadingListener;
    }

    @Override
    public void run() {
        //TODO FARE UNA VOLTA FATTO IL SERVER
        /*
        Unpacker unpacker = new goalServeUnpacker(response);
        allMatchOnGoalServe = unpacker.getAllMatches();
        for (Match savedMatch:allSavedMatch
             ) {
            for (Match goalServeMatch:allMatchOnGoalServe
                 ) {
                if(savedMatch.compareTo(goalServeMatch)==true){
                    savedMatch.setHomeResult(goalServeMatch.getHomeResult());
                    savedMatch.setAwayResult(goalServeMatch.getAwayResult());
                    savedMatch.setTime(goalServeMatch.getTime());
                    ArrayList<HiddenResult> hidden = goalServeMatch.getAllHiddenResult();
                    savedMatch.setAllHiddenResult(goalServeMatch.getAllHiddenResult());
                    break;
                }
            }
        }*/
        loadingListener.onFinishLoading();
    }

    public interface LoadingListener{
        public void onFinishLoading();
    }
}
