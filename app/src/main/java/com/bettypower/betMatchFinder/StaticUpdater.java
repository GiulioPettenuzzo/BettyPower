package com.bettypower.betMatchFinder;

import com.bettypower.betMatchFinder.entities.ConcreteMatchToFind;
import com.bettypower.betMatchFinder.entities.MatchToFind;
import com.bettypower.entities.PalimpsestMatch;
import com.bettypower.util.Helper;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by giuliopettenuzzo on 21/10/17.
 */

public class StaticUpdater {

    private ArrayList<PalimpsestMatch> allPalimpsestMatch;
    private ArrayList<MatchToFind> allMatchFoundByRealTimeOCR;
    private ArrayList<String> allQuoteFoundByRealTimeOCR;
    private Validator validator;
    private Helper helper;

    private ArrayList<MatchToFind> mainListOfMatches;


    public StaticUpdater(ArrayList<PalimpsestMatch> allPalimpsestMatch,ArrayList<MatchToFind> allMatchFoundByRealTimeOCR,ArrayList<String> allQuoteFoundByRealTimeOCR){
        this.allPalimpsestMatch = allPalimpsestMatch;
        this.allMatchFoundByRealTimeOCR = allMatchFoundByRealTimeOCR;
        this.allQuoteFoundByRealTimeOCR = allQuoteFoundByRealTimeOCR;
        mainListOfMatches = new ArrayList<>();
        validator = new Validator();
        helper = new Helper(allPalimpsestMatch);
    }

    public void updateNewElement(Map<String,Object> map) {
        for (String currentKey : map.keySet()) {
            if (currentKey.equals(Finder.PALIMPSEST)) {
                //solo per l'inizio
                if(mainListOfMatches.size()!=0 && getCurrentMatch().getPalimpsest()!=null && getCurrentMatch().getPalimpsest().equals(map.get(currentKey).toString())){
                    return;
                }
                ArrayList<PalimpsestMatch> allPossibleMatch = new ArrayList<>();
                if(mainListOfMatches.size()!=0) {
                    allPossibleMatch = validator.isPalimpsestValidate(map.get(currentKey).toString(), getCurrentMatch().getPalimpsestMatch());
                }
                if(mainListOfMatches.size()==0 || getCurrentMatch().getPalimpsest() != null || allPossibleMatch==null){
                    boolean foundInRealTimeOCR = false;
                    for (int i = 0; i<allMatchFoundByRealTimeOCR.size();i++){
                        if(allMatchFoundByRealTimeOCR.get(i).getPalimpsest()!=null && allMatchFoundByRealTimeOCR.get(i).getPalimpsest().equals(map.get(currentKey).toString())){
                            foundInRealTimeOCR = true;
                            mainListOfMatches.add(allMatchFoundByRealTimeOCR.get(i));
                            allMatchFoundByRealTimeOCR.remove(i);
                            break;
                        }
                        allPossibleMatch = validator.isPalimpsestValidate(map.get(currentKey).toString(),allMatchFoundByRealTimeOCR.get(i).getPalimpsestMatch());
                        if(allPossibleMatch != null){
                            foundInRealTimeOCR = true;
                            allMatchFoundByRealTimeOCR.get(i).setPalimpsest(map.get(currentKey).toString());
                            allMatchFoundByRealTimeOCR.get(i).setPalimpsestMatch(allPossibleMatch);
                            mainListOfMatches.add(allMatchFoundByRealTimeOCR.get(i));
                            allMatchFoundByRealTimeOCR.remove(i);
                            break;
                        }
                    }
                    if(!foundInRealTimeOCR){
                        updatePointer();
                        allPossibleMatch = validator.isPalimpsestValidate(map.get(currentKey).toString(),allPalimpsestMatch);
                        if(allPossibleMatch!=null) {
                            getCurrentMatch().setPalimpsest(map.get(currentKey).toString());
                            getCurrentMatch().setPalimpsestMatch(allPossibleMatch);
                        }
                    }
                }
                else{
                    allPossibleMatch = validator.isPalimpsestValidate(map.get(currentKey).toString(),getCurrentMatch().getPalimpsestMatch());
                    if(allPossibleMatch!=null) {
                        allMatchFoundByRealTimeOCR.get(mainListOfMatches.size()-1).setPalimpsest(map.get(currentKey).toString());
                        allMatchFoundByRealTimeOCR.get(mainListOfMatches.size()-1).setPalimpsestMatch(allPossibleMatch);
                    }
                }
            }
            if (currentKey.equals(Finder.DATE) && mainListOfMatches.size()!=0) {
                ArrayList<PalimpsestMatch> allPossibleMatch = validator.isDateValidate(map.get(currentKey).toString(),getCurrentMatch().getPalimpsestMatch());
                if(allPossibleMatch!=null){
                    getCurrentMatch().setDate(map.get(currentKey).toString());
                    getCurrentMatch().setPalimpsestMatch(allPossibleMatch);
                }
            }
            if (currentKey.equals(Finder.HOUR)&& mainListOfMatches.size()!=0) {
                ArrayList<PalimpsestMatch> allPossibleMatch = validator.isHourValidate(map.get(currentKey).toString(),getCurrentMatch().getPalimpsestMatch());
                if(allPossibleMatch!=null){
                    getCurrentMatch().setHour(map.get(currentKey).toString());
                    getCurrentMatch().setPalimpsestMatch(allPossibleMatch);
                }
            }
            if (currentKey.equals(Finder.NAME)) {
                resolveNameMethod(map.get(currentKey).toString());
            }
            if (currentKey.equals(Finder.NAME_TWO)) {
                resolveNameMethod(map.get(currentKey).toString());
            }
            if (currentKey.equals(Finder.QUOTE)&& mainListOfMatches.size()!=0) {
                /*
                if(mainListOfMatches.get(pointer).getHour() != null && !mainListOfMatches.get(pointer).getHour().equals(map.get(currentKey).toString()) ||
                        (map.containsKey(Finder.QUOTE) && !map.get(Finder.QUOTE).toString().equals(map.get(currentKey).toString())) ){
                    mainListOfOdds.add(map.get(currentKey).toString());
                }*/
            }
          //  if (currentKey.equals(Finder.BOOKMAKER)) {


         //   }
            if (currentKey.equals(Finder.BET)&& mainListOfMatches.size()!=0) {
                if(validator.isBetValidate(map.get(currentKey).toString(),getCurrentMatch().getBetKind(),
                        getCurrentMatch().getOdds(),getCurrentMatch().getPalimpsestMatch())){
                    getCurrentMatch().setBet(map.get(currentKey).toString());
                }
                else{
                    getCurrentMatch().setBet(null);
                    getCurrentMatch().setBetKind(null);
                }
            }
            if (currentKey.equals(Finder.BET_KIND)&& mainListOfMatches.size()!=0) {
                if(validator.isBetKindValidate(getCurrentMatch().getBet(),map.get(currentKey).toString())){
                    getCurrentMatch().setBetKind(map.get(currentKey).toString());
                }
                else {
                    getCurrentMatch().setBet(null);
                    getCurrentMatch().setBetKind(null);
                }
            }
        }
    }

    private MatchToFind getCurrentMatch(){
        return mainListOfMatches.get(mainListOfMatches.size()-1);
    }

    private void updatePointer(){
        mainListOfMatches.add(new ConcreteMatchToFind());
        getCurrentMatch().setPalimpsestMatch(allPalimpsestMatch);
    }

    private boolean isMatchEmpty(MatchToFind matchToFind){
        if(matchToFind.getPalimpsest()==null && matchToFind.getHomeName()==null && matchToFind.getAwayName()==null &&
                matchToFind.getHour()==null && matchToFind.getDate() == null && matchToFind.getBet() == null &&
                matchToFind.getBetKind() == null && matchToFind.getUnknownTeamName()==null){
            return true;
        }
        else{
            return false;
        }
    }

    private void resolveNameMethod(String name){
        if (mainListOfMatches.size() != 0) {
            if ((getCurrentMatch().getHomeName() != null && getCurrentMatch().getHomeName().contains(name)) ||
                    (getCurrentMatch().getAwayName() != null && getCurrentMatch().getAwayName().contains(name)) ||
                    (getCurrentMatch().getUnknownTeamName() != null && getCurrentMatch().getUnknownTeamName().contains(name))) {
                return;
            }
        }
        if(mainListOfMatches.size()==0 || (getCurrentMatch().getHomeName()!=null && getCurrentMatch().getAwayName()!=null)){
            updatePointerForNames(name);
        }

        Map<String,ArrayList<PalimpsestMatch>> allPossibleMapMatches = validator.isNameValidate(name,getCurrentMatch().getPalimpsestMatch());
        if(allPossibleMapMatches==null){
            //TODO
            updatePointerForNames(name);
            resolveNameMethod(name);
        }
        else if(allPossibleMapMatches.containsKey(Validator.HOME_TEAM) && allPossibleMapMatches.containsKey(Validator.AWAY_TEAM)){
            if(getCurrentMatch().getUnknownTeamName()==null){
                if(getCurrentMatch().getHomeName()!=null){
                    getCurrentMatch().setAwayTeamName(name);
                    getCurrentMatch().setPalimpsestMatch(allPossibleMapMatches.get(Validator.AWAY_TEAM));
                }
                else{
                    getCurrentMatch().setUnknownTeamName(name);
                    getCurrentMatch().setPalimpsestMatch(helper.joinTwoPalimpsestLists(allPossibleMapMatches.get(Validator.HOME_TEAM),allPossibleMapMatches.get(Validator.AWAY_TEAM)));
                }
            }
            else{
                getCurrentMatch().setHomeTeamName(getCurrentMatch().getUnknownTeamName());
                getCurrentMatch().setAwayTeamName(name);
                getCurrentMatch().setUnknownTeamName(null);
            }
        }
        else if(allPossibleMapMatches.containsKey(Validator.HOME_TEAM)){
            if(getCurrentMatch().getHomeName()!=null && !getCurrentMatch().getHomeName().contains(name)){
                updatePointerForNames(name);
                resolveNameMethod(name);
            }
            else {
                getCurrentMatch().setHomeTeamName(name);
                getCurrentMatch().setPalimpsestMatch(allPossibleMapMatches.get(Validator.HOME_TEAM));
            }
        }
        else if(allPossibleMapMatches.containsKey(Validator.AWAY_TEAM)){
            if(getCurrentMatch().getAwayName()!=null && !getCurrentMatch().getAwayName().contains(name)){
                updatePointerForNames(name);
                resolveNameMethod(name);
            }
            else {
                getCurrentMatch().setAwayTeamName(name);
                getCurrentMatch().setPalimpsestMatch(allPossibleMapMatches.get(Validator.AWAY_TEAM));
            }
        }
    }

    private void updatePointerForNames(String name){
        boolean foundInRealTimeOCR = false;
        for(int i = 0; i<allMatchFoundByRealTimeOCR.size();i++){
            if((allMatchFoundByRealTimeOCR.get(i).getHomeName()!=null && allMatchFoundByRealTimeOCR.get(i).getHomeName().equals(name)) ||
                    (allMatchFoundByRealTimeOCR.get(i).getAwayName()!=null && allMatchFoundByRealTimeOCR.get(i).getAwayName().equals(name)) ||
                            (allMatchFoundByRealTimeOCR.get(i).getUnknownTeamName()!=null && allMatchFoundByRealTimeOCR.get(i).getUnknownTeamName().equals(name))){
                foundInRealTimeOCR = true;
                mainListOfMatches.add(allMatchFoundByRealTimeOCR.get(i));
                allMatchFoundByRealTimeOCR.remove(i);
                break;
            }
            Map map = validator.isNameValidate(name,allMatchFoundByRealTimeOCR.get(i).getPalimpsestMatch());
            if(map!=null && (map.containsKey(Validator.HOME_TEAM) || map.containsKey(Validator.AWAY_TEAM))){
                foundInRealTimeOCR = true;
                mainListOfMatches.add(allMatchFoundByRealTimeOCR.get(i));
                allMatchFoundByRealTimeOCR.remove(i);
                break;
            }
        }
        if(!foundInRealTimeOCR){
            updatePointer();
        }
    }

    public ArrayList<MatchToFind> getAllMatchFound(){
        return mainListOfMatches;
    }

    public ArrayList<MatchToFind> getAllMatchRemainedFromRealTimeOCR(){
        return allMatchFoundByRealTimeOCR;
    }
}
