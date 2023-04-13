package com.adrninistrator.jacg.runner;

import com.adrninistrator.jacg.common.enums.OtherConfigFileUseSetEnum;
import com.adrninistrator.jacg.conf.ConfigureWrapper;
import com.adrninistrator.jacg.dto.task.CalleeTaskInfo;
import com.adrninistrator.jacg.runner.base.AbstractRunnerGenCallGraph;
import com.adrninistrator.jacg.util.JACGUtil;
import com.adrninistrator.javacg.common.JavaCGConstants;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 生成java对象的向上调用链的runner
 */
public class RunnerGenApiGraph4Callee extends AbstractRunnerGenCallGraph {
    private static final Logger logger = LoggerFactory.getLogger(RunnerGenAllGraph4Callee.class);

    private Object syncResultCalleeLink;
    // 生成指定方法向上的调用链路
    public Object getLink(ConfigureWrapper config){
        //运行方法，结果收集到指定对象中。
        run(config);
        return syncResultCalleeLink;
    }

    @Override
    protected boolean preHandle() {
        // 抽象类中的公共预处理
        if (!commonPreHandle()) {
            return false;
        }

        // 读取配置文件中指定的需要处理的任务
        if (!readTaskInfo(OtherConfigFileUseSetEnum.OCFUSE_METHOD_CLASS_4CALLEE)) {
            return false;
        }

        return true;
    }

    @Override
    protected void handle() {
        //处理需要生成链路的方法
        Map<String, CalleeTaskInfo> calleeTaskInfoMap = genCalleeTaskInfo();
        // 创建线程，不指定任务数量，因为在对类进行处理时实际需要处理的方法数无法提前知道
        createThreadPoolExecutor(null);
        calleeTaskInfoMap.forEach((className, taskInfo)->{

        });
    }

    /**
     * 处理要生成链路的所有任务
     */
    private Map<String, CalleeTaskInfo> genCalleeTaskInfo() {
        /*
            当前方法返回的Map，每个键值对代表一个类
            含义
            key: 类名（简单类名或完整类名）
            value: 任务信息
         */
        Map<String, CalleeTaskInfo> calleeTaskInfoMap = new HashMap<>();
        // 生成需要处理的类名Set
        for (String task : taskSet) {
            String[] taskArray = StringUtils.splitPreserveAllTokens(task, JavaCGConstants.FLAG_COLON);
            //这里校验可以去掉。
            if (taskArray.length != 1 && taskArray.length != 2) {
                logger.error("配置文件 {} 中指定的任务信息非法\n{}\n格式应为以下之一:\n" +
                                "1. [类名] （代表生成指定类所有方法向上的调用链）\n" +
                                "2. [类名]:[方法名] （代表生成指定类指定名称方法向上的调用链）\n" +
                                "3. [类名]:[方法中的代码行号] （代表生成指定类指定代码行号对应方法向上的调用链）",
                        OtherConfigFileUseSetEnum.OCFUSE_METHOD_CLASS_4CALLEE, task);
                return null;
            }

            String className = taskArray[0];

            // 获取唯一类名（简单类名或完整类名）
            String simpleClassName = getSimpleClassName(className);
            if (simpleClassName == null) {
                return null;
            }

            CalleeTaskInfo calleeTaskInfo = calleeTaskInfoMap.computeIfAbsent(simpleClassName, k -> new CalleeTaskInfo());
            if (taskArray.length == 1) {
                // 仅指定了类名，需要处理所有的方法
                if (calleeTaskInfo.getMethodInfoMap() != null) {
                    logger.warn("{} 类指定了处理指定方法，也指定了处理全部方法，对该类的全部方法都会进行处理", simpleClassName);
                }

                calleeTaskInfo.setGenAllMethods(true);
            } else {
                // 除类名外还指定了方法信息，只处理指定的方法
                if (calleeTaskInfo.isGenAllMethods()) {
                    logger.warn("{} 类指定了处理全部方法，也指定了处理指定方法，对该类的全部方法都会进行处理", simpleClassName);
                    continue;
                }

                Map<String, String> methodInfoMap = calleeTaskInfo.getMethodInfoMap();
                if (methodInfoMap == null) {
                    methodInfoMap = new HashMap<>();
                    calleeTaskInfo.setMethodInfoMap(methodInfoMap);
                }
                String methodInfo = taskArray[1];
                 /*
                    以下put的数据：
                    key: 配置文件中指定的任务原始文本
                    value: 配置文件中指定的方法名或代码行号
                  */
                methodInfoMap.put(task, methodInfo);

                if (!JACGUtil.isNumStr(methodInfo)) {
                    // 当有指定通过方法名而不是代码行号获取方法时，设置对应标志
                    calleeTaskInfo.setFindMethodByName(true);
                }
            }
        }

        return calleeTaskInfoMap;
    }

//    private handleOneClass(String className,CalleeTaskInfo taskInfo){
//
//    }
}
