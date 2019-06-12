package com.cfmoto.bar.code.controller;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.cfmoto.bar.code.model.dto.SelectList;
import com.cfmoto.bar.code.model.entity.CfAssemblyPlan;
import com.cfmoto.bar.code.service.ICfAssemblyPlanService;
import com.cfmoto.bar.code.utiles.ExcelUtiles;
import com.github.pig.common.util.QueryPage;
import com.github.pig.common.util.R;
import com.github.pig.common.util.UserUtils;
import com.github.pig.common.web.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * <p>
 * 总成方案数据表 前端控制器
 * </p>
 *
 * @author FangWenFei
 * @since 2019-02-15
 */
@RestController
@RequestMapping("/cfAssemblyPlan")
@Api(tags=" 总成方案数据表")
public class CfAssemblyPlanController extends BaseController {
    @Autowired private ICfAssemblyPlanService cfAssemblyPlanService;

    /**
     * 查询所有的国家country的种类
     *
     *
     * @return CfAssemblyPlan
     */
    @PostMapping("/getAllCountry")
    @ApiOperation(value="通过ID查询")
    public R<List<SelectList>> getAllCountry() {
        try {
            return new R<>(cfAssemblyPlanService.selectAllCountry());
        }catch (Exception e){
            return new R<>(R.FAIL, CfAssemblyPlan.CF_ASSEMBLY_PLAN_EX);
        }

    }
    /**
     * 通过国家获取类型
     *
     *
     * @return SelectList
     */
    @PostMapping("/selectModelByCountry")
    @ApiOperation(value="通过country查询")
    public R<List<SelectList>> selectModelByCountry(@RequestParam String country) {
        try {
            return new R<>(cfAssemblyPlanService.selectModelByCountry(country));
        }catch (Exception e){
            return new R<>(R.FAIL, CfAssemblyPlan.CF_ASSEMBLY_PLAN_EX);
        }

    }


    /**
    * 通过ID查询
    *
    * @param id ID
    * @return CfAssemblyPlan
    */
    @PostMapping("/getById")
    @ApiOperation(value="通过ID查询")
    public R<CfAssemblyPlan> get(@RequestParam Integer id) {
        return new R<>(cfAssemblyPlanService.selectById(id));
    }


    /**
    * 分页查询信息
    *
    * @param params 分页对象
    * @return 分页对象
    */
    @PostMapping("/page")
    @ApiOperation(value="分页查询总成方案数据表")
        public R<Page> page(@RequestParam Map<String, Object> params,CfAssemblyPlan cfAssemblyPlan) {
        EntityWrapper<CfAssemblyPlan> entityWrapper= new EntityWrapper<>(cfAssemblyPlan);
        entityWrapper.like(StringUtils.isNotBlank(cfAssemblyPlan.getMaterialNo()),"material_no",cfAssemblyPlan.getMaterialNo());
        entityWrapper.like(StringUtils.isNotBlank(cfAssemblyPlan.getAssemblyMaterials()),"assembly_materials",cfAssemblyPlan.getAssemblyMaterials());
        entityWrapper.like(StringUtils.isNotBlank(cfAssemblyPlan.getSonMaterial()),"son_material",cfAssemblyPlan.getSonMaterial());
        cfAssemblyPlan.setMaterialNo(null);
        cfAssemblyPlan.setAssemblyMaterials(null);
        cfAssemblyPlan.setSonMaterial(null);
        return new R<>(cfAssemblyPlanService.selectPage(new QueryPage<>(params),entityWrapper));
    }

    /**
     * 添加
     * @param  cfAssemblyPlan  实体
     * @return success/false
     */
    @PostMapping("/add")
    @ApiOperation(value="添加总成方案数据表")
    public R<Boolean> add(@RequestBody CfAssemblyPlan cfAssemblyPlan,HttpServletRequest httpServletRequest) {
        try{
            int userId= UserUtils.getUserId(httpServletRequest);
            cfAssemblyPlan.setObjectSetBasicAttribute(userId,new Date());
            return new R<>(cfAssemblyPlanService.insert(cfAssemblyPlan));
       }catch (Exception e){
            return new R<>(R.FAIL, CfAssemblyPlan.CF_ASSEMBLY_PLAN_SQL_ADD);
        }


    }

    /**
     * 删除
     * @param cfAssemblyPlan
     * @return success/false
     */
    @PostMapping("/delete")
    @ApiOperation(value="删除总成方案数据表cfAssemblyPlan")
    public R<Boolean> delete(@RequestBody CfAssemblyPlan cfAssemblyPlan) {
       // new EntityWrapper<CfAssemblyPlan>();
       // cfAssemblyPlanService.delete(new EntityWrapper<CfAssemblyPlan>(cfAssemblyPlan));
        if(!StringUtils.isNotBlank(cfAssemblyPlan.getMaterialNo())){
            return new R<>(R.FAIL, CfAssemblyPlan.CF_MATERIAL_NO_NULL);
        }else if(!StringUtils.isNotBlank(cfAssemblyPlan.getModel()) ){
            return new R<>(R.FAIL, CfAssemblyPlan.CF_MODEL_NULL);
        }else if(!StringUtils.isNotBlank(cfAssemblyPlan.getCountry()) ){
            return new R<>(R.FAIL, CfAssemblyPlan.CF_COUNTRY_NULL);
        }else{
            cfAssemblyPlan.setAssemblyMaterials(null);
            cfAssemblyPlan.setSonMaterial(null);
            EntityWrapper entityWrapper= new EntityWrapper<CfAssemblyPlan>();
            entityWrapper.setEntity(cfAssemblyPlan);
            return new R<>(cfAssemblyPlanService.delete(entityWrapper));
        }


    }

    /**
     * 编辑
     * @param  cfAssemblyPlan  实体
     * @return success/false
     */
    @PostMapping("/edit")
    @ApiOperation(value="编辑除总成方案数据表")
    public R<Boolean> edit(@RequestBody CfAssemblyPlan cfAssemblyPlan) {
        return new R<>(cfAssemblyPlanService.updateById(cfAssemblyPlan));
    }




    /**
     * 导出
     * @param response
     */
    @RequestMapping("/export")
    public void export(@RequestParam Map<String, Object> params,HttpServletResponse response){
        //模拟从数据库获取需要导出的数据
        Map<String, Object> selectMap=new HashedMap();
        String country= params.getOrDefault("country", "").toString();
        String model= params.getOrDefault("model", "").toString();
        selectMap.put("country",country);
        selectMap.put("model",model);
        List<CfAssemblyPlan> personList = cfAssemblyPlanService.selectByMap(selectMap);
        //导出操作
        ExcelUtiles.exportExcel(personList,"总成方案","总成方案",CfAssemblyPlan.class,"总成方案.xls",response);
    }

    /**
     * 导入
     */
    @RequestMapping("importExcel")
    public  Map<String, String> importExcel(@RequestParam("file") MultipartFile file,HttpServletRequest httpServletRequest){
        Map<String, String> resultMap = new HashMap<>();
        int userId= UserUtils.getUserId(httpServletRequest);
        resultMap.put("FileName",file.getOriginalFilename());
        List<CfAssemblyPlan> personList = ExcelUtiles.importExcel(file,1,1,CfAssemblyPlan.class);
        //也可以使用MultipartFile,使用 FileUtil.importExcel(MultipartFile file, Integer titleRows, Integer headerRows, Class<T>                pojoClass)导入
        System.out.println("导入数据一共【"+personList.size()+"】行");
        logger.info("导入数据一共 :"+JSONArray.toJSON(personList).toString());
        Date date=new Date();
        personList.forEach(cfAssemblyPlan->{
            cfAssemblyPlan.setObjectSetBasicAttribute(userId,date);
            cfAssemblyPlan.setImportTime(date);
            cfAssemblyPlan.setImportUser(String.valueOf(userId));
            cfAssemblyPlan.setMaterialNo(StringUtils.trimToEmpty(cfAssemblyPlan.getMaterialNo()));
            cfAssemblyPlan.setCountry(StringUtils.trimToEmpty(cfAssemblyPlan.getCountry()));
            cfAssemblyPlan.setModel(StringUtils.trimToEmpty(cfAssemblyPlan.getModel()));
            cfAssemblyPlan.setSonMaterial(StringUtils.trimToEmpty(cfAssemblyPlan.getSonMaterial()));
            cfAssemblyPlan.setAssemblyMaterials(StringUtils.trimToEmpty(cfAssemblyPlan.getAssemblyMaterials()));
            cfAssemblyPlan.setMaterialName(StringUtils.trimToEmpty(cfAssemblyPlan.getMaterialName()));
        });
        cfAssemblyPlanService.insertBatch(personList);
       return  resultMap;

    }


}
