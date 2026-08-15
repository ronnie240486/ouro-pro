# Correção do catálogo de séries e abertura lenta

## Diagnóstico

A tela mostrada pelo usuário tinha `All (4)` e uma única categoria `Séries | Crunchyroll`. Isso significa que a resposta global de séries usada pelo APK reconstruído era parcial; comparar apenas `get_series` com `get_second_series` não era suficiente porque ambos eram endpoints globais e um deles continha a URL recuperada com `category_id=*`.

## Correção do catálogo

O aplicativo agora consulta primeiro `get_series_categories` para obter os IDs reais das categorias autorizadas. Depois faz chamadas `get_series` com `category_id` explícito, com no máximo quatro requisições simultâneas. As respostas são agregadas e deduplicadas por `series_id`; quando esse campo não existe, é usada a combinação do nome com a categoria. Cada registro também recebe o ID e o nome da categoria antes de ser salvo.

A tela de Séries inicia essa recuperação quando o cache tem menos de 100 itens, incluindo o caso exibido de quatro itens. A Home dispara a mesma recuperação em segundo plano. Uma resposta parcial isolada não substitui mais o catálogo consolidado.

## Correção da lentidão

Quando já existe servidor configurado e qualquer catálogo local válido, a `MainActivity` abre a Home em modo cache-first, sem aguardar a consulta remota de inicialização nem a sincronização completa. A gravação do catálogo consolidado usa uma instância Realm própria em uma thread de segundo plano. A interface só é atualizada depois que a transação termina.

## Validação

O projeto passou em `:app:testDebugUnitTest` e `:app:assembleDebug`. O APK foi verificado com assinatura debug v1 e v2.

| Campo | Valor |
|---|---|
| APK | `OuroPro6.4-series-category-cache-debug.apk` |
| Pacote | `com.ouropro.player.debug` |
| Build | `BUILD SUCCESSFUL` |
| Assinatura | Debug v1/v2 verificada |

> A quantidade final depende dos IDs e das respostas que o servidor autorizado da conta retorna. O ambiente não possui a sessão do usuário nem um aparelho conectado para confirmar a contagem real de aproximadamente 9.000 séries. Se o servidor bloquear chamadas por categoria, o aplicativo informará falha em vez de apresentar quatro itens como catálogo completo.
