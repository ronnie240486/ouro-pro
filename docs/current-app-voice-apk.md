# APK atual com comando de voz

## Resultado

O módulo `app` do projeto OuroPro foi compilado com sucesso usando JDK 17, Android SDK 35 e Gradle Wrapper 8.10.2. O APK de depuração foi gerado em `artifacts/OuroPro6.4-voz-debug.apk`.

O artefato é o **aplicativo atual reconstruído**, não a prévia independente que foi criada anteriormente por engano. O código do comando de voz está no mesmo módulo `app` e é integrado à `LiveActivity`, preservando a rotina existente de busca, controle parental e reprodução.

## Identificação

| Campo | Valor |
|---|---|
| Arquivo | `artifacts/OuroPro6.4-voz-debug.apk` |
| Pacote de debug | `com.ouropro.player.debug` |
| Versão | `6.4-reconstructed-debug` |
| Version code | `128` |
| Min SDK | `21` |
| Target SDK | `34` |
| Compile SDK | `35` |
| Assinatura | Debug, esquemas v1 e v2 verificados |
| SHA-256 | `a0472a204ab1ff4b65a438619a9455071491e2aedc3b17d565d2e2b1e2bd8cda` |

## Comando de voz

A funcionalidade inclui `VoiceCommand`, `VoiceChannelMatcher` e `VoiceCommandController` no multidex do APK. O usuário toca no botão de voz na tela de canais ao vivo, concede `RECORD_AUDIO` quando solicitado e pode dizer frases como “abrir canal notícias”, “assistir canal 12”, “pesquisar canal esportes”, “próximo canal”, “canal anterior”, “pausar”, “continuar”, “abrir filmes”, “abrir séries” e “abrir configurações”.

O matcher recusa correspondências ambíguas, e a abertura de canal reutiliza a rotina existente da Activity. A escuta é iniciada somente por ação explícita do usuário, é interrompida nos ciclos de pausa/destruição da tela e não grava o áudio em disco.

## Verificações realizadas

A tarefa `:app:assembleDebug` retornou `BUILD SUCCESSFUL`. O APK foi verificado com `apksigner`, possui quatro arquivos DEX, contém as classes de voz no `classes2.dex` e declara `android.permission.RECORD_AUDIO` juntamente com as permissões de rede já usadas pelo aplicativo.

A assinatura é de depuração. O APK foi compilado e inspecionado, mas ainda não foi validado em um dispositivo físico ou em um emulador Android TV nesta sandbox. Também não foi feita uma nova autenticação do usuário no fluxo Xtream nem um teste de reprodução contra uma playlist real.

## Limitação de engenharia reversa

Alguns componentes recuperados do APK original foram adaptados para APIs atuais durante a compilação. O parser legado de trailer foi mantido como compatibilidade mínima, porque a implementação descompilada continha variáveis de exceção inválidas; a reprodução principal continua baseada no ExoPlayer recuperado. Essas adaptações não alteram o objetivo da entrega de voz em canais ao vivo, mas justificam a validação em aparelho antes de uma distribuição ampla.
