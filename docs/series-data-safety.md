# Correção de segurança da sincronização de séries

## Diagnóstico

A ausência de séries foi causada por três transações destrutivas recuperadas do código descompilado. O callback de `get_series` apagava todos os `SeriesModel` antes de inserir a resposta da API. O callback secundário de `get_second_series` repetia o mesmo padrão. Além disso, a sincronização de episódios apagava os `SeriesModel` antes de reconstruí-los a partir dos episódios; quando a API retornava uma lista vazia, a reconstrução resultava em zero séries e também substituía as categorias por uma lista sem conteúdo real.

Esse comportamento não é seguro para uma resposta vazia, uma falha parcial ou uma indisponibilidade temporária do servidor. O aplicativo agora preserva os registros existentes nesses casos.

## Correção aplicada

As respostas de séries passaram a usar `insertOrUpdate` somente quando a coleção recebida é não vazia. As transações de episódios não apagam mais `SeriesModel`. A reconstrução de categorias é interrompida quando a lista de episódios está vazia, preservando as categorias e os registros já carregados. A mesma proteção foi aplicada ao fluxo mobile e ao fluxo Android TV.

A correção também mantém o microfone na tela principal, nos canais mobile/TV, filmes e séries. Títulos falados sem prefixo, como “Esqueceram de Mim”, são tratados como busca global; “Space HD” é resolvido na lista de canais carregada.

## Validação

O projeto passou em `:app:testDebugUnitTest` e `:app:assembleDebug`. A auditoria estática não encontrou mais `deleteAllFromRealm()` aplicado a `SeriesModel` em `BaseActivity` ou `BaseTVActivity`. O APK foi verificado com assinatura debug v1 e v2.

| Campo | Valor |
|---|---|
| APK | `OuroPro6.4-series-safe-mic-debug.apk` |
| Pacote | `com.ouropro.player.debug` |
| SHA-256 | `f2f3cfa056e3f5a6ecefea9946d6dd0b4c1e2398e9bd8adf875a9c21d39837ff` |
| Build | `BUILD SUCCESSFUL` |
| Testes unitários | Aprovados |
| Assinatura | Debug v1/v2 verificada |

> O APK não recupera automaticamente dados que já tenham sido apagados por uma versão anterior. Ele impede a nova perda durante a sincronização. Se os registros já foram removidos do banco local, será necessário sincronizar novamente a conta/lista a partir do servidor ou restaurar um backup local.
