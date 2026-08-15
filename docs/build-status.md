# Status de validação

A validação foi executada com o Gradle Wrapper 8.10.2 incluído no projeto. A primeira execução encontrou uma configuração incorreta do plugin Realm; o build raiz foi corrigido para usar `io.realm:realm-gradle-plugin:10.19.0` por classpath e o módulo passou a aplicar `realm-android`.

A segunda execução resolveu a configuração do plugin e parou na etapa de configuração do módulo por falta de Android SDK nesta sandbox:

```text
SDK location not found. Define a valid SDK location with an ANDROID_HOME environment variable
or by setting the sdk.dir path in local.properties.
```

Isso significa que a configuração Gradle avançou além do erro de plugin, mas a compilação Java/Android, os testes unitários e o APK de debug ainda precisam ser executados em uma máquina com JDK 17, Android SDK, `platforms;android-35` e `build-tools` instalados.

Antes da publicação, também foram feitas verificações estáticas para excluir APKs, AABs, keystores, certificados privados, arquivos `.env` e padrões comuns de chaves de API do conteúdo versionado.
