package com.cfmoto.bar.code.service.allotmanagement;

import com.cfmoto.bar.code.model.vo.CfAllotManagementVo;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 调拨管理通用service接口
 *
 * @author ye
 */
public interface ICfAllotManagementCommonService {

    /**
     * 根据调拨单经过业务处理得到所需调拨信息、清单、扫描表数据
     *
     * @param orderNo 调拨单
     * @param opType  操作类型
     * @param userId  用户ID
     * @return cfAllotManagementVo
     * @throws Exception 数据库相关或调用Sap相关的异常
     */
    CfAllotManagementVo getDataByOrderNo(String orderNo, String opType, int userId) throws Exception;

    /**
     * 从条码系统的数据库中获取所需的调拨信息、清单、扫描表数据
     *
     * @param orderNo 调拨单号
     * @param opType  操作类型
     * @param userId  用户ID
     * @return cfAllotManagementVo
     */
    CfAllotManagementVo getDataFromDataBase(String orderNo, String opType, int userId);


    /**
     * 插入信息表和清单表
     *
     * @param userId              用户ID
     * @param cfAllotManagementVo 从sap获取的调拨管理vo对象
     */
    void insertAllotInfoAndInventory(int userId, CfAllotManagementVo cfAllotManagementVo);

    /**
     * 删除数据库的清单和扫描表数据
     *
     * @param orderNo 调拨单号
     */
    void deleteAllotInfoAndInventory(String orderNo);

    /**
     * 删除调拨扫描记录表行数据、更新调拨清单表数量数据
     *
     * @param orderNo 调拨单号
     * @param id      行id
     * @param opType  接口号
     * @param userId  当前用户id
     * @return vo
     */
    void deleteRow(String orderNo, Integer id, String opType, int userId) throws Exception;

    /**
     * 部品扫描表数据删除
     *
     * @param orderNo 调拨单
     * @param id      扫描标识
     * @param packNo  包号
     * @param userId  用户
     * @throws Exception
     */
    void scanDeleteRow(String orderNo, Integer id, String packNo, int userId) throws Exception;

    /**
     * 二步调拨出库和一步调拨的物料批次自动匹配功能
     *
     * @param barcodeNumber 条码数量
     * @param opType        操作类型
     * @param list          sap的数据集
     * @param materialsNo   物料代码
     * @return
     */
    List<Map<String, Object>> dynamicMatchNumberByBarcodeNumber(int barcodeNumber, String opType, List<Map<String, Object>> list, String materialsNo,int userId);

    /**
     * 插入扫描表和更新清单表
     *
     * @param orderNo     调拨单号
     * @param opType      操作类型
     * @param materialsNo 物料代码
     * @param barcode     条码
     * @param list        批次匹配数据
     * @param userId      用户id
     */
    void insertRecordAndUpdateInventory(String orderNo, String opType, String materialsNo, String barcode, List<Map<String, Object>> list, int userId);


    /**
     * 二步调拨入库的自动匹配功能
     *
     * @param barcodeNumber 条码数量
     * @param materialsNo   物料条码
     * @param orderNo       调拨单号
     * @return
     */
    List<Map<String, Object>> dynamicMatchNumberByBarcodeNumberFoAllotIn(int barcodeNumber, String materialsNo, String orderNo) throws Exception;

    /**
     * 二步调拨入库用的插入扫描表和更新清单表
     *
     * @param orderNo
     * @param opType
     * @param materialsNo
     * @param barcode
     * @param list
     * @param userId
     * @return msg 物料批次匹配结果
     * @throws Exception
     */
    void insertRecordAndUpdateInventoryForAllotIn(String orderNo, String opType, String materialsNo, String barcode, List<Map<String, Object>> list, int userId) throws Exception;

    /**
     * 根据调拨单号删除清单表和扫描表
     *
     * @param orderNo 调拨单号
     */
    void deleteAllotInventoryAndScanRecord(String orderNo);

    /**
     * 删除扫描表行数据，更新清单表数据
     *
     * @param orderNo     单号
     * @param barcode     条码
     * @param materialsNo 物料代码
     * @param apiNo       接口号（1:库存 2:物料）
     * @param opType      操作类型(01:两步出库，02：两步入库，03：一步调拨)
     * @param userId      用户id
     * @throws Exception
     */
    void delete(String orderNo, String barcode, String materialsNo, String apiNo, String opType, Integer userId) throws Exception;

    /**
     * 根据单号、物料代码、操作类型、用户id获取扫描表中扫描数量总数
     *
     * @param orderNo     单号
     * @param materialsNo 物料代码
     * @param opType      操作类型
     * @param userId      用户ID
     * @return total
     */
    Integer getTotal(String orderNo, String materialsNo, String opType, Integer userId) throws Exception;

    /**
     * 修改
     *
     * @param orderNo     单号
     * @param barcode     条码
     * @param materialsNo 物料代码
     * @param opType      操作类型
     * @param apiNo       接口号
     * @param number      数量
     * @param userId      用户ID
     */
    void update(String orderNo, String barcode, String materialsNo, String opType, String apiNo, Integer number, Integer total, Integer userId) throws Exception;

    void updateInventoryByScanList(String orderNo, Integer userId);

    /**
     * 两步调拨更新清单表数量
     *
     * @param orderNo 单号
     * @param userId  用户id
     */
    void updateInventoryByScanListForTwoStep(String orderNo, Integer userId);
}
