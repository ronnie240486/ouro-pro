# Correção da importação M3U de séries

## Diferença entre filmes e séries

O fluxo M3U de filmes cria cada `MovieModel` diretamente a partir de `M3UItem`: copia `group-title` para `category_name`, o nome do item para `name`, a URL para `url` e o logo para `stream_icon`. Por isso o catálogo de filmes aparece mesmo quando a lista é M3U.

O fluxo de séries era indireto: cada item passava por `EpisodeModel.fromM3UItem`, depois `BaseActivity` agrupava os episódios por `series_name` e só então criava um `SeriesModel`. O parser antigo só reconhecia tokens exatamente no formato `S01`. Quando a lista usava `S01E01`, `1x01`, `Temporada 1 Episódio 1`, grupo de séries ou pequenas variações, `series_name` ficava vazio e as categorias não eram formadas corretamente.

## Correção

A nova regra compartilhada reconhece séries por URL, grupo M3U e marcadores comuns de temporada/episódio. Ela extrai o nome base da série antes do marcador, preserva `group-title` como categoria e grava o episódio para que o agrupamento gere um `SeriesModel` por série. A classificação foi aplicada ao fluxo mobile e Android TV. Quando a fonte é M3U e o Realm possui menos de 100 séries, o aplicativo dispara a reimportação dos episódios ao abrir a Home, sem bloquear a tela; em seguida, a tela de Séries também pode solicitar a reconstrução se ainda houver menos de 100 itens.

A abertura rápida do APK anterior foi preservada, assim como o carregamento direto de filmes. Nenhuma rotina de filmes foi alterada pela correção do parser de séries.

## Validação

Os testes unitários cobrem `S01E01`, `1x03`, `Temporada 1 Episódio 4`, grupos de séries e a rejeição de um filme. O build completo foi concluído com sucesso.

| Campo | Valor |
|---|---|
| APK | `OuroPro6.4-m3u-series-fix-debug.apk` |
| Pacote | `com.ouropro.player.debug` |
| Build | `BUILD SUCCESSFUL` |
| Testes | Aprovados |
| Assinatura | Debug v1/v2 verificada |
| SHA-256 | `8540bd07bad8181a2cd5b20645583b68c6561881d010b401f64a2293f7288b18` |

> A quantidade final depende do conteúdo real da M3U e dos padrões de nomes usados por ela. O aplicativo agora importa itens de séries que o parser antigo descartava; não inventa episódios nem altera o catálogo de filmes.
