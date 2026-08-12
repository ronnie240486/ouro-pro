# Backlog priorizado do OuroPro

## Prioridade alta

| Item | Motivo | Critério de aceite |
|---|---|---|
| Migrar dados de catálogo e histórico para banco versionado | Evita limites e corrupção de JSON em preferências | Migração automática, rollback e testes de leitura/escrita |
| Integrar migração de preferências legadas | Evita que usuários percam playlists e configurações | Dados antigos lidos uma vez, cifrados e removidos do armazenamento anterior |
| Cobrir autenticação e parsing M3U com testes | Reduz regressões no fluxo principal | Testes para respostas válidas, expiradas, inválidas e playlists malformadas |
| Implementar retry com backoff e mensagens de erro | Melhora estabilidade em redes móveis e servidores lentos | Sem loops infinitos, timeout explícito e feedback acionável |
| Validar reprodução em Android TV e mobile | O APK declara duas entradas e orientação paisagem | Smoke tests em pelo menos um dispositivo de cada perfil |

## Prioridade média

| Item | Motivo | Critério de aceite |
|---|---|---|
| Configuração remota autenticada | Permite controlar branding sem recompilar | Assinatura/verificação, cache local e fallback offline |
| Suporte a até cinco DNSs | Aumenta resiliência operacional | DNSs configuradas no painel aparecem no app com seleção e health-check |
| Personalização de nome, logo e fundo | Separa identidade do produto da base técnica | Recursos remotos validados, cacheados e sem execução de código remoto |
| Pesquisa e filtros com paginação | Reduz custo de memória para catálogos grandes | Paginação, debounce e estado vazio compreensível |
| Telemetria local sem PII | Ajuda diagnóstico sem expor credenciais | Eventos redigidos, opt-in e exportação controlada |

## Prioridade baixa

| Item | Motivo | Critério de aceite |
|---|---|---|
| Tema claro/escuro e acessibilidade | Melhora uso em diferentes ambientes | Contraste, foco por controle remoto e escalabilidade de texto |
| Slides de conteúdo sugerido | Melhora descoberta de conteúdo autorizado | Categorias configuráveis e fallback offline |
| Introdução com logo animado e som opcional | Melhora identidade visual | Pode ser desativada, respeita volume e não bloqueia o acesso |
