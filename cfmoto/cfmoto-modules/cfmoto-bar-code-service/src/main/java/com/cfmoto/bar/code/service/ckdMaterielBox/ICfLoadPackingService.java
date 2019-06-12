package com.cfmoto.bar.code.service.ckdMaterielBox;

import com.baomidou.mybatisplus.service.IService;
import com.cfmoto.bar.code.model.dto.SelectList;
import com.cfmoto.bar.code.model.entity.CfLoadPacking;
import com.github.pig.common.util.exception.ValidateCodeException;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author FangWenFei
 * @since 2019-02-26
 */
public interface ICfLoadPackingService extends IService<CfLoadPacking> {

    /**
     * 获取国家
     * @return
     */
    List<SelectList> selectAllSalesOrderNo();

    /**
     * 通过国家获取类型
     * @return
     */
    List<SelectList> selectDocumentNoBySalesOrderNo(String salesOrderNo );


   boolean  deleteByCfLoadPacking(CfLoadPacking cfLoadPacking) throws ValidateCodeException;

   boolean addCfLoadPacking(CfLoadPacking cfLoadPacking) throws Exception;
}
