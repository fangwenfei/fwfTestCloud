package com.cfmoto.bar.code.service.partsmanage;

import com.baomidou.mybatisplus.service.IService;
import com.cfmoto.bar.code.model.entity.CfPackingList;
import com.cfmoto.bar.code.model.vo.CfAllotManagementVo;

/**
 * <p>
 * 装箱清单数据表 服务类
 * </p>
 *
 * @author space
 * @since 2019-04-09
 */
public interface ICfPackingListService extends IService<CfPackingList> {

    /**
     * 获取调拨单数据
     *
     * @param orderNo
     * @param userId
     * @return
     * @throws Exception
     */
    CfAllotManagementVo getAllocationOrderData(String orderNo, int userId) throws Exception;

    /**
     * 部品装箱功能-扫描行
     *
     * @param orderNo
     * @param barCode
     * @param scanType I-库存，M-物料
     * @param userId
     * @throws Exception
     */
    void scanRowData(String orderNo, String barCode, String scanType, int userId) throws Exception;

    /**
     * 部品装箱功能-关箱
     *
     * @param userId                用户
     * @param orderNo               调拨单
     * @param length                长
     * @param wide                  宽
     * @param high                  高
     * @param weight                毛重
     * @param express               快递公司
     * @param wayBillNo             运单号
     * @param businessStreamOrderNo 商流订单
     * @return CfAllotManagementVo
     * @throws Exception
     */
    CfAllotManagementVo closeCaseNo(int userId, String orderNo, Integer length, Integer wide, Integer high, String weight, String express, String wayBillNo,
                                    String businessStreamOrderNo) throws Exception;

    /**
     * 部品装箱功能-修改行
     *
     * @param orderNo 调拨单
     * @param barCode 条码
     * @param qty     数量
     * @param userId  用户
     * @throws Exception
     */
    void changeRowData(String orderNo, String barCode, String qty, int userId) throws Exception;
}
