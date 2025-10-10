package com.ruoyi.web.controller.bussiness;

import java.util.List;
import java.util.Map;
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
import com.ruoyi.bussiness.domain.TCompensationDetail;
import com.ruoyi.bussiness.service.ITCompensationDetailService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 补偿详细记录Controller
 *
 * @author ruoyi
 * @date 2024-09-27
 */
@RestController
@RequestMapping("/bussiness/compensation/detail")
public class TCompensationDetailController extends BaseController {
    @Autowired
    private ITCompensationDetailService compensationDetailService;

    /**
     * 查询补偿详细记录列表
     */
    @PreAuthorize("@ss.hasPermi('bussiness:compensation:detail:list')")
    @GetMapping("/list")
    public TableDataInfo list(TCompensationDetail detail) {
        startPage();
        List<TCompensationDetail> list = compensationDetailService.selectDetailList(detail);
        return getDataTable(list);
    }

    /**
     * 根据批次ID查询补偿详细记录
     */
    @PreAuthorize("@ss.hasPermi('bussiness:compensation:detail:list')")
    @GetMapping("/batch/{batchId}")
    public TableDataInfo listByBatchId(@PathVariable Long batchId) {
        startPage();
        List<TCompensationDetail> list = compensationDetailService.selectDetailListByBatchId(batchId);
        return getDataTable(list);
    }

    /**
     * 获取补偿统计信息
     */
    @PreAuthorize("@ss.hasPermi('bussiness:compensation:detail:statistics')")
    @GetMapping("/statistics/{batchId}")
    public AjaxResult getStatistics(@PathVariable Long batchId) {
        Map<String, Object> statistics = compensationDetailService.getCompensationStatistics(batchId);
        return AjaxResult.success(statistics);
    }

    /**
     * 重试失败的补偿
     */
    @PreAuthorize("@ss.hasPermi('bussiness:compensation:detail:retry')")
    @Log(title = "重试补偿", businessType = BusinessType.UPDATE)
    @PostMapping("/retry")
    public AjaxResult retryFailedCompensation(@RequestBody Long[] detailIds) {
        try {
            boolean result = compensationDetailService.retryFailedCompensation(detailIds);
            if (result) {
                return AjaxResult.success("重试补偿成功");
            } else {
                return AjaxResult.error("重试补偿失败");
            }
        } catch (Exception e) {
            return AjaxResult.error("重试失败：" + e.getMessage());
        }
    }

    /**
     * 导出补偿详细记录列表
     */
    @PreAuthorize("@ss.hasPermi('bussiness:compensation:detail:export')")
    @Log(title = "补偿详细记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, TCompensationDetail detail) {
        List<TCompensationDetail> list = compensationDetailService.selectDetailList(detail);
        ExcelUtil<TCompensationDetail> util = new ExcelUtil<TCompensationDetail>(TCompensationDetail.class);
        util.exportExcel(response, list, "补偿详细记录数据");
    }

    /**
     * 获取补偿详细记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('bussiness:compensation:detail:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return AjaxResult.success(compensationDetailService.getById(id));
    }

    /**
     * 新增补偿详细记录
     */
    @PreAuthorize("@ss.hasPermi('bussiness:compensation:detail:add')")
    @Log(title = "补偿详细记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody TCompensationDetail detail) {
        return toAjax(compensationDetailService.insertDetail(detail));
    }

    /**
     * 修改补偿详细记录
     */
    @PreAuthorize("@ss.hasPermi('bussiness:compensation:detail:edit')")
    @Log(title = "补偿详细记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody TCompensationDetail detail) {
        return toAjax(compensationDetailService.updateDetail(detail));
    }

    /**
     * 删除补偿详细记录
     */
    @PreAuthorize("@ss.hasPermi('bussiness:compensation:detail:remove')")
    @Log(title = "补偿详细记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(compensationDetailService.deleteDetailByIds(ids));
    }
}
