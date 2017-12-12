package com.bettypower.betMatchFinder;

import com.bettypower.betMatchFinder.entities.Date;
import com.bettypower.betMatchFinder.fixers.DateFixer;
import com.bettypower.betMatchFinder.fixers.HourFixer;
import com.bettypower.betMatchFinder.fixers.PalimpsestFixer;
import com.bettypower.betMatchFinder.labelSet.BetLabelSet;
import com.bettypower.betMatchFinder.labelSet.BookMakerLabelSet;
import com.bettypower.betMatchFinder.labelSet.SeparatorLabelSet;
import com.bettypower.entities.PalimpsestMatch;
import com.bettypower.util.Helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * This is the class use to recognize if a word is a nuame, a palimpsest, a date, an hour, a bookmaker,
 * a Quote, a Bet or a BetKind
 * Created by giuliopettenuzzo on 21/10/17.
 */

public class Finder {

    private String word;

    private ArrayList<PalimpsestMatch> allPalimpsestMatch;
    private ArrayList<String> lastWords = new ArrayList<>(); //used in palimpsest and bookmaker
    private ArrayList<String> allTeamsWord;
    private ArrayList<Long> allPalimpsestNumbers;

    private Helper helper;
    private Divider divider;
    private boolean bookMakerFound;

    private PalimpsestFixer palimpsestFixer;
    private DateFixer dateFixer;
    private HourFixer hourFixer;

    private static final int NUMBER_OF_WORD_IN_QUOTE = 2;
    private static final int MAX_LENTH_OF_QUOTE = 2;

    static final String PALIMPSEST = "palimpsest";
    static final String BET = "bet";
    static final String BET_KIND = "betkind";
    static final String NAME = "teamName";
    static final String NAME_TWO = "teamName_two";
    static final String QUOTE = "quote";
    static final String BOOKMAKER = "bookmaker";
    static final String DATE = "date";
    static final String HOUR = "hour";


    public Finder(ArrayList<PalimpsestMatch> allPalimpsestMatch){
        this.allPalimpsestMatch = allPalimpsestMatch;
        helper = new Helper(allPalimpsestMatch);
        divider = new Divider();
        bookMakerFound = false;
        allTeamsWord = getAllCompleteStringNames();
        allPalimpsestNumbers = getAllPalimpsestInOrder();
        palimpsestFixer = new PalimpsestFixer();
        dateFixer = new DateFixer();
        hourFixer = new HourFixer();
    }

    /***********************************************************************************************
     *********************************   PUBLIC METHODS   ****************************************
     **********************************************************************************************/

    public void setWord(String word){
        if(this.word!=null) {
            lastWords.add(this.word);
        }
        this.word = word;
        divider.setWord(word);
    }

    public Map<String,Object> getWordKinds(){
        Map<String,Object> map = new HashMap<>();
        boolean found = false;
        for (String currentField:divider.getPossibleFields()
             ) {
            if(currentField.equals(Divider.IS_POSSIBLE_BET)){
                String bet = isBet();
                if(bet != null){
                    map.put(BET,bet);
                    found = true;
                }
            }
            if(currentField.equals(Divider.IS_POSSIBLE_QUOTE)){
                String quote = isQuote();
                if(quote != null) {
                    map.put(QUOTE, quote);
                    found = true;
                }
            }
            if(currentField.equals(Divider.IS_POSSIBLE_BETKIND)){
                String betKind = isBetKind();
                if(betKind != null) {
                    map.put(BET_KIND, betKind);
                    found = true;
                }
            }
            if(currentField.equals(Divider.IS_POSSIBLE_BOOKMAKER)){
                if(!bookMakerFound) {
                    String bookMaker = isBookMaker();
                    if (bookMaker != null) {
                        map.put(BOOKMAKER, bookMaker);
                        found = true;
                        bookMakerFound = true;
                    }
                }
            }
            if(currentField.equals(Divider.IS_POSSIBLE_DATE)){
                Date date = isDate();
                if(date != null) {
                    map.put(DATE, date);
                    found = true;
                }
            }
            if(currentField.equals(Divider.IS_POSSIBLE_HOUR)){
                String hour = isHour();
                if(hour != null) {
                    map.put(HOUR, hour);
                    found = true;
                }
            }
            if(currentField.equals(Divider.IS_POSSIBLE_PALIMPSEST)){
                if(!found) {
                    String palimpsest = isPalimpsest();
                    if (palimpsest != null) {
                        map.put(PALIMPSEST, palimpsest);
                    }
                }
            }
            if(currentField.equals(Divider.IS_POSSIBLE_NAME)){
                if(!found) {
                    SeparatorLabelSet separatorLabelSet = new SeparatorLabelSet();
                    for (String currentSeparator : separatorLabelSet.getAllNameSeparator()
                            ) {
                        if(word.contains(currentSeparator)) {
                            StringTokenizer token = new StringTokenizer(word, currentSeparator);
                            if(token.countTokens()==2) {
                                String teamOne = token.nextToken();
                                teamOne = isName(teamOne);
                                String teamTwo = token.nextToken();
                                teamTwo = isName(teamTwo);
                                if (teamOne != null)
                                    map.put(NAME, teamOne);
                                if (teamTwo != null)
                                    map.put(NAME_TWO, teamTwo);
                            }
                            else {
                                String falseName = word.replaceAll(currentSeparator,"");
                                String name = isName(falseName);
                                if(name != null)
                                    map.put(NAME,name);
                            }

                        }
                        else{
                            String name = isName(word);
                            if(name != null)
                                map.put(NAME,name);
                        }
                    }

                }
            }
        }
        return map;
    }

    /***********************************************************************************************
     *********************************   MAIN METHODS   ****************************************
     **********************************************************************************************/

    public String isBookMaker(){
        BookMakerLabelSet bookMakerLabelSet = new BookMakerLabelSet();
        for (String currentBookMaker:bookMakerLabelSet.getAllSet()
             ) {
            String bookMakerFound = "";
            String pattern = word.toLowerCase();
            if(currentBookMaker.contains(pattern)){
                bookMakerFound = bookMakerFound + pattern;
                if(bookMakerFound.length() == currentBookMaker.length()){
                    return currentBookMaker;
                }
                if(getNumberOfTokens(currentBookMaker)>1) {
                    for (int i = 1; i <= lastWords.size(); i++) {
                        pattern = lastWords.get(lastWords.size() - i).toLowerCase();
                        if (currentBookMaker.contains(pattern)) {
                            bookMakerFound = bookMakerFound + " " + pattern;
                            if (bookMakerFound.length() == currentBookMaker.length()) {
                                return currentBookMaker;
                            }
                        } else {
                            break;
                        }
                    }
                }
            }
        }
        return null;

    }

    /**
     * return null if the word passed in param is not a word,
     * the date in the format of the palimpsest's format match otherwise
     */
    public Date isDate(){
        dateFixer.setWord(word);
        return dateFixer.getDateFixedIfPresent(word);
    }

    /**
     * this method compare the current word to all the palimpsest in the dataset
     * this method also fix the palimpsest before checking
     * this method i O(log n) because use binary search
     * @return return a string contains the palimpsest if it is find, null otherwise
     */
    public String isPalimpsest(){
        palimpsestFixer.setWord(word);
        String palimpsest = palimpsestFixer.getFixedWord();
        if(!helper.isNumber(palimpsest))
            return null;
        Long palimpsestNumber = Long.parseLong(palimpsest);
        if(binarySearch(palimpsestNumber,0,allPalimpsestNumbers.size()-1)){
            return palimpsest;
        }
        if(lastWords.size() > 0) {
            palimpsestFixer.setWord(lastWords.get(lastWords.size() - 1));
            String lastPali = palimpsestFixer.getFixedWord();
            if (helper.isNumber(lastPali)) {
                palimpsest = lastPali + palimpsest;
                try {
                    palimpsestNumber = Long.parseLong(palimpsest);
                }
                catch (NumberFormatException e){
                    return null;
                }
                if (binarySearch(palimpsestNumber, 0, allPalimpsestNumbers.size() - 1)) {
                    return palimpsest;
                }
            }
        }
        return null;
    }

    public String isHour(){
        hourFixer.setWord(word);
        return hourFixer.getFixedWord();
    }

    public String isBet(){
        BetLabelSet betLabelSet = new BetLabelSet();
        for (String currentBetKey:betLabelSet.getAllBet().keySet()
             ) {
            for (String currentBetValue:betLabelSet.getAllBet().get(currentBetKey)
                 ) {
                String betFound = "";
                String pattern = word.toLowerCase();
                if(currentBetValue.contains(pattern)){
                    betFound = betFound + pattern;
                    if(betFound.length() == currentBetValue.length()){
                        return currentBetKey;
                    }
                }
                if(getNumberOfTokens(currentBetValue)>1) {
                    for (int i = 1; i <= lastWords.size(); i++) {
                        pattern = lastWords.get(lastWords.size() - i).toLowerCase();
                        if (currentBetValue.contains(pattern)) {
                            betFound = betFound + " " + pattern;
                            if (betFound.length() == currentBetValue.length()) {
                                return currentBetKey;
                            }
                        } else {
                            break;
                        }
                    }
                }
            }
        }
        return null;
    }

    public String isQuote(){
        SeparatorLabelSet separatorLabelSet = new SeparatorLabelSet();
        for (String currentSeparator:separatorLabelSet.getAllQuoteSeparator()
             ) {
            StringTokenizer token = new StringTokenizer(word,currentSeparator);
            if(token.countTokens() == NUMBER_OF_WORD_IN_QUOTE){
                String firstWord = token.nextToken();
                String secondWord = token.nextToken();
                if(helper.isNumber(firstWord) && helper.isNumber(secondWord) &&
                        firstWord.length() <=MAX_LENTH_OF_QUOTE && secondWord.length() <=MAX_LENTH_OF_QUOTE){
                    return firstWord + "," + secondWord;
                }
            }
        }
        return null;
    }

    public String isBetKind(){
        BetLabelSet betLabelSet = new BetLabelSet();
        for (String currentBetKindKey:betLabelSet.getAllBetKind().keySet()
                ) {
            for (String currentBetKindValue:betLabelSet.getAllBetKind().get(currentBetKindKey)
                    ) {
                String betKindFound = "";
                String pattern = word.toLowerCase();
                if(currentBetKindValue.contains(pattern)){
                    betKindFound = betKindFound + pattern;
                    if(betKindFound.length() == currentBetKindValue.length()){
                        return currentBetKindKey;
                    }
                }
                if(getNumberOfTokens(currentBetKindValue)>1) {
                    for (int i = 1; i <= lastWords.size(); i++) {
                        pattern = lastWords.get(lastWords.size() - i).toLowerCase();
                        if (currentBetKindValue.contains(pattern)) {
                            betKindFound = betKindFound + " " + pattern;
                            if (betKindFound.length() == currentBetKindValue.length()) {
                                return currentBetKindKey;
                            }
                        } else {
                            break;
                        }
                    }
                }
            }
        }
        return null;
    }

    //TODO ricerca binaria anche sui nomi
    public String isName(String word){
        String pattern; //use because I want start with the same word in every cycle
        for (String currentString:allTeamsWord
                ) {
            String nameFound = "";
            pattern = word;
            if(helper.isWordContainedInTeamNameWithoutOcrError(currentString,pattern)){
                nameFound = nameFound + pattern;
                if(currentString.length() == nameFound.length()){
                    return currentString;
                }
                if(getNumberOfTokens(currentString)>1 && !nameFound.isEmpty()) {
                    for (int i = 1; i <= lastWords.size(); i++) {
                        pattern = removeSeparator(lastWords.get(lastWords.size() - i),currentString);
                        if (helper.isWordContainedInTeamNameWithoutOcrError(currentString, pattern)) {
                            nameFound = nameFound + " " + pattern;
                            if (currentString.length() == nameFound.length()) {
                                return currentString;
                            }
                        } else {
                            break;
                        }
                    }
                }
            }
        }
        return null;
    }


    /* *********************************************************************************************
     *********************************   AUSILIAR METHODS   ****************************************
     **********************************************************************************************/

    /**************************************  FOR NAMES   ******************************************/

    private ArrayList<String> getAllCompleteStringNames(){
        ArrayList<String> completeStringNames = new ArrayList<>();
        for (PalimpsestMatch currentPalimpsestMatch:allPalimpsestMatch
             ) {
            String homeName = currentPalimpsestMatch.getHomeTeam().getName();
            if(!stringAlreadyInList(completeStringNames,homeName)){
                completeStringNames.add(homeName);
            }
            String awayName = currentPalimpsestMatch.getAwayTeam().getName();
            if(!stringAlreadyInList(completeStringNames,awayName)){
                completeStringNames.add(awayName);
            }
        }
        return completeStringNames;
    }

    private boolean stringAlreadyInList(ArrayList<String> list,String word){
        for (String currentString:list
             ) {
            if(currentString.equalsIgnoreCase(word)){
                return true;
            }
        }
        return false;
    }

    private String removeSeparator(String word,String totalName){
        SeparatorLabelSet separatorLabelSet = new SeparatorLabelSet();
        for (String currentSepartor:separatorLabelSet.getAllNameSeparator()
             ) {
            if(word.contains(currentSepartor)){
                String wordFixed = word.replaceAll(currentSepartor, "");
                if(!helper.isWordContainedInTeamNameWithoutOcrError(totalName, wordFixed)) {
                    wordFixed = word.replaceAll(currentSepartor, " ");
                    StringTokenizer token = new StringTokenizer(wordFixed);
                    while(token.hasMoreTokens()){
                        wordFixed = token.nextToken();
                        if(helper.isWordContainedInTeamNameWithoutOcrError(totalName, wordFixed)) {
                            word = wordFixed;
                        }
                    }
                }
                else{
                    word = wordFixed;
                }
            }
        }
        return word;
    }

    /************************************  FOR PALIMPSESTS   ****************************************/

//TODO questa cosa di ordinare l'array andrebbe fatta nell'on resolver in questo modo non la deve fare per entrambi gli ocr
    //TODO per il momento lascio stare perche ho anche le stringhe che vorrei ordinare

    private ArrayList<Long> getAllPalimpsestInOrder(){
        ArrayList<Long> result = new ArrayList<>();
        for (PalimpsestMatch currentPalimpsestMatch:allPalimpsestMatch
             ) {
            result.add(Long.parseLong(currentPalimpsestMatch.getCompletePalimpsest()));
        }
        Collections.sort(result);
        return result;
    }

    private boolean binarySearch(long palimpsest , int low, int high) {
        if (high < low)
            return false; // not found
        int mid = low + ((high - low) / 2);
        if (allPalimpsestNumbers.get(mid) > palimpsest)
            return binarySearch(palimpsest, low, mid - 1);
        else
            return allPalimpsestNumbers.get(mid) >= palimpsest || binarySearch(palimpsest, mid + 1, high);
    }

    private int getNumberOfTokens(String word) {
        StringTokenizer token = new StringTokenizer(word);
        return token.countTokens();
    }
}
