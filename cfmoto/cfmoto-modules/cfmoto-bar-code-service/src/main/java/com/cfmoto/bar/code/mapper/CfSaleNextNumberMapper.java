package com.cfmoto.bar.code.mapper;

import com.cfmoto.bar.code.model.entity.CfSaleNextNumber;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 获取销售订单下一编号 Mapper 接口
 * </p>
 *
 * @author space
 * @since 2019-04-12
 */
@Repository
public interface CfSaleNextNumberMapper extends BaseMapper<CfSaleNextNumber> {

    CfSaleNextNumber selectBySaleOrderForUpdate( @Param( "saleOrder") String saleOrder );

}
