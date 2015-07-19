package no.charlie.rsvpapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import no.charlie.rsvpapp.adapters.EventAdapter;
import no.charlie.rsvpapp.adapters.EventListAdapter;
import no.charlie.rsvpapp.adapters.ParticipantAdapter;
import no.charlie.rsvpapp.domain.Event;
import no.charlie.rsvpapp.domain.EventWrapper;
import no.charlie.rsvpapp.service.ApiClient;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class EventActivity extends ActionBarActivity {

    private EventWrapper eventWrapper = new EventWrapper();
    private RecyclerView eventView;
    private RecyclerView participantView;
    private Long eventId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventId = (Long) getIntent().getExtras().get("eventId");
        setContentView(R.layout.activity_event);
        final EventActivity context = this;
        eventView = (RecyclerView) findViewById(R.id.event);
        participantView = (RecyclerView) findViewById(R.id.participants);
        final LinearLayoutManager layout = new LinearLayoutManager(context);
        final LinearLayoutManager layout2 = new LinearLayoutManager(context);
        eventView.setLayoutManager(layout);
        participantView.setLayoutManager(layout2);

        final EventAdapter eventAdapter = new EventAdapter(context.getLayoutInflater(), eventWrapper, context);
        final ParticipantAdapter participantAdapter = new ParticipantAdapter(context.getLayoutInflater(), eventWrapper, context);

        eventView.setAdapter(eventAdapter);
        participantView.setAdapter(participantAdapter);


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
        fetchEvents(eventId);
    }


    private EventAdapter getEventAdapter() {
        return (EventAdapter) eventView.getAdapter();
    }

    private ParticipantAdapter getParticipantAdapter() {
        return (ParticipantAdapter) participantView.getAdapter();
    }

    private void fetchEvents(Long id) {
        setProgressBarIndeterminateVisibility(true);
        ApiClient.getService().findEvent(id, new Callback<Event>() {
            @Override
            public void success(Event event, Response response) {
                System.out.println("-----------------------------");
                System.out.println("Fetch event");
                System.out.println(event);
                System.out.println(response);
                EventActivity.this.eventWrapper.setEvent(event);
                getEventAdapter().notifyDataSetChanged();
                getParticipantAdapter().notifyDataSetChanged();
                setProgressBarIndeterminateVisibility(false);
            }

            @Override
            public void failure(RetrofitError error) {
                setProgressBarIndeterminateVisibility(false);
                Toast.makeText(EventActivity.this, "Shit - kan ikke hente data på event!", Toast.LENGTH_SHORT).show();
            }
        });

    }

}
