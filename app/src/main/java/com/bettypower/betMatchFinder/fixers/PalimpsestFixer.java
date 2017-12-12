package com.bettypower.betMatchFinder.fixers;

import com.bettypower.betMatchFinder.labelSet.SeparatorLabelSet;

/**
 * created in order to remove the ". - / _ \ | , ~ ' : " from the palimpsest code
 * Created by giuliopettenuzzo on 24/10/17.
 */

public class PalimpsestFixer implements Fixer {

    private String palimpsest;

    @Override
    public void setWord(String palimpsest) {
        this.palimpsest = palimpsest;
    }

    @Override
    public String getFixedWord() {
        String fixedPalimpsest = "";
        if(isNumber(palimpsest)){
            return palimpsest;
        }
        SeparatorLabelSet separatorLabelSet = new SeparatorLabelSet();
        for (String currentSeparator:separatorLabelSet.getAllPalimpsestSeparator()
             ) {
            if(palimpsest.contains(currentSeparator)){
                if(palimpsest.endsWith(currentSeparator)){
                    fixedPalimpsest = palimpsest.replace(currentSeparator,"");
                    palimpsest = fixedPalimpsest;
                }
                if(palimpsest.indexOf(currentSeparator)==palimpsest.lastIndexOf(currentSeparator)){
                    fixedPalimpsest = palimpsest.replace(currentSeparator,"");
                    palimpsest = fixedPalimpsest;
                }
            }
        }
        return fixedPalimpsest;
    }

    /**
     * check if the word given in param is a number or not
     * @param word to examinate
     * @return true if word is a number, false otherwise
     */
    public boolean isNumber(String word){
        try{
            long wordToLong = Long.parseLong(word);
            return true;
        }catch(Exception e){
            return false;
        }
    }
}
