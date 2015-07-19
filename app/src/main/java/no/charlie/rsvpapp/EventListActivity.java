package no.charlie.rsvpapp;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import no.charlie.rsvpapp.adapters.EventListAdapter;
import no.charlie.rsvpapp.domain.Event;
import no.charlie.rsvpapp.service.ApiClient;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import android.support.v7.widget.*;
import android.widget.Toast;


public class EventListActivity extends ActionBarActivity {

    private List<Event> events = new ArrayList<Event>();
    private RecyclerView eventListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);
        final EventListActivity context = this;
        eventListView = (RecyclerView) findViewById(R.id.eventList);
        final LinearLayoutManager layout = new LinearLayoutManager(context);
        eventListView.setLayoutManager(layout);
        final EventListAdapter eventListAdapter = new EventListAdapter(context.getLayoutInflater(), events, context);
        eventListView.setAdapter(eventListAdapter);
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


    private EventListAdapter getAdapter() {
        return (EventListAdapter) eventListView.getAdapter();
    }

    private void fetchEvents() {
        setProgressBarIndeterminateVisibility(true);
        ApiClient.getService().findEvents(new Callback<List<Event>>() {
            @Override
            public void success(List<Event> events, Response response) {
                System.out.println("-----------------------------");
                System.out.println("Fetch events");
                Collections.sort(events);
                EventListActivity.this.events.clear();
                EventListActivity.this.events.addAll(events);
                getAdapter().notifyDataSetChanged();
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
