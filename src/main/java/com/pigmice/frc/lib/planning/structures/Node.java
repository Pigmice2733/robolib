package com.pigmice.frc.lib.planning.structures;

import java.util.LinkedList;
import java.util.List;

public class Node<T> {
    private T data;
    private Node<T> parent;
    private List<Node<T>> children;

    public boolean isRoot() {
        return parent == null;
    }

    public boolean isLeaf() {
        return children.size() == 0;
    }

    public Node(T data) {
        this.data = data;
        this.children = new LinkedList<>();
    }

    public Node<T> addChild(T child) {
        Node<T> node = new Node<>(child);
        node.parent = this;
        this.children.add(node);
        return node;
    }
}
