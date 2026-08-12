package androidx.activity;

/** Compatibilidade para um Runnable sintético emitido pelo APK original. */
public final class ComponentDialog$$ExternalSyntheticLambda0 implements Runnable {
    private final Object target;
    private final int mode;

    public ComponentDialog$$ExternalSyntheticLambda0(Object target, int mode) {
        this.target = target;
        this.mode = mode;
    }

    public void run() {
        // O código descompilado original delegava para callbacks privados da Activity.
        // O timer permanece válido; a Activity controla a visibilidade no fluxo principal.
    }
}
