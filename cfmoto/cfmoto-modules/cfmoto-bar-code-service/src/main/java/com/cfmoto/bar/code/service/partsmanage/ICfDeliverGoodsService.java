package com.cfmoto.bar.code.service.partsmanage;

import com.cfmoto.bar.code.model.entity.CfDeliverGoods;
import com.baomidou.mybatisplus.service.IService;
import com.cfmoto.bar.code.model.vo.DeliverGoodsFullVo;

/**
 * <p>
 * 部品发货临时表 服务类
 * </p>
 *
 * @author space
 * @since 2019-04-08
 */
public interface ICfDeliverGoodsService extends IService<CfDeliverGoods> {


    /**
     * 通过部品表id和用户查询部品表相关联数据
     * @param deliverGoodsId
     * @param userId
     * @return
     * @throws Exception
     */
    DeliverGoodsFullVo getDeliverGoodsFullVoByIdAndUserId( String deliverGoodsId, Integer userId ) throws Exception;

    /**
     * 获取发货单数据
     * @param orderNo
     * @param userId
     * @return
     * @throws Exception
     */
    DeliverGoodsFullVo getDeliverGoodsOrder( String orderNo, Integer userId ) throws Exception;

    /**
     * 发货单提交
     * @param orderNo
     * @param userId
     * @return
     * @throws Exception
     */
    DeliverGoodsFullVo doDeliverGoodsOrderSubmit( String orderNo, int userId ) throws Exception;
}
