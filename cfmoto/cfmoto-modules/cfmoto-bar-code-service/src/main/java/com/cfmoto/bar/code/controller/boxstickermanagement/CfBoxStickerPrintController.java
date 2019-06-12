package com.cfmoto.bar.code.controller.boxstickermanagement;

import cn.hutool.core.util.StrUtil;
import com.cfmoto.bar.code.model.entity.CfBarcodeBind;
import com.cfmoto.bar.code.service.boxstickermanagement.ICfBoxStickerPrintService;
import com.cfmoto.bar.code.utiles.BarcodeUtils;
import com.github.pig.common.util.R;
import com.github.pig.common.util.UserUtils;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 箱外贴打印 前端控制器
 *
 * @author ye
 * @date 2019-04-27
 */
@RestController
@RequestMapping("boxStickerPrint")
@Log4j
public class CfBoxStickerPrintController {

    @Autowired
    private ICfBoxStickerPrintService boxStickerPrintService;

    /**
     * 扫描整车CP条码
     *
     * @param carCpCode 整车CP条码
     * @param printType 打印类型
     * @param carType   车辆类型
     * @return r
     */
    @GetMapping("scanCarCpCode")
    public R<CfBarcodeBind> scanCarCpCode(String carCpCode, String printType, String carType) {
        //校验数据
        if (StrUtil.isBlank(carCpCode) || carCpCode.length() <= 2 || !BarcodeUtils.isCpCode(carCpCode)) {
            return new R<>(R.FAIL, "条码为空或条码格式不正确,请注意!!!");
        }

        try {
            //扫描整车cp条码，返回三码绑定表数据
            CfBarcodeBind barcodeBind = boxStickerPrintService.scanCarCpCode(carCpCode, printType, carType);
            return new R<>(barcodeBind);
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
            return new R<>(R.FAIL, e.getMessage());
        }

    }


    /**
     * 箱外贴打印
     *
     * @param barcodeBind 三码绑定对象
     * @param request     请求对象
     * @return r
     */
    @PostMapping("printBoxSticker")
    public R<List<String>> printBoxSticker(@RequestBody CfBarcodeBind barcodeBind, HttpServletRequest request) {

        //校验是否存在cp条码
        if (StrUtil.isBlank(barcodeBind.getCar())) {
            return new R<>(R.FAIL, "请先扫描整车CP条码!!!");
        }

        try {
            List<String> printContent = boxStickerPrintService.printBoxSticker(barcodeBind, UserUtils.getUserId(request));
            return new R<>(printContent);
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
            return new R<>(R.FAIL, e.getMessage());
        }

    }


    /**
     * 箱外贴补打印
     * 扫描整车CP条码
     *
     * @return
     */
    @GetMapping("makeUp/scanCarCpCode")
    public R makeUpScanCarCpCode(String carCpCode) {
        //校验数据格式
        if (StrUtil.isBlank(carCpCode) || carCpCode.length() <= 2 || !BarcodeUtils.isCpCode(carCpCode)) {
            return new R<>(R.FAIL, "条码为空或条码格式不正确,请注意!!!");
        }

        try {
            CfBarcodeBind barcodeBind = boxStickerPrintService.makeUpScanCarCpCode(carCpCode);
            if (barcodeBind.getBoxLabelPrintDate() == null) {
                return new R(R.FAIL, "该整车CP条码不存在打印记录，请注意!!!");
            } else {
                return new R(R.SUCCESS, "扫描成功!!!");
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
            return new R<>(R.FAIL, e.getMessage());
        }

    }


    /**
     * 箱外贴补打印
     *
     * @param carCpCode 整车CP条码
     * @return r
     */
    @GetMapping("makeUp/print")
    public R<List<String>> makeUpPrint(String carCpCode) {

        //校验数据
        if (StrUtil.isBlank(carCpCode)) {
            return new R<>(R.FAIL, "整车CP条码不能为空!!!");
        }

        try {
            List<String> templates = boxStickerPrintService.makeUpPrint(carCpCode);
            R<List<String>> r = new R<>();
            r.setData(templates);
            return r;
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
            return new R<>(R.FAIL, e.getMessage());
        }

    }

}
