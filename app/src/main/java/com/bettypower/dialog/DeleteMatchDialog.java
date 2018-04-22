package com.bettypower.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.bettypower.entities.PalimpsestMatch;
import com.renard.betty.R;

import java.util.StringTokenizer;

/**
 * Created by giuliopettenuzzo on 22/01/18.
 * In order to confirm the elimination of a match in the bet
 */

public class DeleteMatchDialog extends Dialog {

    private PalimpsestMatch match;
    private DeleteMatchDialogListener deleteMatchDialogListener;

    public DeleteMatchDialog(Context context, PalimpsestMatch match){
        super(context);
        this.match = match;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_delate_match);
        Button cancelButton = (Button) findViewById(R.id.delate_dialog_cancel_button);
        Button confirmButton = (Button) findViewById(R.id.delate_dialog_confirm_button);
        TextView title = (TextView) findViewById(R.id.delete_match_dialog_title);

        String matchName = match.getHomeTeam().getName() + " - " + match.getAwayTeam().getName() +"?";
        StringTokenizer token = new StringTokenizer(matchName);
        StringBuilder matchNameCapitalized = new StringBuilder();
        while(token.hasMoreTokens()){
            String word = token.nextToken();
            if(matchNameCapitalized.length() == 0)
                matchNameCapitalized = new StringBuilder(word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase());
            else
                matchNameCapitalized.append(" ").append(word.substring(0, 1).toUpperCase()).append(word.substring(1).toLowerCase());
        }
        title.setText(matchNameCapitalized.toString());
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
