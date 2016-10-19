package pt.bamer.bameropseccao.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import pt.bamer.bameropseccao.Dossier;
import pt.bamer.bameropseccao.PainelGlobal;
import pt.bamer.bameropseccao.R;
import pt.bamer.bameropseccao.objectos.OSBI;
import pt.bamer.bameropseccao.objectos.OSBO;
import pt.bamer.bameropseccao.objectos.OSPROD;
import pt.bamer.bameropseccao.utils.Constantes;
import pt.bamer.bameropseccao.utils.Funcoes;


public class AdapterOS extends RecyclerView.Adapter {
    private static final String TAG = AdapterOS.class.getSimpleName();

    private final Context context;
    private List<OSBO> listaOSBO;
    private List<OSBI> listaOSBI;
    private List<OSPROD> listaOSPROD;

    public AdapterOS(final Context context) {
        this.context = context;
        listaOSBO = new ArrayList<>();
        listaOSBI = new ArrayList<>();
        listaOSPROD = new ArrayList<>();
    }

    public void updateSourceData(final List<OSBO> lOSBO, final List<OSBI> lOSBI, final List<OSPROD> lOSPROD) {
        ((PainelGlobal) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ordered(lOSBO);
                listaOSBO = lOSBO;
                listaOSBI = lOSBI;
                listaOSPROD = lOSPROD;

                Log.v(TAG, "AdapterOS tem " + listaOSBO.size() + " linhas!");
                notifyDataSetChanged();
            }
        });
    }

    private void ordered(List<OSBO> lOSBO) {
        //noinspection unchecked
        Collections.sort(lOSBO, new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                String x1 = ((OSBO) o1).dtcortef;
                String x2 = ((OSBO) o2).dtcortef;
                int sComp = x1.compareTo(x2);

                if (sComp != 0) {
                    return sComp;
                } else {
                    int s1 = ((OSBO) o1).ordem;
                    int s2 = ((OSBO) o2).ordem;
                    x1 = String.format(Locale.getDefault(), "%03d", s1);
                    x2 = String.format(Locale.getDefault(), "%03d", s2);
                    return x1.compareTo(x2);
                }
            }
        });
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.view_osbo, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        DateTimeFormatter dtf = DateTimeFormat.forPattern("dd.MM.yyyy");

        OSBO osbo = listaOSBO.get(position);
        viewHolder.tv_fref.setText(osbo.fref + " - " + osbo.nmfref);
        viewHolder.tv_obrano.setText("OS " + osbo.obrano + " (" + osbo.ordem + ")");
        viewHolder.tv_descricao.setText(osbo.obs);


        LocalDateTime localDateTime = Funcoes.cToT(osbo.dtcortef);
        viewHolder.tv_dtcortef.setText(dtf.print(localDateTime));

        localDateTime = Funcoes.cToT(osbo.dttransf);
        viewHolder.tv_dttransf.setText(dtf.print(localDateTime));

        String bostamp = osbo.bostamp;

        new TaskCalcularValores(bostamp, viewHolder).execute();

        new TaskCalcularTempo(bostamp, viewHolder.tv_temporal).execute();

        viewHolder.llclick.setTag(bostamp);

        viewHolder.llclick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), Dossier.class);
                intent.putExtra(Constantes.INTENT_EXTRA_BOSTAMP, view.getTag().toString());
//                intent.putExtra(Constantes.INTENT_EXTRA_MODO_OPERACIONAL, Constantes.MODO_STARTED);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView tv_fref;
        private final TextView tv_obrano;
        private final TextView tv_descricao;
        private final TextView tv_dtcortef;
        private final TextView tv_dttransf;
        private final TextView tv_qtt;
        private final TextView tv_qttfeita;
        private final TextView tv_temporal;
        private final LinearLayout llclick;

        public ViewHolder(View view) {
            super(view);
            tv_fref = (TextView) view.findViewById(R.id.tv_fref);
            tv_obrano = (TextView) view.findViewById(R.id.tv_obrano);
            tv_descricao = (TextView) view.findViewById(R.id.tv_descricao);
            tv_dtcortef = (TextView) view.findViewById(R.id.tv_dtcortef);
            tv_dttransf = (TextView) view.findViewById(R.id.tv_dttransf);
            tv_qtt = (TextView) view.findViewById(R.id.tv_qtt);
            tv_qttfeita = (TextView) view.findViewById(R.id.tv_qttfeita);
            tv_temporal = (TextView) view.findViewById(R.id.tv_temporal);
            llclick = (LinearLayout) view.findViewById(R.id.llclick);
        }
    }

    @SuppressWarnings("unused")
    public OSBO getItem(int posicao) {
        return listaOSBO.get(posicao);
    }

    @Override
    public int getItemCount() {
        return listaOSBO == null ? 0 : listaOSBO.size();
    }

    private class TaskCalcularTempo extends AsyncTask<Void, Void, Void> {
        @SuppressWarnings("unused")
        private final String bostamp;
        private final TextView tv_temporal;
        private long tempoCalculado;

        public TaskCalcularTempo(String bostamp, TextView tv_temporal) {
            this.bostamp = bostamp;
            this.tv_temporal = tv_temporal;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            tempoCalculado = 0;
            return null;
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(Void aVoid) {
            String textoTempo = Funcoes.milisegundos_em_HH_MM_SS(tempoCalculado * 1000);
            tv_temporal.setText("" + (tempoCalculado == 0 ? "" : textoTempo));
        }
    }

    private class TaskPosicaoDoUltimoTempo extends AsyncTask<Void, Void, Void> {
        @SuppressWarnings("unused")
        private final String bostamp_;
        private final LinearLayout llclick_;

        public TaskPosicaoDoUltimoTempo(String bostamp, LinearLayout llclick) {
            this.bostamp_ = bostamp;
            this.llclick_ = llclick;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            PainelGlobal painel = (PainelGlobal) context;
            painel.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    llclick_.setBackgroundColor(Color.WHITE);
                }
            });

            final int tipoTempo = Integer.parseInt("0");
            painel.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (tipoTempo == Constantes.MODO_STARTED) {
                        llclick_.setBackgroundColor(ContextCompat.getColor(llclick_.getContext(), R.color.md_green_100));
                    }
                }
            });
        }
    }

    private class TaskCalcularValores extends AsyncTask<Void, Void, Void> {
        private final String bostamp;
        private final ViewHolder viewHolder;
        private int qttProd;
        private int qtt;

        public TaskCalcularValores(String bostamp, ViewHolder viewHolder) {
            this.bostamp = bostamp;
            this.viewHolder = viewHolder;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            qttProd = 0;
            for (OSPROD osprod : listaOSPROD) {
                if (osprod.bostamp.equals(bostamp)) {
                    qttProd += osprod.qtt;
                }
            }

            qtt = 0;
            for (OSBI osbi : listaOSBI) {
                if (osbi.bostamp.equals(bostamp)) {
                    qtt += osbi.qtt;
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            viewHolder.tv_qtt.setText(qtt != 0 ? "" + qtt : "");
            viewHolder.tv_qttfeita.setText(qttProd != 0 ? "" + qttProd : "");
            if (qttProd >= qtt)
                viewHolder.llclick.setBackgroundColor(ContextCompat.getColor(context, R.color.md_green_100));
            else
                viewHolder.llclick.setBackgroundColor(ContextCompat.getColor(context, R.color.md_white_1000));
        }
    }
}
