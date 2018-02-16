package com.bettypower.unpacker;

import com.bettypower.entities.HiddenResult;
import com.bettypower.entities.PalimpsestMatch;
import com.bettypower.entities.ParcelablePalimpsestMatch;
import com.bettypower.entities.ParcelableTeam;
import com.bettypower.entities.Team;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by giuliopettenuzzo on 24/06/17.
 * pircing from goalserve,
 */
//TODO inizializza tutto il procedimento nel costruttore e poi con i getter e setter ricava tutto
    //servono dei metodi ausiliari

public class goalServeUnpacker implements Unpacker {

    private ArrayList<Team> allTeams = new ArrayList<>();
    private ArrayList<PalimpsestMatch> allMatches = new ArrayList<>();

    private ArrayList<Team> allHomeTeams = new ArrayList<>();
    private ArrayList<Team> allAwayTeams = new ArrayList<>();

    private ArrayList<String> allHiddenResultOnHtml = new ArrayList<>();

    private String response;

    public goalServeUnpacker(String response){
        this.response = response;
        String[][] allMatchInHtml = getAllMatchesInHtml();
        allMatchInHtml.clone();
        for(int i = 0; i<allMatchInHtml.length;i++){
            if(allMatchInHtml[i][0]!=null) {
                ArrayList<HiddenResult> allHiddenResult = new ArrayList<>();
                for(int j = 1;j<allMatchInHtml.length;j++){
                    if(allMatchInHtml[i][j]!=null){
                        HiddenResultUnpacker unpackHiddenResult = new HiddenResultUnpacker(allMatchInHtml[i][j]);
                        allHiddenResult.add(unpackHiddenResult.getHiddenResult());
                    }
                    else{
                        break;
                    }
                }
                String currentTeam = allMatchInHtml[i][0];
                Team homeTeam = getHomeTeam(currentTeam);
                //remove the space from the beginning of the word if they exist
                    while (homeTeam.getName().startsWith(" ")) {
                        homeTeam.setName(homeTeam.getName().substring(1));
                    }
                Team awayTeam = getAwayTeam(currentTeam);
                    while (awayTeam.getName().startsWith(" ")) {
                        awayTeam.setName(awayTeam.getName().substring(1));
                    }
                String time = getTime(currentTeam);
                    if (time.compareTo("FT") == 0) {
                        time = "Termin.";
                    }
                String result = getResult(currentTeam);
                PalimpsestMatch match = new ParcelablePalimpsestMatch(homeTeam, awayTeam);
                match.setTime(time);
                match.setAwayResult(getAwayScore(result));
                match.setHomeResult(getHomeScore(result));
                match.setAllHiddenResult(allHiddenResult);
                allMatches.add(match);
                allHomeTeams.add(homeTeam);
                allAwayTeams.add(awayTeam);
            }
            else{
                break;
            }
        }
        //allMatches.get(3).getNumberOfHiddenResult();
    }

    @Override
    public ArrayList<Team> getAllTeams() {
        for (Team currTeam:allHomeTeams
             ) {
            allTeams.add(currTeam);
        }
        for (Team currTeam:allAwayTeams
             ) {
            allTeams.add(currTeam);
        }
        return allTeams;
    }

    @Override
    public ArrayList<PalimpsestMatch> getAllMatches() {
        return allMatches;
    }


    /**
     * serve un metodo per estrarre tutti i match in html e uno per estrarre tutte le informazioni da quello specifico match in html
     */
    /**
     * this method give back all the matche in html format
     * ritorna in una matrice tutte le informazioni di tutti i match la prima riga rappresenta la squadra
     * ogni colonna rappresenta i risultati nascosti della squadra nella prima riga.
     * @return an array list that will be pircing
     */
    private String[][] getAllMatchesInHtml(){
        String[][] allMatchesInHtml = new String[1000][1000];
        String matchInString = "";
        String word = "";
        String singleHidden = "";
        StringTokenizer tokenizer = new StringTokenizer(response);
        int i = 0;

        while(tokenizer.hasMoreTokens()){
                if(!word.startsWith("style=\"background:")) {
                word = tokenizer.nextToken();
                }
                if(word.startsWith("style=\"background:")){
                    word = tokenizer.nextToken();
                    while(word.compareTo("</tr>")!=0){
                        matchInString = matchInString + " " + word;
                        word = tokenizer.nextToken();
                        if(word.compareTo("</tr>")==0){
                            allMatchesInHtml[i][0]  = matchInString;
                            matchInString = "";
                            i++;
                        }
                    }
                }
                int j = 1;
                while(!word.startsWith("style=\"background:")&&tokenizer.hasMoreTokens()) {
                    word = tokenizer.nextToken();
                    if(word.compareTo("class=\"hidden-result\"")==0){
                        while (word.compareTo("</tr>") != 0) {
                            singleHidden = singleHidden + " " + word;
                            word = tokenizer.nextToken();
                            if (word.compareTo("</tr>") == 0) {
                                allMatchesInHtml[i-1][j] = singleHidden;
                                j++;
                                singleHidden = "";
                            }
                        }
                    }
                }
        }

        return allMatchesInHtml;
    }

    /**
     * this method is able to sort out the homeTeam from a match in html
     * @return the home team in the match in html
*/
    private Team getHomeTeam(String oneMatchResponse){
        //title="Football" />  Team Name</td>
        String teamName = "";
        Team homeTeam;
        StringTokenizer tokenizer = new StringTokenizer(oneMatchResponse);
        while(tokenizer.hasMoreTokens()){
            String esamination = tokenizer.nextToken();
            if(esamination.compareTo("title=\"Football\"")==0){
                esamination = tokenizer.nextToken();
                if(esamination.compareTo("/>")==0){
                    String word = tokenizer.nextToken();    //Team...
                    if(word.endsWith("</td>")){
                        int wordLength = word.length() - 5; // -5 is size of </td>
                        word = word.substring(0,wordLength);
                        if(!word.isEmpty()) {
                            teamName = word;
                            homeTeam = new ParcelableTeam(teamName);
                            return homeTeam;
                        }
                    }

                    while(!word.endsWith("</td>")){
                        teamName = teamName + " " + word;
                        word = tokenizer.nextToken();       // Name</td>
                        if(word.endsWith("</td>")){
                            int wordLength = word.length() - 5; // -5 is size of </td>
                            word = word.substring(0,wordLength); //Name
                            if(!word.isEmpty()) {
                                teamName = teamName + " " + word;
                                homeTeam = new ParcelableTeam(teamName);
                                return homeTeam;
                            }
                        }
                    }
                }
            }
        }
            return new ParcelableTeam("error");
    }

    private Team getAwayTeam(String oneMatchResponse){
        //width="160px">Almagro <img src
        String teamName = "";
        Team awayTeam;
        StringTokenizer tokenizer = new StringTokenizer(oneMatchResponse);
        while(tokenizer.hasMoreTokens()){
            String esamination = tokenizer.nextToken();
            if(esamination.startsWith("width=\"160px\">")){
                String word = esamination.substring(14);
                while(word.compareTo("<img")!=0){
                    teamName = teamName + " " + word;
                    word = tokenizer.nextToken();
                    if (word.compareTo("<img")==0){
                        awayTeam = new ParcelableTeam(teamName);
                        return awayTeam;
                    }
                }
            }
        }
        return new ParcelableTeam("error");
    }

    private String getTime(String oneMatchResponse){
        //<td width="33" style="background:dedfde;">18:00</td>
        String time = "";
        StringTokenizer tokenizer = new StringTokenizer(oneMatchResponse);
        while(tokenizer.hasMoreTokens()){
            String esamination = tokenizer.nextToken();
            if(esamination.compareTo("<td")==0&&tokenizer.nextToken().compareTo("width=\"33\"")==0){
                String word = tokenizer.nextToken();    //style="background:dedfde;">18:00</td>
                word = word.substring(0,word.length()-5);   //style="background:dedfde;">18:00
                char[] charArray = word.toCharArray();
                int firstIndex = 0;
                for(int current = 0; current<charArray.length;current++){
                    if(charArray[current]=='>'){
                        firstIndex = current+1;
                        word = word.substring(firstIndex);
                        return word;
                    }
                }
            }
        }
        return "error";
    }

    private String getResult(String oneMatchResponse){
        //width="30px">[?-?]</td>
        String result = "";
        StringTokenizer tokenizer = new StringTokenizer(oneMatchResponse);
        while(tokenizer.hasMoreTokens()){
            String word = tokenizer.nextToken();
            if(word.startsWith("width=\"30px\">[")){
                word = word.substring(0,word.length()-5);   //width="30px">[?-?]
                word = word.substring(13);      //[?-?]
                return word;
            }
        }
        return "error";
    }

    private String getAwayScore(String result){     //result =  [?-?]
        String score = result.substring(1,result.length()-1);   // ?-?
        char[] charArray = score.toCharArray();
        int indexOfSeparator = 0; //index of -
        for(int current = 0; current<charArray.length;current++){
            if(charArray[current]=='-'){
                indexOfSeparator = current;
                score = score.substring(indexOfSeparator+1); //the score is suddenly after the separator
                return score;
            }
        }
        return "error";
    }
    private String getHomeScore(String result){     //result =  [?-?]
        String score = result.substring(1,result.length()-1);   // ?-?
        char[] charArray = score.toCharArray();
        int indexOfSeparator = 0; //index of -
        for(int current = 0; current<charArray.length;current++){
            if(charArray[current]=='-'){
                indexOfSeparator = current;
                score = score.substring(0,indexOfSeparator); //the score is suddenly after the separator
                return score;
            }
        }
        return "error";
    }

}
