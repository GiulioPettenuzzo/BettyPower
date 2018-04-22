package com.bettypower.betMatchFinder.threads;


import com.bettypower.betMatchFinder.RealTimePredictor;
import com.bettypower.betMatchFinder.listeners.RealTimeElaborationListener;
import com.bettypower.entities.PalimpsestMatch;

import java.util.ArrayList;

/**
 * This is the main thread of the real time OCR which process the text he detect
 * Created by giuliopettenuzzo on 21/10/17.
 */

public class RealTimeThread extends Thread {

    private String realTimeResponse;
    private ArrayList<PalimpsestMatch> allPalimpsestMatch;
    private RealTimeElaborationListener realTimeElaborationListener;

    public RealTimeThread(ArrayList<PalimpsestMatch> allPalimpsestMatch, String realTimeResponse){
        this.allPalimpsestMatch = allPalimpsestMatch;
        this.realTimeResponse = realTimeResponse;
    }

    @Override
    public void run() {
        super.run();
        RealTimePredictor predictor = new RealTimePredictor(allPalimpsestMatch,realTimeResponse);
        predictor.elaborateResponsetToFindMatch(); //elaboration
        realTimeElaborationListener.onElaborationCompleted(predictor.getAllMatchToFind(),predictor.getAllQuoteTofind(),predictor.getBookmakerAndMoney());
    }

    public void setRealTimeElaborationListener(RealTimeElaborationListener realTimeElaborationListener){
        this.realTimeElaborationListener = realTimeElaborationListener;
    }
}
