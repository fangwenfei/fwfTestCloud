package com.cfmoto.bar.code.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.cfmoto.bar.code.model.dto.SelectList;
import com.cfmoto.bar.code.model.entity.CfMaterielBox;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author FangWenFei
 * @since 2019-02-18
 */
public interface CfMaterielBoxMapper extends BaseMapper<CfMaterielBox> {
    /**
     * 获取销售订单
     * @return
     */
    List<SelectList> selectAllSalesOrderNo();

    /**
     * 通过销售订单获取类型单据
     * @return
     */
    List<SelectList> selectDocumentNoBySalesOrderNo(String salesOrderNo );
}
