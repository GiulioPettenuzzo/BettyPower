package com.bettypower.betMatchFinder;

import com.bettypower.betMatchFinder.entities.MatchToFind;
import com.bettypower.entities.PalimpsestMatch;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by giuliopettenuzzo on 17/11/17.
 */

public class Assembler {

    private ArrayList<MatchToFind> allMatchToAssemble;
    private Validator validator;

    public Assembler(ArrayList<MatchToFind> allMatchToAssemble){
        this.allMatchToAssemble = allMatchToAssemble;
        validator = new Validator();
    }

    public void setAllMatchToAssemble(ArrayList<MatchToFind> allMatchToAssemble){
        this.allMatchToAssemble = allMatchToAssemble;
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
        MatchToFind result = match;
        ArrayList<PalimpsestMatch> allMatchValidate;
        if(match.getPalimpsest()!=null && matchToCompare.getPalimpsest()!=null ){
            return null;
        }
        else if(match.getPalimpsest()==null && matchToCompare.getPalimpsest()!=null){
            allMatchValidate = validator.isPalimpsestValidate(matchToCompare.getPalimpsest(),match.getPalimpsestMatch());
            if(allMatchValidate!=null){
                result.setPalimpsest(matchToCompare.getPalimpsest());
                result.setPalimpsestMatch(allMatchValidate);
            }
            else{
                return null;
            }
        }

        if(match.getHomeName()!=null && matchToCompare.getHomeName()!=null){
            return null;
        }
        else if(match.getHomeName()==null && matchToCompare.getHomeName()!=null){
            allMatchValidate = validator.isHomeNameValidate(matchToCompare.getHomeName(),result.getPalimpsestMatch());
            if(allMatchValidate!=null){
                result.setHomeTeamName(matchToCompare.getHomeName());
                result.setPalimpsestMatch(allMatchValidate);
            }
            else{
                return null;
            }
        }

        if(match.getAwayName()!=null && matchToCompare.getAwayName()!=null){
            return null;
        }
        else if(match.getAwayName()==null && matchToCompare.getAwayName()!=null){
            allMatchValidate = validator.isAwayNameValidate(matchToCompare.getAwayName(),result.getPalimpsestMatch());
            if(allMatchValidate!=null){
                result.setAwayTeamName(matchToCompare.getAwayName());
                result.setPalimpsestMatch(allMatchValidate);
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
            allMatchValidate = validator.isHomeNameValidate(matchToCompare.getUnknownTeamName(),result.getPalimpsestMatch());
            if(allMatchValidate!=null){
                result.setHomeTeamName(matchToCompare.getUnknownTeamName());
                result.setPalimpsestMatch(allMatchValidate);
            }
            else{
                return null;
            }
        }
        else if(match.getHomeName()!=null && match.getAwayName()==null && matchToCompare.getUnknownTeamName()!=null){
            if(match.getUnknownTeamName()!=null){
                return null;
            }
            allMatchValidate = validator.isAwayNameValidate(matchToCompare.getUnknownTeamName(),result.getPalimpsestMatch());
            if(allMatchValidate!=null){
                result.setAwayTeamName(matchToCompare.getUnknownTeamName());
                result.setPalimpsestMatch(allMatchValidate);
            }
            else{
                return null;
            }
        }
        else if(match.getUnknownTeamName()!=null && matchToCompare.getUnknownTeamName()!=null){
            Map<String,ArrayList<PalimpsestMatch>> map = validator.isNameValidate(matchToCompare.getUnknownTeamName(),result.getPalimpsestMatch());
            if(map == null){
                return null;
            }
            else if(map.containsKey(Validator.HOME_TEAM) && map.containsKey(Validator.AWAY_TEAM)){
                //TODO
            }
            else if(map.containsKey(Validator.HOME_TEAM)){
                result.setHomeTeamName(matchToCompare.getUnknownTeamName());
            }
            else if(map.containsKey(Validator.AWAY_TEAM)){
                result.setAwayTeamName(matchToCompare.getUnknownTeamName());
            }
            else{
                return null;
            }
        }

        if(match.getDate()!=null && matchToCompare.getDate()!=null){
            return null;
        }
        else if(match.getDate()==null && matchToCompare.getDate()!=null){
            allMatchValidate = validator.isDateValidate(matchToCompare.getDate(),result.getPalimpsestMatch());
            if(allMatchValidate!=null){
                result.setDate(matchToCompare.getDate());
                result.setPalimpsestMatch(allMatchValidate);
            }
            else{
                return  null;
            }
        }

        if(match.getHour()!=null && matchToCompare.getHour()!=null){
            return null;
        }
        else if(match.getHour()==null && matchToCompare.getHour()!=null){
            allMatchValidate = validator.isHourValidate(matchToCompare.getHour(),result.getPalimpsestMatch());
            if(allMatchValidate!=null){
                result.setHour(matchToCompare.getHour());
                result.setPalimpsestMatch(matchToCompare.getPalimpsestMatch());
            }
            else{
                return null;
            }
        }

        if(match.getBet()!=null && matchToCompare.getBet()!=null){
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
        }

        return result;
    }
}
