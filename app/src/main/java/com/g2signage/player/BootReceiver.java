package com.g2signage.player;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Inicia o player automaticamente quando o dispositivo liga.
 * Útil para TVs e TV Boxes que reiniciam por energia.
 */
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Intent i = new Intent(context, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }
}