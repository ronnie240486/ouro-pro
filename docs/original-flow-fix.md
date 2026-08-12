# Correção baseada no fluxo do APK original

## Comparação

O APK original foi decompilado novamente em uma área isolada. O contrato original usa `get_series` como rota principal, `get_second_series` como fallback e `get_series_categories` para categorias. O fluxo original não tinha a camada de recuperação global parcial adicionada nas versões intermediárias.

A reconstrução passou a preservar o caminho global do original quando ele retorna pelo menos 100 séries. Se a resposta vier parcial, a nova camada consulta as categorias reais e busca cada `category_id`, agregando e deduplicando os registros. Uma resposta parcial não é gravada como catálogo completo.

## Correção da abertura

O `MainActivity` agora abre a Home imediatamente quando encontra qualquer catálogo local — filmes, canais ou séries — sem depender de URL, usuário ou flags antigas de migração. O caminho remoto continua disponível para primeiro login ou quando o banco local está realmente vazio. A gravação das séries consolidadas ocorre em uma instância Realm própria numa thread de segundo plano.

## Limitações honestas

O APK original e a reconstrução não podem ser comparados contra o servidor da conta dentro desta sandbox, e não há dispositivo Android conectado para medir o tempo de abertura ou a quantidade final de séries. A versão não promete 9.000 itens sem receber essa quantidade do servidor; ela evita aceitar quatro itens como catálogo completo e informa falha quando o servidor não fornece categorias/respostas suficientes.

| Campo | Valor |
|---|---|
| APK | `OuroPro6.4-original-flow-fix-debug.apk` |
| Pacote | `com.ouropro.player.debug` |
| SHA-256 | `0dbaae4b112e1d86a7eb2eada966fad3c8a1048427007be677f8bd1a0fce4ea8` |
| Build | `BUILD SUCCESSFUL` |
| Assinatura | Debug v1/v2 verificada |
