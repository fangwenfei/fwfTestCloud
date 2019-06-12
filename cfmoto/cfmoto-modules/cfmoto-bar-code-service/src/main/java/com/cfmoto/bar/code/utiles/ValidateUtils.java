package com.cfmoto.bar.code.utiles;

import com.xiaoleilu.hutool.util.StrUtil;

import java.util.List;

/**
 * web层校验数据时需要用到的校验工具类
 *
 * @author ye
 */
public class ValidateUtils<T> {

    public boolean isNotNull(List<T> list) {
        if (list != null && list.size() != 0) {

            for (T t : list) {
                if (t == null || t == "") {
                    return false;
                }
            }
            return true;
        }
        return false;

    }

    public boolean isNotNull(String... param) {
        for (String s : param) {
            if(StrUtil.isBlank(s)){
                return false;
            }
        }
        return true;
    }
}
