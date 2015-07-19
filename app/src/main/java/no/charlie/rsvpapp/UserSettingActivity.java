package no.charlie.rsvpapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

import no.charlie.rsvpapp.R;

/**
 * Created by charlie midtlyng on 19/07/15.
 */
public class UserSettingActivity extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new UserSettingsFragment()).commit();
    }

    public static class UserSettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
            for (int i = 0; i < getPreferenceScreen().getPreferenceCount(); i++) {
                updatePrefSummary(getPreferenceScreen().getPreference(i));
            }
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            updatePrefSummary(findPreference(key));
        }

        public void updatePrefSummary(Preference preference) {
            if (preference instanceof EditTextPreference) {
                preference.setSummary(((EditTextPreference) preference).getText());
            }
        }
    }

}
