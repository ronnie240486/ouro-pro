# OuroPro

Reconstrução versionável do aplicativo Android **OuroPro 6.4**, preparada a partir do APK fornecido pelo proprietário desta tarefa. O projeto preserva a organização funcional observada no pacote original — login Xtream-style, playlists/M3U, canais ao vivo, filmes, séries, EPG, favoritos, histórico e reprodução — e inicia uma camada de melhorias de segurança e manutenção.

> **Estado atual:** reconstrução inicial baseada em engenharia reversa. O APK original não é versionado neste repositório, e a saída do descompilador não é considerada equivalente ao código-fonte original.

## O que foi recuperado

A base contém o pacote principal `com.ouropro.player`, 279 arquivos Java recuperados, recursos Android, assets, bibliotecas nativas por ABI e os namespaces auxiliares que estavam empacotados no APK. A configuração preserva `minSdkVersion 21`, `targetSdkVersion 34`, `versionCode 128` e a versão funcional `6.4` como referência.

| Área | Situação |
|---|---|
| Login e contrato `player_api.php` | Recuperados para revisão |
| Playlists e parser M3U | Recuperados |
| Canais ao vivo, filmes e séries | Recuperados |
| EPG/catch-up, favoritos e histórico | Recuperados |
| Reprodução ExoPlayer/FFmpeg | Recuperada, sujeita à validação em dispositivo |
| Recursos visuais e assets | Copiados para `app/src/main/res` e `app/src/main/assets` |
| Build Android reprodutível | `assembleDebug` validado com Android SDK 35 e JDK 17 |

## Melhorias aplicadas

A camada de rede agora rejeita endpoints sem host válido, evita usuário e senha dentro da URL e exige HTTPS no caminho padrão. O logging HTTP foi desativado em nível de corpo, reduzindo o risco de credenciais aparecerem no logcat. O cliente anteriormente chamado de `UnsafeOkHttpClient` mantém a assinatura por compatibilidade, mas não aceita mais certificados ou hostnames arbitrários.

As preferências passaram a ser abertas por `SecurePreferenceStore`, usando Android Keystore por meio de `EncryptedSharedPreferences`. Isso protege usuário, senha, URL do servidor, MAC, tokens locais, favoritos e histórico em repouso quando o provider criptográfico está disponível. Também foi formalizado o contrato de playlist com os campos `playlist_url` e `playlist_name`, acompanhado de teste unitário para impedir a regressão para `url` e `name`.

O **comando de voz** agora usa um ícone de microfone na tela principal, nos canais mobile/TV, filmes e séries. Ele aceita títulos sem prefixo obrigatório, como “Esqueceram de Mim”, e frases como “abrir Space HD”. O recurso solicita `RECORD_AUDIO` somente quando o usuário toca no microfone, não grava áudio em disco, recusa correspondências ambíguas e preserva o controle parental. O detalhamento está em [`docs/voice-commands.md`](docs/voice-commands.md).

A sincronização de séries foi protegida contra perda de dados. Respostas vazias de `get_series`, `get_second_series` ou episódios não apagam mais registros do Realm. Quando a tela encontra menos de 100 séries, ela consulta as categorias reais e agrega as respostas por `category_id`, em vez de aceitar o conjunto parcial global. Consulte [`docs/series-data-safety.md`](docs/series-data-safety.md), [`docs/series-voice-recovery.md`](docs/series-voice-recovery.md) e [`docs/series-category-cache.md`](docs/series-category-cache.md).

## Estrutura

```text
app/src/main/java/       Código Java recuperado e melhorias novas
app/src/main/res/        Recursos Android e layouts
app/src/main/assets/     Assets estáticos do APK
app/src/main/jniLibs/    Bibliotecas nativas por ABI
app/src/test/            Testes unitários
analysis/                Identificação e hash do APK analisado
docs/                    Relatório de engenharia reversa
improvements/            Backlog de melhorias planejadas
```

## Build

É necessário instalar o Android SDK com as plataformas correspondentes ao `compileSdk 35`, além de JDK 17 e Gradle 8.6 ou superior. Na raiz do projeto, execute `./gradlew test` para os testes unitários e `./gradlew :app:assembleDebug` para gerar o APK completo de depuração. A compilação foi validada com sucesso nesta entrega.

O instalador desta correção é `artifacts/OuroPro6.4-series-category-cache-debug.apk`. Ele mantém o pacote funcional do aplicativo atual, `com.ouropro.player`, com o sufixo de debug `com.ouropro.player.debug`, usa `versionName=6.4`, abre a Home a partir do cache local e carrega séries por categoria em segundo plano quando o catálogo possui menos de 100 itens. As versões anteriores não devem ser usadas. A assinatura é de debug e serve para teste, não para distribuição comercial.

O projeto deliberadamente não inclui o APK original, chaves de assinatura, credenciais, dados de playlists ou endpoints privados. Para distribuir uma versão, crie uma chave de assinatura própria e configure os segredos fora do Git.

## Próximas melhorias recomendadas

O próximo ciclo deve substituir a persistência baseada em JSON dentro de preferências por Room ou Realm gerenciado, adicionar migração automática de preferências antigas, separar o domínio Xtream/M3U das Activities, implementar retries com backoff e cache controlado, incluir testes de parsing M3U e autenticação, e validar reprodução em Android TV e dispositivos móveis. Também é recomendável criar uma configuração remota autenticada para nome, logo, fundo, DNS e categorias, com no máximo cinco DNSs e sem permitir alteração silenciosa de endpoints.

## Uso responsável

O código deve ser usado somente com servidores, playlists, imagens e conteúdos para os quais o usuário tenha autorização. Este projeto não implementa, recomenda ou documenta bypass de autenticação, DRM, licenciamento, paywall ou controle de acesso.
