package pt.bamer.bameropseccao;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TableLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import pt.bamer.bameropseccao.objectos.Machina;
import pt.bamer.bameropseccao.utils.Constantes;
import pt.bamer.bameropseccao.utils.ValoresDefeito;


public class Entrada extends AppCompatActivity {

    private static final String TAG = Entrada.class.getSimpleName();
    private SmoothProgressBar pb_smooth;
    private TableLayout tbl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrada);

        setTitle(MrApp.getTituloBase(this));

        final Spinner spinn_seccao = (Spinner) findViewById(R.id.spinn_seccao);
        final SharedPreferences prefs = MrApp.getPrefs();
        final String[] seccao = {prefs.getString(Constantes.PREF_SECCAO, ValoresDefeito.SECCAO)};

        tbl = (TableLayout) findViewById(R.id.tbl);
        tbl.setVisibility(View.GONE);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(Constantes.NODE_SECCAO);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Machina> listaMachinas = new ArrayList<>();
                ArrayList<String> listaSeccao = new ArrayList<>();
                for (DataSnapshot snapSeccao : dataSnapshot.getChildren()) {
                    String secc = snapSeccao.getKey();
                    listaSeccao.add(secc);
                    for (DataSnapshot snapMaqOper : snapSeccao.getChildren()) {
                        if(snapMaqOper.getKey().equals(Constantes.NODE_MACHINAS)) {
                            for(DataSnapshot snapMachinas : snapMaqOper.getChildren()){
                                String maq = snapMachinas.getKey();
                                Machina machina = snapMachinas.getValue(Machina.class);
                                machina.seccao = secc;
                                machina.ref = maq;
                                listaMachinas.add(machina);
                            }
                        }
                    }
                }

                String[] array_seccoes = new String[listaSeccao.size()];
                array_seccoes = listaSeccao.toArray(array_seccoes);
                spinn_seccao.setAdapter(new ArrayAdapter<>(spinn_seccao.getContext(), android.R.layout.simple_spinner_dropdown_item, array_seccoes));
                int pos = Arrays.asList(array_seccoes).indexOf(seccao[0]);
                spinn_seccao.setSelection(pos);

                tbl.setVisibility(View.VISIBLE);
                pb_smooth.setVisibility(View.GONE);

                MrApp.setListaDeMachinas(listaMachinas);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Button butok = (Button) findViewById(R.id.butok);
        butok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), PainelGlobal.class);
                startActivity(intent);
            }
        });

        Log.i(TAG, "SECÇÃO PREFERENCE: " + seccao[0]);
//        String[] array_seccoes = getResources().getStringArray(R.array.array_seccao);
//        int pos = Arrays.asList(array_seccoes).indexOf(seccao[0]);
//        spinn_seccao.setSelection(pos);
        spinn_seccao.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, final View view, int i, long l) {
                String novaSeccao = adapterView.getItemAtPosition(i).toString();
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(Constantes.PREF_SECCAO, novaSeccao);
                editor.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Spinner spinn_estado = (Spinner) findViewById(R.id.spinner_estado);
        final String estado = prefs.getString(Constantes.PREF_ESTADO, ValoresDefeito.ESTADO);
        Log.i(TAG, "ESTADO PREFERENCE: " + estado);
        String[] array_estados = getResources().getStringArray(R.array.array_estados);
        int posEstado = Arrays.asList(array_estados).indexOf(estado);
        spinn_estado.setSelection(posEstado);
        spinn_estado.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, final View view, int i, long l) {
                String novoEstado = adapterView.getItemAtPosition(i).toString();
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(Constantes.PREF_ESTADO, novoEstado);
                editor.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        final Spinner spinner_funcionario = (Spinner) findViewById(R.id.spinner_funcionario);


        pb_smooth = (SmoothProgressBar) findViewById(R.id.pb_smooth);
        pb_smooth.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.exit(0);
    }
}
