package com.cfmoto.bar.code.service;

import com.cfmoto.bar.code.model.dto.SelectList;
import com.cfmoto.bar.code.model.entity.CfCabinet;
import com.baomidou.mybatisplus.service.IService;
import com.github.pig.common.util.R;
import com.github.pig.common.util.exception.ValidateCodeException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 柜子信息 服务类
 * </p>
 *
 * @author FangWenFei
 * @since 2019-02-20
 */
public interface ICfCabinetService extends IService<CfCabinet> {
    R<Map<String, Object>> addCabinet(Map<String, Object> params, HttpServletRequest httpServletRequest) throws Exception;

    /**
     * 通过发货通知单获取销售订单list
     * @param userId
     * @param sendGoodsNo
     * @return
     */
    SelectList selectSalesOrderListBySendGoodsNo(int userId, String sendGoodsNo) throws Exception;

}
