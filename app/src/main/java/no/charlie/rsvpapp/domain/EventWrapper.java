package no.charlie.rsvpapp.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by charlie midtlyng on 19/07/15.
 */
public class EventWrapper {
    private Event event;

    public void setEvent(Event event) {
        this.event = event;
    }

    public Event getEvent() {
        return event;
    }

    public List<Participant> participants() {
        return event == null ? new ArrayList<Participant>() : event.participants;
    }
}
