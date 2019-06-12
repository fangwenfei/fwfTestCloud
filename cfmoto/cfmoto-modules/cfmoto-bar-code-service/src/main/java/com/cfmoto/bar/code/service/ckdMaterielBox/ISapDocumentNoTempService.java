package com.cfmoto.bar.code.service.ckdMaterielBox;


import com.baomidou.mybatisplus.service.IService;
import com.cfmoto.bar.code.model.dto.SapDocumentNoTemp;
import com.cfmoto.bar.code.model.dto.SapJobOrderTemp;
import com.cfmoto.bar.code.model.entity.CfLoadPacking;
import com.github.pig.common.util.R;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 模拟接收sap接收的单据数据量 服务类
 * </p>
 *
 * @author FangWenFei
 * @since 2019-02-27
 */
public interface ISapDocumentNoTempService extends IService<SapDocumentNoTemp> {
    List<CfLoadPacking> insertLoadPacking(int userId, List<SapJobOrderTemp> sapJobOrderTempList)throws Exception;

}
