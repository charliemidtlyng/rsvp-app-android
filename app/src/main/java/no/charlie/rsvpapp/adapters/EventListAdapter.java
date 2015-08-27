package no.charlie.rsvpapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import no.charlie.rsvpapp.EventActivity;
import no.charlie.rsvpapp.R;
import no.charlie.rsvpapp.domain.Event;

import static android.app.ActivityOptions.makeSceneTransitionAnimation;

/**
 * Created by charlie midtlyng on 25/02/15.
 */
public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.EventListViewHolder> {


    private LayoutInflater inflater;
    private List<Event> events;
    private Context context;


    public EventListAdapter(LayoutInflater inflater, List<Event> events, Context context) {
        this.inflater = inflater;
        this.events = events;
        this.context = context;
    }

    @Override
    public EventListAdapter.EventListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemMessage = inflater.inflate(R.layout.event_item, parent, false);
        EventListViewHolder eventListHolder = new EventListViewHolder(itemMessage, createEventClickListener());
        eventListHolder.view = itemMessage;
        eventListHolder.eventItem = (RelativeLayout) itemMessage.findViewById(R.id.eventItem);
        eventListHolder.subject = (TextView) itemMessage.findViewById(R.id.subject);
        eventListHolder.day = (TextView) itemMessage.findViewById(R.id.day);
        eventListHolder.date = (TextView) itemMessage.findViewById(R.id.date);
        eventListHolder.remainingSpots = (TextView) itemMessage.findViewById(R.id.remainingSpots);

        return eventListHolder;

    }

    private EventListViewHolder.EventClickListener createEventClickListener() {
        return new EventListViewHolder.EventClickListener() {
            @Override
            public void onClick(View view, int position) {
                Event event = events.get(position);
                Intent intent = new Intent(view.getContext(), EventActivity.class);
                Bundle bundle = new Bundle();
                bundle.putLong("eventId", event.id);
                intent.putExtras(bundle);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    view.getContext().startActivity(intent, makeSceneTransitionAnimation((Activity) view.getContext()).toBundle());
                } else {
                    view.getContext().startActivity(intent);
                }
            }
        };
    }

    @Override
    public void onBindViewHolder(EventListViewHolder holder, int pos) {
        Event event = events.get(pos);

        holder.subject.setText(event.subject);
        holder.day.setText(event.day());
        holder.date.setText(event.startTimeString());
        holder.remainingSpots.setText(event.remainingSpots());
        holder.position = pos;
        if (event.startTime.isBeforeNow()) {
            holder.eventItem.setBackgroundColor(Color.DKGRAY);
        } else {
            holder.eventItem.setBackgroundColor(0xffe9555f);
        }
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public static class EventListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        View view;
        TextView subject, day, date, remainingSpots;
        int position;
        EventClickListener eventClickListener;
        RelativeLayout eventItem;

        public EventListViewHolder(View itemView, EventClickListener eventClickListener) {
            super(itemView);
            this.view = itemView;
            this.eventClickListener = eventClickListener;
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            eventClickListener.onClick(v, getPosition());
        }

        public interface EventClickListener {
            public void onClick(View view, int position);
        }

        @Override
        public boolean onLongClick(View view) {
            //return longTapDelegate.longTappedItemAtPosition(position);
            return false;
        }
    }

}
