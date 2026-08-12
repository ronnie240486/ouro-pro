# APK atual com comando de voz

## Resultado

O módulo `app` do projeto OuroPro foi compilado com sucesso usando JDK 17, Android SDK 35 e Gradle Wrapper 8.10.2. Após um crash reportado na inicialização, foi gerado o APK de correção `artifacts/OuroPro6.4-voz-crashfix-debug.apk`.

O artefato é o **aplicativo atual reconstruído**, não a prévia independente que foi criada anteriormente por engano. O código do comando de voz está no mesmo módulo `app` e é integrado à `LiveActivity`, preservando a rotina existente de busca, controle parental e reprodução.

## Identificação

| Campo | Valor |
|---|---|
| Arquivo | `artifacts/OuroPro6.4-voz-crashfix-debug.apk` |
| Pacote de debug | `com.ouropro.player.debug` |
| Versão | `6.4-reconstructed-debug` |
| Version code | `128` |
| Min SDK | `21` |
| Target SDK | `34` |
| Compile SDK | `35` |
| Assinatura | Debug, esquemas v1 e v2 verificados |
| SHA-256 | `49b9e20d2ca3aa314b8047e0f16ecbc3acf146850ccb3720a24a18925f97b977` |

## Comando de voz

A funcionalidade inclui `VoiceCommand`, `VoiceChannelMatcher` e `VoiceCommandController` no multidex do APK. O usuário toca no botão de voz na tela de canais ao vivo, concede `RECORD_AUDIO` quando solicitado e pode dizer frases como “abrir canal notícias”, “assistir canal 12”, “pesquisar canal esportes”, “próximo canal”, “canal anterior”, “pausar”, “continuar”, “abrir filmes”, “abrir séries” e “abrir configurações”.

O matcher recusa correspondências ambíguas, e a abertura de canal reutiliza a rotina existente da Activity. A escuta é iniciada somente por ação explícita do usuário, é interrompida nos ciclos de pausa/destruição da tela e não grava o áudio em disco.

## Crash corrigido

O log fornecido mostrou `NullPointerException` em `MainActivity.showDescriptionDlgFragment`, na chamada `GifImageView.setVisibility`, porque `image_loader` estava nulo. A correção criou `setLoaderVisibility` e `isLoaderVisible`, que tratam o GIF como componente opcional quando uma variante de layout não o fornece. Os acessos em carregamento, diálogo de descrição, tecla voltar e falha da playlist agora passam por essa proteção.

## Verificações realizadas

A tarefa `:app:assembleDebug` do APK de correção retornou `BUILD SUCCESSFUL`. O APK anterior não deve mais ser usado. A versão de correção deve ser testada no mesmo dispositivo que produziu o log; nesta sandbox não há um dispositivo Android conectado, portanto não vou afirmar que a execução física foi validada até receber um novo log ou confirmação de abertura.

A assinatura é de depuração. O APK foi compilado e inspecionado, mas ainda não foi validado em um dispositivo físico ou em um emulador Android TV nesta sandbox. Também não foi feita uma nova autenticação do usuário no fluxo Xtream nem um teste de reprodução contra uma playlist real.

## Limitação de engenharia reversa

Alguns componentes recuperados do APK original foram adaptados para APIs atuais durante a compilação. O parser legado de trailer foi mantido como compatibilidade mínima, porque a implementação descompilada continha variáveis de exceção inválidas; a reprodução principal continua baseada no ExoPlayer recuperado. Essas adaptações não alteram o objetivo da entrega de voz em canais ao vivo, mas justificam a validação em aparelho antes de uma distribuição ampla.
