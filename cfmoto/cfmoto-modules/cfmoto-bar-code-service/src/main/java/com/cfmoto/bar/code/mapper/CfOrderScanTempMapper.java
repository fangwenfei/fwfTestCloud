package com.cfmoto.bar.code.mapper;

import com.cfmoto.bar.code.model.entity.CfOrderScanTemp;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 订单扫描临时表 Mapper 接口
 * </p>
 *
 * @author space
 * @since 2019-03-12
 */
@Repository
public interface CfOrderScanTempMapper extends BaseMapper<CfOrderScanTemp> {

    Integer insert( CfOrderScanTemp cfOrderScanTemp );

}
