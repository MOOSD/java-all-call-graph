package test.db;

import com.adrninistrator.jacg.common.DC;
import com.adrninistrator.jacg.common.enums.*;
import com.adrninistrator.jacg.conf.ConfigureWrapper;
import com.adrninistrator.jacg.dboper.DbOperWrapper;
import com.adrninistrator.jacg.dboper.DbOperator;
import com.adrninistrator.jacg.dto.annotation.AnnotationWithAttributeInfo;
import com.adrninistrator.jacg.util.JACGSqlUtil;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class DbTest {

    @Test
    public void testCommonCondition(){


        DbOperator dbOperator = DbOperWrapper.genInstance(getConfigureWrapper(), this.getClass().getName()).getDbOperator();
        String sql = "update " + DbTableInfoEnum.DTIE_METHOD_CALL.getTableName() +
                " set " + DC.MC_ENABLED + " = ?" +
                " where " + DC.MC_CALL_ID + " = ?";
        String finalSql = JACGSqlUtil.replaceAppNameInSql(sql, "i8");
        int enabled = 1;
        int methodCallId = 1;
        dbOperator.update(finalSql,enabled, methodCallId);
    }

    @Test
    public void testCommonCondition2(){

        DbOperator dbOperator = DbOperWrapper.genInstance(getConfigureWrapper(), this.getClass().getName()).getDbOperator();
        String sql = "select * from class_annotation_i8 limit ?";
        List<AnnotationWithAttributeInfo> annotationWithAttributeInfos = dbOperator.queryList(sql, AnnotationWithAttributeInfo.class, 2);
        System.out.println(annotationWithAttributeInfos);
    }

    private ConfigureWrapper getConfigureWrapper(){
        ConfigureWrapper configureWrapper = new ConfigureWrapper();
        //config.properties
        configureWrapper.setMainConfig(ConfigKeyEnum.CKE_APP_NAME,"i8");
        configureWrapper.setMainConfig(ConfigKeyEnum.APP_VERSION_ID,"2617ef94-0087-4322-872c-015061a3cb90");
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
        HashSet<String> otherConfigSet= new HashSet<>();
        otherConfigSet.add("cn.newgrand");
        configureWrapper.setOtherConfigSet(OtherConfigFileUseSetEnum.OCFUSE_ALLOWED_CLASS_PREFIX,otherConfigSet);
        return configureWrapper;
    }
}
