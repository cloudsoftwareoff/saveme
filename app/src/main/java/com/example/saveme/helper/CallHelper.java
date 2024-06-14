package com.example.saveme.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.Manifest;
import androidx.core.app.ActivityCompat;

public class CallHelper {

    public static void makePhoneCall(Context context, String phoneNumber) {

        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + phoneNumber));

        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // Request permission if not granted
            ActivityCompat.requestPermissions((Activity) context, new String[] { Manifest.permission.CALL_PHONE }, 1);
            return;
        }

        context.startActivity(intent);
    }
}
