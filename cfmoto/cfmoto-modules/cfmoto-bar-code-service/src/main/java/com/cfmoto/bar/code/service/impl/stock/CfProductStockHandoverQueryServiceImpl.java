package com.cfmoto.bar.code.service.impl.stock;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.cfmoto.bar.code.model.entity.CfStockInventory;
import com.cfmoto.bar.code.model.entity.CfStockListInfo;
import com.cfmoto.bar.code.model.vo.CfStockVo;
import com.cfmoto.bar.code.service.ICfProductPickedHandoverScanRecordService;
import com.cfmoto.bar.code.service.ICfProductStockHandoverQueryService;
import com.cfmoto.bar.code.service.ICfStockInventoryService;
import com.cfmoto.bar.code.service.ICfStockListInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.bind.ValidationException;
import java.util.List;

/**
 * 生产备料交接查询业务层接口实现类
 *
 * @author ye
 * @date 2019-04-23
 */
@Service
public class CfProductStockHandoverQueryServiceImpl implements ICfProductStockHandoverQueryService {

    @Autowired
    private ICfStockListInfoService stockListInfoService;

    @Autowired
    private ICfStockInventoryService stockInventoryService;

    private static final String NO_STOCK_INFO_ERROR_MSG = "对应的备料信息数据未找到,请注意!!!";
    private static final String NO_STOCK_INVENTORY_ERROR_MSG = "对应的备料清单数据未找到,请注意!!!";

    @Override

    public CfStockVo getDataByStockListNo(String stockListNo) throws ValidationException {
        //根据备料单号查询信息表数据
        CfStockListInfo info = stockListInfoService.selectOne(new EntityWrapper<CfStockListInfo>().eq("stock_list_no", stockListNo));

        //判断是否查询到信息表数据
        if (info == null) {
            throw new ValidationException("备料单号" + stockListNo + NO_STOCK_INFO_ERROR_MSG);
        }

        CfStockVo stockVo = new CfStockVo();
        stockVo.setStockListInfo(info);

        //根据备料单号查询对应的备料清单表数据
        List<CfStockInventory> stockInventoryList = stockInventoryService.selectList(new EntityWrapper<CfStockInventory>().eq("stock_list_no", stockListNo));

        //判断是否查询到清单表数据
        if (stockInventoryList == null || stockInventoryList.size() == 0) {
            throw new ValidationException("备料单号" + stockListNo + NO_STOCK_INVENTORY_ERROR_MSG);
        }

        stockVo.setStockInventoryList(stockInventoryList);
        return stockVo;
    }
}
