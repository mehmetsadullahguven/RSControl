package com.msg.rscontrol.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.msg.rscontrol.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class NotificationFragment extends Fragment {

    public static final String TAG = "RSC";

    private TextView logText;

    static ArrayList<String> logs = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        logText = view.findViewById(R.id.logText);

        getToLogs();

        return view;
    }

    // Logcat'e mesaj yazmak için yardımcı metot
    public static void addToLog(String message)
    {
        if (message.equals("exit")) {
            logs = new ArrayList<>();
        } else {
            SimpleDateFormat s = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
            String timeStamp = s.format(new Date());
            String logMessage = "[ " + timeStamp + " ] " + message;

            logs.add(logMessage);
            if (logs.size() >= 20) {
                logs.remove(0);
            }

            Log.d(TAG, logMessage);
        }
    }

    private void getToLogs()
    {
        logText.setText(String.join("\n", logs));
    }
}
