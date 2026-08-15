package com.ouropro.player.net;

import com.ouropro.player.apps.LTVApp;
import com.ouropro.player.utils.Utils;
import iptv.m3u.parser.M3UItem;
import iptv.m3u.parser.M3UToolSet;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/* M3U cache with atomic completion marker. */
public class LoadM3UItemsCommand extends BaseCommand {
    public LoadM3UItemsCommand(String str, ArrayList<org.apache.http.NameValuePair> arrayList) {
        super(str, arrayList);
    }

    public List<M3UItem> execute() throws IOException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        File externalFilesDir = LTVApp.getInstance().getExternalFilesDir(null);
        if (externalFilesDir == null) {
            throw new IOException("Diretório de cache M3U indisponível");
        }
        File file = new File(externalFilesDir, Utils.getUserId(this.urlServer) + ".m3u");
        File completeMarker = new File(file.getPath() + ".complete");
        String today = simpleDateFormat.format(new Date());
        boolean cacheComplete = file.exists() && completeMarker.exists();
        boolean cacheCurrent = today.equals(LTVApp.instance.getM3uDate());

        if (!cacheComplete || !cacheCurrent) {
            File tempFile = new File(file.getPath() + ".part");
            if (tempFile.exists()) {
                tempFile.delete();
            }
            try (InputStream inputStream = new URL(this.urlServer).openStream();
                 FileOutputStream fileOutputStream = new FileOutputStream(tempFile, false)) {
                byte[] buffer = new byte[8192];
                int read;
                while ((read = inputStream.read(buffer)) != -1) {
                    if (read > 0) {
                        fileOutputStream.write(buffer, 0, read);
                    }
                }
                fileOutputStream.flush();
            } catch (IOException error) {
                tempFile.delete();
                throw error;
            }
            if (file.exists() && !file.delete()) {
                tempFile.delete();
                throw new IOException("Não foi possível substituir o cache M3U");
            }
            if (!tempFile.renameTo(file)) {
                tempFile.delete();
                throw new IOException("Não foi possível finalizar o cache M3U");
            }
            if (completeMarker.exists()) {
                completeMarker.delete();
            }
            if (!completeMarker.createNewFile()) {
                file.delete();
                throw new IOException("Não foi possível marcar o cache M3U como completo");
            }
        }

        if (!file.exists() || !completeMarker.exists()) {
            throw new IOException("Cache M3U incompleto");
        }
        List<M3UItem> items = M3UToolSet.load(file.getPath()).getItems();
        if (items == null || items.isEmpty()) {
            completeMarker.delete();
            throw new IOException("M3U sem itens válidos");
        }
        return items;
    }
}
