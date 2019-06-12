package com.cfmoto.bar.code.model.bo;

import com.cfmoto.bar.code.model.entity.CfCecDeliverGoodsInfo;
import com.cfmoto.bar.code.model.entity.CfCecDeliverGoodsInventory;
import com.cfmoto.bar.code.model.vo.CfCecDeliverGoodsVo;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 部品网购发货 BO业务层对象
 *
 * @author yezi
 * @date 2019/6/4
 */
@Component
public class CfCecDeliverGoodsBo {

    //定义已完成订单状态为C
    public static final String COMPLETE_ORDER_STATUS = "C";

    //定义未完成订单状态为U
    public static final String UNCOMPLETE_ORDER_STATUS = "U";

    /**
     * 将ZMM_BC_007-销售订单传输接口返回的数据封装到BO对象中
     */
    public CfCecDeliverGoodsVo transferSapData(Map<String, Object> esDataMap) {
        CfCecDeliverGoodsVo deliverGoodsVo = new CfCecDeliverGoodsVo();
        //设置订单状态
        deliverGoodsVo.setOrderStatus(esDataMap.getOrDefault("EV_GBSTK", "").toString());
        //设置订单类型
        deliverGoodsVo.setOrderType(esDataMap.getOrDefault("EV_LFART", "").toString());
        //设置工厂
        deliverGoodsVo.setFactory(esDataMap.getOrDefault("EV_WERKS", "").toString());

        //获取SAP接口ZMM_BC_007-销售订单传输接口返回的数据集合
        List<Map<String, Object>> dataMapList = (List<Map<String, Object>>) esDataMap.get("ET_DATA");

        //初始化清单表集合
        List<CfCecDeliverGoodsInventory> inventoryList = new ArrayList<>();

        //遍历上述数据集合
        dataMapList.forEach(m -> {
            //如果vo中的信息表数据为空，则设置信息表数据
            if (deliverGoodsVo.getInfo() == null) {
                CfCecDeliverGoodsInfo info = new CfCecDeliverGoodsInfo();
                //设置信息表中的交货单号
                info.setDeliverOrderNo(m.getOrDefault("VBELN", "").toString());
                //设置信息表中的部门
                info.setDepartment(m.getOrDefault("BEZEI", "").toString());
                //设置信息表中的购货单位
                info.setPurchaseUnit(m.getOrDefault("NAME1", "").toString());
                //设置信息表中的制单日期
                info.setMadeOrderDate(new Date());
                deliverGoodsVo.setInfo(info);
            }
            CfCecDeliverGoodsInventory inventory = new CfCecDeliverGoodsInventory();
            //设置清单表的交货单号
            inventory.setDeliverOrderNo(m.getOrDefault("VBELN", "").toString());
            //设置清单表的行项目号
            inventory.setRowItem(m.getOrDefault("POSNR", "").toString());
            //销售订单号
            inventory.setSalesOrderNo(m.getOrDefault("VGBEL", "").toString());
            //物料代码
            inventory.setMaterialsNo(m.getOrDefault("MATNR", "").toString());
            //物料名称
            inventory.setMaterialsName(m.getOrDefault("MAKTX", "").toString());
            //规格型号
            inventory.setSpec(m.getOrDefault("WRKST", "").toString());
            //数量
            inventory.setNumber(new BigDecimal(m.getOrDefault("LFIMG", 0).toString()).intValue());
            //仓库
            inventory.setWarehouse(m.getOrDefault("LGORT", "").toString());
            inventoryList.add(inventory);
        });

        //将清单集合放入vo对象中
        deliverGoodsVo.setInventoryList(inventoryList);
        //返回封装好的VO对象
        return deliverGoodsVo;
    }
}
