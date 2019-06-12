package com.cfmoto.bar.code.controller.ckdMaterielBox;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Date;

import com.cfmoto.bar.code.model.dto.SelectList;
import com.cfmoto.bar.code.model.entity.CfLoadPacking;
import com.cfmoto.bar.code.utiles.ExcelUtiles;
import com.github.pig.common.util.UserUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.github.pig.common.util.QueryPage;
import com.github.pig.common.util.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.cfmoto.bar.code.service.ckdMaterielBox.ICfLoadPackingService;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.pig.common.web.BaseController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author FangWenFei
 * @since 2019-02-26
 */
@RestController
@RequestMapping("/cfLoadPacking")
@Api(tags=" ")
public class CfLoadPackingController extends BaseController {
    @Autowired private ICfLoadPackingService cfLoadPackingService;

    /**
     * 查询所有销售订单的的种类
     *
     *
     * @return SelectList
     */
    @PostMapping("/selectAllSalesOrderNo")
    @ApiOperation(value="查询所有销售订单的的种类")
    public R<List<SelectList>> selectAllSalesOrderNo() {
        try {
            return new R<>(cfLoadPackingService.selectAllSalesOrderNo());
        }catch (Exception e){
            return new R<>(R.FAIL, CfLoadPacking.CF_LOAD_PACKING_EX);
        }
    }
    /**
     * 通过salesOrderNo查询获取单据号
     *
     *
     * @return SelectList
     */
    @PostMapping("/selectDocumentNoBySalesOrderNo")
    @ApiOperation(value="通过salesOrderNo查询获取单据号")
    public R<List<SelectList>> selectDocumentNoBySalesOrderNo(@RequestParam String salesOrderNo) {
        try {
            return new R<>(cfLoadPackingService.selectDocumentNoBySalesOrderNo(salesOrderNo));
        }catch (Exception e){
            return new R<>(R.FAIL,CfLoadPacking.CF_LOAD_PACKING_EX);
        }

    }


    /**
    * 通过ID查询
    *
    * @param id ID
    * @return CfLoadPacking
    */
    @PostMapping("/getById")
    @ApiOperation(value="通过ID查询")
    public R<CfLoadPacking> get(@RequestParam Integer id) {
        return new R<>(cfLoadPackingService.selectById(id));
    }


    /**
    * 分页查询信息
    *
    * @param params 分页对象
    * @return 分页对象
    */
    @PostMapping("/page")
    @ApiOperation(value="分页查询")
    public R<Page> page(@RequestParam Map<String, Object> params,CfLoadPacking cfLoadPacking) {
        EntityWrapper entityWrapper=  new EntityWrapper<>(cfLoadPacking);
        entityWrapper.like(StringUtils.isNotBlank(cfLoadPacking.getMaterialNo()),
                           CfLoadPacking.CF_MATERIAL_NO_SQL,
                           cfLoadPacking.getMaterialNo());
        entityWrapper.like(StringUtils.isNotBlank(cfLoadPacking.getMaterialName()),
                           CfLoadPacking.CF_MATERIAL_NAME_SQL,
                           cfLoadPacking.getMaterialName());
        cfLoadPacking.setMaterialNo(null);
        cfLoadPacking.setMaterialName(null);
        return new R<>(cfLoadPackingService.selectPage(new QueryPage<>(params),entityWrapper));
    }

    /**
     * 导出
     * @param response
     */
    @RequestMapping("/export/cfLoadPacking")
    public void export(@RequestParam Map<String, Object> params, CfLoadPacking cfLoadPacking,HttpServletResponse response){
        EntityWrapper entityWrapper=  new EntityWrapper<>(cfLoadPacking);
        entityWrapper.like(StringUtils.isNotBlank(cfLoadPacking.getMaterialNo()),
                CfLoadPacking.CF_MATERIAL_NO_SQL,
                cfLoadPacking.getMaterialNo());
        entityWrapper.like(StringUtils.isNotBlank(cfLoadPacking.getMaterialName()),
                CfLoadPacking.CF_MATERIAL_NAME_SQL,
                cfLoadPacking.getMaterialName());
        cfLoadPacking.setMaterialNo(null);
        cfLoadPacking.setMaterialName(null);
      List<CfLoadPacking> cfLoadPackingList= cfLoadPackingService.selectList(entityWrapper);
        //导出操作
        ExcelUtiles.exportExcel(cfLoadPackingList,"物料标签","物料标签",CfLoadPacking.class,"物料标签.xls",response);
    }


    /**
     * 添加
     * @param  cfLoadPacking  实体
     * @return success/false
     */
    @PostMapping("/add")
    @ApiOperation(value="添加")
    public R<Boolean> add(@RequestBody CfLoadPacking cfLoadPacking,HttpServletRequest httpServletRequest) {
        try{
            int userId= UserUtils.getUserId(httpServletRequest);
            cfLoadPacking.setObjectSetBasicAttribute(userId,new Date());
            cfLoadPackingService.addCfLoadPacking(cfLoadPacking);
            return new R<>(true);
       }catch (Exception e){
            e.printStackTrace();
            return new R<>(R.FAIL, e.getMessage());
        }


    }

    /**
     * 删除
     * @param loadPackingId ID
     * @return success/false
     */
    @PostMapping("/deleteById")
    @ApiOperation(value="删除通过ID")
    public R<Boolean> delete(@RequestParam Integer loadPackingId) {
        CfLoadPacking cfLoadPacking=   cfLoadPackingService.selectById(loadPackingId);
        if(cfLoadPacking==null){
            return new R<>(R.FAIL,CfLoadPacking.CF_DOUBLE_DELETE_EX);
        }else{
            if(cfLoadPacking.getLoadNumber().compareTo(new BigDecimal(0))>0){
                return new R<>(R.FAIL,CfLoadPacking.CF_LOAD_NUMBER_EX);
            }
        }
        return new R<>(cfLoadPackingService.deleteById(loadPackingId));
    }

    /**
     * 删除
     * @param cfLoadPacking ID
     * @return success/false
     */
    @PostMapping("/deleteByCfLoadPacking")
    @ApiOperation(value="删除通过ID")
    public R<Boolean> deleteByCfLoadPacking(@RequestBody CfLoadPacking cfLoadPacking) {
        try{
            cfLoadPackingService.deleteByCfLoadPacking(cfLoadPacking);
            return new R<>(true);
        }catch (Exception e){
            e.printStackTrace();
            return new R<>(R.FAIL,e.getMessage());
        }


    }


    /**
     * 编辑
     * @param  cfLoadPacking  实体
     * @return success/false
     */
    @PostMapping("/edit")
    @ApiOperation(value="编辑除")
    public R<Boolean> edit(@RequestBody CfLoadPacking cfLoadPacking) {
        if(cfLoadPacking.getLoadNumber().compareTo(cfLoadPacking.getMaterialNumber())>0){
            return new R<>(R.FAIL,CfLoadPacking.CF_TOO_LOAD_NUMBER_EX);
        }
        return new R<>(cfLoadPackingService.updateById(cfLoadPacking));
    }
}
