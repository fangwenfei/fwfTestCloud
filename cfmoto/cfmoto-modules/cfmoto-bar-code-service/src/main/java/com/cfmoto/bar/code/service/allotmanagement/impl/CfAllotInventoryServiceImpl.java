package com.cfmoto.bar.code.service.allotmanagement.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.cfmoto.bar.code.mapper.CfAllotInventoryMapper;
import com.cfmoto.bar.code.model.entity.CfAllotInventory;
import com.cfmoto.bar.code.service.allotmanagement.ICfAllotInventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 调拨清单表 服务实现类
 * </p>
 *
 * @author FangWenFei
 * @since 2019-04-02
 */
@Service
public class CfAllotInventoryServiceImpl extends ServiceImpl<CfAllotInventoryMapper, CfAllotInventory> implements ICfAllotInventoryService {

    @Autowired
    private CfAllotInventoryMapper allotInventoryMapper;

    /**
     * 根据订单号查询调拨清单表集合数据
     *
     * @param orderNo 订单号
     * @return list
     */
    @Override
    public List<CfAllotInventory> getCfAllotInventoryListByOrderNo(String orderNo) {
        EntityWrapper<CfAllotInventory> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("order_no", orderNo);
        return allotInventoryMapper.selectList(entityWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void insertInventoryList(int userId, int allotInfoId, List<CfAllotInventory> allotInventoryList) {
        for (CfAllotInventory cfAllotInventory : allotInventoryList) {
            cfAllotInventory.setObjectSetBasicAttribute(userId, new Date());
            cfAllotInventory.setAllotInfoId(allotInfoId);
            allotInventoryMapper.insert(cfAllotInventory);
        }
    }
}
