package no.charlie.rsvpapp.adapters;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import no.charlie.rsvpapp.EventActivity;
import no.charlie.rsvpapp.R;
import no.charlie.rsvpapp.domain.Event;
import no.charlie.rsvpapp.domain.EventWrapper;

/**
 * Created by charlie midtlyng on 25/02/15.
 */
public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {


    private LayoutInflater inflater;
    private EventWrapper eventWrapper;
    private Context context;


    public EventAdapter(LayoutInflater inflater, EventWrapper eventWrapper, Context context) {
        this.inflater = inflater;
        this.eventWrapper = eventWrapper;
        this.context = context;
    }

    @Override
    public EventAdapter.EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemMessage = inflater.inflate(R.layout.event_details, parent, false);

        EventViewHolder eventHolder = new EventViewHolder(itemMessage);
        eventHolder.view = itemMessage;
        eventHolder.subject = (TextView) itemMessage.findViewById(R.id.subject);
        eventHolder.remainingSpots = (TextView) itemMessage.findViewById(R.id.remainingSpots);
        eventHolder.place = (TextView) itemMessage.findViewById(R.id.place);
        eventHolder.time = (TextView) itemMessage.findViewById(R.id.time);
        eventHolder.attend = (Button) itemMessage.findViewById(R.id.attend);
        eventHolder.attend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(context);
                String name = SP.getString("currentName", null);
                String phone = SP.getString("currentPhone", null);
                String mail = SP.getString("currentEmail", null);
                if (name == null || name == "") {
                    System.out.println("please select a name before attending");
                }
                System.out.println("Attending event with id" + eventWrapper.getEvent().id);
            }
        });
        return eventHolder;

    }

    @Override
    public void onBindViewHolder(EventViewHolder holder, int pos) {
        Event event = eventWrapper.getEvent();
        holder.subject.setText(event.subject);
        holder.remainingSpots.setText(event.remainingSpots());
        holder.place.setText(event.location);
        holder.time.setText(event.startTimeString());
        holder.position = pos;
    }

    @Override
    public int getItemCount() {
        int count = eventWrapper == null || eventWrapper.getEvent() == null ? 0 : 1;
        return count;
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        View view;
        TextView subject, remainingSpots, place, time;
        Button attend;
        int position;

        public EventViewHolder(View itemView) {
            super(itemView);
            this.view = itemView;
        }
    }

}
