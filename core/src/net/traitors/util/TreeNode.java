package net.traitors.util;

import java.util.HashSet;
import java.util.Set;

public class TreeNode {

    private Set<TreeNode> nodes = new HashSet<TreeNode>();
    private Overlapper.RotRec rotRet;

    public TreeNode(Overlapper.RotRec rotRec) {
        this.rotRet = rotRec;
    }

    public void addChild(TreeNode child) {
        nodes.add(child);
    }

    public Set<TreeNode> getChildren() {
        return nodes;
    }

    public Overlapper.RotRec getRotRet() {
        return rotRet;
    }

    public net.traitors.thing.Thing getThing() {
        return rotRet.thing;
    }

}
