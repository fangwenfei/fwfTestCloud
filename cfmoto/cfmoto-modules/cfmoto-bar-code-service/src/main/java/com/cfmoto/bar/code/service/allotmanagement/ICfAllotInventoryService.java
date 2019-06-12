package com.cfmoto.bar.code.service.allotmanagement;

import com.cfmoto.bar.code.model.entity.CfAllotInventory;
import com.baomidou.mybatisplus.service.IService;

import java.util.List;

/**
 * <p>
 * 调拨清单表 服务类
 * </p>
 *
 * @author FangWenFei
 * @since 2019-04-02
 */
public interface ICfAllotInventoryService extends IService<CfAllotInventory> {

    /**
     * 根据订单号获取调拨清单表数据集合
     *
     * @param orderNo 订单号
     * @return list
     */
    List<CfAllotInventory> getCfAllotInventoryListByOrderNo(String orderNo);

    /**
     * 插入清单表集合到数据库
     *
     * @param userId             用户id
     * @param allotInfoId        信息表id
     * @param allotInventoryList 清单表集合
     */
    void insertInventoryList(int userId, int allotInfoId, List<CfAllotInventory> allotInventoryList);
}
