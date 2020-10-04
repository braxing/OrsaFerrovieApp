package it.orsaferrovie.orsaferrovieapp;


import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.os.Environment;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment implements DownloadService.DatiAggiornatiListener {

 /*   private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;*/

    private ListView lstNotizie;
    private TextView txtLoading;
    private DownloadService mService;
    private boolean mBound = false;
    private NewsAdapter mAdapter;
    private boolean firstExecution = true;
    private BroadcastReceiver onComplete;
    private IntentFilter downloadComplete = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
    private DownloadManager dmgr;
    private ProgressDialog prgDlg;
    private long idDownload = -1;
    private MainActivity mActivity;


    public static MainFragment newInstance(MainActivity main) {
        MainFragment fragment = new MainFragment();
        fragment.mActivity = main;
        return fragment;
    }

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        onComplete = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Uri uri = null;
                DownloadManager.Query query = new DownloadManager.Query();
                query.setFilterById(idDownload);
                Cursor c = dmgr.query(query);
                if (c != null && c.moveToFirst()) {
                    int statusIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS);
                    int localUriColIndex = c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI);
                    if (c.getInt(statusIndex) == DownloadManager.STATUS_SUCCESSFUL) {
                        File tmpFile = new File(URI.create(c.getString(localUriColIndex)));
                        uri = FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID + ".provider", tmpFile);
                    }
                }
                if (prgDlg!= null && prgDlg.isShowing())
                    prgDlg.dismiss();
                if (uri != null)
                    openWithProgram(uri);
                else
                    Toast.makeText(context, getString(R.string.download_error), Toast.LENGTH_LONG).show();
            }
        };
        dmgr = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup me = (ViewGroup)inflater.inflate(R.layout.fragment_main, container, false);
        lstNotizie = (ListView)me.findViewById(R.id.lstNotizie);
        txtLoading = (TextView)me.findViewById(R.id.txtLoading);
        prgDlg = new ProgressDialog(getActivity());
        prgDlg.setTitle(getString(R.string.donwload_title));
        prgDlg.setMessage(getString(R.string.download_message));
        prgDlg.setIndeterminate(true);
        mAdapter = new NewsAdapter(getActivity(), loadNotizie());
        lstNotizie.setAdapter(mAdapter);
        lstNotizie.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Notizia itemClicked = (Notizia) adapterView.getItemAtPosition(i);
                URL address = itemClicked.get_address();
                if (!isFile(address))
                        openWithProgram(Uri.parse(address.toString())); //E' un indirizzo web, lo apriamo direttamente
                    else {
                    //è un file, lo scarichiamo e lo apriamo
                    downloadAndOpen(address.toString());
                }
            }
        });
        if (mAdapter.getCount()<=0)
            listaVisibile(false);
        return me;
    }

    private void openWithProgram(Uri address) {
        String type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(address.getPath()));
        Intent actionIntent = new Intent(Intent.ACTION_VIEW);
        actionIntent.setDataAndTypeAndNormalize(address, type);
        actionIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        actionIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(actionIntent);
    }

    private static String getFileName(String fullPathFile){
        return fullPathFile.substring(fullPathFile.lastIndexOf("/") + 1);
    }

    private void downloadAndOpen(String address) {
        if (mActivity.checkPermission()) {
            //Vediamo se il file esiste già, lo apriamo direttamente
            final String filename = getFileName(address);//address.substring(address.lastIndexOf("/") + 1);
            // The place where the downloaded PDF file will be put
            final File tempFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), filename);
            if (tempFile.exists())
                // If we have downloaded the file before, just go ahead and show it.
                //openWithProgram(Uri.fromFile(tempFile));
                openWithProgram(FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID+".provider",tempFile));
            else {
                getActivity().registerReceiver(onComplete, downloadComplete);
                DownloadManager.Request req = new DownloadManager.Request(Uri.parse(address.toString()));
                req.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename);
                idDownload = dmgr.enqueue(req);
                prgDlg.show();
            }
        }
    }



            @Override
    public void onStart() {
        super.onStart();
        Activity activity = getActivity();
        Intent downloadService = new Intent(activity, DownloadService.class);
        activity.bindService(downloadService, mConnection, Context.BIND_AUTO_CREATE);
        //getActivity().registerReceiver(onComplete, new IntentFilter(downloadComplete));
    }

    private void listaVisibile(boolean lista) {
        if (!lista) {
            lstNotizie.setVisibility(View.GONE);
            txtLoading.setVisibility(View.VISIBLE);
        }
        else {
            lstNotizie.setVisibility(View.VISIBLE);
            txtLoading.setVisibility(View.GONE);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().unbindService(mConnection);
        if (mBound) mBound = false;
        //getActivity().unregisterReceiver(onComplete);
    }

    void servizioConnesso() {
        if (mBound) {
            mService.setListener(this);
            if (firstExecution)
                aggiorna();
            firstExecution = false;
        }
    }

    private void aggiorna() {
        if (mBound) {
            mService.eseguiAggiornamento();
            Toast.makeText(getActivity(), getString(R.string.updateText), Toast.LENGTH_SHORT).show();
        }
    }

    private ArrayList<Notizia> loadNotizie() {
        ArrayList<Notizia> result = new ArrayList<>(0);
        try {
            ObjectInputStream input = new ObjectInputStream(getActivity().openFileInput(getString(R.string.file_notizie)));
            result = (ArrayList<Notizia>)input.readObject();
            input.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.mainfragmentmenu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.mnuUpdate:
                aggiorna();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void datiAggiornati() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.clear();
                mAdapter.addAll(loadNotizie());
                mAdapter.notifyDataSetChanged();
                listaVisibile(true);
            }
        });
    }

    private boolean isFile(URL uri) {
        String[] fileExtension = new String[] {"pdf", "jpg", "jpg", "ppt", "pptx", "doc", "docx"};
        String uristr = uri.toString();
        String extension = uristr.substring(uristr.lastIndexOf(".")+1);
        boolean result = false;
        for (String sfile: fileExtension)
            if (extension.equalsIgnoreCase(sfile)) {
                result = true;
                break;
            }
        return result;
    }




    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            DownloadService.LocalBinder binder = (DownloadService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
            servizioConnesso();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

}
