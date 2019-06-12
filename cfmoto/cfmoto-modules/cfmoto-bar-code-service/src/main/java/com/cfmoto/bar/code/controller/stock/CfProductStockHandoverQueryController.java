package com.cfmoto.bar.code.controller.stock;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.cfmoto.bar.code.model.entity.CfProductPickedHandoverScanRecord;
import com.cfmoto.bar.code.model.vo.CfStockVo;
import com.cfmoto.bar.code.service.ICfProductPickedHandoverScanRecordService;
import com.cfmoto.bar.code.service.ICfProductStockHandoverQueryService;
import com.github.pig.common.util.R;
import com.github.pig.common.web.BaseController;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 生产备料交接查询 前端控制器
 *
 * @author ye
 * @date 2019-04-23
 */
@RestController
@RequestMapping("productStockHandoverQuery")
@Log4j
public class CfProductStockHandoverQueryController extends BaseController {

    @Autowired
    private ICfProductStockHandoverQueryService cfProductStockHandoverQueryService;


    @Autowired
    private ICfProductPickedHandoverScanRecordService productPickedHandoverScanRecordService;

    /**
     * 根据备料单查询备料信息表和备料清单表数据返回给前端
     *
     * @param stockListNo 备料单号
     * @return r对象
     */
    @GetMapping("getDataByStockListNo")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "stockListNo", value = "备料单号", dataType = "string", paramType = "query")
    })

    public R<CfStockVo> getDataByStockListNo(String stockListNo) {

        //校验数据
        if (StrUtil.isBlank(stockListNo)) {
            return new R<>(R.FAIL, "请输入备料单号!!!");
        }

        //调用业务层进行数据查询
        try {
            CfStockVo stockVo = cfProductStockHandoverQueryService.getDataByStockListNo(stockListNo);
            return new R<>(stockVo);
        } catch (Exception e) {
            //输出错误信息到控制台
            e.printStackTrace();
            //记录错误日志
            log.error(e.getMessage());
            //返回错误信息
            return new R<>(R.FAIL, e.getMessage());
        }
    }

    /**
     * 根据备料清单表id查询对应的生产领料交接扫描表数据
     *
     * @param stockInventoryId 备料清单表主键id
     * @return R
     */
    @GetMapping("getProductPickedHandoverScanRecordById")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "stockInventoryId", value = "备料清单表主键ID", dataType = "string", paramType = "query")
    })
    public R<CfStockVo> getProductPickedHandoverScanRecordById(Integer stockInventoryId) {
        try {
            List<CfProductPickedHandoverScanRecord> scanRecordList = productPickedHandoverScanRecordService.selectList(new EntityWrapper<CfProductPickedHandoverScanRecord>().eq("stock_inventory_id", stockInventoryId));
            CfStockVo stockVo = new CfStockVo();
            stockVo.setProductPickedHandoverScanRecordList(scanRecordList);
            return new R<>(stockVo);
        } catch (Exception e) {
            //打印错误信息到控制台
            e.printStackTrace();
            log.error(e.getMessage());
            return new R<>(R.FAIL, e.getMessage());
        }

    }

}
