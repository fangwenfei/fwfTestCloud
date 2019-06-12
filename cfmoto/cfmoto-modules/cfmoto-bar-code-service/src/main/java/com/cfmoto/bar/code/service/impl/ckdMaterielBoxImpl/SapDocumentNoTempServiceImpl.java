package com.cfmoto.bar.code.service.impl.ckdMaterielBoxImpl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.cfmoto.bar.code.feign.SapFeignService;
import com.cfmoto.bar.code.mapper.CfAssemblyPlanMapper;
import com.cfmoto.bar.code.mapper.CfLoadPackingMapper;
import com.cfmoto.bar.code.mapper.SapDocumentNoTempMapper;
import com.cfmoto.bar.code.mapper.SapJobOrderTempMapper;
import com.cfmoto.bar.code.model.dto.SapDocumentNoTemp;
import com.cfmoto.bar.code.model.dto.SapJobOrderTemp;
import com.cfmoto.bar.code.model.entity.CfAssemblyPlan;
import com.cfmoto.bar.code.model.entity.CfLoadPacking;
import com.cfmoto.bar.code.service.ckdMaterielBox.ICfLoadPackingService;
import com.cfmoto.bar.code.service.ckdMaterielBox.ISapDocumentNoTempService;
import com.github.pig.common.util.R;
import com.github.pig.common.util.exception.ValidateCodeException;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * <p>
 * 模拟接收sap接收的单据数据量 服务实现类
 * </p>
 *
 * @author FangWenFei
 * @since 2019-02-27
 */
@Service
public class SapDocumentNoTempServiceImpl extends ServiceImpl<SapDocumentNoTempMapper, SapDocumentNoTemp> implements ISapDocumentNoTempService {

    @Autowired
    SapDocumentNoTempMapper sapDocumentNoTempMapper ;

    @Autowired
    CfAssemblyPlanMapper cfAssemblyPlanMapper ;

    @Autowired
    CfLoadPackingMapper cfLoadPackingMapper;

    @Autowired
    SapJobOrderTempMapper sapJobOrderTempMapper;

    @Autowired
    SapFeignService sapFeignService ;

    @Autowired
    ICfLoadPackingService cfLoadPackingService;
    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<CfLoadPacking> insertLoadPacking(int userId, List<SapJobOrderTemp> sapJobOrderTempList) throws Exception{
        //判断是否重复勾选
        String country=null;
        String  model=null;
        String  documentNo="";
        String  materialNo="";
        String  salesOrderNo="";
        String  contractNo="";
        Map<String,Object> cfAssemblyPlanMap= new HashMap<String, Object>();
        Map<String,BigDecimal> assemblyMaterialsMap= new HashMap<String, BigDecimal>();
        Map<String,CfAssemblyPlan> cfAssemblyPlanObjectMap= new HashMap<String, CfAssemblyPlan>();
        Map<String,String> documentNoMap= new HashMap<String, String>();
        CfAssemblyPlan cfAssemblyPlanTemp=new CfAssemblyPlan();
        Map<String,Object> paramMapSap =new HashedMap();
        paramMapSap.put("functionName","ZMM_BC_011");
        ArrayList< Map<String,String>> ItDataArray=new ArrayList<>();
        if(sapJobOrderTempList.size()==0){
            throw  new ValidateCodeException("请勾选数据生成装箱清单");
        }
        for (int i=0;i<sapJobOrderTempList.size();i++) {
            country=sapJobOrderTempList.get(0).getCountry();
            model=sapJobOrderTempList.get(0).getModel();
            contractNo=sapJobOrderTempList.get(0).getContractNo();
            materialNo=sapJobOrderTempList.get(i).getMaterialNo();
            SapJobOrderTemp sapJobOrderTemp=sapJobOrderTempList.get(i);
            documentNo=sapJobOrderTemp.getJobOrderNo()+"&"+documentNo;
            salesOrderNo=sapJobOrderTemp.getSalesOrder();
            //同一国家同一车型
           if(country.equals(sapJobOrderTempList.get(i).getCountry())
                &&model.equals(sapJobOrderTempList.get(i).getModel())){
               //判断是否是重复勾选+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
               try{
                   sapJobOrderTempMapper.insert(sapJobOrderTempList.get(i));     //要改MySQLIntegrityConstraintViolationException
                   Map<String,String> tableMap=new HashedMap();
                   //像sap接口插入数据
                   tableMap.put("AUFNR", sapJobOrderTempList.get(i).getJobOrderNo() );
                   ItDataArray.add(tableMap);
               }catch (DuplicateKeyException e){
                   throw  new ValidateCodeException("不可以重复勾选生产任务单");
               }
               //DuplicateKeyException
               //判断是否是重复勾选+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
               cfAssemblyPlanTemp=new CfAssemblyPlan();
               cfAssemblyPlanTemp.setCountry(country);
               cfAssemblyPlanTemp.setModel(model);
               cfAssemblyPlanTemp.setMaterialNo(materialNo);
               //通过车型国家获取总成方案
               List<CfAssemblyPlan> cfAssemblyPlanTempList= cfAssemblyPlanMapper.selectList(new EntityWrapper<>(cfAssemblyPlanTemp));
               BigDecimal newNumber=null;
               for(CfAssemblyPlan cfAssemblyPlan:cfAssemblyPlanTempList){
                   BigDecimal count =sapJobOrderTemp.getJobOrderNumber();
                   //总成物料数据量

                   //该清单所需总成物料的数量
                   if((!documentNoMap.containsKey(sapJobOrderTemp.getJobOrderNo()+"&&"+cfAssemblyPlan.getAssemblyMaterials()))){
                       BigDecimal assemblyNumber = new BigDecimal(cfAssemblyPlan.getAssemblyNumber());
                       BigDecimal assemblyNumberAll=count.multiply(assemblyNumber);
                       if(assemblyMaterialsMap.containsKey(cfAssemblyPlan.getAssemblyMaterials())){
                           assemblyNumberAll =assemblyNumberAll.add(assemblyMaterialsMap.get(cfAssemblyPlan.getAssemblyMaterials()));
                       }
                       assemblyMaterialsMap.put(cfAssemblyPlan.getAssemblyMaterials(),assemblyNumberAll);
                       documentNoMap.put(sapJobOrderTemp.getJobOrderNo()+"&&"+cfAssemblyPlan.getAssemblyMaterials(),"Y");
                   }
                   cfAssemblyPlanObjectMap.put(cfAssemblyPlan.getAssemblyMaterials(),cfAssemblyPlan);
                   //该清单所需子阶物料数量
                   if(cfAssemblyPlanMap.containsKey(cfAssemblyPlan.getSonMaterial())){
                       //获取上一个子阶物料号的数量
                       BigDecimal beforeNumber= new BigDecimal(cfAssemblyPlanMap.get(cfAssemblyPlan.getSonMaterial()).toString());
                       //当前子阶物料号的数量：物料清单数量 X 单个数量
                       newNumber= cfAssemblyPlan.getSonMaterialNumber().multiply(count).add(beforeNumber);
                   }else{
                       newNumber= cfAssemblyPlan.getSonMaterialNumber().multiply(count);
                   }
                   cfAssemblyPlanMap.put(cfAssemblyPlan.getSonMaterial(),newNumber);
               }
           }else{
               throw  new ValidateCodeException("不是同一国家同一车型");
             //  return new R<>(R.FAIL, "不是同一国家同一车型" );
           }
         }
        Map<String,Object> dataMap =new HashedMap();
        dataMap.put("ET_DATA",ItDataArray);
        paramMapSap.put("paramMap",dataMap);
        //获取物料清单汇合
        //TODO SAP获取数据
        R< Map<String,Object>> result= sapFeignService.executeJcoFunction(paramMapSap);
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
        Map<String,Object> sapDocumentMap=new HashMap<String, Object>();
        Map<String,CfLoadPacking> sapMaterialMap=new HashMap<String, CfLoadPacking>();
        for (int  i=0;i<jsonArray.size();i++ ) {
            JSONObject jsonObjectIn = jsonArray.getJSONObject(i);
            sapDocumentMap.put(jsonObjectIn.getString("MATNR"),jsonObjectIn.getString("BDMNG"));
            CfLoadPacking cfLoadPackingIn =new CfLoadPacking();
            cfLoadPackingIn.setMaterialNo(jsonObjectIn.getString("MATNR"));
            cfLoadPackingIn.setMaterialName(jsonObjectIn.getString("MAKTX_ZH"));
            cfLoadPackingIn.setEnglishName(jsonObjectIn.getString("MAKTX_EN"));
            sapMaterialMap.put(jsonObjectIn.getString("MATNR"),cfLoadPackingIn);

        }
        System.out.println("第三种：通过Map.entrySet遍历key和value");
        for (Map.Entry<String, Object> m : cfAssemblyPlanMap.entrySet()) {
            if(sapDocumentMap.containsKey(m.getKey())){
                //当前的sap获取的清单的数量
                BigDecimal beforeNumber= new BigDecimal(sapDocumentMap.get(m.getKey()).toString());
                //所需的清单的数量
                BigDecimal needNumber= new BigDecimal( m.getValue().toString());
                //判断数量是否满足
                if(beforeNumber.compareTo(needNumber)>0){
                    //剩余数量
                    sapDocumentMap.put(m.getKey(),beforeNumber.subtract(needNumber));
                }else if(beforeNumber.compareTo(needNumber)==0){
                    //相等时候清除该
                    sapDocumentMap.remove(m.getKey());
                }else{
                    throw  new ValidateCodeException(m.getKey()+"数量不足");
                  //  return new R<>(R.FAIL,m.getKey()+"数量不足" );
                }

            }else{
                throw  new ValidateCodeException("缺少总类 ："+m.getKey());
               // return new R<>(R.FAIL,"缺少总类 ："+m.getKey() );
            }
          //System.out.println("key:" + m.getKey() + " value:" + m.getValue());
         }
        List<CfLoadPacking> cfLoadPackingList=new ArrayList<>();
        CfLoadPacking cfLoadPacking;
        //剩余
        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++");
        for (Map.Entry<String, Object> m : sapDocumentMap.entrySet()) {
            System.out.println("K ："+m.getKey()+"  V :"+m.getValue());
            BigDecimal materialNumber= new BigDecimal(m.getValue().toString());
            CfLoadPacking cfLoadPackingMap= sapMaterialMap.get(m.getKey());

            cfLoadPacking=new CfLoadPacking();
            cfLoadPacking.setModel(model);
            cfLoadPacking.setCountry(country);
            cfLoadPacking.setContractNo(contractNo);
            cfLoadPacking.setDocumentNo(documentNo);
            cfLoadPacking.setSalesOrderNo(salesOrderNo);
            cfLoadPacking.setMaterialNo(m.getKey());
            cfLoadPacking.setMaterialNumber(materialNumber);
            cfLoadPacking.setPrintingNumber(new BigDecimal(0));
            cfLoadPacking.setLoadNumber(new BigDecimal(0));
            cfLoadPacking.setObjectSetBasicAttribute(userId,new Date());
            cfLoadPacking.setEnglishName(cfLoadPackingMap.getEnglishName());
            cfLoadPacking.setMaterialName(cfLoadPackingMap.getMaterialName());
         //   cfLoadPackingMapper.insert(cfLoadPacking);
            cfLoadPackingList.add(cfLoadPacking);
        }
        //总成的
        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++");
        for (Map.Entry<String, BigDecimal> m : assemblyMaterialsMap.entrySet()) {
            System.out.println("K ："+m.getKey()+"  V :"+m.getValue());
            BigDecimal materialNumber= m.getValue();
            cfLoadPacking=new CfLoadPacking();
            cfLoadPacking.setCountry(country);
            cfLoadPacking.setModel(model);
            cfLoadPacking.setContractNo(contractNo);
            cfLoadPacking.setDocumentNo(documentNo);
            cfLoadPacking.setSalesOrderNo(salesOrderNo);
            cfLoadPacking.setMaterialNo(m.getKey());
            cfLoadPacking.setMaterialNumber(materialNumber);
            cfLoadPacking.setPrintingNumber(new BigDecimal(0));
            cfLoadPacking.setLoadNumber(new BigDecimal(0));
            cfLoadPacking.setObjectSetBasicAttribute(userId,new Date());
            CfAssemblyPlan cfAssemblyPlanIn= cfAssemblyPlanObjectMap.get(m.getKey());
            cfLoadPacking.setEnglishName(cfAssemblyPlanIn.getEnglishName());
            cfLoadPacking.setMaterialName(cfAssemblyPlanIn.getMaterialName());
        //    cfLoadPackingMapper.insert(cfLoadPacking);
            cfLoadPackingList.add(cfLoadPacking);
        }
        cfLoadPackingService.insertBatch(cfLoadPackingList);
        System.out.println("第三种eee：通过Map.entrySet遍历key和value");

        return cfLoadPackingList ;//new R<>(cfLoadPackingList);
    }
}
