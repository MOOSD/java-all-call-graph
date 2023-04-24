package com.adrninistrator.jacg.util;

import com.adrninistrator.jacg.api.MethodArgument;

import java.util.List;
import java.util.stream.Collectors;

public class MethodUtil {

    /**
     * 从全方法名中获取到方法参数列表
     * @param fullMethod
     * @return
     */
    public static List<MethodArgument> genMethodArgTypeList(String fullMethod){
        List<String> strings = JACGClassMethodUtil.genMethodArgTypeList(fullMethod);
        return strings.stream().map(methodStr -> new MethodArgument(methodStr, null)).collect(Collectors.toList());
    }
}
