package it.orsaferrovie.orsaferrovieapp;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;



import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DownloadService extends IntentService {
    private static OkHttpClient okClient;
    private static String BASE_URL = "https://www.sindacatoorsa.it/orsa_ferrovie/";
    private boolean nuovenotizie = false;
    private DatiAggiornatiListener mListener;
    public final Binder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        public DownloadService getService() {
            return DownloadService.this;
        }
    }


    public DownloadService() {
        super("DownloaderService");
        if (okClient == null)
            okClient = new OkHttpClient();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("Messaggio", "DownloadService sta per essere attivato");
        /*AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Log.d("Messaggio","Prossimo Intent "+am.getNextAlarmClock().getShowIntent().toString());
        Log.d("Messaggio","Prossimo Intent a millisecondi "+String.valueOf(am.getNextAlarmClock().getTriggerTime()));*/
        eseguiAggiornamento();
    }

    public synchronized void eseguiAggiornamento() {
        if (isOnline()) {
            Thread runThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        settaAllarmeConnessione(false);
                        nuovenotizie = false;
                        Request req = new Request.Builder().url(getString(R.string.strurl)).build();
                        Response resp = okClient.newCall(req).execute();
                        if (resp.isSuccessful()) {
                            memorizzaDati(resp.body().string());
                            if (mListener != null) mListener.datiAggiornati();
                            resp.body().close();
                        }
                    /*if (!nuovenotizie)
                        cancellaNotifica();*/

                    } catch (Exception ex) {

                    }
                }
            });
            runThread.start();
            Log.d("Messaggio", "Thread di aggiornamento avviato");
        }
        else
            settaAllarmeConnessione(true);
    }

    private void settaAllarmeConnessione(boolean attiva) {
        PackageManager pm = this.getPackageManager();
        ComponentName networkReceiver = new ComponentName(this, NetworkChangedReceiver.class);
        if (attiva)
            pm.setComponentEnabledSetting(networkReceiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
        else
            pm.setComponentEnabledSetting(networkReceiver, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
    }


    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mListener = null;
        return super.onUnbind(intent);
    }

    public void setListener(DatiAggiornatiListener listener) {
        mListener = listener;
    }

    private void memorizzaDati(String pagina) {
        ArrayList<Notizia> vecchie = loadNotizie();
        try {
            ArrayList<Notizia> dati = parseData(pagina);
            if (dati.size()>0) {
                if (vecchie.isEmpty() || !vecchie.get(0).get_testo().equals(dati.get(0).get_testo())) {
                    doNotification(dati.get(0).get_testo());
                    nuovenotizie = true;
                }
                ObjectOutputStream oStream = new ObjectOutputStream(this.openFileOutput(getString(R.string.file_notizie), MODE_PRIVATE));
                oStream.writeObject(dati);
                oStream.close();
            }
        } catch (Exception ex) {
            Toast.makeText(this, "Errore: "+ex.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    private ArrayList<Notizia> loadNotizie() {
        ArrayList<Notizia> result = new ArrayList<>(0);
        try {
            ObjectInputStream input = new ObjectInputStream(openFileInput(getString(R.string.file_notizie)));
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
    private ArrayList<Notizia> parseData(String data) {
        ArrayList<Notizia> notizieTotal = new ArrayList<>(0);
        Document doc = Jsoup.parse(data);
        Elements notizie = doc.select("table#newsTable tr");
        for (Element tr:notizie) {
            String giorno = tr.select(".data").first().text();
            String testo = tr.select("a").first().text(),
                    link = tr.select("a").first().attr("href"),
                    imageUri = tr.select("img").first().attr("src");
            if (imageUri.isEmpty()) imageUri = "images/news/OrsaNews.gif";
            link = link.replaceFirst("/reader\\.php\\?f=/orsa_ferrovie/", "");
            link = isAbsoluteURL(link)?link:BASE_URL+link;
            imageUri = isAbsoluteURL(imageUri)?imageUri:BASE_URL+imageUri;
            notizieTotal.add(new Notizia(giorno, testo, link, imageUri));
        }
        notizieTotal.trimToSize();
        return notizieTotal;
    }

    public interface DatiAggiornatiListener {
        void datiAggiornati();
    }

    //Questo blocca il thread finché non c'è connessione
    private void waitForConnection() {
        Context context = this;
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = false;
        while (!isConnected) {
            try {
                Thread.sleep(4000);
                isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
            } catch (InterruptedException ex){}
        }
    }

    public boolean isOnline() {

        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        //should check null because in air plan mode it will be null
        return (netInfo != null && netInfo.isConnected());
    }

    private void doNotification(String text) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentTitle(getString(R.string.notification_text));
        builder.setContentText(text);
        builder.setSmallIcon(R.drawable.notifica_logo);
        builder.setAutoCancel(true);
        Intent openAppIntent = new Intent(this, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.from(this);
// Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
// Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(openAppIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        builder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
        mNotificationManager.notify(125, builder.build());
    }

    private void cancellaNotifica() {
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(125);

    }

    private boolean isAbsoluteURL(String url) {
        boolean result = false;
        if (url.startsWith("http://") || url.startsWith("https://")) result = true;
        return result;
    }
}
