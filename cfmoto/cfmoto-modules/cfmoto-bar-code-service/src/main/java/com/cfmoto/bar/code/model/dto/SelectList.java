package com.cfmoto.bar.code.model.dto;

import lombok.Data;

import java.io.Serializable;

/* **********************************************************************
 *              Created by FangWenFei on 2019/2/20.                     
 * **********************************************************************
 * **********Thunderstorm, rain and dew are all grace of heaven**********
 * **********************************************************************
 */
@Data
public class SelectList implements Serializable {
    private static final long serialVersionUID = 1L;
    private String selectKey  ;
    private String selectValue  ;
    private String selectDescription  ;
    public SelectList() {
        super();
    }
    public  SelectList(String selectKey,String selectValue,String selectDescription){
        super();
        this.selectKey=selectKey;
        this.selectValue=selectValue;
        this.selectDescription=selectDescription;
    }

}
