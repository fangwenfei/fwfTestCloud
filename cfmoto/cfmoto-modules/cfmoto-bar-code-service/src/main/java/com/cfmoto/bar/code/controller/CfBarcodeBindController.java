package com.cfmoto.bar.code.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.cfmoto.bar.code.model.entity.CfBarcodeBind;
import com.cfmoto.bar.code.model.vo.ThreeCodeBindVo;
import com.cfmoto.bar.code.service.ICfBarcodeBindService;
import com.github.pig.common.util.QueryPage;
import com.github.pig.common.util.R;
import com.github.pig.common.util.UserUtils;
import com.github.pig.common.web.BaseController;
import com.xiaoleilu.hutool.util.StrUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author space
 * @since 2019-02-28
 */
@RestController
@RequestMapping("/cfBarcodeBind")
@Api(tags = " 三码绑定")
@Slf4j
public class CfBarcodeBindController extends BaseController {

    @Autowired
    private ICfBarcodeBindService cfBarcodeBindService;

    /**
     * 通过ID查询
     *
     * @param id ID
     * @return CfBarcodeBind
     */
    @PostMapping("/getById")
    @ApiOperation(value = "通过ID查询")
    public R<CfBarcodeBind> get(@RequestParam Integer id) {
        return new R<>(cfBarcodeBindService.selectById(id));
    }


    /**
     * 分页查询信息
     *
     * @param params 分页对象
     * @return 分页对象
     */
    @PostMapping("/page")
    @ApiOperation(value = "分页查询")
    public R<Page> page(@RequestParam Map<String, Object> params, CfBarcodeBind cfBarcodeBind) {
        return new R<>(cfBarcodeBindService.selectPage(new QueryPage<>(params), new EntityWrapper<>(cfBarcodeBind)));
    }

    /**
     * 添加
     *
     * @param cfBarcodeBind 实体
     * @return success/false
     */
    @PostMapping("/add")
    @ApiOperation(value = "添加")
    public R<Boolean> add(@RequestBody CfBarcodeBind cfBarcodeBind, HttpServletRequest httpServletRequest) {
        try {
            int userId = UserUtils.getUserId(httpServletRequest);
            cfBarcodeBind.setObjectSetBasicAttribute(userId, new Date());
            return new R<>(cfBarcodeBindService.insert(cfBarcodeBind));
        } catch (Exception e) {
            log.error(ExceptionUtils.getFullStackTrace(e));
            return new R<>(R.FAIL, e.getMessage());
        }

    }

    /**
     * 通过条码查询绑定记录
     *
     * @param barCode
     * @return CfBarcodeBind
     */
    @GetMapping("/getBindByBarcode")
    @ApiOperation(value = "通过条码：整车码/发动机/车架码")
    @ApiImplicitParam(name = "barCode", value = "条码：整车码/发动机/车架码", dataType = "string", paramType = "query")
    public R<CfBarcodeBind> get(String barCode) {

        CfBarcodeBind barcodeBind = null;
        try {
            if (StrUtil.isBlank(barCode)) {
                throw new Exception("条码不能为空");
            }
            Wrapper<CfBarcodeBind> entityWrapper = new EntityWrapper<>();
            entityWrapper.eq("car", barCode).or("frame", barCode).or("engine", barCode);
            barcodeBind = cfBarcodeBindService.selectOne(entityWrapper);
            if (barcodeBind == null) {
                throw new Exception("条码" + barCode + "无绑定记录");
            }
        } catch (Exception e) {
            log.error(ExceptionUtils.getFullStackTrace(e));
            return new R(R.FAIL, e.getMessage());
        }
        return new R<CfBarcodeBind>(barcodeBind);
    }


    /**
     * 验证三码
     *
     * @param code
     * @param codeType
     * @return
     */
    @GetMapping("codeValidate")
    @ApiOperation(value = "三码验证")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "条码", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "codeType", value = "条码类型C:整车条码 E:发动机条码 F：车架条码", dataType = "string", paramType = "query")
    })
    public R<ThreeCodeBindVo> codeValidate(@RequestParam String code, @RequestParam String codeType) {

        ThreeCodeBindVo threeCodeBindVo = null;
        try {
            if (StrUtil.isBlank(code)) {
                return new R(R.FAIL, "code参数不能为空");
            }
            if (StrUtil.isBlank(codeType)) {
                return new R(R.FAIL, "codeType参数不能为空");
            }
            threeCodeBindVo = cfBarcodeBindService.codeValidate(code, codeType);
        } catch (Exception e) {
            log.error(ExceptionUtils.getFullStackTrace(e));
            return new R<ThreeCodeBindVo>(R.FAIL, e.getMessage());
        }
        return new R<ThreeCodeBindVo>(threeCodeBindVo);
    }


    /**
     * 验证三码
     * 增加绑定、解绑类型
     *
     * @param code
     * @param codeType
     * @return
     */
    @GetMapping("threeCodeValidate")
    @ApiOperation(value = "三码验证")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "条码", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "codeType", value = "条码类型C:整车条码 E:发动机条码 F：车架条码", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "bindType", value = "绑定类型B:绑定，U:解绑", dataType = "string", paramType = "query")
    })
    public R<ThreeCodeBindVo> threeCodeValidate( String code, String codeType, String bindType ) {

        ThreeCodeBindVo threeCodeBindVo = null;
        try {
            if (StrUtil.isBlank(code)) {
                return new R(R.FAIL, "code参数不能为空");
            }
            if (StrUtil.isBlank(codeType)) {
                return new R(R.FAIL, "codeType参数不能为空");
            }
            if ( StrUtil.isBlank(bindType) ) { //不传默认为绑定
                bindType = "B";
            }
            threeCodeBindVo = cfBarcodeBindService.codeValidate(code, codeType, bindType );
        } catch (Exception e) {
            log.error(ExceptionUtils.getFullStackTrace(e));
            return new R<ThreeCodeBindVo>(R.FAIL, e.getMessage());
        }
        return new R<ThreeCodeBindVo>(threeCodeBindVo);
    }

    @GetMapping("codeBind")
    @ApiOperation(value = "三码绑定")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "carCode", value = "整车条码", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "engineCode", value = "发动机条码", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "frameCode", value = "车架条码", dataType = "string", paramType = "query")
    })
    public R<String> threeCodeBind(String carCode, String engineCode, String frameCode, HttpServletRequest httpServletRequest) {

        try {

            if (StrUtil.isBlank(carCode)) {
                throw new Exception("整车CP码不能为空");
            }
            if (StrUtil.isBlank(engineCode)) {
                throw new Exception("发动机条码不能为空");
            }
            if (StrUtil.isBlank(frameCode)) {
                throw new Exception("车架条码不能为空");
            }
            int userId = UserUtils.getUserId(httpServletRequest);
            cfBarcodeBindService.threeCodeBind(userId, carCode.trim(), engineCode.trim(), frameCode.trim());
        } catch (Exception e) {
            log.error(ExceptionUtils.getFullStackTrace(e));
            return new R(R.FAIL, e.getMessage());
        }
        return new R(R.SUCCESS, "success");
    }

    @GetMapping("codeUnBind")
    @ApiOperation(value = "三码解绑")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "carCode", value = "整车条码", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "engineCode", value = "发动机条码", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "frameCode", value = "车架条码", dataType = "string", paramType = "query")
    })
    public R<CfBarcodeBind> threeCodeUnbind(String carCode, String engineCode, String frameCode, HttpServletRequest httpServletRequest) {

        CfBarcodeBind cfBarcodeBind = null;
        try {
            if (StrUtil.isBlank(carCode) && StrUtil.isBlank(engineCode) && StrUtil.isBlank(frameCode)) {
                throw new Exception("至少一个解绑条码不能为空");
            }
            int userId = UserUtils.getUserId(httpServletRequest);
            cfBarcodeBind = cfBarcodeBindService.threeCodeUnbind(userId, carCode, engineCode, frameCode);
        } catch (Exception e) {
            log.error(ExceptionUtils.getFullStackTrace(e));
            return new R(R.FAIL, e.getMessage());
        }
        return new R(cfBarcodeBind);
    }

    /**
     * 删除
     *
     * @param id ID
     * @return success/false
     */
    @PostMapping("/deleteById")
    @ApiOperation(value = "删除通过ID")
    public R<Boolean> delete(@RequestParam Integer id) {
        CfBarcodeBind cfBarcodeBind = new CfBarcodeBind();
        return new R<>(cfBarcodeBindService.updateById(cfBarcodeBind));
    }

    /**
     * 编辑
     *
     * @param cfBarcodeBind 实体
     * @return success/false
     */
    @PostMapping("/edit")
    @ApiOperation(value = "编辑除")
    public R<Boolean> edit(@RequestBody CfBarcodeBind cfBarcodeBind) {
        return new R<>(cfBarcodeBindService.updateById(cfBarcodeBind));
    }


    /**
     * 验证T-Box
     *
     * @param code     条码
     * @param codeType 条码类型
     * @return
     */
    @GetMapping("tBoxValidate")
    @ApiOperation(value = "T-Box验证")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "条码", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "codeType", value = "条码类型C:整车条码 T:T-Box条码", dataType = "string", paramType = "query")
    })
    public R tBoxCodeValidate(@RequestParam String code, @RequestParam String codeType) {

        try {

            if (StrUtil.isBlank(code)) {
                return new R(R.FAIL, "code参数不能为空");
            }
            if (StrUtil.isBlank(codeType)) {
                return new R(R.FAIL, "codeType参数不能为空");
            }

            //根据传入的条码类型对条码进行校验,校验不通过会抛出异常，通过则不会做任何操作
            cfBarcodeBindService.tBoxValidate(code, codeType);

            return new R(R.SUCCESS, "校验成功！！！");

        } catch (Exception e) {
            log.error(ExceptionUtils.getFullStackTrace(e));
            return new R(R.FAIL, e.getMessage());
        }

    }


    @GetMapping("tBoxBind")
    @ApiOperation(value = "T-Box绑定")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "carCode", value = "整车条码", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "tBoxCode", value = "T-Box条码", dataType = "string", paramType = "query")
    })
    public R threeCodeBind(String carCode, String tBoxCode, HttpServletRequest httpServletRequest) {

        try {

            if (StrUtil.isBlank(carCode)) {
                throw new Exception("整车CP码不能为空");
            }
            if (StrUtil.isBlank(tBoxCode)) {
                throw new Exception("发动机条码不能为空");
            }
            int userId = UserUtils.getUserId(httpServletRequest);

            cfBarcodeBindService.tBoxBind(carCode, tBoxCode, userId);

            return new R(R.SUCCESS, "T-Box绑定成功!!!");

        } catch (Exception e) {
            log.error(ExceptionUtils.getFullStackTrace(e));
            return new R(R.FAIL, e.getMessage());
        }

    }

}
