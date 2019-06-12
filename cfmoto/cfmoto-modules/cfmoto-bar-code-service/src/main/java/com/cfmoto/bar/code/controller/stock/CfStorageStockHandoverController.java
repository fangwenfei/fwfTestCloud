package com.cfmoto.bar.code.controller.stock;


import com.cfmoto.bar.code.model.dto.CfStockSplitDto;
import com.cfmoto.bar.code.service.ICfStorageStockHandoverService;
import com.cfmoto.bar.code.service.ISapApiService;
import com.github.pig.common.util.R;
import com.github.pig.common.web.BaseController;
import com.xiaoleilu.hutool.util.StrUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 仓库备料交接 前端控制器
 *
 * @author ye
 * @since 2019-04-22
 */
@RestController
@RequestMapping("/cfStorageStockHandover")
@Api(tags = "仓库备料交接")
@Log4j
public class CfStorageStockHandoverController extends BaseController {


    @Autowired
    private ICfStorageStockHandoverService cfStorageStockHandoverService;

    @Autowired
    private ISapApiService sapApiService;

    /**
     * 扫描备料标识条码
     *
     * @param barcode 条码
     * @param request 请求对象
     * @return r
     */
    @GetMapping("scan")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "barcode", value = "备料标识条码", dataType = "string", paramType = "query")
    })
    public R scan(String barcode, HttpServletRequest request) {

        //校验数据
        if (StrUtil.isBlank(barcode)) {
            return new R(R.FAIL, "请输入有效的数据!!!");
        }

        try {
            //根据条码去备料拆分表和备料信息表中获取调用SAP接口所需要的方法参数
            List<CfStockSplitDto> dtoList = cfStorageStockHandoverService.scan(barcode);
            //调用SAP32接口
            sapApiService.getDataFromSapApi032(dtoList);

            //更新备料清单表中备料交接数量字段
            //插入交接数据记录在生产领料交接记录表
            // 和删除备料拆分表的父类（如有）和自己
            cfStorageStockHandoverService.updateDataAfterSap(dtoList, barcode, getUserId());

            return new R();
        } catch (NullPointerException ne) {
            ne.printStackTrace();
            log.error(ne.getMessage());
            return new R(R.FAIL, "数据有误，请注意!!!");
        } catch (Exception e) {
            //打印错误信息到控制台
            e.printStackTrace();
            //记录日志
            log.error(e.getMessage());
            //返回错误消息
            return new R(R.FAIL, e.getMessage());
        }
    }
}
