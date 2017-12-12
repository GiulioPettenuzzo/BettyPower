package com.bettypower.unpacker;

import com.bettypower.entities.HiddenResult;
import com.bettypower.entities.ParcelableHiddenResult;

import java.util.StringTokenizer;

/**
 * Created by giuliopettenuzzo on 28/06/17.
 * This class take the HiddenResult from goalserve
 */

public class HiddenResultUnpacker {

    private String response;

    private static final String ACTION_GOAL = "\"Goal\"";
    private static final String ACTION_YELLOWCARD = "\"Yellowcard\"";
    private static final String ACTION_REDCARD = "\"Redcard\"";
    private static final String NO_ACTION = "no_action";


    public HiddenResultUnpacker(String response){
        this.response = response;
    }
    public HiddenResult getHiddenResult(){
        //TODO controllo no_action
        String player = "home player";
        if(findActionTeam() == 1){
            player = findAwayPlayer();
        }
        else if(findActionTeam() == 0){
            player = findHomePlayer();
        }
        ParcelableHiddenResult hiddenResult =  new ParcelableHiddenResult(player,findActionTime(),findAction(),findActionTeam());
        if(findAction().compareTo("Goal")==0){
            hiddenResult.setResult(findResult());
        }
        return hiddenResult;
    }

    /**
     *
     * @return the name of the action or the string no_action otherwise
     */
    private String findAction(){
        String action = "";
        StringTokenizer tokenizer = new StringTokenizer(response);
        while(tokenizer.hasMoreTokens()){
            String word = tokenizer.nextToken();
            if(word.endsWith(ACTION_GOAL) ||
                    word.endsWith(ACTION_REDCARD) ||
                    word.endsWith(ACTION_YELLOWCARD)){
                char[] charArray = word.toCharArray();
                int index = 0;//index of '"'
                for(int currentChar = 0; currentChar<charArray.length;currentChar++){
                    if(charArray[currentChar]=='"'){
                        index = currentChar;
                        break;
                    }
                }
                action = word.substring(index+1,word.length()-1);
                return action;
            }
            else{
                action = NO_ACTION;
            }
        }
        return action;
    }

    private String findActionTime(){
        String time = "";
        StringTokenizer tokenizer = new StringTokenizer(response,">");
        while (tokenizer.hasMoreTokens()){
            String word = tokenizer.nextToken();
            if(word.endsWith("</td")){
                word = word.substring(0,word.length()-4);
                try{
                    int num = Integer.parseInt(word);
                    return word+"'";
                }
                catch(Exception e){
                    time = "error";
                }
            }
        }
        return time;
    }

    private int findActionTeam(){
        int actionTeam = -1;

        StringTokenizer tokenizer = new StringTokenizer(response,">");
        while (tokenizer.hasMoreTokens()){
            String word = tokenizer.nextToken();
            if(word.endsWith("</td")){
                word = word.substring(0,word.length()-4);
                try{
                    int num = Integer.parseInt(word);
                    if(tokenizer.hasMoreTokens()){
                        return 0;
                    }
                    else{
                        return 1;
                    }
                }
                catch(Exception e){
                }
            }
        }

        return actionTeam;
    }

    public String findHomePlayer(){
        String homePlayer = "";

        StringTokenizer tokenizer = new StringTokenizer(response);
        while(tokenizer.hasMoreTokens()){
            String word = tokenizer.nextToken();
            if(word.compareTo("title=\"Goal\"")==0||
                    word.compareTo("title=\"yellowcard\"")==0||
                    word.compareTo("title=\"Redcard\"")==0){
                word = tokenizer.nextToken();
                    if(word.compareTo("/>")==0){
                        while(!word.endsWith("</td>")&&tokenizer.hasMoreTokens()){
                            word = tokenizer.nextToken();
                            if(word.endsWith("</td>")){
                                word = word.substring(0,word.length()-5);
                                homePlayer = homePlayer + " " + word;
                                return homePlayer;
                            }
                            homePlayer = homePlayer + " " +word;
                    }
                }
            }
        }

        return  homePlayer;
    }

    public String findAwayPlayer(){
        //width="150">T. Kajiyama <img src="/images/football.gif"
        String awayPlayer = "";
        String firstPart = "";
        StringTokenizer tokenizer = new StringTokenizer(response);
        boolean isResult = false;
        while(tokenizer.hasMoreTokens()){
            //String word = tokenizer.nextToken();  //right word
            String toCompare = tokenizer.nextToken();   //for find the right word
            if(toCompare.compareTo("<img")==0){
                isResult = true;
                break;
            }
            else{
                firstPart = firstPart + " " + toCompare;
            }
        }
        if(isResult==false){
            return "no_result";
        }

        StringTokenizer token = new StringTokenizer(firstPart);
        while(token.hasMoreTokens()){
            String word = token.nextToken();
            if((word.startsWith("width=\"150\">"))&&!word.endsWith("</td>")){
                word = word.substring(12);
                awayPlayer = awayPlayer + " " + word;
                while(token.hasMoreTokens()) {
                    word = token.nextToken();
                    awayPlayer = awayPlayer + " " + word;
                }
            }
        }
        return awayPlayer;
    }

    private String findResult(){
        //width="30">[1-1]</td>
        String result = "";
        StringTokenizer tokenizer = new StringTokenizer(response);
        while(tokenizer.hasMoreTokens()){
            String word = tokenizer.nextToken();
            if(word.startsWith("width=\"30\">[")&&word.endsWith("]</td>")){
                word = word.substring(11,word.length()-5);
                result = word;
            }
        }
        char[] charArray = result.toCharArray();
        int index;
        for(int currentChar = 0;currentChar<result.length();currentChar++){
            if(charArray[currentChar]=='-'){
                charArray[currentChar] = ':';
            }
        }
        result = String.copyValueOf(charArray);
        return result;
    }
}
