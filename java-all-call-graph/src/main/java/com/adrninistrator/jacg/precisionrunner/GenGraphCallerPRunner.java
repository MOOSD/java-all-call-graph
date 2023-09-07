package com.adrninistrator.jacg.precisionrunner;

import com.adrninistrator.jacg.precisionrunner.base.AbstractGenCallGraphPRunner;

public class GenGraphCallerPRunner extends AbstractGenCallGraphPRunner {

    @Override
    public String setRunnerName() {
        return "向下的调用树生成Runner";
    }

    @Override
    protected boolean preHandle() {
        return false;
    }

    @Override
    protected void handle() {

    }
}
