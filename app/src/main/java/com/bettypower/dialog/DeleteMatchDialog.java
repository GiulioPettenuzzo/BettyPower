package com.bettypower.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bettypower.entities.Match;
import com.bettypower.entities.PalimpsestMatch;
import com.renard.ocr.R;

import java.util.StringTokenizer;

/**
 * Created by giuliopettenuzzo on 22/01/18.
 */

public class DeleteMatchDialog extends Dialog {

    private Context context;
    private PalimpsestMatch match;
    private DeleteMatchDialogListener deleteMatchDialogListener;

    private Button cancelButton;
    private Button confirmButton;
    private TextView title;

    public DeleteMatchDialog(Context context, PalimpsestMatch match){
        super(context);
        this.context = context;
        this.match = match;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_delate_match);
        cancelButton = (Button) findViewById(R.id.delate_dialog_cancel_button);
        confirmButton = (Button) findViewById(R.id.delate_dialog_confirm_button);
        title = (TextView) findViewById(R.id.delete_match_dialog_title);

        String matchName = match.getHomeTeam().getName() + " - " + match.getAwayTeam().getName() +"?";
        StringTokenizer token = new StringTokenizer(matchName);
        String matchNameCapitalized = "";
        while(token.hasMoreTokens()){
            String word = token.nextToken();
            if(matchNameCapitalized.isEmpty())
                matchNameCapitalized = word.substring(0,1).toUpperCase() + word.substring(1).toLowerCase();
            else
                matchNameCapitalized = matchNameCapitalized + " " + word.substring(0,1).toUpperCase() + word.substring(1).toLowerCase();
        }
        title.setText(matchNameCapitalized);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteMatchDialogListener.onDeleteMatchNotConfirm();
            }
        });
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteMatchDialogListener.onDeleteMatchConfirm();
            }
        });
    }

    public void setDeleteMatchDialogListener(DeleteMatchDialogListener deleteMatchDialogListener){
        this.deleteMatchDialogListener = deleteMatchDialogListener;
    }

    public interface DeleteMatchDialogListener{
        void onDeleteMatchConfirm();
        void onDeleteMatchNotConfirm();
    }
}
