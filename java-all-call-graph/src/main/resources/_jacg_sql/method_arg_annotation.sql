CREATE TABLE if not exists method_arg_annotation_{appName} (
  record_id int NOT NULL COMMENT '记录id',
  full_method text NOT NULL COMMENT '完整方法（类名+方法名+参数）',
  method_hash varchar(30) NOT NULL COMMENT '属性hash+字节数',
  arg_seq tinyint NOT NULL COMMENT '参数序号，从0开始',
  annotation_name varchar(500) NOT NULL COMMENT '注解类名',
  attribute_name varchar(300) NOT NULL COMMENT '注解属性名称，空字符串代表无注解属性',
  attribute_type varchar(5) NULL COMMENT '注解属性类型，s:字符串；bs:包含回车换行的字符串；m:JSON字符串，Map；ls:JSON字符串，List+String；lm:JSON字符串，List+Map',
  attribute_value text NULL COMMENT '注解属性值',
  PRIMARY KEY (record_id),
  INDEX idx_mh_{appName}(method_hash(30))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='方法参数注解信息表';