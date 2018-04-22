package com.bettypower.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.renard.betty.R;

/**
 * Created by giuliopettenuzzo on 07/02/18.
 * In order to confirm the elimination of a bet
 */

public class DeleteBetDialog extends Dialog {

    private DeleteBetDialogListener mDeleteBetDialogListener;

    /**
     * Creates a dialog window that uses the default dialog theme.
     * <p>
     * The supplied {@code context} is used to obtain the window manager and
     * base theme used to present the dialog.
     *
     * @param context the context in which the dialog should run
     */
    public DeleteBetDialog(@NonNull Context context, DeleteBetDialogListener deleteBetDialogListener, int numberDocument) {
        super(context);
        setContentView(R.layout.dialog_delate_match);
        this.mDeleteBetDialogListener = deleteBetDialogListener;
        Button cancelButton = (Button) findViewById(R.id.delate_dialog_cancel_button);
        Button confirmButton = (Button) findViewById(R.id.delate_dialog_confirm_button);
        TextView title = (TextView) findViewById(R.id.main_title);
        TextView subTitle = (TextView) findViewById(R.id.delete_match_dialog_title);
        TextView explaination = (TextView) findViewById(R.id.delate_message);

        if(numberDocument==1) {
            title.setText(R.string.delate_bet_dialog_question_singolar);
            subTitle.setText(R.string.delete_bet_dialog_subtitle_singolar);
            explaination.setText(R.string.delete_bet_dialog_explaination_singolar);
        }else{
            title.setText(R.string.delate_bet_dialog_question_plural);
            subTitle.setText(R.string.delete_bet_dialog_subtitle_plural);
            explaination.setText(R.string.delete_bet_dialog_explaination_plural);
        }

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDeleteBetDialogListener.onBetDelete();
            }
        });
    }

    public interface DeleteBetDialogListener{
        void onBetDelete();
    }
}
