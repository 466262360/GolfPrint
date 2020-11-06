package com.mashangyou.golfprint.util;

import android.text.TextUtils;

import com.mashangyou.golfprint.api.Contant;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2020/11/4.
 * Des:
 */
public class PrintContract {

    /**
     * 打印内容
     */
    public static String createXxTxt(Map<String, String> hashMap) {

        StringBuilder builder = new StringBuilder();

        builder.append(PrintFormatUtils.init());
        //设置大号字体以及加粗
        builder.append(PrintFormatUtils.getAlignCmd(PrintFormatUtils.ALIGN_CENTER));
        builder.append(PrintFormatUtils.getFontSizeCmd(PrintFormatUtils.FONT_BIG));
        builder.append(PrintFormatUtils.getFontBoldCmd(PrintFormatUtils.FONT_BOLD));

        // 标题
        builder.append("订场凭证");
        //换行，调用次数根据换行数来控制
        addLineSeparator(builder);
        addLineSeparator(builder);
        builder.append(PrintFormatUtils.getAlignCmd(PrintFormatUtils.ALIGN_LEFT));
        //设置普通字体大小、不加粗
        builder.append(PrintFormatUtils.getFontSizeCmd(PrintFormatUtils.FONT_NORMAL));
        builder.append(PrintFormatUtils.getFontBoldCmd(PrintFormatUtils.FONT_BOLD_CANCEL));

        //内容
        builder.append("会籍身份");
        addLineSeparator(builder);
        builder.append("姓名：     " + hashMap.get(Contant.PRINT_NAME));
        addLineSeparator(builder);
        builder.append("ID：       " + hashMap.get(Contant.PRINT_ID));
        addLineSeparator(builder);
        builder.append("订场信息");
        addLineSeparator(builder);
        builder.append("时间：     " + hashMap.get(Contant.PRINT_DATE));
        addLineSeparator(builder);
        builder.append("地点：     " + hashMap.get(Contant.PRINT_GOLFNAME));
        addLineSeparator(builder);
        if (!TextUtils.isEmpty(hashMap.get(Contant.PRINT_FREQUENCY))) {
            String[] split = hashMap.get(Contant.PRINT_FREQUENCY).split("同组");
            builder.append("年度剩余： " + "个人" + split[0] + " 同组" + split[1]);
            addLineSeparator(builder);
        }
        builder.append("订单号：   " + hashMap.get(Contant.PRINT_ORDER));
        addLineSeparator(builder);
        builder.append("核销时间： " + hashMap.get(Contant.PRINT_CURRENT_DATE));
        addLineSeparator(builder);
        addLineSeparator(builder);
        builder.append("签名");
        addLineSeparator(builder);
        addLineSeparator(builder);
        builder.append("__________________________________________");
        addLineSeparator(builder);
        //设置某两列文字间空格数, x需要计算出来
        //addIdenticalStrToStringBuilder(builder, x, " ");

        //切纸
        builder.append(PrintFormatUtils.getCutPaperCmd());

        return builder.toString();
    }

    /**
     * 向StringBuilder中添加指定数量的相同字符
     *
     * @param printCount   添加的字符数量
     * @param identicalStr 添加的字符
     */

    private static void addIdenticalStrToStringBuilder(StringBuilder builder, int printCount, String identicalStr) {
        for (int i = 0; i < printCount; i++) {
            builder.append(identicalStr);
        }
    }

    /**
     * 根据字符串截取前指定字节数,按照GBK编码进行截取
     *
     * @param str 原字符串
     * @param len 截取的字节数
     * @return 截取后的字符串
     */
    private static String subStringByGBK(String str, int len) {
        String result = null;
        if (str != null) {
            try {
                byte[] a = str.getBytes("GBK");
                if (a.length <= len) {
                    result = str;
                } else if (len > 0) {
                    result = new String(a, 0, len, "GBK");
                    int length = result.length();
                    if (str.charAt(length - 1) != result.charAt(length - 1)) {
                        if (length < 2) {
                            result = null;
                        } else {
                            result = result.substring(0, length - 1);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 添加换行符
     */
    private static void addLineSeparator(StringBuilder builder) {
        builder.append("\n");
    }

    /**
     * 在GBK编码下，获取其字符串占据的字符个数
     */
    private static int getCharCountByGBKEncoding(String text) {
        try {
            return text.getBytes("GBK").length;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }


    /**
     * 打印相关配置
     */
    public static class PrintConfig {
        public int maxLength = 30;

        public boolean printBarcode = false;  // 打印条码
        public boolean printQrCode = false;   // 打印二维码
        public boolean printEndText = true;   // 打印结束语
        public boolean needCutPaper = false;  // 是否切纸
    }

}
