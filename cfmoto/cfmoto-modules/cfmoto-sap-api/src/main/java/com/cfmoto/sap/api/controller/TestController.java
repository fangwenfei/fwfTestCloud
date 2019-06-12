package com.cfmoto.sap.api.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/* **********************************************************************
 *              Created by FangWenFei on 2019/3/1.                     
 * **********************************************************************
 * **********Thunderstorm, rain and dew are all grace of heaven**********
 * **********************************************************************
 */
@RestController
@RequestMapping("/test")
@Slf4j
public class TestController {

    @GetMapping("/fwf/{username}")
    public  String getName(@PathVariable("username") String username){
        log.info("this is a fwf test mode ,it is real success 1");
        return "this is a fwf test mode ,it is real success 1";
    }
}
