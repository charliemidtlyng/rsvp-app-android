package no.charlie.rsvpapp.notification;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

import static android.app.AlarmManager.INTERVAL_DAY;
import static android.app.AlarmManager.RTC_WAKEUP;
import static java.util.TimeZone.getTimeZone;

public class ScheduleNotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED") ||
                intent.getAction().equals("no.charlie.rsvpapp.APP_STARTED")) {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            Calendar calendar = Calendar.getInstance(getTimeZone("GMT+1"));
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, 8);
            calendar.set(Calendar.MINUTE, 30);
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);

            Intent notificationIntent = new Intent(context, NotificationPublisher.class);
            PendingIntent pendingNotificationIntent = PendingIntent.getBroadcast(context, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

            alarmManager.setRepeating(RTC_WAKEUP, calendar.getTimeInMillis(), INTERVAL_DAY * 7, pendingNotificationIntent);
        }
    }

}
