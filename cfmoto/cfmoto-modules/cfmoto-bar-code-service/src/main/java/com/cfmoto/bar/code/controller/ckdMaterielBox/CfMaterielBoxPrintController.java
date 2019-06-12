package com.cfmoto.bar.code.controller.ckdMaterielBox;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.cfmoto.bar.code.model.dto.SelectList;
import com.cfmoto.bar.code.model.entity.CfMaterielBox;
import com.cfmoto.bar.code.service.ckdMaterielBox.ICfMaterielBoxExportService;
import com.cfmoto.bar.code.service.ckdMaterielBox.ICfMaterielBoxService;
import com.github.pig.common.util.R;
import com.github.pig.common.web.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/* **********************************************************************
 *              Created by FangWenFei on 2019/2/26.                     
 * **********************************************************************
 * **********Thunderstorm, rain and dew are all grace of heaven**********
 * **********************************************************************
 */
@RestController
@RequestMapping("/cfMaterielBoxPrint")
@Api(tags="装箱清单打印（PC端） ")
public class CfMaterielBoxPrintController extends BaseController {
    @Autowired
    private ICfMaterielBoxService cfMaterielBoxService;
    @Autowired
    ICfMaterielBoxExportService iCfMaterielBoxExportService;
    /**
     * 装箱清单打印获取销售订单list
     *
     */
    @PostMapping("/selectSalesOrderList")
    @ApiOperation(value="获取销售订单list")
    public R<List<SelectList>> selectSalesOrderList(@RequestBody Map<String, Object> params, HttpServletRequest httpServletRequest) {
        try{
            return new R<>(cfMaterielBoxService.selectAllSalesOrderNo());
        }catch (Exception e){
            return new R<>(R.FAIL, CfMaterielBox.CF_MATERIEL_BOX_GET_DATA);
        }
    }

    /**
     * 通过销售订单获取单据号list
     *
     */
    @PostMapping("/selectDocumentNoListBySalesOrder")
    @ApiOperation(value="通过销售订单获取单据号list")
    public R< List<SelectList>> selectDocumentNoListBySalesOrder(@RequestBody Map<String, Object> params,HttpServletRequest httpServletRequest) {
        try{
            String salesOrder= params.getOrDefault("salesOrderNo", "").toString();
            // documentNo
            return new R<>(cfMaterielBoxService.selectDocumentNoBySalesOrderNo(salesOrder));
        }catch (Exception e){
            return new R<>(R.FAIL,CfMaterielBox.CF_MATERIEL_BOX_GET_DATA);
        }
    }
    /**
     * 通过销售订单和单据号获取箱号
     */
    @PostMapping("/selectListByCfMaterielBox")
    @ApiOperation(value="通过销售订单和单据号获取箱号")
    public R<List<CfMaterielBox>> selectListByCfMaterielBox(@RequestBody Map<String, Object> params, CfMaterielBox cfMaterielBox) {
        String salesOrder= params.getOrDefault("salesOrderNo", "").toString();
        String documentNo= params.getOrDefault("documentNo", "").toString();
        if(StringUtils.isNotBlank(salesOrder)){
            cfMaterielBox.setSalesOrder(salesOrder);
        }if(StringUtils.isNotBlank(documentNo)){
            cfMaterielBox.setDocumentNo(documentNo);
        }
        cfMaterielBox.setType(CfMaterielBox.CF_TYPE_1);
        return new R<>(cfMaterielBoxService.selectList(new EntityWrapper<>(cfMaterielBox)));
    }


    /**
     * 用于单方法体测试
     * @param params
     * @return
     */
    @PostMapping("/getDataToSap")
    @ApiOperation(value="getDataToSap")
    public ArrayList< Map<String,String>> getDataToSap(@RequestBody Map<String, Object> params) {
        String parentNo= params.getOrDefault("parentNo", "").toString();
        String lumpNo= params.getOrDefault("lumpNo", "").toString();
        String salesOrder= params.getOrDefault("salesOrder", "").toString();
        ArrayList< Map<String,String>> list =new ArrayList<>();
        return cfMaterielBoxService.getDataToSap(list,parentNo,lumpNo,salesOrder);
    }

    @RequestMapping("/getExportData")
    public R<Map<String, Object>> getExportData(@RequestParam Map<String, Object> params, HttpServletResponse response){
        try {
            String barCodeIdSt= params.getOrDefault("barCodeId", "").toString();
            String barCodeNo= params.getOrDefault("barCodeNo", "").toString();
            Integer barCodeId=Integer.parseInt(barCodeIdSt);
            CfMaterielBox cfMaterielBoxParams=new CfMaterielBox ();
            cfMaterielBoxParams.setBarCodeId(barCodeId);
            Map<String, Object> resultMap= iCfMaterielBoxExportService.getExportData(cfMaterielBoxParams);
            return new R<Map<String, Object>>(resultMap);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new R<>(R.FAIL,CfMaterielBox.CF_MATERIEL_BOX_GET_DATA);
    }

    @RequestMapping("/export/cfMaterielBoxPrint")
    public void export(@RequestParam Map<String, Object> params, HttpServletResponse response){
        SXSSFWorkbook sxssfWorkbook=null;
        ServletOutputStream outputStream=null;
        try {
            String barCodeIdSt= params.getOrDefault("barCodeId", "").toString();
            String barCodeNo= params.getOrDefault("barCodeNo", "").toString();
            Integer barCodeId=Integer.parseInt(barCodeIdSt);
            CfMaterielBox cfMaterielBoxParams=new CfMaterielBox ();
            cfMaterielBoxParams.setBarCodeId(barCodeId);
             sxssfWorkbook= iCfMaterielBoxExportService.export(cfMaterielBoxParams);
            // 下载导出
            String filename = barCodeNo+"Detailed_List";
            // 设置头信息http://localhost:9206/cfMaterielBoxPrint/export
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/vnd.ms-excel");
            //一定要设置成xlsx格式
            response.setHeader("Content-Disposition", "attachment;filename=" +filename+ ".xls");
            //创建一个输出流
            outputStream = response.getOutputStream();
            //写入数据
            sxssfWorkbook.write(outputStream);

            // 关闭
            outputStream.close();
            sxssfWorkbook.close();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(outputStream!=null){
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }if(sxssfWorkbook!=null){
                try {
                    sxssfWorkbook.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }


    }

}
