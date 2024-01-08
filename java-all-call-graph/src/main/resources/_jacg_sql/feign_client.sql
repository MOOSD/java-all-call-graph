CREATE TABLE if not exists feign_client_{appName} (
    method_hash varchar(30) NOT NULL COMMENT '方法hash+字节数',
    seq int NOT NULL COMMENT '序号，从0开始，大于0表示此RPC接口具有复数个请求路径',
    service_name varchar(300) NOT NULL COMMENT '此RPC接口的服务名',
    context_id varchar(300) COMMENT '此RPC接口的context_id',
    show_uri varchar(1000) NOT NULL COMMENT '用于显示的URI',
    request_method varchar(100) COMMENT '此feignClient定义的请求方式',
    class_path varchar(300) COMMENT '类上的注解path属性原始值，若有重复则返回其完全限定名',
    method_path varchar(1000) COMMENT '方法上的注解path属性原始值',
    simple_class_name varchar(500) NOT NULL COMMENT '唯一类名',
    class_name varchar(500) NOT NULL COMMENT '类的完全限定名',
    annotation_name varchar(500) NOT NULL COMMENT '注解类名',
    full_method text NOT NULL COMMENT '完整方法（类名+方法名+参数）',
    PRIMARY KEY (method_hash, seq),
    INDEX inx_sc_su_{appName}(show_uri(255)),
    INDEX inx_sc_scn_{appName}(simple_class_name(255))
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='FeignClient信息表';