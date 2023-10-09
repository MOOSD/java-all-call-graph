package com.adrninistrator.jacg.precisionrunner;

import com.adrninistrator.jacg.api.CallTrees;
import com.adrninistrator.jacg.common.enums.*;
import com.adrninistrator.jacg.conf.RunConfig;
import com.adrninistrator.jacg.util.JACGJsonUtil;
import org.junit.Test;

public class GenGraphCalleePRunnerTest {

    /**
     * i8项目，向上调用链路的生成
     *
     */
    @Test
    public void getAllGraph4CalleeForI8(){
        GenGraphCalleePRunner genGraphCalleePRunner = new GenGraphCalleePRunner();
        RunConfig runConfig = new RunConfig();
        runConfig.setMainConfig(ConfigKeyEnum.CKE_APP_NAME,"i8");
        runConfig.setMainConfig(ConfigKeyEnum.APP_VERSION_ID,"0.0.0.3 version");
        runConfig.setMainConfig(ConfigKeyEnum.CKE_THREAD_NUM,"16");
        runConfig.setMainConfig(ConfigKeyEnum.CROSS_SERVICE_BY_OPENFEIGN,"true");
        //config_db.properties
        runConfig.setMainConfig(ConfigDbKeyEnum.CDKE_DB_DRIVER_NAME,"com.mysql.cj.jdbc.Driver");
        runConfig.setMainConfig(ConfigDbKeyEnum.CDKE_DB_URL,"jdbc:mysql://192.168.8.162:3306/test_db?autoReconnect=false&useUnicode=true&characterEncoding=UTF-8&characterSetResults=UTF-8&zeroDateTimeBehavior=convertToNull&useSSL=false&rewriteBatchedStatements=true");
        runConfig.setMainConfig(ConfigDbKeyEnum.CDKE_DB_USERNAME,"root");
        runConfig.setMainConfig(ConfigDbKeyEnum.CDKE_DB_PASSWORD,"123456");
        //allow_class_prefix.properties
        runConfig.setOtherConfigSet(OtherConfigFileUseSetEnum.OCFUSE_ALLOWED_CLASS_PREFIX,"cn.newgrand");

        runConfig.setOtherConfigSet(OtherConfigFileUseSetEnum.OCFUSE_METHOD_CLASS_4CALLEE,"cn.newgrand.pm.pmm.kc.service.impl.bill.KcBillHeadServiceImpl#writeoff");
        runConfig.setOtherConfigSet(OtherConfigFileUseSetEnum.OCFULE_BUSINESS_DATA_TYPE_SHOW_4EE,
                DefaultBusinessDataTypeEnum.BDTE_METHOD_CALL_INFO.getType(),
                DefaultBusinessDataTypeEnum.BDTE_METHOD_ARG_GENERICS_TYPE.getType()
        );


        runConfig.setOtherConfigList(OtherConfigFileUseListEnum.OCFULE_EXTENSIONS_METHOD_ANNOTATION_FORMATTER,
                "com.adrninistrator.jacg.annotation.formatter.SpringMvcRequestMappingFormatter",
                "com.adrninistrator.jacg.annotation.formatter.SpringTransactionalFormatter",
                "com.adrninistrator.jacg.annotation.formatter.DefaultAnnotationFormatter");


        CallTrees tree = genGraphCalleePRunner.getLink(runConfig);

        System.out.println(JACGJsonUtil.getJsonStr(tree));

    }

    @Test
    public void getAllGraph4CalleeForI8RPC(){
        GenGraphCalleePRunner genGraphCalleePRunner = new GenGraphCalleePRunner();
        RunConfig runConfig = new RunConfig();
        runConfig.setMainConfig(ConfigKeyEnum.CKE_APP_NAME,"i8");
        runConfig.setMainConfig(ConfigKeyEnum.APP_VERSION_ID,"0.0.0.3 version");
        runConfig.setMainConfig(ConfigKeyEnum.CKE_THREAD_NUM,"16");
        runConfig.setMainConfig(ConfigKeyEnum.CROSS_SERVICE_BY_OPENFEIGN,"true");
        //config_db.properties
        runConfig.setMainConfig(ConfigDbKeyEnum.CDKE_DB_DRIVER_NAME,"com.mysql.cj.jdbc.Driver");
        runConfig.setMainConfig(ConfigDbKeyEnum.CDKE_DB_URL,"jdbc:mysql://192.168.8.162:3306/test_db?autoReconnect=false&useUnicode=true&characterEncoding=UTF-8&characterSetResults=UTF-8&zeroDateTimeBehavior=convertToNull&useSSL=false&rewriteBatchedStatements=true");
        runConfig.setMainConfig(ConfigDbKeyEnum.CDKE_DB_USERNAME,"root");
        runConfig.setMainConfig(ConfigDbKeyEnum.CDKE_DB_PASSWORD,"123456");
        //allow_class_prefix.properties
        runConfig.setOtherConfigSet(OtherConfigFileUseSetEnum.OCFUSE_ALLOWED_CLASS_PREFIX,"cn.newgrand");

        runConfig.setOtherConfigSet(OtherConfigFileUseSetEnum.OCFUSE_METHOD_CLASS_4CALLEE,"cn.newgrand.pm.pmm.cg.controller.api.CgApiController:getReqPMByDicWhere");
        runConfig.setOtherConfigSet(OtherConfigFileUseSetEnum.OCFULE_BUSINESS_DATA_TYPE_SHOW_4EE,
                DefaultBusinessDataTypeEnum.BDTE_METHOD_CALL_INFO.getType(),
                DefaultBusinessDataTypeEnum.BDTE_METHOD_ARG_GENERICS_TYPE.getType()
        );


        runConfig.setOtherConfigList(OtherConfigFileUseListEnum.OCFULE_EXTENSIONS_METHOD_ANNOTATION_FORMATTER,
                "com.adrninistrator.jacg.annotation.formatter.SpringMvcRequestMappingFormatter",
                "com.adrninistrator.jacg.annotation.formatter.SpringTransactionalFormatter",
                "com.adrninistrator.jacg.annotation.formatter.DefaultAnnotationFormatter");


        CallTrees tree = genGraphCalleePRunner.getLink(runConfig);

        System.out.println(JACGJsonUtil.getJsonStr(tree));

    }


    /**
     * 灭霸项目测试类
     */
    @Test
    public void getAllGraph4CalleeForBK1(){
        GenGraphCalleePRunner runnerGenAllGraph4Callee = new GenGraphCalleePRunner();
        RunConfig runConfig = new RunConfig();
        runConfig.setMainConfig(ConfigKeyEnum.CKE_THREAD_NUM,"16");
        runConfig.setMainConfig(ConfigKeyEnum.CKE_APP_NAME,"bugkiller");
        runConfig.setMainConfig(ConfigKeyEnum.APP_VERSION_ID,"dev");
        runConfig.setMainConfig(ConfigKeyEnum.CROSS_SERVICE_BY_OPENFEIGN,"true");
        //config_db.properties
        runConfig.setMainConfig(ConfigDbKeyEnum.CDKE_DB_DRIVER_NAME,"com.mysql.cj.jdbc.Driver");
        runConfig.setMainConfig(ConfigDbKeyEnum.CDKE_DB_URL,"jdbc:mysql://192.168.8.162:3306/precision_dev?autoReconnect=false&useUnicode=true&characterEncoding=UTF-8&characterSetResults=UTF-8&zeroDateTimeBehavior=convertToNull&useSSL=false&rewriteBatchedStatements=true");
        runConfig.setMainConfig(ConfigDbKeyEnum.CDKE_DB_USERNAME,"root");
        runConfig.setMainConfig(ConfigDbKeyEnum.CDKE_DB_PASSWORD,"123456");
        //allow_class_prefix.properties
//        runConfig.setOtherConfigSet(OtherConfigFileUseSetEnum.OCFUSE_ALLOWED_CLASS_PREFIX,"cn.newgrand");

        runConfig.setOtherConfigSet(OtherConfigFileUseSetEnum.OCFUSE_METHOD_CLASS_4CALLEE,
                "io.metersphere.api.dto.definition.parse.Swagger3Parser:966",
                "io.metersphere.controller.ProjectFeignController:25",
                "io.metersphere.api.controller.ApiDefinitionController:87",
                "io.metersphere.config.TaskSchedulerConfig:12");



        runConfig.setOtherConfigList(OtherConfigFileUseListEnum.OCFULE_EXTENSIONS_METHOD_ANNOTATION_FORMATTER,
                "com.adrninistrator.jacg.annotation.formatter.SpringMvcRequestMappingFormatter",
                "com.adrninistrator.jacg.annotation.formatter.SpringTransactionalFormatter",
                "com.adrninistrator.jacg.annotation.formatter.DefaultAnnotationFormatter");


        CallTrees tree = runnerGenAllGraph4Callee.getLink(runConfig);

        System.out.println(JACGJsonUtil.getJsonStr(tree));

    }
}