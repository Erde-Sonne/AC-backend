package org.orlo.attrTree;

public class Nodes {
    int k;  // only used in root node

    String attr;  //null if this node is root node
    Nodes[] children;
    public boolean satisfiable;
}

