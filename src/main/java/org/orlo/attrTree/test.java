package org.orlo.attrTree;

public class test {
    public static void main(String args[]){
        String policy = "student,computer science,3*securityLevel*,3of3,admin,*time*16:00-22:00,2of3";
        String attr = "student,4*securityLevel*,computer science,notAdmin,*time*16:30-21:30";
        Nodes p = new constructTree().parsePolicyPostfix(policy);
        String[] Attrs = new getAttr().parseAttribute(attr);
        new checkSatisfiable().checkSatisfy(p, Attrs);
        System.out.println(p.satisfiable);
    }
}
