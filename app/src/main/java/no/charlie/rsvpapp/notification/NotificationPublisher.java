package no.charlie.rsvpapp.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.List;

import no.charlie.rsvpapp.R;
import no.charlie.rsvpapp.VerifyActivity;
import no.charlie.rsvpapp.domain.Event;
import no.charlie.rsvpapp.service.ApiClient;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class NotificationPublisher extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {
        getEventsAndTriggerNotifications(context);
    }

    public static void getEventsAndTriggerNotifications(final Context context) {
        ApiClient.getService().findUpcomingEvents(new Callback<List<Event>>() {
            @Override
            public void success(List<Event> events, Response response) {
                for (Event event : events) {
                    if (event.regStart.isBeforeNow()) {
                        createNotification(context, event);
                    }
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(getClass().getName(), "An error occured when retrieving events.");
            }
        });
    }

    private static void createNotification(Context context, Event event) {
        Intent intent = new Intent(context, VerifyActivity.class);
        Bundle bundle = new Bundle();
        bundle.putLong("eventId", event.id);
        intent.putExtras(bundle);
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, 0);

        Notification.Action meldPåAction = new Notification.Action.Builder(android.R.drawable.ic_input_add, "Meld på", pIntent)
                .build();
        Notification notification = new Notification.Builder(context)
                .setContentTitle(event.subject)
                .setContentText("Påmeldingen er nå åpen!")
                .setSmallIcon(R.mipmap.ic_launcher)
                .addAction(meldPåAction)
                .build();
        triggerNotification(context, notification, event.id.intValue());
    }

    private static void triggerNotification(Context context, Notification notification, int id) {
        NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotifyMgr.notify(id, notification);
    }

}
