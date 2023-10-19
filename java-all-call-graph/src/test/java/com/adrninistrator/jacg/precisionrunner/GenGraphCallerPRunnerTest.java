package com.adrninistrator.jacg.precisionrunner;

import com.adrninistrator.jacg.api.CallTrees;
import com.adrninistrator.jacg.api.CallerNode;
import com.adrninistrator.jacg.common.enums.*;
import com.adrninistrator.jacg.conf.ConfigureWrapper;
import com.adrninistrator.jacg.util.JACGJsonUtil;
import org.junit.Test;

public class GenGraphCallerPRunnerTest {

    @Test
    public void getLinkBaseTest() {
        GenGraphCallerPRunner runnerGenAllGraph4Caller = new GenGraphCallerPRunner();
        ConfigureWrapper configureWrapper = new ConfigureWrapper();
        configureWrapper.setMainConfig(ConfigKeyEnum.CKE_THREAD_NUM,"16");
        configureWrapper.setMainConfig(ConfigKeyEnum.CKE_APP_NAME,"i8");
        configureWrapper.setMainConfig(ConfigKeyEnum.APP_VERSION_ID,"dev");
        configureWrapper.setMainConfig(ConfigKeyEnum.CROSS_SERVICE_BY_OPENFEIGN,"false");

        //config_db.properties
        configureWrapper.setMainConfig(ConfigDbKeyEnum.CDKE_DB_DRIVER_NAME,"com.mysql.cj.jdbc.Driver");
        configureWrapper.setMainConfig(ConfigDbKeyEnum.CDKE_DB_URL,"jdbc:mysql://192.168.8.162:3306/precision_dev?autoReconnect=false&useUnicode=true&characterEncoding=UTF-8&characterSetResults=UTF-8&zeroDateTimeBehavior=convertToNull&useSSL=false&rewriteBatchedStatements=true");
        configureWrapper.setMainConfig(ConfigDbKeyEnum.CDKE_DB_USERNAME,"root");
        configureWrapper.setMainConfig(ConfigDbKeyEnum.CDKE_DB_PASSWORD,"123456");
        //allow_class_prefix.properties
        configureWrapper.setOtherConfigSet(OtherConfigFileUseSetEnum.OCFUSE_ALLOWED_CLASS_PREFIX,"cn.newgrand");

        configureWrapper.setOtherConfigSet(OtherConfigFileUseSetEnum.OCFUSE_METHOD_CLASS_4CALLER,"cn.newgrand.pm.pmm.kc.service.impl.bill.KcBillHeadServiceImpl#writeoff");
        configureWrapper.setOtherConfigSet(OtherConfigFileUseSetEnum.OCFULE_BUSINESS_DATA_TYPE_SHOW_4ER,
                DefaultBusinessDataTypeEnum.BDTE_METHOD_CALL_INFO.getType(),
                DefaultBusinessDataTypeEnum.BDTE_METHOD_ARG_GENERICS_TYPE.getType(),
                DefaultBusinessDataTypeEnum.BDTE_NG_JAP_OPERATE_INFO.getType()
        );

        configureWrapper.setMainConfig(ConfigKeyEnum.CKE_IGNORE_DUP_CALLEE_IN_ONE_CALLER,"true");
        configureWrapper.setOtherConfigList(OtherConfigFileUseListEnum.OCFULE_EXTENSIONS_METHOD_ANNOTATION_FORMATTER,
                "com.adrninistrator.jacg.annotation.formatter.SpringMvcRequestMappingFormatter",
                "com.adrninistrator.jacg.annotation.formatter.SpringTransactionalFormatter",
                "com.adrninistrator.jacg.annotation.formatter.DefaultAnnotationFormatter");

        CallTrees<CallerNode> tree = runnerGenAllGraph4Caller.getLink(configureWrapper);
        System.out.println(JACGJsonUtil.getJsonStr(tree));

    }
    @Test
    public void getLinkForRepeat(){
        GenGraphCallerPRunner runnerGenAllGraph4Caller = new GenGraphCallerPRunner();
        ConfigureWrapper configureWrapper = new ConfigureWrapper();
        configureWrapper.setMainConfig(ConfigKeyEnum.CKE_THREAD_NUM,"16");
        configureWrapper.setMainConfig(ConfigKeyEnum.CKE_APP_NAME,"i8");
        configureWrapper.setMainConfig(ConfigKeyEnum.APP_VERSION_ID,"0.0.0.3 version");
        configureWrapper.setMainConfig(ConfigKeyEnum.CROSS_SERVICE_BY_OPENFEIGN,"true");
        configureWrapper.setMainConfig(ConfigKeyEnum.CKE_IGNORE_DUP_CALLEE_IN_ONE_CALLER,"true");
        //config_db.properties
        configureWrapper.setMainConfig(ConfigDbKeyEnum.CDKE_DB_DRIVER_NAME,"com.mysql.cj.jdbc.Driver");
        configureWrapper.setMainConfig(ConfigDbKeyEnum.CDKE_DB_URL,"jdbc:mysql://192.168.8.162:3306/test_db?autoReconnect=false&useUnicode=true&characterEncoding=UTF-8&characterSetResults=UTF-8&zeroDateTimeBehavior=convertToNull&useSSL=false&rewriteBatchedStatements=true");
        configureWrapper.setMainConfig(ConfigDbKeyEnum.CDKE_DB_USERNAME,"root");
        configureWrapper.setMainConfig(ConfigDbKeyEnum.CDKE_DB_PASSWORD,"123456");
        //allow_class_prefix.properties
        configureWrapper.setOtherConfigSet(OtherConfigFileUseSetEnum.OCFUSE_ALLOWED_CLASS_PREFIX,"cn.newgrand");

        configureWrapper.setOtherConfigSet(OtherConfigFileUseSetEnum.OCFUSE_METHOD_CLASS_4CALLER,"cn.newgrand.pm.crm.zb.service.impl.TendReceiptServiceImpl:sendUIC");
        configureWrapper.setOtherConfigSet(OtherConfigFileUseSetEnum.OCFULE_BUSINESS_DATA_TYPE_SHOW_4ER,
                DefaultBusinessDataTypeEnum.BDTE_METHOD_CALL_INFO.getType(),
                DefaultBusinessDataTypeEnum.BDTE_METHOD_ARG_GENERICS_TYPE.getType()
        );
        configureWrapper.setOtherConfigList(OtherConfigFileUseListEnum.OCFULE_EXTENSIONS_METHOD_ANNOTATION_FORMATTER,
                "com.adrninistrator.jacg.annotation.formatter.SpringMvcRequestMappingFormatter",
                "com.adrninistrator.jacg.annotation.formatter.SpringTransactionalFormatter",
                "com.adrninistrator.jacg.annotation.formatter.DefaultAnnotationFormatter");

        CallTrees<CallerNode> tree = runnerGenAllGraph4Caller.getLink(configureWrapper);
        System.out.println(JACGJsonUtil.getJsonStr(tree));
    }

    @Test
    public void getLinkForRPC() {
        GenGraphCallerPRunner runnerGenAllGraph4Caller = new GenGraphCallerPRunner();
        ConfigureWrapper configureWrapper = new ConfigureWrapper();
        configureWrapper.setMainConfig(ConfigKeyEnum.CKE_THREAD_NUM,"16");
        configureWrapper.setMainConfig(ConfigKeyEnum.CKE_APP_NAME,"i8");
        configureWrapper.setMainConfig(ConfigKeyEnum.APP_VERSION_ID,"0.0.0.3 version");
        configureWrapper.setMainConfig(ConfigKeyEnum.CROSS_SERVICE_BY_OPENFEIGN,"true");
        configureWrapper.setMainConfig(ConfigKeyEnum.CKE_IGNORE_DUP_CALLEE_IN_ONE_CALLER,"false");

        //config_db.properties
        configureWrapper.setMainConfig(ConfigDbKeyEnum.CDKE_DB_DRIVER_NAME,"com.mysql.cj.jdbc.Driver");
        configureWrapper.setMainConfig(ConfigDbKeyEnum.CDKE_DB_URL,"jdbc:mysql://192.168.8.162:3306/test_db?autoReconnect=false&useUnicode=true&characterEncoding=UTF-8&characterSetResults=UTF-8&zeroDateTimeBehavior=convertToNull&useSSL=false&rewriteBatchedStatements=true");
        configureWrapper.setMainConfig(ConfigDbKeyEnum.CDKE_DB_USERNAME,"root");
        configureWrapper.setMainConfig(ConfigDbKeyEnum.CDKE_DB_PASSWORD,"123456");
        //allow_class_prefix.properties
        configureWrapper.setOtherConfigSet(OtherConfigFileUseSetEnum.OCFUSE_ALLOWED_CLASS_PREFIX,"cn.newgrand");

        configureWrapper.setOtherConfigSet(OtherConfigFileUseSetEnum.OCFUSE_METHOD_CLASS_4CALLER,"cn.newgrand.pm.crm.zb.service.impl.TendReceiptServiceImpl:sendUIC");
        configureWrapper.setOtherConfigSet(OtherConfigFileUseSetEnum.OCFULE_BUSINESS_DATA_TYPE_SHOW_4ER,
                DefaultBusinessDataTypeEnum.BDTE_METHOD_CALL_INFO.getType(),
                DefaultBusinessDataTypeEnum.BDTE_METHOD_ARG_GENERICS_TYPE.getType()
        );
        configureWrapper.setOtherConfigList(OtherConfigFileUseListEnum.OCFULE_EXTENSIONS_METHOD_ANNOTATION_FORMATTER,
                "com.adrninistrator.jacg.annotation.formatter.SpringMvcRequestMappingFormatter",
                "com.adrninistrator.jacg.annotation.formatter.SpringTransactionalFormatter",
                "com.adrninistrator.jacg.annotation.formatter.DefaultAnnotationFormatter");

        CallTrees<CallerNode> tree = runnerGenAllGraph4Caller.getLink(configureWrapper);
        System.out.println(JACGJsonUtil.getJsonStr(tree));

    }

    @Test
    public void getLinkForIgnore(){
        GenGraphCallerPRunner runnerGenAllGraph4Caller = new GenGraphCallerPRunner();
        ConfigureWrapper configureWrapper = new ConfigureWrapper();
        configureWrapper.setMainConfig(ConfigKeyEnum.CKE_THREAD_NUM,"16");
        configureWrapper.setMainConfig(ConfigKeyEnum.CKE_APP_NAME,"i8");
        configureWrapper.setMainConfig(ConfigKeyEnum.APP_VERSION_ID,"0.0.0.3 version");
        configureWrapper.setMainConfig(ConfigKeyEnum.CROSS_SERVICE_BY_OPENFEIGN,"false");
        configureWrapper.setMainConfig(ConfigKeyEnum.CKE_IGNORE_DUP_CALLEE_IN_ONE_CALLER,"false");
        //config_db.properties
        configureWrapper.setMainConfig(ConfigDbKeyEnum.CDKE_DB_DRIVER_NAME,"com.mysql.cj.jdbc.Driver");
        configureWrapper.setMainConfig(ConfigDbKeyEnum.CDKE_DB_URL,"jdbc:mysql://192.168.8.162:3306/test_db?autoReconnect=false&useUnicode=true&characterEncoding=UTF-8&characterSetResults=UTF-8&zeroDateTimeBehavior=convertToNull&useSSL=false&rewriteBatchedStatements=true");
        configureWrapper.setMainConfig(ConfigDbKeyEnum.CDKE_DB_USERNAME,"root");
        configureWrapper.setMainConfig(ConfigDbKeyEnum.CDKE_DB_PASSWORD,"123456");
        //allow_class_prefix.properties
        configureWrapper.setOtherConfigSet(OtherConfigFileUseSetEnum.OCFUSE_ALLOWED_CLASS_PREFIX,"cn.newgrand");

        configureWrapper.setOtherConfigSet(OtherConfigFileUseSetEnum.OCFUSE_METHOD_CLASS_4CALLER,"cn.newgrand.pm.pmm.cg.controller.api.CgApiController:getMatPMCollectByPhid");
        configureWrapper.setOtherConfigSet(OtherConfigFileUseSetEnum.OCFUSE_IGNORE_CLASS_KEYWORD,"ApiResultUtil");
        configureWrapper.setOtherConfigSet(OtherConfigFileUseSetEnum.OCFULE_BUSINESS_DATA_TYPE_SHOW_4ER,
                DefaultBusinessDataTypeEnum.BDTE_METHOD_CALL_INFO.getType(),
                DefaultBusinessDataTypeEnum.BDTE_METHOD_ARG_GENERICS_TYPE.getType()
        );
        configureWrapper.setOtherConfigList(OtherConfigFileUseListEnum.OCFULE_EXTENSIONS_METHOD_ANNOTATION_FORMATTER,
                "com.adrninistrator.jacg.annotation.formatter.SpringMvcRequestMappingFormatter",
                "com.adrninistrator.jacg.annotation.formatter.SpringTransactionalFormatter",
                "com.adrninistrator.jacg.annotation.formatter.DefaultAnnotationFormatter");

        CallTrees<CallerNode> tree = runnerGenAllGraph4Caller.getLink(configureWrapper);
        System.out.println(JACGJsonUtil.getJsonStr(tree));
    }


    @Test
    public void getLinkForRPCTest2(){
        GenGraphCallerPRunner runnerGenAllGraph4Caller = new GenGraphCallerPRunner();
        ConfigureWrapper configureWrapper = new ConfigureWrapper();
        configureWrapper.setMainConfig(ConfigKeyEnum.CKE_THREAD_NUM,"16");
        configureWrapper.setMainConfig(ConfigKeyEnum.CKE_APP_NAME,"i8");
        configureWrapper.setMainConfig(ConfigKeyEnum.APP_VERSION_ID,"0.0.0.3 version");
        configureWrapper.setMainConfig(ConfigKeyEnum.CROSS_SERVICE_BY_OPENFEIGN,"true");
        configureWrapper.setMainConfig(ConfigKeyEnum.CKE_IGNORE_DUP_CALLEE_IN_ONE_CALLER,"false");
        //config_db.properties
        configureWrapper.setMainConfig(ConfigDbKeyEnum.CDKE_DB_DRIVER_NAME,"com.mysql.cj.jdbc.Driver");
        configureWrapper.setMainConfig(ConfigDbKeyEnum.CDKE_DB_URL,"jdbc:mysql://192.168.8.162:3306/test_db?autoReconnect=false&useUnicode=true&characterEncoding=UTF-8&characterSetResults=UTF-8&zeroDateTimeBehavior=convertToNull&useSSL=false&rewriteBatchedStatements=true");
        configureWrapper.setMainConfig(ConfigDbKeyEnum.CDKE_DB_USERNAME,"root");
        configureWrapper.setMainConfig(ConfigDbKeyEnum.CDKE_DB_PASSWORD,"123456");
        //allow_class_prefix.properties
        configureWrapper.setOtherConfigSet(OtherConfigFileUseSetEnum.OCFUSE_ALLOWED_CLASS_PREFIX,"cn.newgrand");

        configureWrapper.setOtherConfigSet(OtherConfigFileUseSetEnum.OCFUSE_METHOD_CLASS_4CALLER,"cn.newgrand.pm.crm.zb.service.impl.CntAwardNoticeServiceImpl:saveCntAwardNotice(cn.newgrand.pm.crm.zb.domain.CntAwardNoticeModel,java.util.List,java.lang.String,boolean)");
        configureWrapper.setOtherConfigSet(OtherConfigFileUseSetEnum.OCFUSE_IGNORE_CLASS_KEYWORD,"ApiResultUtil");
        configureWrapper.setOtherConfigSet(OtherConfigFileUseSetEnum.OCFULE_BUSINESS_DATA_TYPE_SHOW_4ER,
                DefaultBusinessDataTypeEnum.BDTE_METHOD_CALL_INFO.getType(),
                DefaultBusinessDataTypeEnum.BDTE_METHOD_ARG_GENERICS_TYPE.getType()
        );
        configureWrapper.setOtherConfigList(OtherConfigFileUseListEnum.OCFULE_EXTENSIONS_METHOD_ANNOTATION_FORMATTER,
                "com.adrninistrator.jacg.annotation.formatter.SpringMvcRequestMappingFormatter",
                "com.adrninistrator.jacg.annotation.formatter.SpringTransactionalFormatter",
                "com.adrninistrator.jacg.annotation.formatter.DefaultAnnotationFormatter");

        CallTrees<CallerNode> tree = runnerGenAllGraph4Caller.getLink(configureWrapper);
        System.out.println(JACGJsonUtil.getJsonStr(tree));
    }
}