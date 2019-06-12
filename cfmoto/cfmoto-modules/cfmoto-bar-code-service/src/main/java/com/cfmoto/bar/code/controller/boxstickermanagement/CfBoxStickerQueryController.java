package com.cfmoto.bar.code.controller.boxstickermanagement;

import com.baomidou.mybatisplus.plugins.Page;
import com.cfmoto.bar.code.model.entity.CfBarcodeBind;
import com.cfmoto.bar.code.model.entity.CfBoxStickerColorContrastInfo;
import com.cfmoto.bar.code.service.boxstickermanagement.ICfBoxStickerQueryService;
import com.cfmoto.bar.code.utiles.ExcelUtiles;
import com.github.pig.common.util.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 箱外贴查询 前端控制器
 *
 * @author ye
 * @date 2019-04-25
 */

@RestController
@RequestMapping("/boxStickerQuery")
@Api(tags = " 箱外贴查询")
public class CfBoxStickerQueryController {


    @Autowired
    private ICfBoxStickerQueryService boxStickerQueryService;


    /***
     * 分页过滤查询
     * @param params 分页条件和过滤信息
     * @return r
     */
    @PostMapping("searchPageByFilters")
    @ApiOperation(value = "分页条件查询")
    public R<Page> searchPageByFilters(@RequestBody Map<String, Object> params) {
        return new R<>(boxStickerQueryService.selectPageByFilters(params));
    }


    /**
     * 导出箱外贴查询结果报表
     *
     * @param params   传入参数
     * @param response 响应对象
     */

    @PostMapping("export")
    @ApiOperation(value = "导出报表")
    public void export(@RequestBody Map<String, Object> params, HttpServletResponse response) {
        List<CfBarcodeBind> barcodeBindList = boxStickerQueryService.export(params);
        ExcelUtiles.exportExcel(barcodeBindList, "箱外贴查询报表", "箱外贴查询报表", CfBarcodeBind.class, "箱外贴查询报表.xls", response);
    }


}
