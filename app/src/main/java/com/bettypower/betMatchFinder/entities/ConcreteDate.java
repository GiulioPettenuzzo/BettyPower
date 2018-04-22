package com.bettypower.betMatchFinder.entities;

/**
 * Class created in order to process better the date by the finder,
 * it devide the date in day - month - year and give a string that rappresent the format in which
 * the date were found from ocr text processing
 * Created by giuliopettenuzzo on 26/10/17.
 */

public class ConcreteDate implements Date {
    private String day;
    private String month;
    private String year;

    //this is the format that define the order in witch the date must be read
    private String format;
    private static final String FORMAT_NOT_ALREADY_KNOWN = "not_already_know";
    public static final String FORMAT_DAY_MONTH_YEAR = "day_month_year";
    public static final String FORMAT_MONTH_DAY_YEAR = "month_day_year";
    public static final String FORMAT_DAY_MONTH = "day_month";
    public static final String FORMAT_MONTH_DAY = "month_day";
    public static final String FORMAT_YEAR_MONTH_DAY = "year_month_day";


    public ConcreteDate(){
        this.day = "";
        this.month = "";
        this.year = "";
        this.format = FORMAT_NOT_ALREADY_KNOWN;
    }

    @Override
    public String getDay() {
        return day;
    }

    @Override
    public void setDay(String day) {
        this.day = day;
    }

    @Override
    public String getMonth() {
        return month;
    }

    @Override
    public void setMonth(String month) {
        this.month = month;
    }

    @Override
    public String getYear() {
        return year;
    }

    @Override
    public void setYear(String year) {
        this.year = year;
    }

    @Override
    public String getInitialFormat() {
        return format;
    }

    @Override
    public void setInitialFormat(String format) {
        this.format = format;
    }

    @Override
    public String toString() {
        String result = "";
        if(day != null){
            result = result + day + "/";
        }
        if(month != null){
            result = result + month;// + "/";
        }
        /*
        if(year != null){
            result = result + year;
        }*/
        return result;
    }
}
