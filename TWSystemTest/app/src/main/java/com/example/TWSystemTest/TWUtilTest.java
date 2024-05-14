package com.example.TWSystemTest;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Handler;
import android.os.Message;
import android.tw.john.TWUtil;

public class TWUtilTest
{
    private final TWUtil m_TW = new TWUtil();
    private boolean m_openSuccess = false;
    static MainActivity m_activity = null;
    public TWUtilTest(MainActivity activity)
    {
        m_activity = activity;
    }

    @SuppressLint("HandlerLeak")
    private static final Handler mHandler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            try
            {
                String out = "op: " + String.valueOf(msg.what) + " arg1: " + String.valueOf(msg.arg1) + " arg2: " + String.valueOf(msg.arg2);
                m_activity.AppendLogText(out);
            }
            catch (Exception e)
            {
                m_activity.AppendLogText(e.getMessage());
            }
        }
    };

    public void AttachHandler()
    {
        if(m_openSuccess)
        {
            m_TW.addHandler("TWUtilTest", mHandler);
            m_TW.start();
            m_activity.AppendLogText("Handler attached and started!");
        }
        else
            m_activity.AppendLogText("Attach: Device not open!");
    }

    public void open(short[] args)
    {
        int result = m_TW.open(args);
        if(result != 0)
        {
            AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(m_activity);
            dlgAlert.setMessage("Failed to open /dev/tw with these params.");
            dlgAlert.setTitle("Failed to open /dev/tw");
            m_activity.AppendLogText("Failed to open /dev/tw!");
            dlgAlert.setPositiveButton("OK", null);
            dlgAlert.setCancelable(true);
            dlgAlert.create().show();
        }

        m_activity.AppendLogText("Successfully opened /dev/tw!");
        m_openSuccess = true;
    }

    public void close()
    {
        if(m_openSuccess)
            m_TW.close();

        m_openSuccess = false;
    }

    public void write(short op, short arg1, short arg2)
    {
        if(m_openSuccess)
        {
            int result = 0;
            if(arg2 > 0)
                result = m_TW.write(op, arg1, arg2);
            else if(arg1 > 0)
                result = m_TW.write(op, arg1);
            else
                result = m_TW.write(op);

            m_activity.AppendLogText("Write result: " + String.valueOf(result));
        }
        else
            m_activity.AppendLogText("Write: Device not open!");
    }

    public void TryToggleHVACScreen()
    {
        if(m_TW != null)
        {
            m_TW.write(1281, 170, 85);
            m_activity.AppendLogText("Sending 1281 170 85 to /dev/tw!");
        }
    }
}
