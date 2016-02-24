package no.charlie.rsvpapp.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

public class SMSBroadcastReceiver extends BroadcastReceiver {
    public static final String ACTION = "android.provider.Telephony.SMS_RECEIVED";
    public static final String OTP_RECEIVED_ACTION = "no.charlie.rsvpapp.OTP_RECEIVED";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION)) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Object[] pdus = (Object[]) bundle.get("pdus");
                SmsMessage[] messages = new SmsMessage[pdus.length];
                for (int i = 0; i < pdus.length; i++){
                    messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                }
                for (SmsMessage message : messages) {
                    String strMessageBody = message.getDisplayMessageBody();
                    Intent otpIntent = new Intent();
                    String otp = strMessageBody.trim().split("-")[0];
                    otpIntent.putExtra("otp", otp);
                    otpIntent.setAction(OTP_RECEIVED_ACTION);
                    context.sendBroadcast(otpIntent);
                }
            }
        }
    }
}
