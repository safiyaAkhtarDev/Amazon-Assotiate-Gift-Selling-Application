package com.safiyaakhtar.gifty.connection;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public final class NotificationServiceStarterReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        new NotificationEventReceiver(context);
    }
}