package com.cfmoto.bar.code.service;

import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 仓库备料交接
 *
 * @author ye
 */
public interface ICfRepositoryStockHandoverService {

    Map<String, Object> analysisBarcode(JSONObject jsonObject, HttpServletRequest request);

}
