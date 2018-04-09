package com.aware.questionnaires;

import android.app.NotificationManager;
import android.content.Context;
import android.util.Log;

import com.aware.Aware;
import com.aware.ESM;
import com.aware.ui.esms.ESMFactory;
import com.aware.ui.esms.ESM_Random_Radio;
import com.aware.utils.Scheduler;

import org.json.JSONException;

/**
 * Created by vedantdasswain on 06/02/18.
 */

public class ESMScheduler {

    private static NotificationManager mNotificationManager;


    private static String MHQ_ID = "2601";
    private static String CQ_ID = "1909";

    private static String TAG = "ESM SCHEDULER";

    private static final String[] EQ_instructions = {
            "\"I feel weak right now\"",
            "\"I feel unsuccessful right now\"",
            "\"I feel stressed right now\"",
            "\"I feel negative right now\"",
            "\"I feel disinterested right now\"",
            "\"I feel anxious right now\""
    };

    private static final String[] CQ_instructions = {
            "\"My surroundings are noisy right now\"",
            "\"My surroundings are dirty right now\"",
            "\"My surroundings are dark right now\"",
            "\"My surroundings are dangerous right now\"",
            "\"My surroundings are cold right now\"",
            "\"My surroundings are crowded right now\""
    };

    public static void setESMs(Context context) {
        scheduleEQ(context);
        scheduleCQ(context);

        Aware.startScheduler(context);

        Log.d(TAG, "Setup scheduled questionnaires");

        ESM.NotifyScheduleSetup(context, true);
    }

    private static void scheduleEQ(Context c) {
        try {
            if(Scheduler.getSchedule(c, MHQ_ID)!=null){
                Scheduler.removeSchedule(c, MHQ_ID);
            }

            ESMFactory factory = new ESMFactory();

            //define ESM question
            ESM_Random_Radio esm_rr = new ESM_Random_Radio();
            esm_rr.setCollection(EQ_instructions)
                    .addRadio("Strongly Disagree").addRadio("Disagree")
                    .addRadio("Neither Agree nor Disagree").addRadio("Agree").addRadio("Strongly Agree")
                    .setCancelButton("Not Now")
                    .setNotificationTimeout(1200)
                    .setTrigger("EQ");

            //add them to the factory
            factory.addESM(esm_rr);

            Scheduler.Schedule random = new Scheduler.Schedule(MHQ_ID);
            random.addHour(9).addHour(22)
                    .random(8,40)
                    .setActionType(Scheduler.ACTION_TYPE_BROADCAST)
                    .setActionIntentAction(ESM.ACTION_AWARE_QUEUE_ESM)
                    .addActionExtra(ESM.EXTRA_ESM, factory.build());
            Scheduler.saveSchedule(c, random);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private static void scheduleCQ(Context c) {
        try {
            if(Scheduler.getSchedule(c, CQ_ID)!=null){
                Scheduler.removeSchedule(c, CQ_ID);
            }

            ESMFactory factory = new ESMFactory();

            //define ESM question
            ESM_Random_Radio esm_rr = new ESM_Random_Radio();
            esm_rr.setCollection(CQ_instructions)
                    .addRadio("Strongly Disagree").addRadio("Disagree")
                    .addRadio("Neither Agree nor Disagree").addRadio("Agree").addRadio("Strongly Agree")
                    .setCancelButton("Not Now")
                    .setNotificationTimeout(1200)
                    .setTrigger("CQ");

            //add them to the factory
            factory.addESM(esm_rr);

            Scheduler.Schedule random = new Scheduler.Schedule(CQ_ID);
            random.addHour(9).addHour(22)
                    .random(8,40)
                    .setActionType(Scheduler.ACTION_TYPE_BROADCAST)
                    .setActionIntentAction(ESM.ACTION_AWARE_QUEUE_ESM)
                    .addActionExtra(ESM.EXTRA_ESM, factory.build());
            Scheduler.saveSchedule(c, random);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
