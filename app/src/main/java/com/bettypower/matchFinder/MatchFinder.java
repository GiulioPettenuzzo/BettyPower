package com.bettypower.matchFinder;

import android.text.Html;
import android.util.Log;

import com.bettypower.entities.Match;
import com.bettypower.entities.PalimpsestMatch;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by giuliopettenuzzo on 14/08/17.
 */

//TODO SERVE UN MODO PER RICONOSCERE ANCHE LE PARTITE CHE SONO GIA STATE GIOCATE

public class MatchFinder {

    private ArrayList<PalimpsestMatch> allPalimpsestMatch;
    private ArrayList<String> palimpsestJoined = new ArrayList<>();
    private ArrayList<LikelyHoodMatch> foundByName = new ArrayList<>();
    private ArrayList<LikelyHoodMatch> foundByPalimpsest = new ArrayList<>();
    private ArrayList<LikelyHoodMatch> unsafeMatches;
    private ArrayList<PalimpsestMatch> allMatches = new ArrayList<>();
    private String allText;


    private boolean isThreadFinish = false;
    private PalimpsestThreadFinishListener threadPaliFinishListener;
    private NamesThreadFinishListener threadNameFinishListener;

    public MatchFinder(ArrayList<PalimpsestMatch> allPalimpsestMatch, String allText){
        this.allPalimpsestMatch = allPalimpsestMatch;
        String text = allText;

        this.allText = cleanText(allText);
        Log.i("TESTO cleanhtml " ,this.allText );
        text = cleanHtmlText(text);
        Log.i("TESTO cleaned normale " ,text );

    }

    /**
     * this method starts all threads and recive the result
     * @return
     */
    private ArrayList<PalimpsestMatch> getMatches(){
        PalimpsestThread palimpsestThread = new PalimpsestThread();
        palimpsestThread.start();
        final NamesThread namesThread = new NamesThread();
        namesThread.start();

        palimpsestThread.setPalimpsestThreadFinishListener(new PalimpsestThreadFinishListener() {
            @Override
            public void onPalimpsestThreadFinish(ArrayList<LikelyHoodMatch> allPalimpsestFound) {
                foundByPalimpsest = allPalimpsestFound;
                if(isThreadFinish){
                    allMatches = elaborateResult(foundByName,foundByPalimpsest);
                }
                isThreadFinish = true;
            }
        });

        namesThread.setNameThreadFinishListener(new NamesThreadFinishListener() {
            @Override
            public void onNamesThreadFinishListener(ArrayList<LikelyHoodMatch> allNamesFound) {
                foundByName = allNamesFound;
                if(isThreadFinish){
                    allMatches = elaborateResult(foundByName,foundByPalimpsest);
                }
                isThreadFinish = true;
            }
        });

        try {
            palimpsestThread.join();
            namesThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return allMatches;
    }

    /**
     * this is the method that elaborate the result
     * here you can modify the parameters to match the palimpsest and the names
     * @param matchByName
     * @param matchByPalimpsest
     * @return
     */
    private ArrayList<PalimpsestMatch> elaborateResult(ArrayList<LikelyHoodMatch> matchByName, ArrayList<LikelyHoodMatch> matchByPalimpsest){
        ArrayList<LikelyHoodMatch> tempSafeMatches = new ArrayList<>();
        ArrayList<LikelyHoodMatch> safeMatch = new ArrayList<>();
        String completeNamesString = "";
        for (LikelyHoodMatch currentMatch:matchByPalimpsest
                ) {
            String palimpsestLength = currentMatch.getPalimpsestMatch().getPalimpsest() + currentMatch.getPalimpsestMatch().getEventNumber();
            if (currentMatch.getLikelyHood() == palimpsestLength.length()) {
                if (!isDuplicate(tempSafeMatches, currentMatch))
                    tempSafeMatches.add(currentMatch);
                    completeNamesString = completeNamesString + " " + currentMatch.getPalimpsestMatch().getHomeTeam().getName() + " " + currentMatch.getPalimpsestMatch().getAwayTeam().getName();
            }
        }

        for (LikelyHoodMatch currentMatch:matchByName
             ) {
            if(currentMatch.getPalimpsestMatch().getHomeTeam().getName().equalsIgnoreCase("ATHLETIC BILBAO") &&
                    currentMatch.getPalimpsestMatch().getAwayTeam().getName().contains("SIVIGLIA")){
                String x = "";
            }
            if(currentMatch.getLikelyHood()>0){
                if(isDuplicate(tempSafeMatches,currentMatch)){
                    if(!isDuplicate(safeMatch,currentMatch)) {
                        safeMatch.add(currentMatch);
                        completeNamesString = completeNamesString + " " + currentMatch.getPalimpsestMatch().getHomeTeam().getName() + " " + currentMatch.getPalimpsestMatch().getAwayTeam().getName();
                        Log.i("NORMAL THREAD UNION", currentMatch.getPalimpsestMatch().getHomeTeam().getName() + " " + currentMatch.getPalimpsestMatch().getAwayTeam().getName());
                    }
                }
                //if the likelyhood is high the match is considered safe
                if(currentMatch.getLikelyHood()>2){
                    if(!isDuplicate(safeMatch,currentMatch) && !checkAmbiguos(completeNamesString,currentMatch.getPalimpsestMatch())){
                        safeMatch.add(currentMatch);
                        completeNamesString = completeNamesString + " " + currentMatch.getPalimpsestMatch().getHomeTeam().getName() + " " + currentMatch.getPalimpsestMatch().getAwayTeam().getName();
                    }
                }
            }
        }
        //just in case the bet ticket doesn't contains any palimpsest
        if(foundByPalimpsest == null ||foundByPalimpsest.size()==0){
            safeMatch = matchByName;
        }

        ArrayList<PalimpsestMatch> result = likelyToPalimpsest(safeMatch);
        return result;
    }

    /**
     * never used but this is the way to clean the html code recived from renard ocr
     * @param allText
     * @return
     */
    private String cleanHtmlText(String allText){
        String result = Html.fromHtml(allText).toString();
        return result;
    }

    private String cleanText(String allText){
        String textCleaned = "";
        StringTokenizer token = new StringTokenizer(allText," <>");
        boolean lastWordIsNumber = false;
        while(token.hasMoreTokens()){
            String word = token.nextToken();
            word = word.toUpperCase();
            String cleaned = word.replaceAll("[^A-Za-z0-9\\s]", "");
            if(word.equalsIgnoreCase("totale")){
                break;
            }
            try{
                long moment = Long.parseLong(cleaned);
                if(word.contains("#")&&word.length()>4){
                    word = word.replace("#","");
                }
                if(word.contains("&")&&word.length()>4){
                    word = word.replace("&","");

                }
                if(word.contains(";")&&word.length()>4){
                    word = word.replace(";","");
                }
                if(lastWordIsNumber && !word.contains(":")){
                    textCleaned = textCleaned + word;
                    if(word.length()>3) {
                        palimpsestJoined.add(word);
                    }
                }else {
                    textCleaned = textCleaned + " " + word;
                    lastWordIsNumber = true;
                    palimpsestJoined.add(word);
                }
            }
            catch (Exception e){
                cleaned = word.replaceAll("[^A-Za-z0-9\\s]", " ");
                textCleaned = textCleaned + " " + cleaned;
                lastWordIsNumber = false;
            }
        }
        textCleaned = textCleaned.replace("COLOR","");
        textCleaned = textCleaned.replace("FONT","");
        textCleaned = textCleaned.replace("CONF","");
        textCleaned = textCleaned.replace("STRONG","");
        textCleaned = textCleaned.replace("DE2222","");
        return textCleaned;
    }

    /***********************************************************************************************
     *********************************   AUSILIAR METHODS   ****************************************
     **********************************************************************************************/

    /**
     *
     * @param allTeamInString
     * @param palimpsestMatch
     * @return false if there isn't similar teams in the matches already inserted
     */
    private boolean checkAmbiguos(String allTeamInString, PalimpsestMatch palimpsestMatch){
        if(allTeamInString.contains(palimpsestMatch.getHomeTeam().getName()) || allTeamInString.contains(palimpsestMatch.getAwayTeam().getName())){
            return true;
        }
        return false;
    }

    private boolean isDuplicate(ArrayList<LikelyHoodMatch> allMatches,LikelyHoodMatch match){
        for (LikelyHoodMatch currentMatch:allMatches
             ) {
            if(currentMatch.compareTo(match.getPalimpsestMatch())){
                return true;
            }
            if(currentMatch.compareToByName(match.getPalimpsestMatch())){
                return true;
            }
        }
        return false;
    }

    private ArrayList<PalimpsestMatch> likelyToPalimpsest(ArrayList<LikelyHoodMatch> likelyHoodMatch){
        ArrayList<PalimpsestMatch> result = new ArrayList<>();
        for (LikelyHoodMatch currentLikelyHood:likelyHoodMatch
                ) {
            PalimpsestMatch pali = currentLikelyHood.getPalimpsestMatch();
            result.add(pali);
        }
        return result;
    }

    /***********************************************************************************************
     *********************************   PUBLIC METHODS   ******************************************
     **********************************************************************************************/

    /**
     * @return all match found by name
     */
    public ArrayList<PalimpsestMatch> getMatchFoundByName(){
        return likelyToPalimpsest(foundByName);
    }

    /**
     * @return all matche found by palimpsest
     */
    public ArrayList<PalimpsestMatch> getMatchFoundByPalimpsest(){
        return likelyToPalimpsest(foundByPalimpsest);
    }

    /**
     * @return all match considered unsafe, this list will be match with the list of real time unsafe
     * match
     */
    public ArrayList<LikelyHoodMatch> getAllUnsafeMatch(){
        return unsafeMatches;
    }

    /**
     * @return all match considered safe
     */
    public ArrayList<PalimpsestMatch> getAllMatchFound(){
        return getMatches();
    }

    /***********************************************************************************************
     *************************   NAMES AND PALIMPSEST THREAD   *************************************
     **********************************************************************************************/


    private class PalimpsestThread extends Thread{

        /**
         * When an object implementing interface <code>Runnable</code> is used
         * to create a thread, starting the thread causes the object's
         * <code>run</code> method to be called in that separately executing
         * thread.
         * <p>
         * The general contract of the method <code>run</code> is that it may
         * take any action whatsoever.
         *
         * @see Thread#run()
         */
        @Override
        public void run() {
            ArrayList<LikelyHoodMatch> foundByPalimpsest = new ArrayList<>();
            PalimpsestFinder palimpsestFinder = new PalimpsestFinder(allPalimpsestMatch,allText);
            palimpsestFinder.setPalimpsestJoined(palimpsestJoined);
            foundByPalimpsest = palimpsestFinder.getAllLikelyHoodMatch();
            threadPaliFinishListener.onPalimpsestThreadFinish(foundByPalimpsest);
        }

        public void setPalimpsestThreadFinishListener(PalimpsestThreadFinishListener palimpsestThreadFinishListener){
            threadPaliFinishListener = palimpsestThreadFinishListener;
        }

    }

    private class NamesThread extends Thread{

        /**
         * When an object implementing interface <code>Runnable</code> is used
         * to create a thread, starting the thread causes the object's
         * <code>run</code> method to be called in that separately executing
         * thread.
         * <p>
         * The general contract of the method <code>run</code> is that it may
         * take any action whatsoever.
         *
         * @see Thread#run()
         */
        @Override
        public void run() {
            ArrayList<LikelyHoodMatch> foundByName = new ArrayList<>();
            NamesFinder namesFinder = new NamesFinder(allPalimpsestMatch,allText);
            foundByName = namesFinder.getAllLikelyHoodMatch();
            threadNameFinishListener.onNamesThreadFinishListener(foundByName);
            unsafeMatches = namesFinder.getUnsafeMatches();
        }

        public void setNameThreadFinishListener(NamesThreadFinishListener namesThreadFinishListener){
            threadNameFinishListener = namesThreadFinishListener;
        }
    }

    public interface PalimpsestThreadFinishListener{
        void onPalimpsestThreadFinish(ArrayList<LikelyHoodMatch> allPalimpsestFound);
    }

    public interface NamesThreadFinishListener{
        void onNamesThreadFinishListener(ArrayList<LikelyHoodMatch> allNamesFound);
    }

}
