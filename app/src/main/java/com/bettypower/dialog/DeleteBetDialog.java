package com.bettypower.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.renard.ocr.R;

/**
 * Created by giuliopettenuzzo on 07/02/18.
 */

public class DeleteBetDialog extends Dialog {

    private Button cancelButton;
    private Button confirmButton;
    private TextView title;
    private TextView subTitle;
    private TextView explaination;
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
        cancelButton = (Button) findViewById(R.id.delate_dialog_cancel_button);
        confirmButton = (Button) findViewById(R.id.delate_dialog_confirm_button);
        title = (TextView) findViewById(R.id.main_title);
        subTitle = (TextView) findViewById(R.id.delete_match_dialog_title);
        explaination = (TextView) findViewById(R.id.delate_message);

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
