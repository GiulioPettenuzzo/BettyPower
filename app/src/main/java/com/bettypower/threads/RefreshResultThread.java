package com.bettypower.threads;

import android.util.Log;

import com.bettypower.entities.HiddenResult;
import com.bettypower.entities.PalimpsestMatch;
import com.bettypower.unpacker.Unpacker;
import com.bettypower.unpacker.goalServeUnpacker;
import com.bettypower.util.HashMatchMap;

import java.text.ParseException;
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
    private String yesterdayDate;
    private String dateFromGive;
    private boolean isTimeUnderThree;
    private Map<String, String> allHashMatch = hashMatchMap.getHashMatchMap();


    public RefreshResultThread(String response, ArrayList<PalimpsestMatch> allSavedMatch, LoadingListener loadingListener){
        this.response = response;
        this.allSavedMatch = allSavedMatch;
        this.loadingListener = loadingListener;
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM", Locale.getDefault());
        cal.add(Calendar.DATE, -1);
        this.yesterdayDate = sdf.format(cal.getTime());
        this.dateFromGive = giveDate();
        this.isTimeUnderThree = isTimeUnderThree();
    }

    public RefreshResultThread(){

    }

    @Override
    public void run() {
        long normalInit = System.currentTimeMillis();
        Unpacker unpacker = new goalServeUnpacker(response);
        ArrayList<PalimpsestMatch> allMatchOnGoalServe = unpacker.getAllMatches();
        long normalFin = System.currentTimeMillis();
        long timeNormal = normalFin - normalInit;
        Log.i("time normale",String.valueOf(timeNormal));

        long intellijInit = System.currentTimeMillis();
        String responseMaiusc = response.toUpperCase();
        int i = responseMaiusc.lastIndexOf("FIORENTINA");
        long intellijFin = System.currentTimeMillis();
        long totalTime = intellijFin - intellijInit;
        Log.i("time intellij",String.valueOf(totalTime) + " i = " + String.valueOf(i));

        long initCycle = System.currentTimeMillis();

        for (PalimpsestMatch savedMatch:allSavedMatch
             ) {
            for (PalimpsestMatch goalServeMatch: allMatchOnGoalServe
                 ) {
                //String x = "";
                if(isTheSameMatch(savedMatch,goalServeMatch)){
                    String homeResult = goalServeMatch.getHomeResult().replace("?","-");
                    String awayResult = goalServeMatch.getAwayResult().replace("?","-");
                    if(!homeResult.equals("-") && !awayResult.equals("-") || goalServeMatch.getResultTime().equalsIgnoreCase("posticipata")) {
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

        long finCycle = System.currentTimeMillis();
        long totalCycle = finCycle-initCycle;
        Log.i("ciclo:" ,String.valueOf(totalCycle));

        loadingListener.onFinishLoading(allSavedMatch);
    }

    /*
     * @param betPalimpsest palimpsest from current bet
     * @param goalServePalimpsest palimpsest from goal serve thread
     * @return
     */
    private boolean isTheSameMatch(PalimpsestMatch betPalimpsest,PalimpsestMatch goalServePalimpsest){

        //TODO da testare ma secondo me funziona lo stesso se confronti sia con oggi che con ieri
        if(isTimeUnderThree){
            if(betPalimpsest.getDate().equalsIgnoreCase(dateFromGive) || betPalimpsest.getDate().equalsIgnoreCase(yesterdayDate)){
                return areMatchCompatible(betPalimpsest,goalServePalimpsest);
            }
        }
        else {
            if (betPalimpsest.getDate().equalsIgnoreCase(dateFromGive)) {
                return areMatchCompatible(betPalimpsest,goalServePalimpsest);
            }
        }
        return false;
    }

    private boolean areMatchCompatible(PalimpsestMatch betPalimpsest,PalimpsestMatch goalServePalimpsest){
        String homeTeamOne = betPalimpsest.getHomeTeam().getName().replace("-", " ").toUpperCase();
        String homeTeamTwo = goalServePalimpsest.getHomeTeam().getName().replace("-", " ").toUpperCase();
        String awayTeamOne = betPalimpsest.getAwayTeam().getName().replace("-", " ").toUpperCase();
        String awayTeamTwo = goalServePalimpsest.getAwayTeam().getName().replace("-", " ").toUpperCase();
        if (allHashMatch.containsKey(homeTeamOne) && allHashMatch.get(homeTeamOne).equals(homeTeamTwo) &&
                allHashMatch.containsKey(homeTeamOne) && allHashMatch.get(homeTeamOne).equals(homeTeamTwo)) {
            return true;
        } else if (allHashMatch.containsKey(homeTeamOne) && allHashMatch.get(homeTeamOne).equals(homeTeamTwo)) {
            return true;
        } else if (allHashMatch.containsKey(awayTeamOne) && allHashMatch.get(awayTeamOne).equals(awayTeamTwo)) {
            return true;
        } else {
            int commonHomeName = getNumberOfWordsInCommon(homeTeamOne, homeTeamTwo);
            int commonAwayName = getNumberOfWordsInCommon(awayTeamOne, awayTeamTwo);
            if (homeTeamOne.equalsIgnoreCase(homeTeamTwo) ||
                    awayTeamOne.equalsIgnoreCase(awayTeamTwo)) {
                return true;
            } else if (commonHomeName > 0 || commonAwayName > 0) {
                return commonHomeName + commonAwayName >= 2;
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

    public boolean isTimeUnderThree(){
        String One = "00:00"; //one is current date
        String Two = "04:00";
        java.util.Date now = Calendar.getInstance().getTime();
        String thisHour = String.valueOf(now.getHours());
        String thisminute = String.valueOf(now.getMinutes());
        String thisTime = thisHour+":"+thisminute;
        SimpleDateFormat formatter1=new SimpleDateFormat("HH:mm",Locale.getDefault());
        java.util.Date dateOne = null;
        java.util.Date dateTwo = null;
        java.util.Date dateNow = null;
        try {
            dateOne = formatter1.parse(One);
            dateTwo = formatter1.parse(Two);
            dateNow = formatter1.parse(thisTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        boolean resultdate = false;
        if (dateNow != null && dateNow.after(dateOne) && dateTwo.after(dateNow)) {
            resultdate = true;
        }
        return resultdate;
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
