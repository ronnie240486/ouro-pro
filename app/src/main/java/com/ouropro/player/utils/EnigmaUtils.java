package com.ouropro.player.utils;

/* JADX INFO: loaded from: classes.dex */
public class EnigmaUtils {
    private static final int[] data = {117, 103, 81, 55, 33, 54, 68, 76, 49, 81, 77, 72, 48, 55, 78, 120, 82, 71, 97, 52, 108, 75, 81, 71, 111, 74, 105, 72, 63, 52, 36, 74};
    private static final String HEX = "0123456789abcdef";
    private static final char[] HEX_ARRAY = HEX.toCharArray();

    public static String bytesToHex(byte[] bArr) {
        char[] cArr = new char[bArr.length * 2];
        for (int i = 0; i < bArr.length; i++) {
            int i2 = bArr[i] & 255;
            int i3 = i * 2;
            char[] cArr2 = HEX_ARRAY;
            cArr[i3] = cArr2[i2 >>> 4];
            cArr[i3 + 1] = cArr2[i2 & 15];
        }
        return new String(cArr).substring(0, 14);
    }

    public static String enigmatization(byte[] bArr) {
        try {
            return new String(Security.decrypt(keyToBytes(data), bArr));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] keyToBytes(int[] iArr) {
        int length = (iArr.length / 16) * 16;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append((char) iArr[i]);
        }
        return sb.toString().getBytes();
    }
}
