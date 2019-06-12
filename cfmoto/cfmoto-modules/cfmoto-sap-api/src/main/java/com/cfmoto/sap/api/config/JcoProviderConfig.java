package com.cfmoto.sap.api.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class JcoProviderConfig {

    @Value("${sap.jco.provider.destName}")
    public   String  jcoDestName;

    @Value("${sap.jco.provider.ashost}")
    public   String  jcoAshost;

    @Value("${sap.jco.provider.sysnr}")
    public   String  jcoSysnr;

    @Value("${sap.jco.provider.client}")
    public   String  jcoClient;

    @Value("${sap.jco.provider.user}")
    public   String  jcoUser;

    @Value("${sap.jco.provider.passwd}")
    public   String  jcoPasswd;

    @Value("${sap.jco.provider.pool_capacity}")
    public   String  jcoPoolCapacity;

    @Value("${sap.jco.provider.peak_limit}")
    public   String  jcoPeakLimit;

    @Value("${sap.jco.provider.saprouter}")
    public   String  jcoSaprouter;

    @Value("${sap.jco.provider.lang}")
    public   String jcoLang;

}
