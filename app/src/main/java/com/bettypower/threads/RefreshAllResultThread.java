package com.bettypower.threads;

import com.bettypower.entities.PalimpsestMatch;
import com.bettypower.unpacker.AllMatchesUnpacker;

import java.util.ArrayList;

/**
 * This class charge all the palimpsest match from server
 * Created by giuliopettenuzzo on 06/03/18.
 */

public class RefreshAllResultThread extends Thread {

    private ArrayList<PalimpsestMatch> allSavedMatch = new ArrayList<>();
    private String response;
    private LoadingListener loadingListener;

    public RefreshAllResultThread(String response, ArrayList<PalimpsestMatch> allSavedMatch, LoadingListener loadingListener){
        this.response = response;
        this.allSavedMatch = allSavedMatch;
        this.loadingListener = loadingListener;
    }
    @Override
    public void run() {
        AllMatchesUnpacker allMatchesUnpacker = new AllMatchesUnpacker(response);
        ArrayList<PalimpsestMatch> allPalimpsestMatch = allMatchesUnpacker.getAllMatches();
        loadingListener.onAllPalimpsestReady(allPalimpsestMatch);
        checkForResult(allPalimpsestMatch);
        loadingListener.onBetPalimpsestReady(allSavedMatch);
    }

    private void checkForResult(ArrayList<PalimpsestMatch> allPalimpsestMatch){
        for (PalimpsestMatch currentMatch : allSavedMatch
                ) {
            for (PalimpsestMatch currentResultMatch:allPalimpsestMatch
                    ) {
                if(currentMatch.compareTo(currentResultMatch)){
                    if(!currentResultMatch.getHomeResult().equals("-") && ! currentResultMatch.getAwayResult().equals("-")) {
                        currentMatch.setHomeResult(currentResultMatch.getHomeResult());
                        currentMatch.setAwayResult(currentResultMatch.getAwayResult());
                        currentMatch.setResultTime(currentResultMatch.getResultTime());
                        currentMatch.setAllHiddenResult(currentResultMatch.getAllHiddenResult());
                        break;
                    }
                }
            }

        }
    }

    public interface LoadingListener{
        void onAllPalimpsestReady(ArrayList<PalimpsestMatch> allMatches);
        void onBetPalimpsestReady(ArrayList<PalimpsestMatch> allMatches);

    }
}
