package no.charlie.rsvpapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.Collections;
import java.util.List;

import no.charlie.rsvpapp.adapters.CursorEventListAdapter;
import no.charlie.rsvpapp.domain.Event;
import no.charlie.rsvpapp.persistence.EventDbHelper;
import no.charlie.rsvpapp.persistence.EventDbHelper.CreationResult;
import no.charlie.rsvpapp.service.ApiClient;
import no.charlie.rsvpapp.service.SchedulePollingReceiver;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static android.text.TextUtils.isEmpty;
import static no.charlie.rsvpapp.notification.NotificationPublisher.scheduleNotificationFor;


public class EventListActivity extends AppCompatActivity {

    private RecyclerView eventListView;

    private EventDbHelper db;
    private AlarmManager alarmManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);
        final EventListActivity context = this;
        eventListView = (RecyclerView) findViewById(R.id.eventList);
        final LinearLayoutManager layout = new LinearLayoutManager(context);
        eventListView.setLayoutManager(layout);

        this.db = new EventDbHelper(this);
        CursorEventListAdapter eventListAdapter = new CursorEventListAdapter(db.list(), context.getLayoutInflater());
        eventListView.setAdapter(eventListAdapter);

        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        scheduleInitialAlarm();
        ensureNameAndPhoneSet();
    }

    private void ensureNameAndPhoneSet() {
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String name = SP.getString("currentName", null);
        String phone = SP.getString("currentPhone", null);
        if (isEmpty(name) || isEmpty(phone)) {
            Intent i = new Intent(this, UserSettingActivity.class);
            startActivity(i);
        }
    }

    private void scheduleInitialAlarm() {
        Intent alarmIntent = new Intent(this, SchedulePollingReceiver.class);
        alarmIntent.setAction("no.charlie.rsvpapp.APP_STARTED");
        try {
            PendingIntent.getBroadcast(this, 0, alarmIntent, 0).send();
        } catch (PendingIntent.CanceledException e) {
            Log.d(getClass().getName(), "Unable to send intent for scheduling alarm.");
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_event_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent i = new Intent(this, UserSettingActivity.class);
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchEvents();
    }


    private CursorEventListAdapter getAdapter() {
        return (CursorEventListAdapter) eventListView.getAdapter();
    }

    private void fetchEvents() {
        setProgressBarIndeterminateVisibility(true);
        ApiClient.getService().findUpcomingEvents(new Callback<List<Event>>() {
            @Override
            public void success(List<Event> events, Response response) {
                Collections.sort(events);
                for (Event event : events) {
                    CreationResult result = db.create(event);
                    if (result == CreationResult.CREATED) {
                        scheduleNotificationFor(event, getApplicationContext(), alarmManager);
                    }
                }
                getAdapter().changeCursor(db.list());
                setProgressBarIndeterminateVisibility(false);
            }

            @Override
            public void failure(RetrofitError error) {
                setProgressBarIndeterminateVisibility(false);
                Toast.makeText(EventListActivity.this, "Shit - kan ikke hente liste med data!", Toast.LENGTH_SHORT).show();
            }
        });

    }

}
