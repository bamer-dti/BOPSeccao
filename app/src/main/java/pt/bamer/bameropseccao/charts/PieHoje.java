package pt.bamer.bameropseccao.charts;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.ArrayList;
import java.util.Locale;

import pt.bamer.bameropseccao.R;

public class PieHoje extends PieChart {
    private static final String TAG = PieHoje.class.getSimpleName();
    private int qttProduzida;
    private int qttPedida;

    public PieHoje(Context context) {
        super(context);
        initilize();
    }

    public PieHoje(Context context, AttributeSet attrs) {
        super(context, attrs);
        initilize();
    }

    public PieHoje(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initilize();
    }

    private void initilize() {
        setUsePercentValues(false);
        setDescription("");
        setExtraOffsets(5, 10, 5, 5);
        setDragDecelerationFrictionCoef(0.95f);

        setDrawHoleEnabled(true);
        setHoleColor(Color.WHITE);

        setTransparentCircleColor(Color.WHITE);

        setTransparentCircleAlpha(110);
        setHoleRadius(38f);
        setTransparentCircleRadius(61f);

        setDrawCenterText(true);

        setRotationAngle(0);
        // enable rotation of the chart by touch
        setRotationEnabled(true);
        setHighlightPerTapEnabled(false);

//        animateY(1400, Easing.EasingOption.EaseInOutQuad);
        setEntryLabelColor(Color.DKGRAY);
        setEntryLabelTextSize(12f);

    }

    public void setData(int produzida, int totalpedido) {
        if (produzida == this.qttProduzida && totalpedido == this.qttPedida) {
            return;
        }

        int qtdDelta = Math.abs(produzida - qttProduzida);

        int duracao = 1000 + qtdDelta * 10;
        float paraAngulo = getRotationAngle() + qtdDelta * 10;
        Log.i(TAG, "de " + getRotationAngle() + " para " + paraAngulo + " em " + duracao + " milisegundos. Qtd = " + qtdDelta);
        if (getRotationAngle() != paraAngulo) {
            spin(duracao, getRotationAngle(), paraAngulo, Easing.EasingOption.EaseOutCirc);
        }

        ArrayList<PieEntry> listaDeValores = new ArrayList<>();
        PieEntry qttProd = new PieEntry(produzida, "produzida");
        listaDeValores.add(qttProd);

        PieEntry qttTotal = new PieEntry(totalpedido, "total");
        listaDeValores.add(qttTotal);

        PieDataSet dataSet = new PieDataSet(listaDeValores, "");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        ArrayList<Integer> colors = new ArrayList<>();
        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        dataSet.setColors(colors);
        dataSet.setSelectionShift(0f);


        PieData data = new PieData(dataSet);

        ValueFormatter valueFormatter = new ValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                return String.format(Locale.getDefault(), "%.0f", value);
            }
        };
        data.setValueFormatter(valueFormatter);
        data.setValueTextSize(30f);
        data.setValueTextColor(ContextCompat.getColor(getContext(), R.color.md_green_800));
//        data.setValueTypeface(mTfLight);
        setData(data);

        // undo all highlights
        highlightValues(null);

        float calculo = produzida * 1f / totalpedido * 1f * 100f;
        setCenterText(String.format(Locale.getDefault(), "%.0f%%", calculo));
        setCenterTextSize(30f);

        invalidate();
        this.qttPedida = totalpedido;
        this.qttProduzida = produzida;
    }

    public int getQttProduzida() {
        return qttProduzida;
    }

    public int getQttPedida() {
        return qttPedida;
    }
}
