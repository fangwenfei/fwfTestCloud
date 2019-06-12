package com.cfmoto.bar.code.service.lableprint.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.cfmoto.bar.code.model.entity.CfPrintFunction;
import com.cfmoto.bar.code.model.entity.CfPrintLodopTemplate;
import com.cfmoto.bar.code.service.ICfPrintFunctionService;
import com.cfmoto.bar.code.service.ICfPrintLodopTemplateService;
import com.cfmoto.bar.code.service.lableprint.ICfLabelPrintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 标签打印功能接口
 *
 * @author yezi
 * @date 2019/5/29
 */
@Service
public class CfLabelPrintServiceImpl implements ICfLabelPrintService {
    @Autowired
    private ICfPrintFunctionService printFunctionService;

    @Autowired
    private ICfPrintLodopTemplateService printLodopTemplateService;

    @Override
    public CfPrintLodopTemplate getDefaultTemplateByFunctionName(String functionName) throws Exception {
        CfPrintFunction printFunction = printFunctionService.selectOne(new EntityWrapper<CfPrintFunction>().eq("function_name", functionName));
        List<CfPrintLodopTemplate> templates = printLodopTemplateService.selectList(new EntityWrapper<CfPrintLodopTemplate>().eq("function_id", printFunction.getFunctionId()));
        if (templates == null || templates.size() == 0) {
            throw new Exception("未找到" + functionName + "对应的打印模板!!!");
        }
        return templates.get(0);
    }
}
