package pt.bamer.bameropseccao;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.ToneGenerator;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hanks.htextview.HTextView;

import org.joda.time.DateTime;

import java.util.ArrayList;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import pt.bamer.bameropseccao.adapters.AdapterOS;
import pt.bamer.bameropseccao.charts.PieHoje;
import pt.bamer.bameropseccao.database.DBSqlite;
import pt.bamer.bameropseccao.objectos.Machina;
import pt.bamer.bameropseccao.objectos.OSBI;
import pt.bamer.bameropseccao.objectos.OSBO;
import pt.bamer.bameropseccao.objectos.OSPROD;
import pt.bamer.bameropseccao.objectos.OSTIMER;
import pt.bamer.bameropseccao.utils.Constantes;
import pt.bamer.bameropseccao.utils.Funcoes;

public class PainelGlobal extends AppCompatActivity {
    private static final String TAG = "LOG" + PainelGlobal.class.getSimpleName();
    private SmoothProgressBar pb_smooth;
    private PieHoje pieQtdsHoje;
    private HTextView htv_qtt_antes;
    private HTextView htv_qtt_antes_produzido;
    private HTextView htv_qtt_amanha;
    private HTextView htv_qtt_amanha_produzido;
    private HTextView htv_qtt_futuro;
    private HTextView htv_qtt_futuro_produzido;
    private HTextView htv_inspeccao_numero;
    private AdapterOS adapterOS;
    private Context context = this;
    private ArrayList<OSBO> listaOSBO = new ArrayList<>();


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");
        setContentView(R.layout.activity_painel_global);

        setTitle(MrApp.getSeccao() + " *** " + MrApp.getTituloBase(this));

        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pb_smooth = (SmoothProgressBar) findViewById(R.id.pb_smooth);
        pb_smooth.setVisibility(View.INVISIBLE);

        CardView cardview_inspeccao = (CardView) findViewById(R.id.card_view_inspeccao);
        cardview_inspeccao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "** ainda não implementado **", Toast.LENGTH_SHORT).show();
            }
        });

        htv_inspeccao_numero = (HTextView) findViewById(R.id.htv_prontas);
        htv_inspeccao_numero.animateText("0");

        RecyclerView recycler_os = (RecyclerView) findViewById(R.id.recycler_os);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recycler_os.setLayoutManager(linearLayoutManager);
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(250);
        itemAnimator.setRemoveDuration(250);
        recycler_os.setItemAnimator(itemAnimator);

        htv_qtt_futuro = (HTextView) findViewById(R.id.htv_qtt_futuro);
        htv_qtt_futuro_produzido = (HTextView) findViewById(R.id.htv_qtt_futuro_produzido);

        htv_qtt_amanha = (HTextView) findViewById(R.id.htv_qtt_amanha);
        htv_qtt_amanha_produzido = (HTextView) findViewById(R.id.htv_qtt_amanha_produzido);

        htv_qtt_antes = (HTextView) findViewById(R.id.htv_qtt_antes);
        htv_qtt_antes_produzido = (HTextView) findViewById(R.id.htv_qtt_antes_produzido);

        pieQtdsHoje = (PieHoje) findViewById(R.id.pie_hoje);

        configFirebase();

        adapterOS = new AdapterOS(this);
        recycler_os.setAdapter(adapterOS);
        efectuarMostradorMaquinas(this);
    }

    private void configFirebase() {
        FirebaseDatabase databaseref = FirebaseDatabase.getInstance();

        DatabaseReference refFirebaseOSBO = databaseref.getReference(Constantes.NODE_OSBO);
//        attachRecyclerViewAdapter();
        refFirebaseOSBO.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        ValueEventListener listenerFirebaseOSBO = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                new TaskFirebaseOSBO(dataSnapshot).execute();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        refFirebaseOSBO.addValueEventListener(listenerFirebaseOSBO);

        DatabaseReference refFirebaseOSBI = databaseref.getReference(Constantes.NODE_OSBI);
        ValueEventListener listenerFirebaseOSBI = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                new TaskFirebaseOSBI(dataSnapshot).execute();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        refFirebaseOSBI.addValueEventListener(listenerFirebaseOSBI);

        DatabaseReference refFirebaseOSPROD = databaseref.getReference(Constantes.NODE_OSPROD);
        ValueEventListener listenerFirebaseOSPROD = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                new TaskFirebaseOSPROD(dataSnapshot).execute();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        refFirebaseOSPROD.addValueEventListener(listenerFirebaseOSPROD);

        DatabaseReference refFirebaseOSTIMER = databaseref.getReference(Constantes.NODE_OSTIMER);
        ValueEventListener listenerFirebaseOSTIMER = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                new TaskFirebaseOSTIMER(dataSnapshot).execute();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        refFirebaseOSTIMER.addValueEventListener(listenerFirebaseOSTIMER);
    }

    private void efectuarMostradorMaquinas(final Context context) {
        LinearLayout ll_maquinas = (LinearLayout) findViewById(R.id.ll_maquinas);
        ArrayList<Machina> listaMachinas = MrApp.getMachinas();
        final ArrayList<Cartao_Machina> lista_cartoes_machina = new ArrayList<>();
        for (int i = 0; i < listaMachinas.size(); i++) {
            final Machina machina = listaMachinas.get(i);
            Log.i(TAG, "Nome da máquina: " + machina);

            Cartao_Machina cartao_machina = new Cartao_Machina(context, machina);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            int margem = (int) getResources().getDimension(R.dimen.margem_cartao_maquina);
            params.setMargins(margem, margem, margem, margem);
            params.gravity = Gravity.CENTER_HORIZONTAL;
            ll_maquinas.addView(cartao_machina, params);
            lista_cartoes_machina.add(cartao_machina);
        }

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(Constantes.NODE_OSTIMER);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snap) {
                ArrayList<OSTIMER> listaOSTIMER = new ArrayList<>();
                for (DataSnapshot snapshotOSTimer : snap.getChildren()) {
                    String bostamp = snapshotOSTimer.getKey();
                    for (DataSnapshot dataSnapshotOSTIMER : snapshotOSTimer.getChildren()) {
                        OSTIMER ostimer = dataSnapshotOSTIMER.getValue(OSTIMER.class);
                        ostimer.bostamp = bostamp;
                        ostimer.bistamp = dataSnapshotOSTIMER.getKey();
                        if (ostimer.seccao.equals(MrApp.getSeccao())) {
                            listaOSTIMER.add(ostimer);
                        }
                    }
                }
                int resultados = new DBSqlite(context).saveTimers(listaOSTIMER);
                Log.i(TAG, "Gravados " + resultados + " registo(s) no SQL OSTIMER");

                verificarMaquinas(lista_cartoes_machina);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void verificarMaquinas(ArrayList<Cartao_Machina> lista_cartoes_machina) {
        for (Cartao_Machina cartao_machina : lista_cartoes_machina) {
            cartao_machina.actualizarCronometros();
        }
    }

    private class TaskFirebaseOSBO extends AsyncTask<Void, Void, Void> {
        private final DataSnapshot dataSnapShot;

        public TaskFirebaseOSBO(DataSnapshot dataSnapshot) {
            this.dataSnapShot = dataSnapshot;
        }

        @Override
        protected void onPreExecute() {
            pb_smooth.setVisibility(View.VISIBLE);
            Log.i(TAG, "Secção " + MrApp.getSeccao() + ", estado " + MrApp.getEstado());
        }

        @Override
        protected Void doInBackground(Void... voids) {
            listaOSBO = new ArrayList<>();
            for (DataSnapshot snapshotOSBO : dataSnapShot.getChildren()) {
                String bostamp = snapshotOSBO.getKey();
                OSBO osbo = snapshotOSBO.getValue(OSBO.class);
                osbo.bostamp = bostamp;
                if (osbo.seccao.equals(MrApp.getSeccao())
                        && osbo.estado.equals(MrApp.getEstado())) {
                    listaOSBO.add(osbo);
                }
            }
            Log.i(TAG, "listaOSBO: " + listaOSBO.size());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            adapterOS.updateSourceDataOSBO(listaOSBO);
            pb_smooth.setVisibility(View.INVISIBLE);
            new ActualizarQuadros().execute();
        }
    }

    private class TaskFirebaseOSBI extends AsyncTask<Void, Void, Void> {
        private final ArrayList<OSBI> listaOSBI = new ArrayList<>();
        private final DataSnapshot dataSnapShot;

        public TaskFirebaseOSBI(DataSnapshot dataSnapshot) {
            this.dataSnapShot = dataSnapshot;
        }

        @Override
        protected void onPreExecute() {
            pb_smooth.setVisibility(View.VISIBLE);
            Log.i(TAG, "Secção " + MrApp.getSeccao() + ", estado " + MrApp.getEstado());
        }

        @Override
        protected Void doInBackground(Void... voids) {
            for (DataSnapshot snap : dataSnapShot.getChildren()) {
                String bostamp = snap.getKey();
                for (DataSnapshot dataSnapshotOSBI : snap.getChildren()) {
                    OSBI osbi = dataSnapshotOSBI.getValue(OSBI.class);
                    osbi.bostamp = bostamp;
                    osbi.bistamp = dataSnapshotOSBI.getKey();
                    listaOSBI.add(osbi);
                }
            }
            new DBSqlite(context).gravarListaOSBI(listaOSBI);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            pb_smooth.setVisibility(View.INVISIBLE);
        }
    }

    public class TaskFirebaseOSPROD extends AsyncTask<Void, Void, Void> {
        private final DataSnapshot snap;
        private final ArrayList<OSPROD> listaOSPROD;

        public TaskFirebaseOSPROD(DataSnapshot dataSnapshot) {
            this.snap = dataSnapshot;
            this.listaOSPROD = new ArrayList<>();
            pb_smooth.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            for (DataSnapshot snapshotOSPROD : snap.getChildren()) {
                String bostamp = snapshotOSPROD.getKey();
                for (DataSnapshot dataSnapshotOSPROD : snapshotOSPROD.getChildren()) {
                    OSPROD osprod = dataSnapshotOSPROD.getValue(OSPROD.class);
                    osprod.bostamp = bostamp;
                    osprod.bistamp = dataSnapshotOSPROD.getKey();
                    listaOSPROD.add(osprod);
                    Log.v(TAG, "osbostamp:  " + osprod.bostamp + ", bistamp = " + osprod.bistamp + ", qtt = " + osprod.qtt);
                }
            }
            new DBSqlite(context).gravarOSPROD(listaOSPROD);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            adapterOS.actualizarValoresAdapterOSPROD(listaOSPROD);
            pb_smooth.setVisibility(View.INVISIBLE);
        }
    }

    private class TaskFirebaseOSTIMER extends AsyncTask<Void, Void, Void> {
        private final ArrayList<OSTIMER> listaOSTIMER;
        private final DataSnapshot dataSnapShot;

        public TaskFirebaseOSTIMER(DataSnapshot dataSnapshot) {
            this.listaOSTIMER = new ArrayList<>();
            this.dataSnapShot = dataSnapshot;
        }

        @Override
        protected void onPreExecute() {
            pb_smooth.setVisibility(View.VISIBLE);
            Log.i(TAG, "TaskFirebaseOSTIMER Secção " + MrApp.getSeccao() + ", estado " + MrApp.getEstado());
        }

        @Override
        protected Void doInBackground(Void... voids) {
            for (DataSnapshot snapshotOSTIMER : dataSnapShot.getChildren()) {
                String bostamp = snapshotOSTIMER.getKey();
                for (DataSnapshot snap : snapshotOSTIMER.getChildren()) {
                    OSTIMER ostimer = snap.getValue(OSTIMER.class);
                    String bistamp = snap.getKey();
                    ostimer.bostamp = bostamp;
                    ostimer.bistamp = bistamp;
                    if (ostimer.seccao.equals(MrApp.getSeccao())
                            && ostimer.estado.equals(MrApp.getEstado())
                            )
                        listaOSTIMER.add(ostimer);
                }
            }
            Log.i(TAG, "listaOSTIMER: " + listaOSTIMER.size());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            adapterOS.actualizarValoresAdapterOSTIMER(listaOSTIMER);
            pb_smooth.setVisibility(View.INVISIBLE);
        }
    }

    private class ActualizarQuadros extends AsyncTask<Void, Void, Void> {
        private int totalAtrasado = 0;
        private int totalHoje = 0;
        private int totalAmanha = 0;
        private int totalFuturo = 0;
        private int totalAtrasadoProduzido = 0;
        private int totalHojeProduzido = 0;
        private int totalAmanhaProduzido;
        private int totalFuturoProduzido = 0;
        private ArrayList<OSBO> listaInspeccao = new ArrayList<>();

        public ActualizarQuadros() {

        }

        @Override
        protected void onPreExecute() {
            pb_smooth.setVisibility(View.VISIBLE);
            Log.i(TAG, "Secção " + MrApp.getSeccao() + ", estado " + MrApp.getEstado());
        }

        @Override
        protected Void doInBackground(Void... voids) {
            ArrayList<OSBI> listaOSBI = new DBSqlite(context).getListaOSBIAgrupada();
            ArrayList<OSPROD> listaOSPROD = new DBSqlite(context).getListaOSPROD();
            for (OSBO osbo : listaOSBO) {
                String bostamp = osbo.bostamp;
                DateTime dataHoje = DateTime.now().withTimeAtStartOfDay();
                DateTime dataAmanha = dataHoje.plusDays(1);
                DateTime dataOSBO = Funcoes.cToT(osbo.dtcortef).toDateTime();
                int parcialAtrasado = 0;
                int parcialHoje = 0;
                int parcialAmanha = 0;
                int parcialFuturo = 0;
                int parcialProduzido = 0;

                //QTDS:
                for (OSBI osbi : listaOSBI) {
                    if (osbi.bostamp.equals(bostamp)) {
                        if (dataOSBO.isBefore(dataHoje)) {
                            parcialAtrasado += osbi.qtt;
                            totalAtrasado += osbi.qtt;
                        }
                        if (dataOSBO.isEqual(dataHoje)) {
                            parcialHoje += osbi.qtt;
                            totalHoje += osbi.qtt;
                        }
                        if (dataOSBO.isEqual(dataAmanha)) {
                            parcialAmanha += osbi.qtt;
                            totalAmanha += osbi.qtt;
                        }
                        if (dataOSBO.isAfter(dataAmanha)) {
                            parcialFuturo += osbi.qtt;
                            totalFuturo += osbi.qtt;
                        }
                    }
                }

                //PRODUZIDO:
                for (OSPROD osprod : listaOSPROD) {
                    if (osprod.bostamp.equals(bostamp)) {
                        parcialProduzido += osprod.qtt;
                        if (dataOSBO.isEqual(dataHoje)) {
                            totalHojeProduzido += osprod.qtt;
                        }
                        if (dataOSBO.isBefore(dataHoje)) {
                            totalAtrasadoProduzido += osprod.qtt;
                        }
                        if (dataOSBO.isEqual(dataHoje.plusDays(1))) { //amanhã
                            totalAmanhaProduzido += osprod.qtt;
                        }
                        if (dataOSBO.isAfter(dataHoje.plusDays(1))) { //amanhã
                            totalFuturoProduzido += osprod.qtt;
                        }
                    }
                }
                Log.d(TAG, "Painel: " + bostamp + ", " + dataHoje + ", " + dataOSBO
                        + ": atrasado = " + parcialAtrasado + "/" + totalAtrasado
                        + ": hoje = " + parcialHoje + "/" + totalHoje
                        + ": amanha = " + parcialAmanha + "/" + totalAmanha
                        + ": futuro = " + parcialFuturo + "/" + totalFuturo
                );

                int pedido = parcialAtrasado + parcialHoje + parcialAmanha + parcialFuturo;

                //Prontos para inspecção!
                if (parcialProduzido >= pedido) {
                    listaInspeccao.add(osbo);
                }
            }
            Log.i(TAG, MrApp.getSeccao() + ": Painel: atrasado: " + totalAtrasado + ", hoje = " + totalHoje + ", amanhã = " + totalAmanha + ", futuro = " + totalFuturo);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String textoAntes = htv_qtt_antes.getText().toString();
                    String textoNovo = "" + (totalAtrasado - totalAtrasadoProduzido);
                    if (!textoAntes.equals(textoNovo))
                        htv_qtt_antes.animateText(textoNovo);

                    textoAntes = htv_qtt_antes_produzido.getText().toString();
                    textoNovo = totalAtrasado + "|" + totalAtrasadoProduzido;
                    if (!textoAntes.equals(textoNovo))
                        htv_qtt_antes_produzido.animateText(textoNovo);

                    textoAntes = htv_qtt_amanha.getText().toString();
                    textoNovo = "" + (totalAmanha - totalAmanhaProduzido);
                    if (!textoAntes.equals(textoNovo))
                        htv_qtt_amanha.animateText(textoNovo);

                    textoAntes = htv_qtt_amanha_produzido.getText().toString();
                    textoNovo = totalAmanha + "|" + totalAmanhaProduzido;
                    if (!textoAntes.equals(textoNovo))
                        htv_qtt_amanha_produzido.animateText(textoNovo);


                    textoAntes = htv_qtt_futuro.getText().toString();
                    textoNovo = "" + (totalFuturo - totalFuturoProduzido);
                    if (!textoAntes.equals(textoNovo)) {
                        htv_qtt_futuro.animateText(textoNovo);
                    }

                    textoAntes = htv_qtt_futuro_produzido.getText().toString();
                    textoNovo = totalFuturo + "|" + totalFuturoProduzido;
                    if (!textoAntes.equals(textoNovo))
                        htv_qtt_futuro_produzido.animateText(textoNovo);

                    pieQtdsHoje.setData(totalHojeProduzido, totalHoje);
                }
            });

            //INSPECÇÃO:
            int insAntes = 0;
            try {
                insAntes = Integer.parseInt(htv_inspeccao_numero.getText().toString());
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            final int intNovo = listaInspeccao.size();
            Log.i(TAG, "LISTA INSPECÇÃO TEM " + intNovo + " REGISTOS!");
            if (intNovo > insAntes) {
                new Funcoes.Beep(ToneGenerator.TONE_CDMA_CALL_SIGNAL_ISDN_NORMAL, 200).execute();
            }
            if (insAntes != intNovo) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        htv_inspeccao_numero.animateText(intNovo + "");
                    }
                });
            }

            pb_smooth.setVisibility(View.INVISIBLE);
        }
    }
}
