package com.cfmoto.bar.code.service.ckdMaterielBox;

import com.baomidou.mybatisplus.service.IService;
import com.cfmoto.bar.code.model.dto.SelectList;
import com.cfmoto.bar.code.model.entity.CfMaterielBox;
import com.github.pig.common.util.R;
import com.github.pig.common.util.exception.ValidateCodeException;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author FangWenFei
 * @since 2019-02-18
 */
public interface ICfMaterielBoxService extends IService<CfMaterielBox> {

    Map<String, Object> addMaterielBox(Map<String, Object> params, int userId) throws Exception;

    Map<String, Object> barCodeVerification(Map<String, Object> param) throws ValidateCodeException;

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

     ArrayList< Map<String,String>> getDataToSap(ArrayList< Map<String,String>> resultData, String  parentNo, String lumpNo,String salesOrder);
}
