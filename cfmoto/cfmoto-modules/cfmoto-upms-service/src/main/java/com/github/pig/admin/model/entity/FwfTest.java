package com.github.pig.admin.model.entity;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author FangWenFei
 * @since 2018-12-18
 */
@TableName("fwf_test")
public class FwfTest extends Model<FwfTest> {

    private static final long serialVersionUID = 1L;

    private Integer fwftest;
    private String fwfname;


    public Integer getFwftest() {
        return fwftest;
    }

    public void setFwftest(Integer fwftest) {
        this.fwftest = fwftest;
    }

    public String getFwfname() {
        return fwfname;
    }

    public void setFwfname(String fwfname) {
        this.fwfname = fwfname;
    }

    @Override
    protected Serializable pkVal() {
        return this.fwftest;
    }

    @Override
    public String toString() {
        return "FwfTest{" +
        ", fwftest=" + fwftest +
        ", fwfname=" + fwfname +
        "}";
    }
}
