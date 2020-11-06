

package cn.bigcode.tcodedecoder;

/**
 * 用于调用.so包中的方法，由于Android中需要包，类名称均和.so中一致，所以使用该类代理
 *
 * @author 田广文
 * @date 2011-login1-21 下午05:18:45
 */
public final class TCodeDecoder {

    static {
        System.loadLibrary("TCodeDecoder");
    }

    public static native DecoderResult decoder(byte[] img, int width, int height, int format);
}