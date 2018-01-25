package com.bettypower.adapters.helpers;

import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

/**
 * Created by giuliopettenuzzo on 17/12/17.
 */

public class CorrectionToUserWatcher implements TextWatcher {

    private EditText editText;

    public CorrectionToUserWatcher(EditText editText){
        this.editText = editText;
    }
    /**
     * This method is called to notify you that, within <code>s</code>,
     * the <code>count</code> characters beginning at <code>start</code>
     * are about to be replaced by new text with length <code>after</code>.
     * It is an error to attempt to make changes to <code>s</code> from
     * this callback.
     *
     * @param s
     * @param start
     * @param count
     * @param after
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
     * @param s
     * @param start
     * @param before
     * @param count
     */
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        /*
        if(s.length() > 0) {
            String tmp = String.valueOf(s);
            if ((start == 0 && (s.charAt(0) == '.' || s.charAt(0) == '0')) && s.length()==1) {
                editText.setText("");
                return;
            }
            if((start == 0 && (s.charAt(0) == '.' || s.charAt(0) == '0')) && s.length()>1){
                String str = tmp.substring(1, s.length());
                editText.setText(str);
                editText.setSelection(0);
                return;
            }
            if(s.charAt(s.length()-1) == '.'){
                if(tmp.contains(",")) {
                    tmp = String.valueOf(s.subSequence(0,s.length()-1));
                    editText.setText(tmp);
                    editText.setSelection(tmp.length());
                    return;
                }
                editText.setFilters(new InputFilter[] {new InputFilter.LengthFilter(tmp.length() + 2)});
                tmp = s.subSequence(0,s.length()-1) + ",";
                editText.setText(tmp);
                editText.setSelection(tmp.length());
                return;
            }
            if(!tmp.contains(","))
                editText.setFilters(new InputFilter[] {new InputFilter.LengthFilter(tmp.length() + 2)});
            if(editText.getSelectionStart()!=0 && tmp.contains(",")){
                for(int j= 0;j<s.length();j++){
                    if(s.charAt(j)=='.') {
                        String str = tmp.substring(0, j) + tmp.substring(j + 1, s.length());
                        editText.setText(str);
                        editText.setSelection(j);
                        return;
                    }
                }
                for (int i = 0; i < s.length(); i++) {
                    if(s.charAt(i) == ',' && editText.getSelectionStart()>i){
                        editText.setFilters(new InputFilter[] {new InputFilter.LengthFilter(i + 3)});
                        try {
                            editText.setText(editText.getText().toString().substring(0, i + 3));
                        }catch(StringIndexOutOfBoundsException e) {
                            return;
                        }
                        try {
                            editText.setSelection(tmp.length());
                        }catch(IndexOutOfBoundsException e){
                            return;
                        }
                        return;
                    }
                    if(editText.getSelectionStart()<i) {
                        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(50)});
                        return;
                    }

                }

            }
            */
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
     * to mark your place and then look up from here where the span
     * ended up.
     *
     * @param s
     */
    @Override
    public void afterTextChanged(Editable s) {
        String str = editText.getText().toString();
        if (str.isEmpty()) return;
        String str2 = PerfectDecimal(str, 5, 2);

        if (!str2.equals(str)) {
            editText.setText(str2);
            int pos = editText.getText().length();
            editText.setSelection(pos);
        }
    }

    public String PerfectDecimal(String str, int MAX_BEFORE_POINT, int MAX_DECIMAL){
        if(str.charAt(0) == '.') str = "0"+str;
        int max = str.length();

        String rFinal = "";
        boolean after = false;
        int i = 0, up = 0, decimal = 0; char t;
        while(i < max){
            t = str.charAt(i);
            if(t != '.' && after == false){
                up++;
                if(up > MAX_BEFORE_POINT) return rFinal;
            }else if(t == '.'){
                after = true;
            }else{
                decimal++;
                if(decimal > MAX_DECIMAL)
                    return rFinal;
            }
            rFinal = rFinal + t;
            i++;
        }return rFinal;
    }
}
