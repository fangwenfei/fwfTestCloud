package com.cfmoto.bar.code.service.allotmanagement;

import com.cfmoto.bar.code.model.entity.CfAllotScanRecord;
import com.baomidou.mybatisplus.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 调拨扫描记录表 服务类
 * </p>
 *
 * @author FangWenFei
 * @since 2019-04-02
 */
public interface ICfAllotScanRecordService extends IService<CfAllotScanRecord> {

    /**
     * 根据调拨单号、操作类型、用户ID查询调拨扫描记录中未提交的数据集合
     *
     * @param orderNo 订单号
     * @param opType  操作类型
     * @param userId  用户id
     * @return list
     */
    List<CfAllotScanRecord> getUncommittedCfAllotScanRecordListByOrderNo(String orderNo, String opType, int userId);

    List<CfAllotScanRecord> getUncommittedCfAllotScanRecordListByMaterialsNo(String materialsNo, String opType, int userId);

    /**
     * 删除扫描表集合
     *
     * @param scanRecords 扫描表集合
     */
    void deleteList(List<CfAllotScanRecord> scanRecords);

    /**
     * 更新扫描表数据:长 宽 高 毛重 运单号 快递公司
     * @param list
     * @return
     */
    void batchUpdateScan( List<CfAllotScanRecord> list );
}
