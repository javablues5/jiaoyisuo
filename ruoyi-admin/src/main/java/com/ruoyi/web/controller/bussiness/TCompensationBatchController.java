package com.ruoyi.web.controller.bussiness;

import java.math.BigDecimal;
import java.util.Date;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.format.annotation.DateTimeFormat;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.bussiness.domain.TCompensationBatch;
import com.ruoyi.bussiness.service.ITCompensationBatchService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 补偿批次Controller
 *
 * @author ruoyi
 * @date 2024-09-27
 */
@RestController
@RequestMapping("/bussiness/compensation/batch")
public class TCompensationBatchController extends BaseController {
    @Autowired
    private ITCompensationBatchService compensationBatchService;

    /**
     * 查询补偿批次列表
     */
    @PreAuthorize("@ss.hasPermi('bussiness:compensation:batch:list')")
    @GetMapping("/list")
    public TableDataInfo list(TCompensationBatch batch) {
        startPage();
        List<TCompensationBatch> list = compensationBatchService.selectBatchList(batch);
        return getDataTable(list);
    }

    /**
     * 搜索订单预览
     */
    @PreAuthorize("@ss.hasPermi('bussiness:compensation:batch:search')")
    @GetMapping("/search/preview")
    public AjaxResult searchPreview(
            @RequestParam(required = false) String symbol,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date startTime,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endTime) {
        try {
            Map<String, Object> result = compensationBatchService.searchOrdersPreview(symbol, startTime, endTime);
            return AjaxResult.success(result);
        } catch (Exception e) {
            return AjaxResult.error("搜索失败：" + e.getMessage());
        }
    }

    /**
     * 创建补偿批次
     */
    @PreAuthorize("@ss.hasPermi('bussiness:compensation:batch:create')")
    @Log(title = "创建补偿批次", businessType = BusinessType.INSERT)
    @PostMapping("/create")
    public AjaxResult createBatch(
            @RequestParam Long strategyId,
            @RequestParam String batchName,
            @RequestParam(required = false) String symbol,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date startTime,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endTime,
            @RequestParam(required = false) BigDecimal customRate) {
        try {
            TCompensationBatch batch = compensationBatchService.createCompensationBatch(
                strategyId, batchName, symbol, startTime, endTime, customRate);
            return AjaxResult.success("补偿批次创建成功", batch);
        } catch (Exception e) {
            return AjaxResult.error("创建失败：" + e.getMessage());
        }
    }

    /**
     * 执行补偿批次
     */
    @PreAuthorize("@ss.hasPermi('bussiness:compensation:batch:execute')")
    @Log(title = "执行补偿批次", businessType = BusinessType.UPDATE)
    @PostMapping("/execute/{id}")
    public AjaxResult executeBatch(@PathVariable Long id) {
        try {
            boolean result = compensationBatchService.executeCompensationBatch(id);
            if (result) {
                return AjaxResult.success("补偿批次执行成功");
            } else {
                return AjaxResult.error("补偿批次执行失败");
            }
        } catch (Exception e) {
            return AjaxResult.error("执行失败：" + e.getMessage());
        }
    }

    /**
     * 取消补偿批次
     */
    @PreAuthorize("@ss.hasPermi('bussiness:compensation:batch:cancel')")
    @Log(title = "取消补偿批次", businessType = BusinessType.UPDATE)
    @PostMapping("/cancel/{id}")
    public AjaxResult cancelBatch(@PathVariable Long id) {
        try {
            boolean result = compensationBatchService.cancelCompensationBatch(id);
            if (result) {
                return AjaxResult.success("补偿批次已取消");
            } else {
                return AjaxResult.error("取消失败，批次状态不允许取消");
            }
        } catch (Exception e) {
            return AjaxResult.error("取消失败：" + e.getMessage());
        }
    }

    /**
     * 导出补偿批次列表
     */
    @PreAuthorize("@ss.hasPermi('bussiness:compensation:batch:export')")
    @Log(title = "补偿批次", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, TCompensationBatch batch) {
        List<TCompensationBatch> list = compensationBatchService.selectBatchList(batch);
        ExcelUtil<TCompensationBatch> util = new ExcelUtil<TCompensationBatch>(TCompensationBatch.class);
        util.exportExcel(response, list, "补偿批次数据");
    }

    /**
     * 获取补偿批次详细信息
     */
    @PreAuthorize("@ss.hasPermi('bussiness:compensation:batch:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return AjaxResult.success(compensationBatchService.getById(id));
    }

    /**
     * 新增补偿批次
     */
    @PreAuthorize("@ss.hasPermi('bussiness:compensation:batch:add')")
    @Log(title = "补偿批次", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody TCompensationBatch batch) {
        return toAjax(compensationBatchService.insertBatch(batch));
    }

    /**
     * 修改补偿批次
     */
    @PreAuthorize("@ss.hasPermi('bussiness:compensation:batch:edit')")
    @Log(title = "补偿批次", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody TCompensationBatch batch) {
        return toAjax(compensationBatchService.updateBatch(batch));
    }

    /**
     * 删除补偿批次
     */
    @PreAuthorize("@ss.hasPermi('bussiness:compensation:batch:remove')")
    @Log(title = "补偿批次", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(compensationBatchService.deleteBatchByIds(ids));
    }
}
