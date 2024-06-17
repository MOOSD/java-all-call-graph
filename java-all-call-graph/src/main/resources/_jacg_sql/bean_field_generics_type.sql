CREATE TABLE if not exists bean_field_generics_type_{appName} (
  record_id int NOT NULL COMMENT '记录id',
  field_hash varchar(30) NOT NULL COMMENT '属性hash+字节数',
  simple_class_name varchar(500) NOT NULL COMMENT '唯一类名',
  type varchar(5)  NOT NULL COMMENT '类型，t:参数类型，gt:参数泛型类型',
  generics_path varchar(30) NOT NULL COMMENT '泛型路径，用于描述泛型的层级关系',
  simple_generics_type varchar(500) NOT NULL COMMENT '泛型类型或参数类型唯一类名',
  generics_type varchar(500) NOT NULL COMMENT '泛型类型或参数类型类名',
  full_field_name text NOT NULL COMMENT '完整方法（类名+方法名+参数）',
  PRIMARY KEY (record_id),
  INDEX idx_magt_fh_{appName}(field_hash),
  INDEX idx_magt_scn_{appName}(simple_class_name(255)),
  INDEX idx_magt_sgt_{appName}(simple_generics_type(255))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='bean属性参数泛型类型';