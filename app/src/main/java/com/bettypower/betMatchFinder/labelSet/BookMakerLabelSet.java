package com.bettypower.betMatchFinder.labelSet;

import java.util.ArrayList;

/**
 * Created by giuliopettenuzzo on 29/10/17.
 */

public class BookMakerLabelSet{
    public ArrayList<String> getAllSet() {
        ArrayList<String> allBookMaker = new ArrayList<>();
        allBookMaker.add("matchpoint");
        allBookMaker.add("sisal");
        allBookMaker.add("william hill");
        return allBookMaker;
    }

    public ArrayList<String> getAllVincitaSet(){
        ArrayList<String> result = new ArrayList<>();
        result.add("totale vincita");
        result.add("importo pagamento");
        result.add("vincita potenziale");
        result.add("vincita totale");
        result.add("vincita");
        return result;
    }

    public ArrayList<String> gettAllPuntataSet(){
        ArrayList<String> result = new ArrayList<>();
        result.add("totale scommesso");
        result.add("importo scommesso");
        result.add("scommesso");
        result.add("importo");
        result.add("scommessa");
        return result;
    }
}
