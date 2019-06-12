package com.cfmoto.bar.code.service;

import com.cfmoto.bar.code.model.dto.SelectList;
import com.cfmoto.bar.code.model.entity.CfStorageLocation;
import com.baomidou.mybatisplus.service.IService;
import com.github.pig.common.util.exception.ValidateCodeException;

import java.util.List;

/**
 * <p>
 * 仓库 服务类
 * </p>
 *
 * @author space
 * @since 2019-03-05
 */
public interface ICfStorageLocationService extends IService<CfStorageLocation> {

    List<CfStorageLocation> getWareHouse(String key,Integer userId);

    /**
     * 获取所有仓库
     * @return
     */
    List<SelectList> getStorageLocationAllWareHouse(Integer userId);

    /***
     * 通过仓库获取所有存储区域
     * @param wareHouse
     * @return
     */
    List<SelectList> getstorageAreaByWareHouse(String wareHouse,String site);

    /**
     * 根据工厂查询仓库并分组
     * @param site
     * @return
     */
    List<String> getWareHouseBySite(String site);

    List<CfStorageLocation> importExcel(int userId,List<CfStorageLocation> cfStorageLocationList) throws ValidateCodeException;


}
