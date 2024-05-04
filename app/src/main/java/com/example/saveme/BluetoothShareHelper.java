package com.example.saveme;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.core.content.FileProvider;

import java.io.File;

public class BluetoothShareHelper {


        public static void shareAppViaBluetooth(Context context) {
            Uri apkUri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", new File(context.getApplicationInfo().sourceDir));
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("application/vnd.android.package-archive");
            intent.putExtra(Intent.EXTRA_STREAM, apkUri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            intent.setPackage("com.android.bluetooth");
            context.startActivity(intent);
        }

}
