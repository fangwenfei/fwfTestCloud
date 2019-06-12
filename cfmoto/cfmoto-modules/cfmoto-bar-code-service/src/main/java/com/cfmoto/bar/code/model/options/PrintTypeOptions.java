package com.cfmoto.bar.code.model.options;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * 打印方式选项类
 *
 * @author ye
 * @date 2019-05-07
 */
@Lazy
@Component
public class PrintTypeOptions extends BaseOption {
    {
        options.put(0, "国内打印");
        options.put(1, "国外打印-非美国");
        options.put(2, "国外打印-美国");
    }
}
