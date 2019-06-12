package com.cfmoto.bar.code.service.impl.ckdMaterielBoxImpl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.cfmoto.bar.code.mapper.CfLoadPackingMapper;
import com.cfmoto.bar.code.mapper.CfMaterielBoxMapper;
import com.cfmoto.bar.code.mapper.SapJobOrderTempMapper;
import com.cfmoto.bar.code.model.dto.SapJobOrderTemp;
import com.cfmoto.bar.code.model.entity.CfLoadPacking;
import com.cfmoto.bar.code.model.entity.CfMaterielBox;
import com.cfmoto.bar.code.service.ckdMaterielBox.ICfMaterielBoxExportService;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* **********************************************************************
 *              Created by FangWenFei on 2019/4/10.                     
 * **********************************************************************
 * **********Thunderstorm, rain and dew are all grace of heaven**********
 * **********************************************************************
 */
@Service
public class ICfMaterielBoxExportServiceImpl extends ServiceImpl<CfMaterielBoxMapper, CfMaterielBox> implements ICfMaterielBoxExportService {
    @Autowired
    SapJobOrderTempMapper sapJobOrderTempMapper;
    @Autowired
    CfLoadPackingMapper cfLoadPackingMapper ;

    @Override
    public SXSSFWorkbook  export(CfMaterielBox cfMaterielBoxParams) {



        //创建poi导出数据对象
        SXSSFWorkbook sxssfWorkbook = new SXSSFWorkbook();
        CellStyle cellStyle = sxssfWorkbook.createCellStyle();  //新建单元格样式
        cellStyle.setAlignment(CellStyle.ALIGN_CENTER);//水平居中
        cellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);//垂直居中
/*        cellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);*/
        cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框
        cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框
        cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框
        cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框
        cellStyle.setWrapText(true);

        Font font = sxssfWorkbook.createFont();
        font.setFontName("黑体");
        font.setFontHeightInPoints((short) 10);//设置字体大小
        cellStyle.setFont(font);

        //创建sheet页
        SXSSFSheet sheet = sxssfWorkbook.createSheet("物料清单");
        sheet.setForceFormulaRecalculation(true);
        sheet.setColumnWidth(0, 9 *  256);
        sheet.setColumnWidth(1, 11 *  256);
        sheet.setColumnWidth(2, 23 *  256);
        sheet.setColumnWidth(3, 15 *  256);
        sheet.setColumnWidth(4, 10 *  256);
        sheet.setColumnWidth(5, 20 *  256);

/*        CellRangeAddress region1 = new CellRangeAddress(0, 1, 0, 6);
        //参数1：起始行 参数2：终止行 参数3：起始列 参数4：终止列
        sheet.addMergedRegion(region1);*/
        SXSSFRow headTitle = sheet.createRow(0);
        headTitle.setHeight((short)500);

        SXSSFCell cell0=headTitle.createCell(0);
        XSSFRichTextString rich0 = new XSSFRichTextString("车型\r" + "Vehicle");
        cell0.setCellValue(rich0);
        cell0.setCellStyle(cellStyle);

        SXSSFCell cell1=headTitle.createCell(1);
        XSSFRichTextString rich1 = new XSSFRichTextString("订单合同号\r" + "Order No.");
        cell1.setCellValue(rich1);
        cell1.setCellStyle(cellStyle);


        SXSSFCell cell2=headTitle.createCell(2);
        XSSFRichTextString rich2 = new XSSFRichTextString("重量\r" + "Kg");
        cell2.setCellValue(rich2);
        cell2.setCellStyle(cellStyle);


        SXSSFCell cell3=headTitle.createCell(3);
        XSSFRichTextString rich3 = new XSSFRichTextString("体积\r" + "Volume");
        cell3.setCellValue(rich3);
        cell3.setCellStyle(cellStyle);


        CellRangeAddress region0 = new CellRangeAddress(0, 0, 4, 5);
        sheet.addMergedRegion(region0);
        SXSSFCell cell4=headTitle.createCell(4);
        XSSFRichTextString rich4 = new XSSFRichTextString("箱号\r" + "Box No.");
        cell4.setCellValue(rich4);
        cell4.setCellStyle(cellStyle);
        SXSSFCell cell5=headTitle.createCell(5);
        cell5.setCellStyle(cellStyle);

        cfMaterielBoxParams=this.selectById(cfMaterielBoxParams.getBarCodeId());
        if(cfMaterielBoxParams==null){
            return null;
        }

        String  documentNo=cfMaterielBoxParams.getDocumentNo().split("&")[0];
        SapJobOrderTemp sapJobOrderTemp=new SapJobOrderTemp();
        sapJobOrderTemp.setSalesOrder(cfMaterielBoxParams.getSalesOrder());
        sapJobOrderTemp.setMaterialNo(sapJobOrderTemp.getMaterialNo());
        sapJobOrderTemp.setJobOrderNo(documentNo);
        sapJobOrderTemp= sapJobOrderTempMapper.selectOne(sapJobOrderTemp);


        ////ROW2第二行数据

        SXSSFRow headRow = sheet.createRow(1);
        //设置表头信息
        SXSSFCell cellL0= headRow.createCell(0);
        if(sapJobOrderTemp==null){
            cellL0.setCellValue("");
        }else{
            cellL0.setCellValue(sapJobOrderTemp.getModel());
        }
        cellL0.setCellStyle(cellStyle);

        SXSSFCell cellL1= headRow.createCell(1);
        cellL1.setCellValue(cfMaterielBoxParams.getSalesOrder());
        cellL1.setCellStyle(cellStyle);

        SXSSFCell cellL2= headRow.createCell(2);
        cellL2.setCellValue(cfMaterielBoxParams.getWeight().toPlainString());
        cellL2.setCellStyle(cellStyle);

        SXSSFCell cellL3= headRow.createCell(3);
        cellL3.setCellValue(cfMaterielBoxParams.getModel());
        cellL3.setCellStyle(cellStyle);

        CellRangeAddress region1 = new CellRangeAddress(1, 1, 4, 5);
        sheet.addMergedRegion(region1);
        SXSSFCell cellL4= headRow.createCell(4);
        cellL4.setCellValue(cfMaterielBoxParams.getBarCodeNo());
        cellL4.setCellStyle(cellStyle);
        SXSSFCell cellL5=headRow.createCell(5);
        cellL5.setCellStyle(cellStyle);

        //ROW3第三行数据
        SXSSFRow row2 = sheet.createRow(2);
        row2.setHeight((short)500);

        SXSSFCell cellT0=row2.createCell(0);
        XSSFRichTextString richT0 = new XSSFRichTextString("序号\r" + "Serial No.");
        cellT0.setCellValue(richT0);
        cellT0.setCellStyle(cellStyle);

        SXSSFCell cellT1=row2.createCell(1);
        XSSFRichTextString richT1 = new XSSFRichTextString("物料代码\r" + "Material No.");
        cellT1.setCellValue(richT1);
        cellT1.setCellStyle(cellStyle);


        SXSSFCell cellT2=row2.createCell(2);
        XSSFRichTextString richT2 = new XSSFRichTextString("物料名称\r" + "Material Name");
        cellT2.setCellValue(richT2);
        cellT2.setCellStyle(cellStyle);


        SXSSFCell cellT3=row2.createCell(3);
        XSSFRichTextString richT3 = new XSSFRichTextString("英文名称\r" + "English Name");
        cellT3.setCellValue(richT3);
        cellT3.setCellStyle(cellStyle);

        SXSSFCell cellT4=row2.createCell(4);
        XSSFRichTextString richT4 = new XSSFRichTextString("数量\r" + "Qty");
        cellT4.setCellValue(richT4);
        cellT4.setCellStyle(cellStyle);

        SXSSFCell cellT5=row2.createCell(5);
        XSSFRichTextString richT5 = new XSSFRichTextString("备注\r" + "Remark");
        cellT5.setCellValue(richT5);
        cellT5.setCellStyle(cellStyle);
        int rowIndex=3;
        List<CfMaterielBox> cfMaterielBoxList=this.selectList(new EntityWrapper<CfMaterielBox>()
                .eq("parent_no",cfMaterielBoxParams.getBarCodeNo())
                .eq(CfMaterielBox.SQL_TYPE,CfMaterielBox.CF_TYPE_2)
                .groupBy("material_no,english_name,material_name")
                .setSqlSelect("material_no,sum(qty) qty ,english_name ,material_name ")
        );
        for(CfMaterielBox cfMaterielBox :cfMaterielBoxList){
            SXSSFRow rowLine = sheet.createRow(rowIndex);
            /*rowLine.setHeight((short)500);*/
             //序列号
            SXSSFCell cellLine0=rowLine.createCell(0);
            cellLine0.setCellValue(rowIndex-2);
            cellLine0.setCellStyle(cellStyle);
            //物料代码
            SXSSFCell cellLine1=rowLine.createCell(1);
            XSSFRichTextString richLine1 = new XSSFRichTextString(cfMaterielBox.getMaterialNo());
            cellLine1.setCellValue(richLine1);
            cellLine1.setCellStyle(cellStyle);

            //物料名称
            SXSSFCell cellLine2=rowLine.createCell(2);
            XSSFRichTextString richLine2 = new XSSFRichTextString(cfMaterielBox.getMaterialName());
            cellLine2.setCellValue(richLine2);
            cellLine2.setCellStyle(cellStyle);

            //英文名称
            SXSSFCell cellLine3=rowLine.createCell(3);
            XSSFRichTextString richLine3 = new XSSFRichTextString(cfMaterielBox.getEnglishName());
            cellLine3.setCellValue(richLine3);
            cellLine3.setCellStyle(cellStyle);
            //数量
            SXSSFCell cellLine4=rowLine.createCell(4);
            XSSFRichTextString richLine4 = new XSSFRichTextString(cfMaterielBox.getQty().toPlainString());
            cellLine4.setCellValue(richLine4);
            cellLine4.setCellStyle(cellStyle);
            //备注
            SXSSFCell cellLine5=rowLine.createCell(5);
            XSSFRichTextString richLine5 = new XSSFRichTextString("");
            cellLine5.setCellValue(richLine5);
            cellLine5.setCellStyle(cellStyle);
            rowIndex=rowIndex+1;
        }

        return sxssfWorkbook;
    }

    @Override
    public Map<String, Object> getExportData(CfMaterielBox cfMaterielBoxParams) {
        Map<String, Object> resultMap=new HashMap<>();
        cfMaterielBoxParams=this.selectById(cfMaterielBoxParams.getBarCodeId());
        if(cfMaterielBoxParams==null){
            return null;
        }
        String  documentNo=cfMaterielBoxParams.getDocumentNo().split("&")[0];
        SapJobOrderTemp sapJobOrderTemp=new SapJobOrderTemp();
        sapJobOrderTemp.setSalesOrder(cfMaterielBoxParams.getSalesOrder());
        sapJobOrderTemp.setMaterialNo(sapJobOrderTemp.getMaterialNo());
        sapJobOrderTemp.setJobOrderNo(documentNo);
        sapJobOrderTemp= sapJobOrderTempMapper.selectOne(sapJobOrderTemp);

        List<CfMaterielBox> cfMaterielBoxList=this.selectList(new EntityWrapper<CfMaterielBox>()
                .eq("parent_no",cfMaterielBoxParams.getBarCodeNo())
                .eq(CfMaterielBox.SQL_TYPE,CfMaterielBox.CF_TYPE_2)
                .groupBy("material_no,english_name,material_name")
                .setSqlSelect("material_no,sum(qty) qty ,english_name ,material_name ")
        );
        for(int i=0;i<cfMaterielBoxList.size();i++){
            cfMaterielBoxList.get(i).setBarCodeId(i+1);
        }
        resultMap.put("sapJobOrderTemp",sapJobOrderTemp);
        resultMap.put("cfMaterielBoxList",cfMaterielBoxList);
        return resultMap;
    }
}