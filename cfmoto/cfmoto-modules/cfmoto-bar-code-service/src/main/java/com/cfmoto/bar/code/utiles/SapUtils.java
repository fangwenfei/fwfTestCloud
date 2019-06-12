package com.cfmoto.bar.code.utiles;

import java.util.HashMap;
import java.util.Map;

/**
 * Sap工具类
 *
 * @author ye
 */
public class SapUtils {
    /**
     * 传入sap方法名以及所需方法参数封装成一个map集合返回
     *
     * @param functionName sap方法名
     * @param paramMap     sap方法参数
     * @return map集合
     */
    public static Map<String, Object> packParamsIntoMap(String functionName, Map<String, Object> paramMap) {
        Map<String, Object> returnMap = new HashMap<>(4);
        returnMap.put("functionName", functionName);
        returnMap.put("paramMap", paramMap);
        return returnMap;
    }
}
