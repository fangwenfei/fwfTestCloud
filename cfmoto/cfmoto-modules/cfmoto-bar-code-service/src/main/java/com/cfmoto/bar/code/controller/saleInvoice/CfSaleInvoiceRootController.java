package com.cfmoto.bar.code.controller.saleInvoice;
import java.util.Map;
import java.util.Date;
import com.github.pig.common.util.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.github.pig.common.util.QueryPage;
import com.github.pig.common.util.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.cfmoto.bar.code.model.entity.CfSaleInvoiceRoot;
import com.cfmoto.bar.code.service.saleInvoice.ICfSaleInvoiceRootService;
import javax.servlet.http.HttpServletRequest;
import com.github.pig.common.web.BaseController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author FangWenFei
 * @since 2019-03-04
 */
@RestController
@RequestMapping("/cfSaleInvoiceRoot")
@Api(tags=" ")
public class CfSaleInvoiceRootController extends BaseController {
    @Autowired private ICfSaleInvoiceRootService cfSaleInvoiceRootService;

    /**
    * 通过ID查询
    *
    * @param id ID
    * @return CfSaleInvoiceRoot
    */
    @PostMapping("/getById")
    @ApiOperation(value="通过ID查询")
    public R<CfSaleInvoiceRoot> get(@RequestParam Integer id) {
        return new R<>(cfSaleInvoiceRootService.selectById(id));
    }


    /**
    * 分页查询信息
    *
    * @param params 分页对象
    * @return 分页对象
    */
    @PostMapping("/page")
    @ApiOperation(value="分页查询")
    public R<Page> page(@RequestParam Map<String, Object> params,CfSaleInvoiceRoot cfSaleInvoiceRoot) {
        return new R<>(cfSaleInvoiceRootService.selectPage(new QueryPage<>(params), new EntityWrapper<>(cfSaleInvoiceRoot)));
    }

    /**
     * 添加
     * @param  cfSaleInvoiceRoot  实体
     * @return success/false
     */
    @PostMapping("/add")
    @ApiOperation(value="添加")
    public R<Boolean> add(@RequestBody CfSaleInvoiceRoot cfSaleInvoiceRoot,HttpServletRequest httpServletRequest) {
        try{
            int userId= UserUtils.getUserId(httpServletRequest);
            cfSaleInvoiceRoot.setObjectSetBasicAttribute(userId,new Date());
            return new R<>(cfSaleInvoiceRootService.insert(cfSaleInvoiceRoot));
       }catch (Exception e){
            return new R<>(R.FAIL, e.getMessage() );
        }


    }

    /**
     * 删除
     * @param id ID
     * @return success/false
     */
    @PostMapping("/deleteById")
    @ApiOperation(value="删除通过ID")
    public R<Boolean> delete(@RequestParam Integer id) {
        CfSaleInvoiceRoot cfSaleInvoiceRoot = new CfSaleInvoiceRoot();
        return new R<>(cfSaleInvoiceRootService.updateById(cfSaleInvoiceRoot));
    }

    /**
     * 编辑
     * @param  cfSaleInvoiceRoot  实体
     * @return success/false
     */
    @PostMapping("/edit")
    @ApiOperation(value="编辑除")
    public R<Boolean> edit(@RequestBody CfSaleInvoiceRoot cfSaleInvoiceRoot) {
        return new R<>(cfSaleInvoiceRootService.updateById(cfSaleInvoiceRoot));
    }
}
