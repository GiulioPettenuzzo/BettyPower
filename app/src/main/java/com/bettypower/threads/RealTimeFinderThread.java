package com.bettypower.threads;

import android.util.Log;

import com.bettypower.entities.Match;
import com.bettypower.entities.PalimpsestMatch;
import com.bettypower.entities.ParcelableMatch;
import com.bettypower.matchFinder.DamerauLevenshteinAlgorithm;
import com.bettypower.matchFinder.DateFinder;
import com.bettypower.matchFinder.LikelyHoodMatch;
import com.bettypower.matchFinder.NamesFinder;
import com.bettypower.matchFinder.PalimpsestFinder;
import com.renard.ocr.TextFairyApplication;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by giuliopettenuzzo on 15/09/17.
 */

public class RealTimeFinderThread extends Thread {

    ArrayList<PalimpsestMatch> allPalimpsestMatch;
    String realTimeResult;
    private ArrayList<PalimpsestMatch> allMatchFound; //all match safe found by palimpsest and name
    private ArrayList<PalimpsestMatch> allUnsafeMatch = new ArrayList<>();
    private ArrayList<PalimpsestMatch> allMatchFoundByName = new ArrayList<>();
    private ArrayList<PalimpsestMatch> allMatchFoundByPalimpsest = new ArrayList<>();
    private boolean isFinished = false;
    private ThreadFinishListener threadFinishListener;


    public RealTimeFinderThread(ArrayList<PalimpsestMatch> allPalimpsestMatch,String realTimeResult){
        this.allPalimpsestMatch = allPalimpsestMatch;
        this.realTimeResult = realTimeResult;
        //this.realTimeResult = cleanText(realTimeResult);
        Log.i("REALTIME TEXT CLEANED",realTimeResult);
        allMatchFound = new ArrayList<>();
    }


    @Override
    public void run() {
        super.run();
        Log.i("REAL TIME THREAD", "START");
        PalimpsestFinder palimpsestFinder = new PalimpsestFinder(allPalimpsestMatch, realTimeResult);
        ArrayList<LikelyHoodMatch> allPalimpsestMatchFound = palimpsestFinder.getAllLikelyHoodMatch();
        String completeNames = "";
        for (LikelyHoodMatch currentLikelyMatch : allPalimpsestMatchFound
                ) {
            allMatchFoundByPalimpsest.add(currentLikelyMatch.getPalimpsestMatch());
            String totalMatchNames = currentLikelyMatch.getPalimpsestMatch().getHomeTeam().getName() + " " + currentLikelyMatch.getPalimpsestMatch().getAwayTeam().getName();
            StringTokenizer token = new StringTokenizer(totalMatchNames);
            int totalWords = token.countTokens();
            int successCounter = 0;
            while(token.hasMoreTokens()){
                String word = token.nextToken();
                if(containsWithoutOcrError(word)){
                    successCounter++;
                }
            }

            PalimpsestMatch match = currentLikelyMatch.getPalimpsestMatch();
            match.setHomeResult("-");
            match.setAwayResult("-");

            if(successCounter > 2 || successCounter == totalWords){
                //no insert if the match doesn't exist
                if(!contains(allMatchFound,match)) {
                    completeNames = completeNames + match.getHomeTeam().getName() + " " + match.getAwayTeam().getName();
                    Log.i("REALTIME UNION : ", match.getHomeTeam().getName() + " " + match.getAwayTeam().getName() + " " + match.getCompletePalimpsest());
                    allMatchFound.add(match);
                }
            }
            //qui ci sono le partite il cui palinsesto è stato trovato ma il nome non tanto
            else{
                allUnsafeMatch.add(match);
            }
        }

        NamesFinder namesFinder = new NamesFinder(allPalimpsestMatch,realTimeResult);
        namesFinder.setDistanceToConsider(1);
        ArrayList<LikelyHoodMatch> allPaliFound = namesFinder.getAllLikelyHoodMatch();
        for (LikelyHoodMatch current:allPaliFound
                ) {
            DateFinder dateFinder = new DateFinder(realTimeResult,current.getPalimpsestMatch().getTime());
            if(dateFinder.getLikelyHoodDate()>1 && !checkAmbiguos(completeNames,current.getPalimpsestMatch())) {
                completeNames = completeNames + current.getPalimpsestMatch().getHomeTeam().getName() + " " + current.getPalimpsestMatch().getAwayTeam().getName();
                PalimpsestMatch currentPali = current.getPalimpsestMatch();
                allMatchFoundByName.add(currentPali);
            }
        }
        //IN CASE THE PALIMPSEST IS MISSING IN THE BET TICKET
        if(allMatchFound.size()==0){
            for (PalimpsestMatch current:allMatchFoundByName
                 ) {
                if(!checkAmbiguos(completeNames,current)) {
                    completeNames = completeNames + current.getHomeTeam().getName() + " " + current.getAwayTeam().getName();
                    allMatchFound.add(current);
                }
            }
        }


        isFinished = true;
        Log.i("REAL TIME THREAD", "FINISH");
        if (threadFinishListener != null) {
            threadFinishListener.onThreadFinish(allMatchFound);
        }
    }

    /**
     * @param currentWord
     * @return true if currentWord compare in realTimeResult without the ocr problems, false otherwise
     */
    private boolean containsWithoutOcrError(String currentWord){
        StringTokenizer token = new StringTokenizer(realTimeResult);
        while(token.hasMoreTokens()){
            String word = token.nextToken();
            if(checkWord(word)) {
                word = word.toUpperCase();
                if (checkEquals(word, currentWord)) {
                    return true;
                }
            }
        }
        return false;

    }

    /**
     * in order to avoid that the software has found a match because in the bet the matches where near
     * @param allTeamInString
     * @param palimpsestMatch
     * @return
     */
    private boolean checkAmbiguos(String allTeamInString, PalimpsestMatch palimpsestMatch){
        if(allTeamInString.contains(palimpsestMatch.getHomeTeam().getName()) || allTeamInString.contains(palimpsestMatch.getAwayTeam().getName())){
            return true;
        }
        return false;
    }

    /**
     * @param stringOne
     * @param stringTwo
     * @return true if strings are equals even if the 'i' and 'l' missmatch are done
     */
    private boolean checkEquals(String stringOne,String stringTwo){
        char[] charOne = stringOne.toCharArray();
        char[] charTwo = stringTwo.toCharArray();
        if(stringOne.length()!=stringTwo.length()){
            return false;
        }
        else{
            for(int current = 0; current<stringOne.length();current++){
                if (charOne[current] != charTwo[current]) {
                    if((charOne[current] == 'L' && charTwo[current] == 'I') || (charOne[current] == 'I' && charTwo[current] == 'L')) {
                        current++;
                    }
                    else{
                        return false;
                    }
                }
                else{
                    current++;
                }
            }
        }
        return true;
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

    /**
     * in order to avoid the words that can be ambiguos
     * @param word
     * @return
     */
    private boolean checkWord(String word){
        if(word.equalsIgnoreCase("calcio")){
            return false;
        }
        if(word.equalsIgnoreCase("liga")){
            return false;
        }
        try{
            Integer.parseInt(word);
            return false;
        }
        catch(Exception e){
            return true;
        }
    }

    /**
     * @param allMatch
     * @param match
     * @return true if allMatch contains the match
     */
    private boolean contains(ArrayList<PalimpsestMatch> allMatch, Match match){
        for (Match currentMatch: allMatch
                ) {
            if(currentMatch.compareTo(match)){
                return true;
            }
        }
        return false;
    }

    /***********************************************************************************************
     *********************************   PUBLIC METHODS   ******************************************
     **********************************************************************************************/

    /**
     * @return all matches unsafe about rightness
     */
    public ArrayList<PalimpsestMatch> getAllUnsafeMatch(){
        return allUnsafeMatch;
    }

    public ArrayList<PalimpsestMatch> getAllMatchFoundByPalimpsest(){
        return allMatchFoundByPalimpsest;
    }


    public ArrayList<PalimpsestMatch> getAllMatchFoundByName(){
        return allMatchFoundByName;
    }

    public void setThreadFinishListener(ThreadFinishListener threadFinishListener){
        this.threadFinishListener = threadFinishListener;
    }

    public interface ThreadFinishListener{
        /**
         * AllPalimpsestMatchFound is a list that contains the same match of allMatchFound but with palimpsest
         * this is useful only when the global list of match found is sorting to be in order
         */
        void onThreadFinish(ArrayList<PalimpsestMatch> allMatchFound);
    }


}
