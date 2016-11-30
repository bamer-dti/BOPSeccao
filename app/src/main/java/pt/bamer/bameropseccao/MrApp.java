package pt.bamer.bameropseccao;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.support.multidex.MultiDexApplication;

import java.util.ArrayList;

import pt.bamer.bameropseccao.objectos.Machina;
import pt.bamer.bameropseccao.utils.Constantes;
import pt.bamer.bameropseccao.utils.ValoresDefeito;

public class MrApp extends MultiDexApplication {
    private static SharedPreferences prefs;
    private static ToneGenerator toneG;
    private static ArrayList<Machina> listaDeMachinas;

    public static SharedPreferences getPrefs() {
        return prefs;
    }

    public static void setListaDeMachinas(ArrayList<Machina> listaDeMachinas) {
        MrApp.listaDeMachinas = listaDeMachinas;
    }

    public static String getSeccao() {
        return prefs.getString(Constantes.PREF_SECCAO, ValoresDefeito.SECCAO);
    }

    public static String getEstado() {
        return prefs.getString(Constantes.PREF_ESTADO, ValoresDefeito.ESTADO);
    }

    public static ToneGenerator getToneG() {
        return toneG;
    }

    public static ArrayList<Machina> getMachinas() {
        String sec = getSeccao();
        ArrayList<Machina> lista = new ArrayList<>();
        for (Machina machina : listaDeMachinas) {
            if (machina.seccao.equals(sec)) {
                lista.add(machina);
            }
        }
        return lista;
    }

    public static String getTituloBase(Context context) {
        String versao = context.getString(R.string.app_name);
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            versao += " " + pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versao;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        prefs = getSharedPreferences(Constantes.PREFS_NAME, MODE_PRIVATE);
        toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
