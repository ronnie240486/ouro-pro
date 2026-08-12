# Comandos de voz do OuroPro

A primeira versão do controle de voz funciona na tela de canais ao vivo. O usuário toca no botão **Voz**, concede a permissão de microfone quando solicitada e pronuncia uma frase curta em português do Brasil. A aplicação envia a frase ao reconhecedor do Android, normaliza acentos e resolve a ação localmente.

| Frase de exemplo | Ação |
|---|---|
| “Abrir canal São Paulo” | Procura um único canal na categoria atual e abre o player |
| “Assistir canal notícias” | Procura e abre o canal correspondente |
| “Pesquisar canal esportes” | Filtra a lista de canais pela expressão falada |
| “Próximo canal” / “Canal anterior” | Usa a navegação existente do player |
| “Pausar” / “Continuar” | Pausa ou retoma o ExoPlayer atual |
| “Abrir filmes” | Abre a tela de filmes existente |
| “Abrir séries” | Abre a tela de séries existente |
| “Abrir configurações” | Abre as configurações existentes |

A resolução recusa empates e não abre um canal quando o nome falado não identifica uma correspondência única. Canais protegidos continuam passando pelo diálogo de controle parental já existente. O botão não inicia escuta automaticamente, a permissão é solicitada em tempo de execução e a escuta é interrompida quando a Activity pausa ou é destruída. Nenhum áudio é persistido pelo recurso.

## Limite atual

A integração inicial foi adicionada à `LiveActivity`, onde a lista de canais e a rotina de reprodução já estão disponíveis. O próximo passo é colocar o mesmo acionador na Home, criar suporte equivalente para a tela mobile e permitir confirmação visual quando houver mais de uma correspondência possível.
