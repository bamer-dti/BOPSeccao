package pt.bamer.bameropseccao.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
import pt.bamer.bameropseccao.database.DBSqlite;
import pt.bamer.bameropseccao.objectos.OSBI;
import pt.bamer.bameropseccao.objectos.OSBO;
import pt.bamer.bameropseccao.objectos.OSPROD;
import pt.bamer.bameropseccao.objectos.OSTIMER;
import pt.bamer.bameropseccao.utils.Constantes;
import pt.bamer.bameropseccao.utils.Funcoes;

public class AdapterOS extends RecyclerView.Adapter {
    private static final String TAG = AdapterOS.class.getSimpleName();

    private final Context context;
    private List<OSBO> listaOSBO;

    public AdapterOS(final Context context) {
        this.context = context;
        listaOSBO = new ArrayList<>();
    }

    public void actualizarValoresAdapter(final List<OSBO> lOSBO, final List<OSBI> lOSBI, final List<OSPROD> lOSPROD) {
        ((PainelGlobal) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ordered(lOSBO);
                listaOSBO = lOSBO;
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
        viewHolder.tv_obrano.setText("OS " + osbo.obrano + " (" + osbo.ordem + ") ");
        viewHolder.tv_descricao.setText(osbo.obs);


        LocalDateTime localDateTime = Funcoes.cToT(osbo.dtcortef);
        viewHolder.tv_dtcortef.setText(dtf.print(localDateTime));

        localDateTime = Funcoes.cToT(osbo.dttransf);
        viewHolder.tv_dttransf.setText(dtf.print(localDateTime));

        String bostamp = osbo.bostamp;

        new TaskCalcularValores(bostamp, viewHolder).execute();

        new TaskCalcularTempo(bostamp, viewHolder).execute();

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

    public void updateSourceDataOSBO(final ArrayList<OSBO> lista) {
        ((PainelGlobal) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ordered(lista);
                listaOSBO = lista;
                Log.v(TAG, "AdapterOS tem " + listaOSBO.size() + " linhas!");
                notifyDataSetChanged();
            }
        });
    }

    public void actualizarValoresAdapterOSPROD(ArrayList<OSPROD> listaOSPROD) {
        for (OSPROD osprod : listaOSPROD) {
            for (int i = 0; i < listaOSBO.size(); i++) {
                OSBO osbo = listaOSBO.get(i);
                if (osbo.bostamp.equals(osprod.bostamp)) {
                    notifyItemChanged(i);
                }
            }
        }
    }

    public void actualizarValoresAdapterOSBI() {
        ArrayList<OSBI> lista = new DBSqlite(context).getListaOSBIAgrupada();
        for (OSBI osbi : lista) {
            for (int i = 0; i < listaOSBO.size(); i++) {
                OSBO osbo = listaOSBO.get(i);
                if (osbo.bostamp.equals(osbi.bostamp)) {
                    notifyItemChanged(i);
                }
            }
        }
    }

    public void actualizarValoresAdapterOSTIMER(ArrayList<OSTIMER> listaOSTIMER) {
        for (OSTIMER ostimer : listaOSTIMER) {
            for (int i = 0; i < listaOSBO.size(); i++) {
                OSBO osbo = listaOSBO.get(i);
                if (osbo.bostamp.equals(ostimer.bostamp)) {
                    notifyItemChanged(i);
                }
            }
        }
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
        public long tempoCalculado;

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
        private final String bostamp;
        private final TextView tv_temporal;
        private final ViewHolder viewHolder;

        public TaskCalcularTempo(String bostamp, AdapterOS.ViewHolder viewHolder) {
            this.bostamp = bostamp;
            this.tv_temporal = viewHolder.tv_temporal;
            this.viewHolder = viewHolder;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            viewHolder.tempoCalculado = new DBSqlite(context).getTempoTotal(bostamp);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            String textoTempo = Funcoes.milisegundos_em_HH_MM_SS(viewHolder.tempoCalculado * 1000);
            tv_temporal.setText("" + (viewHolder.tempoCalculado == 0 ? "" : textoTempo));
            new TaskPosicaoDoUltimoTempo(bostamp, viewHolder).execute();
        }
    }

    private class TaskPosicaoDoUltimoTempo extends AsyncTask<Void, Void, Void> {
        @SuppressWarnings("unused")
        private final String bostamp_;
        private final LinearLayout llclick_;
        private int tipoTempo = 0;

        public TaskPosicaoDoUltimoTempo(String bostamp, AdapterOS.ViewHolder viewHolder) {
            this.bostamp_ = bostamp;
            this.llclick_ = viewHolder.llclick;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            tipoTempo = new DBSqlite(context).getUltimaPosicaoTempo(bostamp_);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            PainelGlobal painel = (PainelGlobal) context;
//            painel.runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    llclick_.setBackgroundColor(Color.WHITE);
//                }
//            });

            painel.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (tipoTempo == Constantes.MODO_STARTED) {
                        llclick_.setBackgroundColor(ContextCompat.getColor(llclick_.getContext(), R.color.md_yellow_100));
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
            qttProd = new DBSqlite(context).getQtdProdBostamp(bostamp);
            qtt = new DBSqlite(context).getQtdBostamp(bostamp);
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
