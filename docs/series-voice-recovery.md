# Recuperação de séries e voz por título

## Séries vazias

A tela de séries agora detecta quando o catálogo local está vazio ou quando as categorias persistidas não correspondem aos registros disponíveis. Nessa situação, ela consulta `get_series` e, quando a resposta tem menos de 100 itens — como o caso relatado de apenas 2 séries — tenta também `get_second_series`. O aplicativo usa a maior resposta válida, em vez de aceitar uma resposta parcial como catálogo completo.

A gravação usa `insertOrUpdate`, reconstrói categorias e atualiza o adapter. Respostas vazias ou falhas preservam o cache existente e não apagam o banco. Essa recuperação não inventa conteúdo: ela depende de a conta autorizada e o servidor retornarem as séries.

## Abertura rápida

A `MainActivity` agora usa cache-first quando já existe uma conta configurada e catálogo local. A Home abre imediatamente sem aguardar a cadeia inteira de sincronização. Em paralelo, a Home inicia uma atualização assíncrona de séries quando o cache tem menos de 100 itens. Isso evita que a tela de loading permaneça bloqueando a navegação enquanto o servidor responde.

## Voz

O parser aceita `Space HD`, `abrir Space HD`, `assistir Space HD`, `abrir canal Space HD` e títulos sem prefixo, como `Esqueceram de Mim`. A tela principal pode resolver o título entre canal, filme e série. A tela mobile de canais também consulta o catálogo global quando o canal não está na categoria atualmente aberta.

As consultas globais de canais e filmes foram corrigidas para manter o filtro positivo pelo nome. A implementação anterior aplicava uma negação ao nome no modo M3U, o que fazia o conteúdo correto não ser retornado ao matcher.

## Validação

O build executou `:app:testDebugUnitTest` e `:app:assembleDebug` com sucesso. Foram adicionados casos unitários para `Space HD`, `abrir Space HD` e `Esqueceram de Mim`. A assinatura do APK foi verificada nos esquemas v1 e v2.

| Campo | Valor |
|---|---|
| APK | `OuroPro6.4-series-cache-first-debug.apk` |
| Pacote | `com.ouropro.player.debug` |
| SHA-256 | `ff4055cb7c30fc112dc5e4880f253673ed8a5a5b423fa3481f2bfaa04f75da0f` |
| Build | `BUILD SUCCESSFUL` |
| Assinatura | Debug v1/v2 verificada |

> A validação de rede e reprodução depende do servidor autorizado e do catálogo da conta. O ambiente de build não possui a sua sessão nem um aparelho Android conectado para medir o tempo real de resposta ou confirmar a fala em microfone físico.
