-- 一键补偿系统相关表结构

-- ----------------------------
-- 补偿策略配置表
-- ----------------------------
DROP TABLE IF EXISTS `t_compensation_strategy`;
CREATE TABLE `t_compensation_strategy` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `strategy_name` varchar(100) NOT NULL COMMENT '策略名称',
  `strategy_desc` varchar(500) DEFAULT NULL COMMENT '策略描述',
  `compensation_rate` decimal(5,4) NOT NULL COMMENT '补偿比例(0-1)',
  `min_bet_amount` decimal(20,6) DEFAULT 0 COMMENT '最小投注金额限制',
  `max_bet_amount` decimal(20,6) DEFAULT NULL COMMENT '最大投注金额限制',
  `target_symbol` varchar(50) DEFAULT NULL COMMENT '目标交易对(为空表示全部)',
  `target_period` int DEFAULT NULL COMMENT '目标周期(秒,为空表示全部)',
  `status` int DEFAULT 1 COMMENT '状态(1:启用 0:禁用)',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_symbol_period` (`target_symbol`, `target_period`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='补偿策略配置表';

-- ----------------------------
-- 补偿批次表
-- ----------------------------
DROP TABLE IF EXISTS `t_compensation_batch`;
CREATE TABLE `t_compensation_batch` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `batch_no` varchar(50) NOT NULL COMMENT '批次号',
  `batch_name` varchar(200) NOT NULL COMMENT '批次名称',
  `strategy_id` bigint NOT NULL COMMENT '使用的策略ID',
  `search_conditions` text COMMENT '搜索条件(JSON格式)',
  `total_orders` int DEFAULT 0 COMMENT '符合条件的订单总数',
  `total_users` int DEFAULT 0 COMMENT '涉及用户总数',
  `total_bet_amount` decimal(30,6) DEFAULT 0 COMMENT '总投注金额',
  `total_compensation_amount` decimal(30,6) DEFAULT 0 COMMENT '总补偿金额',
  `compensation_rate` decimal(5,4) NOT NULL COMMENT '实际使用的补偿比例',
  `status` int DEFAULT 0 COMMENT '状态(0:待执行 1:执行中 2:已完成 3:已取消)',
  `execute_time` datetime DEFAULT NULL COMMENT '执行时间',
  `complete_time` datetime DEFAULT NULL COMMENT '完成时间',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_batch_no` (`batch_no`),
  KEY `idx_strategy_id` (`strategy_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='补偿批次表';

-- ----------------------------
-- 补偿详细记录表
-- ----------------------------
DROP TABLE IF EXISTS `t_compensation_detail`;
CREATE TABLE `t_compensation_detail` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `batch_id` bigint NOT NULL COMMENT '批次ID',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `user_address` varchar(255) DEFAULT NULL COMMENT '用户地址',
  `order_no` varchar(255) NOT NULL COMMENT '订单号',
  `symbol` varchar(50) NOT NULL COMMENT '交易对',
  `bet_amount` decimal(20,6) NOT NULL COMMENT '投注金额',
  `compensation_amount` decimal(20,6) NOT NULL COMMENT '补偿金额',
  `compensation_rate` decimal(5,4) NOT NULL COMMENT '补偿比例',
  `status` int DEFAULT 0 COMMENT '补偿状态(0:待补偿 1:已补偿 2:补偿失败)',
  `compensation_time` datetime DEFAULT NULL COMMENT '补偿时间',
  `wallet_record_id` bigint DEFAULT NULL COMMENT '钱包记录ID',
  `error_msg` varchar(500) DEFAULT NULL COMMENT '错误信息',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_batch_id` (`batch_id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='补偿详细记录表';

-- ----------------------------
-- 插入默认补偿策略
-- ----------------------------
INSERT INTO `t_compensation_strategy` (`strategy_name`, `strategy_desc`, `compensation_rate`, `min_bet_amount`, `max_bet_amount`, `status`, `create_by`, `remark`) 
VALUES 
('标准补偿策略', '按投注本金的固定比例进行补偿', 0.5000, 1.000000, NULL, 1, 'system', '标准50%补偿策略'),
('高额补偿策略', '针对大额投注的高比例补偿', 0.8000, 100.000000, NULL, 1, 'system', '大额投注80%补偿策略'),
('小额补偿策略', '针对小额投注的基础补偿', 0.3000, 0.000001, 50.000000, 1, 'system', '小额投注30%补偿策略');
