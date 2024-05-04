package com.example.saveme;

import android.content.Context;
import android.os.VibrationEffect;
import android.os.Vibrator;

public class VibrationHelper {

    public static void vibrate(Context context, long milliseconds) {
        // Get instance of Vibrator from the current Context
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

        // Check if the device supports vibration
        if (vibrator != null && vibrator.hasVibrator()) {
            // Vibrate for the specified duration
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(milliseconds, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                // For older devices without VibrationEffect
                vibrator.vibrate(milliseconds);
            }
        }
    }
}

