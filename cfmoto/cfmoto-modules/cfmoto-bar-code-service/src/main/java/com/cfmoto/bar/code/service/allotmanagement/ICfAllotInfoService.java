package com.cfmoto.bar.code.service.allotmanagement;

import com.cfmoto.bar.code.model.entity.CfAllotInfo;
import com.baomidou.mybatisplus.service.IService;

/**
 * <p>
 * 调拨信息表 服务类
 * </p>
 *
 * @author FangWenFei
 * @since 2019-04-02
 */
public interface ICfAllotInfoService extends IService<CfAllotInfo> {

    /**
     * 根据调拨单号查询调拨信息表数据(一个单号对应一条数据)
     *
     * @param orderNo 调拨单号
     * @return
     */
    CfAllotInfo getAllotInfoByOrderNo(String orderNo);

    /**
     * 插入调拨信息表数据
     *
     * @param cfAllotInfo 调拨信息表数据
     */
    void insertAllotInfo(CfAllotInfo cfAllotInfo);
}
