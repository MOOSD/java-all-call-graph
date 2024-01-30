package com.adrninistrator.jacg.api;

import com.adrninistrator.jacg.exception.RunnerBreakException;

/**
 * 控制Runner停止的类
 */

public class RunnerController {
    private transient boolean isBreak;


    /**
     * 停止runner
     * @return 是否停止成功
     */
    public boolean stop(){
        // 已经中断则无法继续中断
        if(isBreak){
            return false;
        }
        this.isBreak = true;
        return true;
    }

    /**
     * 设置中断点
     */
    public void setBreakPoint() {
        if(isBreak){
            throw new RunnerBreakException("runner执行中断");
        }
    }



}
