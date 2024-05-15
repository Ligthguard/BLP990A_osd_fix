package com.example.TWSystemTest;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Handler;
import android.os.Message;
import android.tw.john.TWUtil;

import java.nio.charset.StandardCharsets;

public class TWUtilTest
{
    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

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
                String hex = "";
                String objAsString = "";
                if(msg.obj != null)
                {
                    try
                    {
                        hex = bytesToHex((byte[])msg.obj);
                    }
                    catch(Exception e)
                    {
                        hex = "Failed to convert obj to hex string";
                    }

                    try
                    {
                        objAsString = new String((byte[])msg.obj, StandardCharsets.UTF_8);
                    }
                    catch(Exception e)
                    {
                        objAsString = "Failed to convert obj to string";
                    }
                }
                String out = "## op: " + String.valueOf(msg.what) + " arg1: " + String.valueOf(msg.arg1) + " arg2: " + String.valueOf(msg.arg2);
                if(!hex.isEmpty())
                    out += "  ## obj (hex): " + hex;
                if(!objAsString.isEmpty())
                    out += "  ## obj (str): " + objAsString;
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

    public void write(short op, short arg1, short arg2, String objBytes)
    {
        if(m_openSuccess)
        {
            int result = 0;
            if(!objBytes.isEmpty())
            {
                try
                {
                    byte[] bytes = hexStringToByteArray(objBytes);
                    result = m_TW.write(op, arg1, arg2, bytes);
                }
                catch (Exception e)
                {
                    m_activity.AppendLogText("Write failed while parsing byte array: " + e.getMessage());
                    return;
                }
            }
            else if(arg2 > 0)
                result = m_TW.write(op, arg1, arg2);
            else if(arg1 > 0)
                result = m_TW.write(op, arg1);
            else
                result = m_TW.write(op);

            m_activity.AppendLogText("Write(" + op + ", " + arg1 + ", " + arg2 + ", {" + objBytes + "}) result: " + String.valueOf(result));
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
