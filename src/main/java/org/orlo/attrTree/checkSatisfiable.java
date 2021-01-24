package org.orlo.attrTree;
//********检验是否符合属性树*********//
public class checkSatisfiable {
    public static void checkSatisfy(Nodes p, String[] Attrs) {
        int i, l;
        int requireLevel, userLevel ;
        String prvAttr;

        p.satisfiable = false;
        if (p.children == null || p.children.length == 0) {   //如果是叶子节点，匹配存在的属性
            for (i = 0; i < Attrs.length; i++) {   //在属性集的范围内
                prvAttr = Attrs[i];   //获取属性
                if(prvAttr.contains("*securityLevel*") && p.attr.contains("*securityLevel*")){
                    userLevel = Integer.parseInt(prvAttr.substring(0,1));
                    requireLevel = Integer.parseInt(p.attr.substring(0,1));
                    if(userLevel >= requireLevel){
                        p.satisfiable = true;
                        break;
                    }
                }else if(prvAttr.contains("*time*") && p.attr.contains("*time*")){
                    timeCheck check = new timeCheck();
                    if(check.checkTime(prvAttr, p.attr)){
                        p.satisfiable = true;
                        break;
                    }
                }else if (prvAttr.compareTo(p.attr) == 0) {   //匹配成功，则叶子节点的satisfiable为真
                    p.satisfiable = true;
                    break;
                }
            }
        } else {
            for (i = 0; i < p.children.length; i++)  //如果是根节点，递归
                checkSatisfy(p.children[i], Attrs);

            l = 0;
            for (i = 0; i < p.children.length; i++) //判断孩子节点匹配个数是否大于门限值
                if (p.children[i].satisfiable)
                    l++;

            if (l >= p.k)  //大于则根节点的satisfiable为真
                p.satisfiable = true;
        }

    }
}
