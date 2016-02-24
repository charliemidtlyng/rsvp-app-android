package no.charlie.rsvpapp.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import no.charlie.rsvpapp.R;
import no.charlie.rsvpapp.domain.Event;
import no.charlie.rsvpapp.domain.EventWrapper;
import no.charlie.rsvpapp.domain.Participant;
import no.charlie.rsvpapp.handler.DialogHandler;
import no.charlie.rsvpapp.service.ApiClient;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by charlie midtlyng on 25/02/15.
 */
public class ParticipantAdapter extends RecyclerView.Adapter<ParticipantAdapter.ParticipantViewHolder> {


    private LayoutInflater inflater;
    private EventWrapper eventWrapper;
    private Context context;


    public ParticipantAdapter(LayoutInflater inflater, EventWrapper eventWrapper, Context context) {
        this.inflater = inflater;
        this.eventWrapper = eventWrapper;
        this.context = context;
    }

    @Override
    public ParticipantAdapter.ParticipantViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemMessage = inflater.inflate(R.layout.participant_item, parent, false);
        return new ParticipantViewHolder(itemMessage, createParticipantClickListener());
    }

    private ParticipantViewHolder.ParticipantClickListener createParticipantClickListener() {
        return new ParticipantViewHolder.ParticipantClickListener() {
            @Override
            public void onClick(View view, int position) {
                Participant participant = eventWrapper.participants().get(position);
                DialogHandler appdialog = new DialogHandler();
                appdialog.Confirm(view.getContext(),
                        "Meld av?",
                        "Vil du melde av " + participant.name,
                        "Cancel", "OK",
                        unattend(position)
                );
            }
        };
    }

    public Runnable unattend(final int position) {
        return new Runnable() {
            public void run() {
                System.out.println("unattended");
                Participant participant = eventWrapper.participants().get(position);
                ApiClient.getService().removeParticipant(eventWrapper.getEvent().id, participant.id, new Callback<Event>() {
                    @Override
                    public void success(Event event, Response response) {
                        eventWrapper.participants().remove(position);
                        notifyDataSetChanged();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        System.out.println(error.getMessage());
                        Toast.makeText(context, R.string.unattendFailed, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };
    }

    @Override
    public void onBindViewHolder(ParticipantViewHolder holder, int pos) {
        Participant participant = eventWrapper.participants().get(pos);
        String name = participant.reserve ? participant.name + "(venteliste)" : participant.name;
        holder.name.setText(name);
        if (participant.reserve) {
            holder.name.setTextColor(Color.DKGRAY);
        }
    }

    @Override
    public int getItemCount() {
        return eventWrapper.participants().size();
    }

    public static class ParticipantViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView name;
        ParticipantClickListener participantClickListener;

        public ParticipantViewHolder(View itemView, ParticipantClickListener participantClickListener) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            itemView.setOnClickListener(this);
            this.participantClickListener = participantClickListener;
        }

        @Override
        public void onClick(View view) {
            participantClickListener.onClick(view, getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View view) {
            //return longTapDelegate.longTappedItemAtPosition(position);
            return false;
        }

        public interface ParticipantClickListener {
            void onClick(View view, int position);
        }

    }

}
