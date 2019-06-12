package com.cfmoto.bar.code.service.allotmanagement.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.cfmoto.bar.code.mapper.CfAllotScanRecordMapper;
import com.cfmoto.bar.code.model.entity.CfAllotScanRecord;
import com.cfmoto.bar.code.service.allotmanagement.ICfAllotScanRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 调拨扫描记录表 服务实现类
 * </p>
 *
 * @author FangWenFei
 * @since 2019-04-02
 */
@Service
public class CfAllotScanRecordServiceImpl extends ServiceImpl<CfAllotScanRecordMapper, CfAllotScanRecord> implements ICfAllotScanRecordService {

    @Autowired
    private CfAllotScanRecordMapper cfAllotScanRecordMapper;

    /**
     * 根据订单号获取调拨扫描记录表中未提交的集合数据
     *
     * @param orderNo 订单号
     * @return list
     */
    @Override
    public List<CfAllotScanRecord> getUncommittedCfAllotScanRecordListByOrderNo(String orderNo, String opType, int userId) {

        EntityWrapper<CfAllotScanRecord> entityWrapper = new EntityWrapper<>();
        //封装查询条件
        //查找未提交的数据
        entityWrapper.eq("order_no", orderNo).andNew().eq("state", "" ).or().isNull("state");

        //动态根据传入的参数封装查询条件
        if (null != opType && !"".equals(opType)) {

            entityWrapper.andNew().eq("operate_type", opType);
        }

        if (userId != 0) {

            entityWrapper.andNew().eq("created_by", userId);
        }

        return cfAllotScanRecordMapper.selectList(entityWrapper);

    }

    @Override
    public List<CfAllotScanRecord> getUncommittedCfAllotScanRecordListByMaterialsNo(String materialsNo, String opType, int userId) {
        EntityWrapper<CfAllotScanRecord> entityWrapper = new EntityWrapper<>();
        //封装查询条件
        //查找未提交的数据
        entityWrapper.eq("materials_no", materialsNo).andNew().eq("state", "" ).or().isNull("state");

        //动态根据传入的参数封装查询条件
        if (null != opType && !"".equals(opType)) {

            entityWrapper.andNew().eq("operate_type", opType);
        }

        if (userId != 0) {

            entityWrapper.andNew().eq("created_by", userId);
        }

        return cfAllotScanRecordMapper.selectList(entityWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteList(List<CfAllotScanRecord> scanRecords) {
        for (CfAllotScanRecord scanRecord : scanRecords) {
            cfAllotScanRecordMapper.deleteById(scanRecord.getAllotScanRecordId());
        }
    }

    /**
     * 更新扫描表数据:长 宽 高 毛重 运单号 快递公司
     * @param list
     * @return
     */
    @Transactional( rollbackFor = Exception.class )
    @Override
    public void batchUpdateScan( List<CfAllotScanRecord> list ) {
        cfAllotScanRecordMapper.batchUpdateScan( list );
    }
}
