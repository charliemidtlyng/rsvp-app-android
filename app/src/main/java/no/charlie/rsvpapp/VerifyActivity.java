package no.charlie.rsvpapp;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.security.Permission;
import java.util.HashMap;
import java.util.Map;

import no.charlie.rsvpapp.domain.Event;
import no.charlie.rsvpapp.service.ApiClient;
import no.charlie.rsvpapp.util.FontResolver;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static android.app.ActivityOptions.makeSceneTransitionAnimation;


public class VerifyActivity extends ActionBarActivity implements View.OnClickListener {
    public static final String OTP_RECEIVED_ACTION = "no.charlie.rsvpapp.OTP_RECEIVED";
    private static final int MY_PERMISSIONS_REQUEST_READ_SMS = 1;

    private Long eventId;
    private EditText answer;
    private Toolbar toolbar;
    private Button verifyButton;
    private BroadcastReceiver otpBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventId = (Long) getIntent().getExtras().get("eventId");
        setContentView(R.layout.activity_captcha);
        setupToolbar();
        this.answer = (EditText) this.findViewById(R.id.answer);
        this.verifyButton = (Button) this.findViewById(R.id.verify);

        verifyButton.setOnClickListener(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int permissionCheck = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_SMS);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_SMS},
                        MY_PERMISSIONS_REQUEST_READ_SMS);
            }
            else {
                this.sendOtp();
            }
        }
        else {
            this.sendOtp();
        }
        otpBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(OTP_RECEIVED_ACTION)) {
                    String otp = intent.getStringExtra("otp");
                    answer.setText(otp);
                    verifyButton.performClick();
                }
            }
        };
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_SMS: {
                this.sendOtp();
                return;
            }
        }
    }

    private void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        TextView toolbarTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        toolbarTitle.setVisibility(View.VISIBLE);
        toolbarTitle.setTypeface(FontResolver.getHeaderFont(this));
        toolbarTitle.setText("Verifisering");
        toolbar.setTitle("");

        ImageView toolbarLogo = (ImageView) toolbar.findViewById(R.id.toolbar_logo);
        toolbarLogo.setVisibility(View.GONE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
        else if (id == android.R.id.home) {
            supportFinishAfterTransition();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter(OTP_RECEIVED_ACTION);
        registerReceiver(otpBroadcastReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(otpBroadcastReceiver);
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
                    Toast.makeText(view.getContext(), R.string.registrationFailed, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}
