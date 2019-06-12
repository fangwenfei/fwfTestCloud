package com.cfmoto.sap.api.feign.fallback;

import com.cfmoto.sap.api.feign.SapUserFeignService;
import com.github.pig.common.vo.UserVO;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/* **********************************************************************
 *              created by fangwenfei on 2019/3/12.
 * **********************************************************************
 * **********thunderstorm, rain and dew are all grace of heaven**********
 * **********************************************************************
 */
@Component
public class SapUserFeignServiceFallback implements SapUserFeignService {
    @Override
    public UserVO user(@RequestParam("id") Integer id) {
        return null;
    }
}
