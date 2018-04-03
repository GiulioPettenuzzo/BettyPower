package com.bettypower.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.bettypower.betMatchFinder.labelSet.BetLabelSet;
import com.bettypower.entities.PalimpsestMatch;
import com.renard.ocr.R;

/**
 * Dialog that allaws user to insert bet kind, bet and quote, just in case the ocr made a mistake
 * Created by giulio pettenuzzo on 23/11/17.
 */

public class SetBetDialog extends Dialog {

    private Context context;
    private TextView showBetKindTextView;
    private Button showBet;
    private EditText showQuote;
    private Button deleteTextView;
    private Button saveTextView;
    private com.shawnlin.numberpicker.NumberPicker selectBetKindPicker;
    private com.shawnlin.numberpicker.NumberPicker selectBetPicker;
    private com.shawnlin.numberpicker.NumberPicker selectQuotePicker;

    private BetLabelSet betLabelSet = new BetLabelSet();
    private Object[] betKindArray;
    private Object[] allBet;
    private PalimpsestMatch match;

    private FinishEdittingDialogListener finishEdittingDialogListener;

    private static final int MIN_QUOTE_VALUE = 101;
    private static final int MAX_QUOTE_VALUE = 9999;

    public SetBetDialog(@NonNull Context context, PalimpsestMatch match) {
        super(context);
        this.context = context;
        this.match = match;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
        setBetKindPickerAndView();
        preSetBetPicker();
        setBetPicker();
        setQuotePickerAndView();

        saveTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(showQuote.getText()==null || showQuote.getText().toString().equals(""))
                    match.setQuote(null);
                else
                    match.setQuote(showQuote.getText().toString());
                match.setBet(showBet.getText().toString());
                match.setBetKind(showBetKindTextView.getText().toString());
                finishEdittingDialogListener.onEdittingDialogFinish(match);
                dismiss();
            }
        });

        deleteTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    /***********************************************************************************************
     ********************************* VIEW'E METHOD   ****************************************
     **********************************************************************************************/

    private void initViews(){
        setContentView(R.layout.dialog_set_bet);
        showBetKindTextView = (TextView) findViewById(R.id.show_bet_kind);
        deleteTextView = (Button) findViewById(R.id.delete_text_view);
        saveTextView = (Button) findViewById(R.id.select_text_view);
        showBet = (Button) findViewById(R.id.show_bet_button);
        showQuote = (EditText) findViewById(R.id.show_quote_edit_text);
        selectBetKindPicker = (com.shawnlin.numberpicker.NumberPicker) findViewById(R.id.select_bet_kind);
        selectBetPicker = (com.shawnlin.numberpicker.NumberPicker) findViewById(R.id.select_bet);
        selectQuotePicker = (com.shawnlin.numberpicker.NumberPicker) findViewById(R.id.select_quote_picker);
        TextView teamsNamesTextView = (TextView) findViewById(R.id.teams_names);
        TextView illustrationTextView = (TextView) findViewById(R.id.illustration);

        betKindArray =  betLabelSet.hashBetAndBetKind().keySet().toArray();
        illustrationTextView.setText(context.getResources().getString(R.string.dialog_illustration_set_bet));
        String teamsName = match.getHomeTeam().getName() + " - " + match.getAwayTeam().getName();
        teamsNamesTextView.setText(teamsName);
    }

    /**
     * this method is separated from setBetPicker because setBetPicker is used more than one time,
     * for example when user scroll selectBetKindPicker
     */
    private void preSetBetPicker(){
        allBet = betLabelSet.hashBetAndBetKind().get(getStringFromBetKindArray(selectBetKindPicker.getValue())).toArray();

        selectBetPicker.setWheelItemCount(8);
        selectBetPicker.setMinValue(0);
        if(match.getBet() != null) {
            selectBetPicker.setValue(get(allBet, match.getBet()));
        }
        else{
            selectBetPicker.setValue(0);
        }
        selectBetPicker.setMaxValue(allBet.length-1);
        selectBetPicker.setWrapSelectorWheel(false);
    }

    private void setBetPicker(){
        showBet.setText(allBet[0].toString());
        selectBetPicker.setFormatter(new com.shawnlin.numberpicker.NumberPicker.Formatter() {
            @Override
            public String format(int value) {
                return allBet[value].toString();
            }
        });

        selectBetPicker.setOnValueChangedListener(new com.shawnlin.numberpicker.NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(com.shawnlin.numberpicker.NumberPicker picker, int oldVal, int newVal) {
                showBet.setText(allBet[newVal].toString());
            }
        });
    }

    private void setQuotePickerAndView(){
        selectQuotePicker.setWheelItemCount(3);
        selectQuotePicker.setMinValue(MIN_QUOTE_VALUE);
        selectQuotePicker.setMaxValue(MAX_QUOTE_VALUE);
        selectQuotePicker.setWrapSelectorWheel(false);
        if(match.getQuote()!=null && !match.getQuote().equals("")){
            selectQuotePicker.setValue(fromQuoteToNumber(match.getQuote()));
            showQuote.setText(getQuoteFormat(selectQuotePicker.getValue()));
        }
        else{
            selectQuotePicker.setValue(200);
            showQuote.setHint("quota");
            showQuote.setHintTextColor(context.getResources().getColor(R.color.grey6));
        }
        selectQuotePicker.setFormatter(new com.shawnlin.numberpicker.NumberPicker.Formatter() {
            @Override
            public String format(int value) {
                return getQuoteFormat(value);
            }
        });
        selectQuotePicker.setOnValueChangedListener(new com.shawnlin.numberpicker.NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(com.shawnlin.numberpicker.NumberPicker picker, int oldVal, int newVal) {
                showQuote.setText(getQuoteFormat(newVal));
            }
        });
        showQuote.getBackground().mutate().setColorFilter(context.getResources().getColor(R.color.primary), PorterDuff.Mode.SRC_ATOP);

        showQuote.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    showQuote.setText("");
                }
            }
        });
        showQuote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setFocusable(true);
                v.setFocusableInTouchMode(true);
            }
        });

        showQuote.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    Log.i("TYPE","TYPE");
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(showQuote.getWindowToken(), 0);
                    }
                    v.setFocusable(false);
                    showQuote.setText(fixQuote(showQuote.getText().toString()));
                    selectQuotePicker.setValue(fromQuoteToNumber(showQuote.getText().toString()));
                    return true;
                }else{
                    return false;
                }
            }
        });
        showQuote.addTextChangedListener(new CorrectionToUserWatcher());
    }

    private void setBetKindPickerAndView(){
        selectBetKindPicker.setMinValue(0);
        selectBetKindPicker.setMaxValue(betKindArray.length-1);
        selectBetKindPicker.setWheelItemCount(8);
        if(match.getBetKind() != null)
            selectBetKindPicker.setValue(get(betKindArray,match.getBetKind()));
        else
            selectBetKindPicker.setValue(0);
        showBetKindTextView.setText(betKindArray[selectBetKindPicker.getValue()].toString());
        selectBetKindPicker.setDisplayedValues(toArrayString(betKindArray));
        selectBetKindPicker.setOnValueChangedListener(new com.shawnlin.numberpicker.NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(com.shawnlin.numberpicker.NumberPicker picker, int oldVal, int newVal) {
                showBetKindTextView.setText(getStringFromBetKindArray(newVal));
                allBet = betLabelSet.hashBetAndBetKind().get(getStringFromBetKindArray(newVal)).toArray();
                selectBetPicker.setMaxValue(allBet.length-1);
                selectBetPicker.setWrapSelectorWheel(false);
                setBetPicker();
            }
        });

    }

    /* **********************************************************************************************
     ********************************* QUOTE  AUSILIAR METHOD   *************************************
     **********************************************************************************************/

    /**
     * if the user type a different format of the quote, it can adjust it...
     */
    private String fixQuote(String currentQuote){
        String result;
        if(currentQuote.length()<=4){
            if(currentQuote.length() == 1){
                if(currentQuote.equals("1")){
                    result = currentQuote + ",01";
                }else {
                    result = currentQuote + ",00";
                }
            }
            else if(currentQuote.length() == 2){
                if(currentQuote.contains(",")){
                    if(currentQuote.equals("1,")){
                        result = currentQuote + "01";
                    }else {
                        result = currentQuote + "00";
                    }
                }
                else{
                    showQuote.setFilters(new InputFilter[] {new InputFilter.LengthFilter(5)});
                    result = currentQuote + ",00";
                }
            }
            else if(currentQuote.length() == 3){
                if(currentQuote.charAt(1) == ','){
                    result = currentQuote + "0";
                }else{
                    showQuote.setFilters(new InputFilter[] {new InputFilter.LengthFilter(5)});
                    result = currentQuote + "00";                }
            }
            else if(currentQuote.length() == 4){
                if(currentQuote.charAt(2) == ','){
                    result = currentQuote + "0";
                }else{
                    result = currentQuote;
                }
            }
            else{
                return "";
            }
        }

        else{
            result = currentQuote;
        }
        return result;
    }

    /*
    * given a string in quote format, it return the number of the corrisponding element so it can be pick
    * from number picker easily
    */
    private int fromQuoteToNumber(String quote){
        StringBuilder result = new StringBuilder();
        for (int i = 0; i<quote.length();i++){
            if(quote.charAt(i) != ','){
                result.append(String.valueOf(quote.charAt(i)));
            }
        }
        try {
            return Integer.parseInt(result.toString());
        }
        catch (NumberFormatException e){
            return 200;
        }
    }

    /**
     * from the value in number picker(int) it return the corrisponding quote format in String
     */
    private String getQuoteFormat(int value){
        String numString = String.valueOf(value);
        if(numString.length()==4){
            numString = String.valueOf(numString.charAt(0)) + String.valueOf(numString.charAt(1)) + "," + String.valueOf(numString.charAt(2)) + String.valueOf(numString.charAt(3));
        }
        if(numString.length()==3) {
            numString = numString.charAt(0) + "," + numString.charAt(1) + numString.charAt(2);
        }
        return numString;
    }

    /* *********************************************************************************************
     *********************************   AUSILIAR METHOD   ****************************************
     **********************************************************************************************/

    /**
     * this is the method get() of array list, it return the position of the value in the array given
     * from param.
     */
    private int get(Object[] array, String value){
        for(int i = 0; i<array.length;i++){
            if(array[i].toString().equals(value)){
                return i;
            }
        }
        return -1;
    }

    /**
     * transform the array of object given in param to the corrisponding array of string,
     * without this method you must use toString every time you want to show a string in number picker
     */
    private String[] toArrayString(Object[] obj){
        String[] result = new String[obj.length];
        for(int i = 0;i<obj.length;i++){
            result[i] = obj[i].toString();
        }
        return result;
    }

    /**
     * this method helps a lot because if I make the string array a circle array:
     * it return the string associated with the int number given in param from number picker
     * @param newVal the value of the number picker
     * @return the corrisponding string
     */
    private String getStringFromBetKindArray(int newVal){
        String betKind;
        if(newVal!=0) {
            betKind = betKindArray[newVal].toString();
        }else{
            betKind = betKindArray[betKindArray.length-1].toString();
        }
        return betKind;
    }
    /***********************************************************************************************
     *********************************   INTERFACE   ****************************************
     **********************************************************************************************/

    /*
     *set the listener
     */
    public void setFinishEdittingDialogListener(FinishEdittingDialogListener finishEdittingDialogListener){
        this.finishEdittingDialogListener = finishEdittingDialogListener;
    }

    /**
     * listener created in order to know when user finish typing and pass the match to the adapter
     */
    public interface FinishEdittingDialogListener{
        void onEdittingDialogFinish(PalimpsestMatch match);
    }

    private class CorrectionToUserWatcher implements TextWatcher {

        /**
         * This method is called to notify you that, within <code>s</code>,
         * the <code>count</code> characters beginning at <code>start</code>
         * are about to be replaced by new text with length <code>after</code>.
         * It is an error to attempt to make changes to <code>s</code> from
         * this callback.
         *
         * @param s s
         * @param start start
         * @param count count
         * @param after after
         */
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        /**
         * This method is called to notify you that, within <code>s</code>,
         * the <code>count</code> characters beginning at <code>start</code>
         * have just replaced old text that had length <code>before</code>.
         * It is an error to attempt to make changes to <code>s</code> from
         * this callback.
         *
         * @param s s
         * @param start start
         * @param before before
         * @param count count
         */
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(s.length() > 0) {
                String tmp = String.valueOf(s);
                if (start == 0 && (s.charAt(0) == '.' || s.charAt(0)=='0')) {
                    showQuote.setText("");
                    return;
                }
                if (tmp.contains(",") && s.charAt(s.length() - 1) == '.') {
                    tmp = String.valueOf(s.subSequence(0, s.length() - 1));
                    showQuote.setText(tmp);
                    showQuote.setSelection(tmp.length());
                    return;
                }
                if (s.charAt(s.length() - 1) == '.') {
                    tmp = String.valueOf(s.subSequence(0, s.length() - 1));
                    tmp = tmp + ",";
                    showQuote.setText(tmp);
                    showQuote.setSelection(tmp.length());
                    if(s.length()==2){
                        showQuote.setFilters(new InputFilter[] {new InputFilter.LengthFilter(4)});
                    }
                    else if(s.length()==3){
                        showQuote.setFilters(new InputFilter[] {new InputFilter.LengthFilter(5)});
                    }
                    return;
                }
                if (s.length() == 3 && !tmp.contains(",")) {
                    showQuote.setFilters(new InputFilter[] {new InputFilter.LengthFilter(5)});
                    tmp = s.subSequence(0, 1) + "," + String.valueOf(s.charAt(1)) + String.valueOf(s.charAt(2));
                    showQuote.setText(tmp);
                    showQuote.setSelection(tmp.length());
                    return;
                }
                if (s.length() == 5 && s.charAt(1) == ',') {
                    tmp = String.valueOf(s.charAt(0)) + String.valueOf(s.charAt(2)) + "," + String.valueOf(s.charAt(3)) + String.valueOf(s.charAt(4));
                    showQuote.setText(tmp);
                    showQuote.setSelection(tmp.length());
                }
            }
        }

        /**
         * This method is called to notify you that, somewhere within
         * <code>s</code>, the text has been changed.
         * It is legitimate to make further changes to <code>s</code> from
         * this callback, but be careful not to get yourself into an infinite
         * loop, because any changes you make will cause this method to be
         * called again recursively.
         * (You are not told where the change took place because other
         * afterTextChanged() methods may already have made other changes
         * and invalidated the offsets.  But if you need to know here,
         * you can use {} in {@link #onTextChanged}
         * to mark your place and then look up from here where the span
         * ended up.
         *
         * @param s s
         */
        @Override
        public void afterTextChanged(Editable s) {

        }
    }
    }
