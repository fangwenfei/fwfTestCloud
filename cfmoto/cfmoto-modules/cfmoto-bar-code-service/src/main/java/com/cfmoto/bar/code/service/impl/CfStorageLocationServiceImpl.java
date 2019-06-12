package com.cfmoto.bar.code.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.cfmoto.bar.code.feign.UserFeignService;
import com.cfmoto.bar.code.mapper.CfStorageLocationMapper;
import com.cfmoto.bar.code.model.dto.SelectList;
import com.cfmoto.bar.code.model.entity.CfStorageLocation;
import com.cfmoto.bar.code.service.ICfStorageLocationService;
import com.github.pig.common.util.exception.ValidateCodeException;
import com.github.pig.common.vo.UserVO;
import lombok.extern.java.Log;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * 仓库 服务实现类
 * </p>
 *
 * @author space
 * @since 2019-03-05
 */
@Service
@Log
public class CfStorageLocationServiceImpl extends ServiceImpl<CfStorageLocationMapper, CfStorageLocation> implements ICfStorageLocationService {

    @Autowired
    private CfStorageLocationMapper storageLocationMapper;

    @Autowired
    private UserFeignService userFeignService;

    @Override
    public List<CfStorageLocation> getWareHouse(String key,Integer userId) {
        UserVO user = userFeignService.user(userId);
        HashMap<String, Object> map = new HashMap<>();
        map.put("key", key);
        map.put("site",user.getSite());
        return storageLocationMapper.getWareHouse(map);
    }

    @Override
    public List<SelectList> getStorageLocationAllWareHouse(Integer userId) {
        String site = userFeignService.user(userId).getSite();
        EntityWrapper<CfStorageLocation> wrapper = new EntityWrapper<>();
        //查询对应用户工厂的仓库
        wrapper.eq("site", site);
        wrapper.groupBy(CfStorageLocation.CF_BARCODE_WAREHOUSE_SQL
                + CfStorageLocation.CF_AND_SQL + CfStorageLocation.CF_BARCODE_WAREHOUSE_DESCRIPTION_SQL
                + CfStorageLocation.CF_AND_SQL + CfStorageLocation.CF_SITE_SQL
        )
                .setSqlSelect(CfStorageLocation.CF_BARCODE_WAREHOUSE_SQL
                        + CfStorageLocation.CF_AND_SQL + CfStorageLocation.CF_BARCODE_WAREHOUSE_DESCRIPTION_SQL
                        + CfStorageLocation.CF_AND_SQL + CfStorageLocation.CF_SITE_SQL
                );
        List<CfStorageLocation> cfStorageLocationList = this.selectList(wrapper);
        List<SelectList> selectList = new ArrayList<>();
        cfStorageLocationList.forEach(c -> {
                    SelectList selectListAt = new SelectList();
                    selectListAt.setSelectKey(c.getSite());
                    selectListAt.setSelectValue(c.getWareHouse());
                    selectListAt.setSelectDescription(c.getWareHouseDeScription());
                    selectList.add(selectListAt);
                }
        );
        return selectList;
    }

    @Override
    public List<SelectList> getstorageAreaByWareHouse(String wareHouse, String site) {
        EntityWrapper<CfStorageLocation> wrapper = new EntityWrapper<CfStorageLocation>();
        wrapper.eq(CfStorageLocation.CF_BARCODE_WAREHOUSE_SQL, wareHouse).and().eq("site", site);
        List<CfStorageLocation> cfStorageLocationList = this.selectList(wrapper);
        List<SelectList> selectList = new ArrayList<>();
        cfStorageLocationList.forEach(c -> {
                    SelectList selectListAt = new SelectList();
                    selectListAt.setSelectKey(String.valueOf(c.getStorageLocationId()));
                    selectListAt.setSelectValue(c.getStorageArea());
                    selectListAt.setSelectDescription(c.getStorageArea());
                    selectList.add(selectListAt);
                }
        );
        return selectList;
    }

    @Override
    public List<String> getWareHouseBySite(String site) {
        return storageLocationMapper.getWareHouseBySite(site);
    }

    @Override
    public List<CfStorageLocation> importExcel(int userId,List<CfStorageLocation> cfStorageLocationList) throws ValidateCodeException {
        //也可以使用MultipartFile,使用 FileUtil.importExcel(MultipartFile file, Integer titleRows, Integer headerRows, Class<T>                pojoClass)导入
        System.out.println("导入数据一共【"+cfStorageLocationList.size()+"】行");
        log.info("导入数据一共 :"+ JSONArray.toJSON(cfStorageLocationList).toString());
        Date date=new Date();
        int errorNumber=0;
        List<CfStorageLocation> errorCfStorageLocationList=new ArrayList<>();
        for(CfStorageLocation cfStorageLocation:cfStorageLocationList){
            try {
            cfStorageLocation.setObjectSetBasicAttribute(userId,date);
            cfStorageLocation.setSite(cfStorageLocation.getSite().trim());
            cfStorageLocation.setWareHouse(cfStorageLocation.getWareHouse().trim());
            cfStorageLocation.setStorageArea(StringUtils.isNotBlank(cfStorageLocation.getStorageArea())?cfStorageLocation.getStorageArea().trim():"");
            cfStorageLocation.setDescription(StringUtils.isNotBlank(cfStorageLocation.getDescription())?cfStorageLocation.getDescription().trim():"");
            cfStorageLocation.setWareHouseDeScription(StringUtils.isNotBlank(cfStorageLocation.getWareHouseDeScription())?cfStorageLocation.getWareHouseDeScription().trim():"");
            this.insert(cfStorageLocation);
            }catch (Exception e){
                if(e.getMessage().contains("MySQLIntegrityConstraintViolationException")){
                    cfStorageLocation.setDescription(CfStorageLocation.EX_WAREHOUSE_DOUBLE);
                }else{
                    cfStorageLocation.setDescription(CfStorageLocation.EX_WAREHOUSE_UNDEFINED);
                }

                errorCfStorageLocationList.add(cfStorageLocation);
                errorNumber=errorNumber+1;
            }
        }
        if(errorNumber>0){
           throw new ValidateCodeException(JSONArray.toJSON(errorCfStorageLocationList).toString());
        }

        return errorCfStorageLocationList;
    }
}
