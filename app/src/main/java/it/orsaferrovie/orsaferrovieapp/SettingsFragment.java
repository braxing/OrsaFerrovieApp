package it.orsaferrovie.orsaferrovieapp;


import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.app.Fragment;

import android.preference.CheckBoxPreference;
import android.preference.Preference;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceFragmentCompat;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        findPreference("autoupdate").setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object o) {
        if (preference.getKey().equals("autoupdate")) {
            PackageManager pm = getActivity().getPackageManager();
            ComponentName receiver = new ComponentName(getActivity(), OrsaAppReceiver.class);
            if (!((CheckBoxPreference)preference).isChecked()) {
                pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
                OrsaAppReceiver.attivaAllarme(getActivity());
            }
            else {
                pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
                OrsaAppReceiver.cancelAlarm(getActivity());
            }

        }
        else if (preference.getKey().equals("intervalupdate")) {
            OrsaAppReceiver.cancelAlarm(getActivity());
            OrsaAppReceiver.attivaAllarme(getActivity());
        }
        return true;
    }
}
