# Recuperação de séries e voz por título

## Séries vazias

A tela de séries agora detecta quando o catálogo local está vazio ou quando as categorias persistidas não correspondem aos registros disponíveis. Nessa situação, ela consulta novamente `get_series`, grava apenas uma resposta HTTP bem-sucedida e não vazia usando `insertOrUpdate`, reconstrói categorias e atualiza o adapter. Respostas vazias ou falhas preservam o cache existente e não apagam o banco.

Essa recuperação não inventa conteúdo: ela depende de a conta autorizada e o servidor retornarem séries. Se o servidor também retornar uma lista vazia, o aplicativo informa a condição sem apagar dados.

## Voz

O parser aceita `Space HD`, `abrir Space HD`, `assistir Space HD`, `abrir canal Space HD` e títulos sem prefixo, como `Esqueceram de Mim`. A tela principal pode resolver o título entre canal, filme e série. A tela mobile de canais também consulta o catálogo global quando o canal não está na categoria atualmente aberta.

As consultas globais de canais e filmes foram corrigidas para manter o filtro positivo pelo nome. A implementação anterior aplicava uma negação ao nome no modo M3U, o que fazia o conteúdo correto não ser retornado ao matcher.

## Validação

O build executou `:app:testDebugUnitTest` e `:app:assembleDebug` com sucesso. Foram adicionados casos unitários para `Space HD`, `abrir Space HD` e `Esqueceram de Mim`. A assinatura do APK foi verificada nos esquemas v1 e v2.

| Campo | Valor |
|---|---|
| APK | `OuroPro6.4-series-voice-recovery-debug.apk` |
| Pacote | `com.ouropro.player.debug` |
| SHA-256 | `a6eb06fad12aa3f13b5e1633df76b160a721262bc27851e5b3e1eecc34a0ff63` |
| Build | `BUILD SUCCESSFUL` |
| Assinatura | Debug v1/v2 verificada |

> A validação de rede e reprodução depende do servidor autorizado e do catálogo da conta. O ambiente de build não possui a sua sessão nem um aparelho Android conectado para medir o tempo real de resposta ou confirmar a fala em microfone físico.
