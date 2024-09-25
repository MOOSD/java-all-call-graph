package com.adrninistrator.jacg.precisionrunner;

import com.adrninistrator.jacg.common.enums.ConfigDbKeyEnum;
import com.adrninistrator.jacg.common.enums.ConfigKeyEnum;
import com.adrninistrator.jacg.common.enums.OtherConfigFileUseListEnum;
import com.adrninistrator.jacg.common.enums.OtherConfigFileUseSetEnum;
import com.adrninistrator.jacg.conf.ConfigureWrapper;
import com.adrninistrator.jacg.dto.write_db.WriteDBResult;
import com.adrninistrator.jacg.util.JSON;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;

public class DBTest {
    @Test
    public void domainPrecisionTest(){
        ConfigureWrapper configureWrapper = new ConfigureWrapper();
        // 主配置文件信息 config.properties
        configureWrapper.setMainConfig(ConfigKeyEnum.CKE_APP_NAME,"testplatform");
        configureWrapper.setMainConfig(ConfigKeyEnum.DOMAIN_CODE,"precision");
        configureWrapper.setMainConfig(ConfigKeyEnum.APP_VERSION_ID,"dev");
        configureWrapper.setMainConfig(ConfigKeyEnum.CKE_THREAD_NUM,"16");
        configureWrapper.setMainConfig(ConfigKeyEnum.CKE_DB_INSERT_BATCH_SIZE,"1000");
        // 设置所有业务域信息
        HashSet<String> configSet = new HashSet<>();
        configSet.add("precision");
        configSet.add("bugkiller");
        configureWrapper.setOtherConfigSet(OtherConfigFileUseSetEnum.OCFULE_PROJECT_DOMAINS, configSet);


        // 写入数据库信息 config_db.propertis
        configureWrapper.setMainConfig(ConfigDbKeyEnum.CDKE_DB_DRIVER_NAME,"com.mysql.cj.jdbc.Driver");
        configureWrapper.setMainConfig(ConfigDbKeyEnum.CDKE_DB_URL,"jdbc:mysql://192.168.8.162:3306/test_db?autoReconnect=false&connectTimeout=60000&socketTimeout=60000&useUnicode=true&characterEncoding=UTF-8&characterSetResults=UTF-8&zeroDateTimeBehavior=convertToNull&useSSL=false&rewriteBatchedStatements=true");
        configureWrapper.setMainConfig(ConfigDbKeyEnum.CDKE_DB_USERNAME,"root");
        configureWrapper.setMainConfig(ConfigDbKeyEnum.CDKE_DB_PASSWORD,"123456");


        //设置jar扫描路径
        ArrayList<String> otherConfigList = new ArrayList<>();
        otherConfigList.add("D:\\work\\jar\\testplatform\\precision\\dev");

        configureWrapper.setOtherConfigList(OtherConfigFileUseListEnum.OCFULE_JAR_DIR,otherConfigList);
        //allow
        HashSet<String> otherConfigSet= new HashSet<>();
        otherConfigSet.add("cn.newgrand");
        configureWrapper.setOtherConfigSet(OtherConfigFileUseSetEnum.OCFUSE_ALLOWED_CLASS_PREFIX, otherConfigSet);


        // 启动
        WriteDBResult writeDBResult = new WriteDbPRunner().runWithResult(configureWrapper, null);
        System.out.println(JSON.toJSONString(writeDBResult));
    }


    @Test
    public void domainBugKillTest(){
        ConfigureWrapper configureWrapper = new ConfigureWrapper();
        // 主配置文件信息 config.properties
        configureWrapper.setMainConfig(ConfigKeyEnum.CKE_APP_NAME,"testplatform");
        configureWrapper.setMainConfig(ConfigKeyEnum.DOMAIN_CODE,"bugkiller");
        configureWrapper.setMainConfig(ConfigKeyEnum.APP_VERSION_ID,"dev");
        configureWrapper.setMainConfig(ConfigKeyEnum.CKE_THREAD_NUM,"16");
        configureWrapper.setMainConfig(ConfigKeyEnum.CKE_DB_INSERT_BATCH_SIZE,"1000");
        // 设置所有业务域信息
        HashSet<String> configSet = new HashSet<>();
        configSet.add("precision");
        configSet.add("bugkiller");
        configureWrapper.setOtherConfigSet(OtherConfigFileUseSetEnum.OCFULE_PROJECT_DOMAINS, configSet);


        // 写入数据库信息 config_db.propertis
        configureWrapper.setMainConfig(ConfigDbKeyEnum.CDKE_DB_DRIVER_NAME,"com.mysql.cj.jdbc.Driver");
        configureWrapper.setMainConfig(ConfigDbKeyEnum.CDKE_DB_URL,"jdbc:mysql://192.168.8.162:3306/test_db?autoReconnect=false&connectTimeout=60000&socketTimeout=60000&useUnicode=true&characterEncoding=UTF-8&characterSetResults=UTF-8&zeroDateTimeBehavior=convertToNull&useSSL=false&rewriteBatchedStatements=true");
        configureWrapper.setMainConfig(ConfigDbKeyEnum.CDKE_DB_USERNAME,"root");
        configureWrapper.setMainConfig(ConfigDbKeyEnum.CDKE_DB_PASSWORD,"123456");


        //设置jar扫描路径
        ArrayList<String> otherConfigList = new ArrayList<>();
        otherConfigList.add("D:\\work\\jar\\testplatform\\bugkiller\\dev");
        configureWrapper.setOtherConfigList(OtherConfigFileUseListEnum.OCFULE_JAR_DIR,otherConfigList);
        // 允许的类前缀
        HashSet<String> otherConfigSet= new HashSet<>();
        otherConfigSet.add("cn.newgrand");
        otherConfigSet.add("io.metersphere");
        configureWrapper.setOtherConfigSet(OtherConfigFileUseSetEnum.OCFUSE_ALLOWED_CLASS_PREFIX, otherConfigSet);

        // 启动
        WriteDBResult writeDBResult = new WriteDbPRunner().runWithResult(configureWrapper, null);
        System.out.println(JSON.toJSONString(writeDBResult));


    }


    @Test
    public void mysqlTest(){
        ConfigureWrapper configureWrapper = new ConfigureWrapper();
        //config.properties
        configureWrapper.setMainConfig(ConfigKeyEnum.CKE_APP_NAME,"i8");
        configureWrapper.setMainConfig(ConfigKeyEnum.APP_VERSION_ID,"1");
        configureWrapper.setMainConfig(ConfigKeyEnum.CKE_THREAD_NUM,"16");
        configureWrapper.setMainConfig(ConfigKeyEnum.CKE_DB_INSERT_BATCH_SIZE,"1000");

        //config_db.propertis
        configureWrapper.setMainConfig(ConfigDbKeyEnum.CDKE_DB_DRIVER_NAME,"com.mysql.cj.jdbc.Driver");
        configureWrapper.setMainConfig(ConfigDbKeyEnum.CDKE_DB_URL,"jdbc:mysql://192.168.8.133:3306/test_db?autoReconnect=false&connectTimeout=60000&socketTimeout=60000&useUnicode=true&characterEncoding=UTF-8&characterSetResults=UTF-8&zeroDateTimeBehavior=convertToNull&useSSL=false&rewriteBatchedStatements=true");
        configureWrapper.setMainConfig(ConfigDbKeyEnum.CDKE_DB_USERNAME,"root");
        configureWrapper.setMainConfig(ConfigDbKeyEnum.CDKE_DB_PASSWORD,"123456");

        //jar_dir.properties
        ArrayList<String> otherConfigList = new ArrayList<>();
        otherConfigList.add("D:\\work\\jar\\100015\\dev");

        configureWrapper.setOtherConfigList(OtherConfigFileUseListEnum.OCFULE_JAR_DIR,otherConfigList);
        //allow
        HashSet<String> otherConfigSet= new HashSet<>();
        otherConfigSet.add("cn.newgrand");
        configureWrapper.setOtherConfigSet(OtherConfigFileUseSetEnum.OCFUSE_ALLOWED_CLASS_PREFIX, otherConfigSet);
        WriteDbPRunner writeDbPRunner = new WriteDbPRunner();
        long beginTime = System.currentTimeMillis();
        WriteDBResult writeDBResult = writeDbPRunner.runWithResult(configureWrapper, null);
        System.out.println(JSON.toJSONString(writeDBResult));
        long nowTime = System.currentTimeMillis();
        System.out.println("执行耗时: " + (nowTime - beginTime));


    }

    @Test
    public void perconaTest(){
        ConfigureWrapper configureWrapper = new ConfigureWrapper();
        //config.properties
        configureWrapper.setMainConfig(ConfigKeyEnum.CKE_APP_NAME,"i8");
        configureWrapper.setMainConfig(ConfigKeyEnum.APP_VERSION_ID,"6.0");
        configureWrapper.setMainConfig(ConfigKeyEnum.CKE_THREAD_NUM,"16");
        configureWrapper.setMainConfig(ConfigKeyEnum.CKE_DB_INSERT_BATCH_SIZE,"1000");

        //config_db.propertis
        configureWrapper.setMainConfig(ConfigDbKeyEnum.CDKE_DB_DRIVER_NAME,"com.mysql.cj.jdbc.Driver");
        configureWrapper.setMainConfig(ConfigDbKeyEnum.CDKE_DB_URL,"jdbc:mysql://192.168.126.10:6603/jacg_db?autoReconnect=false&connectTimeout=60000&socketTimeout=60000&useUnicode=true&characterEncoding=UTF-8&characterSetResults=UTF-8&zeroDateTimeBehavior=convertToNull&useSSL=false&rewriteBatchedStatements=true");
        configureWrapper.setMainConfig(ConfigDbKeyEnum.CDKE_DB_USERNAME,"root");
        configureWrapper.setMainConfig(ConfigDbKeyEnum.CDKE_DB_PASSWORD,"123456");
        configureWrapper.setMainConfig(ConfigKeyEnum.CKE_CHECK_JAR_FILE_UPDATED,"true");

        //jar_dir.properties
        ArrayList<String> otherConfigList = new ArrayList<>();
        otherConfigList.add("D:\\Data\\TestData\\microservice\\i8");

        configureWrapper.setOtherConfigList(OtherConfigFileUseListEnum.OCFULE_JAR_DIR,otherConfigList);
        //allow
        HashSet<String> otherConfigSet= new HashSet<>();
        otherConfigSet.add("cn.newgrand");
        configureWrapper.setOtherConfigSet(OtherConfigFileUseSetEnum.OCFUSE_ALLOWED_CLASS_PREFIX,otherConfigSet);
        WriteDbPRunner writeDbPRunner = new WriteDbPRunner();
        long beginTime = System.currentTimeMillis();
        writeDbPRunner.run(configureWrapper);
        long nowTime = System.currentTimeMillis();
        System.out.println("执行耗时: " + (nowTime - beginTime));
    }


}
