package com.cfmoto.bar.code.model.options;

import java.util.HashMap;
import java.util.Map;

/**
 * options 选项存储基类
 * 主要存储并维护如支付方式、购买方式等同一字段有多种选项的选项类
 *
 * @author ye
 * @date 2019-05-07
 */
public class BaseOption {

    protected Map<Object, String> options = new HashMap<>();

    public Map<Object, String> getOptions() {
        return options;
    }

    public String getValue(Integer key) {
        return options.get(key);
    }

}
