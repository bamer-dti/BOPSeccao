package pt.bamer.bameropseccao.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import pt.bamer.bameropseccao.Dossier;
import pt.bamer.bameropseccao.MrApp;
import pt.bamer.bameropseccao.R;
import pt.bamer.bameropseccao.database.DBSqlite;
import pt.bamer.bameropseccao.objectos.OSBI;
import pt.bamer.bameropseccao.objectos.OSPROD;
import pt.bamer.bameropseccao.utils.Constantes;

public class TarefaRecyclerAdapter extends RecyclerView.Adapter {
    @SuppressWarnings("unused")
    private static final String TAG = TarefaRecyclerAdapter.class.getSimpleName();
    private final Dossier activityDossier;
    private List<OSBI> lista;

    public TarefaRecyclerAdapter(Dossier activityDossier, List<OSBI> lista) {
        this.activityDossier = activityDossier;
        this.lista = lista;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(this.activityDossier).inflate(R.layout.view_task, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final ViewHolder viewHolder = (ViewHolder) holder;
        OSBI osbi = lista.get(position);

        viewHolder.tv_ref.setText(osbi.ref + " - " + osbi.design);

        viewHolder.tv_qtt.setText("...");

        TaskQtd taskQtd = new TaskQtd(viewHolder);
        taskQtd.execute();

        viewHolder.tv_dim.setText(osbi.dim + (osbi.mk.equals("") ? "" : ", mk " + osbi.mk));
        viewHolder.tv_numlinha.setText(osbi.numlinha);
    }

    private OSBI getItem(int position) {
        return lista != null ? lista.get(position) : null;
    }

    @Override
    public int getItemCount() {
        return lista != null ? lista.size() : 0;
    }

    public void populate(ArrayList<OSBI> listaOSBI) {
        lista = new DBSqlite(activityDossier).gravarLista(listaOSBI);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tv_ref;
        private final TextView tv_qtt;
        private final TextView tv_dim;
        private final LinearLayout llinha;
        private final Context contextHolder;
        private final TextView tv_numlinha;
        private final TextView tv_qtt_prod;

        //        public ViewHolder(View itemView, int ViewType) {
        public ViewHolder(View itemView) {
            super(itemView);
            llinha = (LinearLayout) itemView.findViewById(R.id.llinha);

            tv_ref = (TextView) itemView.findViewById(R.id.tv_ref);
            tv_qtt = (TextView) itemView.findViewById(R.id.tv_qtt);
            tv_dim = (TextView) itemView.findViewById(R.id.tv_dim);
            tv_numlinha = (TextView) itemView.findViewById(R.id.tv_numlinha);
            tv_qtt_prod = (TextView) itemView.findViewById(R.id.tv_qtt_prod);
            contextHolder = activityDossier;
        }
    }

    public void pintarObjecto(ViewHolder holder, int qttPedida, int qttProduzida) {
        SharedPreferences prefs = MrApp.getPrefs();
        final boolean vertudo = prefs.getBoolean(Constantes.PREF_MOSTRAR_TODAS_LINHAS_PROD, true);
        holder.tv_qtt.setText("" + qttPedida);
        holder.tv_qtt_prod.setText("" + qttProduzida);
        holder.llinha.setBackgroundColor(ContextCompat.getColor(holder.contextHolder, R.color.md_white_1000));
        if (qttProduzida > 0)
            holder.llinha.setBackgroundColor(ContextCompat.getColor(holder.contextHolder, R.color.md_amber_200));
        holder.llinha.setVisibility(View.VISIBLE);
        if (qttPedida - qttProduzida == 0) {
            if (vertudo) {
                holder.llinha.setBackgroundColor(ContextCompat.getColor(holder.contextHolder, R.color.md_green_200));
            } else {
                removerItem(holder.getAdapterPosition());
            }
        }
    }

    private class TaskQtd extends AsyncTask<Void, Void, Void> {
        private final OSBI osbi;
        private final ViewHolder holder;

        public TaskQtd(ViewHolder viewHolder) {
            this.osbi = getItem(viewHolder.getAdapterPosition());
            this.holder = viewHolder;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            final int qtt = osbi.qtt;
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
            DatabaseReference refOSPROD = ref.child(Constantes.NODE_OSPROD).child(osbi.bostamp);
            refOSPROD.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String bostamp = dataSnapshot.getKey();
                    ArrayList<OSPROD> lista = new ArrayList<>();
                    for (DataSnapshot d : dataSnapshot.getChildren()) {
                        Log.i(TAG, "REF: " + d.toString());
                        OSPROD osprod = d.getValue(OSPROD.class);
                        osprod.bostamp = bostamp;
                        osprod.bistamp = d.getKey();
                        lista.add(osprod);
                    }
                    int qttFeita = 0;
                    for (OSPROD osprod : lista) {
                        if (osprod.ref.equals(osbi.ref)
                                && osprod.design.equals(osbi.design)
                                && osprod.dim.equals(osbi.dim)
                                && osprod.mk.equals(osbi.mk)
                                && osprod.numlinha.equals(osbi.numlinha)
                                ) {
                            qttFeita += osprod.qtt;
                        }
                    }
                    final int finalQttFeita = qttFeita;
                    activityDossier.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pintarObjecto(holder, qtt, finalQttFeita);
                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

        }
    }

    public void removerItem(int position) {
        if (position >= 0 && position <= lista.size()) {
            lista.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, lista.size());
        }
    }
}
