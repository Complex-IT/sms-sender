package com.complexit.smssender;


import android.content.Context;
import android.telephony.SmsManager;
import android.text.Html;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class smsSender {

    private static Queue<String> smsQueue=new LinkedList<>();
    private static smsSender ref = null;
    public static Boolean finish = false;
    public static Context ctx;


    public void run() {

        Runnable r = new Runnable() {
            public void run() {
                do {

                    if (!smsQueue.isEmpty())
                    {
                        String str = smsQueue.poll();
                        String[] smsParams = str.split("-");
                        if (smsParams.length > 0) {
                            sndSMS(smsParams[0], smsParams[1]);
                        }
                    }

                    // wait 100ms
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e)
                    {

                    }

                } while(!finish);
            }
        };

        try {
            Thread t = new Thread(r);
            t.start();
        } catch (Exception e)
        {

        }
    }

    private smsSender(Context _ctx)
    {
        ctx = _ctx;
        run();
    }

    public static smsSender getSmsSender(Context _ctx)
    {
        if (ref != null) return ref;
        else
        {
            ref = new smsSender(_ctx);
            return ref;
        }
    }

    public void putSMS(String str)
    {
        synchronized (this) {
            smsQueue.add(str);
        }
    }

    void sndSMS(String phone, String msg)
    {
        SmsManager sms = SmsManager.getDefault();
        if (!phone.equals("")) {
            try {
                String str = Html.fromHtml(new String(msg.getBytes("UTF-8"))).toString();
                ArrayList<String> ts = sms.divideMessage(str);

                sms.sendMultipartTextMessage(phone,null,ts,null,null);

            } catch (Exception e)
            {

            }
        }
    }

}
