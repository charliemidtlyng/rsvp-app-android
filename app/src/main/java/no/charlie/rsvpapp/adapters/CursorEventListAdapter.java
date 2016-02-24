package no.charlie.rsvpapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import no.charlie.rsvpapp.EventActivity;
import no.charlie.rsvpapp.R;
import no.charlie.rsvpapp.domain.Event;
import no.charlie.rsvpapp.util.FontResolver;

import static android.app.ActivityOptions.makeSceneTransitionAnimation;

public class CursorEventListAdapter extends CursorRecyclerViewAdapter<CursorEventListAdapter.EventListViewHolder> {

    private final LayoutInflater inflater;

    private LayoutInflater inflater;
    private int lastPosition = -1;

    public CursorEventListAdapter(Cursor cursor, LayoutInflater inflater) {
        super(cursor);
        this.inflater = inflater;
    }

    @Override
    public EventListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemMessage = inflater.inflate(R.layout.event_list_item, parent, false);
        return new EventListViewHolder(itemMessage, createEventClickListener());
    }

    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
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
                bundle.putString("subject", event.subject);
                intent.putExtras(bundle);
                view.getContext().startActivity(intent, makeSceneTransitionAnimation((Activity) context,
                        view.findViewById(R.id.list_item_image), "football_logo").toBundle());
            }
        };
    }

    @Override
    public void onBindViewHolder(EventListViewHolder holder, Cursor cursor) {
        Event event = Event.fromCursor(cursor);

        holder.subject.setText(event.subject);
        holder.day.setText(event.day());
        holder.date.setText(event.startTimeString());
//        holder.position = pos;
//        setAnimation(holder.itemView, pos);
        if (event.startTime.isBeforeNow()) {
            holder.eventItem.setBackgroundColor(Color.DKGRAY);
        } else {
            holder.eventItem.setBackgroundColor(0xffe9555f);
        }
    }

    public static class EventListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView subject, day, date;
        int position;
        EventClickListener eventClickListener;
        RelativeLayout eventItem;

        public EventListViewHolder(View itemView, EventClickListener eventClickListener) {
            super(itemView);
            day = (TextView) itemView.findViewById(R.id.day);
            date = (TextView) itemView.findViewById(R.id.date);
            subject = (TextView) itemView.findViewById(R.id.subject);
            subject.setTypeface(FontResolver.getHeaderFont(itemView.getContext()));
            day.setTypeface(FontResolver.getHeaderFont(itemView.getContext()));
            date.setTypeface(FontResolver.getHeaderFont(itemView.getContext()));

            this.eventClickListener = eventClickListener;
            itemView.setOnClickListener(this);
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
