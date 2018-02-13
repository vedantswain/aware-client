package com.aware.ui.esms;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.aware.Aware;
import com.aware.ESM;
import com.aware.R;
import com.aware.providers.ESM_Provider;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Random;

/**
 * Created by vedantdasswain on 13/02/18.
 */

public class ESM_SSE_Radio extends ESM_Radio {

    public static final String esm_radios = "esm_radios";
    public static final String ESM_SSE_TAG = "ESM SSE";

    public static final String[] instructions = {
        "How true is this statement of you right now:\n" + "I feel self-conscious",
        "How true is this statement of you right now:\n" + "I feel as smart as others",
        "How true is this statement of you right now:\n" + "I feel unattractive",
    };

    public ESM_SSE_Radio() throws JSONException {
        this.setType(ESM.TYPE_ESM_SSE_RADIO);
    }

    @NonNull
    @Override
    public String getInstructions() throws JSONException {
        int rnd = new Random().nextInt(instructions.length);
        String instruction_r = instructions[rnd];
        this.esm.put(esm_instructions, instruction_r);
        return this.esm.getString(esm_instructions);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View ui = inflater.inflate(R.layout.esm_radio, null);
        builder.setView(ui);

        esm_dialog = builder.create();
        esm_dialog.setCanceledOnTouchOutside(false);

        try {
            TextView esm_title = (TextView) ui.findViewById(R.id.esm_title);
            esm_title.setText(getTitle());

            TextView esm_instructions = (TextView) ui.findViewById(R.id.esm_instructions);
            esm_instructions.setText(getInstructions());

            final RadioGroup radioOptions = (RadioGroup) ui.findViewById(R.id.esm_radio);
            radioOptions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (getExpirationThreshold() > 0 && expire_monitor != null)
                            expire_monitor.cancel(true);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            final JSONArray radios = getRadios();
            for (int i = 0; i < radios.length(); i++) {
                final RadioButton radioOption = new RadioButton(getActivity());
                radioOption.setId(i);
                radioOption.setText(radios.getString(i));
                radioOptions.addView(radioOption);

                if (radios.getString(i).equals(getResources().getString(R.string.aware_esm_other))) {
                    radioOption.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final Dialog editOther = new Dialog(getActivity());
                            editOther.setTitle(getResources().getString(R.string.aware_esm_other_follow));
                            editOther.getWindow().setGravity(Gravity.TOP);
                            editOther.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

                            LinearLayout editor = new LinearLayout(getActivity());
                            editor.setOrientation(LinearLayout.VERTICAL);

                            editOther.setContentView(editor);
                            editOther.show();

                            final EditText otherText = new EditText(getActivity());
                            otherText.setHint(getResources().getString(R.string.aware_esm_other_follow));
                            editor.addView(otherText);
                            otherText.requestFocus();
                            editOther.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

                            Button confirm = new Button(getActivity());
                            confirm.setText("OK");
                            confirm.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (otherText.length() > 0)
                                        radioOption.setText(otherText.getText());
                                    editOther.dismiss();
                                }
                            });
                            editor.addView(confirm);
                        }
                    });
                }
            }
            Button cancel_radio = (Button) ui.findViewById(R.id.esm_cancel);
            cancel_radio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    esm_dialog.cancel();
                }
            });
            Button submit_radio = (Button) ui.findViewById(R.id.esm_submit);
            submit_radio.setText(getSubmitButton());
            submit_radio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (getExpirationThreshold() > 0 && expire_monitor != null)
                            expire_monitor.cancel(true);

                        ContentValues rowData = new ContentValues();
                        rowData.put(ESM_Provider.ESM_Data.ANSWER_TIMESTAMP, System.currentTimeMillis());

                        RadioGroup radioOptions = (RadioGroup) ui.findViewById(R.id.esm_radio);
                        if (radioOptions.getCheckedRadioButtonId() != -1) {
                            RadioButton selected = (RadioButton) radioOptions.getChildAt(radioOptions.getCheckedRadioButtonId());
                            rowData.put(ESM_Provider.ESM_Data.ANSWER, String.valueOf(selected.getText()).trim());
                        }
                        rowData.put(ESM_Provider.ESM_Data.STATUS, ESM.STATUS_ANSWERED);

                        getContext().getContentResolver().update(ESM_Provider.ESM_Data.CONTENT_URI, rowData, ESM_Provider.ESM_Data._ID + "=" + getID(), null);

                        Intent answer = new Intent(ESM.ACTION_AWARE_ESM_ANSWERED);
                        answer.putExtra(ESM.EXTRA_ANSWER, rowData.getAsString(ESM_Provider.ESM_Data.ANSWER));
                        getActivity().sendBroadcast(answer);

                        if (Aware.DEBUG) Log.d(Aware.TAG, "Answer:" + rowData.toString());

                        esm_dialog.dismiss();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return esm_dialog;
    }
}
