/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.helixproject.launcher;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.helixproject.internal.widget.NumberPicker;

/**
 * A dialog that prompts the user for picking a number
 */
public class NumberPickerDialog extends AlertDialog implements OnClickListener {
    private int mInitialNumber;

    private static final String NUMBER = "number";

    /**
     * The callback interface used to indicate the user is done filling in
     * the time (they clicked on the 'Set' button).
     */
    public interface OnNumberSetListener {

        /**
         * @param number The number that was set.
         */
        void onNumberSet(int number);
    }

    private final NonWrapNumberPicker mNumberPicker;
    private final OnNumberSetListener mCallback;

    /**
     * @param context Parent.
     * @param callBack How parent is notified.
     * @param number The initial number.
     */
    public NumberPickerDialog(Context context,
            OnNumberSetListener callBack,
            int number,
            int rangeMin,
            int rangeMax,
            int title,
			int desc) {
        this(context, android.R.style.Theme_Dialog,
                callBack, number, rangeMin, rangeMax, title, desc);
    }

    /**
     * @param context Parent.
     * @param theme the theme to apply to this dialog
     * @param callBack How parent is notified.
     * @param number The initial number.
     */
    public NumberPickerDialog(Context context,
            int theme,
            OnNumberSetListener callBack,
            int number,
            int rangeMin,
            int rangeMax,
            int title,
			int desc) {
        super(context, theme);
        mCallback = callBack;
        mInitialNumber = number;

        setTitle(title);

        setButton(context.getText(R.string.set), this);
        setButton2(context.getText(R.string.no), (OnClickListener) null);

        LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.number_picker, null);
        setView(view);
        mNumberPicker = (NonWrapNumberPicker) view.findViewById(R.id.number_picker);
		((TextView) view.findViewById(R.id.desc)).setText(desc);
		
        // initialize state
        mNumberPicker.setRange(rangeMin, rangeMax);
        mNumberPicker.setCurrent(number);
        mNumberPicker.setSpeed(150);    // make the repeat rate twice as fast as normal since the
                                        // range is so large.
    }

    public void onClick(DialogInterface dialog, int which) {
        if (mCallback != null) {
            mNumberPicker.clearFocus();
            mCallback.onNumberSet(mNumberPicker.getCurrent());
        }
    }

    @Override
    public Bundle onSaveInstanceState() {
        Bundle state = super.onSaveInstanceState();
        state.putInt(NUMBER, mNumberPicker.getCurrent());
        return state;
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        int number = savedInstanceState.getInt(NUMBER);
        mNumberPicker.setCurrent(number);
    }

    public static class NonWrapNumberPicker extends NumberPicker {

        public NonWrapNumberPicker(Context context) {
            this(context, null);
        }

        public NonWrapNumberPicker(Context context, AttributeSet attrs) {
            this(context, attrs, 0);
        }

        @SuppressWarnings({"UnusedDeclaration"})
        public NonWrapNumberPicker(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs);
        }

        @Override
        protected void changeCurrent(int current) {
            // Don't wrap. Pin instead.
            if (current > mEnd) {
                current = mEnd;
            } else if (current < mStart) {
                current = mStart;
            }
            super.changeCurrent(current);
        }

    }

}