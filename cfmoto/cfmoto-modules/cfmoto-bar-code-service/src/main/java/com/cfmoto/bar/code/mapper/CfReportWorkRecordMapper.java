package com.cfmoto.bar.code.mapper;

import com.cfmoto.bar.code.model.entity.CfReportWorkRecord;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import feign.Param;

/**
 * <p>
 * 扫描报工记录表 Mapper 接口
 * </p>
 *
 * @author FangWenFei
 * @since 2019-06-11
 */
public interface CfReportWorkRecordMapper extends BaseMapper<CfReportWorkRecord> {

    /**
     * 根据生产订单号查询对应数据条数
     *
     * @param productTaskOrder 生产订单号
     * @return 数据条数
     */
    Integer getTotalByProductTaskOrder(@Param("productTaskOrder") String productTaskOrder);
}
