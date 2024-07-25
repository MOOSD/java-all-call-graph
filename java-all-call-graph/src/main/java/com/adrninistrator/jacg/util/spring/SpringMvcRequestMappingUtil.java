package com.adrninistrator.jacg.util.spring;

import com.adrninistrator.jacg.common.JACGCommonNameConstants;
import com.adrninistrator.jacg.util.JACGClassMethodUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * @author adrninistrator
 * @date 2022/11/17
 * @description: Spring MVC相关的RequestMapping等注解处理
 */
public class SpringMvcRequestMappingUtil {
    /**
     * 判断是否为Spring MVC的Controller注解
     *
     * @param annotationName
     * @return
     */
    public static boolean isControllerAnnotation(String annotationName) {
        return StringUtils.equalsAny(annotationName, JACGCommonNameConstants.SPRING_MVC_CONTROLLER_ANNOTATIONS);
    }

    /**
     * 判断是否为Spring MVC的RequestMapping注解
     *
     * @param annotationName
     * @return
     */
    public static boolean isRequestMappingAnnotation(String annotationName) {
        return StringUtils.equalsAny(annotationName, JACGCommonNameConstants.SPRING_MVC_MAPPING_ANNOTATIONS);
    }

    /**
     * 判断是否为Controller的控制器方法
     * @param annotationName 注解名称
     * @return
     */
    public static boolean isControllerHandlerMethod(String annotationName) {
        for (String springMvcMappingAnnotation : JACGCommonNameConstants.SPRING_MVC_MAPPING_ANNOTATIONS) {
            if (annotationName.startsWith(springMvcMappingAnnotation,1)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 从组合注解（GetMapping）名称中获取请求方式
     * @param annotationName
     * @return
     */
    public static String getRequestMethodFromAnnoName(String annotationName){
        //检查是否合法
        for (int i = 1; i < JACGCommonNameConstants.SPRING_MVC_MAPPING_ANNOTATIONS.length; i++) {
            if(annotationName.equals(JACGCommonNameConstants.SPRING_MVC_MAPPING_ANNOTATIONS[i])){
                //简单的获取到类名
                String simpleClassName = JACGClassMethodUtil.getSimpleClassNameFromFull(annotationName);
                //去掉后7位
                return simpleClassName.substring(0, simpleClassName.length() - 7).toUpperCase(Locale.ENGLISH);
            }
        }
        return null;
    }

    /**
     * 从注解属性中获取请求方式，主要是用来处理@RequestMapping中的method属性的属性值,注解属性值默认大写
     * @return 以逗号分割的请求方法
     */
    public static String getRequestMethodFromAnnoAttribute(String annoAttributeValue){
        return annoAttributeValue.substring(1, annoAttributeValue.length() - 1).replace("\"", "");
    }
    public static List<String> getRequestArrayMethodFromAnnoAttribute(String annoAttributeValue){
        String requestMethodFromAnnoAttribute = getRequestMethodFromAnnoAttribute(annoAttributeValue);
        String[] split = StringUtils.split(requestMethodFromAnnoAttribute, ",");
        return Arrays.asList(split);
    }
    public static MappingType getMappingType(String annotationName) {
        for (int i = 0; i < JACGCommonNameConstants.SPRING_MVC_MAPPING_ANNOTATIONS.length; i++) {
            if (annotationName.equals(JACGCommonNameConstants.SPRING_MVC_MAPPING_ANNOTATIONS[i])){
                return i >= 1 ? MappingType.COMPOSED_MAPPING : MappingType.MAPPING;
            }
        }
        return MappingType.NOMAPPING;
    }
    /**
     * 判断是否为Spring MVC的RequestMapping注解的path属性
     *
     * @param attributeName
     * @return
     */
    public static boolean isRequestMappingPathAttribute(String attributeName) {
        return StringUtils.equalsAny(attributeName, JACGCommonNameConstants.SPRING_MVC_MAPPING_ATTRIBUTE_NAMES);
    }

    /**
     * 判断是否为Spring MVC的RequestMapping的属性
     */
    public static boolean isRequestMappingAttribute(String attributeName) {
        return StringUtils.equalsAny(attributeName, JACGCommonNameConstants.SPRING_MVC_MAPPING_SPECIAL_ATTRIBUTE_NAMES);
    }

    /**
     * 生成用于显示的URI
     * 格式为"/classPath/methodPath"
     *
     * @param classPath
     * @param methodPath
     * @return
     */
    public static String genShowUri(String classPath, String methodPath) {
        StringBuilder stringBuilder = new StringBuilder();
        if (StringUtils.isNotBlank(classPath)) {
            if (!classPath.startsWith("/")) {
                stringBuilder.append("/");
            }
            if(classPath.endsWith("/")){
                classPath = classPath.substring(0,classPath.length()-1);
            }
            stringBuilder.append(classPath);
        }


        if (StringUtils.isNotBlank(methodPath)) {
            if (!methodPath.startsWith("/")) {
                stringBuilder.append("/");
            }
            stringBuilder.append(methodPath);
        }
        return stringBuilder.toString();
    }

    public static void main(String[] args) {
        System.out.println(genShowUri("/PMS/PCO/BOQEQ/BoqBill/", "/PostBoqSaveIn"));
    }

    private SpringMvcRequestMappingUtil() {
        throw new IllegalStateException("illegal");
    }
}
