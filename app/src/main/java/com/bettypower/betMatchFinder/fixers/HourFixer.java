package com.bettypower.betMatchFinder.fixers;

import com.bettypower.betMatchFinder.labelSet.HourLabelSet;
import com.bettypower.betMatchFinder.labelSet.SeparatorLabelSet;

import java.util.StringTokenizer;

/**
 * class that return tthe hour in the right format if word is a hour, null otherwise
 * Created by giuliopettenuzzo on 29/10/17.
 */

public class HourFixer implements Fixer {

    private HourLabelSet hourLabelSet;
    private String word;
    private String hourFixed;
    private String minuteFixed;

    private static final int HOUR_WORD_NUMBER = 2;

    public HourFixer(){
        hourLabelSet = new HourLabelSet();
    }

    @Override
    public void setWord(String word) {
        this.word = word;
    }

    @Override
    public String getFixedWord() {
        SeparatorLabelSet separatorLabelSet = new SeparatorLabelSet();
        for (String currentSeparator:separatorLabelSet.getAllHourSeparator()
             ) {
            StringTokenizer token = new StringTokenizer(word,currentSeparator);
            if(token.countTokens()==HOUR_WORD_NUMBER){
                String firstWord = token.nextToken();
                String secondWord = token.nextToken();
                if(isHour(firstWord) && isMinutes(secondWord)){
                    return hourFixed + ":" + minuteFixed;
                }
            }
        }
        return null;
    }

    private boolean isHour(String word){
        for (String currentKey:hourLabelSet.getAllHour().keySet()
             ) {
            for (String currentValue:hourLabelSet.getAllHour().get(currentKey)
                 ) {
                if(currentValue.equalsIgnoreCase(word)){
                    hourFixed = currentKey;
                    return true;
                }
            }
        }
        hourFixed = null;
        return false;
    }

    private boolean isMinutes(String word){
        for (String currentKey:hourLabelSet.getAllMinutes().keySet()
                ) {
            for (String currentValue:hourLabelSet.getAllMinutes().get(currentKey)
                    ) {
                if(currentValue.equalsIgnoreCase(word)){
                    minuteFixed = currentKey;
                    return true;
                }
            }
        }
        minuteFixed = null;
        return false;
    }
}
