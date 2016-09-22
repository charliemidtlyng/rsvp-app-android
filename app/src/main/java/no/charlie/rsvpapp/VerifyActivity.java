package no.charlie.rsvpapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import no.charlie.rsvpapp.domain.Event;
import no.charlie.rsvpapp.service.ApiClient;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

import static android.app.ActivityOptions.makeSceneTransitionAnimation;


public class VerifyActivity extends AppCompatActivity implements View.OnClickListener {

    private Long eventId;
    private EditText answer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventId = (Long) getIntent().getExtras().get("eventId");
        setContentView(R.layout.activity_captcha);

        this.answer = (EditText) this.findViewById(R.id.answer);

        this.findViewById(R.id.verify).setOnClickListener(this);
        this.sendOtp();
    }

    private void sendOtp() {
        Map<String, String> postValues = new HashMap<>();
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        postValues.put("name", SP.getString("currentName", null));
        postValues.put("phoneNumber", SP.getString("currentPhone", null));
        ApiClient.getService().sendOtp(eventId, postValues, new Callback<Event>() {
            @Override
            public void success(Event event, Response response) {
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(getApplicationContext(), R.string.smsFailed, Toast.LENGTH_SHORT).show();
                Log.w(getLocalClassName(), "Sending av engangspassord feilet", error);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_event_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent i = new Intent(this, UserSettingActivity.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(final View view) {
        this.verifyAnswer(view);

    }

    private void verifyAnswer(final View view) {
        if (TextUtils.isEmpty(this.answer.getText())) {
            Toast.makeText(this, R.string.instruction, Toast.LENGTH_SHORT).show();
        } else {
            SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(view.getContext());
            Map<String, String> postValues = new HashMap<>();
            postValues.put("name", SP.getString("currentName", null));
            postValues.put("phoneNumber", SP.getString("currentPhone", null));
            postValues.put("email", SP.getString("currentEmail", null));
            postValues.put("otp", this.answer.getText().toString());
            ApiClient.getService().registerToEvent(eventId, postValues, new Callback<Event>() {
                @Override
                public void success(Event event, Response response) {
                    Intent intent = new Intent(view.getContext(), EventActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putLong("eventId", event.id);
                    intent.putExtras(bundle);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        view.getContext().startActivity(intent, makeSceneTransitionAnimation((Activity) view.getContext()).toBundle());
                    } else {
                        view.getContext().startActivity(intent);
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    String errorMessage = new String(((TypedByteArray) error.getResponse().getBody()).getBytes());
                    Log.w(getClass().getName(), "Registration failed, message from server: " + errorMessage, error);
                    Toast.makeText(view.getContext(), R.string.registrationFailed, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}
