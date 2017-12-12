package com.bettypower.betMatchFinder.fixers;

import com.bettypower.betMatchFinder.entities.ConcreteDate;
import com.bettypower.betMatchFinder.entities.Date;
import com.bettypower.betMatchFinder.labelSet.DateLabelSet;
import com.bettypower.betMatchFinder.labelSet.SeparatorLabelSet;

import java.util.StringTokenizer;

/**
 * this class is alowed to recognize if a word is a date and it can return
 * the date in the format you want
 * Created by giuliopettenuzzo on 25/10/17.
 */
public class DateFixer implements Fixer {

    private String word;
    private DateLabelSet dateLabelSet;
    private String fixDay;
    private String fixMonth;
    private String fixYear;

    private static final int FORMAT_TWO_WORDS = 2;
    private static final int FORMAT_THREE_WORDS = 3;

    private static final char DEFAULT_SEPARATOR = '/';

    public DateFixer(){
        dateLabelSet = new DateLabelSet();
    }

    @Override
    public void setWord(String word) {
        this.word = word;
    }

    @Override
    public String getFixedWord() {
        Date date = getDateFixedIfPresent(word);
        if (date != null) {
            return date.toString();
        }
        return null;
    }

    public Date getDateFixedIfPresent(String word){
        Date date = new ConcreteDate();
        SeparatorLabelSet separatorLabelSet = new SeparatorLabelSet();
        for (String currentSeparator:separatorLabelSet.getAllDateSeparator()
                ) {
            if((word.contains(currentSeparator)&&!isNumber(currentSeparator)) || (!replaceNumberWithSeparator(word,currentSeparator).equalsIgnoreCase(word))) {
                String wordToProcess = replaceNumberWithSeparator(word,currentSeparator);
                if(!wordToProcess.equalsIgnoreCase(word))
                    currentSeparator = String.valueOf(DEFAULT_SEPARATOR);
                StringTokenizer token = new StringTokenizer(wordToProcess, currentSeparator);
                if (token.countTokens() == FORMAT_THREE_WORDS) {
                    String firstWord = token.nextToken();
                    String secondWord = token.nextToken();
                    String thirdWord = token.nextToken();
                    if (isDay(firstWord) && isMonth(secondWord) && isYear(thirdWord)) {
                        date.setDay(fixDay);
                        date.setMonth(fixMonth);
                        date.setYear(fixYear);
                        date.setInitialFormat(ConcreteDate.FORMAT_DAY_MONTH_YEAR);
                        return date;
                    } else if (isDay(secondWord) && isMonth(firstWord) && isYear(thirdWord)) {
                        date.setDay(fixDay);
                        date.setMonth(fixMonth);
                        date.setYear(fixYear);
                        date.setInitialFormat(ConcreteDate.FORMAT_MONTH_DAY_YEAR);
                        return date;
                    } else if (isDay(thirdWord) && isMonth(secondWord) && isYear(firstWord)) {
                        date.setDay(fixDay);
                        date.setMonth(fixMonth);
                        date.setYear(fixYear);
                        date.setInitialFormat(ConcreteDate.FORMAT_YEAR_MONTH_DAY);
                        return date;
                    }
                } else if (token.countTokens() == FORMAT_TWO_WORDS) {
                    String neWord = resolveIfSecondSeparatorIsMissing(word,currentSeparator);
                    StringTokenizer tokenForTwoWord = new StringTokenizer(neWord,currentSeparator);
                    if(tokenForTwoWord.countTokens() == FORMAT_TWO_WORDS) {
                        String firstWord = tokenForTwoWord.nextToken();

                        String secondWord = tokenForTwoWord.nextToken();

                        if (isDay(firstWord) && isMonth(secondWord)) {
                            date.setDay(fixDay);
                            date.setMonth(fixMonth);
                            date.setYear(dateLabelSet.getCurrentYear());
                            date.setInitialFormat(ConcreteDate.FORMAT_DAY_MONTH);
                            return date;
                        } else if (isDay(secondWord) && isMonth(firstWord)) {
                            date.setDay(fixDay);
                            date.setMonth(fixMonth);
                            date.setYear(dateLabelSet.getCurrentYear());
                            date.setInitialFormat(ConcreteDate.FORMAT_MONTH_DAY);
                            return date;
                        }
                    }
                }
                break;
            }
        }
        return null;
    }

    private String resolveIfSecondSeparatorIsMissing(String word, String separator){
        StringTokenizer token = new StringTokenizer(word,separator);
        if(token.countTokens() == FORMAT_TWO_WORDS) {
            String firstWord = token.nextToken(); //no condition here
            String secondWord = token.nextToken();
            for (String currentKey : dateLabelSet.getAllYear().keySet()
                    ) {
                for (String currentYear : dateLabelSet.getAllYear().get(currentKey)
                        ) {
                    if (secondWord.endsWith(currentYear)) {
                        String secondWordFixed = secondWord.substring(0, secondWord.length() - currentYear.length());
                        if (isMonth(secondWordFixed)) {
                            return firstWord + separator + secondWordFixed;
                        }
                    }
                }
            }
        }
        return word;
    }

    /**
     * if the ocr read the numbers instead of separator this method recognize it and replace those numbers
     * with a casual separator '/'
     * after the calling of this method we have a string with the correct separator so we can process the date normally
     * @param wordWithNumber the original word whith numbers instead of separator
     * @return the number with the correct separator if is param the case,  otherwise the original string
     */
    private String replaceNumberWithSeparator(String wordWithNumber, String separator){
        if(isNumber(separator)) {
            if (wordWithNumber.charAt(1) == separator.charAt(0) && wordWithNumber.charAt(4) == separator.charAt(0)){
                char[] res = wordWithNumber.toCharArray();
                res[1] = DEFAULT_SEPARATOR;
                res[4] = DEFAULT_SEPARATOR;
                return String.valueOf(res);
            }
            // the condition >=6 is not usefull in the first if condition thanks to the divider
            if (wordWithNumber.length()>=6 &&wordWithNumber.charAt(2) == separator.charAt(0) && wordWithNumber.charAt(5) == separator.charAt(0)){
                char[] res = wordWithNumber.toCharArray();
                res[2] = DEFAULT_SEPARATOR;
                res[5] = DEFAULT_SEPARATOR;
                return String.valueOf(res);
            }
        }
        return wordWithNumber;
    }

    /**
     * when the day is found the apposite variable fixday is set to his value
     * @param word the word to be examinated
     * @return true if the day is found false otherwise
     */
    private boolean isDay(String word){
        for (String currentKey:dateLabelSet.getAllDay().keySet()
             ) {
            for (String currentDay:dateLabelSet.getAllDay().get(currentKey)
                 ) {
                if(word.equalsIgnoreCase(currentDay)){
                    fixDay = currentKey;
                    return true;
                }
            }
        }
        fixDay = null;
        return false;
    }

    /**
     * when the month is found the apposite variable fixmonth is set to his value
     * @param word the word to be examinated
     * @return true if the month is found false otherwise
     */
    private boolean isMonth(String word){
        for (String currentKey: dateLabelSet.getAllMonth().keySet()
             ) {
            for (String currentMonth:dateLabelSet.getAllMonth().get(currentKey)
                 ) {
                if(word.equalsIgnoreCase(currentMonth)){
                    fixMonth = currentKey;
                    return true;
                }
            }
        }
        fixMonth = null;
        return false;
    }

    /**
     * when the year is found the apposite variable fixyear is set to his value
     * @param word the word to be examinated
     * @return true if the year is found false otherwise
     */
    private boolean isYear(String word){
        for (String currentKey: dateLabelSet.getAllYear().keySet()
                ) {
            for (String currentYear:dateLabelSet.getAllYear().get(currentKey)
                    ) {
                if(word.equalsIgnoreCase(currentYear)){
                    fixYear = currentKey;
                    return true;
                }
            }
        }
        fixYear = null;
        return false;
    }

    /**
     * check if the word given in param is a number or not
     * @param word to examinate
     * @return true if word is a number, false otherwise
     */
    private boolean isNumber(String word){
        try{
            //noinspection ResultOfMethodCallIgnored
            Long.parseLong(word);
            return true;
        }catch(Exception e){
            return false;
        }
    }
}
