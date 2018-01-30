package br.com.test.kaioh.job_sample.reminder;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;

import com.evernote.android.job.JobManager;

import net.vrallev.android.context.AppContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import br.com.test.kaioh.job_sample.MainActivity;
import br.com.test.kaioh.job_sample.R;

/**
 * Created by @autor Kaio Henrique on 29/01/2018.
 */

@SuppressWarnings({"WeakerAccess", "UnusedReturnValue"})
public class ReminderEngine {

    private static final String REMINDER_ID = "REMINDER_ID";
    private static final String REMINDERS = "REMINDERS";

    @SuppressLint("StaticFieldLeak")
    private static final ReminderEngine INSTANCE = new ReminderEngine();

    public static ReminderEngine instance() {
        return INSTANCE;
    }

    private final Context mContext;
    private final SharedPreferences mPreferences;

    private final List<Reminder> mReminders;

    private final List<ReminderChangeListener> mListeners;

    private int mReminderId;

    private ReminderEngine() {
        mContext = AppContext.get();
        mPreferences = mContext.getSharedPreferences("reminders", Context.MODE_PRIVATE); // poor-man's storage
        mReminderId = mPreferences.getInt(REMINDER_ID, 0);

        mReminders = new ArrayList<>();
        mListeners = new ArrayList<>();

        Set<String> reminders = mPreferences.getStringSet(REMINDERS, null);
        if (reminders != null) {
            for (String value : reminders) {
                mReminders.add(Reminder.fromString(value));
            }
        }
        Collections.sort(mReminders);
    }

    public void addListener(ReminderChangeListener listener) {
        mListeners.add(listener);
    }

    public void removeListener(ReminderChangeListener listener) {
        mListeners.remove(listener);
    }

    public List<Reminder> getReminders() {
        return Collections.unmodifiableList(mReminders);
    }

    @Nullable
    public Reminder getReminderById(int id) {
        for (Reminder reminder : mReminders) {
            if (reminder.getmId() == id) {
                return reminder;
            }
        }
        return null;
    }

    public Reminder createNewReminder(long timestamp) {
        mReminderId++;

        Reminder reminder = new Reminder(mReminderId, timestamp);
        mReminders.add(reminder);
        Collections.sort(mReminders);

        int jobId = ReminderJob.schedule(reminder);
        reminder.setmJobId(jobId);

        mPreferences.edit().putInt(REMINDER_ID, mReminderId).apply();
        saveReminders();

        int position = mReminders.indexOf(reminder);
        for (ReminderChangeListener listener : mListeners) {
            listener.onReminderAdded(position, reminder);
        }

        return reminder;
    }

    public void removeReminder(int position) {
        removeReminder(position, true);
    }

    void removeReminder(int position, boolean cancelJob) {
        Reminder reminder = mReminders.remove(position);

        if (cancelJob) {
            JobManager.instance().cancel(reminder.getmJobId());
        }

        saveReminders();

        for (ReminderChangeListener listener : mListeners) {
            listener.onReminderRemoved(position, reminder);
        }
    }

    @SuppressWarnings("ConstantConditions")
    public void showReminder(Reminder reminder) {
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, new Intent(mContext, MainActivity.class), 0);

        String channelId = "reminder";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Job Sample", NotificationManager.IMPORTANCE_LOW);
            channel.setDescription("Job sample");
            mContext.getSystemService(NotificationManager.class).createNotificationChannel(channel);
        }

        Notification notification = new NotificationCompat.Builder(mContext, channelId)
                .setContentTitle("Reminder " + reminder.getmId())
                .setContentText("Hello Droidcon")
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_notify_alter_geq)
                .setShowWhen(true)
                .setColor(ContextCompat.getColor(mContext, R.color.colorPrimaryDark))
                .setLocalOnly(true)
                .build();

        NotificationManagerCompat.from(mContext)
                                 .notify(new Random().nextInt(), notification);
    }

    private void saveReminders() {
        Set<String> reminders = new HashSet<>();
        for (Reminder item : mReminders) {
            reminders.add(item.toPersistableString());
        }

        mPreferences.edit().putStringSet(REMINDERS, reminders).apply();
    }

    public interface ReminderChangeListener {
        void onReminderAdded(int position, Reminder reminder);

        void onReminderRemoved(int position, Reminder reminder);
    }
}
