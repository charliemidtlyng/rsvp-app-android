package no.charlie.rsvpapp.service;

import java.util.List;

import no.charlie.rsvpapp.domain.Event;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by charlie midtlyng on 25/02/15.
 */
public interface EventService {
    @GET("/api/events")
    void findEvents(Callback<List<Event>> trackCallback);

    @GET("/api/events/upcoming")
    void findUpcomingEvents(Callback<List<Event>> trackCallback);

    @GET("/api/events/{id}")
    void findEvent(@Path("id") Long id, Callback<Event> trackCallback);
}
