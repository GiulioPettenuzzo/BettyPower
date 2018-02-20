package com.bettypower.adapters.helpers;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

/**
 * Created by giuliopettenuzzo on 17/12/17.
 * this class is used to correct the text the user insert while he is typing
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
     */
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

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
     * @param s the edit text
     */
    @Override
    public void afterTextChanged(Editable s) {
        String str = editText.getText().toString();
        if (str.isEmpty()) return;
        String str2 = PerfectDecimal(str);

        if (!str2.equals(str)) {
            editText.setText(str2);
            int pos = editText.getText().length();
            editText.setSelection(pos);
        }
    }

    private String PerfectDecimal(String str){
        if(str.charAt(0) == '.') str = "0"+str;
        int max = str.length();

        StringBuilder rFinal = new StringBuilder();
        boolean after = false;
        int i = 0, up = 0, decimal = 0; char t;
        while(i < max){
            t = str.charAt(i);
            if(t != '.' && !after){
                up++;
                if(up > 5) return rFinal.toString();
            }else if(t == '.'){
                after = true;
            }else{
                decimal++;
                if(decimal > 2)
                    return rFinal.toString();
            }
            rFinal.append(t);
            i++;
        }return rFinal.toString();
    }
}
