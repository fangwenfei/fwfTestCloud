package com.cfmoto.bar.code.utiles;

/**
 * Integer包装类工具类
 *
 * @author ye
 */
public class IntegerUtils {

    /**
     * 判断数值是否为空(为null或为0)
     *
     * @param num 传入的数值
     * @return
     */
    public static boolean isBlank(Integer num) {
        return num == null || num == 0;
    }
}
