package com.cfmoto.bar.code.controller.cecdelivergoods;

import cn.hutool.core.util.StrUtil;
import com.cfmoto.bar.code.model.dto.CfCecDeliverGoodsDto;
import com.cfmoto.bar.code.model.vo.CfAllotManagementVo;
import com.cfmoto.bar.code.model.vo.CfCecDeliverGoodsVo;
import com.cfmoto.bar.code.service.cecdelivergoods.ICfCecDeliverGoodsService;
import com.cfmoto.bar.code.utiles.ValidateUtils;
import com.github.pig.common.util.R;
import com.github.pig.common.util.UserUtils;
import com.github.pig.common.web.BaseController;
import com.xiaoleilu.hutool.util.NumberUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 部品网购发货 前端控制器
 * </p>
 *
 * @author FangWenFei
 * @since 2019-06-04
 */
@RestController
@RequestMapping("/deliverGoods")
@Api(tags = " 部品网购发货")
@Log4j
public class CfCecDeliverGoodsController extends BaseController {
    @Autowired
    private ICfCecDeliverGoodsService deliverGoodsService;

    private ValidateUtils<String> validateUtils = new ValidateUtils<>();

    /**
     * 根据交货单号下载数据
     *
     * @param deliverOrderNo 交货单号
     * @param request        请求对象
     * @return R
     */
    @GetMapping("/getDataByDeliverOrderNo")
    @ApiOperation(value = "根据交货单下载数据")
    @ApiImplicitParams({@ApiImplicitParam(name = "deliverOrderNo", value = "交货单号", dataType = "string", paramType = "query")})
    public R<CfCecDeliverGoodsVo> getDataByDeliverOrderNo(@RequestParam() String deliverOrderNo, HttpServletRequest request) {
        //校验数据
        if (StrUtil.isBlank(StrUtil.trim(deliverOrderNo))) {
            return new R<>(R.FAIL, "交货单号不能为空!!!");
        }

        try {
            //调用业务层处理-从sap获取数据并与数据库数据比对后返回给前端进行显示
            CfCecDeliverGoodsVo vo = deliverGoodsService.getDataByDeliverOrderNo(deliverOrderNo, UserUtils.getUserId(request));
            return new R<>(vo);
        } catch (Exception e) {
            log.error(ExceptionUtils.getFullStackTrace(e));
            return new R<>(R.FAIL, e.getMessage());
        }

    }

    /**
     * 扫描条码并返回物料批次匹配结果
     *
     * @param deliverOrderNo 交货单号
     * @param barcode        条码
     * @param warehouse      仓库
     * @param trackingNo     运单号
     * @param request        请求对象
     * @return
     */
    @GetMapping("/scanBarcode")
    @ApiOperation(value = "扫描条码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deliverOrderNo", value = "交货单号", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "barcode", value = "条码", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "warehouse", value = "仓库", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "trackingNo", value = "运单号(非必输)", dataType = "string", paramType = "query")
    })
    public R<CfCecDeliverGoodsVo> scanBarcode(String deliverOrderNo, String barcode, String warehouse, @RequestParam(required = false, defaultValue = "") String trackingNo, HttpServletRequest request) {
        //校验输入数据有效性
        if (StrUtil.isBlank(StrUtil.trim(deliverOrderNo))) {
            return new R<>(R.FAIL, "请输入交货单号!!!");
        }

        if (StrUtil.isBlank(StrUtil.trim(barcode))) {
            return new R<>(R.FAIL, "请输入条码!!!");
        } else if (!barcode.contains("*") || !barcode.split("\\*")[1].matches("^[0-9]*$")) {
            return new R<>(R.FAIL, "条码格式不正确,请注意!!!");
        }

        if (StrUtil.isBlank(StrUtil.trim(warehouse))) {
            return new R<>(R.FAIL, "请输入发货仓库!!!");
        }

        //处理业务逻辑
        try {
            CfCecDeliverGoodsDto dto = new CfCecDeliverGoodsDto();
            dto.setDeliverOrderNo(deliverOrderNo);
            dto.setBarcode(barcode);
            dto.setWarehouse(warehouse);
            dto.setTrackingNo(trackingNo);
            dto.setUserId(UserUtils.getUserId(request));

            //调用业务层接口处理扫描条码的业务逻辑
            CfCecDeliverGoodsVo deliverGoodsVo = deliverGoodsService.scanBarcode(dto);
            return new R<>(deliverGoodsVo);
        } catch (Exception e) {

            //捕获异常并进行处理
            log.error(ExceptionUtils.getFullStackTrace(e));
            return new R<>(R.FAIL, e.getMessage());
        }
    }

    /**
     * 物料批次匹配提交接口
     *
     * @param cfCecDeliverGoodsVo vo对象
     * @param request             请求对象
     * @return
     */
    @PostMapping("commitBatchMatchedData")
    @ApiOperation(value = "物料批次匹配提交")
    public R<CfCecDeliverGoodsVo> commitBatchMatchedData(@RequestBody CfCecDeliverGoodsVo cfCecDeliverGoodsVo, HttpServletRequest request) throws Exception {

        int userId = UserUtils.getUserId(request);

        //从jsonObject中取出所需数据
        List<Map<String, Object>> list = cfCecDeliverGoodsVo.getBatchMatchList();

        ValidateUtils<Map<String, Object>> validateUtils = new ValidateUtils<>();
        boolean notNull = validateUtils.isNotNull(list);
        if (!notNull) {
            return new R<>(R.FAIL, "暂无可提交的数据！");
        }

        //插入扫描数据和更新清单表数据
        try {
            deliverGoodsService.insertRecordAndUpdateInventory(cfCecDeliverGoodsVo, userId);
        } catch (Exception e) {
            log.error(ExceptionUtils.getMessage(e));
            return new R<>(R.FAIL, e.getMessage());
        }

        //从数据库查询数据并返回
        try {
            CfCecDeliverGoodsVo vo = deliverGoodsService.getDataFromDataBase(cfCecDeliverGoodsVo.getDeliverOrderNo(), userId);
            return new R<>(vo);
        } catch (Exception e) {
            log.error(ExceptionUtils.getMessage(e));
            return new R<>(R.FAIL, e.getMessage());
        }
    }

    /**
     * 修改扫描表行数据
     *
     * @param deliverOrderNo 交货单号
     * @param materialsNo    物料代码
     * @param number         修改数量
     * @param total          物料总数
     * @param request        请求对象
     * @return r
     */
    @GetMapping("update")
    @ApiOperation(value = "修改")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deliverOrderNo", value = "交货单号", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "materialsNo", value = "物料代码", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "number", value = "修改数量", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "total", value = "物料总数", dataType = "string", paramType = "query")
    })
    public R<CfCecDeliverGoodsVo> update(String deliverOrderNo, String materialsNo, Integer number, Integer total, HttpServletRequest request) {
        //校验前端传过来的数据
        if (!validateUtils.isNotNull(deliverOrderNo, materialsNo) || number == null) {
            return new R<>(R.FAIL, "交货单号或物料代码或修改数量为空,请注意!!!");
        }

        try {
            //修改扫描记录表数量
            deliverGoodsService.update(deliverOrderNo, materialsNo, number, total, UserUtils.getUserId(request));
            //重新加载数据
            CfCecDeliverGoodsVo deliverGoodsVo = deliverGoodsService.getDataFromDataBase(deliverOrderNo, UserUtils.getUserId(request));
            return new R<>(deliverGoodsVo);
        } catch (Exception e) {
            logger.error(ExceptionUtils.getFullStackTrace(e));
            e.printStackTrace();
            return new R<>(R.FAIL, e.getMessage());
        }

    }


    /**
     * 调拨模块的删除功能（通用于一步、两步出库、两步入库）
     *
     * @param deliverOrderNo 交货单号
     * @param materialsNo    物料代码
     * @param request        请求对象
     * @return CfAllowManagementVo
     */
    @GetMapping("delete")
    @ApiOperation(value = "删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deliverOrderNo", value = "交货单号", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "materialsNo", value = "物料代码", dataType = "string", paramType = "query"),
    })
    public R<CfCecDeliverGoodsVo> delete(String deliverOrderNo, String materialsNo, HttpServletRequest request) {
        //校验前端传过来的数据
        if (!validateUtils.isNotNull(deliverOrderNo, materialsNo)) {
            return new R<>(R.FAIL, "交货单号或物料代码为空,请注意!!!");
        }
        try {
            //删除
            deliverGoodsService.delete(deliverOrderNo, materialsNo, UserUtils.getUserId(request));
            //返回单号对应的数据
            return new R<>(deliverGoodsService.getDataFromDataBase(deliverOrderNo, UserUtils.getUserId(request)));
        } catch (Exception e) {
            logger.error(ExceptionUtils.getFullStackTrace(e));
            e.printStackTrace();
            return new R<>(R.FAIL, e.getMessage());
        }

    }

    /**
     * 提交接口
     *
     * @param deliverOrderNo 交货单号
     * @param request        请求对象
     * @return r
     */
    @GetMapping("finalCommit")
    @ApiOperation(value = "提交")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deliverOrderNo", value = "交货单号", dataType = "string", paramType = "query")
    })
    public R<CfCecDeliverGoodsVo> finalCommit(String deliverOrderNo, HttpServletRequest request) {

        //校验输入数据
        if (StrUtil.isBlank(deliverOrderNo)) {
            return new R<>(R.FAIL, "请输入有效的数据!!!");
        }

        //获取当前用户id
        int userId = UserUtils.getUserId(request);

        try {
            //业务层提交逻辑处理
            deliverGoodsService.finalCommit(deliverOrderNo, userId);

            return new R<>(deliverGoodsService.getDataFromDataBase(deliverOrderNo, userId));

        } catch (Exception e) {
            //打印错误日志
            log.error(ExceptionUtils.getFullStackTrace(e));
            //输出错误信息到控制台
            e.printStackTrace();
            return new R<>(R.FAIL, e.getMessage());
        }
    }

}
