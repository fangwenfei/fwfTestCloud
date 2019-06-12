package com.cfmoto.bar.code.service.boxstickermanagement.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.cfmoto.bar.code.model.entity.*;
import com.cfmoto.bar.code.model.options.PrintTypeOptions;
import com.cfmoto.bar.code.model.vo.ProductionTaskVo;
import com.cfmoto.bar.code.service.*;
import com.cfmoto.bar.code.service.boxstickermanagement.ICfBoxStickerColorContrastInfoService;
import com.cfmoto.bar.code.service.boxstickermanagement.ICfBoxStickerPrintService;
import com.cfmoto.bar.code.utiles.ValidateUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.bind.ValidationException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 箱外贴打印业务层接口实现类
 *
 * @author ye
 * @date 2019-04-29
 */
@Service
public class CfBoxStickerPrintServiceImpl implements ICfBoxStickerPrintService {

    @Autowired
    private ISapApiService sapApiService;

    @Autowired
    private ICfBarcodeBindService cfBarcodeBindService;

    @Autowired
    private ICfBarcodeInventoryService cfBarcodeInventoryService;

    @Autowired
    private ICfBoxStickerColorContrastInfoService boxStickerColorContrastInfoService;

    @Autowired
    private PrintTypeOptions printTypeOptions;

    @Autowired
    private ICfPrintFunctionService cfPrintFunctionService;

    @Autowired
    private ICfPrintLodopTemplateService cfPrintLodopTemplateService;

    @Override
    public CfBarcodeBind scanCarCpCode(String carCpCode, String printType, String carType) throws Exception {
        CfBarcodeBind returnBarcodeBind = new CfBarcodeBind();

        //CP在后台库存表查询，获取生产订单；获取失败，报错
        CfBarcodeInventory barcodeInventory = cfBarcodeInventoryService.selectOne(new EntityWrapper<CfBarcodeInventory>().eq("barcode", carCpCode));
        if (barcodeInventory == null) {
            throw new Exception("库存表中没有对应的整车CP条码,请注意!!!");
        }

        //获取库存表中的生产任务单号
        String orderNo = barcodeInventory.getProductionTaskOrder();
        if (StrUtil.isBlank(orderNo)) {
            throw new Exception("整车CP条码对应的库存表中的生产订单号获取失败,请注意!!!");
        }

        //生产订单信息调取SAP接口，获取数据；获取失败报错（订单合同号可以为空）
        ProductionTaskVo productionTaskVo = sapApiService.getDataFromSapApi004(orderNo);
        //校验SAP返回的数据
        if (StrUtil.isBlank(productionTaskVo.getCarType())) {
            throw new ValidationException("SAP返回的车型为空,请注意!!!");
        }

        if (StrUtil.isBlank(productionTaskVo.getItem())) {
            throw new ValidationException("SAP返回的物料代码为空,请注意!!!");
        }

        if (StrUtil.isBlank(productionTaskVo.getPublicityColor())) {
            throw new ValidationException("SAP返回的宣传颜色为空,请注意!!!");
        }

        //CP码在三码绑定表获取数据；获取失败报错
        CfBarcodeBind barcodeBind = cfBarcodeBindService.selectOne(new EntityWrapper<CfBarcodeBind>().eq("car", carCpCode));
        if (barcodeBind == null) {
            throw new Exception("通过整车CP条码获取三码绑定表数据失败,请注意!!!");
        }


        //查询三码绑定表，获取打印类型对应最后打印数据字段（5天）
        Date now = new Date();
        //计算出五天前的日期
        Date fiveDaysBefore = new Date(now.getTime() - 5 * 24 * 60 * 60 * 1000L);

        //计算出三十天前的日期
        Date thirtyDaysBefore = new Date(now.getTime() - 30 * 24 * 60 * 60 * 1000L);

        //获取五天内最新的一条数据
        List<CfBarcodeBind> cfBarcodeBinds = cfBarcodeBindService.selectList(new EntityWrapper<CfBarcodeBind>().eq("print_type", printType).and().between("box_label_print_date", fiveDaysBefore, now).orderBy("box_label_print_date", false));

        CfBarcodeBind newestBarcodeBind;

        //如果五天内没有三码绑定表，则创建一个空的三码绑定实体类,否则就取五天内的第一条
        if (null == cfBarcodeBinds || cfBarcodeBinds.size() == 0) {
            newestBarcodeBind = new CfBarcodeBind();
        } else {
            newestBarcodeBind = cfBarcodeBinds.get(0);
        }


        //按照合同号+车辆类型+国外/美国，获取行数据条数，箱号为箱号+1；获取失败，默认为1（打印类型为国内，箱号为空）（30天）
        int boxNumber = cfBarcodeBindService.selectCount(
                new EntityWrapper<CfBarcodeBind>()
                        .eq("contract_number", productionTaskVo.getContract())
                        .and().eq("car_type", carType)
                        .and().eq("print_type", printType)
                        .and().between("box_label_print_date", thirtyDaysBefore, now)) + 1;


        //根据宣传颜色获取对照表中的颜色信息
        CfBoxStickerColorContrastInfo boxStickerColorContrastInfo = boxStickerColorContrastInfoService.selectOne(new EntityWrapper<CfBoxStickerColorContrastInfo>().eq("publicity_color", productionTaskVo.getPublicityColor()));
        if (boxStickerColorContrastInfo == null) {
            throw new Exception("根据宣传颜色获取对照表中的颜色信息失败,请注意!!!");
        }


        //封装返回数据
        returnBarcodeBind.setCar(carCpCode);
        returnBarcodeBind.setBoxCode(boxNumber);
        returnBarcodeBind.setCarModel(productionTaskVo.getCarType());
        returnBarcodeBind.setFrame(barcodeBind.getFrame());
        returnBarcodeBind.setEngine(barcodeBind.getEngine());
        returnBarcodeBind.setProductLot(newestBarcodeBind.getProductLot());
        returnBarcodeBind.setMaterialCode(productionTaskVo.getItem());
        returnBarcodeBind.setContractNumber(StringUtils.trimToEmpty(productionTaskVo.getContract()));
        returnBarcodeBind.setPublicityColor(productionTaskVo.getPublicityColor());
        returnBarcodeBind.setSpecialCarConfiguration(newestBarcodeBind.getSpecialCarConfiguration());
        returnBarcodeBind.setRemark(newestBarcodeBind.getRemark());
        returnBarcodeBind.setOkColor(boxStickerColorContrastInfo.getOkColor());
        returnBarcodeBind.setEnglishColor(boxStickerColorContrastInfo.getEnglishColor());
        returnBarcodeBind.settBoxCode(barcodeBind.gettBoxCode());
        returnBarcodeBind.setUsaName(productionTaskVo.getUsaName());
        return returnBarcodeBind;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<String> printBoxSticker(CfBarcodeBind barcodeBind, Integer userId) throws Exception {
        List<String> printContentList = new ArrayList<>();
        //校验是否有箱标贴打印记录，有，报错
        String car = barcodeBind.getCar();
        String printType = barcodeBind.getPrintType();
        ValidateUtils<Object> validateUtils = new ValidateUtils<>();
        CfBarcodeBind temp = cfBarcodeBindService.selectOne(new EntityWrapper<CfBarcodeBind>().eq("car", car));

        if (temp.getBoxLabelPrintDate() != null) {
            throw new Exception("该箱外贴已经打印过，请注意！！！");
        }

        if (printType.equals(printTypeOptions.getValue(0))) {
            //车型、合格证颜色、宣传颜色、车架号、发动机号、物料代码、生产日期、产品代码不能为空，否则对应报错；

            if (!validateUtils.isNotNull(barcodeBind.getCarModel(), barcodeBind.getOkColor(), barcodeBind.getPublicityColor(), barcodeBind.getFrame(), barcodeBind.getEngine(), barcodeBind.getMaterialCode(), barcodeBind.getCar()) || barcodeBind.getProductDate() == null) {
                throw new Exception("车型、合格证颜色、宣传颜色、车架号、发动机号、物料代码、生产日期、产品代码不能为空,请注意!!!");
            }
            //都存在，更新三码绑定表
            temp.setCarModel(barcodeBind.getCarModel());
            temp.setOkColor(barcodeBind.getOkColor());
            temp.setPublicityColor(barcodeBind.getPublicityColor());
            temp.setEnglishColor(barcodeBind.getEnglishColor());
            temp.setMaterialCode(barcodeBind.getMaterialCode());
            temp.setProductDate(barcodeBind.getProductDate());
            temp.setPrintType(barcodeBind.getPrintType());
            temp.setBoxLabelPrintDate(new Date());
            temp.setObjectSetBasicAttributeForUpdate(userId, new Date());
            temp.setCarType(barcodeBind.getCarType());
            temp.setProductLot(barcodeBind.getProductLot());
            temp.setSpecialCarConfiguration(barcodeBind.getSpecialCarConfiguration());
            temp.setBoxCode(barcodeBind.getBoxCode());
            temp.setRemark(barcodeBind.getRemark());
            cfBarcodeBindService.updateById(temp);
            // 调用国内标签模板，打印标签
            CfPrintFunction cfPrintFunction = cfPrintFunctionService.selectOne(new EntityWrapper<CfPrintFunction>().eq("function_name", "boxPrint"));
            CfPrintLodopTemplate cfPrintLodopTemplate = cfPrintLodopTemplateService.selectOne(new EntityWrapper<CfPrintLodopTemplate>().eq("function_id", cfPrintFunction.getFunctionId()).eq("print_lodop_template_name", "inlandLabelTemplate"));
            String template = cfPrintLodopTemplate.getPrintLodopTemplate();
            printContentList.add(replaceTemplateVar(template, barcodeBind));
            return printContentList;
        } else if (printType.equals(printTypeOptions.getValue(1))) {
            //车型、物料代码、发动机号、车架号、英文颜色、批号、合同号、生产日期、CP码不能为空，否则对应报错；都存在，界面输入插入三码绑定表，调用国外非美国标签模板，打印标签
            if (!validateUtils.isNotNull(barcodeBind.getCarModel(), barcodeBind.getMaterialCode(), barcodeBind.getEngine(), barcodeBind.getFrame(), barcodeBind.getEnglishColor(), barcodeBind.getProductLot(), barcodeBind.getContractNumber(), barcodeBind.getCar()) || barcodeBind.getProductDate() == null) {
                throw new Exception("车型、物料代码、发动机号、车架号、英文颜色、批号、合同号、生产日期、CP码不能为空,请注意!!!");
            }
            //更新三码绑定表
            temp.setCarModel(barcodeBind.getCarModel());
            temp.setMaterialCode(barcodeBind.getMaterialCode());
            temp.setOkColor(barcodeBind.getOkColor());
            temp.setPublicityColor(barcodeBind.getPublicityColor());
            temp.setEnglishColor(barcodeBind.getEnglishColor());
            temp.setProductLot(barcodeBind.getProductLot());
            temp.setContractNumber(barcodeBind.getContractNumber());
            temp.setProductDate(barcodeBind.getProductDate());
            temp.setPrintType(barcodeBind.getPrintType());
            temp.setBoxLabelPrintDate(new Date());
            temp.setObjectSetBasicAttributeForUpdate(userId, new Date());
            temp.setProductLot(barcodeBind.getProductLot());
            temp.setCarType(barcodeBind.getCarType());
            temp.setSpecialCarConfiguration(barcodeBind.getSpecialCarConfiguration());
            temp.setBoxCode(barcodeBind.getBoxCode());
            temp.setRemark(barcodeBind.getRemark());
            cfBarcodeBindService.updateById(temp);

            //加载国外通用打印模板
            CfPrintFunction cfPrintFunction = cfPrintFunctionService.selectOne(new EntityWrapper<CfPrintFunction>().eq("function_name", "boxPrint"));
            CfPrintLodopTemplate cfPrintLodopTemplate = cfPrintLodopTemplateService.selectOne(new EntityWrapper<CfPrintLodopTemplate>().eq("function_id", cfPrintFunction.getFunctionId()).eq("print_lodop_template_name", "abroadCommonLabelTemplate"));
            String template = cfPrintLodopTemplate.getPrintLodopTemplate();
            printContentList.add(replaceTemplateVar(template, barcodeBind));
            return printContentList;
        } else if (printType.equals(printTypeOptions.getValue(2))) {
            //车型、物料代码、发动机号、车架号、英文颜色、批号、合同号、生产日期、CP码、商品名称、州区不能为空，否则对应报错；都存在，界面输入插入三码绑定表，调用国外美国州区标签模板，打印标签
            if (!validateUtils.isNotNull(barcodeBind.getCarModel(), barcodeBind.getMaterialCode(), barcodeBind.getEngine(), barcodeBind.getFrame(), barcodeBind.getEnglishColor(), barcodeBind.getProductLot(), barcodeBind.getContractNumber(), barcodeBind.getCar(), barcodeBind.getUsaName(), barcodeBind.getObviously()) || barcodeBind.getProductDate() == null) {
                throw new Exception("车型、物料代码、发动机号、车架号、英文颜色、批号、合同号、生产日期、CP码、商品名称、州区不能为空,请注意!!!");
            }
            //更新三码绑定表
            temp.setCarModel(barcodeBind.getCarModel());
            temp.setMaterialCode(barcodeBind.getMaterialCode());
            temp.setOkColor(barcodeBind.getOkColor());
            temp.setPublicityColor(barcodeBind.getPublicityColor());
            temp.setEnglishColor(barcodeBind.getEnglishColor());
            temp.setProductLot(barcodeBind.getProductLot());
            temp.setContractNumber(barcodeBind.getContractNumber());
            temp.setProductDate(barcodeBind.getProductDate());
            temp.setUsaName(barcodeBind.getUsaName());
            temp.setObviously(barcodeBind.getObviously());
            temp.setPrintType(barcodeBind.getPrintType());
            temp.setObjectSetBasicAttributeForUpdate(userId, new Date());
            temp.setBoxLabelPrintDate(new Date());
            temp.setCarType(barcodeBind.getCarType());
            temp.setSpecialCarConfiguration(barcodeBind.getSpecialCarConfiguration());
            temp.setBoxCode(barcodeBind.getBoxCode());
            temp.setRemark(barcodeBind.getRemark());
            temp.setProductLot(barcodeBind.getProductLot());
            cfBarcodeBindService.updateById(temp);

            //加载国外通用模板
            CfPrintFunction cfPrintFunction = cfPrintFunctionService.selectOne(new EntityWrapper<CfPrintFunction>().eq("function_name", "boxPrint"));
            CfPrintLodopTemplate cfPrintLodopTemplate = cfPrintLodopTemplateService.selectOne(new EntityWrapper<CfPrintLodopTemplate>().eq("function_id", cfPrintFunction.getFunctionId()).eq("print_lodop_template_name", "abroadCommonLabelTemplate"));
            String template = cfPrintLodopTemplate.getPrintLodopTemplate();
            printContentList.add(replaceTemplateVar(template, barcodeBind));

            //加载州区模板
            CfPrintFunction cfPrintFunction1 = cfPrintFunctionService.selectOne(new EntityWrapper<CfPrintFunction>().eq("function_name", "boxPrint"));
            CfPrintLodopTemplate cfPrintLodopTemplate1 = cfPrintLodopTemplateService.selectOne(new EntityWrapper<CfPrintLodopTemplate>().eq("function_id", cfPrintFunction1.getFunctionId()).eq("print_lodop_template_name", barcodeBind.getObviously()));
            if (cfPrintLodopTemplate1 == null) {
                throw new Exception("未能根据条码 " + car + " 对应的三码绑定记录中的州区" + barcodeBind.getObviously() + "找到对应的州区标签打印模板，请注意！！！");
            }

            String template1 = cfPrintLodopTemplate1.getPrintLodopTemplate();
            printContentList.add(replaceTemplateVar(template1, barcodeBind));

            return printContentList;
        } else {
            throw new Exception("打印类型有误,请注意!!!");
        }

    }

    @Override
    public CfBarcodeBind makeUpScanCarCpCode(String carCpCode) {
        return cfBarcodeBindService.selectOne(new EntityWrapper<CfBarcodeBind>().eq("car", carCpCode));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<String> makeUpPrint(String carCpCode) throws Exception {
        //根据整车cp条码获取对应的三码绑定记录
        CfBarcodeBind barcodeBind = cfBarcodeBindService.selectOne(new EntityWrapper<CfBarcodeBind>().eq("car", carCpCode));

        if (barcodeBind == null) {
            throw new Exception("未能根据整车CP条码获取对应的三码绑定记录表，请注意！！！");
        }

        if (barcodeBind.getBoxLabelPrintDate() == null) {
            throw new Exception("该整车CP条码不存在打印记录，请注意!!!");
        }

        //获取三码绑定表中的打印类型
        String printType = barcodeBind.getPrintType();

        List<String> printContentList = new ArrayList<>();

        if (printType.equals(printTypeOptions.getValue(0))) {
            // 调用国内标签模板，打印标签
            CfPrintFunction cfPrintFunction = cfPrintFunctionService.selectOne(new EntityWrapper<CfPrintFunction>().eq("function_name", "boxPrint"));
            CfPrintLodopTemplate cfPrintLodopTemplate = cfPrintLodopTemplateService.selectOne(new EntityWrapper<CfPrintLodopTemplate>().eq("function_id", cfPrintFunction.getFunctionId()).eq("print_lodop_template_name", "inlandLabelTemplate"));
            String template = cfPrintLodopTemplate.getPrintLodopTemplate();
            printContentList.add(replaceTemplateVar(template, barcodeBind));
            return printContentList;
        } else if (printType.equals(printTypeOptions.getValue(1))) {
            //加载国外通用打印模板
            CfPrintFunction cfPrintFunction = cfPrintFunctionService.selectOne(new EntityWrapper<CfPrintFunction>().eq("function_name", "boxPrint"));
            CfPrintLodopTemplate cfPrintLodopTemplate = cfPrintLodopTemplateService.selectOne(new EntityWrapper<CfPrintLodopTemplate>().eq("function_id", cfPrintFunction.getFunctionId()).eq("print_lodop_template_name", "abroadCommonLabelTemplate"));
            String template = cfPrintLodopTemplate.getPrintLodopTemplate();
            printContentList.add(replaceTemplateVar(template, barcodeBind));
            return printContentList;
        } else if (printType.equals(printTypeOptions.getValue(2))) {
            //加载国外通用模板
            CfPrintFunction cfPrintFunction = cfPrintFunctionService.selectOne(new EntityWrapper<CfPrintFunction>().eq("function_name", "boxPrint"));
            CfPrintLodopTemplate cfPrintLodopTemplate = cfPrintLodopTemplateService.selectOne(new EntityWrapper<CfPrintLodopTemplate>().eq("function_id", cfPrintFunction.getFunctionId()).eq("print_lodop_template_name", "abroadCommonLabelTemplate"));
            String template = cfPrintLodopTemplate.getPrintLodopTemplate();
            printContentList.add(replaceTemplateVar(template, barcodeBind));

            //加载州区模板
            CfPrintFunction cfPrintFunction1 = cfPrintFunctionService.selectOne(new EntityWrapper<CfPrintFunction>().eq("function_name", "boxPrint"));
            CfPrintLodopTemplate cfPrintLodopTemplate1 = cfPrintLodopTemplateService.selectOne(new EntityWrapper<CfPrintLodopTemplate>().eq("function_id", cfPrintFunction1.getFunctionId()).eq("print_lodop_template_name", barcodeBind.getObviously()));

            if (cfPrintLodopTemplate1 == null) {
                throw new Exception("未能根据条码 " + carCpCode + " 对应的三码绑定记录中的州区" + barcodeBind.getObviously() + "找到对应的州区标签打印模板，请注意！！！");
            }

            String template1 = cfPrintLodopTemplate1.getPrintLodopTemplate();
            printContentList.add(replaceTemplateVar(template1, barcodeBind));

            //循环替换模板中的变量
            for (int i = 0; i < printContentList.size(); i++) {
                printContentList.set(i, replaceTemplateVar(printContentList.get(i), barcodeBind));
            }

            return printContentList;
        } else {
            throw new Exception("打印类型有误,请注意!!!");
        }
    }

    public String replaceTemplateVar(String template, CfBarcodeBind cfBarcodeBind) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return template.replace("{{car}}", StringUtils.trimToEmpty(cfBarcodeBind.getCar()))
                .replace("{{frame}}", StringUtils.trimToEmpty(cfBarcodeBind.getFrame()))
                .replace("{{engine}}", StringUtils.trimToEmpty(cfBarcodeBind.getEngine()))
                .replace("{{productionOrder}}", StringUtils.trimToEmpty(cfBarcodeBind.getProductionOrder()))
                .replace("{{status}}", StringUtils.trimToEmpty(cfBarcodeBind.getStatus() + ""))
                .replace("{{tBoxCode}}", StringUtils.trimToEmpty(cfBarcodeBind.gettBoxCode()))
                .replace("{{tBoxBindBy}}", StringUtils.trimToEmpty(cfBarcodeBind.gettBoxBindBy() + ""))
                .replace("{{tBoxBindDate}}", StringUtils.trimToEmpty(cfBarcodeBind.gettBoxBindDate() == null ? "无" : sdf.format(cfBarcodeBind.gettBoxBindDate())))
                .replace("{{boxLabelPrintDate}}", StringUtils.trimToEmpty(cfBarcodeBind.getBoxLabelPrintDate() == null ? "无" : sdf.format(cfBarcodeBind.getBoxLabelPrintDate())))
                .replace("{{printType}}", StringUtils.trimToEmpty(cfBarcodeBind.getPrintType()))
                .replace("{{carType}}", StringUtils.trimToEmpty(cfBarcodeBind.getCarType()))
                .replace("{{carModel}}", StringUtils.trimToEmpty(cfBarcodeBind.getCarModel()))
                .replace("{{materialCode}}", StringUtils.trimToEmpty(cfBarcodeBind.getMaterialCode()))
                .replace("{{usaName}}", StringUtils.trimToEmpty(cfBarcodeBind.getUsaName()))
                .replace("{{productDate}}", StringUtils.trimToEmpty(cfBarcodeBind.getBoxLabelPrintDate() == null ? "无" : sdf.format(cfBarcodeBind.getProductDate())))
                .replace("{{okColor}}", StringUtils.trimToEmpty(cfBarcodeBind.getOkColor()))
                .replace("{{publicityColor}}", StringUtils.trimToEmpty(cfBarcodeBind.getPublicityColor()))
                .replace("{{englishColor}}", StringUtils.trimToEmpty(cfBarcodeBind.getEnglishColor()))
                .replace("{{productLot}}", StringUtils.trimToEmpty(cfBarcodeBind.getProductLot()))
                .replace("{{contractNumber}}", StringUtils.trimToEmpty(cfBarcodeBind.getContractNumber()))
                .replace("{{specialCarConfiguration}}", StringUtils.trimToEmpty(cfBarcodeBind.getSpecialCarConfiguration()))
                .replace("{{boxCode}}", StringUtils.trimToEmpty(cfBarcodeBind.getBoxCode() + ""))
                .replace("{{obviously}}", StringUtils.trimToEmpty(cfBarcodeBind.getObviously()))
                .replace("{{remark}}", StringUtils.trimToEmpty(cfBarcodeBind.getRemark()));
    }

}
