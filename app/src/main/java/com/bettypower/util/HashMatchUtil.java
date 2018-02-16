package com.bettypower.util;

import android.content.Intent;

import com.bettypower.entities.HiddenResult;
import com.bettypower.entities.Match;
import com.bettypower.entities.PalimpsestMatch;
import com.bettypower.entities.ParcelableHiddenResult;
import com.bettypower.entities.ParcelableMatch;
import com.bettypower.entities.ParcelablePalimpsestMatch;
import com.bettypower.entities.ParcelableTeam;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by giuliopettenuzzo on 25/11/17.
 */

public class HashMatchUtil {

    private static final String SEPARATOR = ":-)";
    private static final String MATCH_SEPARATOR = "this_match_is_finish";

    /**
     * given an arraylist of matches it returns the string describing the list
     * @param allMatch
     * @return
     */
    //MELBOURNE CITY FC :-) WELLINGTON PHOENIX :-) 21/10 08:35 :-) goal :-) NULL :-) 1 :-) 1 this_match_is_finishFC SKA-ENERGIYA KHABAROVSK :-) UFA :-) 21/10 10:30 :-) goal :-) NULL :-) 1 :-) 1 this_match_is_finishFK TOSNO :-) FC ROSTOV :-) 21/10 13:00 :-) 1 :-) NULL :-) 1 :-) 1 this_match_is_finishSYDNEY :-) WESTERN SYDNEY :-) 21/10 10:50 :-) NULL :-) NULL :-) 1 :-) 1 this_match_is_finishLEVANTE :-) GETAFE :-) 21/10 13:00 :-) 12 :-) NULL :-) 1 :-) 1 this_match_is_finishFINALE :-)  VIAREGGIO :-) 26/11 14:30 :-) NULL :-) NULL :-) 1 :-) 1 this_match_is_finishCHELSEA :-) WATFORD FC :-) 21/10 13:30 :-) 12 :-) NULL :-) 1 :-) 1 this_match_is_finishFINALE :-)  VIAREGGIO :-) 26/11 14:30 :-) NULL :-) NULL :-) 1 :-) 1 this_match_is_finish
    public String fromArrayToString(ArrayList<PalimpsestMatch> allMatch) {
        String result = "";
        for (PalimpsestMatch currentmatch:allMatch
             ) {
            result = result + currentmatch.getHomeTeam().getName() + " " + SEPARATOR;
            result = result + " " + currentmatch.getAwayTeam().getName() + " " + SEPARATOR;
            result = result + " " + currentmatch.getCompletePalimpsest() + " " + SEPARATOR;
            result = result + " " +  currentmatch.getTime() + " " +SEPARATOR;

            if(currentmatch.getBet() == null)
                result = result + " NULL " + SEPARATOR;
            else
                result = result + " " + currentmatch.getBet() + " " + SEPARATOR;
            if(currentmatch.getBetKind() == null)
                result = result + " NULL " + SEPARATOR;
            else
                result = result + " " + currentmatch.getBetKind() + " " + SEPARATOR;
            if(currentmatch.getQuote() == null)
                result = result + " NULL " + SEPARATOR;
            else
                result = result + " " + currentmatch.getQuote() + " " + SEPARATOR;

            result = result + " " + currentmatch.getHomeResult() + " " + SEPARATOR;
            result = result + " " + currentmatch.getAwayResult() + " " + SEPARATOR;
            if(currentmatch.isFissa())
                result = result + " true " + SEPARATOR;
            else
                result = result + " false " + SEPARATOR;
            if(currentmatch.getAllHiddenResult()!=null) {
                for (HiddenResult currentHiddenResult : currentmatch.getAllHiddenResult()
                        ) {
                    result = result + " " + currentHiddenResult.getActionTeam() + " " + SEPARATOR;
                    result = result + " " + currentHiddenResult.getAction() + " " + SEPARATOR;
                    result = result + " " + currentHiddenResult.getPlayerName() + " " + SEPARATOR;
                    if (currentHiddenResult.getResult() != null && !currentHiddenResult.getResult().equals(""))
                        result = result + " " + currentHiddenResult.getResult() + " " + SEPARATOR;
                    else
                        result = result + " NULL " + SEPARATOR;
                    result = result + " " + currentHiddenResult.getTime() + " " + SEPARATOR;
                }
            }
            result = result + " " +  MATCH_SEPARATOR + " ";
        }
        return result;
    }

    public ArrayList<PalimpsestMatch> fromStringToArray(String string) {
        StringTokenizer token = new StringTokenizer(string);
        ArrayList<PalimpsestMatch> result = new ArrayList<>();
        while(token.hasMoreTokens()){
            String word = token.nextToken();
            String homeTeamName = word;
            while(!word.equals(SEPARATOR)){
                word = token.nextToken();
                if(!word.equals(SEPARATOR))
                    homeTeamName = homeTeamName + " " + word;
            }

            word = token.nextToken();
            String awayTeamName = word;
            while(!word.equals(SEPARATOR)){
                word = token.nextToken();
                if(!word.equals(SEPARATOR))
                    awayTeamName = awayTeamName + " " + word;
            }
            PalimpsestMatch match = new ParcelablePalimpsestMatch(new ParcelableTeam(homeTeamName),new ParcelableTeam(awayTeamName));


            match.setCompletePalimpsest(token.nextToken());
            token.nextToken();


            word = token.nextToken();
            String time = word;
            while(!word.equals(SEPARATOR)){
                word = token.nextToken();
                if(!word.equals(SEPARATOR))
                    time = time + " " + word;
            }
            match.setTime(time);

            word = token.nextToken();
            if(!word.equals("NULL")){
                String bet = word;
                while(!word.equals(SEPARATOR)){
                    word = token.nextToken();
                    if(!word.equals(SEPARATOR))
                        bet = bet + " " + word;
                }
                match.setBet(bet);
            }
            else
                token.nextToken();

            word = token.nextToken();
            if(!word.equals("NULL")){
                String betKind = word;
                while(!word.equals(SEPARATOR)){
                    word = token.nextToken();
                    if(!word.equals(SEPARATOR))
                        betKind = betKind + " " + word;
                }
                match.setBetKind(betKind);
            }
            else
                token.nextToken();

            word = token.nextToken();
            if(!word.equals("NULL")){
                String quote = word;
                while(!word.equals(SEPARATOR)){
                    word = token.nextToken();
                    if(!word.equals(SEPARATOR))
                        quote = quote + " " + word;
                }
                match.setQuote(quote);
            }
            else
                token.nextToken();

            word = token.nextToken();
            String homeResult = word;
            if(!homeResult.equalsIgnoreCase("null")) {
                match.setHomeResult(homeResult);
            }else{
                match.setHomeResult("-");
            }

            token.nextToken();

            word = token.nextToken();
            String awayResult = word;
            if(!awayResult.equalsIgnoreCase("null")) {
                match.setAwayResult(awayResult);
            }else{
                match.setAwayResult("-");
            }

            token.nextToken();

            word = token.nextToken();
            String isFissa = word;
            if(isFissa.equals("true")){
                match.setFissa(true);
            }else{
                match.setFissa(false);
            }

            token.nextToken();
            ArrayList<HiddenResult> allHiddenResult = new ArrayList<>();
            while(!word.equals(MATCH_SEPARATOR)){
                word = token.nextToken();
                if(word.equals(MATCH_SEPARATOR)){
                    break;
                }
                String actionTeam = word;
                token.nextToken();

                word = token.nextToken();
                String action = word;
                token.nextToken();

                word = token.nextToken();
                String playerName = word;
                while(!word.equals(SEPARATOR)) {
                    word = token.nextToken();
                    if(!word.equals(SEPARATOR))
                        playerName = playerName + " " + word;
                }

                word = token.nextToken();
                String resultMatchHidden = word;
                token.nextToken();

                word = token.nextToken();
                String timeHidden = word;
                HiddenResult hiddenResult = new ParcelableHiddenResult(playerName,timeHidden,action, Integer.parseInt(actionTeam));
                if(!resultMatchHidden.equals("NULL"))
                    hiddenResult.setResult(resultMatchHidden);
                allHiddenResult.add(hiddenResult);
                word = token.nextToken();
            }
            match.setAllHiddenResult(allHiddenResult);
            result.add(match);

           // token.nextToken();
        }
        return result;
    }


}
