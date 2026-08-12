package at.huber.youtubeExtractor;

import androidx.annotation.NonNull;
import androidx.core.graphics.Insets$$ExternalSyntheticOutline0;

/* JADX INFO: loaded from: classes.dex */
public class Format {
    private ACodec aCodec;
    private final int audioBitrate;
    private final String ext;
    private final int fps;
    private final int height;
    private final boolean isDashContainer;
    private final boolean isHlsContent;
    private final int itag;
    private VCodec vCodec;

    public enum ACodec {
        MP3,
        AAC,
        VORBIS,
        OPUS,
        NONE
    }

    public enum VCodec {
        H263,
        H264,
        MPEG4,
        VP8,
        VP9,
        NONE
    }

    public Format(int i, String str, int i2) {
        this.itag = i;
        this.ext = str;
        this.height = i2;
        this.fps = 30;
        this.audioBitrate = -1;
        this.isDashContainer = true;
        this.isHlsContent = false;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Format format = (Format) obj;
        if (this.itag != format.itag || this.height != format.height || this.fps != format.fps || this.audioBitrate != format.audioBitrate || this.isDashContainer != format.isDashContainer || this.isHlsContent != format.isHlsContent) {
            return false;
        }
        String str = this.ext;
        if (str == null ? format.ext == null : str.equals(format.ext)) {
            return this.vCodec == format.vCodec && this.aCodec == format.aCodec;
        }
        return false;
    }

    public int getAudioBitrate() {
        return this.audioBitrate;
    }

    public ACodec getAudioCodec() {
        return this.aCodec;
    }

    public String getExt() {
        return this.ext;
    }

    public int getFps() {
        return this.fps;
    }

    public int getHeight() {
        return this.height;
    }

    public int getItag() {
        return this.itag;
    }

    public VCodec getVideoCodec() {
        return this.vCodec;
    }

    public int hashCode() {
        int i = this.itag * 31;
        String str = this.ext;
        int iHashCode = (((((i + (str != null ? str.hashCode() : 0)) * 31) + this.height) * 31) + this.fps) * 31;
        VCodec vCodec = this.vCodec;
        int iHashCode2 = (iHashCode + (vCodec != null ? vCodec.hashCode() : 0)) * 31;
        ACodec aCodec = this.aCodec;
        return ((((((iHashCode2 + (aCodec != null ? aCodec.hashCode() : 0)) * 31) + this.audioBitrate) * 31) + (this.isDashContainer ? 1 : 0)) * 31) + (this.isHlsContent ? 1 : 0);
    }

    public boolean isDashContainer() {
        return this.isDashContainer;
    }

    public boolean isHlsContent() {
        return this.isHlsContent;
    }

    @NonNull
    public String toString() {
        StringBuilder sbM = Insets$$ExternalSyntheticOutline0.m("Format{itag=");
        sbM.append(this.itag);
        sbM.append(", ext='");
        Insets$$ExternalSyntheticOutline0.m(sbM, this.ext, '\'', ", height=");
        sbM.append(this.height);
        sbM.append(", fps=");
        sbM.append(this.fps);
        sbM.append(", vCodec=");
        sbM.append(this.vCodec);
        sbM.append(", aCodec=");
        sbM.append(this.aCodec);
        sbM.append(", audioBitrate=");
        sbM.append(this.audioBitrate);
        sbM.append(", isDashContainer=");
        sbM.append(this.isDashContainer);
        sbM.append(", isHlsContent=");
        sbM.append(this.isHlsContent);
        sbM.append('}');
        return sbM.toString();
    }

    public Format(int i, String str, VCodec vCodec, ACodec aCodec, int i2, boolean z) {
        this.itag = i;
        this.ext = str;
        this.height = -1;
        this.fps = 30;
        this.audioBitrate = i2;
        this.isDashContainer = true;
        this.isHlsContent = false;
    }

    public Format(int i, String str, int i2, int i3) {
        this.itag = i;
        this.ext = str;
        this.height = i2;
        this.fps = 30;
        this.audioBitrate = i3;
        this.isDashContainer = false;
        this.isHlsContent = false;
    }

    public Format(int i, int i2, int i3) {
        this.itag = i;
        this.ext = "mp4";
        this.height = i2;
        this.fps = 30;
        this.audioBitrate = i3;
        this.isDashContainer = false;
        this.isHlsContent = true;
    }

    public Format(int i, String str, int i2, VCodec vCodec, int i3, ACodec aCodec, boolean z) {
        this.itag = i;
        this.ext = str;
        this.height = i2;
        this.audioBitrate = -1;
        this.fps = 60;
        this.isDashContainer = true;
        this.isHlsContent = false;
    }
}
