package com.adrninistrator.jacg.api;

import com.adrninistrator.jacg.exception.RunnerBreakException;

/**
 * 控制Runner停止的类
 */

public class RunnerController {
    private transient boolean isBreak;
    public void setBreakPoint() {
        if(isBreak){
            throw new RunnerBreakException();
        }
    }

    public void setBreak(boolean aBreak) {
        isBreak = aBreak;
    }


}
