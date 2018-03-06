package com.bettypower.threads;

import com.bettypower.entities.HiddenResult;
import com.bettypower.entities.PalimpsestMatch;
import com.bettypower.unpacker.Unpacker;
import com.bettypower.unpacker.goalServeUnpacker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by giuliopettenuzzo on 21/07/17.
 *
 */

public class RefreshResultThread extends Thread{

    private ArrayList<PalimpsestMatch> allSavedMatch = new ArrayList<>();
    private String response;
    private LoadingListener loadingListener;

    public RefreshResultThread(String response, ArrayList<PalimpsestMatch> allSavedMatch, LoadingListener loadingListener){
        this.response = response;
        this.allSavedMatch = allSavedMatch;
        this.loadingListener = loadingListener;
    }

    @Override
    public void run() {
        Unpacker unpacker = new goalServeUnpacker(response);
        ArrayList<PalimpsestMatch> allMatchOnGoalServe = unpacker.getAllMatches();
        for (PalimpsestMatch savedMatch:allSavedMatch
             ) {
            for (PalimpsestMatch goalServeMatch: allMatchOnGoalServe
                 ) {
                if(isTheSameMatch(savedMatch,goalServeMatch)){
                    savedMatch.setHomeResult(goalServeMatch.getHomeResult().replace("?","-"));
                    savedMatch.setAwayResult(goalServeMatch.getAwayResult().replace("?","-"));
                    savedMatch.setResultTime(goalServeMatch.getResultTime());
                    ArrayList<HiddenResult> hidden = goalServeMatch.getAllHiddenResult();
                    savedMatch.setAllHiddenResult(hidden);
                    break;
                }
            }
        }
        loadingListener.onFinishLoading(allSavedMatch);
    }

    /*
     * @param paliOne palimpsest from current bet
     * @param paliTwo palimpsest from goal serve thread
     * @return
     */
    private boolean isTheSameMatch(PalimpsestMatch paliOne,PalimpsestMatch paliTwo){

        if(paliOne.getDate().equalsIgnoreCase(giveDate())) {
            int commonHomeName = getNumberOfWordsInCommon(paliOne.getHomeTeam().getName(),paliTwo.getHomeTeam().getName());
            int commonAwayName = getNumberOfWordsInCommon(paliOne.getHomeTeam().getName(),paliTwo.getHomeTeam().getName());
            if (paliOne.getHomeTeam().getName().equalsIgnoreCase(paliTwo.getHomeTeam().getName()) ||
                    paliOne.getAwayTeam().getName().equalsIgnoreCase(paliTwo.getAwayTeam().getName())) {
                return true;
            }
            else if(commonHomeName>0 || commonAwayName>0){
                if(commonHomeName + commonAwayName >= 2){
                    return true;
                }
            }
        }
        return false;
    }

    private int getNumberOfWordsInCommon(String first,String second){
        first = first.toUpperCase();
        second = second.toUpperCase();
        List<String> wordsOfSecond = Arrays.asList(second.split(" "));
        int i = 0;
        //split and compare each word of the first string
        for (String word : first.split(" ")) {
            if(wordsOfSecond.contains(word) && word.length()>3)
                i++;
        }
        return i;
    }

    private String giveDate() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM", Locale.getDefault());
        return sdf.format(cal.getTime());
    }

    public interface LoadingListener{
        void onFinishLoading(ArrayList<PalimpsestMatch> allMatches);
    }
}
