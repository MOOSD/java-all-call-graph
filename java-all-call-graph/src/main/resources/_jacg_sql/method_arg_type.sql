CREATE TABLE if not exists method_arg_type_{appName} (
  id bigint NOT NULL COMMENT '主键',
  version_id varchar(50) NOT NULL DEFAULT 'snapshot'  COMMENT '版本号',
  method_hash varchar(30) NOT NULL COMMENT '方法hash+字节数',
  arg_seq tinyint NOT NULL COMMENT '参数序号，从0开始',
  simple_arg_type varchar(500) NOT NULL COMMENT '参数类型唯一类名',
  arg_type varchar(500) NOT NULL COMMENT '参数类型类名',
  simple_class_name varchar(500) NOT NULL COMMENT '唯一类名',
  full_method text NOT NULL COMMENT '完整方法（类名+方法名+参数）',
  PRIMARY KEY (id),
  UNIQUE INDEX uni_mat_{appName}(version_id,method_hash, arg_seq),
  INDEX idx_mat_sat_{appName}(simple_arg_type(255)),
  INDEX idx_mat_scn_{appName}(simple_class_name(255))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='方法参数类型';