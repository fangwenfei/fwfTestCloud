package com.cfmoto.sap.api.service;

import com.github.pig.common.util.R;

import java.util.Map;

public interface CustomJcoService {

    //测试连接是否连通
    com.github.pig.common.util.R pingCalls(String destName );

    R execute(String functionName, Map<String, Object> paramMap );
}
