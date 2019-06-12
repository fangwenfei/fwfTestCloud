package com.cfmoto.bar.code.controller.allotmanagement;

import cn.hutool.core.util.StrUtil;
import com.cfmoto.bar.code.model.vo.CfAllotManagementVo;
import com.cfmoto.bar.code.service.allotmanagement.ICfAllotManagementCommonService;
import com.cfmoto.bar.code.service.allotmanagement.ICfOneStepAllotService;
import com.github.pig.common.util.R;
import com.github.pig.common.util.UserUtils;
import com.mysql.jdbc.exceptions.MySQLDataException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;

/**
 * 一步调拨  前端控制器
 *
 * @author ye
 * @date 2019-04-08
 */
@RestController
@RequestMapping("allot/oneStep")
@Api(tags = " 一步调拨")
@Log4j
public class CfOneStepAllotController {

    private final Logger logger = LoggerFactory.getLogger(CfOneStepAllotController.class);

    @Autowired
    private ICfOneStepAllotService cfOneStepAllotService;

    @Autowired
    private ICfAllotManagementCommonService cfAllotManagementCommonService;

    /**
     * 一步调拨 → 调拨单
     * 功能概括：根据调拨单经过业务逻辑处理获取调拨信息表、清单表、扫描表数据封装到VO对象中并返回
     * 详细业务逻辑：
     * 1.首先根据调拨单号去SAP接口08获取调拨数据（一条调拨信息数据和多条调拨清单数据）
     * 2.与数据库中数据进行对比
     * 要么插入，要么删除后插入
     * 然后查询数据返回给
     *
     * @param orderNo 调拨单号
     * @param request request对象
     * @return r
     */
    @GetMapping("getDataByOrderNo")
    @ApiOperation(value = "调拨单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderNo", value = "调拨单号", dataType = "string", paramType = "query")
    })
    public R<CfAllotManagementVo> getDataByOrderNo(String orderNo, HttpServletRequest request) {

        //初始化R对象
        R<CfAllotManagementVo> r = new R<>();

        //获取当前登陆用户的ID
        int userId = UserUtils.getUserId(request);

        //校验用户输入的数据
        if (StrUtil.isBlank(orderNo)) {
            r.setErrorAndErrorMsg("请输入有效的数据!!!");
            return r;
        }

        String numberReg = "^[0-9]*$";
        if (!orderNo.matches(numberReg)) {
            r.setErrorAndErrorMsg("一步调拨单格式错误,必须为纯数字!!!");
            return r;
        }

        try {
            CfAllotManagementVo cfAllotManagementVo = cfOneStepAllotService.getDataByOrderNo(orderNo, userId);
            r.setData(cfAllotManagementVo);
        } catch (Exception e) {
            //打印错误日志
            r.setErrorAndErrorMsg(e.getMessage());
            //输出错误信息到控制台上
            e.printStackTrace();
            logger.info(e.getMessage());
        }

        return r;
    }


    /**
     * 扫描条码
     *
     * @param barcode     条码
     * @param barcodeType 条码类型(1:库存条码/2:物料条码)
     * @param orderNo     调拨单号
     * @param wareHouseNo 仓库号
     * @return r
     */
    @PostMapping("scanBarcode")
    @ApiOperation(value = "扫描条码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "barcode", value = "条码", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "barcodeType", value = "条码类型(1:库存条码/2:物料条码)", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "orderNo", value = "调拨单号", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "wareHouseNo", value = "仓库号", dataType = "string", paramType = "query")
    })
    public R<CfAllotManagementVo> scanBarcode(String barcode, String barcodeType, String orderNo, @RequestParam(required = false) String wareHouseNo, HttpServletRequest request) {
        //创建R对象
        R<CfAllotManagementVo> r = new R<>();
        //获取当前用户id
        int userId = UserUtils.getUserId(request);

        //校验数据
        if (StrUtil.isBlank(barcode) || StrUtil.isBlank(barcodeType) || StrUtil.isBlank(orderNo)) {
            r.setErrorAndErrorMsg("请输入有效的数据!!!");
            return r;
        }

        try {
            CfAllotManagementVo vo = cfOneStepAllotService.scanBarcode(barcode, barcodeType, orderNo, wareHouseNo, userId);
            r.setData(vo);
        } catch (Exception e) {
            //打印错误日志
            logger.error(e.getMessage());
            //输出错误信息到控制台
            e.printStackTrace();
            //封装错误信息
            r.setErrorAndErrorMsg(e.getMessage());
        }
        return r;
    }

    /**
     * 提交
     *
     * @param orderNo 调拨单号
     * @param request 请求对象
     * @return r
     */
    @PostMapping("finalCommit")
    @ApiOperation(value = "提交")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderNo", value = "调拨单号", dataType = "string", paramType = "query")
    })
    public R<CfAllotManagementVo> finalCommit(String orderNo, HttpServletRequest request) {
        //创建r对象
        R<CfAllotManagementVo> r = new R<>();
        //校验输入数据
        if (StrUtil.isBlank(orderNo)) {
            r.setErrorAndErrorMsg("请输入有效的数据!!!");
            return r;
        }

        //获取当前用户id
        int userId = UserUtils.getUserId(request);

        try {
            //业务层提交逻辑处理
            String msg = cfOneStepAllotService.finalCommit(orderNo, userId);
            CfAllotManagementVo vo = cfAllotManagementCommonService.getDataFromDataBase(orderNo, "01", userId);
            vo.setOrderStatus(msg);

            //从数据库中拉取数据
            r.setData(vo);

        } catch (Exception e) {
            if (e instanceof SQLException) {
                r.setErrorAndErrorMsg("数据库操作出现异常,请联系管理员!!!");
            }else{
                r.setErrorAndErrorMsg(e.getMessage());
            }
            //打印错误日志
            log.error(e.getMessage());
            //输出错误信息到控制台
            e.printStackTrace();
        }
        return r;
    }

}
