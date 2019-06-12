package com.cfmoto.bar.code.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.cfmoto.bar.code.model.entity.CfOrderTemp;
import com.cfmoto.bar.code.model.vo.OrderFullVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 订单临时表 Mapper 接口
 * </p>
 *
 * @author space
 * @since 2019-03-12
 */
@Repository
public interface CfOrderTempMapper extends BaseMapper<CfOrderTemp> {

    OrderFullVo getOrderFullVo( @Param("userId") int userId, @Param("orderTempId") String orderTempId );

    Integer insert( CfOrderTemp cfOrderTemp );

}
