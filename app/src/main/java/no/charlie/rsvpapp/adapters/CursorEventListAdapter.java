package no.charlie.rsvpapp.adapters;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import no.charlie.rsvpapp.EventActivity;
import no.charlie.rsvpapp.R;
import no.charlie.rsvpapp.domain.Event;

import static android.app.ActivityOptions.makeSceneTransitionAnimation;

public class CursorEventListAdapter extends CursorRecyclerViewAdapter<CursorEventListAdapter.EventListViewHolder> {

    private final LayoutInflater inflater;

    public CursorEventListAdapter(Cursor cursor, LayoutInflater inflater) {
        super(cursor);
        this.inflater = inflater;
    }

    @Override
    public EventListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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
                Cursor cursor = getCursor();
                cursor.moveToPosition(position);
                Event event = Event.fromCursor(cursor);
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
    public void onBindViewHolder(EventListViewHolder holder, Cursor cursor) {
        Event event = Event.fromCursor(cursor);

        holder.subject.setText(event.subject);
        holder.day.setText(event.day());
        holder.date.setText(event.startTimeString());
        holder.remainingSpots.setText(event.remainingSpots());
        if (event.startTime.isBeforeNow()) {
            holder.eventItem.setBackgroundColor(Color.DKGRAY);
        } else {
            holder.eventItem.setBackgroundColor(0xffe9555f);
        }
    }

    public static class EventListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        View view;
        TextView subject, day, date, remainingSpots;
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
            eventClickListener.onClick(v, getAdapterPosition());
        }

        public interface EventClickListener {
            void onClick(View view, int position);
        }

        @Override
        public boolean onLongClick(View view) {
            //return longTapDelegate.longTappedItemAtPosition(position);
            return false;
        }
    }
}
