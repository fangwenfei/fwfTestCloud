package com.cfmoto.bar.code.service;

import com.github.pig.common.util.R;

import java.util.Map;

/**
 * 成本中心退料业务层接口
 *
 * @author ye
 */
public interface ICfCostCenterWithdrawService {
    /**
     * 提交数据到SAP接口07并获取返回数据
     *
     * @param orderNo   订单号
     * @param expressNo 快递单号
     * @param apiNo     接口号
     * @return JSONObject
     */
    R<Map<String, Object>> commitToSapAndGetReturnData(String orderNo, String expressNo, String apiNo,int userId);

    /**
     * 判断条码类型：
     * OT:条码更改状态为可用
     * CP/EG/KTM:增加库存数量为1（KTM在KTM表），更新仓库信息；
     * 删除成本中心领退扫描记录表中对应账号的对应单号的未提交的行数据、删除清单表。
     *
     * @param orderNo 订单号
     * @param userId  用户ID
     * @param apino   接口号
     */
    void withdraw(String orderNo, int userId, String apino) throws Exception;
}
