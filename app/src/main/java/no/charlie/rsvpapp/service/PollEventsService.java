package no.charlie.rsvpapp.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import java.util.List;

import no.charlie.rsvpapp.domain.Event;
import no.charlie.rsvpapp.persistence.EventDbHelper;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class PollEventsService extends IntentService {

    private EventDbHelper db;

    @SuppressWarnings("unused")
    public PollEventsService() { // Needed for declaration in manifest. Do not remove!
        this("poll-events-service");
    }

    public PollEventsService(String name) {
        super(name);
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
                    db.create(event);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(getClass().getName(), "Unable to retrieve upcoming events");
            }
        });
    }
}
