package com.bettypower.util;

import com.bettypower.entities.PalimpsestMatch;
import com.bettypower.entities.Team;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by giuliopettenuzzo on 21/10/17.
 */

public class Helper {

    private ArrayList<PalimpsestMatch> allPalimpsestMatch;

    public Helper(ArrayList<PalimpsestMatch> allPalimpsestMatch){
        this.allPalimpsestMatch = allPalimpsestMatch;
    }

    public Helper(){

    }

    /**
     * check if the word given in param is a number or not
     * @param word to examinate
     * @return true if word is a number, false otherwise
     */
    public boolean isNumber(String word){
        try{
            long wordToLong = Long.parseLong(word);
            return true;
        }catch(Exception e){
            return false;
        }
    }

    public boolean binarySearch(ArrayList<Long> allNumbers,long palimpsest , int low, int high) {
        if (high < low)
            return false; // not found
        int mid = low + ((high - low) / 2);
        if (allNumbers.get(mid) > palimpsest)
            return binarySearch(allNumbers,palimpsest, low, mid-1);
        else if (allNumbers.get(mid) < palimpsest)
            return binarySearch(allNumbers,palimpsest, mid+1, high);
        else
            return true; // found
    }

    /**
     * @return all teams present in the palimpsest match data set
     */
    public ArrayList<Team> getAllTeams(){
        ArrayList<Team> result = new ArrayList<>();
        for (PalimpsestMatch currentPalimpsestMatch:allPalimpsestMatch
             ) {
            Team homeTeam = currentPalimpsestMatch.getHomeTeam();
            Team awayTeam = currentPalimpsestMatch.getAwayTeam();
            if(!isTeamInList(result,homeTeam)){
                result.add(homeTeam);
            }
            if(!isTeamInList(result,awayTeam)){
                result.add(awayTeam);
            }
        }
        return result;
    }

    /**
     * check if the team given in param is already present in the list, the decision is taken with name string matching
     * @param allTeams the list we consider
     * @param team the team we consider
     * @return true if the team is present in the list, false otherwise
     */
    public boolean isTeamInList(ArrayList<Team> allTeams,Team team){
        for (Team currentTeam:allTeams
             ) {
            if(team.getName().equalsIgnoreCase(currentTeam.getName())){
                return true;
            }
        }
        return false;
    }

    /**
     * this method compare two string given in param without the character that are more likely to be read
     * wrong from ocr. usually those character are 'i' and 'l'
     * @return true if wordOne is equals Ignoring Case with wordTwo, false otherwise
     */
    public boolean compareNamesWithoutOCRError(String wordOne, String wordTwo){
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
    public boolean isWordContainedInTeamNameWithoutOcrError(String teamName, String word){
        StringTokenizer token = new StringTokenizer(teamName);
        while(token.hasMoreTokens()){
            String singleTeamName = token.nextToken();
            if(compareNamesWithoutOCRError(singleTeamName,word)){
                return true;
            }
        }
        return false;
    }

    /**
     * this method make the join of the two list given in param,
     * to use this method you have to be sure that the two list haven't any element in common
     */
    public ArrayList<PalimpsestMatch> joinTwoPalimpsestLists(ArrayList<PalimpsestMatch> listOne,ArrayList<PalimpsestMatch> listTwo){
        for (PalimpsestMatch current:listTwo
             ) {
            listOne.add(current);
        }
        return listOne;
    }

}
