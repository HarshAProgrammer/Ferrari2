package com.rackluxury.ferrari.reddit.broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.rackluxury.ferrari.reddit.services.MaterialYouService;

public class WallpaperChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent materialYouIntent = new Intent(context, MaterialYouService.class);
        context.startService(materialYouIntent);
    }
}
