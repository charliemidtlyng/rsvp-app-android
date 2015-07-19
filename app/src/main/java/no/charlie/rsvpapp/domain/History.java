package no.charlie.rsvpapp.domain;

import org.joda.time.DateTime;

/**
 * Created by charlie midtlyng on 25/02/15.
 */
public class History {
    public Long id;
    public Event event;
    public DateTime timestamp;
    public Change change;
    public String details;

    public static enum Change {
        Register,
        Unregister,
        Update
    }

    @Override
    public String toString() {
        return "History{" +
                "id=" + id +
                ", event=" + event +
                ", timestamp=" + timestamp +
                ", change=" + change +
                ", details='" + details + '\'' +
                '}';
    }
}
