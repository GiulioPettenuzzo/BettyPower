package com.bettypower.threads;

import android.util.Log;

import com.bettypower.entities.HiddenResult;
import com.bettypower.entities.PalimpsestMatch;
import com.bettypower.unpacker.Unpacker;
import com.bettypower.unpacker.goalServeUnpacker;
import com.bettypower.util.HashMatchMap;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by giuliopettenuzzo on 21/07/17.
 *
 */

public class RefreshResultThread extends Thread{

    private ArrayList<PalimpsestMatch> allSavedMatch = new ArrayList<>();
    private String response;
    private LoadingListener loadingListener;
    private HashMatchMap hashMatchMap = new HashMatchMap();

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
                    String homeResult = goalServeMatch.getHomeResult().replace("?","-");
                    String awayResult = goalServeMatch.getAwayResult().replace("?","-");
                    if(!homeResult.equals("-") && !awayResult.equals("-")) {
                        savedMatch.setHomeResult(homeResult);
                        savedMatch.setAwayResult(awayResult);
                        savedMatch.setResultTime(goalServeMatch.getResultTime());
                        ArrayList<HiddenResult> hidden = goalServeMatch.getAllHiddenResult();
                        savedMatch.setAllHiddenResult(hidden);
                        break;
                    }
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
            Log.i("FOUND",paliOne.getHomeTeam().getName()+" - "+paliTwo.getHomeTeam().getName());
            String homeTeamOne = paliOne.getHomeTeam().getName().replace("-"," ").toUpperCase();
            String homeTeamTwo = paliTwo.getHomeTeam().getName().replace("-"," ").toUpperCase();
            String awayTeamOne = paliOne.getAwayTeam().getName().replace("-"," ").toUpperCase();
            String awayTeamTwo = paliTwo.getAwayTeam().getName().replace("-"," ").toUpperCase();
            Map<String,String> allHashMatch = hashMatchMap.getHashMatchMap();
            if(allHashMatch.containsKey(homeTeamOne) && allHashMatch.get(homeTeamOne).equals(homeTeamTwo) &&
                    allHashMatch.containsKey(homeTeamOne) && allHashMatch.get(homeTeamOne).equals(homeTeamTwo)){
                return true;
            }else if(allHashMatch.containsKey(homeTeamOne) && allHashMatch.get(homeTeamOne).equals(homeTeamTwo)){
                return true;
            }else if(allHashMatch.containsKey(awayTeamOne) && allHashMatch.get(awayTeamOne).equals(awayTeamTwo)){
                return true;
            }else {
                int commonHomeName = getNumberOfWordsInCommon(homeTeamOne, homeTeamTwo);
                int commonAwayName = getNumberOfWordsInCommon(awayTeamOne, awayTeamTwo);
                if (homeTeamOne.equalsIgnoreCase(homeTeamTwo) ||
                        awayTeamOne.equalsIgnoreCase(awayTeamTwo)) {
                    return true;
                } else if (commonHomeName > 0 || commonAwayName > 0) {
                    if (commonHomeName + commonAwayName >= 2) {
                        return true;
                    }
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
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdfh = new SimpleDateFormat("HH:mm",Locale.getDefault());
        String getCurrentTime = sdfh.format(c.getTime());

        String midnight = "00:00";
        String oneOclock = "01:00";

        String date = "";
        if (getCurrentTime.compareTo(midnight) >= 0 && getCurrentTime.compareTo(oneOclock) <= 0)
        {
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM", Locale.getDefault());
            cal.add(Calendar.DATE, -1);
            date = sdf.format(cal.getTime());
        }
        else
        {
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM", Locale.getDefault());
            date = sdf.format(cal.getTime());
        }
        return date;
    }

    public interface LoadingListener{
        void onFinishLoading(ArrayList<PalimpsestMatch> allMatches);
    }
}
