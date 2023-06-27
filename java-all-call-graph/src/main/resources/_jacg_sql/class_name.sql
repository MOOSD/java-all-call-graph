CREATE TABLE if not exists class_name_{appName} (
  id bigint NOT NULL COMMENT '主键',
  record_id int NOT NULL COMMENT '记录id',
  version_id varchar(50) NOT NULL DEFAULT 'snapshot'  COMMENT '版本号',
  class_name varchar(500) NOT NULL COMMENT '完整类名',
  simple_class_name varchar(500) NOT NULL COMMENT '唯一类名',
  duplicate_class tinyint NOT NULL COMMENT '是否存在同名类，1:是，0:否',
  PRIMARY KEY (id),
  INDEX idx_cn_cn_{appName}(class_name(255)),
  INDEX idx_cn_scn_{appName}(simple_class_name(255)),
  INDEX idx_cn_dc_{appName}(duplicate_class)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='类名信息表';