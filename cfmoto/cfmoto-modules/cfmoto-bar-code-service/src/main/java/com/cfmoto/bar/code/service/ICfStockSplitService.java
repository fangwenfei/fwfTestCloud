package com.cfmoto.bar.code.service;

import com.cfmoto.bar.code.model.entity.CfStockSplit;
import com.baomidou.mybatisplus.service.IService;
import com.github.pig.common.util.exception.ValidateCodeException;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 备料拆分 服务类
 * </p>
 *
 * @author FangWenFei
 * @since 2019-04-22
 */
public interface ICfStockSplitService extends IService<CfStockSplit> {
    Map<String, Object> splitCfStock( Map<String, Object> params,int userId) throws Exception;

    List<CfStockSplit> getCfStockSplit(CfStockSplit cfStockSplit);
}
