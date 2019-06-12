package com.cfmoto.bar.code.service;

import com.cfmoto.bar.code.model.dto.CfStockSplitDto;
import com.cfmoto.bar.code.model.entity.CfAllotScanRecord;
import com.cfmoto.bar.code.model.entity.CfCecDeliverGoodsInventory;
import com.cfmoto.bar.code.model.entity.CfCecDeliverGoodsScanRecord;
import com.cfmoto.bar.code.model.vo.CfAllotManagementVo;
import com.cfmoto.bar.code.model.vo.CfCecDeliverGoodsVo;
import com.cfmoto.bar.code.model.vo.PartsLabelPrintVo;
import com.cfmoto.bar.code.model.vo.ProductionTaskVo;

import javax.xml.bind.ValidationException;
import java.util.List;
import java.util.Map;

/**
 * 调用SAP的接口方法集合
 *
 * @author ye
 */
public interface ISapApiService {
    /**
     * 调用SAP08接口，传入调拨单号得到调拨管理VO对象
     * <p>
     * 二步调拨单传输接口
     *
     * @param orderNo 调拨单号
     * @return jsonObject
     * @throws Exception 调用SA08接口出现的相关异常
     */
    CfAllotManagementVo getDataFromSapApi08(String orderNo) throws Exception;

    /**
     * 根据工厂、仓库代码和物料代码调用SAP10接口，返回list集合对象
     * <p>
     * 仓库下载接口
     *
     * @param factory     工厂
     * @param wareHouseNo 仓库代码
     * @param materialsNo 物料代码
     * @param currentUrl  当前servlet地址
     * @return list
     * @throws Exception
     */
    List<Map<String, Object>> newGetDataFromSapApi10(String wareHouseNo, String materialsNo, String factory, String currentUrl) throws Exception;

    List<Map<String, Object>> newGetDataFromSapApi10(String wareHouseNo, String materialsNo, String factory, String batchNo, String storageArea, String currentUrl) throws Exception;

    /**
     * 二步调拨单调出接口
     *
     * @param scanRecords 扫描数据列表
     * @return vo
     * @throws Exception
     */
    void postDataToSapApi09A(List<CfAllotScanRecord> scanRecords) throws Exception;

    /**
     * 二步调拨单调入接口
     *
     * @param scanRecords 扫描数据列表
     * @throws Exception
     * @retutn vo   vo对象
     */
    CfAllotManagementVo postDataToSapApi09B(List<CfAllotScanRecord> scanRecords) throws Exception;


    /**
     * 一步调拨出入库接口
     *
     * @param orderNo     调拨单号
     * @param scanRecords 扫描记录表集合
     * @throws Exception
     */
    void postDataToSapApi09C(List<CfAllotScanRecord> scanRecords, String orderNo) throws Exception;

    /**
     * 一步调拨传输接口
     *
     * @param orderNo 调拨单号
     * @return
     */
    CfAllotManagementVo getDataToSapApi09D(String orderNo) throws Exception;

    /**
     * 生产领料交接数据接收接口
     *
     * @param dtoList dto集合
     * @throws Exception
     */

    void getDataFromSapApi032(List<CfStockSplitDto> dtoList) throws Exception;


    /**
     * 生产任务单数据下载接口
     *
     * @param orderNo 订单号
     * @return vo
     * @throws Exception
     */
    ProductionTaskVo getDataFromSapApi004(String orderNo) throws Exception;


    /**
     * 销售订单传输接口
     *
     * @param deliverOrderNo 交货单号(发货通知单)
     * @return CfCecDeliverGoodsVo
     * @throws Exception
     */
    CfCecDeliverGoodsVo getDataFromSapApi007(String deliverOrderNo) throws Exception;


    /**
     * 部品网购发货数据提交接口
     *
     * @param deliverOrderNo 交货单号
     * @param scanRecordList 提交数据集合
     * @return
     * @throws Exception
     */
    CfCecDeliverGoodsVo postDataToSapApi006(String deliverOrderNo, List<CfCecDeliverGoodsScanRecord> scanRecordList) throws Exception;

    /**
     * 物料主数据下载接口
     *
     * @param materialsNo   物料编码
     * @param materialsName 物料名称
     * @return List
     * @throws Exception
     */
    List<PartsLabelPrintVo> getDataFromMainData001(String materialsNo, String materialsName) throws ValidationException;
}
