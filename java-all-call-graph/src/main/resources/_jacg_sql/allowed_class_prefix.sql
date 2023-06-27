CREATE TABLE if not exists allowed_class_prefix_{appName} (
  id bigint NOT NULL COMMENT '主键',
  record_id int NOT NULL COMMENT '记录id',
  version_id varchar(50) NOT NULL DEFAULT 'snapshot'  COMMENT '版本号',
  class_prefix varchar(500) NOT NULL COMMENT '类名或包名前缀',
  PRIMARY KEY (id),
  INDEX idx_acp_cp_{appName} (class_prefix(255))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='允许处理的类名或包名前缀';