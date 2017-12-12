package com.bettypower.unpacker;

/**
 * Created by giuliopettenuzzo on 20/07/17.
 */

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
    This class is able to pick up the response from fishtagram server and take it into an array list of string
 witch contains the url of all the images
 */
public class LogoUnpacker {

    private String response;
    private ArrayList<String> allLogos = new ArrayList<>();

    public LogoUnpacker(String response){
        this.response = response;
        allLogos = setAllLogos();
    }

    //<img src="http://fishtagram.it/football_logos/barcellona.png" alt="barcellona">
    private ArrayList<String> setAllLogos(){
        ArrayList<String> allLogos = new ArrayList<>();
        StringTokenizer token = new StringTokenizer(response);
        while(token.hasMoreTokens()){
            String word = token.nextToken();
            if(word.startsWith("src=\"")){
                word = word.substring(5,word.length()-1);
                allLogos.add(word);
            }
        }
        return allLogos;
    }

    public ArrayList<String> getAllLogos(){
        return allLogos;
    }
}
