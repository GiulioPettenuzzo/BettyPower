package com.bettypower;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bettypower.entities.HiddenResult;
import com.renard.ocr.R;

/**
 * Created by giuliopettenuzzo on 30/06/17.
 */

public class HiddenResultLayout extends RelativeLayout {
    Context context;
    TextView homePlayer;
    HiddenResult hiddenResult;
    View view;
    TextView awayPlayer;
    TextView result;
    TextView time;
    ImageView homeAction;
    ImageView awayAction;

    ConstraintLayout constraintLayout;

    private static final int HOME_TEAM = 0;
    private static final int AWAY_TEAM = 1;
    private static final int ERROR_TEAM = -1;

    public HiddenResultLayout(Context context,HiddenResult hiddenResult){
        super(context);
        this.context = context;
        this.hiddenResult = hiddenResult;
        LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        switch (hiddenResult.getActionTeam()){
            case HOME_TEAM:
                view = layoutInflater.inflate(R.layout.item_hidden_result_home, this, true);
                homeAction = (ImageView) findViewById(R.id.home_action);
                homePlayer = (TextView) findViewById(R.id.home_player);
                result = (TextView) findViewById(R.id.action_result);
                time = (TextView) findViewById(R.id.action_time);
                break;
            case AWAY_TEAM:
                view = layoutInflater.inflate(R.layout.item_hidden_result_away, this, true);
                awayAction = (ImageView) findViewById(R.id.away_action);
                awayPlayer = (TextView) findViewById(R.id.away_player);
                result = (TextView) findViewById(R.id.action_result);
                time = (TextView) findViewById(R.id.action_time);
                break;
            case ERROR_TEAM:
                break; //TODO
        }
    }
    public void setGoalHiddenResult(){
        time.setText(hiddenResult.getTime());
        result.setText(hiddenResult.getResult());
       // result.setVisibility(GONE);
        if (hiddenResult.getActionTeam() == HOME_TEAM){
            homeAction.setImageDrawable(context.getResources().getDrawable(R.drawable.goal_ball));
            homePlayer.setText(hiddenResult.getPlayerName());
        }
        else{
            awayAction.setImageDrawable(context.getResources().getDrawable(R.drawable.goal_ball));
            awayPlayer.setText(hiddenResult.getPlayerName());
        }
    }
    public void setYellowCardHiddenResult(){
        result.setVisibility(GONE);
        time.setText(hiddenResult.getTime());
        if (hiddenResult.getActionTeam() == HOME_TEAM){
            homeAction.setImageDrawable(context.getResources().getDrawable(R.drawable.yellow_card));
            homePlayer.setText(hiddenResult.getPlayerName());
        }
        else{
            awayAction.setImageDrawable(context.getResources().getDrawable(R.drawable.yellow_card));
            awayPlayer.setText(hiddenResult.getPlayerName());
        }
    }
    //doesn't work
    public void setRedCardHiddenResult(){
        result.setVisibility(GONE);
        time.setText(hiddenResult.getTime());
        if (hiddenResult.getActionTeam() == HOME_TEAM){
            homeAction.setImageDrawable(context.getResources().getDrawable(R.drawable.red_card));
            homePlayer.setText(hiddenResult.getPlayerName());
        }
        else{
            awayAction.setImageDrawable(context.getResources().getDrawable(R.drawable.red_card));
            awayPlayer.setText(hiddenResult.getPlayerName());
        }
    }
    public TextView getHomPlayer(){
        return homePlayer;
    }
}
