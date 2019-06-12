package com.cfmoto.bar.code.controller.stock;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.cfmoto.bar.code.model.entity.CfBarcodeInventory;
import com.cfmoto.bar.code.model.entity.CfStockListInfo;
import com.cfmoto.bar.code.model.entity.CfStockTooReceiveLine;
import com.cfmoto.bar.code.service.ICfBarcodeInventoryService;
import com.cfmoto.bar.code.service.ICfTooReceiveStockService;
import com.github.pig.common.util.QueryPage;
import com.github.pig.common.util.R;
import com.github.pig.common.util.UserUtils;
import com.github.pig.common.web.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/* **********************************************************************
 *              Created by FangWenFei on 2019/3/18.
 * **********************************************************************
 * **********Thunderstorm, rain and dew are all grace of heaven**********
 * **********************************************************************
 */
@RestController
@RequestMapping("/cfTooReceiveStock")
@Api(tags = " 生产超领")
public class CfTooReceiveStockController extends BaseController {
    @Autowired
    private ICfTooReceiveStockService cfTooReceiveStockService;

    @Autowired
    private ICfBarcodeInventoryService cfBarcodeInventoryService;

    /**
     * 先检查后台备料生产超领信息表里是否存在数据
     */
    @PostMapping("/getDataByStockByNo")
    @ApiOperation(value = "先检查后台备料生产超领信息表里是否存在数据")
    public R<Map<String, Object>> getDataByStockByNo(@RequestBody Map<String, Object> params, HttpServletRequest httpServletRequest) {
        try {
            int userId = UserUtils.getUserId(httpServletRequest);
            params.put("stockFunctionType", CfStockListInfo.STOCK_FUNCTION_TYPE_40);//合并备料：10，生产领料:20,退料: 30,超领 :40
            return new R<>(cfTooReceiveStockService.getDataByStockByNo(userId, params));
        } catch (Exception e) {
            return new R<>(R.FAIL, e.getMessage());
        }

    }

    /**
     * 添加扫描数据，并更新汇总数据
     */
    @PostMapping("/addCfStockScanLineData")
    @ApiOperation(value = "添加扫描数据，并更新汇总数据")
    public R<Map<String, Object>> addCfStockScanLineData(@RequestBody Map<String, Object> params, HttpServletRequest httpServletRequest) {
        try {
            int userId = UserUtils.getUserId(httpServletRequest);
            return new R<>(cfTooReceiveStockService.addScanLineData(userId, params));
        } catch (DuplicateKeyException e) {
            e.printStackTrace();
            return new R<>(R.FAIL, CfStockTooReceiveLine.EX_DOUBLE_DATA);
        } catch (Exception e) {
            e.printStackTrace();
            return new R<>(R.FAIL, e.getMessage());
        }

    }

    /**
     * 删除
     *
     * @param params
     * @return success/false
     */
    @PostMapping("/deleteDataByBarCodeNo")
    @ApiOperation(value = "删除备料超领扫描记录通过ID")
    public R<Map<String, Object>> deleteDataByBarCodeNo(@RequestBody Map<String, Object> params, HttpServletRequest httpServletRequest) {
        try {
            int userId = UserUtils.getUserId(httpServletRequest);
            return new R<>(cfTooReceiveStockService.deleteDataByBarCodeNo(userId, params));
        } catch (Exception e) {
            e.printStackTrace();
            return new R<>(R.FAIL, e.getMessage());
        }
    }

    /**
     * 分页查询备料扫描记录
     *
     * @param params 分页对象
     * @return 分页对象
     */
    @PostMapping("/cfStockTooReceiveLinePage")
    @ApiOperation(value = "分页查询备料超领扫描记录")
    public R<Page> page(@RequestParam Map<String, Object> params) {
        try {
            Integer page = Integer.parseInt(params.getOrDefault("page", 1).toString());
            Integer limit = Integer.parseInt(params.getOrDefault("limit", QueryPage.LIMIT_10000).toString());
            Integer stockRootId = Integer.parseInt(params.getOrDefault("stockRootId", "").toString());
            Page<CfStockTooReceiveLine> pages = new Page<>(page, limit);
            return new R<>(cfTooReceiveStockService.selectPage(pages, new EntityWrapper<CfStockTooReceiveLine>().
                    eq(CfStockTooReceiveLine.STOCK_ROOT_ID_SQL, stockRootId)));
        } catch (Exception e) {
            return new R<>(R.FAIL, e.getMessage());
        }
    }

    /**
     * 分页查询条形码库存表
     *
     * @param materialsNo 物料代码
     * @param warehouse   仓库
     * @return 分页对象
     */
    @GetMapping("/cfBarcodeInventoryPage")
    @ApiOperation(value = "分页查询条形码库存表")
    public R<Page> cfBarcodeInventoryPage(@RequestParam String materialsNo, @RequestParam(required = false) String warehouse, HttpServletRequest httpServletRequest) {
        int userId = UserUtils.getUserId(httpServletRequest);
        try {
            List<CfBarcodeInventory> cfBarcodeInventoryList = cfBarcodeInventoryService.getInventoryFromSap(materialsNo, warehouse, userId, "");
            Page<CfBarcodeInventory> page = new Page<>();
            page.setRecords(cfBarcodeInventoryList);
            return new R<>(page);
        } catch (Exception e) {
            e.printStackTrace();
            return new R<>(R.FAIL, e.getMessage());
        }
    }


    /**
     * 获取汇总数据
     */
    @PostMapping("/getCfStockTooReceiveHeaderPage")
    @ApiOperation(value = "获取汇总数据")
    public R<Page> getCfStockTooReceiveHeaderPage(@RequestBody Map<String, Object> params, HttpServletRequest httpServletRequest) {
        try {
            int userId = UserUtils.getUserId(httpServletRequest);
            return new R<>(cfTooReceiveStockService.getCfStockTooReceiveHeaderPage(userId, params));
        } catch (DuplicateKeyException e) {
            e.printStackTrace();
            return new R<>(R.FAIL, CfStockTooReceiveLine.EX_DOUBLE_DATA);
        } catch (Exception e) {
            e.printStackTrace();
            return new R<>(R.FAIL, e.getMessage());
        }

    }

    /**
     * 编辑除备料超领扫描记录数量
     *
     * @param params
     * @return success/false
     */
    @PostMapping("/edit")
    @ApiOperation(value = "编辑除备料超领扫描记录数量")
    public R<Map<String, Object>> edit(@RequestBody Map<String, Object> params, HttpServletRequest httpServletRequest) {
        try {
            int userId = UserUtils.getUserId(httpServletRequest);
            Map<String, Object> resultMap = cfTooReceiveStockService.updateByStockScanLineData(userId, params);
            return new R<>(resultMap);
        } catch (DuplicateKeyException e) {
            e.printStackTrace();
            return new R<>(R.FAIL, CfStockTooReceiveLine.EX_DOUBLE_DATA);
        } catch (Exception e) {
            e.printStackTrace();
            return new R<>(R.FAIL, e.getMessage());
        }
    }

    /**
     * 点击提交，更新备料超领扫描记录表中状态为未提交的行数据改为已提交；数据通过接口发送至SAP；OT条码扣减对应库存，CP/EG/KTM扣减库存数量为0（KTM在KTM表）；
     */
    @PostMapping("/submitICfStockTooReceiveLineData")
    @ApiOperation(value = "提交备料超领扫描记录")
    public R<Map<String, Object>> submitICfStockScanLineData(@RequestBody Map<String, Object> params, HttpServletRequest httpServletRequest) {
        try {
            int userId = UserUtils.getUserId(httpServletRequest);
            Map<String, Object> resultMap = cfTooReceiveStockService.submitICfStockTooReceiveLineData(userId, params);
            return new R<>(resultMap);
        } catch (DuplicateKeyException e) {
            e.printStackTrace();
            return new R<>(R.FAIL, CfStockTooReceiveLine.EX_DOUBLE_DATA);
        } catch (Exception e) {
            e.printStackTrace();
            return new R<>(R.FAIL, "存在错误请联系管理员" + e.getMessage());
        }
    }


}
