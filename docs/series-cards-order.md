# Correção de cards, temporadas e capítulos M3U

## Causa

A importação M3U já recuperava episódios, mas a temporada era criada usando o título inteiro do episódio. Assim, `S01E01`, `S01E02` e `S01E03` podiam virar temporadas diferentes. Além disso, alguns `SeriesModel` antigos não tinham `stream_icon`, embora o primeiro episódio tivesse logo/capa.

## Correção

O parser agora grava separadamente o nome-base da série, o número da temporada e o número do episódio. `S01E02`, `1x02` e `Temporada 1 Episódio 2` passam a compartilhar a temporada `S01`. As temporadas são ordenadas numericamente e os episódios são ordenados pelo número real do capítulo, com fallback alfabético para conteúdos sem numeração.

Os cards de séries usam `SeriesModel.stream_icon` e, quando ele estiver vazio, usam a capa do primeiro episódio M3U da mesma série. O carrossel da Home usa o mesmo fallback. A migração do parser é versionada; cada instalação executa a reconstrução uma vez, sem repetir a operação em toda abertura.

A gravação do catálogo é feita em transação Realm assíncrona e o agrupamento de milhares de episódios acontece em thread separada. A tela inicial não precisa aguardar essa operação. O ícone de microfone e o comando de voz não foram removidos.

| Campo | Valor |
|---|---|
| APK | `OuroPro6.4-series-cards-order-debug.apk` |
| Pacote | `com.ouropro.player.debug` |
| Build | `BUILD SUCCESSFUL` |
| Testes | Parser M3U, temporadas e testes existentes aprovados |
| Assinatura | Debug v1/v2 verificada |
| SHA-256 | `a430304045d311e2ab7b0b43f4626df0fd4fcd9794cca00144a1180087ee31dc` |

> A instalação deve ser feita por cima da versão debug atual, sem limpar os dados. A migração reprocessará a M3U uma vez; depois, o aplicativo reutilizará o catálogo organizado.
