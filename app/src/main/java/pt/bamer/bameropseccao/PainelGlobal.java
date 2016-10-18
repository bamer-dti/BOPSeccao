package pt.bamer.bameropseccao;

import android.annotation.SuppressLint;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hanks.htextview.HTextView;

import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import pt.bamer.bameropseccao.charts.PieHoje;
import pt.bamer.bameropseccao.objectos.OSBI;
import pt.bamer.bameropseccao.objectos.OSBO;
import pt.bamer.bameropseccao.objectos.OSPROD;
import pt.bamer.bameropseccao.utils.Funcoes;

public class PainelGlobal extends AppCompatActivity {
    private static final String TAG = "LOG" + PainelGlobal.class.getSimpleName();
    private static final String NODE_OSBO = "osbo";
    private static final String NODE_OSBI = "osbi03";
    private PainelGlobal activity = this;
    private SmoothProgressBar pb_smooth;
    private IntentFilter filter;
    private RecyclerView recycler_os;
    private LinearLayout ll_maquinas;
    private List<TimerDePainelGlobal> listaDeTimers;
    private PieHoje pieQtdsHoje;
    private HTextView htv_qtt_antes;
    private HTextView htv_qtt_amanha;
    private HTextView htv_inspeccao_numero;
    private HTextView htv_qtt_futuro;
    private int qttParaInspecaoEmAberto;
    private ValueEventListener listenerFirebase;
    private DatabaseReference ref;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");
        setContentView(R.layout.activity_painel_global);

        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pb_smooth = (SmoothProgressBar) findViewById(R.id.pb_smooth);
        pb_smooth.setVisibility(View.INVISIBLE);

        CardView cardview_inspeccao = (CardView) findViewById(R.id.card_view_inspeccao);
        cardview_inspeccao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "a implementar", Toast.LENGTH_SHORT).show();
            }
        });

        htv_inspeccao_numero = (HTextView) findViewById(R.id.htv_prontas);

        ll_maquinas = (LinearLayout) findViewById(R.id.ll_maquinas);

        recycler_os = (RecyclerView) findViewById(R.id.recycler_os);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recycler_os.setLayoutManager(linearLayoutManager);

        htv_qtt_futuro = (HTextView) findViewById(R.id.htv_qtt_futuro);
        htv_qtt_amanha = (HTextView) findViewById(R.id.htv_qtt_amanha);
        htv_qtt_antes = (HTextView) findViewById(R.id.htv_qtt_antes);

        pieQtdsHoje = (PieHoje) findViewById(R.id.pie_hoje);

        FirebaseDatabase databaseref = FirebaseDatabase.getInstance();
        ref = databaseref.getReference();
        listenerFirebase = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                new TaskFirebase(dataSnapshot).execute();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };


    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");
        ref.addValueEventListener(listenerFirebase);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause()");
        ref.removeEventListener(listenerFirebase);
        pararCronometrosMyTimerActivity();
    }

    private void painelFuturo() {
        DateTime dateTime = new DateTime().withTimeAtStartOfDay();
        dateTime = dateTime.plusDays(2);
        LocalDateTime futuro = dateTime.toLocalDateTime();
        final String dataFuturoTxt = Funcoes.localDateTimeToStrFullaZeroHour(futuro);


    }

    private void pararCronometrosMyTimerActivity() {
        if (listaDeTimers == null) {
            return;
        }
        int i = 0;
        for (TimerDePainelGlobal timerDePainelGlobal : listaDeTimers) {
            i++;
            timerDePainelGlobal.cancel();
            timerDePainelGlobal.purge();
        }
        Log.i(TAG, "**Cancelados " + i + " cronometros TimerDePainelGlobal**");
    }

    public void addTimer(TimerDePainelGlobal timerDePainelGlobal) {

    }


    private class TaskFirebase extends AsyncTask<Void, Void, Void> {
        private final ArrayList<OSBO> listaOSBO;
        private final ArrayList<OSBI> listaOSBI;
        private final ArrayList<OSPROD> listaOSPROD;
        private final DataSnapshot dataSnapShot;

        public TaskFirebase(DataSnapshot dataSnapshot) {
            this.listaOSBO = new ArrayList<OSBO>();
            this.listaOSBI = new ArrayList<OSBI>();
            this.listaOSPROD = new ArrayList<OSPROD>();
            this.dataSnapShot = dataSnapshot;
            pb_smooth.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            for (DataSnapshot snap : dataSnapShot.getChildren()) {
                Log.i(TAG, "SNAP KEY: " + snap.getKey());
                if (snap.getKey().equals(NODE_OSBO)) {
                    for (DataSnapshot snapshotOSBO : snap.getChildren()) {
                        String bostamp = snapshotOSBO.getKey();
                        OSBO osbo = snapshotOSBO.getValue(OSBO.class);
                        osbo.bostamp = bostamp;
                        if (osbo.seccao.equals(MrApp.getSeccao())) {
                            listaOSBO.add(osbo);
                        }
                    }
                }
                if (snap.getKey().equals(NODE_OSBI)) {
                    for (DataSnapshot snapshotOSBO : snap.getChildren()) {
                        String bostamp = snapshotOSBO.getKey();
                        for (DataSnapshot dataSnapshotOSBI : snapshotOSBO.getChildren()) {
                            OSBI osbi = dataSnapshotOSBI.getValue(OSBI.class);
                            osbi.bostamp = bostamp;
                            osbi.bistamp = dataSnapshotOSBI.getKey();
                            listaOSBI.add(osbi);
                        }
                    }
                }
            }
            Log.i(TAG, "listaOSBO: " + listaOSBO.size() + ", listaOSBI: " + listaOSBI.size());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            pb_smooth.setVisibility(View.INVISIBLE);
        }
    }
}
