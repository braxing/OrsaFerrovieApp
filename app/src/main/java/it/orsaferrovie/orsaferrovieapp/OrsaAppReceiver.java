package it.orsaferrovie.orsaferrovieapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;
import android.util.Log;

///*    Questo broadcast riceve l'evento boot del sistema e provvede ad attivare l'allarme con alarmmanager
///     Contiene inoltre i metodi per attivare, tramite alarmmanager l'aggiornamento ogni tot*///


public class OrsaAppReceiver extends BroadcastReceiver {
    public static long UPDATE_INTERVAL = AlarmManager.INTERVAL_FIFTEEN_MINUTES;
    private static PendingIntent pService;

    @Override
    public void onReceive(Context context, Intent intent) {
        attivaAllarme(context);
        Log.d("Messaggio","Avvio Ricevuto");
    }

    public static void attivaAllarme(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        UPDATE_INTERVAL = Long.valueOf(prefs.getString("intervalupdate", String.valueOf(UPDATE_INTERVAL)));
        pService = getPendingIntent(context);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                20000,
                UPDATE_INTERVAL, pService);
        Log.d("Messaggio", "Il broadcast è stato settato minuti "+String.valueOf(UPDATE_INTERVAL/1000/60));
    }

    private static PendingIntent getPendingIntent(Context context) {
        Intent serviceIntent = new Intent(context, DownloadService.class);
        return PendingIntent.getService(context, 0, serviceIntent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    public static void cancelAlarm(Context context) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (pService == null)
            am.cancel(getPendingIntent(context));
        else
            am.cancel(pService);
        Log.d("Messaggio", "Allarme disattivato");
    }
}
