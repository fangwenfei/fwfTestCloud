package com.cfmoto.bar.code.service;

import com.cfmoto.bar.code.model.entity.CfCostCenterPickOrWithdrawInventory;
import com.baomidou.mybatisplus.service.IService;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author FangWenFei
 * @since 2019-03-20
 */
public interface ICfCostCenterPickOrWithdrawInventoryService extends IService<CfCostCenterPickOrWithdrawInventory> {

    /**
     * 根据单号查询对应的清单表数据集合
     *
     * @param orderNo
     * @return list
     * @author ye
     */
    List<CfCostCenterPickOrWithdrawInventory> getInventoryListByOrderNo(String orderNo);

}
