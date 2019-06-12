package com.cfmoto.bar.code.service;

import com.cfmoto.bar.code.model.dto.CfStockSplitDto;

import javax.xml.bind.ValidationException;
import java.util.List;

/**
 * 仓库备料交接业务层接口
 *
 * @author ye
 */
public interface ICfStorageStockHandoverService {
    /**
     * 仓库备料交接扫描条码接口
     *
     * @param barcode 条码
     * @return dto sap接口方法参数传输对象
     */
    List<CfStockSplitDto> scan(String barcode) throws ValidationException;

    /**
     * 备料交接完成后数据的更新
     *
     * @param dtoList 备料拆分数据传输对象\
     * @param barcode 条码
     * @param userId  用户Id
     */
    void updateDataAfterSap(List<CfStockSplitDto> dtoList, String barcode, int userId) throws ValidationException;
}
