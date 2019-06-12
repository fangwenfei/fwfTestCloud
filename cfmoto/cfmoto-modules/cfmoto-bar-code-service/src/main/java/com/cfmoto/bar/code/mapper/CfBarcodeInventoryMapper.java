package com.cfmoto.bar.code.mapper;

import com.cfmoto.bar.code.model.entity.CfBarcodeInventory;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 条形码库存表 Mapper 接口
 * </p>
 *
 * @author FangWenFei
 * @since 2019-03-05
 */
@Repository
public interface CfBarcodeInventoryMapper extends BaseMapper<CfBarcodeInventory> {

    Integer reduceInventoryQtyByBarcode(@Param( "userId" ) int userId, @Param( "barcode" )String barcode, @Param( "qty" )BigDecimal qty,
                                        @Param( "lastUpdateDate" )Date lastUpdateDate );

    Integer reduceInventoryQtyByBarcodeList( @Param( "list" ) List mapList );
}
