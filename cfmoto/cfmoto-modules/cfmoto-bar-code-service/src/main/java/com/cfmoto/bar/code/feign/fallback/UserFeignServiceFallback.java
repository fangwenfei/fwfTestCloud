package com.cfmoto.bar.code.feign.fallback;

import com.cfmoto.bar.code.feign.UserFeignService;
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
public class UserFeignServiceFallback implements UserFeignService {
    @Override
    public UserVO user(@RequestParam("id") Integer id) {
        return null;
    }
}
