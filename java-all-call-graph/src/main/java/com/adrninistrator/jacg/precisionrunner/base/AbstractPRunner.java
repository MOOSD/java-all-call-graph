package com.adrninistrator.jacg.precisionrunner.base;

import com.adrninistrator.jacg.common.enums.ConfigKeyEnum;
import com.adrninistrator.jacg.conf.ConfigureWrapper;
import com.adrninistrator.jacg.dboper.DbOperWrapper;
import com.adrninistrator.jacg.handler.extends_impl.JACGExtendsImplHandler;
import com.adrninistrator.jacg.runner.base.AbstractRunner;
import com.adrninistrator.jacg.util.JACGSqlUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 精准化流程中用到的Runner的抽象类
 */
public abstract class AbstractPRunner extends AbstractRunner {
    private static final Logger logger = LoggerFactory.getLogger(AbstractPRunner.class);

    protected  String currentSimpleClassName;

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
            if (handleDb()) {
                // 需要操作数据库时执行的操作
                // 指定项目名
                appName = JACGSqlUtil.getTableSuffix(configureWrapper.getMainConfig(ConfigKeyEnum.CKE_APP_NAME),
                        configureWrapper.getMainConfig(ConfigKeyEnum.APP_VERSION_ID));

                // 完成需要使用的基础配置的初始化
                dbOperWrapper = DbOperWrapper.genInstance(configureWrapper, currentSimpleClassName);
                dbOperator = dbOperWrapper.getDbOperator();
                jacgExtendsImplHandler = new JACGExtendsImplHandler(dbOperWrapper);
            }
            inited = true;
        }
    }

    /**
     * 入口方法，通过代码指定配置参数
     *
     * @param configureWrapper 当前使用的配置信息
     * @return
     */
    @Override
    public boolean run(ConfigureWrapper configureWrapper) {
        // 记录入口简单类名
        configureWrapper.addEntryClass(currentSimpleClassName);

        try {
            logger.info("{} 开始执行", currentSimpleClassName);
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
}
