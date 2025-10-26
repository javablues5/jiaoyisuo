package com.ruoyi.web.controller.bussiness;

import java.util.List;

import com.ruoyi.bussiness.domain.TAppUser;
import com.ruoyi.web.controller.common.ApiBaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.bussiness.domain.TAppMail;
import com.ruoyi.bussiness.service.ITAppMailService;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 1v1站内信Controller
 * 
 * @author ruoyi
 * @date 2023-07-18
 */
@RestController
@RequestMapping("/api/mail")
public class TAppMailController extends ApiBaseController
{
    @Autowired
    private ITAppMailService tAppMailService;

    /**
     * 查询站内信列表
     */
    @PostMapping("/list")
    public TableDataInfo list(TAppMail tAppMail)
    {
        startPage();
        TAppUser appUser = getAppUser();
        tAppMail.setUserId(appUser.getUserId());
        tAppMail.setDelFlag("0");
        List<TAppMail> list = tAppMailService.selectTAppMailList(tAppMail);
        TAppMail t = new TAppMail();
        t.setType("2");
        t.setDelFlag("0");
        List<TAppMail> qz = tAppMailService.selectTAppMailList(t);
        list.addAll(qz);
        return getDataTable(list);
    }

    /**
     * 查询1v1站内信列表
     */
    @PostMapping("/listByUserId")
    public TableDataInfo listByUserId(TAppMail tAppMail)
    {
        TAppUser appUser = getAppUser();
        tAppMail.setUserId(appUser.getUserId());
        List<TAppMail> list = tAppMailService.listByUserId(tAppMail);
        return getDataTable(list);
    }

    /**
     * 修改1v1站内信
     */
    @PostMapping("/updateMail")
    public AjaxResult updateMail(Long[] ids)
    {
        TAppUser appUser = getAppUser();
        return toAjax(tAppMailService.updateMail(ids,appUser));
    }


}
