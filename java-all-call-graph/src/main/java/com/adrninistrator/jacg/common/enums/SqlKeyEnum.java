package com.adrninistrator.jacg.common.enums;

/**
 * @author adrninistrator
 * @date 2022/12/7
 * @description: 用于缓存sql语句的key
 */
public enum SqlKeyEnum {
    CN_QUERY_DUPLICATE_CLASS,
    CN_QUERY_DUPLICATE_CLASS_BEFORE_UPDATE,
    CN_QUERY_SIMPLE_CLASS,
    CN_QUERY_CLASS,
    CN_UPDATE_SIMPLE_2_FULL,
    MC_QUERY_CALLER_FULL_METHOD,
    MC_QUERY_TOP_METHOD,
    MC_QUERY_ONE_CALLEE,
    MC_QUERY_ONE_CALLEE_CHECK_LINE_NUM,
    MC_QUERY_CALLEE_ALL_METHODS,
    MC_QUERY_CALLER_ALL_METHODS,
    MC_QUERY_ONE_CALLER1,
    MC_QUERY_ONE_RPC1,
    MC_QUERY_ONE_CALLER2,
    MC_QUERY_NOTICE_INFO,
    MC_QUERY_ALL_CALLER,
    MC_QUERY_CALLEE_SEQ_IN_CALLER,
    MC_QUERY_CALLER_FULL_METHOD_BY_HASH,
    MC_QUERY_CALLEE_FULL_METHOD_BY_HASH,
    MC_QUERY_CALLEE_FULL_METHOD_BY_ID,
    MC_QUERY_METHOD_CALL_BY_CALLEE_HASH_OBJ_TYPE,
    MC_QUERY_MAX_CALL_ID,
    MC_QUERY_CHECK_NORMAL_MC_BY_EE_HASH,
    MC_QUERY_FLAG_4EE,
    MC_QUERY_FLAG_4ER,
    MC_QUERY_FLAG_BY_ID,
    MC_QUERY_MC_PAIR_BY_CALLEE,
    MC_QUERY_BY_PAGE_MAX_CALL_ID,
    MC_QUERY_CALL_ID_BY_CALLEE,
    MC_QUERY_CALL_ID_BY_CALLEE_LAST,
    MC_UPDATE_ENABLED,
    MC_UPDATE_FLAGS,
    MI_QUERY_METHOD_HASH,
    MI_QUERY_SIMPLE_CLASS_NAME,
    MI_QUERY_FLAGS,
    MI_QUERY_INFO_BY_CLASSNAME,
    MI_QUERY_BY_CLASS_METHOD,
    MI_QUERY_BY_RETURN_TYPE,
    MLN_QUERY_METHOD_HASH,
    MLN_QUERY_METHOD,
    MRGT_QUERY_BY_RETURN_TYPE,
    JI_QUERY_JAR_INFO,
    MA_QUERY_FMAH_WITH_ANNOTATIONS,
    MA_QUERY_FMAH_WITH_ANNOTATIONS_OF_CLASS,
    MA_QUERY_FULL_METHOD_WITH_ANNOTATIONS,
    MA_QUERY_ANNOTATION_BY_METHOD_HASH,
    MA_QUERY_ALL_ATTRIBUTES,
    MA_QUERY_SINGLE_ATTRIBUTE_BY_METHOD_HASH,
    CA_QUERY_SIMPLE_CLASS_NAME_WITH_ANNOTATION,
    CA_QUERY_CLASS_NAME_WITH_ANNOTATION,
    CA_QUERY_ANNOTATIONS_BY_SIMPLE_CLASS_NAME,
    CA_QUERY_ONE_ANNOTATION_BY_SIMPLE_CLASS_NAME,
    CA_QUERY_ATTRIBUTE_BY_SIMPLE_CLASS_NAME,
    CI_QUERY_ACCESS_FLAGS,
    BD_QUERY_BUSINESS_DATA,
    BD_QUERY_METHOD_BY_BUSINESS_DATA,
    BD_DELETE_BY_TYPE,
    EI_QUERY_DOWNWARD,
    EI_QUERY_UPWARD,
    CSEI1_QUERY_SUPER_INTERFACE_CLASS_NAME,
    CSEI1_QUERY_SIGNATURE_CLASS_NAME,
    MCI_QUERY_VALUE,
    LMI_QUERY_BY_PAGE_MAX_CALL_ID,
    LMI_QUERY_CALLEE_INFO,
    MMT_QUERY_TABLE,
    MMWT_QUERY_TABLE,
    ACP_QUERY,
    MAGT_QUERY,
    MAGT_QUERY_BY_ARG_TYPE,
    MAT_QUERY_BY_ARG_TYPE,
    SPC_QUERY,
    SPC_QUERY_BY_METHOD,
    SPC_QUERY_INFO_BY_METHOD,
    SPT_QUERY,
    SPT_QUERY_BY_CLASS_METHOD,
}
