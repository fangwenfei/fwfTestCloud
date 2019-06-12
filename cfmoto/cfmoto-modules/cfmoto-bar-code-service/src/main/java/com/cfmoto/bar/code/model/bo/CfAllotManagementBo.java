package com.cfmoto.bar.code.model.bo;

import com.alibaba.fastjson.JSONObject;
import com.cfmoto.bar.code.model.entity.CfAllotInfo;
import com.cfmoto.bar.code.model.entity.CfAllotInventory;
import com.cfmoto.bar.code.model.vo.CfAllotManagementVo;
import com.cfmoto.bar.code.utiles.BigDecimalUtils;
import com.xiaoleilu.hutool.date.DateUtil;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 调拨管理模块的 封装业务逻辑的对象
 *
 * @author ye
 */
@Component
public class CfAllotManagementBo {

    private static final String ORDER_STATUS_FINISHED = "已完成";

    /**
     * 二步调拨模块的封装功能
     * 将sap返回的jsonObject对象转化成对应业务的vo对象，此处为调拨管理vo对象
     *
     * @param jsonObject sap返回数据
     * @return cfAllotManagementVo 调拨管理vo对象
     */
    public CfAllotManagementVo sapDataToAllotVo(JSONObject jsonObject) {
        //获取SAP返回的表
        List<Map<String, Object>> dataList = (List<Map<String, Object>>) jsonObject.get("ET_DATA");

        CfAllotManagementVo cfAllotManagementVo = new CfAllotManagementVo();

        cfAllotManagementVo.setOrderStatus(jsonObject.getString("EV_ZSTATU"));

        List<CfAllotInventory> cfAllotInventoryList = new ArrayList<>();

        for (Map<String, Object> map : dataList) {
            Date date = DateUtil.parseDate(map.getOrDefault("ZLDAT", "").toString());
            if (cfAllotManagementVo.getCfAllotInfo() == null) {
                CfAllotInfo cfAllotInfo = new CfAllotInfo();
                cfAllotInfo.setOrderNo((String) map.getOrDefault("ZDBDH", ""));
                cfAllotInfo.setMadeOrderDate(date);
                cfAllotManagementVo.setCfAllotInfo(cfAllotInfo);
            }
            CfAllotInventory cfAllotInventory = new CfAllotInventory();
            cfAllotInventory.setOrderNo((String) map.getOrDefault("ZDBDH", ""));
            cfAllotInventory.setMaterialsNo((String) map.getOrDefault("MATNR", ""));
            cfAllotInventory.setMaterialsName((String) map.getOrDefault("MAKTX", ""));
            cfAllotInventory.setSpec((String) map.getOrDefault("WRKST", ""));
            cfAllotInventory.setAllotOutWarehouse((String) map.getOrDefault("ZDCCK", ""));
            cfAllotInventory.setAllotInWarehouse((String) map.getOrDefault("ZDRCK", ""));
            cfAllotInventory.setSpStorePositionNo((String) map.getOrDefault("ZSPCWH", ""));
            cfAllotInventory.setNumber(BigDecimalUtils.numberObjectToInteger(map.getOrDefault("ZYFSL", 0)));
            cfAllotInventory.setAllotOutScannedNumber(BigDecimalUtils.numberObjectToInteger(map.getOrDefault("ZSFSL", 0)));
            cfAllotInventory.setAllotInScannedNumber(BigDecimalUtils.numberObjectToInteger(map.getOrDefault("ZSRSL", 0)));
            cfAllotInventory.setDept((String) map.getOrDefault("ZCJBM", ""));
            cfAllotInventory.setMadeOrderDate(date);
            cfAllotInventory.setCustomerName((String) map.getOrDefault("ZKHMC", ""));
            cfAllotInventory.setReceiveAddress((String) map.getOrDefault("ZSHZL", ""));
            cfAllotInventory.setReceiveContactName((String) map.getOrDefault("ZFDMC", ""));
            cfAllotInventory.setReceiveContactPhoneNumber((String) map.getOrDefault("ZLXFS", ""));
            cfAllotInventory.setSaleOrderNo((String) map.getOrDefault("VBELN", ""));
            cfAllotInventory.setAddresser((String) map.getOrDefault("ZFJR", ""));
            cfAllotInventory.setAddresserPhoneNumber((String) map.getOrDefault("ZFJRFS", ""));
            cfAllotInventory.setInlandOrAbroad((String) map.getOrDefault("VTWEG", ""));
            cfAllotInventory.setMinimumPackageNumber(BigDecimalUtils.numberObjectToInteger(map.getOrDefault("SCMNG", 0)));
            cfAllotInventory.setSalePrice(BigDecimalUtils.numberObjectToBigDecimal(map.getOrDefault("KBETR", 0)));
            cfAllotInventory.setBusinessStreamOrderNo((String) map.getOrDefault("BSTKD", ""));
            cfAllotInventory.setEnglishName((String) map.getOrDefault("MAKTX_EN", ""));
            cfAllotInventoryList.add(cfAllotInventory);
        }
        cfAllotManagementVo.setCfAllotInventoryList(cfAllotInventoryList);
        return cfAllotManagementVo;
    }


    /**
     * 校验sap返回的状态码，然后校验订单状态
     * 如不符合要求，则抛出异常给上级捕获
     */
    public void verifySapCodeThenOrderStatus(CfAllotManagementVo cf) throws Exception {
        if (ORDER_STATUS_FINISHED.equals(cf.getOrderStatus())) {
            throw new Exception("调拨单已完成，请注意！");
        }
    }


    /**
     * 一步调拨模块的数据封装功能
     */
    public CfAllotManagementVo oneStepSapDataToAllotVo(JSONObject jsonObject) {
        CfAllotManagementVo vo = new CfAllotManagementVo();

        vo.setOrderStatus(jsonObject.getString("EV_ZSTATU"));

        List<Map<String, Object>> list = (List<Map<String, Object>>) jsonObject.get("ET_DATA");

        List<CfAllotInventory> inventoryList = new ArrayList<>();

        for (Map<String, Object> map : list) {
            if (vo.getCfAllotInfo() == null) {
                CfAllotInfo cfAllotInfo = new CfAllotInfo();
                cfAllotInfo.setOrderNo((String) map.getOrDefault("RSNUM", ""));
                cfAllotInfo.setMadeOrderDate(new Date());
                vo.setCfAllotInfo(cfAllotInfo);
            }

            CfAllotInventory inventory = new CfAllotInventory();
            inventory.setOrderNo((String) map.getOrDefault("RSNUM", ""));
            inventory.setMaterialsNo((String) map.getOrDefault("MATNR", ""));
            inventory.setMaterialsName((String) map.getOrDefault("MAKTX", ""));
            inventory.setSpec((String) map.getOrDefault("WRKST", ""));
            //调出仓库
            inventory.setAllotOutWarehouse((String) map.getOrDefault("LGORT", ""));
            //调入仓库
            inventory.setAllotInWarehouse((String) map.getOrDefault("UMLGO", ""));
            inventory.setNumber(BigDecimalUtils.numberObjectToInteger(map.getOrDefault("ERFMG", 0)));
            Integer zwqsl = BigDecimalUtils.numberObjectToInteger(map.getOrDefault("ZWQSL", 0));
            inventory.setAllotInScannedNumber(inventory.getNumber() - zwqsl);
            inventory.setAllotOutScannedNumber(inventory.getNumber() - zwqsl);
            inventory.setSpStorePositionNo((String) map.getOrDefault("ZSPCWH", ""));
            inventoryList.add(inventory);
        }
        vo.setCfAllotInventoryList(inventoryList);
        return vo;

    }

}
