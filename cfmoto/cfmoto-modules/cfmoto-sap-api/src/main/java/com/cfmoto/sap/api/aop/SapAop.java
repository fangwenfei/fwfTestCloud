package com.cfmoto.sap.api.aop;

import com.cfmoto.sap.api.feign.SapUserFeignService;
import com.github.pig.common.constant.SecurityConstants;
import com.github.pig.common.util.R;
import com.github.pig.common.util.UserUtils;
import com.github.pig.common.vo.UserVO;
import com.xiaoleilu.hutool.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;


/**
 * @author yecx
 * @date 2019/05/15
 * sap增强
 */
@Slf4j
@Aspect
@Component
public class SapAop {
    @Autowired
    private SapUserFeignService sapUserFeignService;

    @Value("${pig.sapaop.interceptorFunctionNames}")
    private String interceptorFunctionNames;

    @Pointcut("execution(* com.cfmoto.sap.api.service.impl.CustomJcoServiceImpl.execute(..))")
    public void pointCutSap() {
    }

    /**
     * 拦截器具体实现
     *
     * @param pjp 切点 所有返回对象R
     * @return R  结果包装
     */
    @Around("pointCutSap()")
    public R<Map<String, Object>> methodRHandler(ProceedingJoinPoint pjp) throws Throwable {
        return methodHandler(pjp);
    }

    private R<Map<String, Object>> methodHandler(ProceedingJoinPoint pjp) throws Throwable {
        try {


            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = attributes.getRequest();

            //从请求头中获取用户名
            String username = request.getHeader(SecurityConstants.USER_HEADER);
            //从token中获取用户ID
            Integer userId = UserUtils.getUserId(request);


            R<Map<String, Object>> result;

            //前置增强
            //获取被增强方法的参数
            String functionName = (String) pjp.getArgs()[0];

            if(StrUtil.isNotBlank(interceptorFunctionNames)){
                //根据配置文件中的方法名判断哪些接口需要添加用户名字段
                String[] names = interceptorFunctionNames.split(",");
                for (String name : names) {
                    if (functionName.equals(name)) {
                        Map<String, Object> paramMap = (Map<String, Object>) pjp.getArgs()[1];
                        //增加用户名传入给sap
                        paramMap.put("IV_UNAME", username);
                    }
                }
            }

            //执行被切代码
            result = (R<Map<String, Object>>) pjp.proceed();

            //后置增强
            //获取sap传回的工厂字段并校验是否与当前用户工厂一致

            Map<String, Object> data = result.getData();
            if (data != null) {
                String factory = (String) data.get("EV_WERKS");
                if(StrUtil.isNotBlank(factory)){
                    //获取用户工厂
                    UserVO user = sapUserFeignService.user(userId);
                    if (user != null) {
                        if (StrUtil.isNotBlank(user.getSite())) {
                            if (!user.getSite().equals(factory)) {
                                return new R<>(R.FAIL, "该单号对应工厂与用户工厂不一致!!!");
                            }
                        }
                    }
                }
            }

            return result;
        } catch (Exception e) {
            e.printStackTrace();
            //包装错误信息并返回
            R<Map<String, Object>> r = new R<>();
            r.setErrorAndErrorMsg(e.getMessage());
            return r;
        }

    }
}
