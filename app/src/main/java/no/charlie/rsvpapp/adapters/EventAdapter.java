package no.charlie.rsvpapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import no.charlie.rsvpapp.R;
import no.charlie.rsvpapp.VerifyActivity;
import no.charlie.rsvpapp.domain.Event;
import no.charlie.rsvpapp.domain.EventWrapper;
import no.charlie.rsvpapp.handler.DialogHandler;

import static android.app.ActivityOptions.makeSceneTransitionAnimation;
import static android.text.TextUtils.isEmpty;

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
            public void onClick(View view) {
                SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(context);
                String name = SP.getString("currentName", null);
                String phone = SP.getString("currentPhone", null);
                Context context = view.getContext();
                if (isEmpty(name)) {
                    missingProperties("Legg til navn i innstillinger", context);
                } else if (isEmpty(phone)) {
                    missingProperties("Legg til mobilnr i innstillinger (pga verifisering)", context);
                } else {
                    Intent intent = new Intent(context, VerifyActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putLong("eventId", eventWrapper.getEvent().id);
                    intent.putExtras(bundle);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        context.startActivity(intent, makeSceneTransitionAnimation((Activity) context).toBundle());
                    } else {
                        context.startActivity(intent);
                    }
                }

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
    }

    @Override
    public int getItemCount() {
        return eventWrapper == null || eventWrapper.getEvent() == null ? 0 : 1;
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        View view;
        TextView subject, remainingSpots, place, time;
        Button attend;

        public EventViewHolder(View itemView) {
            super(itemView);
            this.view = itemView;
        }
    }

    private void missingProperties(String errorText, Context context) {
        DialogHandler appdialog = new DialogHandler();
        appdialog.SimpleAlert(context, "Oppdater innstillinger!", errorText, "OK");
    }
}
