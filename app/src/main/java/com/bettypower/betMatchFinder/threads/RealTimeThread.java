package com.bettypower.betMatchFinder.threads;

import android.util.Log;

import com.bettypower.betMatchFinder.Assembler;
import com.bettypower.betMatchFinder.Finder;
import com.bettypower.betMatchFinder.RealTimePredictor;
import com.bettypower.betMatchFinder.StaticPredictor;
import com.bettypower.betMatchFinder.entities.MatchToFind;
import com.bettypower.betMatchFinder.entities.OddsToFind;
import com.bettypower.betMatchFinder.listeners.RealTimeElaborationListener;
import com.bettypower.entities.PalimpsestMatch;

import java.util.ArrayList;

/**
 * This is the main thread of the real time OCR which process the text he detect
 * Created by giuliopettenuzzo on 21/10/17.
 */

public class RealTimeThread extends Thread {

    private String realTimeResponse;
    private ArrayList<String> allStringInRealTimeResponse;
    private ArrayList<PalimpsestMatch> allPalimpsestMatch;
    private RealTimeElaborationListener realTimeElaborationListener;

    public RealTimeThread(ArrayList<PalimpsestMatch> allPalimpsestMatch, String realTimeResponse){
        this.allPalimpsestMatch = allPalimpsestMatch;
        this.realTimeResponse = realTimeResponse;
    }

    public RealTimeThread(ArrayList<PalimpsestMatch> allPalimpsestMatch,ArrayList<String> allStringInRealTimeResponse){
        this.allPalimpsestMatch = allPalimpsestMatch;
        this.allStringInRealTimeResponse = allStringInRealTimeResponse;
    }

    @Override
    public void run() {
        super.run();
        RealTimePredictor predictor = new RealTimePredictor(allPalimpsestMatch,realTimeResponse);
        predictor.elaborateResponsetToFindMatch(); //elaboration
        for (OddsToFind currentOddsToFind:predictor.getAllQuoteTofind()
             ) {
            if(currentOddsToFind.getBet()!=null)
             Log.i("BET ===", currentOddsToFind.getBet());
            if(currentOddsToFind.getBetKind()!=null)
                Log.i("BET KIND ===" , currentOddsToFind.getBetKind());
            Log.i("---------","----------");
        }
        for (OddsToFind currentOddsToFind:predictor.getAllQuoteTofind()
                ) {
            if(currentOddsToFind.getOdds()!=null)
                Log.i("QUOTE ===", currentOddsToFind.getOdds());
            Log.i("---------","----------");
        }

        for (String currentString:predictor.getBookmakerAndMoney().keySet()){
            Log.i(currentString,predictor.getBookmakerAndMoney().get(currentString));
        }


        int i = 0;
        for (MatchToFind currentMatch:predictor.getAllMatchToFind()
                ) {
            Log.i("","");
            Log.i("NEW ","MATCH " + i);
            i++;
            Log.i("number match",String.valueOf(currentMatch.getPalimpsestMatch().size()));
            if(currentMatch.getPalimpsest()!=null)
                Log.i("PLIMPSEST",""+currentMatch.getPalimpsest());
            if(currentMatch.getHomeName()!=null)
                Log.i("HOME NAME",""+currentMatch.getHomeName());
            if(currentMatch.getAwayName()!=null)
                Log.i("AWAY NAME",""+currentMatch.getAwayName());
            if(currentMatch.getUnknownTeamName()!=null)
                Log.i("UNKNOWN NAME",""+currentMatch.getUnknownTeamName());
            if(currentMatch.getDate()!=null)
                Log.i("DATE",""+currentMatch.getDate());
            if(currentMatch.getHour()!=null)
                Log.i("HOUR",""+currentMatch.getHour());
            if(currentMatch.getBet()!=null) {
                Log.i("BET", "" + currentMatch.getBet());
            }
            if(currentMatch.getBetKind()!=null)
                Log.i("BET KIND",""+currentMatch.getBetKind());
            if(currentMatch.getOdds()!=null)
                Log.i("QUOTE",""+currentMatch.getOdds());
        }
        realTimeElaborationListener.onElaborationCompleted(predictor.getAllMatchToFind(),predictor.getAllQuoteTofind(),predictor.getBookmakerAndMoney());
    }

    public void setRealTimeElaborationListener(RealTimeElaborationListener realTimeElaborationListener){
        this.realTimeElaborationListener = realTimeElaborationListener;
    }
}
