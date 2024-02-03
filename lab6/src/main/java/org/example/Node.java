package org.example;

import java.util.ArrayList;

public class Node {
    ArrayList<Node> nextNodes;
    int value;

    public Node(int val){
        this.nextNodes = new ArrayList<>();
        value = val;
    }

    public void addNextNode(Node node){
        this.nextNodes.add(node);
    }

    public ArrayList<Node> getNextNodes(){
        return nextNodes;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value + " ";
    }
}
