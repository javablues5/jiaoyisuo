package com.ruoyi.web.controller.bussiness;

import java.util.Arrays;
import java.util.List;

import com.ruoyi.common.crud.MpCrudTool;
import com.ruoyi.socket.dto.SocketMessageLdVo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 交易雷达Controller
 * 
 * @author ruoyi
 * @date 2025-10-27
 */
@RestController
@RequestMapping("/api/ld/socket")
public class SocketMessageLdVoController extends BaseController
{
    private static final List<String> excludeFields = Arrays.asList("password","user_id","search_value", "params", "update_by","create_by","create_time","update_time","remark");

    /**
     * 查询交易雷达列表
     */
    //@PreAuthorize("@ss.hasPermi('bussiness:socket:list')")
    @GetMapping("/list")
    public TableDataInfo list(SocketMessageLdVo socketMessageLdVo)
    {
        startPage();
        List<SocketMessageLdVo> list = MpCrudTool.selectList(socketMessageLdVo, SocketMessageLdVo.class, excludeFields);
        return getDataTable(list);
    }

    /**
     * 导出交易雷达列表
     */
    /*@PreAuthorize("@ss.hasPermi('bussiness:socket:export')")
    @Log(title = "交易雷达", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SocketMessageLdVo socketMessageLdVo)
    {
        List<SocketMessageLdVo> list = socketMessageLdVoService.selectSocketMessageLdVoList(socketMessageLdVo);
        ExcelUtil<SocketMessageLdVo> util = new ExcelUtil<SocketMessageLdVo>(SocketMessageLdVo.class);
        util.exportExcel(response, list, "交易雷达数据");
    }

    *//**
     * 获取交易雷达详细信息
     *//*
    @PreAuthorize("@ss.hasPermi('bussiness:socket:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(socketMessageLdVoService.selectSocketMessageLdVoById(id));
    }

    *//**
     * 新增交易雷达
     *//*
    @PreAuthorize("@ss.hasPermi('bussiness:socket:add')")
    @Log(title = "交易雷达", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SocketMessageLdVo socketMessageLdVo)
    {
        return toAjax(socketMessageLdVoService.insertSocketMessageLdVo(socketMessageLdVo));
    }

    *//**
     * 修改交易雷达
     *//*
    @PreAuthorize("@ss.hasPermi('bussiness:socket:edit')")
    @Log(title = "交易雷达", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SocketMessageLdVo socketMessageLdVo)
    {
        return toAjax(socketMessageLdVoService.updateSocketMessageLdVo(socketMessageLdVo));
    }

    *//**
     * 删除交易雷达
     *//*
    @PreAuthorize("@ss.hasPermi('bussiness:socket:remove')")
    @Log(title = "交易雷达", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(socketMessageLdVoService.deleteSocketMessageLdVoByIds(ids));
    }*/
}
