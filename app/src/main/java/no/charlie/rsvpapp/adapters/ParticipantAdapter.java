package no.charlie.rsvpapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import no.charlie.rsvpapp.R;
import no.charlie.rsvpapp.domain.EventWrapper;
import no.charlie.rsvpapp.domain.Participant;
import no.charlie.rsvpapp.handler.DialogHandler;

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
        ParticipantViewHolder holder = new ParticipantViewHolder(itemMessage, createParticipantClickListener());
        holder.view = itemMessage;
        holder.name = (TextView) itemMessage.findViewById(R.id.name);

        return holder;

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
                eventWrapper.participants().remove(position);
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public void onBindViewHolder(ParticipantViewHolder holder, int pos) {
        Participant participant = eventWrapper.participants().get(pos);
        holder.name.setText(participant.name);
        holder.position = pos;
    }

    @Override
    public int getItemCount() {
        return eventWrapper.participants().size();
    }

    public static class ParticipantViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        View view;
        TextView name;
        int position;
        ParticipantClickListener participantClickListener;

        public ParticipantViewHolder(View itemView, ParticipantClickListener participantClickListener) {
            super(itemView);
            this.view = itemView;
            view.setOnClickListener(this);
            this.participantClickListener = participantClickListener;
        }

        @Override
        public void onClick(View view) {
            participantClickListener.onClick(view, getPosition());
        }

        @Override
        public boolean onLongClick(View view) {
            //return longTapDelegate.longTappedItemAtPosition(position);
            return false;
        }

        public interface ParticipantClickListener {
            public void onClick(View view, int position);
        }

    }

}
