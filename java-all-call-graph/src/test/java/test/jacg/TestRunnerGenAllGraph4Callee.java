package test.jacg;

import com.adrninistrator.jacg.common.enums.*;
import com.adrninistrator.jacg.conf.ConfigureWrapper;
import com.adrninistrator.jacg.runner.RunnerGenAllGraph4Callee;
import org.junit.Test;

/**
 * @author adrninistrator
 * @date 2021/6/23
 * @description: 生成向上的方法完整调用链
 */

public class TestRunnerGenAllGraph4Callee {

    public static void main(String[] args) {
        new RunnerGenAllGraph4Callee().run();
    }

    @Test
    public void getAllGraph4Callee(){
        RunnerGenAllGraph4Callee runnerGenAllGraph4Callee = new RunnerGenAllGraph4Callee();
        ConfigureWrapper configureWrapper = new ConfigureWrapper();
        configureWrapper.setMainConfig(ConfigKeyEnum.CKE_THREAD_NUM,"16");
        configureWrapper.setMainConfig(ConfigKeyEnum.CKE_APP_NAME,"i8");
        //config_db.properties
        configureWrapper.setMainConfig(ConfigDbKeyEnum.CDKE_DB_DRIVER_NAME,"com.mysql.cj.jdbc.Driver");
        configureWrapper.setMainConfig(ConfigDbKeyEnum.CDKE_DB_URL,"jdbc:mysql://192.168.8.162:3306/test_db?autoReconnect=false&useUnicode=true&characterEncoding=UTF-8&characterSetResults=UTF-8&zeroDateTimeBehavior=convertToNull&useSSL=false&rewriteBatchedStatements=true");
        configureWrapper.setMainConfig(ConfigDbKeyEnum.CDKE_DB_USERNAME,"root");
        configureWrapper.setMainConfig(ConfigDbKeyEnum.CDKE_DB_PASSWORD,"123456");
        //allow_class_prefix.properties
        configureWrapper.setOtherConfigSet(OtherConfigFileUseSetEnum.OCFUSE_ALLOWED_CLASS_PREFIX,"cn.newgrand");

        configureWrapper.setOtherConfigSet(OtherConfigFileUseSetEnum.OCFUSE_METHOD_CLASS_4CALLEE,"cn.newgrand.mspco.boq.IBoqFeign:getBoqDListByDic(java.lang.String)");
        configureWrapper.setOtherConfigSet(OtherConfigFileUseSetEnum.OCFULE_BUSINESS_DATA_TYPE_SHOW_4EE,
                DefaultBusinessDataTypeEnum.BDTE_METHOD_CALL_INFO.getType(),
                DefaultBusinessDataTypeEnum.BDTE_METHOD_ARG_GENERICS_TYPE.getType()
        );
        configureWrapper.setOtherConfigList(OtherConfigFileUseListEnum.OCFULE_EXTENSIONS_METHOD_ANNOTATION_FORMATTER,
                "com.adrninistrator.jacg.annotation.formatter.SpringMvcRequestMappingFormatter",
                "com.adrninistrator.jacg.annotation.formatter.SpringTransactionalFormatter",
                "com.adrninistrator.jacg.annotation.formatter.DefaultAnnotationFormatter");

        runnerGenAllGraph4Callee.run(configureWrapper);
    }

    /**
     * 有bug。父级的父级的调用无法识别
     * lambda调用的文本显示问题
     * 泛型参数问题
     */
    @Test
    public void getAllGraph4CalleeForJacg(){
        RunnerGenAllGraph4Callee runnerGenAllGraph4Callee = new RunnerGenAllGraph4Callee();
        ConfigureWrapper runConfig = new ConfigureWrapper();
        runConfig.setMainConfig(ConfigKeyEnum.CKE_THREAD_NUM,"16");
        runConfig.setMainConfig(ConfigKeyEnum.CKE_APP_NAME,"jacg");
        //config_db.properties
        runConfig.setMainConfig(ConfigDbKeyEnum.CDKE_DB_DRIVER_NAME,"com.mysql.cj.jdbc.Driver");
        runConfig.setMainConfig(ConfigDbKeyEnum.CDKE_DB_URL,"jdbc:mysql://192.168.8.162:3306/test_db?autoReconnect=false&useUnicode=true&characterEncoding=UTF-8&characterSetResults=UTF-8&zeroDateTimeBehavior=convertToNull&useSSL=false&rewriteBatchedStatements=true");
        runConfig.setMainConfig(ConfigDbKeyEnum.CDKE_DB_USERNAME,"root");
        runConfig.setMainConfig(ConfigDbKeyEnum.CDKE_DB_PASSWORD,"123456");
        //allow_class_prefix.properties
        runConfig.setOtherConfigSet(OtherConfigFileUseSetEnum.OCFUSE_ALLOWED_CLASS_PREFIX,"com.adrninistrator");

        runConfig.setOtherConfigSet(OtherConfigFileUseSetEnum.OCFUSE_METHOD_CLASS_4CALLEE,"com.adrninistrator.jacg.runner.RunnerGenApiGraph4Callee:doHandleOneCalleeMethod");
        runConfig.setOtherConfigSet(OtherConfigFileUseSetEnum.OCFULE_BUSINESS_DATA_TYPE_SHOW_4EE,
                DefaultBusinessDataTypeEnum.BDTE_METHOD_CALL_INFO.getType(),
                DefaultBusinessDataTypeEnum.BDTE_METHOD_ARG_GENERICS_TYPE.getType()
        );


        runConfig.setOtherConfigList(OtherConfigFileUseListEnum.OCFULE_EXTENSIONS_METHOD_ANNOTATION_FORMATTER,
                "com.adrninistrator.jacg.annotation.formatter.SpringMvcRequestMappingFormatter",
                "com.adrninistrator.jacg.annotation.formatter.SpringTransactionalFormatter",
                "com.adrninistrator.jacg.annotation.formatter.DefaultAnnotationFormatter");

        runnerGenAllGraph4Callee.run(runConfig);


    }
}
