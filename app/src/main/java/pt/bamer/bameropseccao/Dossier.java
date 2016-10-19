package pt.bamer.bameropseccao;

import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import pt.bamer.bameropseccao.adapters.TarefaRecyclerAdapter;
import pt.bamer.bameropseccao.objectos.OSBI;
import pt.bamer.bameropseccao.utils.Constantes;

public class Dossier extends AppCompatActivity {
    private static final String TAG = Dossier.class.getSimpleName();
    Dossier activity = this;
    private RecyclerView recyclerView;
    private String bostamp;
    private DatabaseReference ref;
    private ValueEventListener listenerFirebase;
    private TarefaRecyclerAdapter adapterTarefaRecyclerAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_dossier);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Paint paint = new Paint();
        paint.setStrokeWidth(1);
        paint.setColor(Color.DKGRAY);
        paint.setAntiAlias(true);
        paint.setPathEffect(new DashPathEffect(new float[]{25.0f, 25.0f}, 0));

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(250);
        itemAnimator.setRemoveDuration(250);
        recyclerView.setItemAnimator(itemAnimator);

        adapterTarefaRecyclerAdapter = new TarefaRecyclerAdapter(this, null);
        recyclerView.setAdapter(adapterTarefaRecyclerAdapter);

        Bundle extras = getIntent().getExtras();
        bostamp = "";
        if (extras != null) {
            bostamp = extras.getString(Constantes.INTENT_EXTRA_BOSTAMP);
            Log.i(TAG, "BOSTAMP bundled: " + bostamp);
        }
        FirebaseDatabase databaseref = FirebaseDatabase.getInstance();
        ref = databaseref.getReference().child("osbi03").child(bostamp);
        listenerFirebase = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<OSBI> listaOSBI = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String bistamp = snapshot.getKey();
                    OSBI osbi = snapshot.getValue(OSBI.class);
                    osbi.bistamp = bistamp;
                    osbi.bostamp = bostamp;
                    listaOSBI.add(osbi);
                }
                adapterTarefaRecyclerAdapter.populate(listaOSBI);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        ref.addValueEventListener(listenerFirebase);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ref.removeEventListener(listenerFirebase);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
