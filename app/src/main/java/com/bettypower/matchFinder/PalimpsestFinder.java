package com.bettypower.matchFinder;

import android.util.Log;

import com.bettypower.entities.PalimpsestMatch;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by giuliopettenuzzo on 14/08/17.
 */

public class PalimpsestFinder {

    private ArrayList<PalimpsestMatch> allPalimpsestMatch;
    private ArrayList<String> palimpsestJoined = new ArrayList<>();
    private String allText;

    public PalimpsestFinder(ArrayList<PalimpsestMatch> allPalimpsestMatch, String allText){
        this.allPalimpsestMatch = allPalimpsestMatch;
        this.allText = allText;
        Log.i("TESTO PER PALINSESTI",allText);
    }

    /**
     * the main method, it is able to recognize a palimpsest and the likelyHood about it's correctness
     * @return
     */
    private ArrayList<LikelyHoodMatch> findMatch(){
        ArrayList<LikelyHoodMatch> allPalimpsestFound = new ArrayList<>();
        StringTokenizer token = new StringTokenizer(allText);
        while(token.hasMoreTokens()){
            String word = token.nextToken();
            word = fixPalimpsest(word);
            if(word.length()>2) {
                try {
                    long toCompare = Long.parseLong(word);
                    //sono sicuro che è un numero, maggiore di due e il palinsesto è fixxato
                    ArrayList<LikelyHoodMatch> temp = checkMatchPalimpsest(word);
                    for (LikelyHoodMatch currentTmp:temp
                         ) {
                        allPalimpsestFound.add(currentTmp);
                    }
                } catch (Exception e) {

                }
            }
        }
        for (String current:palimpsestJoined
             ) {
            ArrayList<LikelyHoodMatch> finalLikelyHoodMatchNotSafe = new ArrayList<>();
            current = fixPalimpsest(current);
            for (PalimpsestMatch currentPalimpsestMatch:allPalimpsestMatch
                 ) {
                int likelyHood = getOrderLikelyHood(currentPalimpsestMatch.getCompletePalimpsest(),current);
                if(likelyHood>0 && current.length()>4){
                    LikelyHoodMatch likelyHoodMatch = new RealLikelyHoodMatch(currentPalimpsestMatch,likelyHood);
                    finalLikelyHoodMatchNotSafe.add(likelyHoodMatch);
                }
            }
            if(finalLikelyHoodMatchNotSafe.size() == 1){
                finalLikelyHoodMatchNotSafe.get(0).setLikelyHood(finalLikelyHoodMatchNotSafe.get(0).getPalimpsestMatch().getCompletePalimpsest().length());
                allPalimpsestFound.add(finalLikelyHoodMatchNotSafe.get(0));
            }
        }
        return allPalimpsestFound;
    }


    /**
     * ritorna la lista di match che hanno i palinsesti che corrispondono per piu di 4 cratteri con quello passato come parametro
     * @param pali
     * @return
     */
    private ArrayList<LikelyHoodMatch> checkMatchPalimpsest(String pali){
        String currentPalimpsest = "";
        ArrayList<LikelyHoodMatch> allLikelyHoodMatch = new ArrayList<>();
        for (PalimpsestMatch currentPalimpsestMatch:allPalimpsestMatch
                ) {
            currentPalimpsest = currentPalimpsestMatch.getPalimpsest()+currentPalimpsestMatch.getEventNumber();
            int likelyHood = getLikelyHood(currentPalimpsest,pali);
            if(likelyHood > currentPalimpsest.length()-2){
                LikelyHoodMatch likelyHoodMatch = new RealLikelyHoodMatch(currentPalimpsestMatch,likelyHood);
                allLikelyHoodMatch.add(likelyHoodMatch);
            }

        }
        //la parte qui sotto ricava solo i match con probabilità piu alta, se il match è unico do per scontato che sia quello giusto
        //per ora funziona sempre al meno che l'ocr non legga piu numeri del dovuto
        ArrayList<LikelyHoodMatch> finalLikelyHoodMatch = new ArrayList<>();
        for (LikelyHoodMatch currentLikelyMatch : allLikelyHoodMatch
                    ) {
                if (currentLikelyMatch.getLikelyHood() == pali.length()) {
                    finalLikelyHoodMatch.add(currentLikelyMatch);
                }
        }

        if(finalLikelyHoodMatch.size()==1){
            finalLikelyHoodMatch.get(0).setLikelyHood(finalLikelyHoodMatch.get(0).getPalimpsestMatch().getCompletePalimpsest().length());
        }
        return finalLikelyHoodMatch;
    }

    /**
     * try to match the palimpsest from right to left in order to find the most likely
     * @param matchPalimpsest
     * @param word
     * @return
     */
    private int getLikelyHood(String matchPalimpsest, String word){
        if(word.length()>matchPalimpsest.length()){
            return 0;
        }
        int likelyHood = 0; //in order to match the palimpsest from left to right
        int lastIndex = -1;
        for(int i=0; i < word.length(); i++){
            if(matchPalimpsest.contains(String.valueOf(word.charAt(i)))){
                char[] myNameChars = matchPalimpsest.toCharArray();
                int currentIndex = matchPalimpsest.indexOf(word.charAt(i));
                if(currentIndex>lastIndex) {
                    likelyHood++;
                    lastIndex = currentIndex;
                    myNameChars[currentIndex] = 'e';
                    matchPalimpsest = String.valueOf(myNameChars);
                }else{
                    break;
                }
            }
        }
        return likelyHood;
    }

    /**
     * @param matchPalimpsest
     * @param word
     * @return the likelyhood based of the number of right match
     */
    private int getOrderLikelyHood(String matchPalimpsest, String word){
        if(word.length()>matchPalimpsest.length()){
            return 0;
        }
        int likelyHood = 0;
        for(int i = 1; i<=word.length(); i++){
            if(matchPalimpsest.charAt(matchPalimpsest.length() - i) == word.charAt(word.length()-i)){
                likelyHood++;
            }
            else{
                return 0;
            }
        }
        return likelyHood;
    }

    /**
     * created in order to remove the ". - / _ \ | ," from the palimpsest code
     * @param palimpsest
     * @return the palimpsest without ". - / _ \ | ,"
     */
    private String fixPalimpsest(String palimpsest){
        String fixedPalimpsest = "";
        String safePalimpsest = palimpsest;
        if(isDate(palimpsest)){
            return palimpsest;
        }
        try
        {
            if(palimpsest.contains(".")&&palimpsest.length()>1){
                if(palimpsest.indexOf(".")==palimpsest.lastIndexOf(".")){
                    fixedPalimpsest = palimpsest.replace(".","");
                    palimpsest = fixedPalimpsest;
                }
            }
            if(palimpsest.contains("-")&&palimpsest.length()>1){
                if(palimpsest.indexOf("-")==palimpsest.lastIndexOf("-")){
                    fixedPalimpsest = palimpsest.replace("-","");
                    palimpsest = fixedPalimpsest;
                }
            }
            if(palimpsest.contains("\\")&&palimpsest.length()>1){
                if(palimpsest.indexOf("\\")==palimpsest.lastIndexOf("\\")){
                    fixedPalimpsest = palimpsest.replace("\\","");
                    palimpsest = fixedPalimpsest;
                }
            }
            if(palimpsest.contains("|")&&palimpsest.length()>1){
                if(palimpsest.indexOf("|")==palimpsest.lastIndexOf("|")){
                    fixedPalimpsest = palimpsest.replace("|","");
                    palimpsest = fixedPalimpsest;
                }
            }
            if(palimpsest.contains("/")&&palimpsest.length()>1){
                if(palimpsest.indexOf("/")==palimpsest.lastIndexOf("/")){
                    fixedPalimpsest = palimpsest.replace("/","");
                    palimpsest = fixedPalimpsest;
                }
            }
            if(palimpsest.contains("'")&&palimpsest.length()>1){
                if(palimpsest.indexOf("'")==palimpsest.lastIndexOf("'")){
                    fixedPalimpsest = palimpsest.replace("'","");
                    palimpsest = fixedPalimpsest;
                }
            }
            if(palimpsest.contains("_")&&palimpsest.length()>1){
                if(palimpsest.indexOf("_")==palimpsest.lastIndexOf("_")){
                    fixedPalimpsest = palimpsest.replace("_","");
                    palimpsest = fixedPalimpsest;
                }
            }
            if(palimpsest.contains(",")&&palimpsest.length()>1){
                if(palimpsest.indexOf(",")==palimpsest.lastIndexOf(",")){
                    fixedPalimpsest = palimpsest.replace(",","");
                    palimpsest = fixedPalimpsest;
                }
            }
            if(palimpsest.contains("~")&&palimpsest.length()>1){
                if(palimpsest.indexOf("~")==palimpsest.lastIndexOf("~")){
                    fixedPalimpsest = palimpsest.replace("~","");
                    palimpsest = fixedPalimpsest;
                }
            }
            if(palimpsest.equalsIgnoreCase("2017")||palimpsest.equalsIgnoreCase("2018")){
                return "";
            }

            if(palimpsest.endsWith(",")){
                fixedPalimpsest = palimpsest.replace(",","");
                palimpsest = fixedPalimpsest;
            }

            if(palimpsest.endsWith(".")){
                fixedPalimpsest = palimpsest.replace(".","");
                palimpsest = fixedPalimpsest;
            }

            long intPalimpsest = Long.parseLong(fixedPalimpsest);
            if(intPalimpsest>10000){
                return fixedPalimpsest;
            }
        }
        //if the string is not a number return the original string
        catch(NumberFormatException nfe)
        {
            return safePalimpsest;
        }
        return safePalimpsest;
    }

    /**
     * @param word
     * @return true if @word is a date, false otherwise
     */
    private boolean isDate(String word){
        char[] charArray = word.toCharArray();
        try {
            if (charArray[2] == '/' && charArray[5] == '/') {
                return true;
            } else {
                return false;
            }
        }
        catch (Exception e){
            return false;
        }
    }

    /***********************************************************************************************
     *********************************   PUBLIC METHODS   ******************************************
     **********************************************************************************************/

    /**
     * set the palimpsest witch where found separated by a space
     * @param palimpsestJoined
     */
    public void setPalimpsestJoined(ArrayList<String> palimpsestJoined){
        this.palimpsestJoined = palimpsestJoined;
    }

    /**
    public ArrayList<LikelyHoodMatch> getAllLikelyHoodMatch(){
        ArrayList<LikelyHoodMatch> allLikelyHoodMatch = findMatch();
        for (LikelyHoodMatch current:allLikelyHoodMatch
                ) {
            int pali = current.getPalimpsestMatch().getCompletePalimpsest().length();
            if(current.getLikelyHood()==pali) {
                Log.i("BYPALIMPSEST ", current.getPalimpsestMatch().getHomeTeam().getName() + " - " + current.getPalimpsestMatch().getAwayTeam().getName());
            }
        }
        return allLikelyHoodMatch;
    }
*/

    public ArrayList<LikelyHoodMatch> getAllLikelyHoodMatch(){
        ArrayList<LikelyHoodMatch> result = new ArrayList<>();
        StringTokenizer token = new StringTokenizer(allText);
        while(token.hasMoreTokens()){
            String word = token.nextToken();
            word = fixPalimpsest(word);
                for (PalimpsestMatch currentPalimpsest:allPalimpsestMatch
                 ) {
                    if(currentPalimpsest.getCompletePalimpsest().equals(word)){
                        LikelyHoodMatch safeLikely = new RealLikelyHoodMatch(currentPalimpsest);
                        if(notInList(result,safeLikely)){
                            result.add(safeLikely);
                        }
                    }
                    for (String currentString:palimpsestJoined
                         ) {
                        if(currentPalimpsest.getCompletePalimpsest().equals(currentString)){
                            LikelyHoodMatch safeLikely = new RealLikelyHoodMatch(currentPalimpsest);
                            if(notInList(result,safeLikely)){
                                result.add(safeLikely);
                            }
                        }
                    }
                }
            }
        return result;
    }

    private boolean notInList(ArrayList<LikelyHoodMatch> allLikelyMatch,LikelyHoodMatch likelyHoodMatch){
        for (LikelyHoodMatch current:allLikelyMatch
             ) {
            if(current.getPalimpsestMatch().getCompletePalimpsest().equals(likelyHoodMatch.getPalimpsestMatch().getCompletePalimpsest())){
                return false;
            }
        }
        Log.i("BYPALIMPSEST ", likelyHoodMatch.getPalimpsestMatch().getHomeTeam().getName() + " - " + likelyHoodMatch.getPalimpsestMatch().getAwayTeam().getName());
        return true;
    }
}
