package com.bettypower.unpacker;

import android.util.Log;

import com.bettypower.entities.HiddenResult;
import com.bettypower.entities.PalimpsestMatch;
import com.bettypower.entities.ParcelablePalimpsestMatch;
import com.bettypower.entities.ParcelableTeam;
import com.renard.ocr.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * from <img src="http://fishtagram.it/football_logos/realsociedad.png" alt="real sociedad"> to arraylist of PalimpsestMatch
 * contains all the information like palimpsest and result
 * Created by giuliopettenuzzo on 02/03/18.
 */

public class AllMatchesUnpacker {

    private String response;
    private HiddenResultUnpacker hiddenResultUnpacker;

    public AllMatchesUnpacker(String response){
        this.response = response;
        hiddenResultUnpacker = new HiddenResultUnpacker();
    }

    public ArrayList<PalimpsestMatch> getAllMatches(){
        ArrayList<PalimpsestMatch> allPalimpsestMatch = new ArrayList<>();
        StringTokenizer token = new StringTokenizer(response);
        int i = 0;
        while(token.hasMoreTokens()){
            String word = token.nextToken();
            if(word.equals("=>")){
                word = token.nextToken();
                if(word.startsWith("[") && word.endsWith("]") && token.nextToken().equals("=>")){
                    word = token.nextToken();
                    if(word.startsWith("[") && word.endsWith("]") && token.nextToken().equals("=>")){
                        String homeTeam = "";
                        while(!word.equals("-")){
                            word = token.nextToken();
                            if(!word.equals("-")){
                                if(homeTeam.isEmpty()){
                                    homeTeam = word;
                                }else{
                                    homeTeam = homeTeam + " " + word;
                                }
                            }
                        }
                        String awayTeam = "";
                        String pali = "";
                        String event = "";
                        String date = "";
                        word = token.nextToken();
                        while(true){
                            if(isNumber(word)){
                                String wordOne = token.nextToken();
                                if(isNumber(wordOne)){
                                    String wordTwo = token.nextToken();
                                    if(isNumber(wordTwo)){
                                        if(awayTeam.isEmpty()){
                                            awayTeam = word;
                                        }else{
                                            awayTeam = awayTeam + " " + word;
                                        }
                                        pali = wordOne;
                                        event = wordTwo;
                                        break;
                                    }else{
                                        pali = word;
                                        event = wordOne;
                                        date = wordTwo;
                                        break;
                                    }
                                }else{ //caso in cui il temName abbia un numero in mezzo
                                    if(awayTeam.isEmpty()){
                                        awayTeam = word;
                                    }else{
                                        awayTeam = awayTeam + " " + word;
                                    }
                                    word = wordOne;
                                }
                            }
                            else{
                                if(awayTeam.isEmpty()){
                                    awayTeam = word;
                                }else{
                                    awayTeam = awayTeam + " " + word;
                                }
                                word = token.nextToken();
                            }
                        }
                        Log.i("TEAMS",homeTeam + " - " + awayTeam);
                        Log.i("PALI",pali);
                        Log.i("EVENT",event);
                        if(date.isEmpty()){
                            date = token.nextToken();
                        }
                        Log.i("DATA",date);
                        String hour = token.nextToken();
                        Log.i("HOUR",hour);

                        String betUno = token.nextToken();
                        String betX = token.nextToken();
                        String betDue = token.nextToken();
                        String betUnoX = token.nextToken();
                        String betXDue = token.nextToken();
                        String betUnoDue = token.nextToken();
                        String betUnder = token.nextToken();
                        String betOver = token.nextToken();
                        String betGoal = token.nextToken();
                        String betNoGoal = token.nextToken();

                        word = token.nextToken();
                        String wordOne = token.nextToken();

                        String resultTime = "";
                        Log.i("WORD",word + " + " + wordOne);

                        PalimpsestMatch palimpsestMatch = new ParcelablePalimpsestMatch(new ParcelableTeam(homeTeam),new ParcelableTeam(awayTeam),pali,event,date+" "+hour);
                        Map<String,String> allOdds = new HashMap<>();
                        allOdds.put("1",betUno);
                        allOdds.put("X",betX);
                        allOdds.put("2",betDue);
                        allOdds.put("1X",betUnoX);
                        allOdds.put("X2",betXDue);
                        allOdds.put("12",betUnoDue);
                        allOdds.put("under",betUnder);
                        allOdds.put("over",betOver);
                        allOdds.put("goal",betGoal);
                        allOdds.put("nogoal",betNoGoal);
                        palimpsestMatch.setAllOdds(allOdds);
                        palimpsestMatch.setHomeResult("-");
                        palimpsestMatch.setAwayResult("-");

                        if(word.equals("===>") || wordOne.equals("===>")) {
                            if (word.equals("===>")) {
                                resultTime = wordOne;
                            } else {
                                resultTime = token.nextToken();
                            }
                            if (!resultTime.equals("</pre><pre>")) {
                                Log.i("RESULT TIME", resultTime);
                                String result = "";
                                if(resultTime.equalsIgnoreCase("ht")){
                                    resultTime = "Intervallo";
                                }
                                if(resultTime.equalsIgnoreCase("ft")){
                                    resultTime = "Terminata";
                                }
                                if(resultTime.equalsIgnoreCase("postp.")){
                                    resultTime = "Posticipata";
                                }
                                while (!(word.startsWith("[") && word.endsWith("]"))) {
                                    word = token.nextToken();
                                    if (word.startsWith("[") && word.endsWith("]")) {
                                        result = word.substring(1, word.length() - 1);
                                        result = result.replace("-", ":");
                                        result = result.replace("?", "-");
                                    }
                                }

                                while (!word.equals("///")) {
                                    word = token.nextToken();
                                }

                                String hiddenResult = "";
                                ArrayList<HiddenResult> allHiddenResult = new ArrayList<>();
                                while (!(word.startsWith("[") && word.endsWith("]"))) {
                                    word = token.nextToken();
                                    hiddenResult = hiddenResult + " " + word;
                                    if ((word.startsWith("[") && word.endsWith("]")) || word.equals("//")) {
                                        hiddenResultUnpacker.setResponse(hiddenResult);
                                        HiddenResult hiddenResultObj = hiddenResultUnpacker.getHiddenResult();
                                        if (hiddenResultObj.getActionTeam() != -1 && !hiddenResultObj.getAction().equals(HiddenResultUnpacker.NO_ACTION)) {
                                            allHiddenResult.add(hiddenResultObj);
                                        }
                                        hiddenResult = "";
                                    }
                                }
                                Log.i("RISULTATO", result);
                                for (HiddenResult currentHiddenResult : allHiddenResult
                                        ) {
                                   // Log.i("PLAYER", currentHiddenResult.getPlayerName());
                                   // Log.i("TIME", currentHiddenResult.getTime());
                                   // Log.i("ACTION", currentHiddenResult.getAction());
                                   // Log.i("TEAM", String.valueOf(currentHiddenResult.getActionTeam()));
                                    if (currentHiddenResult.getAction().equals("Goal")) {
                                   //     Log.i("TEAM", String.valueOf(currentHiddenResult.getResult()));
                                    }
                                //    Log.i("----------", "-------------");
                                }
                                palimpsestMatch.setResultTime(resultTime);
                                result = result.replace(":", " ");
                                StringTokenizer resultToken = new StringTokenizer(result);
                                String homeResult = resultToken.nextToken();
                                String awayResult = resultToken.nextToken();
                                palimpsestMatch.setHomeResult(homeResult);
                                palimpsestMatch.setAwayResult(awayResult);
                                palimpsestMatch.setAllHiddenResult(allHiddenResult);

                            }
                        }
                        allPalimpsestMatch.add(palimpsestMatch);
                    }
                }
            }
        }
        Log.i("RESPONSE",response);
        return allPalimpsestMatch;
    }

    private boolean isNumber(String word){
        try{
            long number = Long.parseLong(word);
            return true;
        }
        catch(Exception e){
            return false;
        }
    }
}
