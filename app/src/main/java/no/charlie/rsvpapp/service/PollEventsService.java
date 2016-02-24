package no.charlie.rsvpapp.service;

import android.app.AlarmManager;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.List;

import no.charlie.rsvpapp.domain.Event;
import no.charlie.rsvpapp.persistence.EventDbHelper;
import no.charlie.rsvpapp.persistence.EventDbHelper.CreationResult;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static no.charlie.rsvpapp.notification.NotificationPublisher.scheduleNotificationFor;
import static no.charlie.rsvpapp.persistence.EventDbHelper.CreationResult.CREATED;

public class PollEventsService extends IntentService {

    private EventDbHelper db;
    private AlarmManager alarmManager;

    public PollEventsService() { // Needed for declaration in manifest. Do not remove!
        super("poll-events-service");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        db = new EventDbHelper(this);
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {
        Log.d(getClass().getName(), "Polling after new events");
        ApiClient.getService().findUpcomingEvents(new Callback<List<Event>>() {
            @Override
            public void success(List<Event> events, Response response) {
                Log.d(getClass().getName(), "Got " + events.size() + " events.");
                for (Event event : events) {
                    CreationResult result = db.create(event);
                    if (result == CREATED) {
                        scheduleNotificationFor(event, getApplicationContext(), alarmManager);
                    }

                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(getClass().getName(), "Unable to retrieve upcoming events");
            }
        });
    }
}
