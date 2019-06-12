package com.cfmoto.sap.api.controller;

import com.cfmoto.sap.api.config.JcoProviderConfig;
import com.cfmoto.sap.api.service.CustomJcoService;
import com.github.pig.common.util.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/customJco")
public class CustomJcoController {

    @Autowired
    private CustomJcoService customJcoService;

    @Autowired
    private JcoProviderConfig jcoProviderConfig;

    @GetMapping("/pingJco")
    public R pingCalls() {

        R r = customJcoService.pingCalls(jcoProviderConfig.getJcoDestName());
        return r;

    }

    /**
     * 传入功能名称和Map类型参数
     *
     * @param
     * @return
     */
    @RequestMapping("/executeJcoFunction")
    public R executeJcoFunction(@RequestBody Map<String, Object> param) {
        String functionName = (String) param.get("functionName");
        Map<String, Object> paramMap = (Map<String, Object>) param.get("paramMap");
        if (functionName == null) {
            return new R(R.FAIL, "param未包含functionName参数");
        }
        return customJcoService.execute(functionName, paramMap);
    }

}
