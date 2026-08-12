# Achados sanitizados: APK original versus reconstrução

## APK original

O `BaseActivity` original classifica uma entrada M3U como canal por padrão, filme quando a URL contém `movie/`, `=movie`, `movies/`, `vod/` ou `video/`, e série somente quando a URL contém `series/`. O `prepareData` original apenas separa as três listas; a transformação de episódio ocorre depois em `LoadEpisodeCommand`/`EpisodeModel.fromM3UItem`.

O `EpisodeModel` original preserva `category_name`, `title`, `url` e `logoURL`. Para títulos com token `S##`, ele cria `season_name` e `series_name` a partir do texto antes do token. O `SeriesRecyclerAdapter` original usa diretamente `SeriesModel.stream_icon`, `default_bg` e o layout `item_vod`/`item_vod_grid`; ele não usa a capa do primeiro episódio como fallback.

## M3U Plus analisada

A lista baixada para diagnóstico continha 329.202 entradas e 121 grupos. O grupo misto `FILMES E SERIES` contém canais lineares, incluindo Space HD, A&E e TNT, com URLs de stream `.ts`. Grupos de catálogo como `SERIES VARIADAS`, `DORAMAS`, `NOVELAS`, `NETFLIX`, `HBO MAX`, `AMAZON PRIME VIDEO`, `DISNEY+`, `STAR+`, `PARAMOUNT+`, `APPLE TV+`, `GLOBO PLAY`, `REELSHORT` e `24H SERIES` contêm títulos com marcadores `S##E##`.

Uma contagem sanitizada com marcadores e grupos de provedores encontrou cerca de 10.312 nomes-base candidatos a séries; o número de cards depende da regra de agrupamento e da forma como a conta organiza duplicatas, idiomas e versões de um mesmo título.

## Divergência a corrigir

A reconstrução passou a classificar qualquer grupo contendo `series` como série. Isso transforma o grupo misto de canais em séries. Também passou a usar heurísticas de grupo sem preservar totalmente a regra original baseada em URL e o campo `stream_icon` do próprio `SeriesModel`. O próximo ajuste deve restaurar a separação original, manter as séries M3U identificadas por marcador/grupo de catálogo sem incluir o grupo misto de canais e usar o card/capa original.

Nenhuma URL com credenciais foi registrada neste arquivo.
