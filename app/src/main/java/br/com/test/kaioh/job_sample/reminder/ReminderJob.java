package br.com.test.kaioh.job_sample.reminder;

import android.support.annotation.NonNull;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobRequest;
import com.evernote.android.job.util.support.PersistableBundleCompat;

/**
 * Created by @autor Kaio Henrique on 29/01/2018.
 */

public class ReminderJob extends Job {

    public static final String TAG = "ReminderJob";

    private static final String EXTRA_ID = "EXTRA_ID";

    static int schedule(@NonNull Reminder reminder) {
        PersistableBundleCompat extras = new PersistableBundleCompat();
        extras.putInt(EXTRA_ID, reminder.getmId());

        long time = Math.max(1L, reminder.getmTimestamp() - System.currentTimeMillis());

        return new JobRequest.Builder(TAG)
                .setExact(time)
                .setExtras(extras)
                .setUpdateCurrent(false)
                .build()
                .schedule();
    }

    @NonNull
    @Override
    protected Result onRunJob(@NonNull Params params) {
        int id = params.getExtras().getInt(EXTRA_ID, -1);
        if (id < 0) {
            return Result.FAILURE;
        }

        Reminder reminder = ReminderEngine.instance().getReminderById(id);
        if (reminder == null){
            return Result.FAILURE;
        }

        int index = ReminderEngine.instance().getReminders().indexOf(reminder);

        ReminderEngine.instance().showReminder(reminder);
        ReminderEngine.instance().removeReminder(index, false);

        return Result.SUCCESS;
    }
}
