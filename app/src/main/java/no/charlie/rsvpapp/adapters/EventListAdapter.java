package no.charlie.rsvpapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import no.charlie.rsvpapp.EventActivity;
import no.charlie.rsvpapp.R;
import no.charlie.rsvpapp.domain.Event;
import no.charlie.rsvpapp.util.FontResolver;

import static android.app.ActivityOptions.makeSceneTransitionAnimation;

/**
 * Created by charlie midtlyng on 25/02/15.
 */
public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.EventListViewHolder> {


    private LayoutInflater inflater;
    private List<Event> events;
    private Context context;
    private int lastPosition = -1;

    public EventListAdapter(LayoutInflater inflater, List<Event> events, Context context) {
        this.inflater = inflater;
        this.events = events;
        this.context = context;
    }

    @Override
    public EventListAdapter.EventListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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
                Event event = events.get(position);
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
    public void onBindViewHolder(EventListViewHolder holder, int pos) {
        Event event = events.get(pos);
        holder.subject.setText(event.subject);
        holder.day.setText(event.day());
        holder.date.setText(event.startTimeString());
        holder.position = pos;
        setAnimation(holder.itemView, pos);
    }

    @Override
    public int getItemCount() {
        return events.size();
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
