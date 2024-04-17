package com.adrninistrator.jacg.precisionrunner;

import com.adrninistrator.jacg.api.CallTrees;
import com.adrninistrator.jacg.api.CalleeNode;
import com.adrninistrator.jacg.common.enums.*;
import com.adrninistrator.jacg.conf.RunConfig;
import com.adrninistrator.jacg.util.JACGJsonUtil;
import com.adrninistrator.jacg.util.JSON;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class GenGraphCalleePRunnerNewTest {

    /**
     * i8项目，向上调用链路的生成
     *
     */
    @Test
    public void getAllGraph4CalleeForI8() throws IOException {
        GenGraphCalleePRunner genGraphCalleePRunner = new GenGraphCalleePRunner();
        RunConfig runConfig = new RunConfig();
        runConfig.setMainConfig(ConfigKeyEnum.CKE_APP_NAME,"i8");
        runConfig.setMainConfig(ConfigKeyEnum.APP_VERSION_ID,"0.0.0.3 version");
        runConfig.setMainConfig(ConfigKeyEnum.CKE_THREAD_NUM,"16");
        runConfig.setMainConfig(ConfigKeyEnum.CROSS_SERVICE_BY_OPENFEIGN,"true");
        runConfig.setMainConfig(ConfigKeyEnum.MAX_NODE_NUM,"50");
        //config_db.properties
        runConfig.setMainConfig(ConfigDbKeyEnum.CDKE_DB_DRIVER_NAME,"com.mysql.cj.jdbc.Driver");
        runConfig.setMainConfig(ConfigDbKeyEnum.CDKE_DB_URL,"jdbc:mysql://192.168.8.162:3306/precision_dev?autoReconnect=false&useUnicode=true&characterEncoding=UTF-8&characterSetResults=UTF-8&zeroDateTimeBehavior=convertToNull&useSSL=false&rewriteBatchedStatements=true");
        runConfig.setMainConfig(ConfigDbKeyEnum.CDKE_DB_USERNAME,"root");
        runConfig.setMainConfig(ConfigDbKeyEnum.CDKE_DB_PASSWORD,"123456");
        //allow_class_prefix.properties
        runConfig.setOtherConfigSet(OtherConfigFileUseSetEnum.OCFUSE_ALLOWED_CLASS_PREFIX,"cn.newgrand");

        runConfig.setOtherConfigSet(OtherConfigFileUseSetEnum.OCFUSE_METHOD_CLASS_4CALLEE,
    "cn.newgrand.pm.crm.zb.service.impl.TendReceiptServiceImpl:sendUIC"
        );
        runConfig.setOtherConfigSet(OtherConfigFileUseSetEnum.OCFULE_BUSINESS_DATA_TYPE_SHOW_4EE,
                DefaultBusinessDataTypeEnum.BDTE_METHOD_CALL_INFO.getType(),
                DefaultBusinessDataTypeEnum.BDTE_METHOD_ARG_GENERICS_TYPE.getType()
        );


        runConfig.setOtherConfigList(OtherConfigFileUseListEnum.OCFULE_EXTENSIONS_METHOD_ANNOTATION_FORMATTER,
                "com.adrninistrator.jacg.annotation.formatter.SpringMvcRequestMappingFormatter",
                "com.adrninistrator.jacg.annotation.formatter.SpringTransactionalFormatter",
                "com.adrninistrator.jacg.annotation.formatter.DefaultAnnotationFormatter");

        long begin = System.currentTimeMillis();
        CallTrees<CalleeNode> tree = genGraphCalleePRunner.getLink(runConfig);
        System.out.println("执行时间: " + (System.currentTimeMillis() - begin));
        System.out.println(JACGJsonUtil.getJsonStr(tree));

    }

    @Test
    public void treeJSONTest(){
        GenGraphCalleePRunner genGraphCalleePRunner = new GenGraphCalleePRunner();
        RunConfig runConfig = new RunConfig();
        runConfig.setMainConfig(ConfigKeyEnum.CKE_APP_NAME,"d6c");
        runConfig.setMainConfig(ConfigKeyEnum.APP_VERSION_ID,"1");
        runConfig.setMainConfig(ConfigKeyEnum.CKE_THREAD_NUM,"16");
        runConfig.setMainConfig(ConfigKeyEnum.CROSS_SERVICE_BY_OPENFEIGN,"true");
        //config_db.properties
        runConfig.setMainConfig(ConfigDbKeyEnum.CDKE_DB_DRIVER_NAME,"com.mysql.cj.jdbc.Driver");
        runConfig.setMainConfig(ConfigDbKeyEnum.CDKE_DB_URL,"jdbc:mysql://192.168.8.162:3306/bytecode_dev?autoReconnect=false&useUnicode=true&characterEncoding=UTF-8&characterSetResults=UTF-8&zeroDateTimeBehavior=convertToNull&useSSL=false&rewriteBatchedStatements=true");
        runConfig.setMainConfig(ConfigDbKeyEnum.CDKE_DB_USERNAME,"root");
        runConfig.setMainConfig(ConfigDbKeyEnum.CDKE_DB_PASSWORD,"123456");
        runConfig.setMainConfig(ConfigKeyEnum.MAX_NODE_NUM,"100");
        //allow_class_prefix.properties
        runConfig.setOtherConfigSet(OtherConfigFileUseSetEnum.OCFUSE_ALLOWED_CLASS_PREFIX,"cn.newgrand");

        runConfig.setOtherConfigSet(OtherConfigFileUseSetEnum.OCFUSE_METHOD_CLASS_4CALLEE,
                "cn.newgrand.dcs.board.basic.service.impl.BoardOrgProjectInfoServiceImpl#shareBoardList"
        );
        runConfig.setOtherConfigSet(OtherConfigFileUseSetEnum.OCFULE_BUSINESS_DATA_TYPE_SHOW_4EE,
                DefaultBusinessDataTypeEnum.BDTE_METHOD_CALL_INFO.getType(),
                DefaultBusinessDataTypeEnum.BDTE_METHOD_ARG_GENERICS_TYPE.getType()
        );


        runConfig.setOtherConfigList(OtherConfigFileUseListEnum.OCFULE_EXTENSIONS_METHOD_ANNOTATION_FORMATTER,
                "com.adrninistrator.jacg.annotation.formatter.SpringMvcRequestMappingFormatter",
                "com.adrninistrator.jacg.annotation.formatter.SpringTransactionalFormatter",
                "com.adrninistrator.jacg.annotation.formatter.DefaultAnnotationFormatter");

        CallTrees<CalleeNode> tree = genGraphCalleePRunner.getLink(runConfig);
        String jsonStr = JACGJsonUtil.getJsonStr(tree);
        System.out.println("执行结果: " + jsonStr);

    }

    @Test
    public void getAllGraph4CalleeForI8RPC(){
        GenGraphCalleePRunner genGraphCalleePRunner = new GenGraphCalleePRunner();
        RunConfig runConfig = new RunConfig();
        runConfig.setMainConfig(ConfigKeyEnum.CKE_APP_NAME,"i8");
        runConfig.setMainConfig(ConfigKeyEnum.APP_VERSION_ID,"0.0.0.3 version");
        runConfig.setMainConfig(ConfigKeyEnum.CKE_THREAD_NUM,"16");
        runConfig.setMainConfig(ConfigKeyEnum.CROSS_SERVICE_BY_OPENFEIGN,"true");
        runConfig.setMainConfig(ConfigKeyEnum.MAX_NODE_NUM,"50");
        //config_db.properties
        runConfig.setMainConfig(ConfigDbKeyEnum.CDKE_DB_DRIVER_NAME,"com.mysql.cj.jdbc.Driver");
        runConfig.setMainConfig(ConfigDbKeyEnum.CDKE_DB_URL,"jdbc:mysql://192.168.8.162:3306/test_db?autoReconnect=false&useUnicode=true&characterEncoding=UTF-8&characterSetResults=UTF-8&zeroDateTimeBehavior=convertToNull&useSSL=false&rewriteBatchedStatements=true");
        runConfig.setMainConfig(ConfigDbKeyEnum.CDKE_DB_USERNAME,"root");
        runConfig.setMainConfig(ConfigDbKeyEnum.CDKE_DB_PASSWORD,"123456");
        //allow_class_prefix.properties
        runConfig.setOtherConfigSet(OtherConfigFileUseSetEnum.OCFUSE_ALLOWED_CLASS_PREFIX,"cn.newgrand");

        runConfig.setOtherConfigSet(OtherConfigFileUseSetEnum.OCFUSE_METHOD_CLASS_4CALLEE,"cn.newgrand.mspco.boq.IBoqFeign:getBoqDListByDic(java.lang.String)");
        runConfig.setOtherConfigSet(OtherConfigFileUseSetEnum.OCFULE_BUSINESS_DATA_TYPE_SHOW_4EE,
                DefaultBusinessDataTypeEnum.BDTE_METHOD_CALL_INFO.getType(),
                DefaultBusinessDataTypeEnum.BDTE_METHOD_ARG_GENERICS_TYPE.getType()
        );


        runConfig.setOtherConfigList(OtherConfigFileUseListEnum.OCFULE_EXTENSIONS_METHOD_ANNOTATION_FORMATTER,
                "com.adrninistrator.jacg.annotation.formatter.SpringMvcRequestMappingFormatter",
                "com.adrninistrator.jacg.annotation.formatter.SpringTransactionalFormatter",
                "com.adrninistrator.jacg.annotation.formatter.DefaultAnnotationFormatter");


        CallTrees<CalleeNode> tree = genGraphCalleePRunner.getLink(runConfig);

        System.out.println(JSON.toJSONString(tree));

    }


    /**
     * 灭霸项目测试类
     */
    @Test
    public void getAllGraph4CalleeForBK1(){
        GenGraphCalleePRunner runnerGenAllGraph4Callee = new GenGraphCalleePRunner();
        RunConfig runConfig = new RunConfig();
        runConfig.setMainConfig(ConfigKeyEnum.CKE_THREAD_NUM,"16");
        runConfig.setMainConfig(ConfigKeyEnum.CKE_APP_NAME,"hawkeye");
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
                "cn.newgrand.ck.service.impl.GetDiffTest:52",
                "cn.newgrand.ck.service.impl.GitInfoServiceImpl:416",
                "cn.newgrand.ck.util.JGitUtil:662",
                "cn.newgrand.ck.service.impl.GitInfoServiceImpl:386",
                "cn.newgrand.ck.service.impl.GitInfoServiceImpl:176"
        );



        runConfig.setOtherConfigList(OtherConfigFileUseListEnum.OCFULE_EXTENSIONS_METHOD_ANNOTATION_FORMATTER,
                "com.adrninistrator.jacg.annotation.formatter.SpringMvcRequestMappingFormatter",
                "com.adrninistrator.jacg.annotation.formatter.SpringTransactionalFormatter",
                "com.adrninistrator.jacg.annotation.formatter.DefaultAnnotationFormatter");


        CallTrees tree = runnerGenAllGraph4Callee.getLink(runConfig);

        System.out.println(JACGJsonUtil.getJsonStr(tree));

    }
    @Test
    public void getAllGraph4CalleeForPrecision(){
        GenGraphCalleePRunner runnerGenAllGraph4Callee = new GenGraphCalleePRunner();
        RunConfig runConfig = new RunConfig();
        runConfig.setMainConfig(ConfigKeyEnum.CKE_THREAD_NUM,"16");
        runConfig.setMainConfig(ConfigKeyEnum.CKE_APP_NAME,"hawkeye");
        runConfig.setMainConfig(ConfigKeyEnum.APP_VERSION_ID,"dev");
        runConfig.setMainConfig(ConfigKeyEnum.CROSS_SERVICE_BY_OPENFEIGN,"true");
        runConfig.setMainConfig(ConfigKeyEnum.MAX_NODE_NUM,"50");
        //config_db.properties
        runConfig.setMainConfig(ConfigDbKeyEnum.CDKE_DB_DRIVER_NAME,"com.mysql.cj.jdbc.Driver");
        runConfig.setMainConfig(ConfigDbKeyEnum.CDKE_DB_URL,"jdbc:mysql://192.168.8.162:3306/bytecode_dev?autoReconnect=false&useUnicode=true&characterEncoding=UTF-8&characterSetResults=UTF-8&zeroDateTimeBehavior=convertToNull&useSSL=false&rewriteBatchedStatements=true");
        runConfig.setMainConfig(ConfigDbKeyEnum.CDKE_DB_USERNAME,"root");
        runConfig.setMainConfig(ConfigDbKeyEnum.CDKE_DB_PASSWORD,"123456");
        //allow_class_prefix.properties
//        runConfig.setOtherConfigSet(OtherConfigFileUseSetEnum.OCFUSE_ALLOWED_CLASS_PREFIX,"cn.newgrand");

        runConfig.setOtherConfigSet(OtherConfigFileUseSetEnum.OCFUSE_METHOD_CLASS_4CALLEE,
                "cn.newgrand.ck.service.impl.GetDiffTest:52",
                "cn.newgrand.ck.service.impl.GitInfoServiceImpl:416",
                "cn.newgrand.ck.util.JGitUtil:662",
                "cn.newgrand.ck.service.impl.GitInfoServiceImpl:386",
                "cn.newgrand.ck.service.impl.GitInfoServiceImpl:176"
        );



        runConfig.setOtherConfigList(OtherConfigFileUseListEnum.OCFULE_EXTENSIONS_METHOD_ANNOTATION_FORMATTER,
                "com.adrninistrator.jacg.annotation.formatter.SpringMvcRequestMappingFormatter",
                "com.adrninistrator.jacg.annotation.formatter.SpringTransactionalFormatter",
                "com.adrninistrator.jacg.annotation.formatter.DefaultAnnotationFormatter");


        CallTrees tree = runnerGenAllGraph4Callee.getLink(runConfig);

        System.out.println(JACGJsonUtil.getJsonStr(tree));

    }


    /**
     * 灭霸项目测试类2
     */
    @Test
    public void getAllGraph4CalleeForBK2() throws IOException {
        GenGraphCalleePRunner runnerGenAllGraph4Callee = new GenGraphCalleePRunner();
        RunConfig runConfig = new RunConfig();
        runConfig.setMainConfig(ConfigKeyEnum.CKE_THREAD_NUM,"16");
        runConfig.setMainConfig(ConfigKeyEnum.CKE_APP_NAME,"bugkiller");
        runConfig.setMainConfig(ConfigKeyEnum.APP_VERSION_ID,"1");
        runConfig.setMainConfig(ConfigKeyEnum.CROSS_SERVICE_BY_OPENFEIGN,"true");
        runConfig.setMainConfig(ConfigKeyEnum.MAX_NODE_NUM,"500000");
        //config_db.properties
        runConfig.setMainConfig(ConfigDbKeyEnum.CDKE_DB_DRIVER_NAME,"com.mysql.cj.jdbc.Driver");
        runConfig.setMainConfig(ConfigDbKeyEnum.CDKE_DB_URL,"jdbc:mysql://192.168.8.162:3306/test_db?autoReconnect=false&useUnicode=true&characterEncoding=UTF-8&characterSetResults=UTF-8&zeroDateTimeBehavior=convertToNull&useSSL=false&rewriteBatchedStatements=true");
        runConfig.setMainConfig(ConfigDbKeyEnum.CDKE_DB_USERNAME,"root");
        runConfig.setMainConfig(ConfigDbKeyEnum.CDKE_DB_PASSWORD,"123456");
        //allow_class_prefix.properties
//        runConfig.setOtherConfigSet(OtherConfigFileUseSetEnum.OCFUSE_ALLOWED_CLASS_PREFIX,"cn.newgrand");

        runConfig.setOtherConfigSet(OtherConfigFileUseSetEnum.OCFUSE_METHOD_CLASS_4CALLEE,
                "io.metersphere.api.dto.definition.request.sampler.MsHTTPSamplerProxy:141"
        );



        runConfig.setOtherConfigList(OtherConfigFileUseListEnum.OCFULE_EXTENSIONS_METHOD_ANNOTATION_FORMATTER,
                "com.adrninistrator.jacg.annotation.formatter.SpringMvcRequestMappingFormatter",
                "com.adrninistrator.jacg.annotation.formatter.SpringTransactionalFormatter",
                "com.adrninistrator.jacg.annotation.formatter.DefaultAnnotationFormatter");


        CallTrees<CalleeNode> tree = runnerGenAllGraph4Callee.getLink(runConfig);

        String jsonStr = JACGJsonUtil.getJsonStr(tree);

        // 输出节点数量
        CalleeNode calleeNode = tree.getTrees().get(0);
        AtomicInteger num = new AtomicInteger();
        calleeNode.forEach(methodNode -> num.getAndIncrement());
        System.out.println("节点总数:"+num.get());


        FileWriter file = new FileWriter("C:\\Users\\77064\\Desktop\\calleeTreeJson");
        BufferedWriter bufferedWriter = new BufferedWriter(file);
        bufferedWriter.write(jsonStr);
        bufferedWriter.close();


    }

    /**
     * i8 财务域测试
     */
    @Test
    public void getAllGraph4CalleeForI8Cai() throws IOException {
        GenGraphCalleePRunner runnerGenAllGraph4Callee = new GenGraphCalleePRunner();
        RunConfig runConfig = new RunConfig();
        runConfig.setMainConfig(ConfigKeyEnum.CKE_THREAD_NUM,"16");
        runConfig.setMainConfig(ConfigKeyEnum.CKE_APP_NAME,"100017");
        runConfig.setMainConfig(ConfigKeyEnum.APP_VERSION_ID,"0");
        runConfig.setMainConfig(ConfigKeyEnum.CROSS_SERVICE_BY_OPENFEIGN,"true");
        runConfig.setMainConfig(ConfigKeyEnum.MAX_NODE_NUM,"100");
        //config_db.properties
        runConfig.setMainConfig(ConfigDbKeyEnum.CDKE_DB_DRIVER_NAME,"com.mysql.cj.jdbc.Driver");
        runConfig.setMainConfig(ConfigDbKeyEnum.CDKE_DB_URL,"jdbc:mysql://192.168.8.162:3306/bytecode_dev?autoReconnect=false&useUnicode=true&characterEncoding=UTF-8&characterSetResults=UTF-8&zeroDateTimeBehavior=convertToNull&useSSL=false&rewriteBatchedStatements=true");
        runConfig.setMainConfig(ConfigDbKeyEnum.CDKE_DB_USERNAME,"root");
        runConfig.setMainConfig(ConfigDbKeyEnum.CDKE_DB_PASSWORD,"123456");
        //allow_class_prefix.properties
        runConfig.setOtherConfigSet(OtherConfigFileUseSetEnum.OCFUSE_ALLOWED_CLASS_PREFIX,"cn.newgrand");

        runConfig.setOtherConfigSet(OtherConfigFileUseSetEnum.OCFUSE_METHOD_CLASS_4CALLEE,
            "cn.newgrand.msgfi.gc.common.helper.ReportQueryManager:471",
            "cn.newgrand.msgfi.frc.api.cr.controller.FrMergePlanController:94",
            "cn.newgrand.msgfi.gc.api.accbook.service.impl.ExtendAccoMultDetailedAccountServiceImpl:445",
            "cn.newgrand.msgfi.data.client.biz.impl.DataUnitProClientImpl:43",
            "cn.newgrand.msgfi.constant.enums.EnumDataBean:85",
            "cn.newgrand.msgfi.frc.api.cr.controller.FrAdjustPlanController:79",
            "cn.newgrand.msgfi.data.client.biz.impl.DataFeetypeClientImpl:43",
            "cn.newgrand.msgfi.gc.api.bookvoucher.service.impl.FinanceVoucherServiceImpl:1344",
            "cn.newgrand.msgfi.frc.api.cr.controller.FrAdjustPlanController:105",
            "cn.newgrand.msgfi.frc.api.cr.controller.FrAdjustPlanController:147",
            "cn.newgrand.msgfi.frc.api.cr.controller.FrReclassOffsetController:51",
            "cn.newgrand.msgfi.data.DataProcessor:62",
            "cn.newgrand.msgfi.gc.api.accbook.service.impl.AccoExtendTrialBalanceAccountServiceImpl:903",
            "cn.newgrand.msgfi.data.client.biz.impl.DataSimpleDataClientImpl:40",
            "cn.newgrand.msgfi.frc.api.cr.controller.FrMergePlanController:57",
            "cn.newgrand.msgfi.frc.api.cr.controller.FrMergePlanController:107",
            "cn.newgrand.msgfi.gc.api.bookvoucher.helper.BookVoucherHelper:2330",
            "cn.newgrand.msgfi.gc.api.accbook.service.impl.EmpDetailedAccountServiceImpl:422",
            "cn.newgrand.msgfi.data.client.biz.impl.DataBankSysClientImpl:39",
            "cn.newgrand.msgfi.gc.common.helper.CashflowFuncHelper:88",
            "cn.newgrand.msgfi.data.client.biz.impl.DataTeamsGroupClientImpl:43",
            "cn.newgrand.msgfi.gc.api.accbook.service.impl.EmpJournalServiceImpl:189",
            "cn.newgrand.msgfi.gc.common.helper.GlFuncHelper:210",
            "cn.newgrand.msgfi.frc.api.cr.service.impl.FrInterOffsetEntryServiceImpl:203",
            "cn.newgrand.msgfi.frc.api.cr.controller.FrAdjustPlanController:40",
            "cn.newgrand.msgfi.gc.api.accbook.service.impl.AccoExtendTrialBalanceAccountServiceImpl:217",
            "cn.newgrand.msgfi.gc.api.voucher.service.impl.ImportTempServiceImpl$2:793",
            "cn.newgrand.msgfi.frc.api.cr.controller.FrReclassOffsetController:64",
            "cn.newgrand.msgfi.gc.common.helper.VoucherNumGenerateHelper:170",
            "cn.newgrand.msgfi.data.client.biz.impl.DataUserClientImpl:36",
            "cn.newgrand.msgfi.data.client.biz.impl.DataCbsClientImpl:42",
            "cn.newgrand.msgfi.frc.api.cr.controller.FrMergePlanController:69",
            "cn.newgrand.msgfi.frc.api.cr.controller.FrMergePlanController:119",
            "cn.newgrand.msgfi.frc.common.supcan.util.FuncUtls:33",
            "cn.newgrand.msgfi.data.client.biz.impl.DataCustomerClientImpl:42",
            "cn.newgrand.msgfi.frc.api.report.service.impl.FrReportServiceImpl:419",
            "cn.newgrand.msgfi.data.client.biz.impl.DataResBsClientImpl:39",
            "cn.newgrand.msgfi.frc.api.cr.controller.FrAdjustPlanController:54",
            "cn.newgrand.msgfi.gc.api.accbook.service.impl.EmpJournalServiceImpl:158",
            "cn.newgrand.msgfi.frc.api.cr.controller.FrAdjustPlanController:131",
            "cn.newgrand.msgfi.gc.api.endterm.service.impl.MonthlyExchageServiceImpl:182",
            "cn.newgrand.msgfi.constant.enums.EnumDataBean:62",
            "cn.newgrand.msgfi.data.client.biz.impl.DataEnterpriseClientImpl:40",
            "cn.newgrand.msgfi.frc.api.cr.controller.FrReclassOffsetController:36",
            "cn.newgrand.msgfi.frc.api.cr.controller.FrReclassOffsetController:77",
            "cn.newgrand.msgfi.data.DataProcessor:80",
            "cn.newgrand.msgfi.frc.api.cr.controller.FrAdjustPlanController:92",
            "cn.newgrand.msgfi.gc.common.helper.GlFuncHelper:98",
            "cn.newgrand.msgfi.data.client.biz.impl.DataComeGoProjectClientImpl:40",
            "cn.newgrand.msgfi.gc.api.bookvoucher.helper.BookVoucherHelper:4453",
            "cn.newgrand.msgfi.data.client.biz.impl.DataOutCntMClientImpl:42",
            "cn.newgrand.msgfi.data.client.biz.impl.DataItemDataClientImpl:39",
            "cn.newgrand.msgfi.frc.api.cr.controller.FrMergePlanController:43",
            "cn.newgrand.msgfi.gc.api.bookvoucher.helper.BookVoucherHelper:1168",
            "cn.newgrand.msgfi.frc.api.cr.controller.FrMergePlanController:81",
            "cn.newgrand.msgfi.frc.api.cr.controller.FrAdjustPlanController:66",
            "cn.newgrand.msgfi.data.client.biz.impl.DataRegionClientImpl:40",
            "cn.newgrand.msgfi.data.client.biz.impl.DataSupplyFzrClientImpl:37",
            "cn.newgrand.msgfi.data.client.biz.impl.DataOuterAccClientImpl:47",
            "cn.newgrand.msgfi.gc.api.voucher.service.impl.ImportTempServiceImpl:754",
            "cn.newgrand.msgfi.data.client.biz.impl.DataPcClientImpl:43",
            "cn.newgrand.msgfi.data.client.biz.impl.DataRoleClientImpl:37",
            "cn.newgrand.msgfi.data.client.biz.impl.DataWbsClientImpl:42",
            "cn.newgrand.msgfi.frc.api.cr.controller.FrAdjustPlanController:118",
            "cn.newgrand.msgfi.gc.common.helper.VoucherNumGenerateHelper:359",
            "cn.newgrand.msgfi.data.client.biz.impl.DataSecuserClientImpl:39",
            "cn.newgrand.msgfi.data.client.biz.impl.DataSupplyClientImpl:42",
            "cn.newgrand.msgfi.data.client.biz.impl.DataVehicleClientImpl:42",
            "cn.newgrand.msgfi.frc.api.cr.controller.FrReclassOffsetController:43",
            "cn.newgrand.msgfi.frc.api.cr.controller.FrMergePlanController:133",
            "cn.newgrand.msgfi.data.client.biz.impl.DataInCntMClientImpl:42",
            "cn.newgrand.msgfi.gc.api.accbook.service.impl.ExtendAccoTrialBalanceAccountServiceImpl:191"
        );



        runConfig.setOtherConfigList(OtherConfigFileUseListEnum.OCFULE_EXTENSIONS_METHOD_ANNOTATION_FORMATTER,
                "com.adrninistrator.jacg.annotation.formatter.SpringMvcRequestMappingFormatter",
                "com.adrninistrator.jacg.annotation.formatter.SpringTransactionalFormatter",
                "com.adrninistrator.jacg.annotation.formatter.DefaultAnnotationFormatter");


        CallTrees<CalleeNode> tree = runnerGenAllGraph4Callee.getLink(runConfig);

        String jsonStr = JACGJsonUtil.getJsonStr(tree);

        // 输出节点数量
        CalleeNode calleeNode = tree.getTrees().get(0);
        AtomicInteger num = new AtomicInteger();
        calleeNode.forEach(methodNode -> num.getAndIncrement());
        System.out.println("节点总数:"+num.get());


        FileWriter file = new FileWriter("C:\\Users\\77064\\Desktop\\calleeTree.json");
        BufferedWriter bufferedWriter = new BufferedWriter(file);
        bufferedWriter.write(jsonStr);
        bufferedWriter.close();


    }
}