package com.bettypower.unpacker;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by giuliopettenuzzo on 31/07/17.
 */

public class AllMatchesByPalimpsestURLUnpacker {

    String response;

    public AllMatchesByPalimpsestURLUnpacker(String response){
        this.response = response;
    }

    public ArrayList<String> getAllURL(){
        ArrayList<String> allURL = new ArrayList<>();
        StringTokenizer token = new StringTokenizer(response);
        while(token.hasMoreTokens()){
            String word = token.nextToken(" ");
            if(word.endsWith("Numero")){
                word = token.nextToken(" ");
                if(word.equals("palinsesto")){
                    word = token.nextToken("<");
                    allURL.add(word);
                }
            }
        }

        return  allURL;
    }

}
