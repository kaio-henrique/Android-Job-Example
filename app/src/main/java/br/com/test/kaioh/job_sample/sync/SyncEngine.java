package br.com.test.kaioh.job_sample.sync;

import android.app.Notification;
import android.graphics.Color;
import android.os.SystemClock;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.NotificationCompat;

import net.vrallev.android.context.AppContext;

import java.io.IOException;
import java.util.Random;

import br.com.test.kaioh.job_sample.R;

/**
 * Created by @autor Kaio Henrique on 29/01/2018.
 */

@SuppressWarnings("WeakerAccess")
public class SyncEngine {

    public void syncReminders() throws IOException {
        SystemClock.sleep(3_000L);

        boolean error = Math.random() > 0.5;

        Notification notification = new NotificationCompat.Builder(AppContext.get())
                .setContentTitle("Sync")
                .setContentText(error ? "Sync Failed" : "Sync successful")
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_notify_alter_geq)
                .setShowWhen(true)
                .setColor(error ? Color.RED : Color.GREEN)
                .setLocalOnly(true)
                .build();

        NotificationManagerCompat.from(AppContext.get())
                                 .notify(new Random().nextInt(), notification);

        if (error) {
            throw new IOException("Dummy exception");
        }
    }
}
