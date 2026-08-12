# APK atual com comando de voz

## Resultado

O módulo `app` do projeto OuroPro foi compilado com sucesso usando JDK 17, Android SDK 35 e Gradle Wrapper 8.10.2. Após os crashes reportados na inicialização, foi gerado o APK final de correção `artifacts/OuroPro6.4-resource-id-fix-debug.apk`.

O artefato é o **aplicativo atual reconstruído**, não a prévia independente que foi criada anteriormente por engano. O código do comando de voz está no mesmo módulo `app` e é integrado à `LiveActivity`, preservando a rotina existente de busca, controle parental e reprodução.

## Identificação

| Campo | Valor |
|---|---|
| Arquivo | `artifacts/OuroPro6.4-resource-id-fix-debug.apk` |
| Pacote de debug | `com.ouropro.player.debug` |
| Versão | `6.4-reconstructed-debug` |
| Version code | `128` |
| Min SDK | `21` |
| Target SDK | `34` |
| Compile SDK | `35` |
| Assinatura | Debug, esquemas v1 e v2 verificados |
| SHA-256 | `4752fc8f7903f07df7b3ef04dd864eacfdfbf9c42c1935aa2ef576cb93efa54a` |

## Comando de voz

A funcionalidade inclui `VoiceCommand`, `VoiceChannelMatcher` e `VoiceCommandController` no multidex do APK. O usuário toca no botão de voz na tela de canais ao vivo, concede `RECORD_AUDIO` quando solicitado e pode dizer frases como “abrir canal notícias”, “assistir canal 12”, “pesquisar canal esportes”, “próximo canal”, “canal anterior”, “pausar”, “continuar”, “abrir filmes”, “abrir séries” e “abrir configurações”.

O matcher recusa correspondências ambíguas, e a abertura de canal reutiliza a rotina existente da Activity. A escuta é iniciada somente por ação explícita do usuário, é interrompida nos ciclos de pausa/destruição da tela e não grava o áudio em disco.

## Crash corrigido

Os logs mostraram dois `NullPointerException` consecutivos em views de inicialização: `image_loader` nulo em `MainActivity.showDescriptionDlgFragment` e `btn_reload` nulo em `DescriptionDlgFragment.initView`. A causa estrutural foi confirmada comparando o APK: o `R.java` recuperado usava IDs antigos, por exemplo `btn_reload=0x7f0b0099`, enquanto o APK empacotava `btn_reload=0x7f0b009c`. A correção remove o `R.java` manual, ativa IDs finais gerados pelo Android Gradle Plugin e mantém guards defensivos no loader. Dessa forma, o código passa a consultar os IDs atuais dos layouts, corrigindo a origem dos NPEs de views.

## Verificações realizadas

A tarefa `:app:clean :app:assembleDebug` do APK final retornou `BUILD SUCCESSFUL`. Os IDs empacotados foram conferidos e a assinatura v1/v2 foi verificada. Os APKs anteriores não devem mais ser usados. A versão final ainda deve ser testada no mesmo dispositivo que produziu os logs; nesta sandbox não há ADB nem dispositivo Android conectado, portanto não vou afirmar que a execução física foi validada até receber confirmação de abertura.

A assinatura é de depuração. O APK foi compilado e inspecionado, mas ainda não foi validado em um dispositivo físico ou em um emulador Android TV nesta sandbox. Também não foi feita uma nova autenticação do usuário no fluxo Xtream nem um teste de reprodução contra uma playlist real.

## Limitação de engenharia reversa

Alguns componentes recuperados do APK original foram adaptados para APIs atuais durante a compilação. O parser legado de trailer foi mantido como compatibilidade mínima, porque a implementação descompilada continha variáveis de exceção inválidas; a reprodução principal continua baseada no ExoPlayer recuperado. Essas adaptações não alteram o objetivo da entrega de voz em canais ao vivo, mas justificam a validação em aparelho antes de uma distribuição ampla.
