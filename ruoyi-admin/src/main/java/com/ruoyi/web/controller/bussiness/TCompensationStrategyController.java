package com.ruoyi.web.controller.bussiness;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.bussiness.domain.TCompensationStrategy;
import com.ruoyi.bussiness.service.ITCompensationStrategyService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 补偿策略配置Controller
 *
 * @author ruoyi
 * @date 2024-09-27
 */
@RestController
@RequestMapping("/bussiness/compensation/strategy")
public class TCompensationStrategyController extends BaseController {
    @Autowired
    private ITCompensationStrategyService compensationStrategyService;

    /**
     * 查询补偿策略配置列表
     */
    @PreAuthorize("@ss.hasPermi('bussiness:compensation:strategy:list')")
    @GetMapping("/list")
    public TableDataInfo list(TCompensationStrategy strategy) {
        startPage();
        List<TCompensationStrategy> list = compensationStrategyService.selectStrategyList(strategy);
        return getDataTable(list);
    }

    /**
     * 获取启用的策略列表
     */
    @GetMapping("/enabled")
    public AjaxResult getEnabledStrategies() {
        List<TCompensationStrategy> list = compensationStrategyService.selectEnabledStrategies();
        return AjaxResult.success(list);
    }

    /**
     * 导出补偿策略配置列表
     */
    @PreAuthorize("@ss.hasPermi('bussiness:compensation:strategy:export')")
    @Log(title = "补偿策略配置", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, TCompensationStrategy strategy) {
        List<TCompensationStrategy> list = compensationStrategyService.selectStrategyList(strategy);
        ExcelUtil<TCompensationStrategy> util = new ExcelUtil<TCompensationStrategy>(TCompensationStrategy.class);
        util.exportExcel(response, list, "补偿策略配置数据");
    }

    /**
     * 获取补偿策略配置详细信息
     */
    @PreAuthorize("@ss.hasPermi('bussiness:compensation:strategy:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return AjaxResult.success(compensationStrategyService.getById(id));
    }

    /**
     * 新增补偿策略配置
     */
    @PreAuthorize("@ss.hasPermi('bussiness:compensation:strategy:add')")
    @Log(title = "补偿策略配置", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody TCompensationStrategy strategy) {
        return toAjax(compensationStrategyService.insertStrategy(strategy));
    }

    /**
     * 修改补偿策略配置
     */
    @PreAuthorize("@ss.hasPermi('bussiness:compensation:strategy:edit')")
    @Log(title = "补偿策略配置", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody TCompensationStrategy strategy) {
        return toAjax(compensationStrategyService.updateStrategy(strategy));
    }

    /**
     * 删除补偿策略配置
     */
    @PreAuthorize("@ss.hasPermi('bussiness:compensation:strategy:remove')")
    @Log(title = "补偿策略配置", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(compensationStrategyService.deleteStrategyByIds(ids));
    }
}
