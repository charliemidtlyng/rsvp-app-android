package no.charlie.rsvpapp.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import static android.app.AlarmManager.INTERVAL_HALF_DAY;
import static android.app.AlarmManager.RTC;

public class SchedulePollingReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.w(getClass().getName(), "yeah");
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED") ||
                intent.getAction().equals("no.charlie.rsvpapp.APP_STARTED")) {
            Log.w(getClass().getName(), intent.getAction());
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            Intent pollEventServiceIntent = new Intent(context, PollEventsService.class);
            PendingIntent pendingIntent = PendingIntent.getService(context, 0, pollEventServiceIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            alarmManager.setRepeating(RTC, System.currentTimeMillis(), INTERVAL_HALF_DAY, pendingIntent);
        }
    }

}
