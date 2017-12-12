package com.bettypower.betMatchFinder;

import android.content.Context;
import android.util.Log;

import com.bettypower.betMatchFinder.entities.MatchToFind;
import com.bettypower.betMatchFinder.listeners.CompleteElaborationListener;
import com.bettypower.betMatchFinder.listeners.RealTimeElaborationListener;
import com.bettypower.betMatchFinder.listeners.StaticElaborationListener;
import com.bettypower.betMatchFinder.threads.RealTimeThread;
import com.bettypower.betMatchFinder.threads.StaticThread;
import com.bettypower.entities.Comparator.PalimpsestMatchByPalimpsestComparator;
import com.bettypower.entities.Match;
import com.bettypower.entities.PalimpsestMatch;
import com.bettypower.entities.ParcelableMatch;
import com.bettypower.entities.ParcelableTeam;
import com.bettypower.entities.Team;
import com.bettypower.matchFinder.ResponseResolver;
import com.renard.ocr.TextFairyApplication;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by giuliopettenuzzo on 21/10/17.
 */

public class Resolver {

    private TextFairyApplication application;
    private CompleteElaborationListener completeElaborationListener;

    private ArrayList<PalimpsestMatch> allPalimpsestMatch;  //all the palimpsest from bookmaker
    private ArrayList<MatchToFind> allMatchFoundByRealTimeOCR;
    private ArrayList<String> allQuoteFoundByRealTimeOCR;
    private ArrayList<MatchToFind> allMatchFoundByStaticOCR;

    private String realTimeResponse;
    private ArrayList<String> allStringInRealTimeResponse;
    private String staticResponse;
    private RealTimeThread realTimeThread;
    private StaticThread staticThread;
    private boolean realTimeExecutionFinish = false;
    private boolean staticExecutionFinish = false;


    /**
     * this constructor also sort the palimpsest match array list in order by the palimpsest number
     * @param allPalimpsestMatch
     * @param context
     */
    public Resolver(ArrayList<PalimpsestMatch> allPalimpsestMatch,Context context){
        this.allPalimpsestMatch = allPalimpsestMatch;
        application = (TextFairyApplication) context;
    }

    public Resolver(Context context){
        application = (TextFairyApplication) context;
    }


    /***********************************************************************************************
     *********************************   PUBLIC METHODS   ******************************************
     **********************************************************************************************/

    /**
     * set response from google ocr
     * @param realTimeResponse response from google ocr
     */
    public void setRealTimeResponse(String realTimeResponse){
        this.realTimeResponse = realTimeResponse;
        checkExeguible();
    }

    public void setRealTimeResponse(ArrayList<String> realTimeResponse){
        //TODO FATTO QUESTO DEVE FUNZIONARE CAZZO
        allStringInRealTimeResponse = realTimeResponse;
        checkExeguible();
    }

    /**
     * set response from renard ocr
     * @param staticResponse response from renard ocr
     */
    public void setNormalResponse(String staticResponse){
        this.staticResponse = staticResponse;
        checkExeguible();
    }
    /**
     * set the listener created in order to know when all the elaboration are done
     * @param completeElaborationListener
     */
    public void setCompleteElaborationListener(CompleteElaborationListener completeElaborationListener){
        this.completeElaborationListener = completeElaborationListener;
    }

    /***********************************************************************************************
     *********************************   PRIVATE METHODS   ******************************************
     **********************************************************************************************/

    /**
     * called when one of the two thread finish, this method is used in order to know when every
     * thread execution  are terminated
     */
    private void notifyThreads(){
        if(!realTimeExecutionFinish && realTimeResponse!=null){
            if(realTimeThread == null){
                executeRealTimeResponse();
            }
        }
        if(!staticExecutionFinish && staticResponse!=null && realTimeExecutionFinish){
            if(staticThread == null){
                executeStaticResponse();
            }
        }
        if(staticExecutionFinish && realTimeExecutionFinish){
            notifyThreadsFinish();
        }

    }

    private void notifyThreadsFinish(){
        ArrayList<Match> allMatchFound = new ArrayList<>();
        for (MatchToFind currentMatch:allMatchFoundByStaticOCR
             ) {
            if(currentMatch.getPalimpsestMatch().size()==1) {
                PalimpsestMatch palimpsestMatch = currentMatch.getPalimpsestMatch().get(0);
                Match match = new ParcelableMatch(palimpsestMatch.getHomeTeam(),palimpsestMatch.getAwayTeam());
                match.setTime(palimpsestMatch.getDate() + " " + palimpsestMatch.getHour());
                match.setHomeResult("1");
                match.setAwayResult("1");
                if(currentMatch.getBet()!=null) {
                    match.setBet(currentMatch.getBet());
                }
                if(currentMatch.getBetKind()!=null){
                    match.setBetKind(currentMatch.getBetKind());
                }
                //TODO quote
                allMatchFound.add(match);
            }
        }
        completeElaborationListener.onElaborationComplete(allMatchFound);
    }

    /**
     * the apposite variable is changing when the match are loaded
     */
    private void checkExeguible(){
        if(allPalimpsestMatch == null){
            application.setAllMatchLoadListener(new TextFairyApplication.AllMatchLoadListener() {
                @Override
                public void onMatchLoaded(ArrayList<PalimpsestMatch> allMatch) {
                    allPalimpsestMatch = allMatch;
                    notifyThreads();
                }
            });
        }
        else{
            notifyThreads();
        }
    }

    /**
     * start thread from google ocr and wait the response in a listener
     */
    private void executeRealTimeResponse(){
        realTimeThread = new RealTimeThread(allPalimpsestMatch,realTimeResponse);
        Log.i("REAL TIME RESPONSE",realTimeResponse);
        realTimeThread.start();
        realTimeThread.setRealTimeElaborationListener(new RealTimeElaborationListener() {
            @Override
            public void onElaborationCompleted(ArrayList<MatchToFind> allMatchFound,ArrayList<String> quote) {
                realTimeExecutionFinish = true;
                allMatchFoundByRealTimeOCR = allMatchFound;
                allQuoteFoundByRealTimeOCR = quote;
                notifyThreads();
            }
        });
    }

    /**
     * start thread from renard ocr and wait the response in a listener
     */
    private void executeStaticResponse(){
        staticThread = new StaticThread(allPalimpsestMatch,staticResponse,allMatchFoundByRealTimeOCR,allQuoteFoundByRealTimeOCR);
        Log.i("STATIC RESPONSE",staticResponse);
        staticThread.start();
        staticThread.setStaticElaborationListener(new StaticElaborationListener() {
            @Override
            public void onElaborationCompleted(ArrayList<MatchToFind> allMatchFound) {
                allMatchFoundByStaticOCR = allMatchFound;
                staticExecutionFinish = true;
                notifyThreads();
            }
        });
    }

}
