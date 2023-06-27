CREATE TABLE if not exists business_data_{appName} (
  id bigint NOT NULL COMMENT '主键',
  call_id int NOT NULL COMMENT '方法调用序号',
  version_id varchar(50) NOT NULL DEFAULT 'snapshot'  COMMENT '版本号',
  data_type varchar(30) NOT NULL COMMENT '数据类型，默认类型参考BusinessDataTypeEnum枚举类，也支持自定义类型',
  data_value text COLLATE utf8mb4_bin NOT NULL COMMENT '数据内容，JSON字符串格式',
  PRIMARY KEY (id),
  UNIQUE INDEX uni_bd_{appName}(version_id ,call_id, data_type),
  INDEX idx_bd_dt_{appName} (data_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='方法调用业务功能数据表';