package net.traitors.util;

import java.util.HashSet;
import java.util.Set;

public class TreeNode<T> {

    private Set<TreeNode<T>> nodes = new HashSet<TreeNode<T>>();
    private T payload;

    TreeNode(T payload) {
        this.payload = payload;
    }

    void addChild(TreeNode<T> child) {
        nodes.add(child);
    }

    public Set<TreeNode<T>> getChildren() {
        return nodes;
    }

    public T getPayload() {
        return payload;
    }

}
