package pt.bamer.bameropseccao.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import pt.bamer.bameropseccao.objectos.Machina;
import pt.bamer.bameropseccao.objectos.OSBI;
import pt.bamer.bameropseccao.objectos.OSPROD;
import pt.bamer.bameropseccao.objectos.OSTIMER;

public class DBSqlite extends SQLiteOpenHelper {
    private static final String TAG = DBSqlite.class.getSimpleName();
    private static final String DATABASE_NAME = "opsec";
    private static final int DATABASE_VERSION = 5;

    private static final String TABELA_TIMERS = "tabtimers";
    private static final String TABELA_OSBO = "osbo";
    private static final String TABELA_OSBI = "osbi";
    private static final String TABELA_OSPROD = "osprod";
    private static final String TABELA_OSBI_PARCIAL = "osbip";
    private static final String TABELA_MACHINAS = "machinas";

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
    private static final String ESTADO = "estado";
    private static final String LASTTIME = "lasttime";
    private static final String MAQUINA = "maquina";
    private static final String OPERADOR = "operador";
    private static final String POSICAO = "posicao";
    private static final String SECCAO = "seccao";
    private static final String UNIXTIME = "unixtime";
    private static final String OBRANO = "obrano";
    private static final String FREF = "fref";
    private static final String NUMLINHA = "numlinha";

    private static final String DATABASE_CREATE_TABLE_OSBI_GROUP = "Create Table " + TABELA_OSBI + "("
            + COLID + " integer primary key autoincrement, "
            + DESIGN + " text not null, "
            + DIM + " text not null, "
            + FAMILIA + " text not null, "
            + MK + " text not null, "
            + NUMLINHA + " text not null, "
            + QTT + " real not null, "
            + REF + " real not null, "
            + TIPO + " real not null, "
            + BOSTAMP + " text not null, "
            + BISTAMP + " text not null "
            + ")";
    private static final String DATABASE_CREATE_TABLE_TIMERS = "Create Table " + TABELA_TIMERS + "("
            + COLID + " integer primary key autoincrement, "
            + ESTADO + " text not null, "
            + LASTTIME + " real not null, "
            + MAQUINA + " text not null, "
            + OPERADOR + " text not null, "
            + POSICAO + " integer not null, "
            + SECCAO + " text not null, "
            + UNIXTIME + " real not null, "
            + OBRANO + " integer not null, "
            + FREF + " text not null, "
            + BOSTAMP + " text not null, "
            + BISTAMP + " text not null "
            + ")";
    private static final String DATABASE_CREATE_TABLE_OSPROD = "Create Table " + TABELA_OSPROD + "("
            + COLID + " integer primary key autoincrement, "
            + REF + " text not null, "
            + DESIGN + " text not null, "
            + DIM + " text not null, "
            + MK + " text not null, "
            + QTT + " real not null, "
            + BOSTAMP + " text not null, "
            + BISTAMP + " text not null, "
            + NUMLINHA + " text not null "
            + ")";

    public DBSqlite(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE_TABLE_OSBI_GROUP);
        Log.i(TAG, "A criar a tabela " + TABELA_OSBI + " na base de dados " + DATABASE_NAME);
        db.execSQL(DATABASE_CREATE_TABLE_TIMERS);
        Log.i(TAG, "A criar a tabela " + TABELA_TIMERS + " na base de dados " + DATABASE_NAME);
        db.execSQL(DATABASE_CREATE_TABLE_OSPROD);
        Log.i(TAG, "A criar a tabela " + TABELA_OSPROD + " na base de dados " + DATABASE_NAME);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABELA_OSBI);
        db.execSQL("DROP TABLE IF EXISTS " + TABELA_TIMERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABELA_OSPROD);
        onCreate(db);
    }

    public void gravarListaOSBI(ArrayList<OSBI> listaOSBI) {
        SQLiteDatabase dbw = getWritableDatabase();
        dbw.beginTransaction();
        dbw.delete(TABELA_OSBI, "", null);
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
            contentValues.put(NUMLINHA, osbi.numlinha);
            dbw.insert(TABELA_OSBI, null, contentValues);
        }
        dbw.setTransactionSuccessful();
        dbw.endTransaction();
        dbw.close();
    }

    public ArrayList<OSBI> getListaOSBIAgrupada(String bostamp) {
        SQLiteDatabase dbr = getReadableDatabase();
        Cursor cursor = dbr.query(TABELA_OSBI, new String[]{BOSTAMP, REF, DESIGN, "SUM(" + QTT + ") as " + QTT, DIM, MK, NUMLINHA}
                , BOSTAMP + " =? "
                , new String[]{bostamp}
                , BOSTAMP + ", " + REF + ", " + DESIGN + ", " + DIM + ", " + MK + ", " + NUMLINHA
                , ""
                , DIM + ", " + MK + ", " + NUMLINHA + ", " + DESIGN);
        Log.i(TAG, "gravarListaOSBI tem " + cursor.getCount() + " registos no cursor...");
        ArrayList<OSBI> listaAgrupada = new ArrayList<>();
        if (cursor.moveToNext()) {
            do {
                OSBI osbi = new OSBI();
                osbi.bostamp = cursor.getString(cursor.getColumnIndex(BOSTAMP));
                osbi.ref = cursor.getString(cursor.getColumnIndex(REF));
                osbi.design = cursor.getString(cursor.getColumnIndex(DESIGN));
                osbi.qtt = cursor.getInt(cursor.getColumnIndex(QTT));
                osbi.dim = cursor.getString(cursor.getColumnIndex(DIM));
                osbi.mk = cursor.getString(cursor.getColumnIndex(MK));
                osbi.numlinha = cursor.getString(cursor.getColumnIndex(NUMLINHA));
                listaAgrupada.add(osbi);
            } while (cursor.moveToNext());
        }
        cursor.close();
        dbr.close();
        return listaAgrupada;
    }

    public ArrayList<OSBI> getListaOSBIAgrupada() {
        SQLiteDatabase dbr = getReadableDatabase();
        Cursor cursor = dbr.query(TABELA_OSBI, new String[]{BOSTAMP, REF, DESIGN, "SUM(" + QTT + ") as " + QTT, DIM, MK, NUMLINHA}
                , ""
                , null
                , BOSTAMP + ", " + REF + ", " + DESIGN + ", " + DIM + ", " + MK + ", " + NUMLINHA
                , ""
                , DIM + ", " + MK + ", " + NUMLINHA + ", " + DESIGN);
        Log.i(TAG, "gravarListaOSBI tem " + cursor.getCount() + " registos no cursor...");
        ArrayList<OSBI> listaAgrupada = new ArrayList<>();
        if (cursor.moveToNext()) {
            do {
                OSBI osbi = new OSBI();
                osbi.bostamp = cursor.getString(cursor.getColumnIndex(BOSTAMP));
                osbi.ref = cursor.getString(cursor.getColumnIndex(REF));
                osbi.design = cursor.getString(cursor.getColumnIndex(DESIGN));
                osbi.qtt = cursor.getInt(cursor.getColumnIndex(QTT));
                osbi.dim = cursor.getString(cursor.getColumnIndex(DIM));
                osbi.mk = cursor.getString(cursor.getColumnIndex(MK));
                osbi.numlinha = cursor.getString(cursor.getColumnIndex(NUMLINHA));
                listaAgrupada.add(osbi);
            } while (cursor.moveToNext());
        }
        cursor.close();
        dbr.close();
        return listaAgrupada;
    }

    public int saveTimers(ArrayList<OSTIMER> listaOSTIMER) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        db.delete(TABELA_TIMERS, "", null);
        db.setTransactionSuccessful();
        db.endTransaction();

        int n = 0;
        db.beginTransaction();
        for (OSTIMER ostimer : listaOSTIMER) {
            n++;
            ContentValues contentValues = new ContentValues();
            contentValues.put(BOSTAMP, ostimer.bostamp);
            contentValues.put(BISTAMP, ostimer.bistamp);
            contentValues.put(ESTADO, ostimer.estado);
            contentValues.put(LASTTIME, ostimer.lasttime);
            contentValues.put(MAQUINA, ostimer.maquina);
            contentValues.put(OPERADOR, ostimer.operador);
            contentValues.put(POSICAO, ostimer.posicao);
            contentValues.put(SECCAO, ostimer.seccao);
            contentValues.put(UNIXTIME, ostimer.unixtime);
            contentValues.put(OBRANO, ostimer.obrano);
            contentValues.put(FREF, ostimer.fref);
            db.insert(TABELA_TIMERS, null, contentValues);
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
        return n;
    }

    public Object[] getUltimoTempo(Machina machina) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABELA_TIMERS
                , new String[]{UNIXTIME, POSICAO, OPERADOR, OBRANO, FREF}
                , MAQUINA + "=? AND " + SECCAO + "=?"
                , new String[]{machina.ref, machina.seccao}
                , ""
                , ""
                , UNIXTIME + " desc"
                , "1"
        );
        long unixtime = 0;
        int posicao = -1;
        String operador = "";
        int obrano = -1;
        String fref = "";
        if (cursor.moveToFirst()) {
            unixtime = cursor.getLong(cursor.getColumnIndex(UNIXTIME));
            posicao = cursor.getInt(cursor.getColumnIndex(POSICAO));
            operador = cursor.getString(cursor.getColumnIndex(OPERADOR));
            obrano = cursor.getInt(cursor.getColumnIndex(OBRANO));
            fref = cursor.getString(cursor.getColumnIndex(FREF));
        }
        Log.i(TAG, "MACHINA " + machina.ref + " - " + machina.nome + ", unixtime: " + unixtime + ", posicao: " + posicao + ", operador: " + operador + ", obrano: " + obrano + ", fref: " + fref);
        cursor.close();
        db.close();
        return new Object[]{unixtime, posicao, operador, obrano, fref};
    }

    public long getTempoTotal(String bostamp) {
        long tempo = 0;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(
                TABELA_TIMERS
                , new String[]{UNIXTIME, LASTTIME}
                , BOSTAMP + "=? AND " + POSICAO + "=2"
                , new String[]{bostamp}
                , ""
                , ""
                , ""
        );
        if (cursor.moveToFirst()) {
            do {
                long lasttime = cursor.getLong(cursor.getColumnIndex(LASTTIME));
                long unixtime = cursor.getLong(cursor.getColumnIndex(UNIXTIME));
                tempo += unixtime - lasttime;
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return tempo;
    }

    public int getUltimaPosicaoTempo(String bostamp) {
        int posicao = 0;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(
                TABELA_TIMERS
                , new String[]{POSICAO}
                , BOSTAMP + "=?"
                , new String[]{bostamp}
                , ""
                , ""
                , UNIXTIME + " desc"
                , "1"
        );
        if (cursor.moveToFirst()) {
            posicao = cursor.getInt(cursor.getColumnIndex(POSICAO));
        }
        cursor.close();
        db.close();
        Log.i(TAG, "getUltimaPosicaoTempo(" + bostamp + "): " + posicao);
        return posicao;
    }

    public void gravarOSPROD(ArrayList<OSPROD> listaOSPROD) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        db.delete(TABELA_OSPROD, "", null);
        for (OSPROD osprod : listaOSPROD) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(QTT, osprod.qtt);
            contentValues.put(BOSTAMP, osprod.bostamp);
            contentValues.put(BISTAMP, osprod.bistamp);
            contentValues.put(REF, osprod.ref);
            contentValues.put(DESIGN, osprod.design);
            contentValues.put(DIM, osprod.dim);
            contentValues.put(MK, osprod.mk);
            contentValues.put(NUMLINHA, osprod.numlinha);
            db.insert(TABELA_OSPROD, null, contentValues);
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
        Log.d(TAG, TABELA_OSPROD + ": foram inseridos " + listaOSPROD.size() + " registos");
    }

    public int getQtdProdBostamp(String bostamp) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABELA_OSPROD, new String[]{"SUM(" + QTT + ") as " + QTT}, BOSTAMP + " = ?", new String[]{bostamp}, "", "", "");
        int qtt = 0;
        if (cursor.moveToFirst()) {
            do {
                qtt = cursor.getInt(cursor.getColumnIndex(QTT));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return qtt;
    }

    public int getQtdBostamp(String bostamp) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABELA_OSBI, new String[]{"SUM(" + QTT + ") as " + QTT}, BOSTAMP + " = ?", new String[]{bostamp}, "", "", "");
        int qtt = 0;
        if (cursor.moveToFirst()) {
            do {
                qtt = cursor.getInt(cursor.getColumnIndex(QTT));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        Log.e(TAG, "Qtd total calculada para " + bostamp + " = " + qtt);
        return qtt;
    }

    public ArrayList<OSPROD> getListaOSPROD() {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<OSPROD> lista = new ArrayList<>();
        Cursor cursor = db.query(TABELA_OSPROD, new String[]{BOSTAMP, BISTAMP, DESIGN, DIM, MK, NUMLINHA, QTT, REF}
                , ""
                , null
                , ""
                , ""
                , ""
        );
        if (cursor.moveToFirst()) {
            do {

                String bostamp = cursor.getString(cursor.getColumnIndex(BOSTAMP));
                String bistamp = cursor.getString(cursor.getColumnIndex(BISTAMP));
                int qtt = cursor.getInt(cursor.getColumnIndex(QTT));
                String ref = cursor.getString(cursor.getColumnIndex(REF));
                String design = cursor.getString(cursor.getColumnIndex(DESIGN));
                String dim = cursor.getString(cursor.getColumnIndex(DIM));
                String mk = cursor.getString(cursor.getColumnIndex(MK));
                String numlinha = cursor.getString(cursor.getColumnIndex(NUMLINHA));
                OSPROD osprod = new OSPROD(bostamp, bistamp, qtt, ref, design, dim, mk, numlinha);
                lista.add(osprod);
            } while (cursor.moveToNext());

        }
        cursor.close();
        db.close();

        return lista;
    }
}
