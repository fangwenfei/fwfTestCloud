package com.cfmoto.bar.code.feign;

import com.cfmoto.bar.code.feign.fallback.UserFeignServiceFallback;
import com.github.pig.common.vo.UserVO;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/* **********************************************************************
 *              Created by FangWenFei on 2019/3/12.                     
 * **********************************************************************
 * **********Thunderstorm, rain and dew are all grace of heaven**********
 * **********************************************************************
 */
@FeignClient( name="cfmoto-upms-service",fallback= UserFeignServiceFallback.class )
public interface UserFeignService {

    @RequestMapping( value = "/user/id",method = RequestMethod.POST )
    UserVO user(@RequestParam("id") Integer id);
}
