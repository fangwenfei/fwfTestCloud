package com.cfmoto.bar.code.feign;

import com.cfmoto.bar.code.feign.fallback.SapFeignServiceFallback;
import com.github.pig.common.util.R;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "cfmoto-sap-api"/*, fallbackFactory = SapFeignServiceFallback.class*/)
public interface SapFeignService {

    String SUCCESS_CODE = "1";
    String ERROR_CODE = "0";

    /**
     * paramMap参数必须包含 String functionName rfc名称, Map<String,Object> paramMap 需要传递的参数
     */
    @RequestMapping(value = "/customJco/executeJcoFunction", method = RequestMethod.POST)
    R executeJcoFunction(@RequestBody Map<String, Object> callParamMap);

}
