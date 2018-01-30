package br.com.test.kaioh.job_sample;

import android.app.Application;

import com.evernote.android.job.JobManager;

import br.com.test.kaioh.job_sample.sync.SyncJob;

/**
 * Created by @autor Kaio Henrique on 29/01/2018.
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        JobManager.create(this).addJobCreator(new ReminderJobCreator());

        SyncJob.schedule();
    }
}
