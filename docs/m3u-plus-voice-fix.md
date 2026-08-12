# Correção M3U Plus, cards e comando de voz

## Diagnóstico da lista

A análise local do arquivo M3U Plus mostrou 329.202 entradas e 121 grupos. O grupo `FILMES E SERIES` contém canais lineares como Space HD, A&E e TNT; ele não pode ser tratado como grupo de séries. Os grupos de catálogo incluem séries com padrões `S01E01`, `1x02` e nomes de provedores como Netflix, HBO Max, Amazon Prime Video, Disney+, Star+, Paramount+, Apple TV+, Globo Play, ReelShort e 24H.

A classificação anterior considerava qualquer grupo contendo a palavra `series` como série. Isso misturava canais do grupo `FILMES E SERIES` e deixava de reconhecer grupos de provedores. A regra nova dá prioridade a URL e marcador de episódio, exclui grupos mistos/filmes quando não há marcador e reconhece os grupos de provedores observados na lista real.

## Voz

A Home agora resolve correspondências exatas e abre diretamente o detalhe do conteúdo, em vez de navegar apenas para a tela de busca. As frases esperadas incluem “Space HD”, “canal Space HD”, “De Volta para Futuro”, “filme De Volta para o Futuro”, “The Walking Dead” e “série The Walking Dead”. A busca ignora artigos e preposições comuns, então variações naturais da fala continuam encontrando o mesmo título. Correspondências exatas têm prioridade mesmo quando existem cópias em categorias diferentes.

## Cards e dados

Os cards de séries usam o mesmo layout compacto de mídia do catálogo de filmes. Quando `SeriesModel.stream_icon` está vazio, o card usa o logo do primeiro episódio da série. O agrupamento preserva temporada e capítulo, e a migração M3U foi incrementada para reprocessar o catálogo existente uma vez.

| Item | Valor |
|---|---|
| APK | `OuroPro6.4-m3u-plus-voice-fix-debug.apk` |
| Pacote | `com.ouropro.player.debug` |
| Build | `BUILD SUCCESSFUL` |
| Testes | Testes de parser M3U, voz e projeto aprovados |
| Assinatura | Debug v1/v2 verificada |
| SHA-256 | `16f883deb59099bd92ec0a0d90225140913ce233d9599d2b5ca047f0db6e8cd3` |

> A URL da lista e quaisquer credenciais foram deliberadamente excluídas deste documento e do repositório. Como a credencial foi compartilhada em texto aberto durante o diagnóstico, a senha deve ser trocada no provedor.
