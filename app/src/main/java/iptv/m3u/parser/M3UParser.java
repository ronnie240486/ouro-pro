package iptv.m3u.parser;

import androidx.core.graphics.Insets$$ExternalSyntheticOutline0;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes2.dex */
public class M3UParser {
    private static final String ATTR_CHANNEL_ID = "id";
    private static final String ATTR_CHANNEL_NAME = "channel_name";
    private static final String ATTR_DLNA_EXTRAS = "dlna_extras";
    private static final String ATTR_DURATION = "duration";
    private static final String ATTR_GROUP_TITLE = "group-title";
    private static final String ATTR_LOGO = "logo";
    private static final String ATTR_NAME = "name";
    private static final String ATTR_PLUGIN = "plugin";
    private static final String ATTR_TVG_PREFIX = "tvg-";
    private static final String ATTR_TVG_SUFFIX = "-tvg";
    private static final String ATTR_TYPE = "type";
    private static final String EMPTY_STRING = "";
    private static final String INVALID_STREAM_URL = "http://0.0.0.0:1234";
    private static final String PREFIX_COMMENT = "#";
    private static final String PREFIX_EXTINF = "#EXTINF:";
    private static final String PREFIX_EXTM3U = "#EXTM3U";
    private static M3UParser mInstance;
    private M3UHandler mHandler = null;
    private M3UItem mTempItem = null;

    /* renamed from: iptv.m3u.parser.M3UParser$1, reason: invalid class name */
    public static /* synthetic */ class AnonymousClass1 {
        public static final /* synthetic */ int[] $SwitchMap$iptv$m3u$parser$M3UParser$Status;

        static {
            int[] iArr = new int[Status.values().length];
            $SwitchMap$iptv$m3u$parser$M3UParser$Status = iArr;
            try {
                iArr[Status.READY.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$iptv$m3u$parser$M3UParser$Status[Status.READING_KEY.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$iptv$m3u$parser$M3UParser$Status[Status.KEY_READY.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$iptv$m3u$parser$M3UParser$Status[Status.READING_VALUE.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
        }
    }

    public enum Status {
        READY,
        READING_KEY,
        KEY_READY,
        READING_VALUE
    }

    private M3UParser() {
    }

    private int convert2int(String str) {
        try {
            return Integer.parseInt(str);
        } catch (Exception unused) {
            return -1;
        }
    }

    private void flush(M3UHandler m3UHandler) {
        M3UItem m3UItem = this.mTempItem;
        if (m3UItem != null) {
            if (m3UItem.getStreamURL() != null) {
                m3UHandler.onReadEXTINF(this.mTempItem);
            }
            this.mTempItem = null;
        }
    }

    private String getAttr(Map<String, String> map, String str) {
        String str2 = map.get(str);
        if (str2 != null) {
            return str2;
        }
        String str3 = map.get(ATTR_TVG_PREFIX + str);
        if (str3 != null) {
            return str3;
        }
        return map.get(str + ATTR_TVG_SUFFIX);
    }

    public static final M3UParser getInstance() {
        if (mInstance == null) {
            mInstance = new M3UParser();
        }
        return mInstance;
    }

    private Map<String, String> parseAttributes(String str) {
        String str2;
        HashMap hashMap = new HashMap();
        if (str != null && !str.equals("")) {
            Status status = Status.READY;
            StringBuffer stringBuffer = new StringBuffer();
            char charAt = str.charAt(0);
            if (charAt == '-' || Character.isDigit(charAt)) {
                stringBuffer.append(charAt);
                int i = 0;
                while (true) {
                    i++;
                    if (i >= str.length()) {
                        break;
                    }
                    char charAt2 = str.charAt(i);
                    if (!Character.isDigit(charAt2)) {
                        break;
                    }
                    stringBuffer.append(charAt2);
                }
                putAttr(hashMap, "duration", stringBuffer.toString());
                str = shrink(str.replaceFirst(stringBuffer.toString(), ""));
                reset(stringBuffer);
            }
            int i2 = 0;
            loop0: while (true) {
                boolean z = false;
                str2 = "";
                while (i2 < str.length()) {
                    int i3 = i2 + 1;
                    char charAt3 = str.charAt(i2);
                    int i4 = AnonymousClass1.$SwitchMap$iptv$m3u$parser$M3UParser$Status[status.ordinal()];
                    if (i4 != 1) {
                        if (i4 != 2) {
                            if (i4 != 3) {
                                if (i4 == 4) {
                                    if (z) {
                                        while (charAt3 != '\"' && i3 < str.length()) {
                                            stringBuffer.append(charAt3);
                                            charAt3 = str.charAt(i3);
                                            i3++;
                                        }
                                        if (stringBuffer.length() > 0) {
                                            putAttr(hashMap, str2, stringBuffer.toString());
                                            reset(stringBuffer);
                                        }
                                        status = Status.READY;
                                        i2 = i3;
                                    } else if (Character.isWhitespace(charAt3)) {
                                        if (stringBuffer.length() > 0) {
                                            putAttr(hashMap, str2, stringBuffer.toString());
                                            reset(stringBuffer);
                                        }
                                        status = Status.READY;
                                        str2 = "";
                                    } else {
                                        stringBuffer.append(charAt3);
                                    }
                                }
                            } else if (!Character.isWhitespace(charAt3)) {
                                if (charAt3 == '\"') {
                                    z = true;
                                } else {
                                    stringBuffer.append(charAt3);
                                }
                                status = Status.READING_VALUE;
                            }
                        } else if (charAt3 == '=') {
                            StringBuilder m = Insets$$ExternalSyntheticOutline0.m(str2);
                            m.append(stringBuffer.toString());
                            String shrink = shrink(m.toString());
                            reset(stringBuffer);
                            str2 = shrink;
                            status = Status.KEY_READY;
                        } else {
                            stringBuffer.append(charAt3);
                        }
                    } else if (!Character.isWhitespace(charAt3)) {
                        if (charAt3 == ',') {
                            putAttr(hashMap, ATTR_CHANNEL_NAME, str.substring(i3));
                            i2 = str.length();
                        } else {
                            stringBuffer.append(charAt3);
                            status = Status.READING_KEY;
                        }
                    }
                    i2 = i3;
                }
                break loop0;
            }
            if (!str2.equals("") && stringBuffer.length() > 0) {
                putAttr(hashMap, str2, stringBuffer.toString());
                reset(stringBuffer);
            }
        }
        return hashMap;
    }

    private M3UHead parseHead(String str) {
        Map<String, String> parseAttributes = parseAttributes(str);
        M3UHead m3UHead = new M3UHead();
        m3UHead.setName(getAttr(parseAttributes, ATTR_NAME));
        m3UHead.setType(getAttr(parseAttributes, ATTR_TYPE));
        m3UHead.setDLNAExtras(getAttr(parseAttributes, ATTR_DLNA_EXTRAS));
        m3UHead.setPlugin(getAttr(parseAttributes, ATTR_PLUGIN));
        return m3UHead;
    }

    private M3UItem parseItem(String str) {
        Map<String, String> parseAttributes = parseAttributes(str);
        M3UItem m3UItem = new M3UItem();
        m3UItem.setChannelID(getAttr(parseAttributes, "id"));
        m3UItem.setChannelName(getAttr(parseAttributes, ATTR_CHANNEL_NAME));
        m3UItem.setDuration(convert2int(getAttr(parseAttributes, "duration")));
        m3UItem.setLogoURL(getAttr(parseAttributes, ATTR_LOGO));
        m3UItem.setGroupTitle(getAttr(parseAttributes, ATTR_GROUP_TITLE));
        m3UItem.setType(getAttr(parseAttributes, ATTR_TYPE));
        m3UItem.setDLNAExtras(getAttr(parseAttributes, ATTR_DLNA_EXTRAS));
        m3UItem.setPlugin(getAttr(parseAttributes, ATTR_PLUGIN));
        return m3UItem;
    }

    private void putAttr(Map<String, String> map, String str, String str2) {
        map.put(str, str2);
    }

    private void reset(StringBuffer stringBuffer) {
        stringBuffer.delete(0, stringBuffer.length());
    }

    private String shrink(String str) {
        if (str == null) {
            return null;
        }
        return str.trim();
    }

    private void updateURL(String str) {
        if (this.mTempItem == null || INVALID_STREAM_URL.equals(str)) {
            return;
        }
        this.mTempItem.setStreamURL(str.trim());
    }

    public void parse(String str) {
        parse(str, this.mHandler);
    }

    public void setHandler(M3UHandler m3UHandler) {
        this.mHandler = m3UHandler;
    }

    public void parse(String str, M3UHandler m3UHandler) {
        if (m3UHandler == null) {
            return;
        }
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(str), StandardCharsets.UTF_8));
            while (true) {
                String shrink = shrink(bufferedReader.readLine());
                if (shrink == null) {
                    flush(m3UHandler);
                    bufferedReader.close();
                    return;
                } else if (shrink.startsWith(PREFIX_EXTM3U)) {
                    m3UHandler.onReadEXTM3U(parseHead(shrink(shrink.replaceFirst(PREFIX_EXTM3U, ""))));
                } else if (shrink.startsWith(PREFIX_EXTINF)) {
                    flush(m3UHandler);
                    this.mTempItem = parseItem(shrink(shrink.replaceFirst(PREFIX_EXTINF, "")));
                } else if (!shrink.startsWith(PREFIX_COMMENT) && !shrink.equals("")) {
                    updateURL(shrink);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
    }
}
