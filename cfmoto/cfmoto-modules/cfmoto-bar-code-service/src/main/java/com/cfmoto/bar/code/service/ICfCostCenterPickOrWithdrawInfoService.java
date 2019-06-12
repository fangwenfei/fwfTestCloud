package com.cfmoto.bar.code.service;

import com.cfmoto.bar.code.model.entity.CfCostCenterPickOrWithdrawInfo;
import com.baomidou.mybatisplus.service.IService;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author FangWenFei
 * @since 2019-03-20
 */
public interface ICfCostCenterPickOrWithdrawInfoService extends IService<CfCostCenterPickOrWithdrawInfo> {

    /**
     * 根据订单号去成本中心领退料信息表中查询数据
     *
     * @param orderNo
     */
    CfCostCenterPickOrWithdrawInfo getByOrderNo(String orderNo);
}
