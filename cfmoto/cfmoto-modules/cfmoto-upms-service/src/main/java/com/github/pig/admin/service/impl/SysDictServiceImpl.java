/*
 *    Copyright (c) 2018-2025, lengleng All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * Neither the name of the pig4cloud.com developer nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 * Author: lengleng (wangiegie@gmail.com)
 */

package com.github.pig.admin.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.github.pig.admin.mapper.SysDictMapper;
import com.github.pig.admin.model.entity.SysDict;
import com.github.pig.admin.service.SysDictService;
import com.github.pig.common.constant.CommonConstant;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 字典表 服务实现类
 * </p>
 *
 * @author lengleng
 * @since 2017-11-19
 */
@Service
public class SysDictServiceImpl extends ServiceImpl<SysDictMapper, SysDict> implements SysDictService {


    @Override
    @Cacheable(value = "R:dict_details", key = "'R:dict_details:'+#type")
    public List<SysDict> getSysDictList(String type) {
        SysDict condition = new SysDict();
        condition.setDelFlag(CommonConstant.STATUS_NORMAL);
        condition.setType(type);
        return  this.selectList(new EntityWrapper<>(condition));
    }
}
