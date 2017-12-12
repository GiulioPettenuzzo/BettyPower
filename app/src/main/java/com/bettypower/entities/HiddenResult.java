package com.bettypower.entities;

/**
 * Created by giuliopettenuzzo on 28/06/17.
 */

public interface HiddenResult{
    public String getPlayerName();
    public void setPlayerName(String name);

    //yellowcard - redCard - goal
    public String getAction();
    public void setAction(String actionName);

    /**
     * the time of the action
     * @return
     */
    public String getTime();
    public void setTime(String time);

    //in this case the result is grouped into an only textView
    public String getResult();
    public void setResult(String result);

    /**
     * @return 0 if homeTeam, 1 if awayTeam.
     */
    public int getActionTeam();
    public void setActionTeam(int numberIdentifier);
}
