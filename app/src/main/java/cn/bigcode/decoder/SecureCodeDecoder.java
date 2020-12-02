package cn.bigcode.decoder;

/**
 * Created by WanChun on 2017/7/29.
 */

public class SecureCodeDecoder {
    public String qrcode;
    public int qrlen;
    public String secureCode;
    public int secureLen;
    public int codeVersion;
    static {
        System.loadLibrary("SecureCodeDecoder");
    }

    public static native ScanResult decode(byte[] img, int width, int height, int format, int decodeMode);

    public static native String getVersion();
}
