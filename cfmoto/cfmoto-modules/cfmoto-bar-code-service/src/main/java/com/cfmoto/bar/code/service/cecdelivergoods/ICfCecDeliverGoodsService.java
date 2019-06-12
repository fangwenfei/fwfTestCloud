package com.cfmoto.bar.code.service.cecdelivergoods;

import com.cfmoto.bar.code.model.dto.CfCecDeliverGoodsDto;
import com.cfmoto.bar.code.model.entity.CfCecDeliverGoodsInventory;
import com.cfmoto.bar.code.model.entity.CfCecDeliverGoodsScanRecord;
import com.cfmoto.bar.code.model.vo.CfCecDeliverGoodsVo;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 部品网购发货 服务类
 * </p>
 *
 * @author FangWenFei
 * @since 2019-06-04
 */
public interface ICfCecDeliverGoodsService {

    /**
     * 根据交货单号去SAP系统中查找数据，并与本地数据库比较更新后返回最新数据
     *
     * @param deliverOrderNo 交货单号
     * @param userId         用户ID
     * @return CfCecDeliverGoodsVo
     * @throws Exception
     */
    CfCecDeliverGoodsVo getDataByDeliverOrderNo(String deliverOrderNo, int userId) throws Exception;

    /**
     * 插入部品网购发货信息表和清单表数据
     *
     * @param userId         用户ID
     * @param deliverGoodsVo 部品网购发货VO对象
     */
    void insertInfoAndInventory(int userId, CfCecDeliverGoodsVo deliverGoodsVo);

    /**
     * 插入部品网购发货清单表数据到数据库
     *
     * @param userId        用户ID
     * @param inventoryList 清单表集合
     */
    void insertInventoryList(int userId, List<CfCecDeliverGoodsInventory> inventoryList);

    /**
     * 从数据库中加载信息表、清单表、扫描表数据
     *
     * @param deliverOrderNo 交货单号
     * @param userId         用户ID
     * @return CfCecDeliverGoodsVo
     */
    CfCecDeliverGoodsVo getDataFromDataBase(String deliverOrderNo, int userId);


    /**
     * 将查询到的扫描表按单号和物料代码汇总（相同单号、物料代码的扫描数据数量叠加在一起）
     *
     * @param scanRecordList 部品网购发货-扫描表数据
     * @return List 处理好的扫描表数据集合
     */
    List<CfCecDeliverGoodsScanRecord> gatherManyToOne(List<CfCecDeliverGoodsScanRecord> scanRecordList);

    /**
     * 数据库中的清单表数据更新成sap最新数据后，需要根据数据库的扫描表数据来更新清单表的已扫描数量
     *
     * @param deliverOrderNo 交货单
     * @param userId         用户ID
     */
    void updateInventoryByScanList(String deliverOrderNo, Integer userId);

    /**
     * 扫描部品网购发货条码,返回物料批次匹配数据
     *
     * @param dto 数据传输对象
     * @return VO
     * @throws Exception
     */
    CfCecDeliverGoodsVo scanBarcode(CfCecDeliverGoodsDto dto) throws Exception;


    /**
     * 动态批次匹配
     *
     * @param barcodeNumber  条码数量
     * @param list           库存数据
     * @param materialsNo    物料代码
     * @param deliverOrderNo 交货单号
     * @param userId         用户ID
     * @return
     */
    List<Map<String, Object>> dynamicMatchNumberByBarcodeNumber(int barcodeNumber, List<Map<String, Object>> list, String materialsNo, String deliverOrderNo, int userId);

    /**
     * 插入物料批次匹配数据到扫描表并更新清单表
     *
     * @param cfCecDeliverGoodsVo vo对象
     * @param userId              用户ID
     * @throws Exception
     */
    void insertRecordAndUpdateInventory(CfCecDeliverGoodsVo cfCecDeliverGoodsVo, int userId) throws Exception;

    /**
     * 修改扫描记录表数量
     *
     * @param deliverOrderNo 交货单号
     * @param materialsNo    物料代码
     * @param number         修改数量
     * @param total          扫描物料总数
     * @param userId         用户id
     * @throws Exception
     */
    void update(String deliverOrderNo, String materialsNo, Integer number, Integer total, Integer userId) throws Exception;

    /**
     * 删除扫描记录表
     *
     * @param deliverOrderNo 交货单号
     * @param materialsNo    物料代码
     * @param userId         用户id
     * @throws Exception
     */
    void delete(String deliverOrderNo, String materialsNo, Integer userId) throws Exception;

    /**
     * 提交接口，将所有对应单号的数据发送给sap并删除数据库中对应单号的数据
     *
     * @param deliverOrderNo 交货单号
     * @param userId         用户ID
     * @throws Exception
     */
    void finalCommit(String deliverOrderNo, int userId) throws Exception;
}
