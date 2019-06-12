package com.cfmoto.bar.code.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.cfmoto.bar.code.model.entity.CfAllotScanRecord;

import java.util.List;

/**
 * <p>
 * 调拨扫描记录表 Mapper 接口
 * </p>
 *
 * @author FangWenFei
 * @since 2019-04-07
 */
public interface CfAllotScanRecordMapper extends BaseMapper<CfAllotScanRecord> {

    void batchUpdateScan( List<CfAllotScanRecord> list );

}
