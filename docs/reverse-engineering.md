# Relatório de inspeção do APK OuroPro 6.4

## Identificação

| Campo | Resultado |
|---|---|
| Arquivo analisado | `OuroPro6.4.apk` |
| SHA-256 | Ver `sha256.txt` |
| Framework aparente | Android nativo em Java/Kotlin, não Flutter |
| Pacote principal | `com.ouropro.player` |
| DEX | `classes.dex` e `classes2.dex` |
| Recursos | `res/`, `assets/`, bibliotecas nativas por ABI |
| Saída decompilada | `decompiled-apk/` e `jadx-sources/` |

## Funcionalidades observadas

A estrutura recuperada contém telas e modelos para autenticação, gerenciamento de playlist, canais ao vivo, VOD/filmes, séries/temporadas/episódios, EPG/catch-up, favoritos, histórico/resume, pesquisa, configurações, legendas, transmissão para dispositivos e reprodução de vídeo. O contrato de rede usa endpoints Xtream-style `player_api.php`, `xmltv.php` e consultas de categorias/streams.

## Bibliotecas e componentes relevantes

Foram identificados Retrofit/OkHttp/Gson, ExoPlayer, FFmpeg JNI, Realm, Glide, AndroidX/Material, Leanback para TV, Cronet e `libpl_droidsonroids_gif`. O manifesto declara duas entradas de lançamento: `MainActivity` e `MainTVActivity`, ambas em orientação paisagem.

## Riscos técnicos prioritários

1. `RetroClass` configura `HttpLoggingInterceptor.Level.BODY`, o que pode registrar URLs, parâmetros e credenciais.
2. `UnsafeOkHttpClient` aceita certificados e nomes de host sem validação; isso enfraquece TLS e permite interceptação.
3. `PreferenceHelper` armazena usuário, senha, modelo de login, URL do servidor e MAC em `SharedPreferences` sem cifragem.
4. Parte da lógica recuperada pelo JADX possui erros de descompilação; a saída não deve ser tratada como equivalente ao código-fonte original.
5. O APK não contém arquivos Gradle nem o código-fonte original; a reconstrução exige um projeto Android novo e validação funcional em dispositivo/emulador.

## Limitações

A engenharia reversa recupera bytecode, recursos e nomes, mas não recupera comentários, histórico, nomes removidos por obfuscação ou decisões de arquitetura. Não serão incluídos mecanismos para burlar autenticação, licenciamento, DRM ou controles de acesso. As melhorias devem operar somente com servidores, playlists e conteúdo autorizados pelo usuário.
