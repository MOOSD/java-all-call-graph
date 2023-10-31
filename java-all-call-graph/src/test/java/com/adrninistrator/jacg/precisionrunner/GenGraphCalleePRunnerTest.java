package com.adrninistrator.jacg.precisionrunner;

import com.adrninistrator.jacg.api.CallTrees;
import com.adrninistrator.jacg.api.CalleeNode;
import com.adrninistrator.jacg.common.enums.*;
import com.adrninistrator.jacg.conf.RunConfig;
import com.adrninistrator.jacg.util.JACGJsonUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

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
        runConfig.setMainConfig(ConfigKeyEnum.APP_VERSION_ID,"dev");
        runConfig.setMainConfig(ConfigKeyEnum.CKE_THREAD_NUM,"16");
        runConfig.setMainConfig(ConfigKeyEnum.CROSS_SERVICE_BY_OPENFEIGN,"true");
        //config_db.properties
        runConfig.setMainConfig(ConfigDbKeyEnum.CDKE_DB_DRIVER_NAME,"com.mysql.cj.jdbc.Driver");
        runConfig.setMainConfig(ConfigDbKeyEnum.CDKE_DB_URL,"jdbc:mysql://192.168.8.162:3306/precision_dev?autoReconnect=false&useUnicode=true&characterEncoding=UTF-8&characterSetResults=UTF-8&zeroDateTimeBehavior=convertToNull&useSSL=false&rewriteBatchedStatements=true");
        runConfig.setMainConfig(ConfigDbKeyEnum.CDKE_DB_USERNAME,"root");
        runConfig.setMainConfig(ConfigDbKeyEnum.CDKE_DB_PASSWORD,"123456");
        //allow_class_prefix.properties
        runConfig.setOtherConfigSet(OtherConfigFileUseSetEnum.OCFUSE_ALLOWED_CLASS_PREFIX,"cn.newgrand");

        runConfig.setOtherConfigSet(OtherConfigFileUseSetEnum.OCFUSE_METHOD_CLASS_4CALLEE,
                "cn.newgrand.pm.pcm.ht.service.impl.CntProjBalServiceImpl:1314",
                "cn.newgrand.pm.pco.service.impl.budget.TotalCostPlanMServiceImpl:2073",
                "cn.newgrand.pm.pms.service.impl.zy.ProjResMServiceImpl:210",
                "cn.newgrand.pm.pco.service.impl.ays.AysCostMServiceImpl:753",
                "cn.newgrand.pm.pmm.kc.service.impl.bill.KcBillHeadServiceImpl:3276",
                "cn.newgrand.pm.pmm.cg.domain.model.CgXunjMModel:510",
                "cn.newgrand.pm.pcm.ht.domain.CntAdjustiveDescModel:280",
                "cn.newgrand.pm.pco.controller.params.list.ListParam:48",
                "cn.newgrand.pm.pms.controller.zy.ProjResMController:718",
                "cn.newgrand.pm.pmm.cg.domain.model.MatPMModel:576",
                "cn.newgrand.pm.pco.domain.model.budget.PlanCostChgDItemModel:426",
                "cn.newgrand.gc.parent.common.business.CloudApi:1504",
                "cn.newgrand.pm.pcm.ht.service.impl.CntAdjustiveMServiceImpl:937",
                "cn.newgrand.pm.pco.controller.params.list.ListParam:53",
                "cn.newgrand.pm.crm.zb.rule.impl.CntExpertTypeRuleImpl:27",
                "cn.newgrand.pm.pcm.ht.rule.impl.CntAdjustiveDescRuleImpl:46",
                "cn.newgrand.pm.pco.service.impl.budget.PlanCostChgMServiceImpl:2942",
                "cn.newgrand.pm.pco.domain.model.budget.PlanCostChgDItemModel:56",
                "cn.newgrand.pm.supply.evalaccess.rule.impl.SppEvalAccessSuppRuleImpl:502",
                "cn.newgrand.gc.parent.common.business.CalPrecisionUtil:684",
                "cn.newgrand.pm.pmm.cg.domain.model.CgXunjMModel:504",
                "cn.newgrand.pm.supply.webapi.WorkFlowStrategy:106",
                "cn.newgrand.pm.pcm.ht.domain.CntAdjustiveDescModel:269",
                "cn.newgrand.pm.pco.rule.impl.budget.BudgetItemSetbackHisRuleImpl:1959",
                "cn.newgrand.pm.pco.controller.params.list.ListParam:41",
                "cn.newgrand.pm.pco.domain.model.budget.PlanCostChgDItemModel:433",
                "cn.newgrand.pm.pcm.ht.service.impl.CntAdjustiveMServiceImpl:922",
                "cn.newgrand.pm.pco.controller.params.save.BreakCostSaveParam:13",
                "cn.newgrand.pm.pmm.cg.rule.impl.ReqPMRuleImpl:107",
                "cn.newgrand.pm.crm.zb.rule.impl.CntExpertTypeRuleImpl:18",
                "cn.newgrand.pm.pmm.kc.controller.api.KcBill.KcBillApiController:812",
                "cn.newgrand.pm.supply.evalbackchg.controller.SppEvalBackChgController:83",
                "cn.newgrand.pm.pmm.cg.domain.model.MatPMModel:582",
                "cn.newgrand.pm.pmm.kc.service.impl.bill.KcBillHeadServiceImpl:4586",
                "cn.newgrand.pm.pmm.kc.controller.pd.KcCheckReportMController:145",
                "cn.newgrand.pm.pco.controller.params.list.ListParam:71",
                "cn.newgrand.pm.pco.domain.model.budget.PlanCostChgDItemModel:403",
                "cn.newgrand.pm.pcm.ht.service.impl.CntChangeDServiceImpl:127",
                "cn.newgrand.pm.pmm.cg.rule.impl.ReqPMRuleImpl:114",
                "cn.newgrand.pm.supply.evalbackchg.controller.SppEvalBackChgController:52",
                "cn.newgrand.pm.pms.cz.service.impl.CzActMServiceImpl:741",
                "cn.newgrand.pm.pcm.ht.domain.CntAdjustiveDescModel:297",
                "cn.newgrand.pm.pco.service.impl.budget.PlanCostBreakMServiceImpl:265",
                "cn.newgrand.pm.pco.common.CostBudgetCommon:888",
                "cn.newgrand.pm.crm.zb.controller.CntTenderFileController:485",
                "cn.newgrand.pm.pcm.ht.service.impl.CntGuaranteeServiceImpl:96",
                "cn.newgrand.gc.parent.common.business.CalPrecisionUtil:667",
                "cn.newgrand.pm.crm.zb.domain.CntTenderQuestionDModel:260",
                "cn.newgrand.pm.pms.service.impl.zy.ProjResMServiceImpl:2129",
                "cn.newgrand.pm.pms.basicdata.controller.res.ResBsController:112",
                "cn.newgrand.pm.supply.evalbackchg.controller.SppEvalBackChgController:66",
                "cn.newgrand.pm.pmm.cg.rule.impl.ReqPMRuleImpl:125",
                "cn.newgrand.gc.parent.common.business.CloudApi:2845",
                "cn.newgrand.pm.pco.controller.budget.TotalCostPlanMController:273",
                "cn.newgrand.pm.pco.domain.model.budget.PlanCostChgDItemModel:410",
                "cn.newgrand.pm.pco.controller.params.list.ListParam:62",
                "cn.newgrand.pm.pcm.ht.domain.CntAdjustiveDescModel:286",
                "cn.newgrand.pm.pmm.cg.service.impl.ReqPDServiceImpl:101",
                "cn.newgrand.pm.pmm.kc.service.impl.bill.KcBillHeadServiceImpl:5018",
                "cn.newgrand.pm.pcm.ht.service.impl.CntFinalDescServiceImpl:35",
                "cn.newgrand.pm.pmm.cg.domain.model.MatPChgDModel:1354",
                "cn.newgrand.pm.pmm.kc.service.impl.reports.VKcSendRecServiceImpl:197",
                "cn.newgrand.pm.pco.domain.model.budget.PlanCostChgDItemModel:83",
                "cn.newgrand.pm.pcm.ht.dao.impl.CntChangeMDaoImpl:100",
                "cn.newgrand.pm.crm.zb.controller.CntTenderFileController:460",
                "cn.newgrand.pm.pco.domain.business.budget.PlanCostChgDTree:810",
                "cn.newgrand.pm.pmm.cg.controller.ReqPMController:634",
                "cn.newgrand.pm.pco.controller.budget.CostctrlmodesetController:127",
                "cn.newgrand.pm.pcm.ht.controller.CntTemplateController:117",
                "cn.newgrand.pm.pcm.ht.service.impl.CntMServiceImpl:4291",
                "cn.newgrand.pm.crm.zb.domain.CntTenderBulletinDModel:455",
                "cn.newgrand.pm.pco.domain.model.budget.PlanCostChgDItemModel:99",
                "cn.newgrand.pm.pmm.cg.rule.impl.PurOrderChgDRuleImpl:105",
                "cn.newgrand.pm.pcm.ht.rule.impl.CntProjBalRuleImpl:390",
                "cn.newgrand.pm.pcm.ht.service.impl.CntChangeDServiceImpl:117",
                "cn.newgrand.pm.pco.controller.params.list.ListParam:82",
                "cn.newgrand.pm.pco.domain.model.budget.PlanCostBreakDModel:729",
                "cn.newgrand.pm.pmm.kc.service.impl.chargersheet.KcChargerSheetMServiceImpl:217",
                "cn.newgrand.pm.pcm.ht.service.impl.CntPayMServiceImpl:1632",
                "cn.newgrand.pm.pco.controller.params.list.ListParam:85",
                "cn.newgrand.pm.pmm.kc.service.impl.bill.KcBillBodyServiceImpl:976",
                "cn.newgrand.pm.pco.domain.business.budget.PlanCostChgDTree:828",
                "cn.newgrand.pm.pco.domain.model.budget.PlanCostBreakDModel:726",
                "cn.newgrand.pm.supply.basicdata.service.impl.SppEvalLevelServiceImpl:114",
                "cn.newgrand.pm.pms.cz.domain.model.CzActDModel:112",
                "cn.newgrand.pm.pco.domain.business.budget.PlanCostChgDTree:821",
                "cn.newgrand.pm.pco.controller.ays.AysItemMController:157",
                "cn.newgrand.pm.supply.evalyear.service.impl.SppEvalYearServiceImpl:375",
                "cn.newgrand.pm.crm.zb.controller.TendRefundController:196",
                "cn.newgrand.pm.pmm.kc.service.impl.bill.KcBillHeadServiceImpl:7655",
                "cn.newgrand.pm.crm.zb.controller.TendRefundController:190",
                "cn.newgrand.gc.bsdata.common.datacheck.DataCheckFactory:26",
                "cn.newgrand.pm.crm.zb.controller.CntBidEvaTaskController:183",
                "cn.newgrand.pm.crm.zb.domain.TenderAccessModel:59",
                "cn.newgrand.pm.pmm.kc.service.impl.zhc.KcZcbfDServiceImpl:185",
                "cn.newgrand.pm.pco.domain.model.budget.PlanCostChgDItemModel:63",
                "cn.newgrand.pm.pmm.cg.service.impl.CgJiesuanMServiceImpl:324",
                "cn.newgrand.pm.pco.domain.business.budget.TotalCostPlanDTree:1106",
                "cn.newgrand.pm.supply.evalbackchg.controller.SppEvalBackChgController:96"
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
        AtomicInteger i = new AtomicInteger();
        tree.forEach(methodNode -> i.getAndIncrement());
        System.out.println("节点数量" + i.get());
        System.out.println("生成节点数量: " + tree.getNodeIndex().size());
        System.out.println("执行结果: " + JACGJsonUtil.getJsonStr(tree));

    }

    @Test
    public void treeJSONTest(){
        GenGraphCalleePRunner genGraphCalleePRunner = new GenGraphCalleePRunner();
        RunConfig runConfig = new RunConfig();
        runConfig.setMainConfig(ConfigKeyEnum.CKE_APP_NAME,"i8");
        runConfig.setMainConfig(ConfigKeyEnum.APP_VERSION_ID,"dev");
        runConfig.setMainConfig(ConfigKeyEnum.CKE_THREAD_NUM,"16");
        runConfig.setMainConfig(ConfigKeyEnum.CROSS_SERVICE_BY_OPENFEIGN,"true");
        //config_db.properties
        runConfig.setMainConfig(ConfigDbKeyEnum.CDKE_DB_DRIVER_NAME,"com.mysql.cj.jdbc.Driver");
        runConfig.setMainConfig(ConfigDbKeyEnum.CDKE_DB_URL,"jdbc:mysql://192.168.8.162:3306/precision_dev?autoReconnect=false&useUnicode=true&characterEncoding=UTF-8&characterSetResults=UTF-8&zeroDateTimeBehavior=convertToNull&useSSL=false&rewriteBatchedStatements=true");
        runConfig.setMainConfig(ConfigDbKeyEnum.CDKE_DB_USERNAME,"root");
        runConfig.setMainConfig(ConfigDbKeyEnum.CDKE_DB_PASSWORD,"123456");
        //allow_class_prefix.properties
        runConfig.setOtherConfigSet(OtherConfigFileUseSetEnum.OCFUSE_ALLOWED_CLASS_PREFIX,"cn.newgrand");

        runConfig.setOtherConfigSet(OtherConfigFileUseSetEnum.OCFUSE_METHOD_CLASS_4CALLEE,
                "cn.newgrand.pm.pcm.ht.service.impl.CntProjBalServiceImpl:1314",
                "cn.newgrand.pm.pco.service.impl.budget.TotalCostPlanMServiceImpl:2073"
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
        String jsonStr = JACGJsonUtil.getJsonStr(tree);
        System.out.println("执行时间: " + (System.currentTimeMillis() - begin));
        AtomicInteger i = new AtomicInteger();
        tree.forEach(methodNode -> i.getAndIncrement());
        System.out.println("节点数量" + i.get());
        System.out.println("生成节点数量: " + tree.getNodeIndex().size());
        System.out.println("执行结果: " + jsonStr);

        CallTrees<CalleeNode> objFromJsonStr = JACGJsonUtil.getObjFromJsonStr(jsonStr, new TypeReference<CallTrees<CalleeNode>>() {
        });
        System.out.println("反序列化结果:" + JACGJsonUtil.getJsonStr(objFromJsonStr));


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


        CallTrees<CalleeNode> tree = genGraphCalleePRunner.getLink(runConfig);

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
}