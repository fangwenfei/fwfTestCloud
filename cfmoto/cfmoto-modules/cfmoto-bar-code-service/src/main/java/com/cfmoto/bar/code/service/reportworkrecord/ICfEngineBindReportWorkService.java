package com.cfmoto.bar.code.service.reportworkrecord;

import com.cfmoto.bar.code.model.vo.ProductionTaskVo;

/**
 * 发动机绑定报工业务层接口
 *
 * @author yezi
 * @date 2019/6/11
 */
public interface ICfEngineBindReportWorkService {
    /**
     * 1.调用SAP004接口获取订单数量
     * 2.根据单号从数据库中查找已绑定数量，并计算出待绑定数量
     *
     * @param productOrderNo 生产订单号
     * @return ProductionTaskVo
     * @throws Exception
     */
    ProductionTaskVo productOrder(String productOrderNo) throws Exception;

    /**
     * 1.查找库存表中是否有此条码
     * a.存在弹出报错确认框“该条码存在绑定记录，请确定！”
     * b.不存在，输入插入到库存表，状态为N。
     *
     * @param productionTaskVo 生产任务单相关数据
     * @param userId           用户ID
     * @throws Exception
     */
    void engineBarcodeScan(ProductionTaskVo productionTaskVo, Integer userId) throws Exception;

    /**
     * 1、校验条码是否存在库存；
     * 不存在报错“条码不存在”；
     * 存在，校验状态是否为N，不为N报错“条码有入库记录，不能解绑”，为N，删除库存表数据，删除报工记录表数据
     *
     * @param engineBarcode 发动机条码
     */
    void unbindEngineBarcode(String engineBarcode) throws Exception;
}
