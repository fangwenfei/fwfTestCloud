package com.cfmoto.bar.code.service;

import com.cfmoto.bar.code.model.vo.CfStockVo;

import javax.xml.bind.ValidationException;

/**
 * 生产备料交接查询业务层接口
 *
 * @author ye
 * @date 2019-04-23
 */
public interface ICfProductStockHandoverQueryService {

    /**
     * 根据备料单号分别查询备料信息表和备料清单表（列表）数据并封装到vo对象中
     *
     * @param stockListNo 备料单号
     * @return CfStockVo
     */
    CfStockVo getDataByStockListNo(String stockListNo) throws ValidationException;

}
