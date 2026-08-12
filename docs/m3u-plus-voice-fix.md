# Correção M3U Plus, cards e comando de voz

## Diagnóstico da lista

A análise local do arquivo M3U Plus mostrou 329.202 entradas e 121 grupos. O grupo `FILMES E SERIES` contém canais lineares como Space HD, A&E e TNT; ele não pode ser tratado como grupo de séries. Os grupos de catálogo incluem séries com padrões `S01E01`, `1x02` e nomes de provedores como Netflix, HBO Max, Amazon Prime Video, Disney+, Star+, Paramount+, Apple TV+, Globo Play, ReelShort e 24H.

A classificação anterior considerava qualquer grupo contendo a palavra `series` como série. Isso misturava canais do grupo `FILMES E SERIES` e deixava de reconhecer grupos de provedores. A regra nova dá prioridade a URL e marcador de episódio, exclui grupos mistos/filmes quando não há marcador e reconhece os grupos de provedores observados na lista real.

## Voz

A Home agora resolve correspondências exatas e abre diretamente o detalhe do conteúdo, em vez de navegar apenas para a tela de busca. As frases esperadas incluem “Space HD”, “canal Space HD”, “De Volta para Futuro”, “filme De Volta para o Futuro”, “The Walking Dead” e “série The Walking Dead”. A busca ignora artigos e preposições comuns, então variações naturais da fala continuam encontrando o mesmo título. Correspondências exatas têm prioridade mesmo quando existem cópias em categorias diferentes.

## Cards e dados

Os cards de séries usam exatamente o mesmo `item_vod`/`item_vod_grid` do catálogo de filmes. O adapter usa diretamente `SeriesModel.stream_icon`, como no APK original, sem substituir a capa pelo logo de um episódio diferente. O agrupamento usa o `series_name` exato do APK original, preserva temporada/capítulo e a migração M3U foi incrementada para reconstruir os registros existentes uma vez. A classificação foi alinhada ao APK original: somente URL de série ou marcador real de episódio transforma uma entrada em série; canais HBO, Space HD e HBO Family permanecem em Canais.

| Item | Valor |
|---|---|
| APK | `OuroPro6.4-account-expiry-original-flow-debug.apk` |
| Pacote | `com.ouropro.player.debug` |
| Build | `BUILD SUCCESSFUL` |
| Testes | Testes de parser M3U, voz e projeto aprovados |
| Assinatura | Debug v1/v2 verificada |
| SHA-256 | `f0405d1902c18e6eeff4a5bbf576c993235be64b09f71338beb95e7022eca42f` |

> A URL da lista e quaisquer credenciais foram deliberadamente excluídas deste documento e do repositório. Como a credencial foi compartilhada em texto aberto durante o diagnóstico, a senha deve ser trocada no provedor. Para listas M3U Plus com credenciais na URL, o app consulta o `player_api.php` correspondente em segundo plano e salva `LoginModel.exp_date` sem bloquear o catálogo. Para a conta analisada, a resposta sanitizada retornou expiração em `27/08/2026`; se o servidor não fornecer esse campo, a Home exibe uma mensagem explícita em vez de `Undefined`.
