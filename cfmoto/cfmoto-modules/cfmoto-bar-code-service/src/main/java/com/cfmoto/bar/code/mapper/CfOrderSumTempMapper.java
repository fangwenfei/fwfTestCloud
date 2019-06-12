package com.cfmoto.bar.code.mapper;

import com.cfmoto.bar.code.model.entity.CfOrderSumTemp;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 订单汇总临时表 Mapper 接口
 * </p>
 *
 * @author space
 * @since 2019-03-12
 */
@Repository
public interface CfOrderSumTempMapper extends BaseMapper<CfOrderSumTemp> {

    void insertDataByBatch( List<CfOrderSumTemp> cfOrderSumTempList );

}
