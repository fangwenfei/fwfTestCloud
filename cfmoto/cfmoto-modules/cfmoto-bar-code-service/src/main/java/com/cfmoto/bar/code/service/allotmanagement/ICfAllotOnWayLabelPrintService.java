package com.cfmoto.bar.code.service.allotmanagement;

import com.cfmoto.bar.code.model.bo.CfAllotOnWayDataBo;
import com.cfmoto.bar.code.model.entity.CfAllotOnWayData;
import com.cfmoto.bar.code.model.entity.CfAllotOnWayLabel;

import java.util.List;

/**
 * 调拨在途标签打印业务层接口
 *
 * @author ye
 */
public interface ICfAllotOnWayLabelPrintService {
    /**
     * 根据调拨单号查询在途数据表中数据
     *
     * @param orderNo 调拨单号
     * @return
     */
    List<CfAllotOnWayData> getOnWayByOrderNo(String orderNo);

    /**
     * 打印（拆分并生成在途标签号：条码，插入到库存表中并返回给pda）
     *
     * @param bo     在途bo对象
     * @param userId 用户id
     * @return
     * @throws InterruptedException
     */
    List<CfAllotOnWayLabel> print(CfAllotOnWayDataBo bo, int userId) throws Exception;
}
