package no.charlie.rsvpapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import no.charlie.rsvpapp.adapters.ParticipantAdapter;
import no.charlie.rsvpapp.domain.Event;
import no.charlie.rsvpapp.domain.EventWrapper;
import no.charlie.rsvpapp.handler.DialogHandler;
import no.charlie.rsvpapp.service.ApiClient;
import no.charlie.rsvpapp.util.FontResolver;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static android.app.ActivityOptions.makeSceneTransitionAnimation;
import static android.text.TextUtils.isEmpty;

public class EventActivity extends AppCompatActivity {

    private EventWrapper eventWrapper = new EventWrapper();
    private RecyclerView participantView;
    private Long eventId;
    private String subject;
    private Toolbar toolbar;
    private TextView locationInfo;
    private TextView timeInfo;
    private TextView signedUpLabel;
    private TextView signedUpContent;
    private TextView waitingListLabel;
    private TextView waitingListContent;
    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventId = (Long) getIntent().getExtras().get("eventId");
        subject = (String) getIntent().getExtras().get("subject");
        setContentView(R.layout.activity_event);
        final EventActivity context = this;
        participantView = (RecyclerView) findViewById(R.id.participants);
        final LinearLayoutManager layout2 = new LinearLayoutManager(context);
        participantView.setLayoutManager(layout2);

        final ParticipantAdapter participantAdapter = new ParticipantAdapter(context.getLayoutInflater(), eventWrapper, context);

        participantView.setAdapter(participantAdapter);
        setupToolbar();
        setupTopSection();
        setupMiddleSection();
    }

    private void setupMiddleSection() {
        signedUpLabel = (TextView) findViewById(R.id.signed_up_label);
        signedUpLabel.setTypeface(FontResolver.getHeaderFont(this));
        signedUpContent = (TextView) findViewById(R.id.signed_up);
        signedUpContent.setTypeface(FontResolver.getHeaderFont(this));
        waitingListLabel = (TextView) findViewById(R.id.waiting_list_label);
        waitingListLabel.setTypeface(FontResolver.getHeaderFont(this));
        waitingListContent = (TextView) findViewById(R.id.waiting_list);
        waitingListContent.setTypeface(FontResolver.getHeaderFont(this));
    }

    private void setupTopSection() {
        timeInfo = (TextView) findViewById(R.id.time_info);
        timeInfo.setTypeface(FontResolver.getHeaderFont(this));
        locationInfo = (TextView) findViewById(R.id.location_info);
        locationInfo.setTypeface(FontResolver.getHeaderFont(this));
        submitButton = (Button) findViewById(R.id.submit_button);
        submitButton.setTypeface(FontResolver.getHeaderFont(this));
        final Context context = this;
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(context);
                String name = SP.getString("currentName", null);
                String phone = SP.getString("currentPhone", null);
                if (isEmpty(name)) {
                    missingProperties("Legg til navn i innstillinger", context);
                } else if (isEmpty(phone)) {
                    missingProperties("Legg til mobilnr i innstillinger (pga verifisering)", context);
                } else {
                    Intent intent = new Intent(context, VerifyActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putLong("eventId", eventWrapper.getEvent().id);
                    intent.putExtras(bundle);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        context.startActivity(intent, makeSceneTransitionAnimation((Activity) context).toBundle());
                    } else {
                        context.startActivity(intent);
                    }
                }

            }
        });
    }

    private void missingProperties(String errorText, Context context) {
        DialogHandler appdialog = new DialogHandler();
        appdialog.SimpleAlert(context, "Oppdater innstillinger!", errorText, "OK");
    }

    private void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        TextView toolbarTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        toolbarTitle.setVisibility(View.VISIBLE);
        toolbarTitle.setTypeface(FontResolver.getHeaderFont(this));
        toolbarTitle.setText(subject);
        toolbar.setTitle("");

        ImageView toolbarLogo = (ImageView) toolbar.findViewById(R.id.toolbar_logo);
        toolbarLogo.setVisibility(View.GONE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                startActivity(i, makeSceneTransitionAnimation(this).toBundle());
            } else {
                startActivity(i);
            }
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
        fetchEvents(eventId);
    }


    private ParticipantAdapter getParticipantAdapter() {
        return (ParticipantAdapter) participantView.getAdapter();
    }

    private void fetchEvents(Long id) {
        setProgressBarIndeterminateVisibility(true);
        ApiClient.getService().findEvent(id, new Callback<Event>() {
            @Override
            public void success(Event event, Response response) {
                EventActivity.this.eventWrapper.setEvent(event);
                populateEvent(event);
                getParticipantAdapter().notifyDataSetChanged();
                setProgressBarIndeterminateVisibility(false);
            }

            @Override
            public void failure(RetrofitError error) {
                setProgressBarIndeterminateVisibility(false);
                Toast.makeText(EventActivity.this, "Shit - kan ikke hente data på event!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void populateEvent(Event event) {
        timeInfo.setText(event.day() + " " + event.startTime.dayOfMonth().get() + ". " + event.month() + " KL " + event.startTime());
        locationInfo.setText(event.location);
        int numberOnWaitingList = event.participants.size() - event.maxNumber;
        waitingListContent.setText("" + (numberOnWaitingList > 0 ? numberOnWaitingList : 0));
        int signedUp = event.participants.size() <= event.maxNumber ? event.participants.size() : event.maxNumber;
        signedUpContent.setText("" + signedUp);
    }
}
