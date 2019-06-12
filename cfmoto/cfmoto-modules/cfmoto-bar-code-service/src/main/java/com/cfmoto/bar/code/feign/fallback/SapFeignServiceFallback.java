package com.cfmoto.bar.code.feign.fallback;

import com.cfmoto.bar.code.feign.SapFeignService;
import com.github.pig.common.util.R;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@Component
public class SapFeignServiceFallback implements FallbackFactory<SapFeignService> {


    @Override
    public SapFeignService create(Throwable throwable) {
        return new SapFeignService() {
            @Override
            public R executeJcoFunction(@RequestBody Map<String, Object> callParamMap) {
                System.out.println("服务调用错误");
                return new R(R.FAIL, "服务调用错误");
            }
        };

    }
}
