package com.cfmoto.bar.code.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.cfmoto.bar.code.model.entity.CfBarcodeInventory;
import com.cfmoto.bar.code.model.entity.CfPrintLodopTemplate;
import com.cfmoto.bar.code.model.vo.CostCenterPrintInVo;
import com.cfmoto.bar.code.model.vo.CostCenterPrintOutVo;
import com.cfmoto.bar.code.service.ICfBarcodeInventoryService;
import com.cfmoto.bar.code.service.ICfCustomService;
import com.cfmoto.bar.code.service.ICfPrintLodopTemplateService;
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
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 条形码库存表 前端控制器
 * </p>
 *
 * @author FangWenFei
 * @since 2019-03-05
 */
@RestController
@RequestMapping("/cfBarcodeInventory")
@Api(tags = " 条形码库存表")
@Slf4j
public class CfBarcodeInventoryController extends BaseController {

    @Autowired
    private ICfBarcodeInventoryService cfBarcodeInventoryService;

    @Autowired
    private ICfCustomService iCfCustomService;


    /**
     * 通过模糊条件查询条形码库存表
     *
     * @param params 分页对象
     * @return 分页对象
     */
    @PostMapping("/pageByLikeParam")
    @ApiOperation(value = "通过模糊条件查询条形码库存表")
    public R<Page> pageByLikeParam(@RequestParam Map<String, Object> params, CfBarcodeInventory cfBarcodeInventory) {
        return new R<>(cfBarcodeInventoryService.pageByLikeParam(params, cfBarcodeInventory));
    }


    /**
     * 通过条件查询SAP条形码库存表
     *
     * @param params 分页对象
     * @return 分页对象
     */
    @PostMapping("/getSapDataByParam")
    @ApiOperation(value = "通过条件查询SAP条形码库存表")
    public R getSapDataByParam(@RequestParam Map<String, Object> params) {
        try {
            //工厂
            String IV_WERKS = params.getOrDefault("IV_WERKS", "").toString();
            //仓库
            String IV_LGORT = params.getOrDefault("IV_LGORT", "").toString();

            //校验必输数据：工厂和仓库
            if ((!StringUtils.isNotBlank(IV_WERKS)) || (!StringUtils.isNotBlank(IV_LGORT))) {
                throw new Exception(CfBarcodeInventory.CF_BARCODE_INVENTORY_PARAM_ERROR);
            }

            return cfBarcodeInventoryService.getSapDataByParam(params);
        } catch (Exception e) {
            return new R<>(R.FAIL, e.getMessage());
        }
    }


    /**
     * 通过ID查询
     *
     * @param id ID
     * @return CfBarcodeInventory
     */
    @PostMapping("/getById")
    @ApiOperation(value = "通过ID查询")
    public R<CfBarcodeInventory> get(@RequestParam Integer id) {
        return new R<>(cfBarcodeInventoryService.selectById(id));
    }


    /**
     * 分页查询信息
     *
     * @param params 分页对象
     * @return 分页对象
     */
    @PostMapping("/page")
    @ApiOperation(value = "分页查询条形码库存表")
    public R<Page> page(@RequestParam Map<String, Object> params, CfBarcodeInventory cfBarcodeInventory) {
        return new R<>(cfBarcodeInventoryService.selectPage(new QueryPage<>(params), new EntityWrapper<>(cfBarcodeInventory)));
    }

    /**
     * 添加
     *
     * @param cfBarcodeInventory 实体
     * @return success/false
     */
    @PostMapping("/add")
    @ApiOperation(value = "添加条形码库存表")
    public R<Boolean> add(@RequestBody CfBarcodeInventory cfBarcodeInventory, HttpServletRequest httpServletRequest) {
        try {
            int userId = UserUtils.getUserId(httpServletRequest);
            cfBarcodeInventory.setObjectSetBasicAttribute(userId, new Date());
            return new R<>(cfBarcodeInventoryService.insert(cfBarcodeInventory));
        } catch (Exception e) {
            return new R<>(R.FAIL, e.getMessage());
        }


    }

    /**
     * 删除
     *
     * @param id ID
     * @return success/false
     */
    @PostMapping("/deleteById")
    @ApiOperation(value = "删除条形码库存表通过ID")
    public R<Boolean> delete(@RequestParam Integer id) {
        CfBarcodeInventory cfBarcodeInventory = new CfBarcodeInventory();
        return new R<>(cfBarcodeInventoryService.updateById(cfBarcodeInventory));
    }

    /**
     * 编辑
     *
     * @param cfBarcodeInventory 实体
     * @return success/false
     */
    @PostMapping("/edit")
    @ApiOperation(value = "编辑除条形码库存表")
    public R<Boolean> edit(@RequestBody CfBarcodeInventory cfBarcodeInventory) {
        return new R<>(cfBarcodeInventoryService.updateById(cfBarcodeInventory));
    }


    /**
     * 通过条形码查询
     * 有行数据时，带出相关信息；
     * 无行数据时，报错“条码不正确，请注意！”
     *
     * @param barcode 条形码
     * @return cfBarcodeInventory 实体
     */
    @GetMapping("/getBarcode")
    @ApiOperation(value = "扫描条码")
    @ApiImplicitParam(name = "barcode", value = "条码", dataType = "string", paramType = "query")
    public R<CfBarcodeInventory> getBarcode(String barcode) {
        try {
            return cfBarcodeInventoryService.getByBarcode(barcode);
        } catch (Exception e) {
            log.error(ExceptionUtils.getFullStackTrace(e));
            return new R<>(R.FAIL, e.getMessage());
        }
    }

    /**
     * 编辑条形码数量
     * 只可以小于当前数量，不可大于当前数量
     *
     * @param barcode 条形码   barcodeNumber 条形码数量
     * @return success/false
     */
    @GetMapping("/updateBarcodeNumber")
    @ApiOperation(value = "更新条码数量")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "barcode", value = "条码", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "barcodeNumber", value = "条码数量", dataType = "int", paramType = "query")
    })
    public R<CfBarcodeInventory> updateBarcodeNumber(String barcode, Integer barcodeNumber, HttpServletRequest request) {
        try {
            return cfBarcodeInventoryService.updateNumberByBarcode(barcode, barcodeNumber, UserUtils.getUserId(request));
        } catch (Exception e) {
            log.error(ExceptionUtils.getFullStackTrace(e));
            return new R<>(R.FAIL, e.getMessage());
        }

    }

    /**
     * 根据物料代码查询条码库存表数据，并按批次排序
     *
     * @param materialsNo 物料代码
     * @param warehouse   仓库（非必传）
     * @return list
     * @author ye
     */
    @GetMapping("getListByMaterialsNo")
    @ApiOperation(value = "根据物料代码查询数据集合并按批次排序")
    public R<List<CfBarcodeInventory>> getListByMaterialsNo(String materialsNo, @RequestParam(required = false) String warehouse, HttpServletRequest request) {
        if (StrUtil.isBlank(materialsNo)) {
            return new R<>(R.FAIL, "请输入物料代码!!!");
        }
        try {

            List<CfBarcodeInventory> barcodeInventorieList = cfBarcodeInventoryService.getInventoryFromSap(materialsNo, warehouse, UserUtils.getUserId(request), "");
            return new R<>(barcodeInventorieList);
        } catch (Exception e) {
            log.error(ExceptionUtils.getFullStackTrace(e));
            e.printStackTrace();
            return new R<>(R.FAIL, e.getMessage());
        }
    }


    /**
     * 成本中心退料入库标签打印
     *
     * @param costCenterPrintInVo
     * @param httpServletRequest
     * @return
     */
    @PostMapping("/costCenterPrint")
    @ApiOperation(value = "成本中心退料入库标签打印")
    public R<CostCenterPrintOutVo> costCenterPrint(@RequestBody CostCenterPrintInVo costCenterPrintInVo, HttpServletRequest httpServletRequest) {

        CostCenterPrintOutVo costCenterPrintOutVo = null;
        try {
            int userId = UserUtils.getUserId(httpServletRequest);
            costCenterPrintOutVo = iCfCustomService.costCenterPrint(costCenterPrintInVo, userId);
        } catch (Exception e) {
            log.error(ExceptionUtils.getFullStackTrace(e));
            return new R<>(R.FAIL, e.getMessage());
        }
        return new R<>(costCenterPrintOutVo);
    }

}
