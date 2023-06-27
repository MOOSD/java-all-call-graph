CREATE TABLE if not exists extends_impl_{appName} (
  id bigint NOT NULL COMMENT '主键',
  record_id int NOT NULL COMMENT '记录id',
  version_id varchar(50) NOT NULL DEFAULT 'snapshot'  COMMENT '版本号',
  simple_class_name varchar(500) NOT NULL COMMENT '唯一类名',
  class_name varchar(500) NOT NULL COMMENT '完整类名',
  access_flags int NOT NULL COMMENT '类的access_flags',
  type char(1) NOT NULL COMMENT '类型，e:继承，i:实现',
  seq int NOT NULL COMMENT '序号，从0开始，支持实现多个接口',
  exists_downward_classes tinyint NOT NULL COMMENT '是否存在子类或子接口，0:不存在；1:存在',
  upward_simple_class_name varchar(500) NOT NULL COMMENT '父类或接口的唯一类名',
  upward_class_name varchar(500) NOT NULL COMMENT '父类或接口的完整类名',
  PRIMARY KEY (id),
  INDEX idx_ei_scn_{appName}(simple_class_name(255)),
  INDEX idx_ei_uscn_{appName}(upward_simple_class_name(255))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='继承与实现相关信息表';