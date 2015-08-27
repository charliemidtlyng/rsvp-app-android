package no.charlie.rsvpapp.service;

import java.util.List;
import java.util.Map;

import no.charlie.rsvpapp.domain.Event;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.Field;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

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

    @POST("/api/events/{id}/register")
    void registerToEvent(@Path("id") Long id, @Body Map valueMap, Callback<Event> trackCallback);

    @POST("/api/events/{id}/otp")
    void sendOtp(@Path("id") Long id, @Body Map valueMap, Callback<Event> callback);

    @DELETE("/api/events/{eventId}/register/{participantId}")
    void removeParticipant(@Path("eventId") Long eventId, @Path("participantId") Long participantId, Callback<Event> callback);
}
