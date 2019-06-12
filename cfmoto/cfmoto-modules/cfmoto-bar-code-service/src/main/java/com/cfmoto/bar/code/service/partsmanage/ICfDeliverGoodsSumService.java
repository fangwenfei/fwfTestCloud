package com.cfmoto.bar.code.service.partsmanage;

import com.cfmoto.bar.code.model.entity.CfDeliverGoodsSum;
import com.baomidou.mybatisplus.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author space
 * @since 2019-04-08
 */
public interface ICfDeliverGoodsSumService extends IService<CfDeliverGoodsSum> {

    void saveSumBatch( List<CfDeliverGoodsSum> cfDeliverGoodsSumList );
}
