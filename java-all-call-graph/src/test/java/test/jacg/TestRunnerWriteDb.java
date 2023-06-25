package test.jacg;

import com.adrninistrator.jacg.common.enums.ConfigDbKeyEnum;
import com.adrninistrator.jacg.common.enums.ConfigKeyEnum;
import com.adrninistrator.jacg.common.enums.OtherConfigFileUseListEnum;
import com.adrninistrator.jacg.common.enums.OtherConfigFileUseSetEnum;
import com.adrninistrator.jacg.conf.ConfigureWrapper;
import com.adrninistrator.jacg.runner.RunnerWriteDb;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * @author adrninistrator
 * @date 2021/6/23
 * @description: 读取jar包内容，生成方法调用关系，并写入数据库
 */

public class TestRunnerWriteDb {

    public static void main(String[] args) {
        new RunnerWriteDb().run();
    }

    /**
     * sql: [insert into spring_controller_i8(method_hash, seq, show_uri, class_path, method_path, annotation_name, simple_class_name, full_method) values (?, ?, ?, ?, ?, ?, ?, ?)]
     * java.sql.BatchUpdateException: Duplicate entry 'R5bWsNDiQqfp38HkZoirdg==#052-0' for key 'PRIMARY'
     *
     * sql: [insert into method_annotation_i8(record_id, method_hash, annotation_name, attribute_name, attribute_type, attribute_value, full_method, simple_class_name, spring_mapping_annotation, is_feign_client) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)]
     * java.sql.BatchUpdateException: Incorrect string value: '\xF0\x9F\xA7\xB5st...' for column 'attribute_value' at row 1
     */
    @Test
    public void writeDBbyApiFori8(){
        ConfigureWrapper configureWrapper = new ConfigureWrapper();
        //config.properties
        configureWrapper.setMainConfig(ConfigKeyEnum.CKE_APP_NAME,"i8");
        configureWrapper.setMainConfig(ConfigKeyEnum.CKE_THREAD_NUM,"16");
        configureWrapper.setMainConfig(ConfigKeyEnum.CKE_DB_INSERT_BATCH_SIZE,"1000");
        //config_db.propertis
        configureWrapper.setMainConfig(ConfigDbKeyEnum.CDKE_DB_DRIVER_NAME,"com.mysql.cj.jdbc.Driver");
        configureWrapper.setMainConfig(ConfigDbKeyEnum.CDKE_DB_URL,"jdbc:mysql://192.168.8.162:3306/test_db?autoReconnect=false&useUnicode=true&characterEncoding=UTF-8&characterSetResults=UTF-8&zeroDateTimeBehavior=convertToNull&useSSL=false&rewriteBatchedStatements=true");
        configureWrapper.setMainConfig(ConfigDbKeyEnum.CDKE_DB_USERNAME,"root");
        configureWrapper.setMainConfig(ConfigDbKeyEnum.CDKE_DB_PASSWORD,"123456");
        //jar_dir.properties
        ArrayList<String> otherConfigList = new ArrayList<>();
        otherConfigList.add("D:\\Data\\TestData\\microservice\\i8");

        configureWrapper.setOtherConfigList(OtherConfigFileUseListEnum.OCFULE_JAR_DIR,otherConfigList);
        //allow
        HashSet<String>  otherConfigSet= new HashSet<>();
        otherConfigSet.add("cn.newgrand");
        configureWrapper.setOtherConfigSet(OtherConfigFileUseSetEnum.OCFUSE_ALLOWED_CLASS_PREFIX,otherConfigSet);
        RunnerWriteDb runnerWriteDb = new RunnerWriteDb();
        runnerWriteDb.run(configureWrapper);
    }

    @Test
    public void writeDBbyApiForJacg(){
        ConfigureWrapper configureWrapper = new ConfigureWrapper();
        //config.properties
        configureWrapper.setMainConfig(ConfigKeyEnum.CKE_APP_NAME,"jacg");
        configureWrapper.setMainConfig(ConfigKeyEnum.CKE_THREAD_NUM,"16");
        configureWrapper.setMainConfig(ConfigKeyEnum.CKE_DB_INSERT_BATCH_SIZE,"1000");
        //config_db.propertis
        configureWrapper.setMainConfig(ConfigDbKeyEnum.CDKE_DB_DRIVER_NAME,"com.mysql.cj.jdbc.Driver");
        configureWrapper.setMainConfig(ConfigDbKeyEnum.CDKE_DB_URL,"jdbc:mysql://192.168.8.162:3306/test_db?autoReconnect=false&useUnicode=true&characterEncoding=UTF-8&characterSetResults=UTF-8&zeroDateTimeBehavior=convertToNull&useSSL=false&rewriteBatchedStatements=true");
        configureWrapper.setMainConfig(ConfigDbKeyEnum.CDKE_DB_USERNAME,"root");
        configureWrapper.setMainConfig(ConfigDbKeyEnum.CDKE_DB_PASSWORD,"123456");
        //jar_dir.properties
        ArrayList<String> otherConfigList = new ArrayList<>();
        otherConfigList.add("C:\\Users\\77064\\Desktop\\microservice\\java-all-call-graph-1.0.6.jar");
        configureWrapper.setOtherConfigList(OtherConfigFileUseListEnum.OCFULE_JAR_DIR,otherConfigList);
        //allow
        HashSet<String>  otherConfigSet= new HashSet<>();
        otherConfigSet.add("com.adrninistrator");
        configureWrapper.setOtherConfigSet(OtherConfigFileUseSetEnum.OCFUSE_ALLOWED_CLASS_PREFIX,otherConfigSet);
        RunnerWriteDb runnerWriteDb = new RunnerWriteDb();
        runnerWriteDb.run(configureWrapper);
    }

    /**
     * 增量更新数据库。（非全删全插）
     * 代码的修改意味着，新增，修改，删除 但是针对代码删除的情况无法同步删除记录的。
     */
    @Test
    public void updateDBbyApiFori8(){
        ConfigureWrapper configureWrapper = new ConfigureWrapper();
        //config.properties
        configureWrapper.setMainConfig(ConfigKeyEnum.CKE_APP_NAME,"i8");
        configureWrapper.setMainConfig(ConfigKeyEnum.CKE_THREAD_NUM,"16");
        configureWrapper.setMainConfig(ConfigKeyEnum.CKE_DB_INSERT_BATCH_SIZE,"1000");
        // 增量更新配置为true
        configureWrapper.setMainConfig(ConfigKeyEnum.INCREMENT_UPDATE,"true");
        //config_db.propertis
        configureWrapper.setMainConfig(ConfigDbKeyEnum.CDKE_DB_DRIVER_NAME,"com.mysql.cj.jdbc.Driver");
        configureWrapper.setMainConfig(ConfigDbKeyEnum.CDKE_DB_URL,"jdbc:mysql://192.168.8.162:3306/test_db?autoReconnect=false&useUnicode=true&characterEncoding=UTF-8&characterSetResults=UTF-8&zeroDateTimeBehavior=convertToNull&useSSL=false&rewriteBatchedStatements=true");
        configureWrapper.setMainConfig(ConfigDbKeyEnum.CDKE_DB_USERNAME,"root");
        configureWrapper.setMainConfig(ConfigDbKeyEnum.CDKE_DB_PASSWORD,"123456");
        //jar_dir.properties
        ArrayList<String> otherConfigList = new ArrayList<>();
        otherConfigList.add("D:\\Data\\TestData\\microservice\\i8");

        configureWrapper.setOtherConfigList(OtherConfigFileUseListEnum.OCFULE_JAR_DIR,otherConfigList);
        //allow
        HashSet<String>  otherConfigSet= new HashSet<>();
        otherConfigSet.add("cn.newgrand");
        configureWrapper.setOtherConfigSet(OtherConfigFileUseSetEnum.OCFUSE_ALLOWED_CLASS_PREFIX,otherConfigSet);
        RunnerWriteDb runnerWriteDb = new RunnerWriteDb();
        runnerWriteDb.run(configureWrapper);
    }
}
