CREATE TABLE if not exists class_info_{appName} (
  id bigint NOT NULL COMMENT '主键',
  record_id int NOT NULL COMMENT '记录id',
  version_id varchar(50) NOT NULL DEFAULT 'snapshot'  COMMENT '版本号',
  simple_class_name varchar(500) NOT NULL COMMENT '唯一类名',
  access_flags int NOT NULL COMMENT '类的access_flags',
  class_name varchar(500) NOT NULL COMMENT '完整类名',
  PRIMARY KEY (id),
  INDEX idx_ci_scn_{appName} (simple_class_name(255))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='类的信息表';