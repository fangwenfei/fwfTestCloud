package com.cfmoto.bar.code.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.cfmoto.bar.code.model.entity.CfBarcodeInventory;
import com.cfmoto.bar.code.model.vo.EnginePutVo;
import com.cfmoto.bar.code.model.vo.ProductionTaskVo;
import com.github.pig.common.util.R;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 条形码库存表 服务类
 * </p>
 *
 * @author FangWenFei
 * @since 2019-03-05
 */
public interface ICfBarcodeInventoryService extends IService<CfBarcodeInventory> {

    //发送机关联入库
    ProductionTaskVo engineWarehousing(int userId, EnginePutVo enginePutVo) throws Exception;

    //根据条形码查询

    /**
     * 根据条码查询对应信息，查不到则报错提示：条码不正确，请注意！
     *
     * @param barCode 条码
     * @return r
     * @author ye
     */
    R<CfBarcodeInventory> getByBarcode(String barCode) throws Exception;

    /**
     * 根据条码更新条码数量，只可调小，不可调大
     *
     * @param barcode       条码
     * @param barcodeNumber 条码数量
     * @param userId        用户ID
     * @return r
     * @author ye
     */
    R<CfBarcodeInventory> updateNumberByBarcode(String barcode, Integer barcodeNumber, Integer userId) throws Exception;

    void reduceInventoryQtyByBarcode(int userId, String barcode, BigDecimal qty);

    /**
     * 批量减少库存数量
     *
     * @param mapList
     */
    void reduceInventoryQtyByBarcodeList(List mapList);

    Page pageByLikeParam(Map<String, Object> params, CfBarcodeInventory cfBarcodeInventory);

    R getSapDataByParam(Map<String, Object> params) throws Exception;

    /*
      拆分打印生产入库标签打印（非整车、发动机）（PC）
     */
    List<CfBarcodeInventory> splitDataProducePrintByParam(Map<String, Object> params, int userId) throws Exception;

    /*
   拆分打印生产入库标签打印（整车、发动机）（PC）
  */
    List<CfBarcodeInventory> splitDataProducePrintByParamCP(Map<String, Object> params, int userId) throws Exception;

    /**
     * 根据用户id和物料代码获取sap的库存信息
     *
     * @param materialsNo 物料代码
     * @param warehouse   仓库（非必传）
     * @param userId      用户ID
     * @param currentUrl  当前url
     * @return list
     * @throws Exception
     */
    List<CfBarcodeInventory> getInventoryFromSap(String materialsNo, String warehouse, Integer userId, String currentUrl) throws Exception;
}
