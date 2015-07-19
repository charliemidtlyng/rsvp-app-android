package no.charlie.rsvpapp.domain;

import org.joda.time.DateTime;

/**
 * Created by charlie midtlyng on 25/02/15.
 */
public class Participant {
    public Long id;
    public Event event;
    public String name;
    public String email;
    public String phoneNumber;
    public Boolean reserve;
    public DateTime timestamp;

    @Override
    public String toString() {
        return "Participant{" +
                "id=" + id +
                ", event=" + event +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", reserve=" + reserve +
                ", timestamp=" + timestamp +
                '}';
    }
}
