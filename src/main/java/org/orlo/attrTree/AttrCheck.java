package org.orlo.attrTree;


import org.orlo.entity.UserVerify;

public class AttrCheck {
    private String policy;  //属性树
    private String attr;  //用户属性

    public void getUserAttr(UserVerify usr){
        String Attrs = usr.getDevice()+","
                +  usr.getDepartment()+",访问时段："
                +  usr.getTime()+",安全级别："
                +  usr.getSafe()+","
                +  usr.getType()+","
                +  usr.getIp()+","
                +  usr.getMAC()+","
                +  usr.getSwitcher()+","
                +  usr.getPort();
        this.attr = Attrs;
    }

    public boolean Check(){
        Nodes p = new constructTree().parsePolicyPostfix(policy);
        String[] Attrs = new getAttr().parseAttribute(attr);

        new checkSatisfiable().checkSatisfy(p, Attrs);
        return  p.satisfiable;
    }

    public void setPolicy(String policy) {
        this.policy = policy;
    }
}
