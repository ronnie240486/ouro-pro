package com.ouropro.player.compat;

// BUG corrigido: essa interface existia em
// app/src/main/java/androidx/constraintlayout/core/state/Interpolator.java,
// com o mesmo pacote/nome da interface interna da biblioteca
// androidx.constraintlayout:constraintlayout:2.2.0. Ela foi apagada
// achando que a biblioteca de verdade supriria o mesmo símbolo -- só que
// essa interface é INTERNA da biblioteca (não fica exposta pro classpath
// de compilação do app), e por isso o build quebrou de vez com
// "cannot find symbol: class Interpolator" (erro visto em
// BaseActivity$$ExternalSyntheticLambda0.java e
// RealmController$$ExternalSyntheticLambda0.java). Solução definitiva:
// ter nossa própria cópia aqui, no pacote do app, sem depender de nada da
// biblioteca -- só precisa ter o mesmo método (mesma "forma" de SAM
// interface) que essas duas classes já implementam.
public interface Interpolator {
    float getInterpolation(float value);
}
