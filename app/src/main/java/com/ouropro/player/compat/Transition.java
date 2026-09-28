package com.ouropro.player.compat;

// BUG corrigido: esse arquivo existia como
// app/src/main/java/androidx/constraintlayout/core/state/Transition.java --
// ou seja, com o MESMO pacote/nome da classe "Transition" de verdade da
// biblioteca androidx.constraintlayout:constraintlayout:2.2.0 (dependência
// real, declarada no build.gradle). Isso NÃO é uma cópia da biblioteca --
// são métodos estáticos sintéticos (lambda$getInterpolator$N) que sobraram
// da decompilação do app original e são usados de verdade em dois lugares
// do app (RealmController$$ExternalSyntheticLambda0 e
// BaseActivity$$ExternalSyntheticLambda0). Só que, por estar com o mesmo
// nome canônico da classe real da biblioteca, todo build que faz o merge
// completo do dex (bundleRelease, exigido pra gerar o .aab da Play Store)
// falhava com "duplicate class ... androidx.constraintlayout.core.state
// .Interpolator/Transition is defined multiple times". O build de debug
// (assembleDebug) não fazia esse merge completo, por isso nunca dava erro
// ali -- só apareceu agora que o job de build do .aab de release foi criado.
// Correção: mover essa classe pra um pacote só do app (aqui), sem tocar em
// nenhuma lógica -- só ajustar o "import" nos 2 arquivos que a usam.
public final class Transition {
    private Transition() { }
    public static float lambda$getInterpolator$0(String ignored, float value) { return value; }
    public static float lambda$getInterpolator$1(float value) { return value; }
    public static float lambda$getInterpolator$2(float value) { return value; }
    public static float lambda$getInterpolator$3(float value) { return value; }
    public static float lambda$getInterpolator$4(float value) { return value; }
    public static float lambda$getInterpolator$5(float value) { return value; }
    public static float lambda$getInterpolator$6(float value) { return value; }
    public static float lambda$getInterpolator$7(float value) { return value; }
}
