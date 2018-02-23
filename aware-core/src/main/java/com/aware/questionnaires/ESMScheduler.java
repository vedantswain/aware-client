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

    private static final String[] MHQ_instructions = {
            "How true is this right now:\n" + "\"I feel self-conscious\"",
            "How true is this right now:\n" + "\"I feel as smart as others\"",
            "How true is this right now:\n" + "\"I feel unattractive\"",
            "How true is this right now:\n" + "\"I feel confident about my abilities\"",
            "How true is this right now:\n" + "\"I am dissatisfied with my weight\"",
            "How true is this right now:\n" + "\"I feel displeased with myself\"",
            "How true is this right now:\n" + "\"I feel good about myself\"",
            "How true is this right now:\n" + "\"I feel like I'm not doing well\"",
            "How true is this right now:\n" + "\"I am worried about looking foolish\"",
    };

    private static final String[] CQ_instructions = {
            "How true is this right now:\n" + "\"I feel hungry\"",
            "How true is this right now:\n" + "\"I feel thirsty\"",
            "How true is this right now:\n" + "\"I feel sleepy\"",
            "How true is this right now:\n" + "\"I feel tired\"",
            "How true is this right now:\n" + "\"I feel energetic\"",
    };

    public static void setESMs(Context context) {
        scheduleMHQ(context);
        scheduleCQ(context);

        Aware.startScheduler(context);

        Log.d(TAG, "Setup scheduled questionnaires");
    }

    private static void scheduleMHQ(Context c) {
        try {
            if(Scheduler.getSchedule(c, MHQ_ID)!=null){
                Scheduler.removeSchedule(c, MHQ_ID);
            }

            ESMFactory factory = new ESMFactory();

            //define ESM question
            ESM_Random_Radio esm_rr = new ESM_Random_Radio();
            esm_rr.setCollection(MHQ_instructions)
                    .addRadio("not at all").addRadio("a little bit")
                    .addRadio("somewhat").addRadio("very much").addRadio("extremely")
                    .setCancelButton("Not Now")
                    .setNotificationTimeout(900)
                    .setTrigger("MHQ");

            //add them to the factory
            factory.addESM(esm_rr);

            Scheduler.Schedule random = new Scheduler.Schedule(MHQ_ID);
            random.addHour(9).addHour(23)
                    .random(8,60)
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
                    .addRadio("not at all").addRadio("a little bit")
                    .addRadio("somewhat").addRadio("very much").addRadio("extremely")
                    .setCancelButton("Not Now")
                    .setNotificationTimeout(900)
                    .setTrigger("CQ");

            //add them to the factory
            factory.addESM(esm_rr);

            Scheduler.Schedule random = new Scheduler.Schedule(CQ_ID);
            random.addHour(9).addHour(23)
                    .random(8,60)
                    .setActionType(Scheduler.ACTION_TYPE_BROADCAST)
                    .setActionIntentAction(ESM.ACTION_AWARE_QUEUE_ESM)
                    .addActionExtra(ESM.EXTRA_ESM, factory.build());
            Scheduler.saveSchedule(c, random);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



}
