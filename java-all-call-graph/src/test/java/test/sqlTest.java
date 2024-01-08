package test;

import com.adrninistrator.jacg.common.DC;
import com.adrninistrator.jacg.common.enums.DbTableInfoEnum;
import org.junit.Test;

public class sqlTest {
    @Test
    public void sqlPrintTest(){
        String sql = "select " + "f."+ DC.FC_FULL_METHOD +" as "+DC.MC_CALLER_FULL_METHOD +",f."+DC.FC_METHOD_HASH + " as " + DC.MC_CALLER_METHOD_HASH +
                " from " + DbTableInfoEnum.DTIE_SPRING_CONTROLLER.getTableName() + " as s " +
                " inner join " + DbTableInfoEnum.DTIE_FEIGN_CLIENT.getTableName() + " as f " +
                " on " + "s." + DC.SPC_SHOW_URI+ " = f." + DC.FC_SHOW_URI +
                " and ( s." + DC.SPC_REQUEST_METHOD +" = f."+ DC.FC_REQUEST_METHOD +" or s."+DC.SPC_REQUEST_METHOD +" is null)" +
                " where " + " s." + DC.SPC_METHOD_HASH + " = ?";

        System.out.println(sql);
    }
}
