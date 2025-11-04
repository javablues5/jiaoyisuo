package com.ruoyi.common.crud;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.*;

/**
 * MyBatis-Plus 通用 CRUD 工具类
 * 支持静态方法调用，但需要实体类对应的 Mapper 接口继承 BaseMapper
 */
@Component
public class MpCrudTool implements ApplicationContextAware {

    // 静态持有 ApplicationContext
    private static ApplicationContext applicationContext;

    /**
     * 必须实现 ApplicationContextAware 接口以静态获取 ApplicationContext
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        MpCrudTool.applicationContext = applicationContext;
    }

    /**
     * 【核心方法】
     * 静态方法：根据实体类型获取对应的 BaseMapper 实例。
     * @param entityClass 实体类 Class 对象
     * @return 对应的 BaseMapper 实例
     */
    private static <T> BaseMapper<T> getMapper(Class<T> entityClass) {
        if (applicationContext == null) {
            throw new IllegalStateException("ApplicationContext 尚未初始化，请确保 MpCrudTool 已经注入到 Spring 容器中。");
        }

        // 约定：所有 Mapper 的命名规范是 [实体类名] + "Mapper"
        String mapperName = entityClass.getSimpleName() + "Mapper";
        // 将首字母转小写，符合 Spring Bean 的默认命名
        String beanName = mapperName.substring(0, 1).toLowerCase() + mapperName.substring(1);

        try {
            // 从 Spring 容器中查找 Bean
            return (BaseMapper<T>) applicationContext.getBean(beanName);
        } catch (BeansException e) {
            try {
                return (BaseMapper<T>) applicationContext.getBean(mapperName);
            } catch (BeansException e1) {
                // 如果找不到，尝试查找以 I 开头的接口名称，例如 IUserMapper
                String interfaceName = "i" + mapperName;
                String interfaceBeanName = interfaceName.substring(0, 1).toLowerCase() + interfaceName.substring(1);
                try {
                    return (BaseMapper<T>) applicationContext.getBean(interfaceBeanName);
                } catch (BeansException ex) {
                    throw new RuntimeException("未能找到实体类 " + entityClass.getSimpleName() + " 对应的 Mapper Bean。请检查 Bean 名是否为: " + beanName + " 或 " + interfaceBeanName, e);
                }
            }
        }
    }

    // --- 增 ---

    /**
     * 通用插入方法
     * @param entity 要插入的实体对象
     * @return 影响的行数
     */
    public static <T> int insert(T entity) {
        BaseMapper<T> mapper = getMapper((Class<T>) entity.getClass());
        return mapper.insert(entity);
    }

    // --- 删 ---

    /**
     * 通用按 ID 删除方法
     * @param entityClass 实体类 Class
     * @param id 主键 ID
     * @return 影响的行数
     */
    public static <T> int deleteById(Class<T> entityClass, Serializable id) {
        BaseMapper<T> mapper = getMapper(entityClass);
        return mapper.deleteById(id);
    }

    /**
     * 通用按 Map 条件删除
     * @param entityClass 实体类 Class
     * @param columnMap 条件 Map (列名 -> 值)
     * @return 影响的行数
     */
    public static <T> int deleteByMap(Class<T> entityClass, Map<String, Object> columnMap) {
        BaseMapper<T> mapper = getMapper(entityClass);
        return mapper.deleteByMap(columnMap);
    }

    // --- 改 ---

    /**
     * 通用按 ID 更新方法 (只更新非空的字段)
     * @param entity 要更新的实体对象 (ID 字段必须有值)
     * @return 影响的行数
     */
    public static <T> int updateById(T entity) {
        BaseMapper<T> mapper = getMapper((Class<T>) entity.getClass());
        return mapper.updateById(entity);
    }

    /**
     * 通用更新方法 (只更新非空的字段)
     * @param entity 要更新的实体对象 (ID 字段必须有值)
     * @return 影响的行数
     */
    public static <T> int update(T entity) {
        BaseMapper<T> mapper = getMapper((Class<T>) entity.getClass());
        //return mapper.updateById(entity);
        return mapper.update(entity,Wrappers.lambdaUpdate(entity));
    }

    // --- 查 ---

    /**
     * 通用按 ID 查找单条记录
     * @param entityClass 实体类 Class
     * @param id 主键 ID
     * @return 对应的实体对象，或 null
     */
    public static <T> T selectById(Class<T> entityClass, Serializable id) {
        BaseMapper<T> mapper = getMapper(entityClass);
        return mapper.selectById(id);
    }

    /**
     * 通用查找所有记录
     * @param entityClass 实体类 Class
     * @return 实体列表
     */
    public static <T> List<T> selectList(Class<T> entityClass) {
        BaseMapper<T> mapper = getMapper(entityClass);
        // 查询所有，使用空的 Wrapper
        return mapper.selectList(Wrappers.emptyWrapper());
    }

    /**
     * 通用按条件查找列表 (使用 QueryWrapper)
     * @param entityClass 实体类 Class
     * @param wrapper 查询条件
     * @return 实体列表
     */
    public static <T> List<T> selectList(Class<T> entityClass, QueryWrapper<T> wrapper) {
        BaseMapper<T> mapper = getMapper(entityClass);
        return mapper.selectList(wrapper);
    }

    /**
     * 通用按T条件查找所有记录
     * @param entityClass 实体类 Class
     * @return 实体列表
     */
    public static <T> List<T> selectList(T t, Class<T> entityClass) {
        BaseMapper<T> mapper = getMapper(entityClass);
        // 查询所有
        return mapper.selectList(Wrappers.lambdaQuery(t));
    }

    //private static final List<String> excludeFields = Arrays.asList("password","search_value", "params", "update_by","create_by","update_time","remark");

    public static <T> List<T> selectList(T t, Class<T> entityClass, List<String> excludeFields,Map<String,Object> params) {
        BaseMapper<T> mapper = getMapper(entityClass);
        // 查询所有
        return mapper.selectList(Wrappers.query(t)
                .select(entityClass, info -> !excludeFields.contains(info.getColumn())) // 排除 searchValue);
                .between(params!=null && StringUtils.isNotEmpty((String)params.get("begin_time")),
                        params!=null?(String) params.get("column"):null,
                        params!=null?params.get("begin_time"):null,params!=null?params.get("end_time"):null));
    }


    // 查询 createTime >= startOfDay 且 createTime <= endOfDay
    //wrapper.ge("createTime", startOfDay);
    //wrapper.le("createTime", endOfDay);
    //仅内部使用，开放到API 有SQL注入风险
    public static <T> List<T> select_apply_List(T t, Class<T> entityClass, List<String> excludeFields,Map<String,Object> params) {
        if (params==null) params = new HashMap<>();
        BaseMapper<T> mapper = getMapper(entityClass);
        // 查询所有
        return mapper.selectList(Wrappers.query(t)
                .select(entityClass, info -> !excludeFields.contains(info.getColumn())) // 排除 searchValue);
                .apply(params.get("apply")!=null, (String) params.get("apply"))
                .between(Objects.nonNull(params.get("begin_time")),
                        (String) params.get("column"),
                        params.get("begin_time"),params.get("end_time")));
    }

    public static <T> List<T> selectList(T t, Class<T> entityClass, List<String> excludeFields) {
        BaseMapper<T> mapper = getMapper(entityClass);
        // 查询所有
        return mapper.selectList(Wrappers.lambdaQuery(t)
                .select(entityClass, info -> !excludeFields.contains(info.getColumn()))); // 排除 searchValue);
    }
}
