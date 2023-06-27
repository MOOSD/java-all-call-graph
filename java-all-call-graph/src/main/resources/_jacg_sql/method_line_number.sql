CREATE TABLE if not exists method_line_number_{appName} (
  id bigint NOT NULL COMMENT '主键',
  version_id varchar(50) NOT NULL DEFAULT 'snapshot'  COMMENT '版本号',
  method_hash varchar(30) NOT NULL COMMENT '方法hash+字节数',
  simple_class_name varchar(500) NOT NULL COMMENT '唯一类名',
  min_line_number int NOT NULL COMMENT '起始代码行号',
  max_line_number int NOT NULL COMMENT '结束代码行号',
  full_method text NOT NULL COMMENT '完整方法（类名+方法名+参数）',
  PRIMARY KEY (id),
  UNIQUE INDEX uni_cn_mh_{appName}(version_id , method_hash),
  INDEX idx_cn_cl_{appName}(simple_class_name(255), min_line_number, max_line_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='方法代码行号信息表';