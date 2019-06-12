package com.cfmoto.bar.code.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.cfmoto.bar.code.model.entity.CfDeliverGoodsSum;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author space
 * @since 2019-04-08
 */
@Repository
public interface CfDeliverGoodsSumMapper extends BaseMapper<CfDeliverGoodsSum> {

    void saveSumBatch( List<CfDeliverGoodsSum> cfDeliverGoodsSumList );

}
