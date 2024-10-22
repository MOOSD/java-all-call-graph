package com.adrninistrator.jacg.precisionrunner.base;

import com.adrninistrator.jacg.common.enums.ConfigKeyEnum;
import com.adrninistrator.jacg.common.enums.DbTableInfoEnum;
import com.adrninistrator.jacg.conf.ConfigureWrapper;
import com.adrninistrator.jacg.dboper.DbOperWrapper;
import com.adrninistrator.jacg.dto.call_graph.DomainInfo;
import com.adrninistrator.jacg.exception.RunnerBreakException;
import com.adrninistrator.jacg.handler.extends_impl.JACGExtendsImplHandler;
import com.adrninistrator.jacg.runner.base.AbstractRunner;
import com.adrninistrator.jacg.util.JACGSqlUtil;
import com.adrninistrator.jacg.util.JSON;
import com.alibaba.ttl.TransmittableThreadLocal;
import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 精准化流程中用到的Runner的抽象类
 */
public abstract class AbstractPRunner extends AbstractRunner {
    private static final Logger logger = LoggerFactory.getLogger(AbstractPRunner.class);

    protected TransmittableThreadLocal<String> appName;
    protected TransmittableThreadLocal<DomainInfo> domain;
    protected  String currentSimpleClassName;
    protected Map<String, DomainInfo> domainInfoMap;
    protected String projectCode;
    protected String versionCode;

    public abstract String setRunnerName();

    /**
     * 初始化
     */
    private void init(ConfigureWrapper configureWrapper) {
        synchronized (this) {
            // 初始化当前Runner的名称
            currentSimpleClassName = setRunnerName();

            if (inited) {
                logger.warn("{} 已完成初始化，不会再初始化", currentSimpleClassName);
                return;
            }

            this.configureWrapper = configureWrapper;
            // 数据库操作相关的初始化
            if (handleDb()) {
                this.projectCode = configureWrapper.getMainConfig(ConfigKeyEnum.CKE_APP_NAME);
                this.versionCode = configureWrapper.getMainConfig(ConfigKeyEnum.APP_VERSION_ID);
                // 从数据库表中读取，而非从配置中获取
                String domain = configureWrapper.getMainConfig(ConfigKeyEnum.DOMAIN_CODE);
                this.domain =  new TransmittableThreadLocal<DomainInfo>(){
                    @Override
                    protected DomainInfo initialValue() {
                        return domainInfoMap.get(domain);
                    }
                };
                // 创建项目名
                this.appName = new TransmittableThreadLocal<String>(){
                    @Override
                    protected String initialValue() {
                        return JACGSqlUtil.getTableSuffix(projectCode, domain, versionCode);
                    }
                };

                // 完成需要使用的基础配置的初始化
                dbOperWrapper = DbOperWrapper.genInstance(configureWrapper, currentSimpleClassName);
                dbOperator = dbOperWrapper.getDbOperator();
                jacgExtendsImplHandler = new JACGExtendsImplHandler(dbOperWrapper);

                // 初始化所有业务域信息
                domainInfoMap = getAllDomainName();
                // 校验业务域信息
                if (CollectionUtils.isEmpty(domainInfoMap) || !domainInfoMap.containsKey(domain)){
                    logger.warn("业务域不存在");
                    return;
                }

            }


            inited = true;
        }
    }

    public String getAppName() {
        return appName.get();
    }

    public String getDomainCode(){
        return domain.get().getDomainCode();
    }

    public DomainInfo getDomain(){
        return domain.get();
    }
    /**
     * 修改APP名称中的业务域
     */
    public void tryChangeAppDomain(String doMainCode){
        if (getDomainCode().equals(doMainCode)) {
            logger.debug("处于相同业务域下无需切换");
            return;
        }
        logger.info("切换业务域:[{}], [{}]", getDomainCode() ,doMainCode);
        DomainInfo domainInfo = domainInfoMap.get(doMainCode);
        this.domain.set(domainInfo);
        this.appName.set(getAppNameByDomain(doMainCode));
    }

    /**
     * 范会修改doMain后的AppName
     */
    public String getAppNameByDomain(String doMainCode){
        return JACGSqlUtil.getTableSuffix(projectCode, doMainCode, versionCode);
    }
    /**
     * 入口方法，通过代码指定配置参数
     *
     * @param configureWrapper 当前使用的配置信息
     * @return
     */
    @Override
    public boolean run(ConfigureWrapper configureWrapper) throws RunnerBreakException {
        // 记录入口简单类名
        configureWrapper.addEntryClass(currentSimpleClassName);

        try {
            logger.info("开始执行");
            long startTime = System.currentTimeMillis();
            someTaskFail = false;

            // 初始化
            init(configureWrapper);

            // 预检查
            if (!preCheck()) {
                logger.error("{} 预检查失败", currentSimpleClassName);
                return false;
            }

            // 预处理
            if (!preHandle()) {
                logger.error("{} 预处理失败", currentSimpleClassName);
                return false;
            }

            // 执行处理
            handle();

            if (someTaskFail) {
                logger.error("{} 执行失败", currentSimpleClassName);
                return false;
            }
            // 执行完毕时尝试打印当前使用的配置信息
            long spendTime = System.currentTimeMillis() - startTime;
            logger.info("{} 执行完毕，耗时: {} S", currentSimpleClassName, spendTime / 1000.0D);
            return true;
        } catch (Exception e) {
            // 如果为中断异常，则输出日志后正常返回
            if(e instanceof RunnerBreakException){
                logger.warn("runner执行中断");
                throw e;
            }
            // 异常直接抛出
            throw new RuntimeException(currentSimpleClassName+ "执行异常",e);
        } finally {
            beforeExit();
        }
    }

    @Override
    protected void printAllConfigInfo() {
        // 暂时设置空实现
    }

    /**
     * 获取所有表中已有的业务域的名称
     */
    protected Map<String, DomainInfo> getAllDomainName(){
        Map<String, DomainInfo> allDomainCode = new HashMap<>();
        // 处理配置文件中的domains

        String mainConfigStr = configureWrapper.getMainConfig(ConfigKeyEnum.DOMAIN_CODES, true);
        if(StringUtils.isNotBlank(mainConfigStr)){
            List<DomainInfo> domainInfos = JSON.parseArray(mainConfigStr, new TypeReference<DomainInfo>() {});
            for (DomainInfo domainInfo : domainInfos) {
                allDomainCode.put(domainInfo.getDomainCode(), domainInfo);
            }
        }

        // 读取数据库中实际存在的业务域信息
        String tableNameSuffix = StringUtils.joinWith("_",DbTableInfoEnum.DTIE_METHOD_CALL.getSqlKey4Print(), this.projectCode);
        String sql = "show tables like '"+tableNameSuffix + "%'";
        List<String> tableNames = dbOperator.getJdbcTemplate().queryForList(sql, String.class);
        return tableNames.stream()
                .map(domainName -> {
                    String[] split = StringUtils.split(domainName, '_');
                    if (split.length != 5) {
                        return null;
                    }
                    return split[split.length - 2];
                })
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toMap(domainCode -> domainCode, domainCode -> {
                    DomainInfo domainInfo = allDomainCode.get(domainCode);
                    if(Objects.isNull(domainInfo)){
                        return new DomainInfo(domainCode,domainCode);
                    }
                    return domainInfo;
                }));


    }
}
