package com.ruoyi.bussiness.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.socket.dto.SocketMessageLdVo;

import java.util.List;

/**
 * 交易雷达Mapper接口
 * 
 * @author ruoyi
 * @date 2025-10-27
 */
public interface SocketMessageLdVoMapper extends BaseMapper<SocketMessageLdVo>
{
    /**
     * 查询交易雷达
     * 
     * @param id 交易雷达主键
     * @return 交易雷达
     */
    public SocketMessageLdVo selectSocketMessageLdVoById(Long id);

    /**
     * 查询交易雷达列表
     * 
     * @param socketMessageLdVo 交易雷达
     * @return 交易雷达集合
     */
    public List<SocketMessageLdVo> selectSocketMessageLdVoList(SocketMessageLdVo socketMessageLdVo);

    /**
     * 新增交易雷达
     * 
     * @param socketMessageLdVo 交易雷达
     * @return 结果
     */
    public int insertSocketMessageLdVo(SocketMessageLdVo socketMessageLdVo);

    /**
     * 修改交易雷达
     * 
     * @param socketMessageLdVo 交易雷达
     * @return 结果
     */
    public int updateSocketMessageLdVo(SocketMessageLdVo socketMessageLdVo);

    /**
     * 删除交易雷达
     * 
     * @param id 交易雷达主键
     * @return 结果
     */
    public int deleteSocketMessageLdVoById(Long id);

    /**
     * 批量删除交易雷达
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSocketMessageLdVoByIds(Long[] ids);
}
