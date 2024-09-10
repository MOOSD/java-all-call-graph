package com.adrninistrator.jacg.precisionrunner;

import com.adrninistrator.jacg.api.RunnerController;
import com.adrninistrator.jacg.common.enums.ConfigDbKeyEnum;
import com.adrninistrator.jacg.common.enums.ConfigKeyEnum;
import com.adrninistrator.jacg.common.enums.OtherConfigFileUseListEnum;
import com.adrninistrator.jacg.common.enums.OtherConfigFileUseSetEnum;
import com.adrninistrator.jacg.conf.ConfigureWrapper;
import com.adrninistrator.jacg.dto.write_db.WriteDBResult;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class WriteDbPRunnerNewTest {

    @Test
    public void writeDBbyApiFori8(){
        ConfigureWrapper configureWrapper = new ConfigureWrapper();
        //config.properties
        configureWrapper.setMainConfig(ConfigKeyEnum.CKE_APP_NAME,"i8");
        configureWrapper.setMainConfig(ConfigKeyEnum.APP_VERSION_ID,"dev");
        configureWrapper.setMainConfig(ConfigKeyEnum.CKE_THREAD_NUM,"16");
        configureWrapper.setMainConfig(ConfigKeyEnum.CKE_DB_INSERT_BATCH_SIZE,"1000");

        //config_db.propertis
        configureWrapper.setMainConfig(ConfigDbKeyEnum.CDKE_DB_DRIVER_NAME,"com.mysql.cj.jdbc.Driver");
        configureWrapper.setMainConfig(ConfigDbKeyEnum.CDKE_DB_URL,"jdbc:mysql://192.168.8.162:3306/test_db?autoReconnect=false&connectTimeout=60000&socketTimeout=60000&useUnicode=true&characterEncoding=UTF-8&characterSetResults=UTF-8&zeroDateTimeBehavior=convertToNull&useSSL=false&rewriteBatchedStatements=true");
        configureWrapper.setMainConfig(ConfigDbKeyEnum.CDKE_DB_USERNAME,"root");
        configureWrapper.setMainConfig(ConfigDbKeyEnum.CDKE_DB_PASSWORD,"123456");

        //jar_dir.properties
        ArrayList<String> otherConfigList = new ArrayList<>();
        otherConfigList.add("D:\\Data\\TestData\\i8\\dev 24-7-25");

        configureWrapper.setOtherConfigList(OtherConfigFileUseListEnum.OCFULE_JAR_DIR,otherConfigList);
        //allow
        HashSet<String> otherConfigSet= new HashSet<>();
        otherConfigSet.add("cn.newgrand");
        configureWrapper.setOtherConfigSet(OtherConfigFileUseSetEnum.OCFUSE_ALLOWED_CLASS_PREFIX,otherConfigSet);
        WriteDbPRunner writeDbPRunner = new WriteDbPRunner();
        writeDbPRunner.run(configureWrapper);

    }

    @Test
    public void writeDBbyApiForD6C(){
        ConfigureWrapper configureWrapper = new ConfigureWrapper();
        //config.properties
        configureWrapper.setMainConfig(ConfigKeyEnum.CKE_APP_NAME,"d6c");
        configureWrapper.setMainConfig(ConfigKeyEnum.APP_VERSION_ID,"dev");
        configureWrapper.setMainConfig(ConfigKeyEnum.CKE_THREAD_NUM,"16");
        configureWrapper.setMainConfig(ConfigKeyEnum.CKE_DB_INSERT_BATCH_SIZE,"1000");

        //config_db.propertis
        configureWrapper.setMainConfig(ConfigDbKeyEnum.CDKE_DB_DRIVER_NAME,"com.mysql.cj.jdbc.Driver");
        configureWrapper.setMainConfig(ConfigDbKeyEnum.CDKE_DB_URL,"jdbc:mysql://192.168.8.162:3306/test_db?autoReconnect=false&connectTimeout=60000&socketTimeout=60000&useUnicode=true&characterEncoding=UTF-8&characterSetResults=UTF-8&zeroDateTimeBehavior=convertToNull&useSSL=false&rewriteBatchedStatements=true");
        configureWrapper.setMainConfig(ConfigDbKeyEnum.CDKE_DB_USERNAME,"root");
        configureWrapper.setMainConfig(ConfigDbKeyEnum.CDKE_DB_PASSWORD,"123456");

        //jar_dir.properties
        ArrayList<String> otherConfigList = new ArrayList<>();
        otherConfigList.add("D:\\Data\\TestData\\D6G\\7-25 172\\dcs-platform-business");

        configureWrapper.setOtherConfigList(OtherConfigFileUseListEnum.OCFULE_JAR_DIR,otherConfigList);
        //allow
        HashSet<String> otherConfigSet= new HashSet<>();
        otherConfigSet.add("cn.newgrand");
        configureWrapper.setOtherConfigSet(OtherConfigFileUseSetEnum.OCFUSE_ALLOWED_CLASS_PREFIX,otherConfigSet);
        WriteDbPRunner writeDbPRunner = new WriteDbPRunner();
        writeDbPRunner.run(configureWrapper);

    }

    @Test
    public void writeDBbyApiForD6CStop() throws ExecutionException, InterruptedException {
        ConfigureWrapper configureWrapper = new ConfigureWrapper();
        //config.properties
        configureWrapper.setMainConfig(ConfigKeyEnum.CKE_APP_NAME,"d6c");
        configureWrapper.setMainConfig(ConfigKeyEnum.APP_VERSION_ID,"master");
        configureWrapper.setMainConfig(ConfigKeyEnum.CKE_THREAD_NUM,"16");
        configureWrapper.setMainConfig(ConfigKeyEnum.CKE_DB_INSERT_BATCH_SIZE,"1000");

        //config_db.propertis
        configureWrapper.setMainConfig(ConfigDbKeyEnum.CDKE_DB_DRIVER_NAME,"com.mysql.cj.jdbc.Driver");
        configureWrapper.setMainConfig(ConfigDbKeyEnum.CDKE_DB_URL,"jdbc:mysql://192.168.8.162:3306/test_db?autoReconnect=false&connectTimeout=60000&socketTimeout=60000&useUnicode=true&characterEncoding=UTF-8&characterSetResults=UTF-8&zeroDateTimeBehavior=convertToNull&useSSL=false&rewriteBatchedStatements=true");
        configureWrapper.setMainConfig(ConfigDbKeyEnum.CDKE_DB_USERNAME,"root");
        configureWrapper.setMainConfig(ConfigDbKeyEnum.CDKE_DB_PASSWORD,"123456");

        //jar_dir.properties
        ArrayList<String> otherConfigList = new ArrayList<>();
        otherConfigList.add("D:\\Data\\TestData\\D6G");

        configureWrapper.setOtherConfigList(OtherConfigFileUseListEnum.OCFULE_JAR_DIR,otherConfigList);
        //allow
        HashSet<String> otherConfigSet= new HashSet<>();
        otherConfigSet.add("cn.newgrand");
        configureWrapper.setOtherConfigSet(OtherConfigFileUseSetEnum.OCFUSE_ALLOWED_CLASS_PREFIX,otherConfigSet);
        WriteDbPRunner writeDbPRunner = new WriteDbPRunner();
        RunnerController runnerController = new RunnerController();
        FutureTask<WriteDBResult> writeDBResultFutureTask = new FutureTask<>(() -> writeDbPRunner.runWithResult(configureWrapper, runnerController));
        // 任务执行
        new Thread(writeDBResultFutureTask).start();

        // 10秒后任务取消
        Thread.sleep(10 * 1000);
        System.out.println("任务取消");
        runnerController.stop();
        WriteDBResult writeDBResult = writeDBResultFutureTask.get();
        System.out.println("执行结束"+writeDBResult);

    }
    @Test
    public void writeDBbyApiForPrecision(){
        ConfigureWrapper configureWrapper = new ConfigureWrapper();
        //config.properties
        configureWrapper.setMainConfig(ConfigKeyEnum.CKE_APP_NAME,"hawkeye");
        configureWrapper.setMainConfig(ConfigKeyEnum.APP_VERSION_ID,"dev");
        configureWrapper.setMainConfig(ConfigKeyEnum.CKE_THREAD_NUM,"16");
        configureWrapper.setMainConfig(ConfigKeyEnum.CKE_DB_INSERT_BATCH_SIZE,"1000");

        //config_db.propertis
        configureWrapper.setMainConfig(ConfigDbKeyEnum.CDKE_DB_DRIVER_NAME,"com.mysql.cj.jdbc.Driver");
        configureWrapper.setMainConfig(ConfigDbKeyEnum.CDKE_DB_URL,"jdbc:mysql://192.168.8.162:3306/bytecode_dev?autoReconnect=false&connectTimeout=60000&socketTimeout=60000&useUnicode=true&characterEncoding=UTF-8&characterSetResults=UTF-8&zeroDateTimeBehavior=convertToNull&useSSL=false&rewriteBatchedStatements=true");
        configureWrapper.setMainConfig(ConfigDbKeyEnum.CDKE_DB_USERNAME,"root");
        configureWrapper.setMainConfig(ConfigDbKeyEnum.CDKE_DB_PASSWORD,"123456");

        //jar_dir.properties
        ArrayList<String> otherConfigList = new ArrayList<>();
        otherConfigList.add("D:\\work\\jar\\hawkeye\\dev");

        configureWrapper.setOtherConfigList(OtherConfigFileUseListEnum.OCFULE_JAR_DIR,otherConfigList);
        //allow
        HashSet<String> otherConfigSet= new HashSet<>();
        otherConfigSet.add("cn.newgrand");
        configureWrapper.setOtherConfigSet(OtherConfigFileUseSetEnum.OCFUSE_ALLOWED_CLASS_PREFIX,otherConfigSet);
        WriteDbPRunner writeDbPRunner = new WriteDbPRunner();
        writeDbPRunner.run(configureWrapper);

    }


    @Test
    public void writeDBbyApiForBK(){
        ConfigureWrapper configureWrapper = new ConfigureWrapper();
        //config.properties
        configureWrapper.setMainConfig(ConfigKeyEnum.CKE_APP_NAME,"bugkiller");
        configureWrapper.setMainConfig(ConfigKeyEnum.APP_VERSION_ID,"1");
        configureWrapper.setMainConfig(ConfigKeyEnum.CKE_THREAD_NUM,"16");
        configureWrapper.setMainConfig(ConfigKeyEnum.CKE_DB_INSERT_BATCH_SIZE,"1000");

        //config_db.propertis
        configureWrapper.setMainConfig(ConfigDbKeyEnum.CDKE_DB_DRIVER_NAME,"com.mysql.cj.jdbc.Driver");
        configureWrapper.setMainConfig(ConfigDbKeyEnum.CDKE_DB_URL,"jdbc:mysql://192.168.8.162:3306/test_db?autoReconnect=false&connectTimeout=60000&socketTimeout=60000&useUnicode=true&characterEncoding=UTF-8&characterSetResults=UTF-8&zeroDateTimeBehavior=convertToNull&useSSL=false&rewriteBatchedStatements=true");
        configureWrapper.setMainConfig(ConfigDbKeyEnum.CDKE_DB_USERNAME,"root");
        configureWrapper.setMainConfig(ConfigDbKeyEnum.CDKE_DB_PASSWORD,"123456");

        //jar_dir.properties
        ArrayList<String> otherConfigList = new ArrayList<>();
        otherConfigList.add("D:\\Data\\TestData\\BKclasses");

        configureWrapper.setOtherConfigList(OtherConfigFileUseListEnum.OCFULE_JAR_DIR,otherConfigList);
        //allow
        HashSet<String> otherConfigSet= new HashSet<>();
        otherConfigSet.add("io.metersphere");
        configureWrapper.setOtherConfigSet(OtherConfigFileUseSetEnum.OCFUSE_ALLOWED_CLASS_PREFIX,otherConfigSet);
        WriteDbPRunner writeDbPRunner = new WriteDbPRunner();
        writeDbPRunner.run(configureWrapper);

    }

}