package com.cfmoto.bar.code.service.workProductionMap.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.cfmoto.bar.code.feign.SapFeignService;
import com.cfmoto.bar.code.mapper.CfBarcodeInventoryMapper;
import com.cfmoto.bar.code.mapper.CfReportWorkRecordMapper;
import com.cfmoto.bar.code.mapper.CfWorkProductionMapMapper;
import com.cfmoto.bar.code.model.dto.SelectList;
import com.cfmoto.bar.code.model.entity.CfBarcodeInventory;
import com.cfmoto.bar.code.model.entity.CfReportWorkRecord;
import com.cfmoto.bar.code.model.entity.CfWorkProductionMap;
import com.cfmoto.bar.code.model.vo.OperationWorkVo;
import com.cfmoto.bar.code.service.workProductionMap.ICfWorkProductionMapService;
import com.github.pig.common.constant.HandleRefConstants;
import com.github.pig.common.util.R;
import com.github.pig.common.util.UserUtils;
import com.github.pig.common.util.exception.ValidateCodeException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author FangWenFei
 * @since 2019-06-10
 */
@Service
public class CfWorkProductionMapServiceImpl extends ServiceImpl<CfWorkProductionMapMapper, CfWorkProductionMap> implements ICfWorkProductionMapService {

    @Autowired
    private CfBarcodeInventoryMapper cfBarcodeInventoryMapper ;

    @Autowired
    private CfReportWorkRecordMapper cfReportWorkRecordMapper;

    @Autowired
    private SapFeignService sapFeignService;

    @Override
    public List<SelectList> selectWorkProductionMapByAll() {
        List<SelectList> selectList=new ArrayList<>();
        EntityWrapper entityWrapper=  new EntityWrapper<CfWorkProductionMap>();
        entityWrapper.setSqlSelect(CfWorkProductionMap.SQL_PRODUCTION_NAME).groupBy(CfWorkProductionMap.SQL_PRODUCTION_NAME);
        List<CfWorkProductionMap> workProductionMapList=  this.selectList(entityWrapper);
        workProductionMapList.forEach(c->{
            SelectList s=new SelectList();
            s.setSelectKey(c.getProductionName());
            s.setSelectValue(c.getProductionName());
            s.setSelectDescription(c.getProductionName());
            selectList.add(s);
                }
        );
        return selectList;
    }

    @Override
    @Transactional(rollbackFor = ValidateCodeException.class)
    public OperationWorkVo submitAllData(String barCode, String workNo, HttpServletRequest request) throws ValidateCodeException {
        if(!StringUtils.isNotBlank(barCode)){
            throw  new ValidateCodeException(CfWorkProductionMap.BARCODE_EX);
        }
        if(!StringUtils.isNotBlank(workNo)){
            throw  new ValidateCodeException(CfWorkProductionMap.WORKNO_EX);
        }
        int userId= UserUtils.getUserId(request);
        CfBarcodeInventory cfBarcodeInventory=new CfBarcodeInventory();
        cfBarcodeInventory.setBarcode(barCode);
        cfBarcodeInventory=cfBarcodeInventoryMapper.selectOne(cfBarcodeInventory);
        if(cfBarcodeInventory==null){
            throw  new ValidateCodeException(CfWorkProductionMap.BARCODE_IS_NULL_EX);
        }
        CfReportWorkRecord cfReportWorkRecord=new CfReportWorkRecord();
        cfReportWorkRecord.setBarcode(barCode);
        cfReportWorkRecord=cfReportWorkRecordMapper.selectOne(cfReportWorkRecord);
        if(cfReportWorkRecord!=null){
            if(new BigDecimal(cfReportWorkRecord.getWorkNumber()).compareTo(new BigDecimal(workNo))>=0){
                throw  new ValidateCodeException(CfWorkProductionMap.BARCODE_USERD_EX);
            }
        }
        Map<String, Object> callParamMap = new HashMap<String, Object>();
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put( "IV_AUFNR",cfBarcodeInventory.getProductionTaskOrder() ); //生产订单号
        paramMap.put( "IV_VORNR",workNo ); //操作/活动编号
        paramMap.put( "IV_LMNGA",1 ); //报工数量
        paramMap.put( "IV_STATU","1" ); //标识码（0：返回未清数量；1：报工操作）
        callParamMap.put( HandleRefConstants.FUNCTION_NAME, "ZPP_BC_001" );
        callParamMap.put( HandleRefConstants.PARAM_MAP, paramMap );
        R returnR = sapFeignService.executeJcoFunction( callParamMap );
        if( returnR.getCode()!=0 ){
            throw  new ValidateCodeException( returnR.getMsg());

        }
        Map<String, Object> dataMap = (Map<String, Object>) returnR.getData();
        if( (Integer) dataMap.get( "EV_STATUS" )==0 ){
            throw  new ValidateCodeException( (String) dataMap.get( "EV_MESSAGE" ));
        }
        OperationWorkVo   operationWorkVo = new OperationWorkVo();
        operationWorkVo.setTaskNo( cfBarcodeInventory.getProductionTaskOrder()  );
        operationWorkVo.setOperation( workNo );
        operationWorkVo.setOperationUnclearQty( dataMap.get( "EV_LMNGA" ).toString() );
         if(cfReportWorkRecord!=null){
             cfReportWorkRecord.setWorkNumber(workNo);
             cfReportWorkRecordMapper.updateAllColumnById(cfReportWorkRecord);
         }else{
             cfReportWorkRecord=new CfReportWorkRecord();
             cfReportWorkRecord.setWorkNumber(workNo);
             cfReportWorkRecord.setBarcode(barCode);
             cfReportWorkRecord.setBarcodeType(cfBarcodeInventory.getBarcodeType());
             cfReportWorkRecord.setProductionTaskOrder(cfBarcodeInventory.getProductionTaskOrder());
             cfReportWorkRecord.setObjectSetBasicAttribute(userId,new Date());
             cfReportWorkRecordMapper.insert(cfReportWorkRecord);
         }
        return operationWorkVo;
    }

    @Override
    @Transactional(rollbackFor = ValidateCodeException.class)
    public OperationWorkVo submitAllDataThreeBarCode(String barCode, String workNo, HttpServletRequest request) throws ValidateCodeException {
        if(!StringUtils.isNotBlank(barCode)){
            throw  new ValidateCodeException(CfWorkProductionMap.BARCODE_EX);
        }
        int userId= UserUtils.getUserId(request);
        CfBarcodeInventory cfBarcodeInventory=new CfBarcodeInventory();
        cfBarcodeInventory.setBarcode(barCode);
        cfBarcodeInventory=cfBarcodeInventoryMapper.selectOne(cfBarcodeInventory);
        //判断条码
        if(cfBarcodeInventory==null){
            throw  new ValidateCodeException(CfWorkProductionMap.BARCODE_IS_NULL_EX);
        }
        CfReportWorkRecord cfReportWorkRecord=new CfReportWorkRecord();
        cfReportWorkRecord.setBarcode(barCode);
        cfReportWorkRecord=cfReportWorkRecordMapper.selectOne(cfReportWorkRecord);
        if(cfReportWorkRecord!=null){
            throw  new ValidateCodeException(CfWorkProductionMap.BARCODE_USERD_EX);
        }
        Map<String, Object> callParamMap = new HashMap<String, Object>();
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put( "IV_AUFNR",cfBarcodeInventory.getProductionTaskOrder() ); //生产订单号
        paramMap.put( "IV_VORNR",workNo ); //操作/活动编号
        paramMap.put( "IV_LMNGA",1 ); //报工数量
        paramMap.put( "IV_STATU","1" ); //标识码（0：返回未清数量；1：报工操作）
        callParamMap.put( HandleRefConstants.FUNCTION_NAME, "ZPP_BC_001" );
        callParamMap.put( HandleRefConstants.PARAM_MAP, paramMap );
        R returnR = sapFeignService.executeJcoFunction( callParamMap );
        if( returnR.getCode()!=0 ){
            throw  new ValidateCodeException( returnR.getMsg());

        }
        Map<String, Object> dataMap = (Map<String, Object>) returnR.getData();
        if( (Integer) dataMap.get( "EV_STATUS" )==0 ){
            throw  new ValidateCodeException( (String) dataMap.get( "EV_MESSAGE" ));
        }
        cfReportWorkRecord=new CfReportWorkRecord();
        cfReportWorkRecord.setWorkNumber(workNo);
        cfReportWorkRecord.setBarcode(barCode);
        cfReportWorkRecord.setBarcodeType(cfBarcodeInventory.getBarcodeType());
        cfReportWorkRecord.setProductionTaskOrder(cfBarcodeInventory.getProductionTaskOrder());
        cfReportWorkRecord.setObjectSetBasicAttribute(userId,new Date());
        cfReportWorkRecordMapper.insert(cfReportWorkRecord);

        OperationWorkVo   operationWorkVo = new OperationWorkVo();
        operationWorkVo.setTaskNo( cfBarcodeInventory.getProductionTaskOrder()  );
        operationWorkVo.setOperation( workNo );
        operationWorkVo.setOperationUnclearQty( dataMap.get( "EV_LMNGA" ).toString() );


        return operationWorkVo;
    }
}
