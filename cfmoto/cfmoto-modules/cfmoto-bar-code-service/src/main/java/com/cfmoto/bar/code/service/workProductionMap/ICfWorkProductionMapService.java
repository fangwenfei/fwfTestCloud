package com.cfmoto.bar.code.service.workProductionMap;

import com.cfmoto.bar.code.model.dto.SelectList;
import com.cfmoto.bar.code.model.entity.CfWorkProductionMap;
import com.baomidou.mybatisplus.service.IService;
import com.cfmoto.bar.code.model.vo.OperationWorkVo;
import com.github.pig.common.util.exception.ValidateCodeException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author FangWenFei
 * @since 2019-06-10
 */
public interface ICfWorkProductionMapService extends IService<CfWorkProductionMap> {
    List<SelectList> selectWorkProductionMapByAll();

    OperationWorkVo submitAllData(String barCode, String workNo , HttpServletRequest request) throws ValidateCodeException;

    OperationWorkVo submitAllDataThreeBarCode(String barCode, String workNo , HttpServletRequest request) throws ValidateCodeException;

}
