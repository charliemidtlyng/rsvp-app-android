package no.charlie.rsvpapp.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.joda.time.DateTime;

import java.lang.reflect.Type;
import java.util.Date;

import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

/**
 * Created by charlie midtlyng on 25/02/15.
 */
public class ApiClient {

    private static EventService eventService;


    private static final JsonSerializer<DateTime> dateTimeSerializer = new JsonSerializer<DateTime>() {
        @Override
        public JsonElement serialize(DateTime src, Type typeOfSrc, JsonSerializationContext context) {
            return src == null ? null : new JsonPrimitive(src.getMillis());
        }
    };


    public static EventService getService() {
        if (eventService != null) {
            return eventService;
        }
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(DateTime.class, new JsonDeserializer<DateTime>() {
                    @Override
                    public DateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                        return json == null ? null : new DateTime(json.getAsJsonPrimitive().getAsLong());
                    }
                })
                .registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                    @Override
                    public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                        return json == null ? null : new Date(json.getAsJsonPrimitive().getAsLong());
                    }
                })
                .create();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://paamelding.herokuapp.com/")
                .setConverter(new GsonConverter(gson))
                .build();

        eventService = restAdapter.create(EventService.class);
        return eventService;

    }

}
