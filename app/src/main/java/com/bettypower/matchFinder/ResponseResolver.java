package com.bettypower.matchFinder;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.bettypower.entities.Match;
import com.bettypower.entities.PalimpsestMatch;
import com.bettypower.entities.ParcelableMatch;
import com.bettypower.threads.NormalFinderThread;
import com.bettypower.threads.RealTimeFinderThread;
import com.renard.ocr.TextFairyApplication;

import java.util.ArrayList;

/**
 * Created by giuliopettenuzzo on 21/09/17.
 */

public class ResponseResolver {

    private RealTimeFinderThread realTimeFinderThread;
    private NormalFinderThread normalThread;

    private ArrayList<PalimpsestMatch> allPalimpsestMatch;
    private ArrayList<PalimpsestMatch> allMatchFromGoogleOcr;
    private ArrayList<PalimpsestMatch> allMatchFromRenardOcr;
    private ArrayList<PalimpsestMatch> allMatchFound;
    private ArrayList<PalimpsestMatch> totalUnsafeMatches;

    private Context context;
    private TextFairyApplication application;
    private String renardOcrResponse;
    private String googleOcrResponse;
    private CompleteElaborationListener completeElaborationListener;

    public ResponseResolver(ArrayList<PalimpsestMatch> allPalimpsestMatch, Context context){
        this.context = context;
        application = (TextFairyApplication) context;
        this.allPalimpsestMatch = allPalimpsestMatch;
    }

    /**
     * start thread from google ocr and wait the response in a listener
     */
    private void getMatchFromGoogleOcr(){
        realTimeFinderThread = new RealTimeFinderThread(allPalimpsestMatch,googleOcrResponse);
        realTimeFinderThread.start();
        realTimeFinderThread.setThreadFinishListener(new RealTimeFinderThread.ThreadFinishListener() {
            @Override
            public void onThreadFinish(ArrayList<PalimpsestMatch> allMatchFound) {
                allMatchFromGoogleOcr = allMatchFound;
                notifyThreads();
            }
        });

    }

    /**
     * start thread from renard ocr and wait the response in a listener
     */
    private void getMatchFromRenardOcr(){
        normalThread = new NormalFinderThread(allPalimpsestMatch,renardOcrResponse);
        normalThread.start();
        normalThread.setThreadFinishListener(new NormalFinderThread.NormalThreadFinishListener() {
            @Override
            public void onThreadFinish(ArrayList<PalimpsestMatch> allMatchFound) {
                allMatchFromRenardOcr = allMatchFound;
                notifyThreads();
            }
        });
    }

    /**
     * called when one of the two thread finish, this method is used in order to know when every
     * thread execution  are terminated
     */
    private void notifyThreads(){
        if(allMatchFromGoogleOcr == null && googleOcrResponse!=null){
            if(realTimeFinderThread == null){
                getMatchFromGoogleOcr();
            }
        }
        if(allMatchFromRenardOcr == null && renardOcrResponse!=null){
            if(normalThread == null){
                getMatchFromRenardOcr();
            }
        }
        if(allMatchFromRenardOcr!=null && allMatchFromGoogleOcr!=null){
            ArrayList<PalimpsestMatch> unsafeMatchFromRenardOcr = normalThread.getUnsafeMatches();
            ArrayList<PalimpsestMatch> unsafeMatchFromGoogleOcr = realTimeFinderThread.getAllUnsafeMatch();
            totalUnsafeMatches = joinOcrUnsafeMatches(unsafeMatchFromRenardOcr,unsafeMatchFromGoogleOcr);
            notifyThreadsFinish();
        }

    }

    /**
     * join the unsafe matche from google and renard ocr
     * @param normal
     * @param realTime
     * @return
     */
    private ArrayList<PalimpsestMatch> joinOcrUnsafeMatches(ArrayList<PalimpsestMatch> normal, ArrayList<PalimpsestMatch> realTime){
        ArrayList<PalimpsestMatch> result = new ArrayList<>();
        for (PalimpsestMatch currentPalimpsestMatch:normal
             ) {
            if(isPalimpsestMatchAlreadyInList(realTime,currentPalimpsestMatch)){
                currentPalimpsestMatch.setHomeResult("-");
                currentPalimpsestMatch.setAwayResult("-");
                if(!isMatchAlreadyInList(result,currentPalimpsestMatch)){
                    result.add(currentPalimpsestMatch);
                }
            }
        }
        return result;
    }

    /**
     * called when all the threads are finish
     * from here I am sure that I have the result of all the threads
     */
    private void notifyThreadsFinish(){
        allMatchFound = joinOcrResults(allMatchFromGoogleOcr,allMatchFromRenardOcr);
        for (PalimpsestMatch current:totalUnsafeMatches
             ) {
            if(!isMatchAlreadyInList(allMatchFound,current)){
                allMatchFound.add(current);
                Log.i("UNSAFE INSERTED",current.getHomeTeam().getName()+current.getAwayTeam().getName());
            }
        }
        ArrayList<PalimpsestMatch> namesFromGoogleOcr = realTimeFinderThread.getAllMatchFoundByName();
        ArrayList<PalimpsestMatch> namesFromRenardOcr = normalThread.getAllMatchFoundByName();
        ArrayList<PalimpsestMatch> palimpsestFromGoogleOcr = realTimeFinderThread.getAllMatchFoundByPalimpsest();
        ArrayList<PalimpsestMatch> palimpsestFromRenardOcr = normalThread.getAllMatchFoundByPalimpsest();
        //just in case the second ocr doesn't work
        if(namesFromGoogleOcr.size()==0 && palimpsestFromGoogleOcr.size()==0){
            for (PalimpsestMatch current:namesFromRenardOcr
                 ) {
                if(!isMatchAlreadyInList(allMatchFound,current)) {
                    allMatchFound.add(current);
                }
            }
            for (PalimpsestMatch current:palimpsestFromRenardOcr
                 ) {
                if(!isMatchAlreadyInList(allMatchFound,current)) {
                    allMatchFound.add(current);
                }
            }
        }
        else {
            for (PalimpsestMatch currentByName : namesFromRenardOcr
                    ) {
                if (isPalimpsestMatchAlreadyInList(namesFromGoogleOcr, currentByName) || isPalimpsestMatchAlreadyInList(palimpsestFromGoogleOcr, currentByName)) {
                    if (!isMatchAlreadyInList(allMatchFound, currentByName)) {
                        allMatchFound.add(currentByName);
                        Log.i("JOIN INSERTED", currentByName.getHomeTeam().getName() + currentByName.getAwayTeam().getName());
                    }
                }
            }
            for (PalimpsestMatch currentByPali : palimpsestFromRenardOcr
                    ) {
                if (isPalimpsestMatchAlreadyInList(namesFromGoogleOcr, currentByPali) || isPalimpsestMatchAlreadyInList(palimpsestFromGoogleOcr, currentByPali)) {
                    if (!isMatchAlreadyInList(allMatchFound, currentByPali)) {
                        allMatchFound.add(currentByPali);
                        Log.i("JOIN INSERTED", currentByPali.getHomeTeam().getName() + currentByPali.getAwayTeam().getName());
                    }
                }
            }
        }
        ArrayList<Match> finalMatchFound = fromPalimpsestToMatch(allMatchFound);
        //TODO qui si ordina la lista e si ricavano le puntate
       // SorterMatchInOrder sorterMatchInOrder = new SorterMatchInOrder(allMatchFound,);
        completeElaborationListener.onElaborationCompleted(finalMatchFound);
    }

    /**
     *  join the ocr about renard and google
     * @param matchOne
     * @param matchTwo
     * @return
     */
    private ArrayList<PalimpsestMatch> joinOcrResults(ArrayList<PalimpsestMatch> matchOne, ArrayList<PalimpsestMatch> matchTwo){
        ArrayList<PalimpsestMatch> result = new ArrayList<>();
        for (PalimpsestMatch currentMatchOne:matchOne
                ) {
            if(!isMatchAlreadyInList(result,currentMatchOne)){
                result.add(currentMatchOne);
            }
        }
        for (PalimpsestMatch currentMatchTwo:matchTwo
                ) {
            if(!isMatchAlreadyInList(result,currentMatchTwo)){
                result.add(currentMatchTwo);
            }
        }
        return result;
    }

    /***********************************************************************************************
     *********************************   AUSILIAR METHODS   ****************************************
     **********************************************************************************************/

    private boolean isMatchAlreadyInList(ArrayList<PalimpsestMatch> allMatch, PalimpsestMatch match){
        for (PalimpsestMatch currentMatch:allMatch
                ) {
            if(currentMatch.getHomeTeam().getName().equals(match.getHomeTeam().getName()) &&
                    currentMatch.getAwayTeam().getName().equals(match.getAwayTeam().getName())){
                return true;
            }
        }
        return false;
    }

    private ArrayList<Match> fromPalimpsestToMatch(ArrayList<PalimpsestMatch> allMatches){
        ArrayList<Match> result = new ArrayList<>();
        for (PalimpsestMatch currentPalimpsest:allMatches
             ) {
            Log.i("FINAL MATCH FOUND",currentPalimpsest.getHomeTeam().getName() + " " + currentPalimpsest.getAwayTeam().getName() + " " +
            currentPalimpsest.getCompletePalimpsest());
            Match match = (Match) currentPalimpsest;
            result.add(match);
        }
        return result;
    }

    /**
     * @param list
     * @param pali
     * @return return true if a match is present in the list, false otherwise
     */
    private boolean isPalimpsestMatchAlreadyInList(ArrayList<PalimpsestMatch> list,PalimpsestMatch pali){
        for (PalimpsestMatch current:list
                ) {
            if(pali.getCompletePalimpsest().equals(current.getCompletePalimpsest())){
                return true;
            }
        }
        return false;
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

    /***********************************************************************************************
     *********************************   PUBLIC METHODS   ******************************************
     **********************************************************************************************/

    /**
     * set response from google ocr
     * @param realTimeResponse
     */
    public void setRealTimeResponse(String realTimeResponse){
        googleOcrResponse = realTimeResponse;
        checkExeguible();
    }

    /**
     * set response from renard ocr
     * @param normalResponse
     */
    public void setNormalResponse(String normalResponse){
        renardOcrResponse = normalResponse;
        checkExeguible();
    }

    /**
     * set the listener created in order to know when all the elaboration are done
     * @param completeElaborationListener
     */
    public void setCompleteElaborationListener(CompleteElaborationListener completeElaborationListener){
        this.completeElaborationListener = completeElaborationListener;
    }

    public interface CompleteElaborationListener{
        void onElaborationCompleted(ArrayList<Match> allMatches);
    }

}
