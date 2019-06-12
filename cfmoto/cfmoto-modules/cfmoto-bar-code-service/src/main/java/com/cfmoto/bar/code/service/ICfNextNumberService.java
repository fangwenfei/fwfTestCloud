package com.cfmoto.bar.code.service;

import com.baomidou.mybatisplus.service.IService;
import com.cfmoto.bar.code.model.entity.CfNextNumber;


public interface ICfNextNumberService extends IService<CfNextNumber> {

    String generateNextNumber( String nextType ) throws Exception;

}
