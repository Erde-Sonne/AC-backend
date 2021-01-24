package org.orlo.attrTree;

import java.util.ArrayList;

//*******构造属性树********//
public class constructTree {
    public static Nodes parsePolicyPostfix(String s) {
        String[] toks;
        String tok;
        ArrayList<Nodes> stack = new ArrayList<Nodes>();
        Nodes root;

        toks = s.split(",");

        int toks_cnt = toks.length;
        for (int index = 0; index < toks_cnt; index++) {
            int i, k, n;
            tok = toks[index];

            if (!tok.contains("of")) {   //leaf node
                stack.add(baseNode(1, tok));
            } else {
                Nodes node;
                /* parse kof n node */
                String[] k_n = tok.split("of");
                k = Integer.parseInt(k_n[0]);
                n = Integer.parseInt(k_n[1]);

                if (k < 1) {
                    System.out.println("error parsing " + s
                            + ": trivially satisfied operator " + tok);
                    return null;
                } else if (k > n) {
                    System.out.println("error parsing " + s
                            + ": unsatisfiable operator " + tok);
                    return null;
                } else if (n == 1) {
                    System.out.println("error parsing " + s
                            + ": indentity operator " + tok);
                    return null;
                } else if (n > stack.size()) {
                    System.out.println("error parsing " + s
                            + ": stack underflow at " + tok);
                    return null;
                }

                /* pop n things and fill in children */
                node = baseNode(k, null);  //root node
                node.children = new Nodes[n];

                for (i = n - 1; i >= 0; i--)
                    node.children[i] = stack.remove(stack.size() - 1);

                /* push result */
                stack.add(node);
            }
        }

        if (stack.size() > 1) {
            System.out.println("error parsing " + s
                    + ": extra node left on the stack");
            return null;
        } else if (stack.size() < 1) {
            System.out.println("error parsing " + s + ": empty policy");
            return null;
        }

        root = stack.get(0);
        return root;
    }

    private static Nodes baseNode(int k, String s) {
        Nodes p = new Nodes();

        p.k = k;
        if (!(s == null))
            p.attr = s;
        else
            p.attr = null;

        return p;
    }
}
