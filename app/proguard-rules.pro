# Regras conservadoras para a reconstrução inicial.
# O APK original não traz o mapeamento de obfuscação, portanto a otimização será ativada
# somente depois de testes funcionais em dispositivo.

-keep class com.ouropro.player.models.** { *; }
-keep class com.ouropro.player.remote.** { *; }
-keep class io.realm.** { *; }
-keep class com.google.gson.** { *; }
-keepattributes Signature
-keepattributes *Annotation*

-dontwarn org.apache.http.**
-dontwarn org.chromium.net.**
-dontwarn javax.annotation.**
