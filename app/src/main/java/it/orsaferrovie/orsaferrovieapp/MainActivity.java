package it.orsaferrovie.orsaferrovieapp;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.support.v4.widget.DrawerLayout;
import android.widget.AdapterView;
import android.widget.ListView;


public class MainActivity extends AppCompatActivity {

    private CharSequence mTitle;
    private String[] menu_items;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private String mDrawerTitle;
    private int currentPosition = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        checkFirstTimeBoot();
        setContentView(R.layout.activity_main);
        menu_items = getResources().getStringArray(R.array.lst_menu);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.lst_menu);
        mTitle = getTitle();
        mDrawerTitle = getString(R.string.drawer_title);
        mDrawerList.setAdapter(new MenuAdapter(this, menu_items));
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectItem(i);
            }
        });

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open_drawer, R.string.close_drawer) {
            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mTitle);
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle(mDrawerTitle);
            }

        };
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        //attiviamo anche il servizio
        /*Intent srvintent = new Intent(this, DownloadService.class);
        startService(srvintent);*/
        selectItem(0);
    }

    private void checkFirstTimeBoot() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isFirstTime = sp.getBoolean(getString(R.string.pref_firstTime), true);
        if (isFirstTime) {
            OrsaAppReceiver.attivaAllarme(this);
            SharedPreferences.Editor pEditor = sp.edit();
            pEditor.putBoolean(getString(R.string.pref_firstTime), false);
            pEditor.commit();
        }
    }

    private void selectItem(int position) {
        if (currentPosition != position) {
            Fragment nuovo = null;
            setTitle(menu_items[position]);
            switch (position) {
                case 0: //Ultime notizie
                    nuovo = MainFragment.newInstance(this);
                    break;
                case 1: //Scrivici
                    nuovo = ScriviciFragment.newInstance();
                    break;
                case 2: //Pagina facebook
                    nuovo = SocialFragment.newInstance();
                    break;
                case 3: //Impostazioni
                    nuovo = new SettingsFragment();
                    break;
                default:
                    nuovo = null;
            }
            if (nuovo != null)
                getSupportFragmentManager().beginTransaction().replace(R.id.container, nuovo).commit();
            mDrawerLayout.closeDrawer(mDrawerList);
            currentPosition = position;
        }
    }



    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }

    public boolean checkPermission() {
        int mPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (mPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1);
            return false;
        }
        else
            return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    // 1. Instantiate an AlertDialog.Builder with its constructor
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage(R.string.dialog_permission)
                            .setTitle(R.string.dilaog_permission_title);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                return;
            }
        }
    }
}
