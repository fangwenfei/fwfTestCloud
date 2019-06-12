package com.cfmoto.bar.code.service.ckdMaterielBox;

import com.cfmoto.bar.code.model.entity.CfLocationBinding;
import com.baomidou.mybatisplus.service.IService;
import com.github.pig.common.util.exception.ValidateCodeException;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

/**
 * <p>
 * 物料条码和货位条码绑定 服务类
 * </p>
 *
 * @author FangWenFei
 * @since 2019-01-24
 */
public interface ICfLocationBindingService extends IService<CfLocationBinding> {

  Boolean  locationBindingToSap(Map<String, Object> params,int userId) throws ValidateCodeException ,Exception;

    Map<String,Object> getDetailByBarcode(Map<String, Object> params,int userId) throws ValidateCodeException,Exception;

}
