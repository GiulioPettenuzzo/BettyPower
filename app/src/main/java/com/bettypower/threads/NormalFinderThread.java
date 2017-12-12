package com.bettypower.threads;

import android.util.Log;

import com.bettypower.entities.Match;
import com.bettypower.entities.PalimpsestMatch;
import com.bettypower.entities.ParcelableMatch;
import com.bettypower.matchFinder.LikelyHoodMatch;
import com.bettypower.matchFinder.MatchFinder;

import java.util.ArrayList;

/**
 * Created by giuliopettenuzzo on 15/09/17.
 */

public class NormalFinderThread extends Thread {

    private ArrayList<PalimpsestMatch> allPalimpsestMatch;
    private String normalResult;
    private ArrayList<PalimpsestMatch> allMatchFound;
    private  ArrayList<PalimpsestMatch> allMatchFoundByName;
    private ArrayList<PalimpsestMatch> allMatchFoundByPalimpsest;
    private ArrayList<PalimpsestMatch> unsafeMatch = new ArrayList<>();
    private NormalThreadFinishListener threadFinishListener;


    public NormalFinderThread(ArrayList<PalimpsestMatch> allPalimpsestMatch,String result){
        this.allPalimpsestMatch = allPalimpsestMatch;
        this.normalResult = result;
        allMatchFound = new ArrayList<>();
    }


    @Override
    public void run() {
        super.run();
        Log.i("NORMAL  THREAD", "START");

        com.bettypower.matchFinder.MatchFinder matchFinder = new MatchFinder(allPalimpsestMatch,normalResult);
        ArrayList<PalimpsestMatch> allPalimpsestMatchFound = matchFinder.getAllMatchFound();
        for (PalimpsestMatch currentMatch:allPalimpsestMatchFound
                ) {
            Log.i("NORMAL OCR : ",currentMatch.getHomeTeam().getName() + " " + currentMatch.getAwayTeam().getName() + " " + currentMatch.getPalimpsest() +  currentMatch.getEventNumber());
            currentMatch.setHomeResult("-");
            currentMatch.setAwayResult("-");
            allMatchFound.add(currentMatch);
        }
        allMatchFoundByName = matchFinder.getMatchFoundByName();
        allMatchFoundByPalimpsest = matchFinder.getMatchFoundByPalimpsest();
        unsafeMatch = likelyToPalimpsest(matchFinder.getAllUnsafeMatch());
        threadFinishListener.onThreadFinish(allMatchFound);
        Log.i("NORMAL  THREAD", "FINISH");

    }

    /**
     * convert likelyHoodMatch to PalimpsestMatch
     * @param allLikelyMatches
     * @return
     */
    private ArrayList<PalimpsestMatch> likelyToPalimpsest(ArrayList<LikelyHoodMatch> allLikelyMatches){
        ArrayList<PalimpsestMatch> allPaliMatches = new ArrayList<>();
        for (LikelyHoodMatch current:allLikelyMatches
             ) {
            PalimpsestMatch palimpsestMatch = current.getPalimpsestMatch();
            allPaliMatches.add(palimpsestMatch);
        }
        return allPaliMatches;
    }

    /***********************************************************************************************
     *********************************   PUBLIC METHODS   ******************************************
     **********************************************************************************************/

    public ArrayList<PalimpsestMatch> getAllMatchFoundByName(){
        return allMatchFoundByName;
    }

    public ArrayList<PalimpsestMatch> getAllMatchFoundByPalimpsest(){
        return allMatchFoundByPalimpsest;
    }

    public ArrayList<PalimpsestMatch> getUnsafeMatches(){
        return unsafeMatch;
    }

    public void setThreadFinishListener(NormalThreadFinishListener threadFinishListener){
        this.threadFinishListener = threadFinishListener;
    }

    public interface NormalThreadFinishListener{
        void onThreadFinish(ArrayList<PalimpsestMatch> allMatchFound);
    }
}
