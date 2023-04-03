package com.adrninistrator.jacg.util.feign;

import com.adrninistrator.jacg.common.JACGCommonNameConstants;
import org.h2.util.StringUtils;

public class OpenFeignUtil {

    public static boolean isFeignClient(String annotationName){
        return (!StringUtils.isNullOrEmpty(annotationName) && JACGCommonNameConstants.FEIGN_CLIENT_ANNOTATIONS.equals(annotationName));
    }


}
