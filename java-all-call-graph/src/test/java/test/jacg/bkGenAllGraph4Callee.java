package test.jacg;

import com.adrninistrator.jacg.api.CalleeTrees;
import com.adrninistrator.jacg.common.enums.ConfigDbKeyEnum;
import com.adrninistrator.jacg.common.enums.ConfigKeyEnum;
import com.adrninistrator.jacg.common.enums.OtherConfigFileUseListEnum;
import com.adrninistrator.jacg.common.enums.OtherConfigFileUseSetEnum;
import com.adrninistrator.jacg.conf.RunConfig;
import com.adrninistrator.jacg.runner.RunnerGenGraph4ApiCallee;
import com.adrninistrator.jacg.util.JACGJsonUtil;
import org.junit.Test;

public class bkGenAllGraph4Callee {



    @Test
    public void getAllGraph4CalleeForBK(){
        RunnerGenGraph4ApiCallee runnerGenAllGraph4Callee = new RunnerGenGraph4ApiCallee();
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
                "io.metersphere.api.dto.definition.parse.Swagger3Parser:buildResponseBody(com.alibaba.fastjson.JSONObject)",
                "io.metersphere.controller.ProjectFeignController:getProject(java.lang.String)",
//                "io.metersphere.api.controller.ApiDefinitionController:diffList(int,int,ApiDefinitionRequest)",
                "io.metersphere.config.TaskSchedulerConfig:taskScheduler()");



        runConfig.setOtherConfigList(OtherConfigFileUseListEnum.OCFULE_EXTENSIONS_METHOD_ANNOTATION_FORMATTER,
                "com.adrninistrator.jacg.annotation.formatter.SpringMvcRequestMappingFormatter",
                "com.adrninistrator.jacg.annotation.formatter.SpringTransactionalFormatter",
                "com.adrninistrator.jacg.annotation.formatter.DefaultAnnotationFormatter");


        CalleeTrees tree = runnerGenAllGraph4Callee.getLink(runConfig);

        System.out.println(JACGJsonUtil.getJsonStr(tree));

    }

    @Test
    public void getAllGraph4CalleeForBK1(){
        RunnerGenGraph4ApiCallee runnerGenAllGraph4Callee = new RunnerGenGraph4ApiCallee();
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

        runConfig.setOtherConfigSet(OtherConfigFileUseSetEnum.OCFUSE_METHOD_CLASS_4CALLEE, "cn.newgrand.ck.service.impl.ByteCodeServiceJavaImpl:genCallerTrees(cn.newgrand.ck.entity.dto.GenCallTreeConfig)",
                "cn.newgrand.ck.util.JGitUtil:prepareTreeParser(org.eclipse.jgit.lib.Repository,java.lang.String)",
                "cn.newgrand.ck.controller.GitInfoController:addGitInfo(cn.newgrand.ck.entity.GitInfo)",
                "cn.newgrand.ck.controller.GitInfoController:diffResult(cn.newgrand.ck.entity.vo.GitDiffRequestVo)",
                "cn.newgrand.ck.util.JGitUtil:getDiffFilePathsBySpoon(org.eclipse.jgit.lib.Repository,java.lang.String,org.eclipse.jgit.revwalk.RevCommit,org.eclipse.jgit.revwalk.RevCommit)",
                "cn.newgrand.ck.service.impl.ByteCodeServiceJavaImplTest:genCalleeTrees()",
                "cn.newgrand.ck.service.impl.GitInfoServiceImpl:getDiffResult(cn.newgrand.ck.entity.vo.GitDiffRequestVo)",
                "cn.newgrand.ck.service.impl.GitInfoServiceImpl:update()",
                "cn.newgrand.ck.service.impl.GitInfoServiceImpl:branchDiff(cn.newgrand.ck.entity.vo.GitDiffRequestVo)",
                "cn.newgrand.ck.controller.GitInfoController:getGitInfoById(java.lang.Long)",
                "cn.newgrand.ck.controller.GitInfoController:getListByPage(java.lang.Integer,java.lang.Integer)",
                "cn.newgrand.ck.service.impl.ByteCodeServiceJavaImpl:initConnectionInfo(cn.newgrand.ck.entity.dto.JacgRunConfig)",
                "cn.newgrand.ck.controller.GitInfoController:getGitInfoByProjectId(java.lang.String)",
                "cn.newgrand.ck.controller.GitInfoController:getAll()",
                "cn.newgrand.ck.controller.GitInfoController:getCurrentProjectGitInfo()",
                "cn.newgrand.ck.service.PrecisionAsyncRunner:genLifecycle(cn.newgrand.ck.entity.dto.PrecisionRunReport)",
                "cn.newgrand.ck.service.impl.GitInfoServiceImpl:updateByProject(java.lang.Long)",
                "cn.newgrand.ck.controller.GitInfoController:getCurrentBranch(cn.newgrand.ck.entity.GitInfo)",
                "cn.newgrand.ck.service.impl.GitInfoServiceImpl:controllerDiff(cn.newgrand.ck.entity.vo.GitDiffRequestVo,java.util.List)",
                "cn.newgrand.ck.service.impl.GitInfoServiceImpl:delete(java.lang.Long)",
                "cn.newgrand.ck.service.impl.GitInfoServiceImpl:add(cn.newgrand.ck.entity.GitInfo)",
                "cn.newgrand.ck.service.impl.ByteCodeServiceJavaImpl:genCalleeTreesUseDefaultConfig(cn.newgrand.ck.entity.request.GenCallTreeRequest)",
                "cn.newgrand.ck.service.impl.GitInfoServiceImpl:getGitDiff(cn.newgrand.ck.entity.vo.GitDiffRequestVo)",
                "cn.newgrand.ck.controller.test.TestController:echo(java.lang.String)",
                "cn.newgrand.ck.controller.test.TestController:getMethod()",
                "cn.newgrand.ck.service.impl.ProjectConfigServiceImpl:updateConfig(cn.newgrand.ck.entity.request.ProjectConfigInsertRequest)",
                "cn.newgrand.ck.service.impl.GitInfoServiceImpl:updateById(java.lang.Long)",
                "cn.newgrand.ck.controller.ByteCodeController:genCalleeTrees(cn.newgrand.ck.entity.request.GenCallTreeRequest)",
                "cn.newgrand.ck.service.impl.GitInfoServiceImpl:gitInfo2DTO(cn.newgrand.ck.entity.GitInfo)",
                "cn.newgrand.ck.controller.ByteCodeController:genCallerTrees(cn.newgrand.ck.entity.dto.GenCallTreeConfig)",
                "cn.newgrand.ck.service.impl.ByteCodeServiceJavaImpl:availableVersionList(java.lang.String)",
                "cn.newgrand.ck.controller.test.TestController:echo(java.lang.Integer)",
                "cn.newgrand.ck.service.impl.ByteCodeServiceJavaImpl:analyzeAndPersist(cn.newgrand.ck.entity.dto.WriteDBConfig)",
                "cn.newgrand.ck.service.impl.ByteCodeServiceJavaImpl:genCalleeTrees(cn.newgrand.ck.entity.dto.GenCallTreeConfig)",
                "cn.newgrand.ck.controller.GitInfoController:update()",
                "cn.newgrand.ck.controller.GitInfoController:deleteGitInfo(java.lang.Long)",
                "cn.newgrand.ck.controller.GitInfoController:getCommit(cn.newgrand.ck.entity.GitInfo)",
                "cn.newgrand.ck.service.impl.PrecisionServiceImpl:run(cn.newgrand.ck.entity.request.PrecisionStartRequest)",
                "cn.newgrand.ck.service.impl.GitInfoServiceImpl:getLocalBranch(cn.newgrand.ck.entity.GitInfo)",
                "cn.newgrand.ck.controller.GitInfoController:updateById(java.lang.Long)",
                "cn.newgrand.ck.exception.GlobalExceptionHandler:BusinessExceptionHandler(cn.newgrand.ck.exception.BusinessException)",
                "cn.newgrand.ck.service.impl.GitInfoServiceImpl:getGitInfoLocalPath(cn.newgrand.ck.entity.GitInfo)",
                "cn.newgrand.ck.controller.GitInfoController:checkoutBranch(cn.newgrand.ck.entity.GitInfo,java.lang.String)",
                "cn.newgrand.ck.controller.GitInfoController:editGitInfo(cn.newgrand.ck.entity.GitInfo)",
                "cn.newgrand.ck.controller.GitInfoController:getLocalBranch(cn.newgrand.ck.entity.GitInfo)",
                "cn.newgrand.ck.service.impl.ByteCodeServiceJavaImpl:getTableListByProject(java.lang.String)",
                "cn.newgrand.ck.controller.GitInfoController:getRemoteBranch(cn.newgrand.ck.entity.GitInfo)",
                "cn.newgrand.ck.service.PrecisionAsyncRunner:updateRepository(cn.newgrand.ck.entity.dto.PrecisionRunReport,cn.newgrand.ck.entity.StageInfo)",
                "cn.newgrand.ck.controller.ByteCodeController:getCurrentProjectByteCodeInfo(java.lang.String)",
                "cn.newgrand.ck.service.impl.JarInfoServiceImpl:getVersionList(java.lang.String)",
                "cn.newgrand.ck.controller.GitInfoController:getTag(cn.newgrand.ck.entity.GitInfo)");



        runConfig.setOtherConfigList(OtherConfigFileUseListEnum.OCFULE_EXTENSIONS_METHOD_ANNOTATION_FORMATTER,
                "com.adrninistrator.jacg.annotation.formatter.SpringMvcRequestMappingFormatter",
                "com.adrninistrator.jacg.annotation.formatter.SpringTransactionalFormatter",
                "com.adrninistrator.jacg.annotation.formatter.DefaultAnnotationFormatter");


        CalleeTrees tree = runnerGenAllGraph4Callee.getLink(runConfig);

        System.out.println(JACGJsonUtil.getJsonStr(tree));

    }
}
