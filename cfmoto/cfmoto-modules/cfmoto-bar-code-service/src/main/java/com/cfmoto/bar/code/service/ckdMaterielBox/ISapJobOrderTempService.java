package com.cfmoto.bar.code.service.ckdMaterielBox;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.cfmoto.bar.code.model.dto.SapJobOrderTemp;
import com.cfmoto.bar.code.model.dto.SelectList;
import com.github.pig.common.util.exception.ValidateCodeException;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 模拟通过sap获取生产任务单信息 服务类
 * </p>
 *
 * @author FangWenFei
 * @since 2019-02-27
 */
public interface ISapJobOrderTempService extends IService<SapJobOrderTemp> {

    List<SapJobOrderTemp> getSapJobOrderData(Map<String, Object> params, SapJobOrderTemp sapJobOrderTemp) throws Exception;

    List<SelectList> selectDocumentNoBySapJobOrder(String salesOrder);

}
