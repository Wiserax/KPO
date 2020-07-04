package com.example.testtodoapp;

public class CircularList {

    class Node {
        int value;
        Node nextNode;

        public Node(int value) {
            this.value = value;
        }
    }

    public void addNode(int value) {
        Node newNode = new Node(value);
        this.size++;

        if (head == null) {
            head = newNode;
            head.nextNode = head;
        } else if (tail == null) {
            tail = newNode;
            tail.nextNode = head;
            head.nextNode = tail;
        } else {
            tail.nextNode = newNode;
            tail = newNode;
            tail.nextNode = head;
        }
    }

    public int getValue(int position) {
        Node iterator = head;
        for (int i = 0; i < position - 1; i++) {
            iterator = iterator.nextNode;
            int tmp = iterator.value;
            int tmp2 = iterator.value;
        }
        return iterator.value;
    }

    public int getSize() {
        return this.size;
    }

    private Node head = null;
    private Node tail = null;
    private int size = 0;
}
