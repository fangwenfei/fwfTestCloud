package com.cfmoto.bar.code.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.cfmoto.bar.code.feign.SapFeignService;
import com.cfmoto.bar.code.feign.UserFeignService;
import com.cfmoto.bar.code.model.entity.CfCostCenterPickOrWithdrawInventory;
import com.cfmoto.bar.code.model.entity.CfStorageLocation;
import com.cfmoto.bar.code.service.ICfCostCenterPickComponentService;
import com.cfmoto.bar.code.service.ICfCostCenterPickOrWithdrawInventoryService;
import com.cfmoto.bar.code.service.ICfStorageLocationService;
import com.cfmoto.bar.code.utiles.BarcodeUtils;
import com.cfmoto.bar.code.utiles.ValidateUtils;
import com.github.pig.common.util.R;
import com.github.pig.common.util.UserUtils;
import com.github.pig.common.vo.UserVO;
import com.xiaoleilu.hutool.util.StrUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 成本中心领料-部品(PDA)   前端控制器
 *
 * @author ye
 */

@RestController
@RequestMapping("costCenterComponent")
@Api(tags = " 成本中心领料-部品")
public class CfCostCenterPickComponentController {

    @Autowired
    private ICfCostCenterPickOrWithdrawInventoryService inventoryService;

    @Autowired
    private UserFeignService userFeignService;

    @Autowired
    private ICfCostCenterPickComponentService componentService;

    @Autowired
    private SapFeignService sapFeignService;

    @Autowired
    private ICfStorageLocationService storageLocationService;

    private Logger logger = LoggerFactory.getLogger(CfCostCenterPickComponentController.class);

    /**
     * @param barcode     条码
     * @param warehouseNo 仓库代码
     * @param orderNo     单号
     * @return
     */
    @GetMapping("scanBarcode")
    @ApiOperation(value = "部品中的扫描条码")
    public R<Map<String, Object>> scanBarcode(String barcode, String warehouseNo, String orderNo,HttpServletRequest request) {
        R<Map<String, Object>> r = new R<>();


        //校验参数是否有效
        if (StrUtil.isBlank(barcode)) {

            r.setErrorAndErrorMsg("请输入条码后再进行扫描!");
            return r;

        } else if (StrUtil.isBlank(warehouseNo)) {

            r.setErrorAndErrorMsg("请先选择领料仓库!");
            return r;

        } else if (StrUtil.isBlank(orderNo)) {

            r.setErrorAndErrorMsg("请传入单号!");
            return r;

        }


        //校验条码中是否有*及解析条码中的物料代码和数量
        r = BarcodeUtils.anaylysisAndSplitBarcode(barcode, r);

        Map<String, Object> resultMap;


        //判断校验结果
        if (r.getCode() == R.FAIL) {
            return r;
        } else if (r.getCode() == R.SUCCESS) {
            resultMap = r.getData();

            int number = Integer.parseInt(resultMap.get("number").toString());
            String materialsNo = resultMap.get("materialsNo").toString();


            //校验数量是否为0
            if (number == 0) {
                r.setErrorAndErrorMsg("条码数量为0，请注意！");
                r.setData(null);
                return r;
            }



         /*   	检验物料是否存在汇总界面
            	否，报错“条码不存在汇总，请注意！”
            	是，获取物料行数据*/


            //首先根据传过来的单号找出对应的汇总界面数据集合
            List<CfCostCenterPickOrWithdrawInventory> inventoryList = inventoryService.getInventoryListByOrderNo(orderNo);

            CfCostCenterPickOrWithdrawInventory gatherView = new CfCostCenterPickOrWithdrawInventory();

            //遍历汇总界面数据集合校验条码中的物料代码是否存在其中
            boolean flag = false;

            for (CfCostCenterPickOrWithdrawInventory inventory : inventoryList) {
                if (inventory.getMaterialsNo().equals(materialsNo)) {
                    flag = true;
                    gatherView = inventory;
                    break;
                }
            }

            //物料代码不存在汇总界面
            if (!flag) {
                r.setErrorAndErrorMsg("条码不存在汇总，请注意！");
                r.setData(null);
                return r;
            }


            //	校验汇总数量
            //   	当汇总中实发数量=应发数量，报错“该物料已经领料完成，请注意”；
            //   	当条码数量>应发数量-实发数量，报错“该物料条码超过待领料数量，请注意”；
            //   	当条码数量<=应发数量-实发数量，弹出物料批次匹配界面

            int actualSendNumber = gatherView.getScannedNumber();
            int shouldveSendNumber = gatherView.getShouldPickOrWithdrawNumber();

            //实发数量=应发数量,报错
            if (actualSendNumber == shouldveSendNumber) {
                r.setErrorAndErrorMsg("该物料已经领料完成，请注意");
                r.setData(null);
            } else if (number > shouldveSendNumber - actualSendNumber) {
                r.setErrorAndErrorMsg("该物料条码超过待领料数量，请注意");
                r.setData(null);
                //弹出物料批次匹配界面
            } else if (number <= shouldveSendNumber - actualSendNumber) {

                Map<String, Object> returnMap = new HashMap<>();

                //物料代码
                returnMap.put("materialsNo", gatherView.getMaterialsNo());
                //物料名称
                returnMap.put("materialsName", gatherView.getMaterialsName());
                //未清数量
                returnMap.put("unClearedNumber", gatherView.getShouldPickOrWithdrawNumber() - gatherView.getScannedNumber());
                returnMap.put("barcodeNumber", number);

                List<Map<String, Object>> maps = new ArrayList<>();

                Map<String, Object> paramMap = new HashMap<>(4);
                paramMap.put("functionName", "ZMM_BC_020");

                Map<String, Object> dataMap = new HashMap<>(4);


                //传入当前用户对应的工厂
                UserVO user = userFeignService.user(UserUtils.getUserId(request));
                dataMap.put("IV_WERKS", user.getSite());
                dataMap.put("IV_LGORT", warehouseNo);
                dataMap.put("IV_MATNR", materialsNo);
                paramMap.put("paramMap", dataMap);

                r = sapFeignService.executeJcoFunction(paramMap);

                if (r.getCode() == R.FAIL) {
                    //首先打印错误信息到日志
                    logger.error(r.getMsg());
                    //再返回友好的提示信息给前端用户界面
                    r.setMsg("查询接口出现错误，请联系管理员！");
                    r.setData(null);
                    return r;
                } else {
                    Map<String, Object> data = r.getData();
                    JSONObject jsonObject = new JSONObject(data);

                    //获取sap接口返回的状态并判断
                    String sapStatus = jsonObject.getString("EV_STATUS");

                    //sap接口返回的状态码为1，即错误码
                    if (sapStatus.equals(SapFeignService.ERROR_CODE)) {

                        r.setErrorAndErrorMsg(jsonObject.getString("EV_MESSAGE"));
                        r.setData(null);
                        return r;
                    }

                    JSONArray etData = jsonObject.getJSONArray("ET_DATA");

                    for (Object etDatum : etData) {
                        Map<String, Object> temp = (Map<String, Object>) etDatum;

                        //首先判断非限制库存是否大于0，小于0则不放入map返回
                        if (new BigDecimal(temp.getOrDefault("ZFXKC",0).toString()).intValue() <= 0) {
                            continue;
                        }

                        Map<String, Object> map = new HashMap<>(12);

                        map.put("batchNo", temp.getOrDefault("CHARG",""));
                        map.put("batchNumber", temp.getOrDefault("ZFXKC",0));
                        map.put("materialsNo", temp.getOrDefault("MATNR",""));

                        map.put("spec", temp.getOrDefault("WRSKT",""));
                        map.put("wareHouse", temp.getOrDefault("LGORT",""));
                        map.put("storageArea", temp.getOrDefault("LGTYP",""));
                        map.put("warehousePosition", temp.getOrDefault("LGPLA",""));
                        maps.add(map);

                    }

                }

                List<Map<String, Object>> returnList = componentService.dynamicMatchNumberByBarcodeNumber(number, maps, materialsNo,user.getUserId());
                //校验匹配数量是否足够
                int matchedNumber = 0;
                for (Map<String, Object> map : returnList) {
                    matchedNumber += (int) map.get("toBeMatchNumber");
                }
                if (number > matchedNumber) {
                    return new R<>(R.FAIL, "可匹配数量不足,为" + matchedNumber + "个,请注意！！！");
                }

                returnMap.put("returnList", returnList);
                r.setData(returnMap);

            }

        }

        return r;
    }

    /**
     * 物料批次匹配提交接口
     * <p>
     * 点击提交，将匹配数量不为0的行数据插入到已扫描界面，更新汇总（同步更新后台表）
     *
     * @param jsonObject
     */
    @PostMapping("commitBatchMatchedData")
    @ApiOperation(value = "物料批次匹配提交接口")
    public R<Map<String, Object>> submitBatchMatchedData(@RequestBody JSONObject jsonObject, HttpServletRequest request) {
        R<Map<String, Object>> r = new R<>();

        int userId = UserUtils.getUserId(request);

        //从jsonObject中取出所需数据
        List<Map<String, Object>> list = (List<Map<String, Object>>) jsonObject.get("batchList");

        ValidateUtils<Map<String, Object>> validateUtils = new ValidateUtils<>();
        boolean notNull = validateUtils.isNotNull(list);
        if (!notNull) {
            r.setErrorAndErrorMsg("暂无可提交的数据！");
            return r;
        }

        //获取订单号
        String orderNo = jsonObject.getString("orderNo");

        //获取物料代码
        String materialsNo = jsonObject.getString("materialsNo");

        //获取条码
        String barcode = jsonObject.getString("barcode");

        //校验提交数量
        CfCostCenterPickOrWithdrawInventory inventory = inventoryService.selectOne(new EntityWrapper<CfCostCenterPickOrWithdrawInventory>().eq("order_no", orderNo).eq("materials_no",materialsNo));
        R<Map<String, Object>> mapR = BarcodeUtils.anaylysisAndSplitBarcode(barcode, r);

        if (mapR.getCode() == R.FAIL) {
            return r;
        }

        int number = (int) mapR.getData().get("number");
        if (number > inventory.getShouldPickOrWithdrawNumber() - inventory.getScannedNumber()) {
            r.setErrorAndErrorMsg("条码数量大于未清数量!!!");
            return r;
        }


        if (!validateUtils.isNotNull(orderNo, materialsNo, barcode)) {
            r.setErrorAndErrorMsg("请输入有效的数据！");
            return r;
        }

        Map<String, Object> resultMap = componentService.insertRecordAndUpdateInventory(orderNo, materialsNo, barcode, list, userId);
        if ((int) resultMap.get("code") == 1) {
            r.setErrorAndErrorMsg((String) resultMap.get("msg"));
            return r;
        } else {
            r.setData((Map<String, Object>) resultMap.get("data"));
            return r;
        }

    }


    @GetMapping("getWareHouse")
    @ApiOperation(value = "领料仓库")
    public R<List<CfStorageLocation>> getWareHouse(@RequestParam(required = false, defaultValue = "") String key,HttpServletRequest request) {
        return new R<>(storageLocationService.getWareHouse(key,UserUtils.getUserId(request)));
    }
}
