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
    String yesterdayDate;
    String dateFromGive;
    boolean isTimeUnderThree;
    Map<String, String> allHashMatch = hashMatchMap.getHashMatchMap();


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


        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM", Locale.getDefault());
        cal.add(Calendar.DATE, -1);
        String yesterdyDate = sdf.format(cal.getTime());
        String dateFromGive = giveDate();

        long initCycle = System.currentTimeMillis();

        for (PalimpsestMatch savedMatch:allSavedMatch
             ) {
            for (PalimpsestMatch goalServeMatch: allMatchOnGoalServe
                 ) {
                //String x = "";
                //TODO è questo il metodo che butta via na vita di tempo
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

        long finCycle = System.currentTimeMillis();
        long totalCycle = finCycle-initCycle;
        Log.i("ciclo:" ,String.valueOf(totalCycle));

        loadingListener.onFinishLoading(allSavedMatch);
    }

    /*
     * @param paliOne palimpsest from current bet
     * @param paliTwo palimpsest from goal serve thread
     * @return
     */
    private boolean isTheSameMatch(PalimpsestMatch paliOne,PalimpsestMatch paliTwo){

        if(isTimeUnderThree){
            if(paliOne.getDate().equalsIgnoreCase(dateFromGive) || paliOne.getDate().equalsIgnoreCase(yesterdayDate)){
                return areMatchCompatible(paliOne,paliTwo);
            }
        }
        else {
            if (paliOne.getDate().equalsIgnoreCase(dateFromGive)) {
                return areMatchCompatible(paliOne,paliTwo);
            }
        }
        return false;
    }

    private boolean areMatchCompatible(PalimpsestMatch paliOne,PalimpsestMatch paliTwo){
        String homeTeamOne = paliOne.getHomeTeam().getName().replace("-", " ").toUpperCase();
        String homeTeamTwo = paliTwo.getHomeTeam().getName().replace("-", " ").toUpperCase();
        String awayTeamOne = paliOne.getAwayTeam().getName().replace("-", " ").toUpperCase();
        String awayTeamTwo = paliTwo.getAwayTeam().getName().replace("-", " ").toUpperCase();
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
                if (commonHomeName + commonAwayName >= 2) {
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

    private boolean isTimeUnderThree(){
        String One = "01:00"; //one is current date
        String Two = "03:00";
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
        if(dateNow.after(dateOne) && dateTwo.after(dateNow)){
            resultdate = true;
        }
        return resultdate;
    }

    private String giveDate() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdfh = new SimpleDateFormat("HH:mm",Locale.getDefault());
        String getCurrentTime = sdfh.format(c.getTime());

        String midnight = "00:00"; //TODO se la partita comincia a mezzanotte non funziona nulla fino all'una
        //questo è dovuto al fatto che molte volte a mezzanotte segnano che le partite sono il giorno prima stronzi dimmmerda
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
