package br.com.test.kaioh.job_sample;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import br.com.test.kaioh.job_sample.reminder.Reminder;
import br.com.test.kaioh.job_sample.reminder.ReminderEngine;

public class MainActivity extends AppCompatActivity implements ReminderEngine.ReminderChangeListener {

    private static final long REMINDER_LIMIT = TimeUnit.MINUTES.toMillis(1);

    private List<Reminder> mReminders;
    private ReminderAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mReminders = ReminderEngine.instance().getReminders();
        ReminderEngine.instance().addListener(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mAdapter = new ReminderAdapter();

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);

        ItemTouchHelper.SimpleCallback swipeCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                ReminderEngine.instance().removeReminder(viewHolder.getAdapterPosition());
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long when = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(10) + (new Random().nextLong() % REMINDER_LIMIT);
                ReminderEngine.instance().createNewReminder(when);
            }
        });
    }

    @Override
    protected void onDestroy() {
        ReminderEngine.instance().removeListener(this);
        super.onDestroy();
    }

    @Override
    public void onReminderAdded(final int position, Reminder reminder) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.notifyItemInserted(position);
            }
        });
    }

    @Override
    public void onReminderRemoved(final int position, Reminder reminder) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.notifyItemRemoved(position);
            }
        });
    }

    @SuppressWarnings("WeakerAccess")
    private class ReminderAdapter extends RecyclerView.Adapter<ReminderViewHolder> {

        private final SimpleDateFormat mDateFormat;

        public ReminderAdapter() {
            mDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        }

        @Override
        public ReminderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
            return new ReminderViewHolder(view);
        }

        @Override
        public int getItemViewType(int position) {
            return R.layout.content_main;
        }

        @Override
        public int getItemCount() {
            return mReminders.size();
        }

        @Override
        public void onBindViewHolder(ReminderViewHolder holder, int position) {
            holder.mTextView.setText(mDateFormat.format(new Date(mReminders.get(position).getmTimestamp())));
            holder.mTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(MainActivity.this, "bla", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @SuppressWarnings("WeakerAccess")
    private class ReminderViewHolder extends RecyclerView.ViewHolder {

        private final TextView mTextView;

        public ReminderViewHolder(View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.text_view);
        }
    }
}
