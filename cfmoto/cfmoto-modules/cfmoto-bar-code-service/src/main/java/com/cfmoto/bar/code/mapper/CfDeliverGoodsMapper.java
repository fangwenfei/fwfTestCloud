package com.cfmoto.bar.code.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.cfmoto.bar.code.model.entity.CfDeliverGoods;
import com.cfmoto.bar.code.model.vo.DeliverGoodsFullVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 部品发货临时表 Mapper 接口
 * </p>
 *
 * @author space
 * @since 2019-04-08
 */
@Repository
public interface CfDeliverGoodsMapper extends BaseMapper<CfDeliverGoods> {

    DeliverGoodsFullVo getDeliverGoodsFullVoByIdAndUserId( @Param( "deliverGoodsId" ) String deliverGoodsId ,@Param( "userId" ) Integer userId );

    Integer customInsert( CfDeliverGoods cfDeliverGoods );

}
