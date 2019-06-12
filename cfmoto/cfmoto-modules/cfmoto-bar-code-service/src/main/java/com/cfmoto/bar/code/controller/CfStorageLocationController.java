package com.cfmoto.bar.code.controller;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.enums.SqlLike;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.cfmoto.bar.code.model.dto.SelectList;
import com.cfmoto.bar.code.model.entity.CfStorageLocation;
import com.cfmoto.bar.code.service.ICfStorageLocationService;
import com.cfmoto.bar.code.utiles.ExcelUtiles;
import com.github.pig.common.util.QueryPage;
import com.github.pig.common.util.R;
import com.github.pig.common.util.UserUtils;
import com.github.pig.common.util.exception.ValidateCodeException;
import com.github.pig.common.web.BaseController;
import com.xiaoleilu.hutool.util.StrUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * <p>
 * 仓库 前端控制器
 * </p>
 *
 * @author space
 * @since 2019-03-05
 */
@RestController
@RequestMapping("/cfStorageLocation")
@Api(tags = " 仓库")
@Slf4j
public class CfStorageLocationController extends BaseController {

    @Autowired
    private ICfStorageLocationService cfStorageLocationService;


    /**
     * 导入
     */
    @RequestMapping("importExcel")
    public  R<Boolean> importExcel(@RequestParam("file") MultipartFile file, HttpServletRequest httpServletRequest){
        Map<String, String> resultMap = new HashMap<>();
        int userId= UserUtils.getUserId(httpServletRequest);
        resultMap.put("FileName",file.getOriginalFilename());
        List<CfStorageLocation> cfStorageLocationList = ExcelUtiles.importExcel(file,1,1,CfStorageLocation.class);
        try {
            cfStorageLocationService.importExcel(userId,cfStorageLocationList);
            return    new R<>(true);
        }catch (ValidateCodeException e){
            return new R<>(2, e.getMessage());

        }catch (Exception e){
            return new R<>(R.FAIL,e.getMessage());
        }

    }


    /**
     * 导出
     * @param response
     */
    @RequestMapping("/export")
    public void export(@RequestParam Map<String, Object> params,HttpServletResponse response){
        //模拟从数据库获取需要导出的数据
        Map<String, Object> selectMap=new HashedMap();
        String warehouse= params.getOrDefault("warehouse", "").toString();
        selectMap.put("warehouse",warehouse);
        List<CfStorageLocation> personList = cfStorageLocationService.selectByMap(selectMap);
        //导出操作
        ExcelUtiles.exportExcel(personList,"仓库维护","仓库维护",CfStorageLocation.class,"仓库维护.xls",response);
    }

    /**
     * 获取所有数据
     * List<SelectList> SelectList=new ArrayList<>();
     * cfStorageLocationService.selectList(
     * new EntityWrapper<CfStorageLocation>().setSqlSelect("warehouse ,site")).
     * forEach(m->SelectList.add(new SelectList(m.getSite(),m.getWareHouse(),m.getWareHouse())) );
     * return new R<>(SelectList);
     *
     * @return CfStorageLocation
     */
    @PostMapping("/getAllData")
    @ApiOperation(value = "获取所有数据")
    public R<List<CfStorageLocation>> getAllData() {
        return new R<>(cfStorageLocationService.selectList(new EntityWrapper<>()));
    }

    @GetMapping("getStorageLocation")
    @ApiOperation(value = "获取仓库数据")
    @ApiImplicitParam(name = "storageLocation", value = "仓库-模糊查询", dataType = "string", paramType = "query")
    public R<List<CfStorageLocation>> getStorageLocation(String storageLocation) {

        EntityWrapper<CfStorageLocation> wrapper = new EntityWrapper<CfStorageLocation>();
        if (StrUtil.isBlank(storageLocation)) {
            storageLocation = "";
        }
        wrapper.like("warehouse", storageLocation, SqlLike.RIGHT);
        List<CfStorageLocation> cfStorageLocationList = null;
        try {
            cfStorageLocationList = cfStorageLocationService.selectList(wrapper);
        } catch (Exception e) {
            log.error(ExceptionUtils.getFullStackTrace(e));
            return new R<List<CfStorageLocation>>(R.FAIL, e.getMessage());
        }
        return new R<List<CfStorageLocation>>(cfStorageLocationList);
    }

    @PostMapping("getStorageLocationAllWareHouse")
    @ApiOperation(value = "获取所有仓库")
    public R<List<SelectList>> getStorageLocationAllWareHouse(HttpServletRequest request) {
        try {
            List<SelectList> cfStorageLocationList = cfStorageLocationService.getStorageLocationAllWareHouse(UserUtils.getUserId(request));
            return new R<>(cfStorageLocationList);
        } catch (Exception e) {
            log.error(ExceptionUtils.getFullStackTrace(e));
            return new R<>(R.FAIL, e.getMessage());
        }

    }


    @GetMapping("getstorageAreaByWareHouseAndSite")
    @ApiOperation(value = "通过仓库获取存储区域")
    @ApiImplicitParam(name = "wareHouse", value = "仓库", dataType = "string", paramType = "query")
    public R<List<SelectList>> getstorageAreaByWareHouse(String wareHouse, String site) {
        try {
            List<SelectList> cfStorageLocationList = cfStorageLocationService.getstorageAreaByWareHouse(wareHouse, site);
            return new R<>(cfStorageLocationList);
        } catch (Exception e) {
            log.error(ExceptionUtils.getFullStackTrace(e));
            return new R<>(R.FAIL, e.getMessage());
        }

    }

    /**
     * 通过ID查询
     *
     * @param id ID
     * @return CfStorageLocation
     */
    @PostMapping("/getById")
    @ApiOperation(value = "通过ID查询")
    public R<CfStorageLocation> get(@RequestParam Integer id) {
        return new R<>(cfStorageLocationService.selectById(id));
    }


    /**
     * 分页查询信息
     *
     * @param params 分页对象
     * @return 分页对象
     */
    @PostMapping("/page")
    @ApiOperation(value = "分页查询仓库")
    public R<Page> page(@RequestBody Map<String, Object> params) {

        String page = null;
        EntityWrapper<CfStorageLocation> entityWrapper = new EntityWrapper<CfStorageLocation>();
        entityWrapper.like("warehouse", (String) params.get("field"), SqlLike.RIGHT);
        return new R<>(cfStorageLocationService.selectPage(new QueryPage<>(params), entityWrapper));
    }

    /**
     * 添加
     *
     * @param cfStorageLocation 实体
     * @return success/false
     */
    @PostMapping("/add")
    @ApiOperation(value = "添加仓库")
    public R<Boolean> add(@RequestBody CfStorageLocation cfStorageLocation, HttpServletRequest httpServletRequest) {
        try {
            int userId = UserUtils.getUserId(httpServletRequest);
            cfStorageLocation.setObjectSetBasicAttribute(userId, new Date());
            return new R<>(cfStorageLocationService.insert(cfStorageLocation));
        } catch (Exception e) {
            if (e.getMessage().contains("MySQLIntegrityConstraintViolationException")) {
                return new R<>(R.FAIL, "仓库数据已维护");
            }
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
    @ApiOperation(value = "删除仓库通过ID")
    public R<Boolean> delete(@RequestParam Integer id) {
        try {

            if (id == null) {
                throw new Exception("id不能为空");
            }
            CfStorageLocation cfStorageLocation = cfStorageLocationService.selectById(id);
            if (cfStorageLocation == null) {
                throw new Exception("删除数据不存在");
            }
            cfStorageLocationService.deleteById(id);
        } catch (Exception e) {
            return new R<>(R.FAIL, e.getMessage());
        }
        return new R<>(R.SUCCESS, "删除成功");
    }

    /**
     * 编辑
     *
     * @param cfStorageLocation 实体
     * @return success/false
     */
    @PostMapping("/edit")
    @ApiOperation(value = "编辑除仓库")
    public R<Boolean> edit(@RequestBody CfStorageLocation cfStorageLocation) {
        return new R<>(cfStorageLocationService.updateById(cfStorageLocation));
    }

    @GetMapping
    @ApiOperation(value = "根据仓库获取数据")
    public R<CfStorageLocation> getByWareHouse(String wareHouse) {
        if (StrUtil.isBlank(wareHouse)) {
            return new R<>(R.FAIL, "请输入有效的数据！！！");
        }
        try {
            return new R<>(cfStorageLocationService.selectOne(new EntityWrapper<CfStorageLocation>().eq("warehouse", wareHouse)));
        } catch (Exception e) {
            //记录错误日志
            log.error(e.getMessage());
            //输出错误信息到控制台
            e.printStackTrace();
            //返回错误信息
            return new R<>(R.FAIL, "发生未知错误，请联系管理员！！！");
        }
    }


    @GetMapping("getWareHouseBySite")
    public R<List<String>> getWareHouseBySite(@RequestParam(required = false, defaultValue = "") String site) {
        try {

            List<String> cfStorageLocationList = cfStorageLocationService.getWareHouseBySite(site);
            return new R<>(cfStorageLocationList);

        } catch (Exception e) {

            //记录错误日志
            log.error(e.getMessage());
            //输出错误信息到控制台
            e.printStackTrace();
            //返回错误信息
            return new R<>(R.FAIL, "发生未知错误，请联系管理员！！！");
        }
    }

    @GetMapping("getIdBySiteAndWarehouseAndStorageArea")
    public R<Integer> getIdBySiteAndWarehouseAndStorageArea(String site, String warehouse, String storageArea) {

        try {
            EntityWrapper<CfStorageLocation> wrapper = new EntityWrapper<>();
            wrapper.eq("site", site).and().eq("warehouse", warehouse).and().eq("storage_area", storageArea);
            CfStorageLocation storageLocation = cfStorageLocationService.selectOne(wrapper);
            if(storageLocation == null){
                return new R<>(R.FAIL,"未能找到对应的仓库信息");
            }
            return new R<>(storageLocation.getStorageLocationId());
        } catch (Exception e) {
            //记录错误日志
            log.error(e.getMessage());
            //输出错误信息到控制台
            e.printStackTrace();
            //返回错误信息
            return new R<>(R.FAIL, "发生未知错误，请联系管理员！！！");
        }


    }
}
