package com.bettypower.entities;

import android.os.Parcelable;

/**
 * Created by giuliopettenuzzo on 28/06/17.
 */

public interface HiddenResult extends Parcelable{
    String getPlayerName();
    void setPlayerName(String name);

    //yellowcard - redCard - goal
    String getAction();
    void setAction(String actionName);

    /**
     * the time of the action
     * @return
     */
    String getTime();
    void setTime(String time);

    //in this case the result is grouped into an only textView
    String getResult();
    void setResult(String result);

    /**
     * @return 0 if homeTeam, 1 if awayTeam.
     */
    int getActionTeam();
    void setActionTeam(int numberIdentifier);
}
