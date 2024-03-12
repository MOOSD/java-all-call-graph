CREATE TABLE if not exists spring_controller_{appName} (
  seq int NOT NULL AUTO_INCREMENT COMMENT '主键',
  method_hash varchar(30) NOT NULL COMMENT '方法hash+字节数',
  show_uri varchar(1000) NOT NULL COMMENT '用于显示的URI',
  request_method varchar(100) COMMENT '此controller支持的请求方式',
  class_path varchar(300) COMMENT '类上的注解path属性原始值',
  method_path varchar(1000)  COMMENT '方法上的注解path属性原始值',
  annotation_name varchar(500) NOT NULL COMMENT '注解类名',
  simple_class_name varchar(500) NOT NULL COMMENT '唯一类名',
  full_method text NOT NULL COMMENT '完整方法（类名+方法名+参数）',
  PRIMARY KEY (seq),
  INDEX inx_spc_mh_{appName}(method_hash),
  INDEX inx_spc_su_{appName}(show_uri(255),request_method),
  INDEX inx_spc_scn_{appName}(simple_class_name(255))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='Spring Bean信息表';