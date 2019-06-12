package com.cfmoto.bar.code.service.impl.stock;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.cfmoto.bar.code.model.dto.CfStockSplitDto;
import com.cfmoto.bar.code.model.entity.CfProductPickedHandoverScanRecord;
import com.cfmoto.bar.code.model.entity.CfStockInventory;
import com.cfmoto.bar.code.model.entity.CfStockListInfo;
import com.cfmoto.bar.code.model.entity.CfStockSplit;
import com.cfmoto.bar.code.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.bind.ValidationException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 仓库备料交接业务层接口实现类
 *
 * @author ye
 */
@Service
public class CfStorageStockHandoverServiceImpl implements ICfStorageStockHandoverService {

    private static final String BARCODETYPE = "OT";

    @Autowired
    private ICfStockSplitService cfStockSplitService;

    @Autowired
    private ICfStockListInfoService cfStockListInfoService;

    @Autowired
    private ICfProductPickedHandoverScanRecordService cfProductPickedHandoverScanRecordService;

    @Autowired
    private ICfStockInventoryService cfStockInventoryService;

    @Override
    public List<CfStockSplitDto> scan(String barcode) throws ValidationException {
        //根据条码获取备料拆分表中对应数据
        CfStockSplit stockSplit = cfStockSplitService.selectOne(new EntityWrapper<CfStockSplit>().eq("split_no", barcode));
        //判断获取的数据是否为空
        if (stockSplit == null) {
            //抛出异常,并携带错误消息
            throw new ValidationException(CfStockSplitDto.INVALID_BARCODE);
        }

        //根据备料拆分表的备料单号去备料信息表中获取备料仓库
        CfStockListInfo stockListInfo = cfStockListInfoService.selectOne(new EntityWrapper<CfStockListInfo>().eq("stock_list_no", stockSplit.getStockListNo()));
        if (stockListInfo == null) {
            throw new ValidationException(CfStockSplitDto.NO_STOCK_LIST_INFO);
        }
        String stockWarehouse = stockListInfo.getStockRepository();

        //从获取的备料拆分表中获取json格式的数据
        JSONArray jsonArray = JSONArray.parseArray(stockSplit.getBatchNoText());
        List<CfStockSplitDto> dtoList = jsonArray.toJavaList(CfStockSplitDto.class);

        for (CfStockSplitDto dto : dtoList) {
            dto.setStockListNo(stockSplit.getStockListNo());
            dto.setBarcodeType(BARCODETYPE);
            dto.setStockWarehouse(stockWarehouse);
        }
        return dtoList;
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateDataAfterSap(List<CfStockSplitDto> dtoList, String barcode, int userId) throws ValidationException {

        CfStockSplit stockSplit = cfStockSplitService.selectOne(new EntityWrapper<CfStockSplit>().eq("split_no", barcode));

        //更新备料清单表中备料交接数量字段
        //插入交接数据记录在生产领料交接记录表
        for (CfStockSplitDto stockSplitDto : dtoList) {
            CfStockInventory inventory = cfStockInventoryService.selectOne(new EntityWrapper<CfStockInventory>().eq("stock_list_no", stockSplit.getStockListNo()).eq("materials_no", stockSplitDto.getMaterialsNo()));
            if(inventory == null){
                throw new ValidationException("物料代码" + stockSplitDto.getMaterialsNo() + CfStockSplitDto.NO_STOCK_INVENTORY);
            }
            inventory.setStockHandoverNumber(inventory.getStockHandoverNumber().add(new BigDecimal(stockSplitDto.getNumber())));
            inventory.setObjectSetBasicAttributeWhileUpdate(userId, new Date());
            cfStockInventoryService.updateById(inventory);

            CfProductPickedHandoverScanRecord scanRecord = new CfProductPickedHandoverScanRecord();
            scanRecord.setBarcode(barcode);
            scanRecord.setBatchNo(stockSplitDto.getBatchNo());
            scanRecord.setMaterialsNo(stockSplitDto.getMaterialsNo());
            scanRecord.setMaterialsName(stockSplitDto.getMaterialsName());
            scanRecord.setStockListNo(stockSplitDto.getStockListNo());
            scanRecord.setRepository(stockSplitDto.getStockWarehouse());
            scanRecord.setObjectSetBasicAttribute(userId, new Date());
            scanRecord.setStockInventoryId(inventory.getStockInventoryId());
            scanRecord.setStockListId(inventory.getStockListId());
            cfProductPickedHandoverScanRecordService.insert(scanRecord);

        }

        // 删除备料拆分表的父类（如有）和自己
        Integer splitParentId = stockSplit.getSplitParentId();
        CfStockSplit stockSplit1 = cfStockSplitService.selectOne(new EntityWrapper<CfStockSplit>().eq("split_id", splitParentId));
        if (stockSplit1 != null) {
            //删除父类
            cfStockSplitService.deleteById(splitParentId);
        }
        //删除自己
        cfStockSplitService.deleteById(stockSplit);

    }
}
