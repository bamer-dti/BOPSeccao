package pt.bamer.bameropseccao;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.ToneGenerator;

import pt.bamer.bameropseccao.utils.Constantes;
import pt.bamer.bameropseccao.utils.ValoresDefeito;

public class MrApp extends Application {
    private static SharedPreferences prefs;
    private static String operador;
    private static String estado;
    private static ToneGenerator toneG;
    private static ProgressDialog dialogoInterminavel;

    public static SharedPreferences getPrefs() {
        return prefs;
    }

    public static void setOperador(String operador) {
        MrApp.operador = operador;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        prefs = getSharedPreferences(Constantes.PREFS_NAME, MODE_PRIVATE);
        estado = prefs.getString(Constantes.PREF_ESTADO, ValoresDefeito.ESTADO);
        toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);

    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public static String getSeccao() {
        return prefs.getString(Constantes.PREF_SECCAO, ValoresDefeito.SECCAO);
    }

    public static String getEstado() {
        return  prefs.getString(Constantes.PREF_ESTADO, ValoresDefeito.ESTADO);
    }

    public static ToneGenerator getToneG() {
        return toneG;
    }
}
