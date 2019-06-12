package com.cfmoto.bar.code.controller.stock;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.cfmoto.bar.code.model.entity.CfBarcodeInventory;
import com.cfmoto.bar.code.model.entity.CfStockListInfo;
import com.cfmoto.bar.code.model.entity.CfStockScanLine;
import com.cfmoto.bar.code.service.ICfBarcodeInventoryService;
import com.cfmoto.bar.code.service.ICfStockScanLineService;
import com.github.pig.common.util.QueryPage;
import com.github.pig.common.util.R;
import com.github.pig.common.util.UserUtils;
import com.github.pig.common.web.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;

/**
 * <p>
 * 备料扫描记录 前端控制器
 * </p>
 *
 * @author FangWenFei
 * @since 2019-03-12
 */
@RestController
@RequestMapping("/cfStockScanLine")
@Api(tags=" 备料扫描记录")
public class CfStockScanLineController extends BaseController {
    @Autowired private ICfStockScanLineService cfStockScanLineService;

    @Autowired
    private ICfBarcodeInventoryService cfBarcodeInventoryService;
    /**
     * 先检查后台备料信息表里是否存在数据
     *
     */
    @PostMapping("/getDataByStockListNo")
    @ApiOperation(value="先检查后台备料信息表里是否存在数据")
    public R<Map<String, Object>> getDataByStockListNo(@RequestBody Map<String, Object> params,HttpServletRequest httpServletRequest) {
        try{
            int userId=UserUtils.getUserId(httpServletRequest);
            params.put("stockFunctionType", CfStockListInfo.STOCK_FUNCTION_TYPE_10);//合并备料：10，生产领料:20,退料: 30,超领 :40
            return new R<>(cfStockScanLineService.getDataByStockListNo(userId,params));
        }catch (Exception e){
            return new R<>(R.FAIL, e.getMessage() );
        }

    }

    /**
     * 添加扫描数据，并更新汇总数据
     *
     */
    @PostMapping("/addCfStockScanLineData")
    @ApiOperation(value="添加扫描数据，并更新汇总数据")
    public R<Map<String, Object>> addCfStockScanLineData(@RequestBody Map<String, Object> params,HttpServletRequest httpServletRequest) {
        try{
            int userId=UserUtils.getUserId(httpServletRequest);
            return new R<>(cfStockScanLineService.addScanLineData(userId,params));
        }catch (DuplicateKeyException e){
            e.printStackTrace();
            return new R<>(R.FAIL, CfStockScanLine.EX_DOUBLE_DATA);
        }catch (Exception e){
            e.printStackTrace();
            return new R<>(R.FAIL, e.getMessage() );
        }

    }

    /**
     * 删除
     * @param params
     * @return success/false
     */
    @PostMapping("/deleteDataByBarCodeNo")
    @ApiOperation(value="删除备料扫描记录通过ID")
    public R<Map<String, Object>> deleteDataByBarCodeNo(@RequestBody  Map<String, Object> params, HttpServletRequest httpServletRequest) {
        try{
            int userId=UserUtils.getUserId(httpServletRequest);
            return new R<>(cfStockScanLineService.deleteDataByBarCodeNo(userId,params));
        }catch (Exception e){
            e.printStackTrace();
            return new R<>(R.FAIL, e.getMessage() );
        }
    }


    /**
     * 分页查询备料扫描记录
     *
     * @param params 分页对象
     * @return 分页对象
     */
    @PostMapping("/cfStockScanLinePage")
    @ApiOperation(value="分页查询备料扫描记录")
    public R<Page> page(@RequestParam Map<String, Object> params) {
        try{
            Integer  page= Integer.parseInt(params.getOrDefault("page", 1).toString());
            Integer  limit= Integer.parseInt(params.getOrDefault("limit", QueryPage.LIMIT_10000).toString());
            Integer  stockListId= Integer.parseInt(params.getOrDefault("stockListId", "").toString());
            Page<CfStockScanLine> pages=new Page<>(page,limit);
            return new R<>(cfStockScanLineService.selectPage(pages,  new EntityWrapper<CfStockScanLine>().
                    eq(CfStockScanLine.STOCK_LIST_ID_SQL,stockListId)));
        }catch (Exception e){
            return new R<>(R.FAIL, e.getMessage() );
        }
    }




    /**
     * 分页查询条形码库存表
     *
     * @param params 分页对象
     * @return 分页对象
     */
    @PostMapping("/cfBarcodeInventoryPage")
    @ApiOperation(value = "分页查询条形码库存表")
    public R<Page> cfBarcodeInventoryPage(@RequestParam Map<String, Object> params) {
        String   storageArea= params.getOrDefault("storageArea", "").toString();
        String   materialsNo= params.getOrDefault("materialsNo", "").toString();
        String   repository= params.getOrDefault("repository", "").toString();
        params.put("limit",QueryPage.LIMIT_10000);
        CfBarcodeInventory   cfBarcodeInventory=new CfBarcodeInventory();
        if(StringUtils.isNotBlank(storageArea)){
            cfBarcodeInventory.setStorageArea(storageArea);
        }
        if(StringUtils.isNotBlank(materialsNo)){
            cfBarcodeInventory.setMaterialsNo(materialsNo);
        }
        if(StringUtils.isNotBlank(repository)){
            cfBarcodeInventory.setWarehouse(repository);
        }
        return new R<>(cfBarcodeInventoryService.selectPage(new QueryPage<>(params), new EntityWrapper<>(cfBarcodeInventory)));
    }


    /**
     * 获取汇总数据
     *
     */
    @PostMapping("/getCfStockInventoryPage")
    @ApiOperation(value="获取汇总数据")
    public R<Page> getCfStockInventoryPage(@RequestBody Map<String, Object> params,HttpServletRequest httpServletRequest) {
        try{
            int userId=UserUtils.getUserId(httpServletRequest);
            return new R<>(cfStockScanLineService.getCfStockInventoryPage(userId,params));
        }catch (DuplicateKeyException e){
            e.printStackTrace();
            return new R<>(R.FAIL, CfStockScanLine.EX_DOUBLE_DATA);
        }catch (Exception e){
            e.printStackTrace();
            return new R<>(R.FAIL, e.getMessage() );
        }

    }

    /**
     * 编辑备料扫描记录数量
     * @param  params
     * @return success/false
     */
    @PostMapping("/edit")
    @ApiOperation(value="编辑备料扫描记录数量")
    public R<Map<String, Object>> edit(@RequestBody Map<String, Object> params,HttpServletRequest httpServletRequest) {
        try{
            int userId=UserUtils.getUserId(httpServletRequest);
            Map<String, Object> rusultMap=  cfStockScanLineService.updateByStockScanLineData(userId,params);
            return new R<>(rusultMap);
        }catch (DuplicateKeyException e){
            e.printStackTrace();
            return new R<>(R.FAIL,  CfStockScanLine.EX_DOUBLE_DATA);
        }catch (Exception e){
            e.printStackTrace();
            return new R<>(R.FAIL, e.getMessage() );
        }
    }


    /**
     * 点击提交，更新备料扫描记录表中状态为未提交的行数据改为已提交；数据通过接口发送至SAP；OT条码扣减对应库存，CP/EG/KTM扣减库存数量为0（KTM在KTM表）；
     *
     */
    @PostMapping("/submitICfStockScanLineData")
    @ApiOperation(value="提交备料扫描记录")
    public R<Map<String, Object>> submitICfStockScanLineData(@RequestBody Map<String, Object> params,HttpServletRequest httpServletRequest) {
        try{
            int userId=UserUtils.getUserId(httpServletRequest);
            Map<String, Object> resultMap=   cfStockScanLineService.submitICfStockScanLineData(userId,params);
            return new R<>(resultMap);
        }catch (DuplicateKeyException e){
            e.printStackTrace();
            return new R<>(R.FAIL,  CfStockScanLine.EX_DOUBLE_DATA);
        }catch (Exception e){
            e.printStackTrace();
            return new R<>(R.FAIL, e.getMessage() );
        }
    }



    /**
    * 通过ID查询
    *
    * @param id ID
    * @return CfStockScanLine
    */
    @PostMapping("/getById")
    @ApiOperation(value="通过ID查询")
    public R<CfStockScanLine> get(@RequestParam Integer id) {
        return new R<>(cfStockScanLineService.selectById(id));
    }





    /**
     * 添加
     * @param  cfStockScanLine  实体
     * @return success/false
     */
    @PostMapping("/add")
    @ApiOperation(value="添加备料扫描记录")
    public R<Boolean> add(@RequestBody CfStockScanLine cfStockScanLine,HttpServletRequest httpServletRequest) {
        try{
            int userId= UserUtils.getUserId(httpServletRequest);
            cfStockScanLine.setObjectSetBasicAttribute(userId,new Date());
            return new R<>(cfStockScanLineService.insert(cfStockScanLine));
       }catch (Exception e){
            return new R<>(R.FAIL, e.getMessage() );
        }


    }




}
