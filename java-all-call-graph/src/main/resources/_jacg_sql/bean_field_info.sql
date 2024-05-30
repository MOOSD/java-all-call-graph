CREATE TABLE if not exists bean_field_info_{appName} (
  field_hash varchar(30) NOT NULL COMMENT '方法hash+字节数',
  simple_class_name varchar(500) NOT NULL COMMENT '唯一类名',
  fqcn  varchar(500) NOT NULL COMMENT '完全限定类名',
  access_flags int NOT NULL COMMENT '属性的access_flags',
  field_name varchar(300) NOT NULL COMMENT '属性名',
  full_field_name text NOT NULL COMMENT '完整属性（类名+方法名+参数）',
  field_type varchar(500) NOT NULL COMMENT '属性类型',
  has_getter tinyint NOT NULL COMMENT '属性是否存在getter',
  has_setter tinyint NOT NULL COMMENT '属性是否存在setter',
  PRIMARY KEY (field_hash),
  INDEX idx_scn_fn_{appName}(simple_class_name(255), field_name(255)),
  INDEX idx_fqcn_fn_{appName}(fqcn(255), field_name(255))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='bean属性的信息表';