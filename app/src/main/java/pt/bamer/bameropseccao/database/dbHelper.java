package pt.bamer.bameropseccao.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import pt.bamer.bameropseccao.objectos.OSBI;

public class dbHelper extends SQLiteOpenHelper {
    private static final String TAG = dbHelper.class.getSimpleName();
    private static final String DATABASE_NAME = "opsec";
    private static final int DATABASE_VERSION = 1;
    private static final String TABELA_OSBI_GROUP = "osbi_grp";
    private static final String COLID = "_id";
    private static final String BOSTAMP = "bostamp";
    private static final String BISTAMP = "bistamp";
    private static final String QTT = "qtt";
    private static final String DESIGN = "design";
    private static final String DIM = "dim";
    private static final String FAMILIA = "familia";
    private static final String MK = "mk";
    private static final String REF = "ref";
    private static final String TIPO = "tipo";
    private static final String DATABASE_CREATE_TABLE_ENTREGAS = "Create Table " + TABELA_OSBI_GROUP + "("
            + COLID + " integer primary key autoincrement, "
            + DESIGN + " text not null, "
            + DIM + " text not null, "
            + FAMILIA + " text not null, "
            + MK + " text not null, "
            + QTT + " real not null, "
            + REF + " real not null, "
            + TIPO + " real not null, "
            + BOSTAMP + " text not null, "
            + BISTAMP + " text not null "
            + ")";
    ;

    public dbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE_TABLE_ENTREGAS);
        Log.i(TAG, "A criar a tabela " + TABELA_OSBI_GROUP + " na base de dados " + DATABASE_NAME);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABELA_OSBI_GROUP);
        onCreate(db);
    }

    public ArrayList<OSBI> gravarLista(ArrayList<OSBI> listaOSBI, boolean mostrarPormenor) {
        SQLiteDatabase dbw = getWritableDatabase();
        dbw.beginTransaction();
        dbw.delete(TABELA_OSBI_GROUP, "", null);
        for (OSBI osbi : listaOSBI) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(DESIGN, osbi.design);
            contentValues.put(DIM, osbi.dim);
            contentValues.put(FAMILIA, osbi.familia);
            contentValues.put(MK, osbi.mk);
            contentValues.put(QTT, osbi.qtt);
            contentValues.put(REF, osbi.ref);
            contentValues.put(TIPO, osbi.tipo);
            contentValues.put(BOSTAMP, osbi.bostamp);
            contentValues.put(BISTAMP, osbi.bistamp);
            dbw.insert(TABELA_OSBI_GROUP, null, contentValues);
        }
        dbw.setTransactionSuccessful();
        dbw.endTransaction();
        dbw.close();

        SQLiteDatabase dbr = getReadableDatabase();
        Cursor cursor = dbr.query(TABELA_OSBI_GROUP, new String[]{REF, DESIGN, "SUM(" + QTT + ") as " + QTT, DIM, MK}, "", null, REF + ", " + DESIGN + ", " + DIM + ", " + MK,
                "", DIM + ", " + MK + ", " + DESIGN);
        Log.i(TAG, "gravarLista tem " + cursor.getCount() + " registos no cursor...");
        ArrayList<OSBI> listaAgrupada = new ArrayList<>();
        if (cursor.moveToNext()) {
            do {
                OSBI osbi = new OSBI();
                osbi.ref = cursor.getString(cursor.getColumnIndex(REF));
                osbi.design = cursor.getString(cursor.getColumnIndex(DESIGN));
                osbi.qtt = cursor.getInt(cursor.getColumnIndex(QTT));
                osbi.dim = cursor.getString(cursor.getColumnIndex(DIM));
                osbi.mk = cursor.getString(cursor.getColumnIndex(MK));
                listaAgrupada.add(osbi);
            } while (cursor.moveToNext());
        }
        if (mostrarPormenor) {
            listaAgrupada.add(new OSBI("", "desagrupado -", 0, "", ""));

            cursor.close();
            cursor = dbr.query(TABELA_OSBI_GROUP, new String[]{REF, DESIGN, QTT, DIM, MK}, "", null, null,
                    "", DIM + ", " + MK + ", " + DESIGN);
            Log.i(TAG, "gravarLista tem " + cursor.getCount() + " registos no cursor...");
            if (cursor.moveToNext()) {
                do {
                    OSBI osbi = new OSBI();
                    osbi.ref = cursor.getString(cursor.getColumnIndex(REF));
                    osbi.design = cursor.getString(cursor.getColumnIndex(DESIGN));
                    osbi.qtt = cursor.getInt(cursor.getColumnIndex(QTT));
                    osbi.dim = cursor.getString(cursor.getColumnIndex(DIM));
                    osbi.mk = cursor.getString(cursor.getColumnIndex(MK));
                    listaAgrupada.add(osbi);
                } while (cursor.moveToNext());
            }
        }
        cursor.close();
        dbr.close();
        return listaAgrupada;
    }
}
