CREATE TABLE if not exists spring_task_{appName} (
  id bigint NOT NULL COMMENT '主键',
  record_id int NOT NULL COMMENT '记录id',
  version_id varchar(50) NOT NULL DEFAULT 'snapshot'  COMMENT '版本号',
  spring_bean_name varchar(500) NOT NULL COMMENT 'Spring Bean的名称',
  class_name varchar(500) NOT NULL COMMENT '完整类名',
  method_name varchar(300) NOT NULL COMMENT '方法名',
  PRIMARY KEY (id),
  INDEX inx_spt_sbn_{appName}(spring_bean_name(255)),
  INDEX inx_spt_cn_{appName}(class_name(255))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='Spring定时任务信息表';