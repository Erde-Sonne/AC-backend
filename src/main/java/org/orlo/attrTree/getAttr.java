package org.orlo.attrTree;
//*****用户属性*******//
public class getAttr {
    public static String[] parseAttribute(String s) {
        String res[];
        res = s.split(",");
        return res;
    }
}
