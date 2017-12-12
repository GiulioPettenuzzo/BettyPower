package com.bettypower.util;

import com.bettypower.entities.Match;
import com.bettypower.entities.ParcelableMatch;
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
    public String fromArrayToString(ArrayList<Match> allMatch) {
        String result = "";
        for (Match currentmatch:allMatch
             ) {
            result = result + currentmatch.getHomeTeam().getName() + " " + SEPARATOR;
            result = result + " " + currentmatch.getAwayTeam().getName() + " " + SEPARATOR;
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
            result = result + " " + currentmatch.getAwayResult() + " " + MATCH_SEPARATOR + " ";
        }
        return result;
    }

    public ArrayList<Match> fromStringToArray(String string) {
        StringTokenizer token = new StringTokenizer(string);
        ArrayList<Match> result = new ArrayList<>();
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

            Match match = new ParcelableMatch(new ParcelableTeam(homeTeamName),new ParcelableTeam(awayTeamName));

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
            match.setHomeResult(homeResult);

            token.nextToken();

            word = token.nextToken();
            String awayResult = word;
            match.setAwayResult(awayResult);

            result.add(match);

            token.nextToken();
        }
        return result;
    }


}
