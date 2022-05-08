package com.example.shop;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class NotificationHandler {
    private static final String CHANNEL_ID = "shop_notification_channel";
    private final int NOTIFICATION_ID = 0;

    private Context fContext;
    private NotificationManager fNotificationManager;
    public NotificationHandler(Context context) {
        this.fContext = context;
        this.fNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        createChannel();
    }

    private void createChannel(){
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
            return;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Shop notification", NotificationManager.IMPORTANCE_DEFAULT);

        channel.enableLights(true);
        channel.enableVibration(true);
        channel.setLightColor(Color.RED);
        channel.setDescription("Notifications from Shop app");
        this.fNotificationManager.createNotificationChannel(channel);
    }

    public void send(String message){

        NotificationCompat.Builder builder = new NotificationCompat.Builder(fContext, CHANNEL_ID)
                .setContentTitle("Shop App")
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_shopping_cart);

        this.fNotificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}
