package no.charlie.rsvpapp.notification;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import no.charlie.rsvpapp.R;
import no.charlie.rsvpapp.VerifyActivity;
import no.charlie.rsvpapp.domain.Event;

import static android.app.AlarmManager.RTC_WAKEUP;

public class NotificationPublisher extends BroadcastReceiver {

    public static void scheduleNotificationFor(Event event, Context context, AlarmManager alarmManager) {
        Intent notificationIntent = new Intent(context, NotificationPublisher.class);
        notificationIntent.putExtra("event", event);
        PendingIntent pendingNotificationIntent = PendingIntent.getBroadcast(context, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        Log.d("RSVP-app", "Scheduling notification at " + event.regStartString());
        alarmManager.setExact(RTC_WAKEUP, event.regStart.getMillis(), pendingNotificationIntent);
    }

    public void onReceive(Context context, Intent intent) {
        Event event = (Event) intent.getSerializableExtra("event");
        createNotification(context, event);
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

        NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotifyMgr.notify(event.id.intValue(), notification);
    }

}
