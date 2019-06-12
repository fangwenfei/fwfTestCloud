package com.cfmoto.bar.code.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.cfmoto.bar.code.mapper.CfCostCenterPickOrWithdrawInventoryMapper;
import com.cfmoto.bar.code.mapper.CfCostCenterPickOrWithdrawScanRecordMapper;
import com.cfmoto.bar.code.model.entity.CfCostCenterPickOrWithdrawInventory;
import com.cfmoto.bar.code.model.entity.CfCostCenterPickOrWithdrawScanRecord;
import com.cfmoto.bar.code.service.ICfCostCenterPickOrWithdrawScanRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
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
public class CfCostCenterPickOrWithdrawScanRecordServiceImpl extends ServiceImpl<CfCostCenterPickOrWithdrawScanRecordMapper, CfCostCenterPickOrWithdrawScanRecord> implements ICfCostCenterPickOrWithdrawScanRecordService {

    @Autowired
    private CfCostCenterPickOrWithdrawScanRecordMapper scanRecordMapper;

    @Autowired
    private CfCostCenterPickOrWithdrawInventoryMapper inventoryMapper;

    /**
     * 根据id删除扫描记录表中的行数据，更新清单表中实发数量字段，并将更新后的实发数量返回
     *
     * @param id
     * @return map
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteAndGetUpdateNumberById(Integer id, Integer userId, String orderNo) throws Exception {
        //先根据id查询扫描记录表
        CfCostCenterPickOrWithdrawScanRecord scanRecord = scanRecordMapper.selectById(id);

        //根据id删除扫描记录表中的数据
        scanRecordMapper.deleteById(id);

        //根据主键id更新清单表中实发数量字段的值（shouldPickOrWithdrawNumber）
        CfCostCenterPickOrWithdrawInventory inventory = new CfCostCenterPickOrWithdrawInventory();
        inventory.setOrderNo(scanRecord.getOrderNo());
        inventory.setMaterialsNo(scanRecord.getMaterialsNo());
        CfCostCenterPickOrWithdrawInventory newInventory = inventoryMapper.selectOne(inventory);

        if (newInventory == null) {
            throw new Exception("未找到该扫描表数据对应的清单表数据!!!");
        }

        newInventory.setScannedNumber(newInventory.getScannedNumber() - scanRecord.getNumber());

        newInventory.setObjectSetBasicAttribute(userId, new Date());

        inventoryMapper.updateById(newInventory);
    }


    /**
     * 根据id修改扫描记录表对应数据
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updatedById(String orderNo, Integer id, Integer number, String remark, Integer userId) throws Exception {
        //修改扫描记录表中的数量
        CfCostCenterPickOrWithdrawScanRecord scanRecord = scanRecordMapper.selectById(id);

        //记录修改了扫描记录表中多少数量
        Integer updatedNumber = scanRecord.getNumber() - number;

        scanRecord.setNumber(number);

        scanRecord.setObjectSetBasicAttribute(userId, new Date());

        scanRecordMapper.updateById(scanRecord);

        //修改清单表中的实发数量
        CfCostCenterPickOrWithdrawInventory temp = new CfCostCenterPickOrWithdrawInventory();
        temp.setOrderNo(scanRecord.getOrderNo());
        temp.setMaterialsNo(scanRecord.getMaterialsNo());

        CfCostCenterPickOrWithdrawInventory inventory = inventoryMapper.selectOne(temp);

        if (inventory == null) {
            throw new Exception("未找到该扫描表对应的清单表数据!!!");
        }

        inventory.setScannedNumber(inventory.getScannedNumber() - updatedNumber);

        inventory.setObjectSetBasicAttribute(userId, new Date());

        inventoryMapper.updateById(inventory);
    }

    /**
     * 根据单号查找扫描记录表中对应单号的状态为未提交(即为空，N为已提交，不可用)的行数据集合
     *
     * @param orderNo
     * @return List
     */
    @Override
    public List<CfCostCenterPickOrWithdrawScanRecord> getUnCommitedDataByOrderNo(String orderNo, int userId) {

        //拼接查询条件
        EntityWrapper<CfCostCenterPickOrWithdrawScanRecord> wrapper = new EntityWrapper<>();

        wrapper
                .eq("order_no", orderNo)
                .andNew()
                .eq("state", "")
                .or()
                .isNull("state")
                .andNew().eq("created_by", userId);

        return scanRecordMapper.selectList(wrapper);
    }

    @Override
    public List<CfCostCenterPickOrWithdrawScanRecord> getUnCommitedDataByMaterialsNo(String materialsNo, int userId) {
        //拼接查询条件
        EntityWrapper<CfCostCenterPickOrWithdrawScanRecord> wrapper = new EntityWrapper<>();

        wrapper.eq("materials_no", materialsNo)
                .andNew()
                .eq("state", "")
                .or()
                .isNull("state")
                .andNew()
                .eq("created_by", userId);

        return scanRecordMapper.selectList(wrapper);
    }
}
