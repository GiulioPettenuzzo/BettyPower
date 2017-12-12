package com.bettypower.betMatchFinder.threads;

import com.bettypower.betMatchFinder.StaticPredictor;
import com.bettypower.betMatchFinder.entities.MatchToFind;
import com.bettypower.betMatchFinder.listeners.StaticElaborationListener;
import com.bettypower.entities.PalimpsestMatch;

import java.util.ArrayList;

/**
 * Created by giuliopettenuzzo on 21/10/17.
 */

public class StaticThread extends Thread {

    private String staticResponse;
    private ArrayList<PalimpsestMatch> allPalimpsestMatch;
    private StaticElaborationListener staticElaborationListener;
    private ArrayList<MatchToFind> allMatchFoundByRealTimeOCR;
    private ArrayList<String> allQuoteFoundByRealTimeOCR;

    public StaticThread(ArrayList<PalimpsestMatch> allPalimpsestMatch, String realTimeResponse, ArrayList<MatchToFind> allMatchFoundByRealTimeOCR,ArrayList<String> allQuoteFoundByRealTimeOCR){
        this.allPalimpsestMatch = allPalimpsestMatch;
        this.staticResponse = realTimeResponse;
        this.allMatchFoundByRealTimeOCR = allMatchFoundByRealTimeOCR;
        this.allQuoteFoundByRealTimeOCR = allQuoteFoundByRealTimeOCR;
    }
    @Override
    public void run() {
        super.run();
        StaticPredictor predictor = new StaticPredictor(allPalimpsestMatch,staticResponse,allMatchFoundByRealTimeOCR,allQuoteFoundByRealTimeOCR);
        predictor.getAllElementFound(); //elaboration
        staticElaborationListener.onElaborationCompleted(predictor.getAllMatchesFound());
    }

    public void setStaticElaborationListener(StaticElaborationListener staticElaborationListener){
        this.staticElaborationListener = staticElaborationListener;
    }
}
