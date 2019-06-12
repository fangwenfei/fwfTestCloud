package com.cfmoto.bar.code.service;

import com.baomidou.mybatisplus.service.IService;
import com.cfmoto.bar.code.model.entity.CfBarcodeBind;
import com.cfmoto.bar.code.model.vo.ThreeCodeBindVo;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author space
 * @since 2019-02-28
 */
public interface ICfBarcodeBindService extends IService<CfBarcodeBind> {

    ThreeCodeBindVo codeValidate(String code, String codeType) throws Exception;

    ThreeCodeBindVo codeValidate(String code, String codeType, String bindType) throws Exception;

    ThreeCodeBindVo validateCP(String code) throws Exception;

    ThreeCodeBindVo validateEG(String code) throws Exception;

    ThreeCodeBindVo validateFR(String code) throws Exception;

    /**
     * T-Box验证：
     * CP类型条码有效性验证
     *
     * @param code 条码
     * @throws Exception
     */
    void validateTBoxCP(String code) throws Exception;

    /**
     * T-Box验证：
     * T-Box类型条码有效性验证
     *
     * @param code 条码
     * @throws Exception
     */
    void validateTBox(String code) throws Exception;

    /**
     * 三码绑定接口
     *
     * @param userId
     * @param carCode
     * @param engineCode
     * @param frameCode
     * @throws Exception
     */
    void threeCodeBind(Integer userId, String carCode, String engineCode, String frameCode) throws Exception;


    /**
     * 三码解绑
     *
     * @param userId
     * @param carCode
     * @param engineCode
     * @param frameCode
     * @return
     * @throws Exception
     */
    CfBarcodeBind threeCodeUnbind(Integer userId, String carCode, String engineCode, String frameCode) throws Exception;

    /**
     * T-Box条码验证
     *
     * @param code     条码
     * @param codeType 条码类型
     * @throws Exception
     */
    void tBoxValidate(String code, String codeType) throws Exception;

    /**
     * T-Box绑定
     *
     * @param carCode  整车CP条码
     * @param tBoxCode T-Box条码
     * @param userId   用户ID
     * @throws Exception
     */
    void tBoxBind(String carCode, String tBoxCode, int userId) throws Exception;
}
