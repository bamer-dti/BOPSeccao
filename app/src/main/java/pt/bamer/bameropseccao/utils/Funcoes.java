package pt.bamer.bameropseccao.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;

import org.joda.time.LocalDateTime;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.util.Calendar;
import java.util.Locale;

import pt.bamer.bameropseccao.MrApp;
import pt.bamer.bameropseccao.R;

public class Funcoes {
    public static LocalDateTime cToT(String datastr) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        return formatter.parseLocalDateTime(datastr);
    }

    public static String milisegundos_em_HH_MM_SS(long milisegundos) {
        PeriodFormatter fmt = new PeriodFormatterBuilder()
                .printZeroAlways()
                .minimumPrintedDigits(2)
                .appendHours()
                .appendSeparator(":")
                .printZeroAlways()
                .minimumPrintedDigits(2)
                .appendMinutes()
                .appendSeparator(":")
                .printZeroAlways()
                .minimumPrintedDigits(2)
                .appendSeconds()
                .toFormatter();
        Period period = new Period(milisegundos);
        return fmt.print(period);
    }

    public static long getUnixTimeHoraDoDia(int ano, int mes, int dia, int hora, int minuto, int segundo) {
        Calendar c = Calendar.getInstance(Locale.getDefault());
        c.set(Calendar.YEAR, ano);
        c.set(Calendar.MONTH, mes - 1);
        c.set(Calendar.DAY_OF_MONTH, dia);
        c.set(Calendar.HOUR_OF_DAY, hora);
        c.set(Calendar.MINUTE, minuto);
        c.set(Calendar.SECOND, segundo);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis() / 1000;
    }

    public static String localDateTimeToStrFullaZeroHour(LocalDateTime data) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd 00:00:00");
        return formatter.print(data);
    }

    public static void alerta(Context context, String titulo, String mensagem) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyAlertDialogStyle);
        builder.setTitle(titulo);
        builder.setMessage(mensagem);
        builder.setPositiveButton("OK", null);
//        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    public static class Beep extends AsyncTask<Void, Void, Void> {
        private final int duracell;
        private int toner;

        public Beep(int toneGenerator, int duracao_em_milisegundos) {
            toner = toneGenerator;
            duracell = duracao_em_milisegundos;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                MrApp.getToneG().startTone(toner, duracell);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
