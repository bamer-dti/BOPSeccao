package pt.bamer.bameropseccao;

import android.app.Activity;
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
    private static String seccao;
    private static String estado;
    private static ToneGenerator toneG;
    private static ProgressDialog dialogoInterminavel;

    public static SharedPreferences getPrefs() {
        return prefs;
    }

    public static void setOperador(String operador) {
        MrApp.operador = operador;
    }

    public static String getEstado() {
        return estado;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        prefs = getSharedPreferences(Constantes.PREFS_NAME, MODE_PRIVATE);
        seccao = prefs.getString(Constantes.PREF_SECCAO, ValoresDefeito.SECCAO);
        estado = prefs.getString(Constantes.PREF_ESTADO, ValoresDefeito.ESTADO);
        toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);

    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public static String getSeccao() {
        prefs.getString(Constantes.PREF_SECCAO, ValoresDefeito.SECCAO);
        return seccao;
    }

    public static ToneGenerator getToneG() {
        return toneG;
    }

    public static void mostrarAlertToWait(final Activity activity, final String mensagem) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (dialogoInterminavel == null) {
                    dialogoInterminavel = new ProgressDialog(activity);
                    dialogoInterminavel.setMessage(mensagem);
                    dialogoInterminavel.show();
                } else {
                    dialogoInterminavel.setMessage(mensagem);
                }
            }
        });
    }

    public static void esconderAlertToWait(Activity activity) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (dialogoInterminavel != null) {
                    dialogoInterminavel.hide();
                    dialogoInterminavel.dismiss();
                    dialogoInterminavel = null;
                }
            }
        });
    }
}
