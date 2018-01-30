package br.com.test.kaioh.job_sample;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;

import br.com.test.kaioh.job_sample.reminder.ReminderJob;
import br.com.test.kaioh.job_sample.sync.SyncJob;

/**
 * Created by @autor Kaio Henrique on 29/01/2018.
 */

public class ReminderJobCreator implements JobCreator {

    @Nullable
    @Override
    public Job create(@NonNull String tag) {
        switch (tag) {
            case ReminderJob.TAG:
                return new ReminderJob();

            case SyncJob.TAG:
                return new SyncJob();

            default:
                return null;
        }
    }
}
