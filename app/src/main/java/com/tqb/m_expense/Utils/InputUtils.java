package com.tqb.m_expense.Utils;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.Objects;

public class InputUtils {
    public static boolean checkInput(Context context, View... views) {
        boolean validation = true;
        for (View view : views) {
            if (view instanceof EditText) {
                if (((EditText) view).getText() == null ||
                        Objects.requireNonNull(((EditText) view).getText()).toString().isEmpty()) {
                    validation = false;
                    ((EditText) view).setError("Please fill in this field.");
                    ((EditText) view).requestFocus();
                }
            }
            if (view instanceof RadioGroup) {
                if (((RadioGroup) view).getCheckedRadioButtonId() == -1) {
                    Toast.makeText(context, "Please select a risk assessment option.", Toast.LENGTH_SHORT).show();
                    validation = false;
                }
            }
        }

        return validation;
    }
}
