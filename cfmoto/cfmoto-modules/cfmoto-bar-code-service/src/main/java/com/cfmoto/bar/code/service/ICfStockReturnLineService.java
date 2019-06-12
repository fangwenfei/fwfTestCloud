package com.cfmoto.bar.code.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.cfmoto.bar.code.model.entity.CfStockReturnLine;
import com.baomidou.mybatisplus.service.IService;

import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author FangWenFei
 * @since 2019-03-19
 */
public interface ICfStockReturnLineService extends IService<CfStockReturnLine> {
    Map<String, Object> getDataByStockByNo(int userId, Map<String, Object> params) throws Exception;

    Map<String, Object> addScanLineData(int userId, Map<String, Object> params) throws Exception;

    Map<String, Object> deleteDataByBarCodeNo(int userId,   Map<String, Object> params)  throws Exception;

    Map<String, Object> submitICfStockReturnLineData(int userId, Map<String, Object> params) throws Exception;

    Page getCfStockReturnHeaderPage(int userId, Map<String, Object> params) throws Exception;

}
