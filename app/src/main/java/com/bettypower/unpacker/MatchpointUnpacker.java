package com.bettypower.unpacker;

import com.bettypower.entities.PalimpsestMatch;
import com.bettypower.entities.ParcelablePalimpsestMatch;
import com.bettypower.entities.ParcelableTeam;
import com.bettypower.entities.Team;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by giuliopettenuzzo on 18/09/17.
 */

//TODO NON FUNZIONA, IL SITO HA UN FORMATO LEGGERMENTE DIVERSO
public class MatchpointUnpacker {

    ArrayList<PalimpsestMatch> allMatchpointMatch;
    String response;

    public MatchpointUnpacker(String response){
        allMatchpointMatch = new ArrayList<>();
        this.response = response;
    }

    public void getCurrentPalimpsestMatch(String response){
        ArrayList<String> allTeams = findTeams(response);
        ArrayList<String> palimpsests = findAllPalimpsest(response);
        ArrayList<String> allTime = findMatchTime(response);
        for (String current:allTeams
                ) {
            Team homeTeam = new ParcelableTeam(findHomeTeam(current));
            Team awayTeam = new ParcelableTeam(findAwayTeam(current));
            String pali = findPalimpsestCode(palimpsests.get(allTeams.lastIndexOf(current)));
            String event = findEventCode(palimpsests.get(allTeams.lastIndexOf(current)));
            String time = allTime.get(allTeams.lastIndexOf(current));
            PalimpsestMatch palimpsestMatch = new ParcelablePalimpsestMatch(homeTeam,awayTeam,pali +event,time);
            allMatchpointMatch.add(palimpsestMatch);
        }
    }

    private ArrayList<String> findTeams(String response){
        //title="SCOMMESSE FINN HARPS FC - GALWAY UNITED">
        ArrayList<String> allTeams = new ArrayList<>();
        StringTokenizer token = new StringTokenizer(response);
        while(token.hasMoreTokens()){
            String word = token.nextToken();
            if(word.endsWith("SCOMMESSE")){
                String team = "";
                while(!word.contains("\">")){
                    word = token.nextToken();
                    team = team + " " + word;
                    if(word.contains("\">")){
                        allTeams.add(team);
                    }
                }
            }
        }
        return allTeams;
    }
    private String findHomeTeam(String singleResponse){
        // FINN HARPS FC - GALWAY UNITED"><strong>FINN
        String homeTeam = "";
        StringTokenizer token = new StringTokenizer(singleResponse);
        String word = token.nextToken();
        homeTeam = homeTeam + word;
        while(!word.equals("-") && token.hasMoreTokens()){
            word = token.nextToken();
            if(word.startsWith("(")&&word.endsWith(")")){
                word = "";
            }
            if(!word.equals("-")){
                homeTeam = homeTeam + " " + word;
            }
        }

        return homeTeam;
    }

    private String findAwayTeam(String singleResponse){
        // FINN HARPS FC - GALWAY UNITED"><strong>FINN
        String awayTeam = "";
        StringTokenizer token = new StringTokenizer(singleResponse);
        while(token.hasMoreTokens()){
            String word = token.nextToken();
            if(word.startsWith("(")&&word.endsWith(")")){
                word = "";
            }
            if(word.equals("-")){
                awayTeam = token.nextToken("\">");
            }
        }
        return awayTeam;
    }

    private String findPalimpsestCode(String singleResponse){
        //SANTOS</strong></a></td><td>27301</td><td>254</td><td
        String palimsest = "";
        StringTokenizer token = new StringTokenizer(singleResponse);
        while(token.hasMoreTokens()){
            String word = token.nextToken(">");
            String numberOnly= word.replaceAll("[^0-9]", "");
            if(!numberOnly.equals("")){
                palimsest = numberOnly;
                break;
            }

        }
        return palimsest;
    }

    private String findEventCode(String singleResponse){
        //SANTOS</strong></a></td><td>27301</td><td>254</td><td
        String eventCode = "";
        StringTokenizer token = new StringTokenizer(singleResponse);
        while(token.hasMoreTokens()){
            String word = token.nextToken(">");
            String numberOnly= word.replaceAll("[^0-9]", "");
            if(!numberOnly.equals("")){
                eventCode = numberOnly;
            }
        }
        return eventCode;
    }

    private ArrayList<String> findAllPalimpsest(String response){
        // </strong></a></td><td>27331</td><td>1587</td><td nowrap>19/08 15:30</td><td
        ArrayList<String> allPalimpsest = new ArrayList<>();
        StringTokenizer token = new StringTokenizer(response);
        while(token.hasMoreTokens()){
            String word = token.nextToken();
            if(word.contains("</strong></a></td><td>")){
                allPalimpsest.add(word);
            }
        }
        return allPalimpsest;
    }

    private ArrayList<String> findMatchTime(String response){
        //</td><td nowrap>19/08 15:30</td><td
        ArrayList<String> allMatchTime = new ArrayList<>();
        StringTokenizer token = new StringTokenizer(response);
        while(token.hasMoreTokens()){
            String word = token.nextToken();
            if(word.endsWith("</td><td")){
                word = token.nextToken();
                if(word.startsWith("nowrap>")){
                    String time = "";
                    word = word.substring(7);
                    time = time + word;
                    while(!word.endsWith("</td><td")){
                        word = token.nextToken();
                        if(word.endsWith("</td><td")){
                            String parseWord = word.substring(0,word.length()-8);
                            time = time + " " + parseWord;
                            allMatchTime.add(time);
                        }
                        else{
                            time = time + " " + word;
                            allMatchTime.add(time);
                        }
                    }
                }
            }
        }
        return allMatchTime;
    }


    public ArrayList<PalimpsestMatch> getAllPalimpsestMatch(){
        getCurrentPalimpsestMatch(response);
        return allMatchpointMatch;
    }
}
