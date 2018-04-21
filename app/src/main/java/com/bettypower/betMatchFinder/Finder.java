package com.bettypower.betMatchFinder;

import android.util.ArrayMap;

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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
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

    //private BetLabelSet betLabelSet = new BetLabelSet();
    private Map<String,ArrayList<String>> betLabelSet = new BetLabelSet().getAllBet();
    private Map<String,ArrayList<String>> betKindLabelSet = new BetLabelSet().getAllBetKind();
    private ArrayList<String> bookMakerLabelSet = new BookMakerLabelSet().getAllSet();
    private ArrayList<String> nameSeparatorLabelSet = new SeparatorLabelSet().getAllNameSeparator();
    private ArrayList<String> quoteSeparatorLabelSet = new SeparatorLabelSet().getAllQuoteSeparator();
    private ArrayList<String> moneySeparator = new SeparatorLabelSet().getMoneySeparator();
    private ArrayList<String> vincitaLabelSet = new BookMakerLabelSet().getAllVincitaSet();
    private ArrayList<String> puntataLabelSet = new BookMakerLabelSet().gettAllPuntataSet();

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
    static final String VINCITA = "vincita";
    static final String PUNTATA = "puntata";
    static final String EURO = "euro";


    public Finder(ArrayList<PalimpsestMatch> allPalimpsestMatch){
        this.allPalimpsestMatch = allPalimpsestMatch;
        helper = new Helper(allPalimpsestMatch);
        divider = new Divider();
        bookMakerFound = false;
        allTeamsWord = getAllCompleteStringNamesOrdered();
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
                    //TODO caso "atalanta-chievo verona"
                    for (String currentSeparator : nameSeparatorLabelSet
                            ) {
                        if(word.contains(currentSeparator) && !currentSeparator.equals(".")) {
                            StringTokenizer token = new StringTokenizer(word, currentSeparator);
                            if(token.countTokens()==2) {
                                String teamOne = token.nextToken();
                                teamOne = isNameBinaryBig(teamOne);
                                lastWords.add(teamOne);
                                String teamTwo = token.nextToken();
                                lastWords.add(teamTwo);
                                teamTwo = isNameBinaryBig(teamTwo);
                                if (teamOne != null)
                                    map.put(NAME, teamOne);
                                if (teamTwo != null)
                                    map.put(NAME_TWO, teamTwo);
                            }
                            else {
                                String falseName = word.replaceAll(currentSeparator,"");
                                String name = isNameBinaryBig(falseName);
                                if(name != null)
                                    map.put(NAME,name);
                            }

                        }
                        else{
                            String name = isNameBinaryBig(word);
                            if(name != null)
                                map.put(NAME,name);
                        }
                    }


                    String name = isNameBinaryBig(word);
                    if(name != null){
                        map.put(NAME,name);
                    }

                }
            }

            if(isVincita()!=null && map.size()<=1){
                if(map.get(QUOTE)!=null){
                    map.remove(QUOTE);
                    map.put(VINCITA,isVincita());
                }
            }
            if(isPuntata()!=null && map.size()<=1){
                if(map.get(QUOTE)!=null){
                    map.remove(QUOTE);
                    map.put(VINCITA,isPuntata());
                }
            }
            if(isThereEuro && map.size()==0){
                map.put(EURO,isMoney(word));
            }
        }
        return map;
    }

    /***********************************************************************************************
     *********************************   MAIN METHODS   ****************************************
     **********************************************************************************************/

    public String isBookMaker(){
        for (String currentBookMaker:bookMakerLabelSet
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

    private boolean isPossibleVincita = false;
    private boolean isPossiblePuntata = false;
    private boolean isThereEuro = false;

    public String isVincita(){
        String lowerWord = word.toLowerCase();
        if(isPossibleVincita && isMoney(lowerWord)!=null){
            return isMoney(lowerWord);
        }
        isPossibleVincita = false;
        for (String currentWord:vincitaLabelSet
             ) {
            lowerWord = word.toLowerCase();
            if(currentWord.contains(lowerWord)){
                StringTokenizer token = new StringTokenizer(currentWord);
                if(token.countTokens()==1){
                    isPossibleVincita=true;
                }else{
                    if(lastWords.size()>token.countTokens()){
                        int occurrence = 1;
                        for (int i = lastWords.size()-token.countTokens();i<lastWords.size();i++){
                            lowerWord = lastWords.get(i).toLowerCase();
                            if(currentWord.contains(lowerWord)){
                                occurrence++;
                                if(token.countTokens()==occurrence){
                                    isPossibleVincita=true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    public String isPuntata(){
        String lowerWord = word.toLowerCase();
        if(isPossiblePuntata && isMoney(lowerWord)!=null){
            return isMoney(lowerWord);
        }
        isPossiblePuntata = false;
        for (String currentWord:puntataLabelSet
             ) {
            lowerWord = word.toLowerCase();
            if(currentWord.contains(lowerWord)){
                StringTokenizer token = new StringTokenizer(currentWord);
                if(token.countTokens()==1){
                    isPossiblePuntata = true;
                }else{
                    if(lastWords.size()>token.countTokens()){
                        int occurrence = 1;
                        for (int i = lastWords.size()-token.countTokens();i<lastWords.size();i++){
                            lowerWord = lastWords.get(i).toLowerCase();
                            if(currentWord.contains(lowerWord)){
                                occurrence++;
                                if(token.countTokens()==occurrence){
                                    isPossiblePuntata=true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    public String isMoney(String currentWord){

        if(currentWord.equalsIgnoreCase("€")){
            try {
                String isMoney =  isMoney(lastWords.get(lastWords.size() - 1));
                if(isMoney!=null){
                    isThereEuro=true;
                    return isMoney;
                }
            }catch(Exception ignored){

            }
        }

        for (String currentSeparator:moneySeparator
                ) {
            StringTokenizer token = new StringTokenizer(currentWord,currentSeparator);
            if(token.countTokens() == NUMBER_OF_WORD_IN_QUOTE){
                String firstWord = token.nextToken();
                String secondWord = token.nextToken();
                if(secondWord.contains("€")&& secondWord.length() <=MAX_LENTH_OF_QUOTE+1){ //più uno perch' c'è anche l'euro
                    isThereEuro = true;
                    return firstWord + "," + secondWord.replace("€","");
                }
                if(helper.isNumber(firstWord) && helper.isNumber(secondWord) && secondWord.length() <=MAX_LENTH_OF_QUOTE){
                    return firstWord + "," + secondWord;
                }
            }
        }
        isThereEuro=false;
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
        for (String currentBetKey:betLabelSet.keySet()
             ) {
            for (String currentBetValue:betLabelSet.get(currentBetKey)
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
                        if(lastWords.get(lastWords.size() - i)!=null) {
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
        }
        return null;
    }

    public String isQuote(){
        for (String currentSeparator:quoteSeparatorLabelSet
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
        for (String currentBetKindKey:betKindLabelSet.keySet()
                ) {
            for (String currentBetKindValue:betKindLabelSet.get(currentBetKindKey)
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
                        if(lastWords.get(lastWords.size() - i)!=null) {
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
        }
        return null;
    }

    //qui perdo tre secondi
    public String isName(String word){
        String pattern; //use because I want start with the same word in every cycle
        for (String currentString:allTeamsWord
                ) {
            String nameFound = "";
            pattern = word;
           if(isWordContainedInTeamNameWithoutOcrError(currentString,pattern)){
                nameFound = nameFound + pattern;
                if(currentString.length() == nameFound.length()){
                    return currentString;
                }
                if(getNumberOfTokens(currentString)>1 && !nameFound.isEmpty()) {
                    for (int i = 1; i <= lastWords.size(); i++) {
                        pattern = removeSeparator(lastWords.get(lastWords.size() - i),currentString);
                        if (isWordContainedInTeamNameWithoutOcrError(currentString, pattern)) {
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

    private String isNameBinaryBig(String word){
        String wordToCompare = removeSeparatorTwo(word);
        wordToCompare = removeIandL(wordToCompare);
        int index = Collections.binarySearch(allTeamsWord,wordToCompare);
        String result = null;
        String correctWord = hashCompleteName.get(wordToCompare);
        if (index >= 0 && correctWord.length() == word.length()) {
            result = correctWord;
        }
        for (int i = 1; i <= 5; i++) {
            try {

                if(removeSeparatorNoSpace(lastWords.get(lastWords.size()-i)).length() == lastWords.get(lastWords.size()-i).length()) {
                    word = lastWords.get(lastWords.size() - i) + " " + word;
                    wordToCompare = removeSeparatorTwo(word);
                    wordToCompare = removeIandL(wordToCompare);
                    index = Collections.binarySearch(allTeamsWord, wordToCompare);
                    correctWord = hashCompleteName.get(wordToCompare);
                    if (index > 0 && correctWord.length() == word.length()) {
                        result = correctWord;
                    }
                }
            } catch (Exception e) {
                break;
            }
        }
        return result;
    }

    private Map<String,String> hashCompleteName = new HashMap<>();

    private ArrayList<String> getAllCompleteStringNamesOrdered(){
        ArrayList<String> completeStringNames = new ArrayList<>();
        //TODO qui crasha se non ha le partite null pointer exception
        for (PalimpsestMatch currentPalimpsestMatch:allPalimpsestMatch
                ) {
            String homeName = currentPalimpsestMatch.getHomeTeam().getName();
            homeName = removeSeparatorTwo(homeName);
            homeName = removeIandL(homeName);
            if(completeStringNames.lastIndexOf(homeName)<0){
                completeStringNames.add(homeName);
                hashCompleteName.put(homeName,currentPalimpsestMatch.getHomeTeam().getName());
            }
            String awayName = currentPalimpsestMatch.getAwayTeam().getName();
            awayName = removeSeparatorTwo(awayName);
            awayName = removeIandL(awayName);
            if(completeStringNames.lastIndexOf(awayName)<0){
                completeStringNames.add(awayName);
                hashCompleteName.put(awayName,currentPalimpsestMatch.getAwayTeam().getName());
            }
        }
        Collections.sort(completeStringNames);
        return completeStringNames;
    }

    private String removeSeparatorTwo(String teamNameWord){
        for (String currentSepartor:nameSeparatorLabelSet
                ) {
            if(teamNameWord.contains(currentSepartor)){
                if(currentSepartor.equals(".")){
                    teamNameWord = teamNameWord.replaceAll("\\.", " ");
                }else {
                    teamNameWord = teamNameWord.replaceAll(currentSepartor, " ");
                }
            }
        }
        return teamNameWord;
    }

    private String removeSeparatorNoSpace(String teamNameWord){
        for (String currentSepartor:nameSeparatorLabelSet
                ) {
            if(teamNameWord.contains(currentSepartor)){
                if(!currentSepartor.equals(".")){
                    teamNameWord = teamNameWord.replaceAll(currentSepartor, "");
                }
            }
        }
        return teamNameWord;
    }

    private String removeIandL(String word){
        word = word.toUpperCase();
        word = word.replaceAll("I", "");
        word = word.replaceAll("L", "");
        return word;
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
        //Collection<String> userCollection = new HashSet<String>(completeStringNames);
        Collections.sort(completeStringNames);
        //Collections.binarySearch(completeStringNames,"ciao");
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
        for (String currentSepartor:nameSeparatorLabelSet
             ) {
            if(word.contains(currentSepartor)){
                String wordFixed = word.replaceAll(currentSepartor, "");
                if(!isWordContainedInTeamNameWithoutOcrError(totalName, wordFixed)) {
                    wordFixed = word.replaceAll(currentSepartor, " ");
                    StringTokenizer token = new StringTokenizer(wordFixed);
                    while(token.hasMoreTokens()){
                        wordFixed = token.nextToken();
                        if(isWordContainedInTeamNameWithoutOcrError(totalName, wordFixed)) {
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

    /**
     * this method compare two string given in param without the character that are more likely to be read
     * wrong from ocr. usually those character are 'i' and 'l'
     * @return true if wordOne is equals Ignoring Case with wordTwo, false otherwise
     */
    private boolean compareNamesWithoutOCRError(String wordOne, String wordTwo){
        wordOne = wordOne.toUpperCase();
        wordTwo = wordTwo.toUpperCase();
        if(wordOne.length() == wordTwo.length()){
            for(int i = 0;i<wordOne.length();i++){
                if((wordOne.charAt(i) != wordTwo.charAt(i)) && (
                        (wordOne.charAt(i) != 'L' && wordOne.charAt(i) != 'I') ||
                                (wordTwo.charAt(i) != 'L' && wordTwo.charAt(i) != 'I'))){
                    return false;
                }
            }
        }
        else{
            return false;
        }
        return true;
    }

    /**
     * this metod do the same work of word1.contains(word2) but using the method that comapre the two word
     * without take care about the difference between 'i' and 'l'
     * @param teamName has more than one word
     * @param word has only one word
     * @return true if teamName contains word, false otherwise
     */
    private boolean isWordContainedInTeamNameWithoutOcrError(String teamName, String word){
        StringTokenizer token = new StringTokenizer(teamName);
        while(token.hasMoreTokens()){
            String singleTeamName = token.nextToken();
            if(compareNamesWithoutOCRError(singleTeamName,word)){
                return true;
            }
        }
        return false;
    }

    /************************************  FOR PALIMPSESTS   ****************************************/

    private ArrayList<Long> getAllPalimpsestInOrder(){
        ArrayList<Long> result = new ArrayList<>();
        for (PalimpsestMatch currentPalimpsestMatch:allPalimpsestMatch
             ) {
            result.add(Long.parseLong(currentPalimpsestMatch.getPalimpsest()));
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
