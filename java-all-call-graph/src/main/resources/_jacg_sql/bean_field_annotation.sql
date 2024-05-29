CREATE TABLE if not exists bean_field_annotation_{appName} (
  record_id int NOT NULL COMMENT '记录id',
  simple_class_name varchar(500) NOT NULL COMMENT '唯一类名',
  field_hash varchar(30) NOT NULL COMMENT '属性hash+字节数',
  annotation_name varchar(500) NOT NULL COMMENT '注解类名',
  attribute_name varchar(300) NOT NULL COMMENT '注解属性名称，空字符串代表无注解属性',
  attribute_type varchar(5) NULL COMMENT '注解属性类型，s:字符串；bs:包含回车换行的字符串；m:JSON字符串，Map；ls:JSON字符串，List+String；lm:JSON字符串，List+Map',
  attribute_value text NULL COMMENT '注解属性值',
  full_field_name text NOT NULL COMMENT '完整方法（类名+属性名+参数）',

  PRIMARY KEY (record_id),
  INDEX idx_ma_1_{appName}(field_hash, annotation_name(255)),
  INDEX idx_ma_an_{appName}(annotation_name(255)),
  INDEX idx_ma_scn_{appName}(simple_class_name(255))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='属性上的注解信息表';