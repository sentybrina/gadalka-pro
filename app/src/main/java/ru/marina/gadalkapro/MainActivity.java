package ru.marina.gadalkapro;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    // 1 hour
    public static final long MAX_TIME_DELTA_SECONDS = 3600;
    public static final String LAST_INDEX = "lastIndex";
    public static final String PREV_TIME = "prevTime";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sp = getPreferences(MODE_PRIVATE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView textView = findViewById(R.id.mainTextView);
        textView.setClickable(true);
        textView.setOnClickListener(click -> createPrediction(textView, sp));
        createPrediction(textView, sp);
    }

    private void createPrediction(TextView textView, SharedPreferences sp) {
        String[] predictions = getResources().getStringArray(R.array.predictions);
        String toastText, mainText;
        long prevTime = sp.getLong(PREV_TIME, 0);
        long now = System.currentTimeMillis();
        long timeDeltaSeconds = (now - prevTime) / 1000;

        if (timeDeltaSeconds > MAX_TIME_DELTA_SECONDS) {
            toastText = getString(R.string.prediction_is_ready);

            int randomIndex = getRandomIndex(predictions.length);
            mainText = predictions[randomIndex];

            setNewPrevTimeValue(sp, now);
            setLastIndex(sp, randomIndex);
        } else {
            toastText = getTimeoutToastText(MAX_TIME_DELTA_SECONDS - timeDeltaSeconds);

            int lastIndex = sp.getInt(LAST_INDEX, 0);
            mainText = predictions[lastIndex];
        }
        textView.setText(mainText);
        Toast
                .makeText(this, toastText, Toast.LENGTH_SHORT)
                .show();
    }

    private void setNewPrevTimeValue(SharedPreferences sp, long now) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong(PREV_TIME, now);
        editor.apply();
    }

    private void setLastIndex(SharedPreferences sp, int index) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(LAST_INDEX, index);
        editor.apply();
    }

    @NonNull
    private String getTimeoutToastText(long timeDeltaSeconds) {
        String toastText;
        toastText = getString(R.string.prediction_timeout_pattern);
        long h = getHours(timeDeltaSeconds),
                m = getMinutes(timeDeltaSeconds),
                s = getSeconds(timeDeltaSeconds);
        toastText = String.format(toastText, h, m, s);
        return toastText;
    }

    private static int getRandomIndex(int max) {
        return new Random().nextInt(max);
    }

    private static long getHours(long seconds) {
        return seconds / 3600;
    }

    private static long getMinutes(long seconds) {
        return (seconds % 3600) / 60;
    }

    private static long getSeconds(long seconds) {
        return seconds % 60;
    }
}