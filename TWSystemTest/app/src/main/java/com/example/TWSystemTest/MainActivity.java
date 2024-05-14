package com.example.TWSystemTest;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.tw.john.TWUtil;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    TWUtilTest m_test = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void attachHandler(View view)
    {
        if(m_test != null)
        {
            m_test.AttachHandler();
        }
        else
            AppendLogText("/dev/tw was not opened yet!");
    }

    public void openDeviceWithCustomValuesOnClick(View view)
    {
        try
        {
            if(m_test == null)
                m_test = new TWUtilTest(this);

            boolean isRangeMode = ((Switch)findViewById(R.id.rangeModeSwitch)).isChecked();
            String _values = ((TextView) findViewById(R.id.idList)).getText().toString();
            String[] parts = _values.split(" ");

            if(!isRangeMode)
            {
                short[] tempValues = new short[parts.length];
                for (int i = 0; i < parts.length; ++i)
                    tempValues[i] = Short.parseShort(parts[i]);

                AppendLogText("Opening /dev/tw with values: " + _values);
                m_test.open(tempValues);
            }
            else
            {
                if(parts.length < 2)
                {
                    AppendLogText("Invalid values for range start/end.");
                    return;
                }

                short start = Short.parseShort(parts[0]);
                short end = Short.parseShort(parts[1]);

                if(start >= end)
                {
                    AppendLogText("Range start should be smaller than range end.");
                    return;
                }

                short[] values = new short[end - start + 1];
                StringBuilder logtext = new StringBuilder();
                for(short i=0; i<=(end - start); ++i)
                {
                    values[i] = (short)(start + i);
                    logtext.append(String.valueOf(values[i])).append(" ");
                }

                AppendLogText("Opening /dev/tw with values: " + logtext);
                m_test.open(values);
            }
        }
        catch (Exception e)
        {
            AppendLogText(e.getMessage());
        }
    }

    public void closeDevice(View view)
    {
        if(m_test != null)
        {
            m_test.close();
            AppendLogText("Closed /dev/tw.");
        }
    }

    public void toggleHVACScreen(View view)
    {
        if(m_test == null)
            m_test = new TWUtilTest(this);

        try
        {
            m_test.TryToggleHVACScreen();
        }
        catch (Exception e)
        {
            AppendLogText(e.getMessage());
        }
    }

    public void write(View view)
    {
        try
        {
            short op = Short.parseShort(((TextView)findViewById(R.id.opTextBox)).getText().toString());
            short arg1 = Short.parseShort(((TextView)findViewById(R.id.arg1TextBox)).getText().toString());
            short arg2 = Short.parseShort(((TextView)findViewById(R.id.arg2TextBox)).getText().toString());

            if(m_test != null)
            {
                m_test.write(op, arg1, arg2);
            }
            else
                AppendLogText("Write: Device not open!");
        }
        catch(Exception e)
        {
            AppendLogText(e.getMessage());
        }
    }

    public void clearLog(View view)
    {
        TextView v = (TextView)findViewById(R.id.textView);
        if(v != null)
            v.setText("");
    }

    public void AppendLogText(String str)
    {
        str += "\r\n";
        TextView v = (TextView)findViewById(R.id.textView);
        if(v != null)
            v.append(str);
    }
}