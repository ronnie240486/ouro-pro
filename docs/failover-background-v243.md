# Failover em segundo plano — v243

## Objetivo

A versão 243 mantém o contrato de failover já validado na versão 241 e acrescenta um caminho específico para a tela de canais. Quando o painel informa que a playlist M3U ativa falhou ou que a playlist principal foi restaurada, a tela LiveActivity ou LiveMobileActivity inicia uma sincronização transparente da nova URL. A tela Change Playlist continua disponível como fallback para fluxos que não são telas Live, para URLs Xtream ou quando o launcher da atividade não estiver disponível.

## Fluxo implementado

1. O gerenciador consulta o estado do painel e obtém a configuração atualizada da playlist reserva ou principal.
2. Em uma tela de canais TV ou mobile, uma atividade transparente executa o pipeline M3U existente, sem apresentar uma tela intermediária ao usuário.
3. O pipeline repopula o Realm e as categorias da playlist selecionada por meio das rotinas já existentes de carregamento.
4. Ao concluir, a atividade auxiliar retorna `background_sync=true`, `go_to_channel=true` e o `target_stream_id` do canal que estava em reprodução.
5. A tela de canais localiza novamente esse stream_id em todas as categorias da playlist recém-carregada, atualiza a posição visual, atualiza os adapters e chama `playSelectedChannel`.
6. Se a URL for Xtream ou se a execução direta não puder ser usada, o caminho legado permanece ativo.

## Proteções

A atividade auxiliar limpa os flags estáticos `busy` de `BaseActivity` e `BaseTVActivity` antes de iniciar um novo reload e novamente ao finalizar. Resultado inválido ou falha de download encerra silenciosamente a atividade e não apaga a playlist válida anterior. O identificador `target_stream_id` é removido do Intent depois de ser consumido durante uma recriação da tela.

## Build e verificação

O APK entregue foi gerado a partir do projeto reconstruído com Apktool 2.10.0, alinhado e assinado com o mesmo certificado debug do v241 para permitir atualização sobre a versão estável. Os metadados verificados são:

| Campo | Valor |
|---|---|
| Application ID | `com.ouropro.player.debug` |
| Version code | `243` |
| Version name | `6.4` |
| Min SDK | `21` |
| Target SDK | `34` |
| Assinatura | Android Debug, SHA-256 `b719c38ce127914645a3674a12ea4a51c01fe4536a5d4bc2a0db660c5a35934a` |
| SHA-256 do APK v243 | `8319b642793c138cb01424a2133d19382503d5523caec88fe2f84dc4a0bb8f64` |

A validação estática confirmou que o arquivo é um ZIP íntegro, que a assinatura v1/v2/v3 é válida, que o Manifest registra `BackgroundPlaylistSyncActivity` e que o projeto Java do repositório compila com `./gradlew :app:assembleDebug`.

## Roteiro de teste manual

Instale o v243 sobre o v241, sem limpar os dados do aplicativo. Em uma lista M3U com pelo menos duas playlists, abra um canal da Lista 1 e derrube a Lista 1 no painel. A mensagem de failover deve aparecer, a tela Change Playlist não deve ser aberta e o mesmo canal deve voltar a tocar após o carregamento da Lista 2. Em seguida, restaure a Lista 1 no painel. A sincronização deve ocorrer na própria tela, o canal deve ser localizado novamente pelo `stream_id` e a reprodução deve retornar à lista principal.

Repita o mesmo roteiro no mobile. Também verifique que uma URL Xtream continua seguindo o fluxo legado e que, se o canal não existir na playlist reserva, o aplicativo não entra em loop nem apaga o conteúdo válido já carregado.

## Rollback

O APK estável anterior permanece preservado em `artifacts/stable/OuroPro6.4-v241-STABLE.apk`. O v241 deve ser usado para rollback caso o teste manual revele incompatibilidade específica do dispositivo ou da playlist.
