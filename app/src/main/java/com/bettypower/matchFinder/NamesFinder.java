package com.bettypower.matchFinder;

import android.util.Log;

import com.bettypower.entities.PalimpsestMatch;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.StringTokenizer;

/**
 * Created by giuliopettenuzzo on 14/08/17.
 */

//TODO il testo dell'ocr va trasformato tutto in maiuscolo
public class NamesFinder {

    private ArrayList<PalimpsestMatch> allPalimpsestMatch = new ArrayList<>();
    private ArrayList<LikelyHoodMatch> unsafeMatches = new ArrayList<>();
    private ArrayList<String> allText = new ArrayList<>();

    private int distanceToConsider = 3; //the maximum distance that must have the word of the match in the text to be recognize
    private int indexVariable = -1;

    private String[] wordsFound;
    private String[] equalsWord;

    /**
     * CONSTRUCTOR: clear all text in order to be examinated by the ausiliar methods
     * @param allPalimpsestMatch
     * @param text
     */
    public NamesFinder(ArrayList<PalimpsestMatch> allPalimpsestMatch, String text){
        StringTokenizer token = new StringTokenizer(text);
        ArrayList<String> allTeamWords = getAllStringTeamWords(allPalimpsestMatch);
        //TODO senza questo algoritmo il rallentamento passa da 10 secondi a 6 secondi di esecuzione
        DamerauLevenshteinAlgorithm levenshteinAlgorithm = new DamerauLevenshteinAlgorithm(10,10,1,10);
        while(token.hasMoreTokens()){
            String word = token.nextToken();
            if(word.length()>2 && checkWord(word)) {
                ArrayList<String> wordWithSameDistance = new ArrayList<>();
                int min = 100;
                for (String currentWord:allTeamWords
                        ) {
                    if(currentWord.length()>3) {
                        int currentMin = levenshteinAlgorithm.execute(word, currentWord);
                        if (currentMin < min && currentMin <= 3 && isStringDifferentsFor(word,currentWord)) {
                            wordWithSameDistance.clear();
                            wordWithSameDistance.add(currentWord);
                        }
                        if (currentMin == min && currentMin <= 3 && isStringDifferentsFor(word,currentWord)) {
                            wordWithSameDistance.add(currentWord);
                        }
                    }
                }
                if(wordWithSameDistance.size()==1){
                    allText.add(wordWithSameDistance.get(0));
                }
                else {
                    allText.add(word);
                }

            }
        }
        wordsFound = new String[allText.size()];
        equalsWord = new String[allText.size()];
        for (PalimpsestMatch currentPalimpsestMatch:allPalimpsestMatch
                ) {
            if(!isDuplicate(this.allPalimpsestMatch,currentPalimpsestMatch)){
                this.allPalimpsestMatch.add(currentPalimpsestMatch);
            }
        }
        //just for debug
        String res = "";
        for (String words:allText
                ) {
            res = res + " " + words;
        }
        Log.i("TESTO CORRETTO",res);
    }

    /**
     * All the match is found by getLikelyHood method, then only the more likely is selected and all the match
     * is ready for another selection witch block the match that has been found with less than two word.
     * each palimpsest match is compared with all the text detected by the ocr
     * @return
     */
    private ArrayList<LikelyHoodMatch> findMatch(){
        ArrayList<LikelyHoodMatch> allMatchesFound = new ArrayList<>();
        for (PalimpsestMatch currentPalimpsestMatch:allPalimpsestMatch
                ) {
            int likelyHood = getLikelyHood(currentPalimpsestMatch);
            if(likelyHood>=0){
                if(currentPalimpsestMatch!=null) {
                    LikelyHoodMatch likelyHoodMatch = new RealLikelyHoodMatch(currentPalimpsestMatch, likelyHood);
                    if(!isLikelyDuplicate(allMatchesFound,currentPalimpsestMatch)) {
                        allMatchesFound.add(likelyHoodMatch);
                    }
                }
            }
        }
        allMatchesFound = keepOnlyTheBest(allMatchesFound);
        //now the second selection
        allMatchesFound = setLikelyHood(allMatchesFound);
        ArrayList<LikelyHoodMatch> safeMatchs = new ArrayList<>();
        for (LikelyHoodMatch currentLikelyHoodMatch: allMatchesFound
             ) {
            if(currentLikelyHoodMatch.getLikelyHood()>1){
                safeMatchs.add(currentLikelyHoodMatch);
            }
            else{
                unsafeMatches.add(currentLikelyHoodMatch);
            }
        }
        return safeMatchs;
    }

    /**
     * this method provide the first selection, given a palimpsestMatch by param, it returns the likelyHood
     * about the text is speaking about it
     * @param palimpsestMatch
     * @return
     */
    private int getLikelyHood(PalimpsestMatch palimpsestMatch) {
        String homeTeam = removeShortWordsInHomeTeamName(palimpsestMatch);
        String awayTeam = removeShortWordsInAwayTeamName(palimpsestMatch);

        indexVariable = -1;
        int likelyHood = 0;
        String textForDate = "";

        for(int i=0;i<allText.size();i++){
            String home = homeTeam;
            String away = awayTeam;
            boolean partialHomeMatch = false;
            boolean partialAwayMatch = false;
            int firstFoundMatch = -1; //0 = home; 1 = away;
            int cycle = 0;
            while(cycle<distanceToConsider && i<allText.size()) {
                String word = allText.get(i);
                StringTokenizer homeToken =  new StringTokenizer(home);
                boolean jumpAway = false;
                while (homeToken.hasMoreTokens()) {
                    String homeWord = homeToken.nextToken();
                    if (word.equalsIgnoreCase(homeWord) && firstFoundMatch != 1) {
                        partialHomeMatch = true;
                        cycle = 0;
                        textForDate = selectText(allText,i);
                        jumpAway = true;
                        firstFoundMatch = 0;
                        home = home.replace(word,"");
                        if(wordsFound[i] != null){ //word already associated to a team
                            equalsWord[i] = word;
                        }else{
                            wordsFound[i] = word;
                        }
                    }
                }
                StringTokenizer awayToken = new StringTokenizer(away);
                while (awayToken.hasMoreTokens() && !jumpAway) {
                    String awayWord = awayToken.nextToken();
                    if (word.equalsIgnoreCase(awayWord)) {
                        partialAwayMatch = true;
                        cycle = 0;
                        textForDate = selectText(allText,i);
                        if(firstFoundMatch == -1){
                            firstFoundMatch = 1;
                        }
                        if(wordsFound[i] != null){ //word already associated to a team
                            equalsWord[i] = word;
                        }else{
                            wordsFound[i] = word;
                        }
                    }
                }

                if(!partialAwayMatch && !partialHomeMatch){
                    cycle = distanceToConsider-1; //will be distanceToConsider in the next line
                }
                cycle++;
                int likelyHoodToCompare = elaborateLikelyHoodTwo(partialHomeMatch,partialAwayMatch);
                if(cycle == distanceToConsider && likelyHoodToCompare>likelyHood){
                    likelyHood = likelyHoodToCompare;
                    indexVariable = i;
                }
                if(cycle!=0 && cycle!=distanceToConsider) {
                    i++;
                }
            }
        }
        return indexVariable;
    }

    /**
     * this class define the way in witch you can trust the result, this is the second selection
     * each team name is comparing with the words found array in order to know woh many words are matching in a match
     * teams.
     * @param allLikelyHoodMatch
     * @return 0 if no match are found (limit situation)
     *         1 if only one word of home or away tem are found
     *         2 if all home and away match are found
     *         3 if all home and away match are found and the number of match is greater than 2
     */
    //TODO
    private ArrayList<LikelyHoodMatch> setLikelyHood(ArrayList<LikelyHoodMatch> allLikelyHoodMatch){
        ArrayList<LikelyHoodMatch> result = new ArrayList<>();
        for (LikelyHoodMatch currentLikelyHoodMatch:allLikelyHoodMatch
             ) {
            int finalLikelyHood = 0;
            PalimpsestMatch currentPalimpsestMatch = currentLikelyHoodMatch.getPalimpsestMatch();
            String currentPalimpsestMatchNames = currentPalimpsestMatch.getHomeTeam().getName() + " " + currentPalimpsestMatch.getAwayTeam().getName();
            StringTokenizer token = new StringTokenizer(currentPalimpsestMatchNames);
            String[] completeNames = new String[token.countTokens()];
            boolean isHomeTeamWordFound = false;
            boolean isAwayTeamHomeFound = false;
            //put the match teams names inside an array
            int count = 0;
            while(token.hasMoreTokens()){
                completeNames[count] = token.nextToken();
                count++;
            }

            for (int wordsFoundCounter = 0; wordsFoundCounter < wordsFound.length; wordsFoundCounter++) {
                int tempWordsFoundCounter = wordsFoundCounter;
                int cycle = 0;
                int likelyhood = 0;
                for(int matchFoundCounter = 0;matchFoundCounter<completeNames.length;matchFoundCounter++){
                    cycle++;
                    if(wordsFound[tempWordsFoundCounter] != null && wordsFound[tempWordsFoundCounter].equalsIgnoreCase(completeNames[matchFoundCounter])){
                        if(currentPalimpsestMatch.getHomeTeam().getName().contains(completeNames[matchFoundCounter])){
                            isHomeTeamWordFound = true;
                        }
                        else if(currentPalimpsestMatch.getAwayTeam().getName().contains(completeNames[matchFoundCounter])){
                            isAwayTeamHomeFound = true;
                        }
                        tempWordsFoundCounter++;
                        likelyhood++;
                        cycle = 0;
                    }
                    else if((wordsFound[tempWordsFoundCounter] == null || !wordsFound[tempWordsFoundCounter].equalsIgnoreCase(completeNames[matchFoundCounter])) &&
                            likelyhood>0){
                        matchFoundCounter--;
                    }
                    if(likelyhood == 0){
                        cycle = 0;
                    }
                    if(cycle == distanceToConsider || tempWordsFoundCounter == wordsFound.length){
                        break;
                    }
                }
                if(likelyhood > finalLikelyHood){
                    finalLikelyHood = likelyhood;
                }
            }

            //now set the likelyhood in base of the number of match found in the line above

            if(!isHomeTeamWordFound && !isAwayTeamHomeFound){
                finalLikelyHood = 0;
            }
            else if(!isHomeTeamWordFound || !isAwayTeamHomeFound){
                finalLikelyHood = 1;
            }
            else if(isHomeTeamWordFound && isAwayTeamHomeFound && finalLikelyHood>2){
                finalLikelyHood = 3;
            }
            else{
                finalLikelyHood = 2;
            }
            currentLikelyHoodMatch.setLikelyHood(finalLikelyHood);
            result.add(currentLikelyHoodMatch);
        }
        return result;
    }

    /**
     * given a set of palimpsestMatch it returns the most likely
     * @param allMatchFound
     * @return a list because every match with the same likelyhood are returned
     */
    private ArrayList<LikelyHoodMatch> keepOnlyTheBest(ArrayList<LikelyHoodMatch> allMatchFound){
        ArrayList<LikelyHoodMatch> safeMatch = new ArrayList<>();
        ArrayList<LikelyHoodMatch> allDoubleMatches = new ArrayList<>();
        for(int i = 0;i<equalsWord.length;i++) {
            ArrayList<LikelyHoodMatch> tempSafeMatch = new ArrayList<>();
            for (LikelyHoodMatch likelyMatch : allMatchFound
                    ) {
                String homeName = likelyMatch.getPalimpsestMatch().getHomeTeam().getName();
                String awayName = likelyMatch.getPalimpsestMatch().getAwayTeam().getName();

                if(equalsWord[i] != null) {
                    if ((homeName.contains(equalsWord[i]) || awayName.contains(equalsWord[i])) &&
                            i <= likelyMatch.getLikelyHood() && i>= likelyMatch.getLikelyHood()-10) {
                        tempSafeMatch.add(likelyMatch);
                        allDoubleMatches.add(likelyMatch);
                    }
                }
            }
            LikelyHoodMatch winMatch = selectTheBestOne(tempSafeMatch,i);

            if(winMatch!= null && winMatch.getLikelyHood()!=-10){
                safeMatch.add(winMatch);
            }
        }
        for (LikelyHoodMatch current:allDoubleMatches
                ) {
            allMatchFound.remove(current);
        }
        for (LikelyHoodMatch likelyMatch : allMatchFound
                ) {
            safeMatch.add(likelyMatch);
        }
        return safeMatch;
    }

    /**
     * given an array list witch contains all the palimpsest match found by the same word,
     * @param tempSafeMatch
     * @param position
     * @return the best likely match, the other matches are putting into unsafeMatch ArrayList
     */
    private LikelyHoodMatch selectTheBestOne(ArrayList<LikelyHoodMatch> tempSafeMatch, int position){
        int max = 0;
        LikelyHoodMatch maxLikelyMatch = null;
        for (LikelyHoodMatch currentLikelyMatch:tempSafeMatch
                ) {
            int current = 0;
            String word;
            String homeName = currentLikelyMatch.getPalimpsestMatch().getHomeTeam().getName();
            String awayName = currentLikelyMatch.getPalimpsestMatch().getAwayTeam().getName();

            for(int i = -10; i<=10;i++){
                try {
                    word = wordsFound[position + i];
                }
                catch(ArrayIndexOutOfBoundsException e){
                    word = null;
                }
                if(word != null) {
                    if (homeName.contains(word) || awayName.contains(word)) {
                        current++;
                        if(homeName.contains(word)){
                            homeName = homeName.replace(word,"");
                        } else{
                            awayName = awayName.replace(word,"");
                        }
                    }
                }
            }

            if(current>max){
                max = current;
                maxLikelyMatch = currentLikelyMatch;
            }

            else if(current==max && maxLikelyMatch != null){
                String textForDate = selectText(allText,position);
                DateFinder dateFinderMax = new DateFinder(textForDate,maxLikelyMatch.getPalimpsestMatch().getTime());
                DateFinder dateFinderCurrent = new DateFinder(textForDate,currentLikelyMatch.getPalimpsestMatch().getTime());
                if(dateFinderMax.getLikelyHoodDate()<dateFinderCurrent.getLikelyHoodDate()){
                    maxLikelyMatch = currentLikelyMatch;
                }
                else if(dateFinderMax.getLikelyHoodDate()==dateFinderCurrent.getLikelyHoodDate()){
                    maxLikelyMatch.setLikelyHood(-10); //indica che sono passato di qua
                    if(unsafeMatches.lastIndexOf(maxLikelyMatch)>=0){
                        unsafeMatches.add(maxLikelyMatch);
                    }
                    unsafeMatches.add(currentLikelyMatch);
                }
            }
        }
        return maxLikelyMatch;
    }


    private int elaborateLikelyHoodTwo(boolean partialHome, boolean partialAway) {
        int likelyHood = 0;

        if(partialHome || partialAway){
            likelyHood = 1;
        }

        if(partialHome && partialAway){
            likelyHood = 2;
        }
        return likelyHood;
    }





    /***********************************************************************************************
     *********************************   AUSILIAR METHODS   ****************************************
     **********************************************************************************************/


    /**
     * @return an array list containing all the String that appear in all the team words without repetitions
     */
    private ArrayList<String> getAllStringTeamWords(ArrayList<PalimpsestMatch> allPalimpsestMatch){
        ArrayList<String> allString = new ArrayList<>();

        for (PalimpsestMatch currentMatch:allPalimpsestMatch
                ) {
            String homeName = currentMatch.getHomeTeam().getName();
            String awayTeam = currentMatch.getAwayTeam().getName();
            String completeName = homeName + awayTeam;
            StringTokenizer token = new StringTokenizer(completeName);
            while(token.hasMoreTokens()){
                String word = token.nextToken();
                if(word.length()>2 && allString.lastIndexOf(word)<0){
                    allString.add(word);
                }
            }
        }

        return allString;
    }

    /**
     * the short word cases a lot of error,
     * this method removes short word (word.length > 2) from the home team name
     * @param palimpsestMatch
     * @return
     */
    private String removeShortWordsInHomeTeamName(PalimpsestMatch palimpsestMatch){
        String homeTeam = palimpsestMatch.getHomeTeam().getName();
        StringTokenizer homeToken = new StringTokenizer(homeTeam);
        homeTeam = "";
        while(homeToken.hasMoreTokens()){
            String word = homeToken.nextToken(" -");
            if(word.length()>2 && !word.equalsIgnoreCase("calcio") && !word.equalsIgnoreCase("liga")){
                homeTeam = homeTeam + " " + word;
            }
        }
        return homeTeam;
    }

    /**
     * the short word cases a lot of error,
     * this method removes short word (word.length > 2) from the away team name
     * @param palimpsestMatch
     * @return
     */
    private String removeShortWordsInAwayTeamName(PalimpsestMatch palimpsestMatch){
        String awayTeam = palimpsestMatch.getAwayTeam().getName();
        StringTokenizer awayToken = new StringTokenizer(awayTeam);
        awayTeam = "";
        while(awayToken.hasMoreTokens()){
            String word = awayToken.nextToken(" -");
            if(word.length()>2 && !word.equalsIgnoreCase("calcio") && !word.equalsIgnoreCase("liga")){
                awayTeam = awayTeam + " " + word;
            }
        }
        return awayTeam;
    }

    /**
     * used for PalimpsestMatch ArrayList
     * @param allMatchFound
     * @param palimpsestMatch
     * @return true if allMatchFound contains palimpsestMatch, false otherwise
     */
    private boolean isDuplicate(ArrayList<PalimpsestMatch> allMatchFound,PalimpsestMatch palimpsestMatch){
        for (PalimpsestMatch currentPalimpsestMatch:allMatchFound
                ) {
            if(palimpsestMatch.compareToByName(currentPalimpsestMatch)){
                return true;
            }
        }
        return false;
    }

    /**
     * used for LikelyHoodMatch ArrayList
     * @param allMatchFound
     * @param palimpsestMatch
     * @return true if allMatchFound contains palimpsestMatch, false otherwise
     */
    private boolean isLikelyDuplicate(ArrayList<LikelyHoodMatch> allMatchFound,PalimpsestMatch palimpsestMatch){
        for (LikelyHoodMatch currentPalimpsestMatch:allMatchFound
                ) {
            if(palimpsestMatch.compareToByName(currentPalimpsestMatch.getPalimpsestMatch())){
                return true;
            }
        }
        return false;
    }

    /**
     * a simple calculation of the difference of two word given by parameters
     * @param stringOne
     * @param stringTwo
     * @return true if the difference between the two word is only 'i', 'l' or a number.
     */
    private boolean isStringDifferentsFor(String stringOne, String stringTwo){
        ArrayList<Character> allCharacterInDifference = new ArrayList<>();
        allCharacterInDifference = stringDifference(stringOne,stringTwo);
        boolean alreadyNumberFound = false;
        for (Character currentCharacter:allCharacterInDifference
                ) {
            if(currentCharacter != 'I' && currentCharacter != 'L'){
                try{
                    Integer.parseInt(String.valueOf(currentCharacter));
                    if(alreadyNumberFound){
                        return false;
                    }
                    alreadyNumberFound = true;
                }
                catch (Exception e){
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * used in order to obtain all the character that the two string are different for,
     * and put them into an ArrayList to be examinate
     * @param stringOne
     * @param stringTwo
     * @return
     */
    private ArrayList<Character> stringDifference(String stringOne, String stringTwo){
        ArrayList<Character> allDiferenceCharacter = new ArrayList<>();

        if(stringOne.length()!=stringTwo.length()){
            return allDiferenceCharacter;
        }
        else{
            for(int i = 0;i<stringOne.length();i++){
                if(stringOne.charAt(i)!=stringTwo.charAt(i)){
                    if(isNumber(String.valueOf(stringOne.charAt(i)))){
                        allDiferenceCharacter.add(stringOne.charAt(i));
                    }
                    if(isNumber(String.valueOf(stringTwo.charAt(i)))){
                        allDiferenceCharacter.add(stringTwo.charAt(i));
                    }
                    if(!isNumber(String.valueOf(stringOne.charAt(i)))&&!isNumber(String.valueOf(stringTwo.charAt(i)))) {
                        allDiferenceCharacter.add(stringOne.charAt(i));
                        allDiferenceCharacter.add(stringTwo.charAt(i));
                    }
                }
            }
        }
        return allDiferenceCharacter;
    }

    /**
     * check if the word given by param is a number or not
     * @param word
     * @return true if word is a number, false otherwise
     */
    private boolean isNumber(String word){
        try{
            Integer.parseInt(word);
            return true;
        }
        catch(Exception e){
            return false;
        }
    }

    /**
     * remove the most common name in order to avoid errors
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
        if(word.equalsIgnoreCase("cica")){
            return false;
        }
        if(word.equalsIgnoreCase("team")){
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
     * select the a part of the text given in param,
     * from index +/- 5
     * @param textInArray
     * @param index
     * @return
     */
    private String selectText(ArrayList<String> textInArray,int index){
        String result = "";
        for (int i = 4; i > 0; i--) {
            try {
                result = result + " " + textInArray.get(index - i);
            }
            catch(IndexOutOfBoundsException e){

            }
        }
        for (int i = 0; i < 4; i++) {
            try {
                result = result + " " + textInArray.get(index + i);
            }
            catch(IndexOutOfBoundsException e){
                break;
            }
        }
        return result;
    }

    /***********************************************************************************************
     *********************************   PUBLIC METHODS   ******************************************
     **********************************************************************************************/

    /**
     * set the distanse to consider when a match is searching in the ocr text
     * @param distanceToConsider
     */
    public void setDistanceToConsider(int distanceToConsider){
        this.distanceToConsider = distanceToConsider;
    }

    public int getDistanceToConsider(){
        return distanceToConsider;
    }

    /**
     * get all the match that doesn't have the likely to be consider safe
     * @return
     */
    public ArrayList<LikelyHoodMatch> getUnsafeMatches(){
        return unsafeMatches;
    }

    public void setUnsafeMatches(ArrayList<LikelyHoodMatch> unsafeMatches){
        this.unsafeMatches = unsafeMatches;
    }

    /**
     * give all the match that are found in the ocrText and them LikelyHood
     * @return
     */
    public ArrayList<LikelyHoodMatch> getAllLikelyHoodMatch(){
        ArrayList<LikelyHoodMatch> allLikelyHoodMatch = findMatch();
        for (LikelyHoodMatch current:allLikelyHoodMatch
                ) {
                  Log.i("SAFE MATCH ", current.getPalimpsestMatch().getHomeTeam().getName() + " - " + current.getPalimpsestMatch().getAwayTeam().getName() + " " + current.getLikelyHood());
        }
        for (LikelyHoodMatch current:unsafeMatches
                ) {
            Log.i("NOT SAFE MATCH ", current.getPalimpsestMatch().getHomeTeam().getName() + " - " + current.getPalimpsestMatch().getAwayTeam().getName() + " " + current.getLikelyHood());

        }
        return allLikelyHoodMatch;
    }
}
