package com.cfmoto.bar.code.service;

import com.baomidou.mybatisplus.service.IService;
import com.cfmoto.bar.code.model.entity.CfCostCenterPickOrWithdrawScanRecord;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author FangWenFei
 * @since 2019-03-20
 */
public interface ICfCostCenterPickOrWithdrawScanRecordService extends IService<CfCostCenterPickOrWithdrawScanRecord> {

    /**
     * 根据id删除扫描记录表中的行数据，更新清单表中实发数量字段，并将更新后的实发数量返回
     *
     * @param id
     * @return map
     */
    void deleteAndGetUpdateNumberById(Integer id, Integer userId, String orderNo) throws Exception;

    void updatedById(String orderNo, Integer id, Integer number, String remark, Integer userId) throws Exception;


    /**
     * 根据单号查找扫描记录表中对应单号的状态为未提交的行数据集合
     *
     * @param orderNo
     * @return List
     * @author ye
     */
    List<CfCostCenterPickOrWithdrawScanRecord> getUnCommitedDataByOrderNo(String orderNo,int userId);


    List<CfCostCenterPickOrWithdrawScanRecord> getUnCommitedDataByMaterialsNo(String materialsNo,int userId);
}
