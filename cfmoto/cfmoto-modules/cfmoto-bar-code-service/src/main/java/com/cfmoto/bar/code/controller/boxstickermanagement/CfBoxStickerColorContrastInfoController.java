package com.cfmoto.bar.code.controller.boxstickermanagement;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.cfmoto.bar.code.model.entity.CfBoxStickerColorContrastInfo;
import com.cfmoto.bar.code.service.boxstickermanagement.ICfBoxStickerColorContrastInfoService;
import com.cfmoto.bar.code.utiles.ExcelUtiles;
import com.github.pig.common.util.QueryPage;
import com.github.pig.common.util.R;
import com.github.pig.common.util.UserUtils;
import com.github.pig.common.web.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 箱外贴颜色对照信息表 前端控制器
 * </p>
 *
 * @author FangWenFei
 * @since 2019-04-24
 */
@RestController
@RequestMapping("/cfBoxStickerColorContrastInfo")
@Api(tags = " 箱外贴颜色对照信息表")
public class CfBoxStickerColorContrastInfoController extends BaseController {
    @Autowired
    private ICfBoxStickerColorContrastInfoService cfBoxStickerColorContrastInfoService;

    /**
     * 通过ID查询
     *
     * @param id ID
     * @return CfBoxStickerColorContrastInfo
     */
    @PostMapping("/getById")
    @ApiOperation(value = "通过ID查询")
    public R<CfBoxStickerColorContrastInfo> get(@RequestParam Integer id) {
        return new R<>(cfBoxStickerColorContrastInfoService.selectById(id));
    }


    /**
     * 分页按条件查询信息
     *
     * @param params 分页对象和条件
     * @return 分页对象
     */
    @GetMapping("/searchPageByCondition")
    @ApiOperation(value = "分页查询箱外贴颜色对照信息表")
    public R<Page> page(@RequestParam Map<String, Object> params) {
        String condition = (String) params.get("condition");
        Wrapper<CfBoxStickerColorContrastInfo> entity = new EntityWrapper<CfBoxStickerColorContrastInfo>()
                .like("cf_box_sticker_color_contrast_info_id", condition).or()
                .like("publicity_color", condition).or()
                .like("ok_color", condition).or()
                .like("english_color", condition);
        return new R<>(cfBoxStickerColorContrastInfoService.selectPage(new QueryPage<>(params), entity));
    }

    /**
     * 添加
     *
     * @param cfBoxStickerColorContrastInfo 实体
     * @return success/false
     */
    @PostMapping("/add")
    @ApiOperation(value = "添加箱外贴颜色对照信息表")
    public R<Boolean> add(@RequestBody CfBoxStickerColorContrastInfo cfBoxStickerColorContrastInfo, HttpServletRequest httpServletRequest) {
        try {
            int userId = UserUtils.getUserId(httpServletRequest);
            cfBoxStickerColorContrastInfo.setObjectSetBasicAttribute(userId, new Date());
            return new R<>(cfBoxStickerColorContrastInfoService.insert(cfBoxStickerColorContrastInfo));
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
    @GetMapping("/deleteById")
    @ApiOperation(value = "删除箱外贴颜色对照信息表通过ID")
    public R<Boolean> delete(@RequestParam Integer id) {
        return new R<>(cfBoxStickerColorContrastInfoService.deleteById(id));
    }

    /**
     * 编辑
     *
     * @param cfBoxStickerColorContrastInfo 实体
     * @return success/false
     */
    @PostMapping("/edit")
    @ApiOperation(value = "编辑除箱外贴颜色对照信息表")
    public R<Boolean> edit(@RequestBody CfBoxStickerColorContrastInfo cfBoxStickerColorContrastInfo) {
        return new R<>(cfBoxStickerColorContrastInfoService.updateById(cfBoxStickerColorContrastInfo));
    }


    @PostMapping("addOrEdit")
    @ApiOperation(value = "新增或修改信息表数据")
    public R<Boolean> addOrEdit(@RequestBody CfBoxStickerColorContrastInfo cfBoxStickerColorContrastInfo) {
        return new R<>(cfBoxStickerColorContrastInfoService.addOrEdit(cfBoxStickerColorContrastInfo, getUserId()));
    }

    /**
     * 导出
     *
     * @param response
     */
    @RequestMapping("/export")
    public void export(@RequestParam String condition, HttpServletResponse response) {
        Wrapper<CfBoxStickerColorContrastInfo> entity = new EntityWrapper<CfBoxStickerColorContrastInfo>()
                .like("cf_box_sticker_color_contrast_info_id", condition).or()
                .like("publicity_color", condition).or()
                .like("ok_color", condition).or()
                .like("english_color", condition);
        List<CfBoxStickerColorContrastInfo> infoList = cfBoxStickerColorContrastInfoService.selectList(entity);
        //导出操作
        ExcelUtiles.exportExcel(infoList, "箱外贴颜色对照信息", "箱外贴颜色对照信息", CfBoxStickerColorContrastInfo.class, "箱外贴颜色对照信息.xls", response);
    }

    /**
     * 导入
     */
    @RequestMapping("importExcel")
    public Map<String, String> importExcel(@RequestParam("file") MultipartFile file, HttpServletRequest httpServletRequest) {
        Map<String, String> resultMap = new HashMap<>();
        int userId = UserUtils.getUserId(httpServletRequest);
        resultMap.put("FileName", file.getOriginalFilename());
        List<CfBoxStickerColorContrastInfo> personList = ExcelUtiles.importExcel(file, 1, 1, CfBoxStickerColorContrastInfo.class);
        //也可以使用MultipartFile,使用 FileUtil.importExcel(MultipartFile file, Integer titleRows, Integer headerRows, Class<T>pojoClass)导入
        System.out.println("导入数据一共【" + personList.size() + "】行");
        logger.info("导入数据一共 :" + JSONArray.toJSON(personList).toString());
        Date date = new Date();
        personList.forEach(cfBoxStickerColorContrastInfo -> {
            cfBoxStickerColorContrastInfo.setObjectSetBasicAttribute(userId, date);
            cfBoxStickerColorContrastInfo.setImportTime(date);
            cfBoxStickerColorContrastInfo.setImportUser(String.valueOf(userId));
        });
        cfBoxStickerColorContrastInfoService.insertBatch(personList);
        return resultMap;

    }

}