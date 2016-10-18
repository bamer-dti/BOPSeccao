package pt.bamer.bameropseccao;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.hanks.htextview.HTextView;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import java.util.TimerTask;

import pt.bamer.bameropseccao.utils.Constantes;
import pt.bamer.bameropseccao.utils.Funcoes;

public class Cartao_Maquina extends CardView {
    private static String TAG;
    private PainelGlobal painelGlobalActivity;
    private String idInterno = "vazio";
    private Context context = this.getContext();
    private TimerDePainelGlobal cronometroOsEmTrabalho;
    private TextView tv_maquina;
    private TextView tv_ultimo_utilizador;
    private TextView tv_ultimo_os;
    private TextView tv_ultimo_fref;
    private HTextView htv_timer;

    public Cartao_Maquina(Context context) {
        super(context);
        initialize(context);
    }

    public Cartao_Maquina(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public Cartao_Maquina(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    public Cartao_Maquina(PainelGlobal painelGlobalActivity, String maquina) {
        super(painelGlobalActivity);
        TAG = Cartao_Maquina.class.getSimpleName() + "_" + maquina;
        this.painelGlobalActivity = painelGlobalActivity;
        idInterno = maquina;
        maquina = maquina.toUpperCase();
        initialize(painelGlobalActivity);
        Log.d(TAG, "A colocar cartão da máquina " + maquina);
        tv_maquina.setText(idInterno);
    }

    private void initialize(Context context) {
        Log.i(TAG, "*** INICIALIZOU O CARTÃO MÁQUINA *** ");
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        View root = inflater.inflate(R.layout.card_machina, this, true);
        tv_maquina = (TextView) root.findViewById(R.id.tv_maquina);
        tv_maquina.setText("");

        htv_timer = (HTextView) root.findViewById(R.id.htv_timer);
        htv_timer.animateText("");

        tv_ultimo_utilizador = (TextView) root.findViewById(R.id.tv_ultimo_utilizador);
        tv_ultimo_utilizador.setText("");

        tv_ultimo_os = (TextView) root.findViewById(R.id.tv_ultimo_os);
        tv_ultimo_os.setText("");

        tv_ultimo_fref = (TextView) root.findViewById(R.id.tv_ultimo_fref);
        tv_ultimo_fref.setText("");
    }


    private void colocarCronometroOsEmTrabalho(final long tempoUnix, final int tempoTipo) {
        if (cronometroOsEmTrabalho != null) {
            cronometroOsEmTrabalho.cancel();
            cronometroOsEmTrabalho.purge();
            cronometroOsEmTrabalho = null;
        }
        TimerTask timerTaskOS_em_Trabalho = new TimerTask() {
            @Override
            public void run() {
                final long unixNow = System.currentTimeMillis() / 1000L;
                final long intervaloTempo = unixNow - tempoUnix;
                final String textoIntervaloTempo = "" + Funcoes.milisegundos_em_HH_MM_SS(intervaloTempo * 1000);
                Log.v("CRONOGRAFO", idInterno + "-> ** " + textoIntervaloTempo);
                painelGlobalActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (tempoTipo == Constantes.MODO_STARTED) {
                            setCardBackgroundColor(ContextCompat.getColor(context, R.color.md_green_300));

                        }
                        if (tempoTipo == Constantes.MODO_STOPED) {

                            DateTime dataAntes = new DateTime(tempoUnix * 1000);
                            DateTime dataAgora = new DateTime(unixNow * 1000);
                            Duration duration = new Duration(dataAntes, dataAgora);
                            long difEmMinutos = duration.getStandardMinutes();
                            Log.v(TAG, "Diferença em minutos: " + difEmMinutos);
                            if (difEmMinutos >= Constantes.TEMPO_MINIMO_PARAGEM_EM_MINUTOS) {
                                setCardBackgroundColor(ContextCompat.getColor(context, R.color.md_red_300));
                            } else {
                                setCardBackgroundColor(ContextCompat.getColor(context, R.color.md_yellow_600));
                            }
                        }
                        htv_timer.animateText(textoIntervaloTempo);

                    }
                });
            }
        };
        cronometroOsEmTrabalho = new TimerDePainelGlobal(painelGlobalActivity);
        cronometroOsEmTrabalho.schedule(timerTaskOS_em_Trabalho, 0, 1000);
    }

    public String getIdInterno() {
        return idInterno;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
