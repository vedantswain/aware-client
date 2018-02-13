package com.aware.questionnaires;

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

    private static String MHQ_ID = "2601";
    private static String CQ_ID = "1909";

    private static String TAG = "ESM SCHEDULER";

    private static final String[] instructions = {
            "How true is this statement of you right now:\n" + "I feel self-conscious",
            "How true is this statement of you right now:\n" + "I feel as smart as others",
            "How true is this statement of you right now:\n" + "I feel unattractive",
    };

    public static void setESMs(Context context) {
        scheduleMHQ(context);

        Aware.startScheduler(context);

        Log.d(TAG, "Setup scheduled questionnaires");
    }

    public static void removeESMs(Context context) {
        String[] ids = {MHQ_ID, CQ_ID};

        for (int i=0; i<ids.length; i++) {
            if(Scheduler.getSchedule(context, ids[i])!=null) {
                Scheduler.removeSchedule(context, ids[i]);
                Log.d(TAG, "Removed scheduled questionnaire: "+ids[i]);
            }
        }

        Aware.stopScheduler(context);
    }

    private static void scheduleMHQ(Context c) {
        try {

            ESMFactory factory = new ESMFactory();

            //define ESM question
            ESM_Random_Radio esm_rr = new ESM_Random_Radio();
            esm_rr.setCollection(instructions)
                    .addRadio("not at all").addRadio("a little bit")
                    .addRadio("somewhat").addRadio("very much").addRadio("extremely")
                    .setSubmitButton("Done");

            //add them to the factory
            factory.addESM(esm_rr);

            Scheduler.Schedule random = new Scheduler.Schedule(MHQ_ID);
            random.setInterval(3)
                    .setActionType(Scheduler.ACTION_TYPE_BROADCAST)
                    .setActionIntentAction(ESM.ACTION_AWARE_QUEUE_ESM)
                    .addActionExtra(ESM.EXTRA_ESM, factory.build());
            Scheduler.saveSchedule(c, random);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



}
