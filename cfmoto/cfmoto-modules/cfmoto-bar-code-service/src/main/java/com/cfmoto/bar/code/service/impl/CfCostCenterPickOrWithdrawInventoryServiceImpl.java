package com.cfmoto.bar.code.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.cfmoto.bar.code.model.entity.CfCostCenterPickOrWithdrawInventory;
import com.cfmoto.bar.code.mapper.CfCostCenterPickOrWithdrawInventoryMapper;
import com.cfmoto.bar.code.service.ICfCostCenterPickOrWithdrawInventoryService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author FangWenFei
 * @since 2019-03-20
 */
@Service
public class CfCostCenterPickOrWithdrawInventoryServiceImpl extends ServiceImpl<CfCostCenterPickOrWithdrawInventoryMapper, CfCostCenterPickOrWithdrawInventory> implements ICfCostCenterPickOrWithdrawInventoryService {


    @Autowired
    private CfCostCenterPickOrWithdrawInventoryMapper inventoryMapper;

    /**
     * 根据单号查询对应的清单表数据集合
     *
     * @param orderNo
     * @return list
     * @author ye
     */
    @Override
    public List<CfCostCenterPickOrWithdrawInventory> getInventoryListByOrderNo(String orderNo) {
        CfCostCenterPickOrWithdrawInventory inventory = new CfCostCenterPickOrWithdrawInventory();
        inventory.setOrderNo(orderNo);
        return inventoryMapper.selectList(new EntityWrapper<>(inventory));
    }

}
