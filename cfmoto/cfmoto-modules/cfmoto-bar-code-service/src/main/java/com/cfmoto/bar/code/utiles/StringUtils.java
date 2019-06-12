package com.cfmoto.bar.code.utiles;

/**
 * 字符串工具类
 *
 * @author ye
 */
public class StringUtils {

    private static final int ORDER_NO_LENGTH = 10;

    /**
     * 给订单号补充0，订单号固定长度为10位
     *
     * @author ye
     */
    public static String fillOrderNoWithZero(String orderNo) {
        int toFillNumber = ORDER_NO_LENGTH - orderNo.length();
        StringBuilder sb = new StringBuilder(orderNo);
        for (int i = 0; i < toFillNumber; i++) {
            sb.insert(0, "0");
        }
        return sb.toString();
    }
}
