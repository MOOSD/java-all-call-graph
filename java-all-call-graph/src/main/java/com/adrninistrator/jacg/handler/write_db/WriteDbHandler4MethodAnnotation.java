package com.adrninistrator.jacg.handler.write_db;

import com.adrninistrator.jacg.annotation.util.AnnotationAttributesParseUtil;
import com.adrninistrator.jacg.common.JACGCommonNameConstants;
import com.adrninistrator.jacg.common.JACGConstants;
import com.adrninistrator.jacg.common.annotations.JACGWriteDbHandler;
import com.adrninistrator.jacg.common.enums.DbTableInfoEnum;
import com.adrninistrator.jacg.dto.write_db.WriteDbData4FeignClientData;
import com.adrninistrator.jacg.dto.write_db.WriteDbData4MethodAnnotation;
import com.adrninistrator.jacg.dto.write_db.WriteDbData4SpringController;
import com.adrninistrator.jacg.util.JACGClassMethodUtil;
import com.adrninistrator.jacg.util.JACGUtil;
import com.adrninistrator.jacg.util.spring.MappingType;
import com.adrninistrator.jacg.util.spring.SpringMvcRequestMappingUtil;
import com.adrninistrator.javacg.common.enums.JavaCGOutPutFileTypeEnum;
import com.adrninistrator.javacg.common.enums.JavaCGYesNoEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author adrninistrator
 * @date 2022/11/15
 * @description: 写入数据库，方法的注解
 */
@JACGWriteDbHandler(
        readFile = true,
        mainFile = true,
        mainFileTypeEnum = JavaCGOutPutFileTypeEnum.OPFTE_METHOD_ANNOTATION,
        minColumnNum = JACGConstants.ANNOTATION_COLUMN_NUM_WITHOUT_ATTRIBUTE,
        maxColumnNum = JACGConstants.ANNOTATION_COLUMN_NUM_WITH_ATTRIBUTE,
        dbTableInfoEnum = DbTableInfoEnum.DTIE_METHOD_ANNOTATION
)
public class WriteDbHandler4MethodAnnotation extends AbstractWriteDbHandler<WriteDbData4MethodAnnotation> {
    private static final Logger logger = LoggerFactory.getLogger(WriteDbHandler4MethodAnnotation.class);

    private Map<String, List<String>> classRequestMappingMap;

    //feignclient的相关信息
    private Map<String, Map<String, String>> feignClientClassMap;

    private WriteDbHandler4FeignClient writeDbHandler4FeignClient;

    // 将Spring Controller信息写入数据库的类
    private WriteDbHandler4SpringController writeDbHandler4SpringController;

    // Spring Controller对应的方法HASH+长度(供生成方法调用信息使用)
    private Set<String> springControllerMethodHashSet = new HashSet<>();

    // 有注解的方法HASH+长度
    private Set<String> withAnnotationMethodHashSet = new HashSet<>();

    // Spring Controller相关信息
    private final List<WriteDbData4SpringController> writeDbData4SpringControllerList = new ArrayList<>(batchSize);

    // feign Client相关信息
    private final List<WriteDbData4FeignClientData> writeDbData4FeignClientList = new ArrayList<>(batchSize);

    // 暂存feign的方法hash todo (供生成方法调用信息使用)
    private final Set<String> feignClientHashSet = new HashSet<>();

    // 用于存储接口请求方式的map
    private final Map<String,List<String>> controllerReqMethodMap = new HashMap<>();
    private final Map<String,List<WriteDbData4SpringController>> incompleteController = new HashMap<>();

    // 用于存储Feign请求方式的map
    private final Map<String,List<String>> feignClientReqMethodMap = new HashMap<>();
    private final Map<String,List<WriteDbData4FeignClientData>> incompleteFeignClient = new HashMap<>();
    public static final String UNSPECIFIED_REQ_METHOD = "UNSPECIFIED";


    @Override
    protected WriteDbData4MethodAnnotation genData(String[] array) {
        // 拆分时限制列数，最后一列注解属性中可能出现空格
        String fullMethod = array[0];
        // 根据完整方法前缀判断是否需要处理
        if (!isAllowedClassPrefix(fullMethod)) {
            return null;
        }

        String className = JACGClassMethodUtil.getClassNameFromMethod(fullMethod);
        String simpleClassName = dbOperWrapper.getSimpleClassName(className);
        String methodHash = JACGUtil.genHashWithLen(fullMethod);
        String annotationName = array[1];
        // 若当前行的注解信息无属性，注解属性名称设为空字符串
        String attributeName = "";
        String attributeType = null;
        String attributeValue = null;
        if (array.length > JACGConstants.ANNOTATION_COLUMN_NUM_WITHOUT_ATTRIBUTE) {
            // 当前行的注解信息有属性
            attributeName = array[2];
            attributeType = array[3];
            // 从文件记录解析注解属性
            attributeValue = AnnotationAttributesParseUtil.parseFromFile(attributeType, array[4]);
        }

        // 记录有注解的方法HASH+长度
        withAnnotationMethodHashSet.add(methodHash);
        // 处理Spring Controller相关注解
        boolean isSpringMappingAnnotation = handleSpringControllerAnnotation(methodHash, fullMethod, simpleClassName, annotationName, attributeName, attributeValue);
        // 处理FeignClient相关注解
        boolean isFeignClient = handleFeignClientAnnotation(methodHash, fullMethod, className,simpleClassName, annotationName, attributeName, attributeValue);

        WriteDbData4MethodAnnotation writeDbData4MethodAnnotation = new WriteDbData4MethodAnnotation();
        writeDbData4MethodAnnotation.setMethodHash(methodHash);
        writeDbData4MethodAnnotation.setAnnotationName(annotationName);
        writeDbData4MethodAnnotation.setAttributeName(attributeName);
        writeDbData4MethodAnnotation.setAnnotationType(attributeType);
        writeDbData4MethodAnnotation.setAttributeValue(attributeValue);
        writeDbData4MethodAnnotation.setFullMethod(fullMethod);
        writeDbData4MethodAnnotation.setSimpleClassName(simpleClassName);
        writeDbData4MethodAnnotation.setSpringMappingAnnotation(JavaCGYesNoEnum.parseIntValue(isSpringMappingAnnotation));
        writeDbData4MethodAnnotation.setIsFeignClient(JavaCGYesNoEnum.parseIntValue(isFeignClient));
        return writeDbData4MethodAnnotation;
    }

    @Override
    protected Object[] genObjectArray(WriteDbData4MethodAnnotation data) {
        return new Object[]{
                genNextRecordId(),
                data.getMethodHash(),
                data.getAnnotationName(),
                data.getAttributeName(),
                data.getAnnotationType(),
                data.getAttributeValue(),
                data.getFullMethod(),
                data.getSimpleClassName(),
                data.getSpringMappingAnnotation(),
                data.getIsFeignClient()
        };
    }

    @Override
    public String[] chooseFileColumnDesc() {
        return new String[]{
                "完整方法（类名+方法名+参数）",
                "注解类名",
                "注解属性名称，空字符串代表无注解属性",
                "注解属性类型，s:字符串；bs:包含回车换行的字符串；m:JSON字符串，Map；ls:JSON字符串，List+String；lm:JSON字符串，List+Map",
                "注解属性值"
        };
    }

    @Override
    public String[] chooseOtherFileDetailInfo() {
        return new String[]{
                "方法上指定的注解信息",
                "若注解没有属性值，则相关字段为空",
                "若注解有属性值，则每个属性值占一行"
        };
    }

    /**
     * 处理Spring Controller相关注解
     *
     * @param methodHash
     * @param fullMethod
     * @param simpleClassName
     * @param annotationName
     * @param attributeName
     * @param attributeValue
     * @return false: 不是Spring Controller相关注解 true: 是Spring Controller相关注解
     */
    private boolean handleSpringControllerAnnotation(String methodHash, String fullMethod, String simpleClassName, String annotationName, String attributeName,
                                                     String attributeValue) {
        // 判断对应的类是否与Spring Controller相关
        List<String> classRequestMappingPathList = classRequestMappingMap.get(simpleClassName);
        // 获取注解上的requestMapping的类型
        MappingType mappingType = SpringMvcRequestMappingUtil.getMappingType(annotationName);
        if (classRequestMappingPathList == null || mappingType.equals(MappingType.NOMAPPING)) {
            // 当前类与Spring Controller相关，或当前方法的注解不是@RequestMapping
            return false;
        }
        if (!attributeName.isEmpty() && !SpringMvcRequestMappingUtil.isRequestMappingAttribute(attributeName)) {
            // 注解属性名称非空，且不是@RequestMapping注解的path属性
            return false;
        }

        // 记录Spring Controller对应的方法HASH+长度
        springControllerMethodHashSet.add(methodHash);

        if (classRequestMappingPathList.isEmpty()) {
            // 假如类的path列表为空，则创建为只有一个空字符串的列表
            classRequestMappingPathList = Collections.singletonList("");
        }

        //对@RequestMapping的attributeName = method的情况进行处理
        if(MappingType.MAPPING.equals(mappingType) && JACGCommonNameConstants.SPRING_MVC_MAPPING_ATTRIBUTE_METHOD.equals(attributeName)){
            controllerReqMethodMap.put(methodHash, SpringMvcRequestMappingUtil.getRequestArrayMethodFromAnnoAttribute(attributeValue));
        }else{
            //@RequestMapping的attributeName是path 或者是@GetMapping在这里处理
            //尝试从注解中获取请求方法,若无法获取则尝试记录的map中获取
            List<String> requestMethods = new ArrayList<>();
            String requestMethodFromAnnoName = SpringMvcRequestMappingUtil.getRequestMethodFromAnnoName(annotationName);
            List<String> requestMappings = controllerReqMethodMap.get(methodHash);
            if(Objects.nonNull(requestMethodFromAnnoName)){
                requestMethods.add(requestMethodFromAnnoName);
            }else if(!CollectionUtils.isEmpty(requestMappings)){
                requestMethods.addAll(requestMappings);
            }
            List<String> methodPathList = null;
            if (attributeValue != null) {
                methodPathList = AnnotationAttributesParseUtil.parseListStringAttribute(attributeValue);
            }
            if (methodPathList == null || methodPathList.isEmpty()) {
                // 假如方法的path列表为空，则创建为只有一个空字符串的列表
                methodPathList = Collections.singletonList("");
            }

            List<WriteDbData4SpringController> controllerList = new ArrayList<>();
            for (String classRequestMappingPath : classRequestMappingPathList) {
                for (String methodPath : methodPathList) {
                    if(CollectionUtils.isEmpty(requestMethods)){
                        String showUri = SpringMvcRequestMappingUtil.genShowUri(classRequestMappingPath, methodPath);
                        WriteDbData4SpringController writeDbData4SpringController = new WriteDbData4SpringController();
                        writeDbData4SpringController.setMethodHash(methodHash);
                        writeDbData4SpringController.setShowUri(showUri);
                        writeDbData4SpringController.setClassPath(classRequestMappingPath);
                        writeDbData4SpringController.setMethodPath(methodPath);
                        writeDbData4SpringController.setAnnotationName(annotationName);
                        writeDbData4SpringController.setSimpleClassName(simpleClassName);
                        writeDbData4SpringController.setFullMethod(fullMethod);
                        writeDbData4SpringController.setRequestMethod(UNSPECIFIED_REQ_METHOD);
                        controllerList.add(writeDbData4SpringController);
                        logger.debug("找到接口信息信息: {}", writeDbData4SpringController.getShowUri());
                    }else{
                        for (String requestMethod : requestMethods) {
                            String showUri = SpringMvcRequestMappingUtil.genShowUri(classRequestMappingPath, methodPath);
                            WriteDbData4SpringController writeDbData4SpringController = new WriteDbData4SpringController();
                            writeDbData4SpringController.setMethodHash(methodHash);
                            writeDbData4SpringController.setShowUri(showUri);
                            writeDbData4SpringController.setClassPath(classRequestMappingPath);
                            writeDbData4SpringController.setMethodPath(methodPath);
                            writeDbData4SpringController.setAnnotationName(annotationName);
                            writeDbData4SpringController.setSimpleClassName(simpleClassName);
                            writeDbData4SpringController.setFullMethod(fullMethod);
                            writeDbData4SpringController.setRequestMethod(requestMethod);
                            controllerList.add(writeDbData4SpringController);
                            logger.debug("找到接口信息信息: {}", writeDbData4SpringController.getShowUri());
                        }
                    }
                }
            }
            //如果请求方法是空，则表示此纪录并不完整,暂存
            if(CollectionUtils.isEmpty(requestMethods)){
                incompleteController.put(methodHash, controllerList);
            }else{
                //直接存储
                writeDbData4SpringControllerList.addAll(controllerList);
                // 尝试写入Spring Controller信息
                writeDbHandler4SpringController.tryInsertDb(writeDbData4SpringControllerList);
                return true;
            }
        }

        //检查不完整的记录，查看是否可以组装出完整记录
        if (incompleteController.containsKey(methodHash) && controllerReqMethodMap.containsKey(methodHash)) {
            List<String> requestMethods = controllerReqMethodMap.get(methodHash);
            List<WriteDbData4SpringController> controllerList = incompleteController.get(methodHash);
            // 存储复制后的接口信息
            LinkedList<WriteDbData4SpringController> targetList = new LinkedList<>();
            for (WriteDbData4SpringController writeDbData4SpringController : controllerList) {
                for (String requestMethod : requestMethods) {
                    WriteDbData4SpringController newWriteDbData4SpringController = new WriteDbData4SpringController();
                    BeanUtils.copyProperties(writeDbData4SpringController, newWriteDbData4SpringController);
                    newWriteDbData4SpringController.setRequestMethod(requestMethod);
                    targetList.add(newWriteDbData4SpringController);
                }
            }
            //记录组装完成，添加到记录新增列表
            writeDbData4SpringControllerList.addAll(targetList);
            //释放记录
            controllerReqMethodMap.remove(methodHash);
            incompleteController.remove(methodHash);
        }

        // 尝试写入Spring Controller信息
        writeDbHandler4SpringController.tryInsertDb(writeDbData4SpringControllerList);
        return true;

    }


    /**
     * 处理openFeign相关注解
     */
    private boolean handleFeignClientAnnotation(String methodHash, String fullMethod, String className,String simpleClassName, String annotationName, String attributeName,
                                                String attributeValue){
        MappingType mappingType = SpringMvcRequestMappingUtil.getMappingType(annotationName);
        //此方法对应类不为FeignClient
        if (!feignClientClassMap.containsKey(simpleClassName) || mappingType.equals(MappingType.NOMAPPING)) {
            return false;
        }
        if (!attributeName.isEmpty() && !SpringMvcRequestMappingUtil.isRequestMappingAttribute(attributeName)) {
            // 注解属性名称非空，且不是@RequestMapping注解的path属性
            return false;
        }

        feignClientHashSet.add(methodHash);
        //对@RequestMapping的attributeName = method的情况进行处理
        if(MappingType.MAPPING.equals(mappingType) && JACGCommonNameConstants.SPRING_MVC_MAPPING_ATTRIBUTE_METHOD.equals(attributeName)){
            feignClientReqMethodMap.put(methodHash, SpringMvcRequestMappingUtil.getRequestArrayMethodFromAnnoAttribute(attributeValue));

        }else{

            //尝试从注解中获取请求方法,若无法获取则尝试记录的map中获取
            List<String> requestMethods = new ArrayList<>();
            String requestMethodFromAnnoName = SpringMvcRequestMappingUtil.getRequestMethodFromAnnoName(annotationName);
            List<String> requestMappings = feignClientReqMethodMap.get(methodHash);
            if(Objects.nonNull(requestMethodFromAnnoName)){
                requestMethods.add(requestMethodFromAnnoName);
            }else if(!CollectionUtils.isEmpty(requestMappings)){
                requestMethods.addAll(requestMappings);
            }
            Map<String, String> classAttrMap = feignClientClassMap.get(simpleClassName);
            //获取FeignClient属性名
            String classAttrName = Objects.isNull(classAttrMap.get(JACGCommonNameConstants.FEIGN_CLIENT_ATTR_NAME_VALUE))
                    ? classAttrMap.get(JACGCommonNameConstants.FEIGN_CLIENT_ATTR_NAME_NAME)
                    : classAttrMap.get(JACGCommonNameConstants.FEIGN_CLIENT_ATTR_NAME_VALUE);
            String classAttrContextId = classAttrMap.get(JACGCommonNameConstants.FEIGN_CLIENT_ATTR_NAME_CONTEXTID);
            //feignClient的path属性是String类型，不是List<String>
            String classAttrPath = classAttrMap.get(JACGCommonNameConstants.FEIGN_CLIENT_ATTR_NAME_PATH);

            List<String> methodPathList = Collections.emptyList();
            if (attributeValue != null) {
                methodPathList = AnnotationAttributesParseUtil.parseListStringAttribute(attributeValue);
            }

            List<WriteDbData4FeignClientData> feignClientDataList = new ArrayList<>();
            for (String methodPath : methodPathList) {
                //实例化DO
                if(CollectionUtils.isEmpty(requestMethods)){
                    WriteDbData4FeignClientData writeDbData4FeignClientData = new WriteDbData4FeignClientData();
                    writeDbData4FeignClientData.setContextId(classAttrContextId);
                    writeDbData4FeignClientData.setServiceName(classAttrName);
                    writeDbData4FeignClientData.setClassPath(classAttrPath);

                    writeDbData4FeignClientData.setClassName(className); //todo:className目测多余，simpleClassName已经是唯一的了
                    writeDbData4FeignClientData.setAnnotationName(annotationName);
                    writeDbData4FeignClientData.setSimpleClassName(simpleClassName);
                    writeDbData4FeignClientData.setFullMethod(fullMethod);
                    writeDbData4FeignClientData.setMethodPath(methodPath);
                    writeDbData4FeignClientData.setShowUri(SpringMvcRequestMappingUtil.genShowUri(classAttrPath, methodPath));
                    writeDbData4FeignClientData.setMethodHash(methodHash);
                    writeDbData4FeignClientData.setRequestMethod(UNSPECIFIED_REQ_METHOD);
                    //新增记录添加到对应List
                    feignClientDataList.add(writeDbData4FeignClientData);
                    logger.debug("找到RPC接口信息信息: {}", writeDbData4FeignClientData.getShowUri());
                }else{
                    for (String requestMethod : requestMethods) {
                        WriteDbData4FeignClientData writeDbData4FeignClientData = new WriteDbData4FeignClientData();
                        writeDbData4FeignClientData.setContextId(classAttrContextId);
                        writeDbData4FeignClientData.setServiceName(classAttrName);
                        writeDbData4FeignClientData.setClassPath(classAttrPath);

                        writeDbData4FeignClientData.setClassName(className); //todo:className目测多余，simpleClassName已经是唯一的了
                        writeDbData4FeignClientData.setAnnotationName(annotationName);
                        writeDbData4FeignClientData.setSimpleClassName(simpleClassName);
                        writeDbData4FeignClientData.setFullMethod(fullMethod);
                        writeDbData4FeignClientData.setMethodPath(methodPath);
                        writeDbData4FeignClientData.setShowUri(SpringMvcRequestMappingUtil.genShowUri(classAttrPath, methodPath));
                        writeDbData4FeignClientData.setMethodHash(methodHash);
                        writeDbData4FeignClientData.setRequestMethod(requestMethod);
                        //新增记录添加到对应List
                        feignClientDataList.add(writeDbData4FeignClientData);
                        logger.debug("找到RPC接口信息信息: {}", writeDbData4FeignClientData.getShowUri());
                    }
                }

            }
            //如果请求方法是空，则表示此纪录并不完整,暂存
            if(CollectionUtils.isEmpty(requestMethods)){
                incompleteFeignClient.put(methodHash,feignClientDataList);
            }else{
                //直接存储
                writeDbData4FeignClientList.addAll(feignClientDataList);
                // 尝试写入Spring Controller信息
                writeDbHandler4FeignClient.tryInsertDb(writeDbData4FeignClientList);
                return true;
            }
        }

        //检查不完整的记录，查看是否可以组装出完整记录
        if (incompleteFeignClient.containsKey(methodHash) && feignClientReqMethodMap.containsKey(methodHash)) {
            List<String> requestMethods = feignClientReqMethodMap.get(methodHash);
            List<WriteDbData4FeignClientData> feignClientList = incompleteFeignClient.get(methodHash);
            LinkedList<WriteDbData4FeignClientData> targetList = new LinkedList<>();
            for (WriteDbData4FeignClientData writeDbData4FeignClientData : feignClientList) {
                for (String requestMethod : requestMethods) {
                    WriteDbData4FeignClientData newWriteDbData4FeignClientData = new WriteDbData4FeignClientData();
                    BeanUtils.copyProperties(writeDbData4FeignClientData,newWriteDbData4FeignClientData);
                    newWriteDbData4FeignClientData.setRequestMethod(requestMethod);
                    targetList.add(newWriteDbData4FeignClientData);
                }
            }
            //记录组装完成，添加到记录新增列表
            writeDbData4FeignClientList.addAll(targetList);
            //释放记录
            feignClientReqMethodMap.remove(methodHash);
            incompleteFeignClient.remove(methodHash);
        }

        // 尝试写入feignClient的信息
        writeDbHandler4FeignClient.tryInsertDb(writeDbData4FeignClientList);
        return true;
    }
    @Override
    protected void beforeDone() {
        //将剩余没有请求方式的controller信息写入到数据库
        List<WriteDbData4SpringController> noReqMethodControllerList = incompleteController
                .values().stream().flatMap(Collection::stream).collect(Collectors.toList());
        writeDbData4SpringControllerList.addAll(noReqMethodControllerList);
        //将剩余没有表示请求方式的feignclient信息写入数据库
        List<WriteDbData4FeignClientData> noReqMethodFeignClient = incompleteFeignClient
                .values().stream().flatMap(Collection::stream).collect(Collectors.toList());
        writeDbData4FeignClientList.addAll(noReqMethodFeignClient);


        // 写入Spring Controller剩余信息
        writeDbHandler4SpringController.insertDb(writeDbData4SpringControllerList);
        // 写入feignClient的剩余信息
        writeDbHandler4FeignClient.insertDb(writeDbData4FeignClientList);
    }

    //
    public void setClassRequestMappingMap(Map<String, List<String>> classRequestMappingMap) {
        this.classRequestMappingMap = classRequestMappingMap;
    }

    public void setWriteDbHandler4SpringController(WriteDbHandler4SpringController writeDbHandler4SpringController) {
        this.writeDbHandler4SpringController = writeDbHandler4SpringController;
    }

    public void setSpringControllerMethodHashSet(Set<String> springControllerMethodHashSet) {
        this.springControllerMethodHashSet = springControllerMethodHashSet;
    }

    public void setWithAnnotationMethodHashSet(Set<String> withAnnotationMethodHashSet) {
        this.withAnnotationMethodHashSet = withAnnotationMethodHashSet;
    }

    public void setFeignClientClassMap(Map<String, Map<String, String>> feignClientClassMap) {
        this.feignClientClassMap = feignClientClassMap;
    }

    public void setWriteDbHandler4FeignClient(WriteDbHandler4FeignClient writeDbHandler4FeignClient) {
        this.writeDbHandler4FeignClient = writeDbHandler4FeignClient;
    }
}
