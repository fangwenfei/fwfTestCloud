package com.cfmoto.bar.code.service.lableprint;

import com.cfmoto.bar.code.model.entity.CfPrintLodopTemplate;

/**
 * @author yezi
 * @date 2019/5/29
 */
public interface ICfLabelPrintService {
    /**
     * 根据打印功能名称获取默认模板
     *
     * @param functionName 打印功能名称
     * @return Map
     */
    CfPrintLodopTemplate getDefaultTemplateByFunctionName(String functionName) throws Exception;
}
