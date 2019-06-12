package com.cfmoto.bar.code.service.impl.stock;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.cfmoto.bar.code.model.entity.CfStockScanLine;
import com.cfmoto.bar.code.model.entity.CfStockSplit;
import com.cfmoto.bar.code.mapper.CfStockSplitMapper;
import com.cfmoto.bar.code.model.vo.CfStockSplitVo;
import com.cfmoto.bar.code.service.ICfNextNumberService;
import com.cfmoto.bar.code.service.ICfStockSplitService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.github.pig.common.util.exception.ValidateCodeException;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 备料拆分 服务实现类
 * </p>
 *
 * @author FangWenFei
 * @since 2019-04-22
 */
@Service
public class CfStockSplitServiceImpl extends ServiceImpl<CfStockSplitMapper, CfStockSplit> implements ICfStockSplitService {

    @Autowired
    private ICfNextNumberService cfNextNumberService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> splitCfStock(Map<String, Object> params, int userId) throws Exception {
        BigDecimal splitUnit=new BigDecimal(params.getOrDefault("splitUnit","").toString());
        Integer splitId=Integer.parseInt(params.getOrDefault("splitId","").toString());
        Date newDate=new Date();

        CfStockSplit  cfStockSplitOne=  this.selectById(splitId);
        if(cfStockSplitOne.getFlag()==1){
           this.delete(new EntityWrapper<CfStockSplit>().eq(CfStockSplit.SPLIT_PARENT_ID_SQL,cfStockSplitOne.getSplitId()));
        }

        if(cfStockSplitOne.getNumber().compareTo(splitUnit)<0){
           throw  new ValidateCodeException(CfStockSplit.EX_TOO_BIG_SPLIT_UNIT+"总量为"+cfStockSplitOne.getNumber());
        }
       int splitNumber=0;
        List<CfStockSplitVo> cfStockSplitVoList= JSONArray.parseArray(cfStockSplitOne.getBatchNoText(),CfStockSplitVo.class);
        BigDecimal allNumber=cfStockSplitOne.getNumber();
        //
        //取余
        BigDecimal remainderNumber= allNumber.divideAndRemainder(splitUnit)[1];
        //整除
        BigDecimal integerNumber=allNumber.divideToIntegralValue(splitUnit);

        List<CfStockSplitVo> cfStockSplitVoListIn=new ArrayList<>();
        List<CfStockSplit> cfStockSplitList=new ArrayList<>();
        BigDecimal inCount=new BigDecimal(0);
        for(int i=0;i<cfStockSplitVoList.size();i++){
            CfStockSplitVo cfStockSplitVo=cfStockSplitVoList.get(i);
            BigDecimal inCountB=inCount;
            inCount= inCount.add(cfStockSplitVo.getNumber());
            if(inCount.compareTo(splitUnit)>=0){
                CfStockSplitVo cfStockSplitVoA=new CfStockSplitVo();
                cfStockSplitVoA.setMaterialsName(cfStockSplitVo.getMaterialsName());
                cfStockSplitVoA.setMaterialsNo(cfStockSplitVo.getMaterialsNo());
                cfStockSplitVoA.setBatchNo(cfStockSplitVo.getBatchNo());
                cfStockSplitVoA.setNumber(splitUnit.subtract(inCountB));
                cfStockSplitVoListIn.add(cfStockSplitVoA);
                CfStockSplit cfStockSplitA =new CfStockSplit();

                String NextNumber= cfNextNumberService.generateNextNumber(CfStockScanLine.STOCK_SPLIT);
                cfStockSplitA.setSplitNo(NextNumber);
                cfStockSplitA.setStockListNo(cfStockSplitOne.getStockListNo());
                cfStockSplitA.setObjectSetBasicAttribute(userId,newDate);
                cfStockSplitA.setFlag(1);
                cfStockSplitA.setMaterialsNo(cfStockSplitOne.getMaterialsNo());
                cfStockSplitA.setMode(cfStockSplitOne.getMode());
                cfStockSplitA.setMaterialsName(cfStockSplitOne.getMaterialsName());
                cfStockSplitA.setSplitParentId(cfStockSplitOne.getSplitId());
                cfStockSplitA.setBatchNoText(JSONObject.toJSONString(cfStockSplitVoListIn));

                cfStockSplitA.setNumber(splitUnit);
                cfStockSplitList.add(cfStockSplitA);
                inCount=inCount.subtract(splitUnit);
                cfStockSplitVoListIn=new ArrayList<>();
                while (inCount.compareTo(new BigDecimal(0))>0){
                    if(inCount.compareTo(splitUnit)>0){
                        CfStockSplitVo cfStockSplitVoIn=new CfStockSplitVo();
                        cfStockSplitVoIn.setMaterialsName(cfStockSplitVo.getMaterialsName());
                        cfStockSplitVoIn.setMaterialsNo(cfStockSplitVo.getMaterialsNo());
                        cfStockSplitVoIn.setBatchNo(cfStockSplitVo.getBatchNo());
                        cfStockSplitVoIn.setNumber(splitUnit);
                        cfStockSplitVoListIn.add(cfStockSplitVoIn);
;
                        CfStockSplit cfStockSplit =new CfStockSplit();
                        String NextNumberIn= cfNextNumberService.generateNextNumber(CfStockScanLine.STOCK_SPLIT);
                        cfStockSplit.setSplitNo(NextNumberIn);
                        cfStockSplit.setStockListNo(cfStockSplitOne.getStockListNo());
                        cfStockSplit.setObjectSetBasicAttribute(userId,newDate);
                        cfStockSplit.setFlag(1);
                        cfStockSplit.setMaterialsNo(cfStockSplitOne.getMaterialsNo());
                        cfStockSplit.setMaterialsName(cfStockSplitOne.getMaterialsName());
                        cfStockSplit.setMode(cfStockSplitOne.getMode());
                        cfStockSplit.setBatchNoText(JSONObject.toJSONString(cfStockSplitVoListIn));
                        cfStockSplit.setSplitParentId(cfStockSplitOne.getSplitId());
                        cfStockSplit.setNumber(splitUnit);
                        cfStockSplitList.add(cfStockSplit);
                        cfStockSplitVoListIn=new ArrayList<>();
                        inCount=inCount.subtract(splitUnit);
                    }else if(inCount.compareTo(splitUnit)==0){
                        CfStockSplitVo cfStockSplitVoIn=new CfStockSplitVo();
                        cfStockSplitVoIn.setMaterialsName(cfStockSplitVo.getMaterialsName());
                        cfStockSplitVoIn.setMaterialsNo(cfStockSplitVo.getMaterialsNo());
                        cfStockSplitVoIn.setBatchNo(cfStockSplitVo.getBatchNo());
                        cfStockSplitVoIn.setNumber(splitUnit);
                        cfStockSplitVoListIn.add(cfStockSplitVoIn);
                        ;
                        CfStockSplit cfStockSplit =new CfStockSplit();
                        String NextNumberIn= cfNextNumberService.generateNextNumber(CfStockScanLine.STOCK_SPLIT);
                        cfStockSplit.setSplitNo(NextNumberIn);
                        cfStockSplit.setStockListNo(cfStockSplitOne.getStockListNo());
                        cfStockSplit.setObjectSetBasicAttribute(userId,newDate);
                        cfStockSplit.setMode(cfStockSplitOne.getMode());
                        cfStockSplit.setFlag(1);
                        cfStockSplit.setMaterialsNo(cfStockSplitOne.getMaterialsNo());
                        cfStockSplit.setMaterialsName(cfStockSplitOne.getMaterialsName());
                        cfStockSplit.setBatchNoText(JSONObject.toJSONString(cfStockSplitVoListIn));
                        cfStockSplit.setSplitParentId(cfStockSplitOne.getSplitId());
                        cfStockSplit.setNumber(splitUnit);
                        cfStockSplitList.add(cfStockSplit);
                        cfStockSplitVoListIn=new ArrayList<>();
                        inCount=inCount.subtract(splitUnit);
                        break;
                    }else{
                        CfStockSplitVo cfStockSplitVoIn=new CfStockSplitVo();
                        cfStockSplitVoIn.setMaterialsName(cfStockSplitVo.getMaterialsName());
                        cfStockSplitVoIn.setMaterialsNo(cfStockSplitVo.getMaterialsNo());
                        cfStockSplitVoIn.setBatchNo(cfStockSplitVo.getBatchNo());
                        cfStockSplitVoIn.setNumber(inCount);
                        cfStockSplitVoListIn.add(cfStockSplitVoIn);
                        break;
                    }
                }
            }else{
                CfStockSplitVo cfStockSplitVoIn=new CfStockSplitVo();
                cfStockSplitVoIn.setMaterialsName(cfStockSplitVo.getMaterialsName());
                cfStockSplitVoIn.setMaterialsNo(cfStockSplitVo.getMaterialsNo());
                cfStockSplitVoIn.setBatchNo(cfStockSplitVo.getBatchNo());
                cfStockSplitVoIn.setNumber(cfStockSplitVo.getNumber());
                cfStockSplitVoListIn.add(cfStockSplitVoIn);
            }
            if(i==cfStockSplitVoList.size()-1&&inCount.compareTo(new BigDecimal(0))>0){
                CfStockSplit cfStockSplit =new CfStockSplit();
                String NextNumberIn= cfNextNumberService.generateNextNumber(CfStockScanLine.STOCK_SPLIT);
                cfStockSplit.setSplitNo(NextNumberIn);
                cfStockSplit.setStockListNo(cfStockSplitOne.getStockListNo());
                cfStockSplit.setObjectSetBasicAttribute(userId,newDate);
                cfStockSplit.setFlag(1);
                cfStockSplit.setMaterialsNo(cfStockSplitOne.getMaterialsNo());
                cfStockSplit.setMaterialsName(cfStockSplitOne.getMaterialsName());
                cfStockSplit.setMode(cfStockSplitOne.getMode());
                cfStockSplit.setBatchNoText(JSONObject.toJSONString(cfStockSplitVoListIn));
                cfStockSplit.setSplitParentId(cfStockSplitOne.getSplitId());
                cfStockSplit.setNumber(inCount);
                cfStockSplitList.add(cfStockSplit);
                cfStockSplitVoListIn=new ArrayList<>();
            }
        }
         if(remainderNumber.compareTo(new BigDecimal(0))>0){
             integerNumber=integerNumber.add(new BigDecimal(1));
         }
         cfStockSplitOne.setFlag(1);
         this.updateById(cfStockSplitOne);
         this.insertBatch(cfStockSplitList);
        Map<String, Object> resultMap=new HashedMap();
        resultMap.put("splitNumber",integerNumber);
        resultMap.put("cfStockSplitList",cfStockSplitList);
        return resultMap;
    }

    @Override
    public List<CfStockSplit> getCfStockSplit(CfStockSplit cfStockSplit) {
        EntityWrapper  cfStockSplitEntityWrapper=  new EntityWrapper<CfStockSplit>();
        cfStockSplitEntityWrapper.eq(CfStockSplit.STOCK_LIST_NO_SQL,cfStockSplit.getStockListNo())
                .like(StringUtils.isNoneBlank(cfStockSplit.getMaterialsNo()),CfStockSplit.MATERIALS_NO_SQL,cfStockSplit.getMaterialsNo().trim())
                .like(StringUtils.isNoneBlank(cfStockSplit.getMaterialsName()),CfStockSplit.MATERIALS_NAME_SQL,cfStockSplit.getMaterialsName().trim());
        List<CfStockSplit> cfStockSplitsList=  this.selectList(cfStockSplitEntityWrapper);
        return cfStockSplitsList;
    }
}
