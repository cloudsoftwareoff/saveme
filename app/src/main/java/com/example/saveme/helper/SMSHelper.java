package com.example.saveme.helper;

import android.content.pm.PackageManager;
import android.telephony.SmsManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.content.Context;
import android.widget.Toast;

public class SMSHelper {

    public static void sendMapLocationSMS(Context context,String prounous, String contact, String phoneNumber, double latitude,
            double longitude) {

        String mapLink = "https://maps.google.com/?q=" + latitude + "," + longitude;
        String message =prounous+ contact + " Needs help ,Location: " + mapLink;

        SmsManager smsManager = SmsManager.getDefault();

        PendingIntent sentIntent = PendingIntent.getBroadcast(context, 0, new Intent("SMS_SENT"),
                PendingIntent.FLAG_IMMUTABLE);

        // Check for permission to send SMS
        if (context.checkSelfPermission(android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            // Request permission if not granted
            Toast.makeText(context, "SMS permission not granted", Toast.LENGTH_SHORT).show();
            return;
        }

        // Send the SMS
        smsManager.sendTextMessage(phoneNumber, null, message, sentIntent, null);
    }
}
