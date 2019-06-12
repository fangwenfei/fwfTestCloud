package com.cfmoto.bar.code.service.impl.ckdMaterielBoxImpl;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.cfmoto.bar.code.feign.SapFeignService;
import com.cfmoto.bar.code.mapper.SapJobOrderTempMapper;
import com.cfmoto.bar.code.model.dto.SapJobOrderTemp;
import com.cfmoto.bar.code.model.dto.SelectList;
import com.cfmoto.bar.code.service.ckdMaterielBox.ISapJobOrderTempService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.github.pig.common.util.R;
import com.github.pig.common.util.exception.ValidateCodeException;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 模拟通过sap获取生产任务单信息 服务实现类
 * </p>
 *
 * @author FangWenFei
 * @since 2019-02-27
 */
@Service
public class SapJobOrderTempServiceImpl extends ServiceImpl<SapJobOrderTempMapper, SapJobOrderTemp> implements ISapJobOrderTempService {

    @Autowired
    SapFeignService sapFeignService ;


    @Override
    public  List<SapJobOrderTemp> getSapJobOrderData(Map<String, Object> params, SapJobOrderTemp sapJobOrderTemp) throws Exception {
        if(!StringUtils.isNotBlank(sapJobOrderTemp.getSalesOrder())){
            throw  new ValidateCodeException(SapJobOrderTemp.SALES_ORDER_NOT_NULL);
        }
        sapJobOrderTemp.setSalesOrder(sapJobOrderTemp.getSalesOrder().replaceAll("^(0+)", ""));
        Map<String,Object> paramMap =new HashedMap();
        paramMap.put("functionName","ZMM_BC_010");
        Map<String,Object> dataMap =new HashedMap();
        dataMap.put("IV_KDAUF",sapJobOrderTemp.getSalesOrder());
        paramMap.put("paramMap",dataMap);
        try {
            R< Map<String,Object>> result= sapFeignService.executeJcoFunction(paramMap);
            if(result.getCode()!=0){
                throw  new ValidateCodeException(result.getMsg());
            }

            Map<String,Object> resultMapData=result.getData();
            JSONObject jsonObject =new JSONObject(resultMapData);
            if(!jsonObject.getString("EV_STATUS").equals("1")){
                throw  new ValidateCodeException(jsonObject.getString("EV_MESSAGE"));
            }
            // ET_DATA
            JSONArray jsonArray=jsonObject.getJSONArray("ET_DATA");
            //插入数据
            List<SapJobOrderTemp> sapJobOrderTempList=new ArrayList<>();
            for (int i=0;i<jsonArray.size();i++) {
                JSONObject etData=jsonArray.getJSONObject(i);
                SapJobOrderTemp sapJobOrder = new SapJobOrderTemp();
                sapJobOrder.setSalesOrder(etData.getString("KDAUF"));
                sapJobOrder.setJobOrderNo(etData.getString("AUFNR"));
                sapJobOrder.setCountry(etData.getString("LANDX"));
                sapJobOrder.setModel(etData.getString("FERTH"));
                sapJobOrder.setJobOrderNumber(new BigDecimal(etData.getString("GAMNG")));
                sapJobOrder.setMaterialDisc(etData.getString("MAKTX"));
                sapJobOrder.setMaterialNo(etData.getString("MATNR"));
                sapJobOrder.setContractNo(etData.getString("ZHTH"));//合同号
                //VBELN  发货通知单
                sapJobOrderTempList.add(sapJobOrder);

            }

         return sapJobOrderTempList;
        }catch (Exception e){
            throw  new ValidateCodeException(e.getMessage());
        }
    }

    @Override
    @Cacheable(value = "R:SapJobOrderTemp:selectDocumentNoBySapJobOrder", key = "'R:SapJobOrderTemp:selectDocumentNoBySapJobOrder:'+#salesOrder")
    public List<SelectList> selectDocumentNoBySapJobOrder(String salesOrder ) {
        List<SapJobOrderTemp> sapJobOrderTempLis= this.selectList(new EntityWrapper<SapJobOrderTemp>().eq(SapJobOrderTemp.SQL_SALES_ORDER_STR,salesOrder).groupBy(SapJobOrderTemp.SQL_JOB_ORDER_NO_STR).setSqlSelect(SapJobOrderTemp.SQL_JOB_ORDER_NO_STR));
        List<SelectList> selectList=new ArrayList<>();
        for(SapJobOrderTemp sapJobOrderTemp:sapJobOrderTempLis){
            SelectList selectListA=new SelectList();
            selectListA.setSelectKey(sapJobOrderTemp.getJobOrderNo());
            selectListA.setSelectDescription(sapJobOrderTemp.getJobOrderNo());
            selectListA.setSelectValue(sapJobOrderTemp.getJobOrderNo());
            selectList.add(selectListA);
        }
        return selectList;
    }
}
