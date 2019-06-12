package com.cfmoto.bar.code.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.cfmoto.bar.code.model.entity.CfStockTooReceiveLine;

import java.util.Map;

/* **********************************************************************
 *              Created by FangWenFei on 2019/3/18.                     
 * **********************************************************************
 * **********Thunderstorm, rain and dew are all grace of heaven**********
 * **********************************************************************
 */
public interface ICfTooReceiveStockService extends IService<CfStockTooReceiveLine> {
    Map<String, Object> getDataByStockByNo(int userId, Map<String, Object> params) throws Exception;

    Map<String, Object> addScanLineData(int userId, Map<String, Object> params) throws Exception;

    Map<String, Object> deleteDataByBarCodeNo(int userId, Map<String, Object> params)  throws Exception;

    Map<String, Object> submitICfStockTooReceiveLineData(int userId, Map<String, Object> params) throws Exception;

    Page getCfStockTooReceiveHeaderPage(int userId, Map<String, Object> params) throws Exception;

    Map<String, Object> updateByStockScanLineData(int userId,  Map<String, Object> params) throws Exception;
}
