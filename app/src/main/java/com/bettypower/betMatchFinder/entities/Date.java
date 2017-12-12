package com.bettypower.betMatchFinder.entities;

/**
 * Created by giuliopettenuzzo on 26/10/17.
 */

public interface Date {

    String getDay();
    void setDay(String date);

    String getMonth();
    void setMonth(String month);

    String getYear();
    void setYear(String year);

    String getInitialFormat();
    void setInitialFormat(String format);
}
