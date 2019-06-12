package com.cfmoto.bar.code.controller.lableprint;

import com.baomidou.mybatisplus.enums.SqlLike;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.cfmoto.bar.code.model.entity.CfAllotInventory;
import com.cfmoto.bar.code.model.entity.CfPartLabelRelationship;
import com.cfmoto.bar.code.model.entity.CfPrintFunction;
import com.cfmoto.bar.code.model.entity.CfPrintLodopTemplate;
import com.cfmoto.bar.code.model.vo.CfAllotManagementVo;
import com.cfmoto.bar.code.model.vo.ItemPrintTemplateOutVo;
import com.cfmoto.bar.code.model.vo.PartsLabelPrintVo;
import com.cfmoto.bar.code.service.ICfPrintFunctionService;
import com.cfmoto.bar.code.service.ICfPrintLodopTemplateService;
import com.cfmoto.bar.code.service.ISapApiService;
import com.cfmoto.bar.code.service.lableprint.ICfPartLabelRelationshipService;
import com.cfmoto.bar.code.utiles.ExcelUtiles;
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
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.*;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author space
 * @since 2019-04-20
 */
@RestController
@RequestMapping("/cfPartLabelRelationship")
@Api(tags = " 部品零部件标签模板对照信息维护")
@Slf4j
public class CfPartLabelRelationshipController extends BaseController {

    @Autowired
    private ICfPartLabelRelationshipService cfPartLabelRelationshipService;

    @Autowired
    private ICfPrintLodopTemplateService cfPrintLodopTemplateService;

    @Autowired
    private ICfPrintFunctionService cfPrintFunctionService;

    @Autowired
    private ISapApiService iSapApiService;

    public static final String STATIC_FUNCTION_NAME = "partsPrint";


    /**
     * 通过物料代码和模板查询对照关系
     *
     * @param item          物料代码
     * @param labelTemplate 标签模板
     * @return
     */
    @GetMapping("/searchLabelRelationship")
    @ApiOperation(value = "通过物料代码和模板查询对照关系")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "item", value = "物料条码", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "labelTemplate", value = "标签模板", dataType = "string", paramType = "query")
    })
    public R<List<CfPartLabelRelationship>> searchLabelRelationship(String item, String labelTemplate) {

        if (StrUtil.isBlank(item)) {
            item = "";
        }
        if (StrUtil.isBlank(labelTemplate)) {
            labelTemplate = "";
        }
        List<CfPartLabelRelationship> cfPartLabelRelationshipList = new ArrayList<CfPartLabelRelationship>();
        try {
            EntityWrapper<CfPartLabelRelationship> wrapper = new EntityWrapper<CfPartLabelRelationship>();
            wrapper.like("item", item.trim(), SqlLike.RIGHT);
            wrapper.andNew().like("label_template", labelTemplate.trim(), SqlLike.RIGHT).orderBy("last_update_date", false);
            cfPartLabelRelationshipList = cfPartLabelRelationshipService.selectList(wrapper);
        } catch (Exception e) {
            log.error(ExceptionUtils.getFullStackTrace(e));
            return new R<List<CfPartLabelRelationship>>(R.FAIL, e.getMessage());
        }
        return new R<List<CfPartLabelRelationship>>(cfPartLabelRelationshipList);
    }

    /**
     * 导入
     *
     * @param file
     * @param httpServletRequest
     * @return
     */
    @PostMapping("/importExcel")
    @ApiOperation(value = "导入")
    public R<List<CfPartLabelRelationship>> importExcel(@RequestParam("file") MultipartFile file, HttpServletRequest httpServletRequest) {

        List<CfPartLabelRelationship> cfPartLabelRelationshipList = null;
        try {

            Map<String, Boolean> existMap = new HashMap<String, Boolean>();//存放已验证的模板
            int userId = UserUtils.getUserId(httpServletRequest);
            cfPartLabelRelationshipList = ExcelUtiles.importExcel(file, 1, 1, CfPartLabelRelationship.class);
            CfPartLabelRelationship cfPartLabelRelationship = null;
            Date importDate = new Date();
            for (int i = 0, len = cfPartLabelRelationshipList.size(); i < len; i++) {
                cfPartLabelRelationship = cfPartLabelRelationshipList.get(i);
                if (StrUtil.isBlank(cfPartLabelRelationship.getItem())) {
                    throw new Exception("第" + (i + 3) + "行物料代码不能为空");
                }
                if (StrUtil.isBlank(cfPartLabelRelationship.getLabelTemplate())) {
                    throw new Exception("第" + (i + 3) + "行标签模板不能为空");
                }
                cfPartLabelRelationship.setObjectSetBasicAttribute(userId, importDate);
                if (!existMap.containsKey(cfPartLabelRelationship.getLabelTemplate())) {
                    validatePrintLabel(cfPartLabelRelationship.getLabelTemplate());
                    existMap.put(cfPartLabelRelationship.getLabelTemplate(), true);
                }
            }
            cfPartLabelRelationshipService.customInsertOrSaveBatch(cfPartLabelRelationshipList);

            //查询数据
            //cfPartLabelRelationshipList = cfPartLabelRelationshipService.selectList( new EntityWrapper<CfPartLabelRelationship>() );

        } catch (Exception e) {
            log.error(ExceptionUtils.getFullStackTrace(e));
            return new R<>(R.FAIL, e.getMessage());
        }
        return new R<List<CfPartLabelRelationship>>(cfPartLabelRelationshipList);
    }

    /**
     * 验证打印模板
     *
     * @param printLabel
     * @return
     * @throws Exception
     */
    public boolean validatePrintLabel(String printLabel) throws Exception {

        //功能模块名
        CfPrintFunction cfPrintFunction = new CfPrintFunction();
        cfPrintFunction.setFunctionName(STATIC_FUNCTION_NAME);
        EntityWrapper<CfPrintFunction> wrapper = new EntityWrapper<CfPrintFunction>();
        wrapper.setEntity(cfPrintFunction);
        cfPrintFunction = cfPrintFunctionService.selectOne(wrapper);
        if (cfPrintFunction != null) {
            CfPrintLodopTemplate cfPrintLodopTemplate = new CfPrintLodopTemplate();
            cfPrintLodopTemplate.setFunctionId(cfPrintFunction.getFunctionId());
            cfPrintLodopTemplate.setPrintLodopTemplateName(printLabel);
            List<CfPrintLodopTemplate> cfPrintLodopTemplateList =
                    cfPrintLodopTemplateService.selectList(new EntityWrapper<CfPrintLodopTemplate>(cfPrintLodopTemplate));
            if (cfPrintLodopTemplateList.size() == 0) {
                throw new Exception("打印模板" + STATIC_FUNCTION_NAME + "模板" + printLabel + "未维护");
            } else {
                return true;
            }
        } else {
            throw new Exception("打印功能模块" + STATIC_FUNCTION_NAME + "未维护");
        }
    }

    /**
     * 导出
     *
     * @param item
     * @param labelTemplate
     * @param response
     */
    @GetMapping("/export")
    @ApiOperation(value = "导出")
    public void export(String item, String labelTemplate, HttpServletResponse response) {

        List<CfPartLabelRelationship> cfPartLabelRelationshipList = new ArrayList<CfPartLabelRelationship>();
        try {
            if (StrUtil.isBlank(item)) {
                item = "";
            }
            if (StrUtil.isBlank(labelTemplate)) {
                labelTemplate = "";
            }
            EntityWrapper<CfPartLabelRelationship> wrapper = new EntityWrapper<CfPartLabelRelationship>();
            wrapper.like("item", item.trim(), SqlLike.RIGHT);
            wrapper.andNew().like("label_template", labelTemplate.trim(), SqlLike.RIGHT).orderBy("last_update_date", false);
            cfPartLabelRelationshipList = cfPartLabelRelationshipService.selectList(wrapper);
            //导出操作
            ExcelUtiles.exportExcel(cfPartLabelRelationshipList, "部品零部件标签模板对照信息维护", "部品零部件标签模板对照信息维护",
                    CfPartLabelRelationship.class, "部品零部件标签模板对照信息维护.xls", response);
        } catch (Exception e) {
            log.error(ExceptionUtils.getFullStackTrace(e));
        }
    }


    /**
     * 通过ID查询
     *
     * @param id ID
     * @return CfPartLabelRelationship
     */
    @PostMapping("/getById")
    @ApiOperation(value = "通过ID查询")
    public R<CfPartLabelRelationship> get(@RequestParam Integer id) {
        return new R<>(cfPartLabelRelationshipService.selectById(id));
    }


    /**
     * 分页查询信息
     *
     * @param params 分页对象
     * @return 分页对象
     */
    @PostMapping("/page")
    @ApiOperation(value = "分页查询")
    public R<Page> page(@RequestParam Map<String, Object> params, CfPartLabelRelationship cfPartLabelRelationship) {
        return new R<>(cfPartLabelRelationshipService.selectPage(new QueryPage<>(params), new EntityWrapper<>(cfPartLabelRelationship)));
    }

    /**
     * 添加
     *
     * @param cfPartLabelRelationship 实体
     * @return success/false
     */
    @PostMapping("/add")
    @ApiOperation(value = "添加")
    public R<Boolean> add(@RequestBody CfPartLabelRelationship cfPartLabelRelationship, HttpServletRequest httpServletRequest) {
        try {
            int userId = UserUtils.getUserId(httpServletRequest);
            cfPartLabelRelationship.setObjectSetBasicAttribute(userId, new Date());
            if (StrUtil.isBlank(cfPartLabelRelationship.getItem())) {
                throw new Exception("物料代码不能为空");
            }
            if (StrUtil.isBlank(cfPartLabelRelationship.getLabelTemplate())) {
                throw new Exception("标签模板不能为空");
            }
            EntityWrapper<CfPartLabelRelationship> wrapper = new EntityWrapper<CfPartLabelRelationship>();
            CfPartLabelRelationship cfPartLabel = new CfPartLabelRelationship();
            cfPartLabel.setItem(cfPartLabelRelationship.getItem().trim());
            wrapper.setEntity(cfPartLabel);
            CfPartLabelRelationship cfPartLabelRelationship1 = cfPartLabelRelationshipService.selectOne(wrapper);
            if (cfPartLabelRelationship1 != null) {
                throw new Exception("物料标签模板已维护");
            }
            return new R<>(cfPartLabelRelationshipService.insert(cfPartLabelRelationship));
        } catch (Exception e) {
            log.error(ExceptionUtils.getFullStackTrace(e));
            return new R<>(R.FAIL, e.getMessage());
        }

    }

    @PostMapping("/updateByItem")
    @ApiOperation(value = "通过物料代码更新模板对应关系")
    public R<String> updateByItem(@RequestBody CfPartLabelRelationship cfPartLabelRelationship) {
        try {
            if (StrUtil.isBlank(cfPartLabelRelationship.getItem())) {
                throw new Exception("物料代码不能为空");
            }
            if (StrUtil.isBlank(cfPartLabelRelationship.getLabelTemplate())) {
                throw new Exception("模板不能为空");
            }
            EntityWrapper<CfPartLabelRelationship> wrapper = new EntityWrapper<CfPartLabelRelationship>();
            CfPartLabelRelationship cfPartLabelRelationship1 = new CfPartLabelRelationship();
            cfPartLabelRelationship1.setItem(cfPartLabelRelationship.getItem());
            cfPartLabelRelationship1.setLabelTemplate(cfPartLabelRelationship.getLabelTemplate());
            cfPartLabelRelationship1.setRemarks(cfPartLabelRelationship.getRemarks());
            wrapper.eq("item", cfPartLabelRelationship1.getItem());
            cfPartLabelRelationshipService.update(cfPartLabelRelationship1, wrapper);
        } catch (Exception e) {
            log.error(ExceptionUtils.getFullStackTrace(e));
            return new R<String>(R.FAIL, e.getMessage());
        }
        return new R<String>("success");
    }

    @GetMapping("/searchTemplate")
    @ApiOperation(value = "查询标签模板")
    public R<List<CfPrintLodopTemplate>> searchTemplate() {

        List<CfPrintLodopTemplate> cfPrintLodopTemplateList = new ArrayList<CfPrintLodopTemplate>();
        try {
            //功能模块名
            CfPrintFunction cfPrintFunction = new CfPrintFunction();
            cfPrintFunction.setFunctionName(STATIC_FUNCTION_NAME);
            EntityWrapper<CfPrintFunction> wrapper = new EntityWrapper<CfPrintFunction>();
            wrapper.setEntity(cfPrintFunction);
            cfPrintFunction = cfPrintFunctionService.selectOne(wrapper);
            if (cfPrintFunction != null) {
                CfPrintLodopTemplate cfPrintLodopTemplate = new CfPrintLodopTemplate();
                cfPrintLodopTemplate.setFunctionId(cfPrintFunction.getFunctionId());
                cfPrintLodopTemplateList = cfPrintLodopTemplateService.selectList(new EntityWrapper<CfPrintLodopTemplate>(cfPrintLodopTemplate));
            } else {
                throw new Exception("打印功能名称" + STATIC_FUNCTION_NAME + "未维护");
            }
        } catch (Exception e) {
            log.error(ExceptionUtils.getFullStackTrace(e));
            return new R<List<CfPrintLodopTemplate>>(R.FAIL, e.getMessage());
        }
        return new R<List<CfPrintLodopTemplate>>(cfPrintLodopTemplateList);
    }

    @GetMapping("/deleteByItem")
    @ApiOperation(value = "通过物料代码删除模板")
    public R<String> deleteByItem(String item) {
        try {
            if (StrUtil.isBlank(item)) {
                throw new Exception("物料代码不能为空");
            }
            EntityWrapper<CfPartLabelRelationship> wrapper = new EntityWrapper<CfPartLabelRelationship>();
            wrapper.eq("item", item.trim());
            cfPartLabelRelationshipService.delete(wrapper);
        } catch (Exception e) {
            log.error(ExceptionUtils.getFullStackTrace(e));
            return new R<>(R.FAIL, e.getMessage());
        }
        CfPartLabelRelationship cfPartLabelRelationship = new CfPartLabelRelationship();
        return new R<String>("success");
    }


    @GetMapping("/partsLabelPrintSearch")
    @ApiOperation(value = "部品零部件标签打印-查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "conTypeKey", value = "查询类型：I/物料代码，D/物料名称，O/两步调拨单", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "inputVal", value = "查询内容", dataType = "string", paramType = "query")
    })
    public R<List<PartsLabelPrintVo>> partsLabelPrintSearch(String conTypeKey, String inputVal) {

        List<PartsLabelPrintVo> partsLabelPrintVoList = new ArrayList<>();
        try {

            if (StrUtil.isBlank(conTypeKey)) {
                throw new Exception("查询类型不能为空");
            }
            if (StrUtil.isBlank(inputVal)) {
                throw new Exception("查询内容不能为空");
            }
            //调拨单查询
            if ("O".equalsIgnoreCase(conTypeKey)) {
                CfAllotManagementVo cfAllotManagementVo = iSapApiService.getDataFromSapApi08(inputVal.trim());
                List<CfAllotInventory> cfAllotInventoryList = cfAllotManagementVo.getCfAllotInventoryList();
                PartsLabelPrintVo partsLabelPrintVo = null;
                for (CfAllotInventory cfAllotInventory : cfAllotInventoryList) {
                    partsLabelPrintVo = new PartsLabelPrintVo();
                    partsLabelPrintVo.setOrderNo(cfAllotInventory.getOrderNo());
                    partsLabelPrintVo.setItem(cfAllotInventory.getMaterialsNo());
                    partsLabelPrintVo.setItemDesc(cfAllotInventory.getMaterialsName());
                    partsLabelPrintVo.setMode(cfAllotInventory.getSpec());
                    partsLabelPrintVo.setQty(new BigDecimal(cfAllotInventory.getNumber()));
                    partsLabelPrintVo.setSpStorageLocationPosition(cfAllotInventory.getSpStorePositionNo());
                    partsLabelPrintVo.setMapNumber("");
                    partsLabelPrintVo.setMinimumPackageNumber(cfAllotInventory.getMinimumPackageNumber());
                    partsLabelPrintVo.setSalePrice(cfAllotInventory.getSalePrice());
                    partsLabelPrintVo.setEnglishName(cfAllotInventory.getEnglishName());
                    partsLabelPrintVoList.add(partsLabelPrintVo);
                }

                //根据物料代码查询物料主数据
            } else if ("I".equalsIgnoreCase(conTypeKey)) {

                //调用ZMM_BC_001（物料主数据查询接口）
                return new R<>(iSapApiService.getDataFromMainData001(inputVal, ""));

                //根据物料名称查询物料主数据
            } else if ("D".equalsIgnoreCase(conTypeKey)) {

                return new R<>(iSapApiService.getDataFromMainData001("", inputVal));

            } else {
                throw new Exception("未知查询条件类型");
            }

        } catch (Exception e) {
            log.error(ExceptionUtils.getFullStackTrace(e));
            return new R<>(R.FAIL, e.getMessage());
        }
        return new R<>(partsLabelPrintVoList);
    }


    @PostMapping("/getPrintLabelByItems")
    @ApiOperation(value = "部品零部件标签打印-物料查询标签模板")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "itemList", value = "物料", dataType = "List<String>", paramType = "query")
    })
    public R<List<ItemPrintTemplateOutVo>> getPrintLabelByItems(@RequestBody List<String> itemList) {
        Map<String, Object> params = new HashMap<>();
        params.put(CfPrintLodopTemplate.FUNCTION_NAME_SQL, "warehousePositionPrint");
        List<ItemPrintTemplateOutVo> itemPrintTemplateOutVoList = new ArrayList<ItemPrintTemplateOutVo>();
        try {
            CfPrintLodopTemplate cfPrintLodopTemplateTemp = cfPrintLodopTemplateService.getPrintLodopTemplate(params);
            EntityWrapper<CfPartLabelRelationship> wrapper = new EntityWrapper<CfPartLabelRelationship>();
            wrapper.in("item", itemList);
            Map<String, String> itemMap = new HashMap<>();
            itemList.forEach(c -> {
                itemMap.put(c, c);
            });
            List<CfPartLabelRelationship> cfPartLabelRelationshipList = cfPartLabelRelationshipService.selectList(wrapper);
            if (cfPartLabelRelationshipList.size() == 0) {
                throw new Exception("物料" + itemList + "标签模板未维护");
            }
            if (cfPartLabelRelationshipList.size() < itemList.size()) {
                throw new Exception("物料" + itemList + "标签模板未维护完全");
            }
            //查询功能模块名
            CfPrintFunction cfPrintFunction = new CfPrintFunction();
            cfPrintFunction.setFunctionName(STATIC_FUNCTION_NAME);
            cfPrintFunction = cfPrintFunctionService.selectOne(new EntityWrapper<CfPrintFunction>(cfPrintFunction));
            if (cfPrintFunction == null) {
                throw new Exception("打印模块" + STATIC_FUNCTION_NAME + "未维护");
            }
            ItemPrintTemplateOutVo itemPrintTemplateOutVo = null;
            CfPrintLodopTemplate cfPrintLodopTemplate = null;
            CfPrintLodopTemplate cfPrintLodop = new CfPrintLodopTemplate();
            for (int i = 0, len = cfPartLabelRelationshipList.size(); i < len; i++) {
                itemMap.remove(cfPartLabelRelationshipList.get(i).getItem());
                cfPrintLodop.setFunctionId(cfPrintFunction.getFunctionId());
                cfPrintLodop.setPrintLodopTemplateName(cfPartLabelRelationshipList.get(i).getLabelTemplate());
                cfPrintLodopTemplate = cfPrintLodopTemplateService.selectOne(new EntityWrapper<CfPrintLodopTemplate>(cfPrintLodop));
                itemPrintTemplateOutVo = new ItemPrintTemplateOutVo();
                String printLodopTemplateName = cfPrintLodopTemplateTemp.getPrintLodopTemplateName();
                String printLodopTemplate = cfPrintLodopTemplateTemp.getPrintLodopTemplate();
                if (cfPrintLodopTemplate == null) {
                    printLodopTemplateName = cfPrintLodopTemplate.getPrintLodopTemplateName();
                    printLodopTemplate = cfPrintLodopTemplate.getPrintLodopTemplate();
                }
                if (StrUtil.isBlank(cfPrintLodopTemplate.getPrintLodopTemplate())) {
                    printLodopTemplateName = cfPrintLodopTemplate.getPrintLodopTemplateName();
                    printLodopTemplate = cfPrintLodopTemplate.getPrintLodopTemplate();
                }

                itemPrintTemplateOutVo.setItem(cfPartLabelRelationshipList.get(i).getItem());
                itemPrintTemplateOutVo.setPrintLodopTemplateName(printLodopTemplateName);
                itemPrintTemplateOutVo.setPrintLodopTemplate(printLodopTemplate);
                itemPrintTemplateOutVoList.add(itemPrintTemplateOutVo);
            }
            for (Map.Entry<String, String> mp : itemMap.entrySet()) {
                itemPrintTemplateOutVo = new ItemPrintTemplateOutVo();
                String printLodopTemplateName = cfPrintLodopTemplateTemp.getPrintLodopTemplateName();
                String printLodopTemplate = cfPrintLodopTemplateTemp.getPrintLodopTemplate();
                itemPrintTemplateOutVo.setItem(mp.getKey());
                itemPrintTemplateOutVo.setPrintLodopTemplateName(printLodopTemplateName);
                itemPrintTemplateOutVo.setPrintLodopTemplate(printLodopTemplate);
                itemPrintTemplateOutVoList.add(itemPrintTemplateOutVo);
            }

        } catch (Exception e) {
            log.error(ExceptionUtils.getFullStackTrace(e));
            return new R<List<ItemPrintTemplateOutVo>>(R.FAIL, e.getMessage());
        }
        return new R<List<ItemPrintTemplateOutVo>>(itemPrintTemplateOutVoList);
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
        CfPartLabelRelationship cfPartLabelRelationship = new CfPartLabelRelationship();
        return new R<>(cfPartLabelRelationshipService.updateById(cfPartLabelRelationship));
    }

    /**
     * 编辑
     *
     * @param cfPartLabelRelationship 实体
     * @return success/false
     */
    @PostMapping("/edit")
    @ApiOperation(value = "编辑除")
    public R<Boolean> edit(@RequestBody CfPartLabelRelationship cfPartLabelRelationship) {
        return new R<>(cfPartLabelRelationshipService.updateById(cfPartLabelRelationship));
    }
}
