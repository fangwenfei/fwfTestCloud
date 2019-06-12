package com.cfmoto.bar.code.controller.lableprint;

import com.cfmoto.bar.code.model.entity.CfPrintLodopTemplate;
import com.cfmoto.bar.code.service.lableprint.ICfLabelPrintService;
import com.github.pig.common.util.R;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 标签打印功能前端控制器
 *
 * @author yezi
 * @date 2019/5/29
 */
@RestController
@RequestMapping("/labelPrint")
@Log4j
public class CfLabelPrintController {

    @Autowired
    private ICfLabelPrintService cfLabelPrintService;

    /**
     * 根据打印功能名称获取默认打印模板
     */
    @GetMapping("/getDefaultTemplateByFunctionName")
    @ApiOperation(value = "根据打印功能名称获取默认打印模板")
    public R<CfPrintLodopTemplate> getDefaultTemplateByFunctionName(@RequestParam("functionName") String functionName) {
        try {
            CfPrintLodopTemplate defaultPrintTemplate = cfLabelPrintService.getDefaultTemplateByFunctionName(functionName);
            return new R<>(defaultPrintTemplate);
        } catch (Exception e) {
            log.error(ExceptionUtils.getMessage(e));
            return new R<>(R.FAIL, e.getMessage());
        }
    }
}
