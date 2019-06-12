package com.cfmoto.bar.code.mapper;

import com.cfmoto.bar.code.model.entity.CfPackingList;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 装箱清单数据表 Mapper 接口
 * </p>
 *
 * @author space
 * @since 2019-04-09
 */
@Repository
public interface CfPackingListMapper extends BaseMapper<CfPackingList> {

    void packListInsertOrSaveBatch( List<CfPackingList> cfPackingListList );

}
