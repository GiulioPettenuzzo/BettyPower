package com.bettypower.betMatchFinder;

import com.bettypower.betMatchFinder.entities.MatchToFind;
import com.bettypower.entities.PalimpsestMatch;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by giuliopettenuzzo on 17/11/17.
 * Check if the predictors doesn't recognize that two match where the same
 */

public class Assembler {

    private ArrayList<MatchToFind> allMatchToAssemble;
    private Validator validator;

    public Assembler(ArrayList<MatchToFind> allMatchToAssemble){
        this.allMatchToAssemble = allMatchToAssemble;
        validator = new Validator();
    }

    public ArrayList<MatchToFind> getAllMatchReassembled(){
        for(int i = allMatchToAssemble.size()-1;i>=0;i--){
            MatchToFind currentMatch = allMatchToAssemble.get(i);
            for(int j = i-1; j>=0; j--){
                MatchToFind currentCompareMatch = allMatchToAssemble.get(j);
                MatchToFind localResult = isTheSameMatch(currentCompareMatch,currentMatch);
                if(localResult!=null){
                    allMatchToAssemble.set(j,localResult);
                    allMatchToAssemble.remove(i);
                    break;
                }
            }
        }
        return allMatchToAssemble;
    }

    private MatchToFind isTheSameMatch(MatchToFind match,MatchToFind matchToCompare){
        ArrayList<PalimpsestMatch> allMatchValidate;
        if(match.getPalimpsest()!=null && matchToCompare.getPalimpsest()!=null ){
            return null;
        }
        else if(match.getPalimpsest()==null && matchToCompare.getPalimpsest()!=null){
            allMatchValidate = validator.isPalimpsestValidate(matchToCompare.getPalimpsest(),match.getPalimpsestMatch());
            if(allMatchValidate!=null){
                match.setPalimpsest(matchToCompare.getPalimpsest());
                match.setPalimpsestMatch(allMatchValidate);
            }
            else{
                return null;
            }
        }

        if(match.getHomeName()!=null && matchToCompare.getHomeName()!=null){
            return null;
        }
        else if(match.getHomeName()==null && matchToCompare.getHomeName()!=null){
            allMatchValidate = validator.isHomeNameValidate(matchToCompare.getHomeName(), match.getPalimpsestMatch());
            if(allMatchValidate!=null){
                match.setHomeTeamName(matchToCompare.getHomeName());
                match.setPalimpsestMatch(allMatchValidate);
            }
            else{
                return null;
            }
        }

        if(match.getAwayName()!=null && matchToCompare.getAwayName()!=null){
            return null;
        }
        else if(match.getAwayName()==null && matchToCompare.getAwayName()!=null){
            allMatchValidate = validator.isAwayNameValidate(matchToCompare.getAwayName(), match.getPalimpsestMatch());
            if(allMatchValidate!=null){
                match.setAwayTeamName(matchToCompare.getAwayName());
                match.setPalimpsestMatch(allMatchValidate);
            }
            else{
                return null;
            }
        }

        //UNKNOWN NAMES
        if(match.getHomeName()!=null && match.getAwayName()!=null && matchToCompare.getUnknownTeamName()!=null){
            return null;
        }
        else if(match.getHomeName()==null && match.getAwayName()!=null && matchToCompare.getUnknownTeamName()!=null){
            if(match.getUnknownTeamName()!=null){
                return null;
            }
            allMatchValidate = validator.isHomeNameValidate(matchToCompare.getUnknownTeamName(), match.getPalimpsestMatch());
            if(allMatchValidate!=null){
                match.setHomeTeamName(matchToCompare.getUnknownTeamName());
                match.setPalimpsestMatch(allMatchValidate);
            }
            else{
                return null;
            }
        }
        else if(match.getHomeName()!=null && match.getAwayName()==null && matchToCompare.getUnknownTeamName()!=null){
            if(match.getUnknownTeamName()!=null){
                return null;
            }
            allMatchValidate = validator.isAwayNameValidate(matchToCompare.getUnknownTeamName(), match.getPalimpsestMatch());
            if(allMatchValidate!=null){
                match.setAwayTeamName(matchToCompare.getUnknownTeamName());
                match.setPalimpsestMatch(allMatchValidate);
            }
            else{
                return null;
            }
        }
        else if(match.getUnknownTeamName()!=null && matchToCompare.getUnknownTeamName()!=null){
            Map<String,ArrayList<PalimpsestMatch>> map = validator.isNameValidate(matchToCompare.getUnknownTeamName(), match.getPalimpsestMatch());
            if(map == null){
                return null;
            }
            else if(map.containsKey(Validator.HOME_TEAM)){
                match.setHomeTeamName(matchToCompare.getUnknownTeamName());
            }
            else if(map.containsKey(Validator.AWAY_TEAM)){
                match.setAwayTeamName(matchToCompare.getUnknownTeamName());
            }
            else{
                return null;
            }
        }

        if(match.getDate()!=null && matchToCompare.getDate()!=null){
            return null;
        }
        else if(match.getDate()==null && matchToCompare.getDate()!=null){
            allMatchValidate = validator.isDateValidate(matchToCompare.getDate(), match.getPalimpsestMatch());
            if(allMatchValidate!=null){
                match.setDate(matchToCompare.getDate());
                match.setPalimpsestMatch(allMatchValidate);
            }
            else{
                return  null;
            }
        }

        if(match.getHour()!=null && matchToCompare.getHour()!=null){
            return null;
        }
        else if(match.getHour()==null && matchToCompare.getHour()!=null){
            allMatchValidate = validator.isHourValidate(matchToCompare.getHour(), match.getPalimpsestMatch());
            if(allMatchValidate!=null){
                match.setHour(matchToCompare.getHour());
                match.setPalimpsestMatch(matchToCompare.getPalimpsestMatch());
            }
            else{
                return null;
            }
        }

      /*  if(match.getBet()!=null && matchToCompare.getBet()!=null){
            return null;
        }
        else if(match.getBet()==null && matchToCompare.getBet()!=null){
            if(validator.isBetKindValidate(matchToCompare.getBet(),match.getBetKind())){
                result.setBet(matchToCompare.getBet());
            }
            else{
                return null;
            }
        }

        if(match.getBetKind()!=null && matchToCompare.getBetKind()!=null){
            return null;
        }
        else if(match.getBetKind()==null && matchToCompare.getBetKind()!=null){
            if(validator.isBetKindValidate(match.getBet(),matchToCompare.getBetKind())){
                result.setBetKind(matchToCompare.getBetKind());
            }
            else{
                return null;
            }
        }*/

        return match;
    }
}
