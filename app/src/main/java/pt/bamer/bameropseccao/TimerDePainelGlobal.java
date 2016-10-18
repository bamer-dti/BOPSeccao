package pt.bamer.bameropseccao;

import java.util.Timer;

public class TimerDePainelGlobal extends Timer {
    public TimerDePainelGlobal(PainelGlobal activity) {
        activity.addTimer(this);
    }
}
