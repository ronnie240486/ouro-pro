# Tutorial: atualização interna de APK no Android

Este guia descreve o fluxo implementado no OuroPro para baixar uma versão nova do aplicativo e abrir o instalador do Android **sem abrir o navegador**. O procedimento funciona para aplicativos Android comuns, desde que a instalação final seja confirmada pelo usuário no sistema.

> **Limite de segurança do Android:** um aplicativo normal não pode substituir silenciosamente a própria instalação. Depois do download, o Android exibe o instalador e o usuário confirma a instalação. Instalação sem confirmação exige um dispositivo gerenciado (Device Owner/MDM), root ou distribuição corporativa específica.

## 1. Publicar um link direto de APK

O campo de atualização deve apontar para o arquivo APK em si, e não para uma página HTML, JSON, página de release ou encurtador que exija login. O link deve responder com o conteúdo binário do APK e ser acessível diretamente pelo dispositivo.

| Correto | Incorreto |
|---|---|
| `https://servidor.exemplo/app-v7.apk` | `https://github.com/usuario/repositorio/releases` |
| Arquivo `.apk` direto | Página web, JSON, HTML ou tela de login |
| Mesmo certificado de assinatura | APK assinado com outra chave |

Em um repositório GitHub privado, o APK não é acessível diretamente a um aparelho sem autenticação. Mantenha o código privado e publique o artefato APK em uma origem de download controlada e acessível, ou entregue a URL direta pelo painel.

## 2. Permissões e FileProvider

No `AndroidManifest.xml`, inclua a permissão de instalação e um `FileProvider` cujo `authorities` seja baseado no `applicationId`:

```xml
<uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />

<provider
    android:name="androidx.core.content.FileProvider"
    android:authorities="${applicationId}.provider"
    android:exported="false"
    android:grantUriPermissions="true">
    <meta-data
        android:name="android.support.FILE_PROVIDER_PATHS"
        android:resource="@xml/provider_paths" />
</provider>
```

Crie `res/xml/provider_paths.xml` permitindo apenas o diretório privado usado para downloads:

```xml
<paths xmlns:android="http://schemas.android.com/apk/res/android">
    <cache-path name="updates" path="updates/" />
</paths>
```

## 3. Baixar o APK de forma robusta

Baixe para um arquivo temporário dentro de `cacheDir/updates`. Não ignore exceções de leitura ou gravação. Só renomeie o arquivo temporário para `.apk` depois que o download for concluído. Verifique o código HTTP e rejeite respostas de erro.

```java
File updatesDir = new File(getCacheDir(), "updates");
updatesDir.mkdirs();
File partial = new File(updatesDir, "update.part");
File apk = new File(updatesDir, "update.apk");

HttpURLConnection connection = (HttpURLConnection) new URL(apkUrl).openConnection();
connection.setConnectTimeout(20_000);
connection.setReadTimeout(60_000);
connection.setInstanceFollowRedirects(true);

if (connection.getResponseCode() < 200 || connection.getResponseCode() >= 300) {
    throw new IOException("Resposta HTTP inválida");
}

try (InputStream in = new BufferedInputStream(connection.getInputStream());
     OutputStream out = new BufferedOutputStream(new FileOutputStream(partial))) {
    byte[] buffer = new byte[16 * 1024];
    int read;
    while ((read = in.read(buffer)) != -1) {
        out.write(buffer, 0, read);
    }
}

if (!partial.renameTo(apk)) {
    throw new IOException("Não foi possível finalizar o APK");
}
```

## 4. Validar antes de abrir o instalador

Não envie ao instalador uma resposta HTML, JSON ou download incompleto. Valide o arquivo como APK/ZIP, confirme a presença de `AndroidManifest.xml` e confira o pacote esperado.

```java
try (ZipFile zip = new ZipFile(apk)) {
    if (zip.getEntry("AndroidManifest.xml") == null) {
        throw new IOException("Arquivo não é um APK válido");
    }
}

PackageInfo archive = getPackageManager().getPackageArchiveInfo(apk.getAbsolutePath(), 0);
if (archive == null || !getPackageName().equals(archive.packageName)) {
    throw new IOException("APK não pertence a este aplicativo");
}
```

Também é recomendável validar o certificado de assinatura do APK baixado contra o certificado instalado. Isso evita aceitar uma atualização assinada por outra chave.

## 5. Abrir o instalador do Android

Use o `FileProvider` para compartilhar a URI do arquivo com o instalador.

```java
Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".provider", apk);
Intent intent = new Intent(Intent.ACTION_VIEW);
intent.setDataAndType(uri, "application/vnd.android.package-archive");
intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
startActivity(intent);
```

Se necessário, verifique `getPackageManager().canRequestPackageInstalls()` e encaminhe o usuário às configurações de permissão de instalação por fontes desconhecidas.

## 6. Fluxo recomendado de interface

O botão **Atualizar agora** deve seguir esta sequência: verificar se existe URL; mostrar download/progresso; validar o APK; abrir o instalador Android; informar falhas sem tentar instalar arquivos inválidos. Mensagens úteis incluem: “O link não é um APK direto”, “Download incompleto”, “APK de outro aplicativo” e “A atualização precisa da confirmação do Android”.

## 7. Checklist de testes

| Cenário | Resultado esperado |
|---|---|
| Link direto para APK válido | Instalador abre com a nova versão. |
| Link para HTML/JSON | Aplicativo mostra erro e não abre instalador. |
| Download interrompido | Arquivo parcial é apagado. |
| APK de outro pacote | Atualização é recusada. |
| APK com assinatura diferente | Android recusa a substituição. |
| Usuário cancela instalação | Aplicativo atual continua funcionando. |

## 8. Integração com painel

Armazene no painel uma URL direta atualizada no campo `apk_link`. Sempre que publicar uma nova versão, atualize esse campo para o novo artefato. O aplicativo não precisa de token GitHub quando o link já é direto e publicamente acessível ao dispositivo.

Para o OuroPro, mantenha o repositório de código privado e distribua somente o APK assinado por uma origem apropriada para atualizações. Nunca incorpore tokens de GitHub no APK.

---

**Resultado:** o usuário toca em Atualizar, o APK é baixado e validado, o Android pede a confirmação de instalação e a nova versão passa a abrir após a substituição.
