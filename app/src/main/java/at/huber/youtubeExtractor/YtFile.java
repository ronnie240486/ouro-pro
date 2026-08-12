package at.huber.youtubeExtractor;

import androidx.annotation.NonNull;
import androidx.core.graphics.Insets$$ExternalSyntheticOutline0;

/* JADX INFO: loaded from: classes.dex */
public class YtFile {
    private final Format format;
    private final String url;

    public YtFile(Format format, String str) {
        this.format = format;
        this.url = str;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        YtFile ytFile = (YtFile) obj;
        Format format = this.format;
        if (format == null ? ytFile.format != null : !format.equals(ytFile.format)) {
            return false;
        }
        String str = this.url;
        String str2 = ytFile.url;
        if (str != null) {
            return str.equals(str2);
        }
        return str2 == null;
    }

    public Format getFormat() {
        return this.format;
    }

    @Deprecated
    public Format getMeta() {
        return this.format;
    }

    public String getUrl() {
        return this.url;
    }

    public int hashCode() {
        Format format = this.format;
        int iHashCode = (format != null ? format.hashCode() : 0) * 31;
        String str = this.url;
        return iHashCode + (str != null ? str.hashCode() : 0);
    }

    @NonNull
    public String toString() {
        StringBuilder sbM = Insets$$ExternalSyntheticOutline0.m("YtFile{format=");
        sbM.append(this.format);
        sbM.append(", url='");
        sbM.append(this.url);
        sbM.append('\'');
        sbM.append('}');
        return sbM.toString();
    }
}
