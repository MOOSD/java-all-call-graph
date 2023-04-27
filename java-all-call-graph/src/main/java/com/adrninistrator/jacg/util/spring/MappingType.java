package com.adrninistrator.jacg.util.spring;

public enum MappingType {
    /*
    非SpringMVC的RequestMapping注解
     */
    NOMAPPING,
    /*
    @RequestMapping注解
     */
    MAPPING,
    /*
    组合映射注解,例如@GetMapping
     */
    COMPOSED_MAPPING,
}
