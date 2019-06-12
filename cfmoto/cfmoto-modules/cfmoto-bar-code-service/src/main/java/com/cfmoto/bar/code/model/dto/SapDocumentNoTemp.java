package com.cfmoto.bar.code.model.dto;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>
 * 模拟接收sap接收的单据数据量
 * </p>
 *
 * @author  FangWenFei
 * @since 2019-02-27
 */
@TableName("sap_document_no_temp")
@ApiModel(value="SapDocumentNoTemp",description="模拟接收sap接收的单据数据量")
public class SapDocumentNoTemp extends Model<SapDocumentNoTemp> {

    private static final long serialVersionUID = 1L;

    /**
     * 单据号
     */
    @ApiModelProperty(value="单据号")
    @TableField("document_no")
    private String documentNo;
    /**
     * 该单据数量
     */
    @ApiModelProperty(value="该单据数量")
    @TableField("document_number")
    private Integer documentNumber;


    public String getDocumentNo() {
        return documentNo;
    }

    public void setDocumentNo(String documentNo) {
        this.documentNo = documentNo;
    }

    public Integer getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(Integer documentNumber) {
        this.documentNumber = documentNumber;
    }

    @Override
    public String toString() {
        return "SapDocumentNoTemp{" +
        ", documentNo=" + documentNo +
        ", documentNumber=" + documentNumber +
        "}";
    }

    @Override
    protected Serializable pkVal() {
        return null;
    }
}
