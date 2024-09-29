package com.adrninistrator.jacg.precisionrunner;

import com.adrninistrator.jacg.api.MethodNode;

public class TreeVistor {

    public static void printTree(MethodNode node, String prefix) {
        String strInfo = node.getCallInfo().getCallType() +"  "+node.getDomainCode() + " : " + node.getClassName() + "#" + node.getMethodName();
        System.out.println(prefix + strInfo);
        for (MethodNode child : node.getChildren()) {
            printTree(child, prefix + "    ");
        }
    }

    public static void printPrettyTree(MethodNode node, String prefix, boolean isLast){
        System.out.print(prefix);
        System.out.print(isLast ? "└── " : "├── ");
        String strInfo = node.getDomainCode() + " : " + node.getClassName() + "#" + node.getMethodName()  +"  "+ node.getCallInfo().getCallType();
        System.out.println(strInfo);

        prefix += isLast ? "    " : "│   ";
        for (int i = 0; i < node.getChildren().size(); i++) {
            printPrettyTree(node.getChildren().get(i), prefix, i == node.getChildren().size() - 1);
        }
    }
}
