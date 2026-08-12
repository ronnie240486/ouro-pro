# APK atual com comando de voz

## Resultado

O módulo `app` do projeto OuroPro foi compilado com sucesso usando JDK 17, Android SDK 35 e Gradle Wrapper 8.10.2. O APK atual desta rodada é `artifacts/OuroPro6.4-voice-media-debug.apk`.

O artefato é o **aplicativo atual reconstruído**, não a prévia independente criada anteriormente por engano. O comando de voz está integrado às telas de canais ao vivo, filmes e séries, usando os catálogos já carregados e as mesmas rotinas visuais de abertura.

## Identificação

| Campo | Valor |
|---|---|
| Arquivo | `artifacts/OuroPro6.4-voice-media-debug.apk` |
| Pacote de debug | `com.ouropro.player.debug` |
| Versão | `6.4` |
| Version code | `128` |
| Min SDK | `21` |
| Target SDK | `34` |
| Compile SDK | `35` |
| Assinatura | Debug, esquemas v1 e v2 verificados |
| SHA-256 | `dcaebe185d4472e5bbc04e77269e33a7a270563250ad03b286c28bf54689a22c` |

## Comando de voz

Na tela **Canais ao Vivo**, toque no botão **Voz** e diga, por exemplo, “abrir Space HD”, “assistir canal notícias” ou “pesquisar canal esportes”. A correspondência normaliza maiúsculas, acentos e espaços e recusa empates para evitar abrir o item errado.

Na tela **Filmes**, toque em **Voz** e diga “abrir filme Titanic” ou “pesquisar filme ação”. O título é procurado na lista carregada e, quando há uma correspondência única, o app chama a mesma rotina do clique do filme, preservando a verificação de conteúdo adulto e a tela `MovieInfoActivity`.

Na tela **Séries**, toque em **Voz** e diga “abrir série The Last of Us” ou “pesquisar série policial”. O título é resolvido no catálogo carregado e a abertura usa a mesma rotina do clique da série, encaminhando para `SeriesInfoActivity` ou `SeasonActivity` quando a categoria for de retomada.

Os comandos gerais “abrir filmes”, “abrir séries”, “abrir canais”, “abrir configurações”, “pausar”, “continuar”, “próximo canal” e “canal anterior” continuam disponíveis conforme a tela e o player. O microfone é solicitado somente após o toque do usuário; o áudio não é gravado em disco.

## Erros corrigidos

Os dois NPEs iniciais foram causados por um `R.java` manual recuperado do APK, com IDs antigos diferentes dos IDs gerados pelos recursos atuais. O arquivo manual foi removido e o Gradle foi configurado com `android.nonFinalResIds=false`, fazendo o Android Gradle Plugin gerar os IDs finais usados pelos layouts. Isso corrigiu os acessos nulos a `image_loader`, `btn_reload` e `btn_cancel`.

O erro de configurações causado por `Double.parseDouble("6.4-reconstructed-debug")` foi corrigido restaurando `versionName '6.4'` e removendo o sufixo de versão textual do build de debug. O pacote continua distinto por meio de `applicationIdSuffix '.debug'`.

O erro `NoSuchMethodError` do `ComponentDialog$$ExternalSyntheticLambda0` foi corrigido removendo o wrapper sintético inválido. Os cinco timers de players mobile agora usam `Runnable` direto ligado aos callbacks reais de cada Activity, restaurando a ocultação automática dos controles sem chamar métodos inexistentes do AndroidX.

## Verificações realizadas

A sequência `:app:testDebugUnitTest :app:assembleDebug` retornou `BUILD SUCCESSFUL`. Os nove testes unitários passaram, incluindo parser de canais, parser de filmes, parser de séries e o contrato de playlist. O APK contém `OPEN_MOVIE_ITEM`, `OPEN_SERIES_ITEM`, `VoiceCommandController` e `VoiceMediaMatcher` no multidex; a classe sintética inválida não está empacotada. A assinatura v1/v2 foi verificada.

O APK ainda deve ser testado no mesmo dispositivo que produziu os logs. Não há ADB nem dispositivo Android conectado nesta sandbox, portanto a validação de reprodução, microfone e navegação em catálogo real ainda depende do teste no aparelho.

## Limitação de engenharia reversa

Alguns componentes recuperados do APK original foram adaptados para APIs atuais durante a compilação. O parser legado de trailer foi mantido como compatibilidade mínima, porque a implementação descompilada continha variáveis de exceção inválidas; a reprodução principal continua baseada no ExoPlayer recuperado. Essas adaptações justificam a validação incremental em aparelho antes de uma distribuição ampla.
