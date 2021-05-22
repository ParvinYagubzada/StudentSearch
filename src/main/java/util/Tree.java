package util;

import app.Student;

import java.util.Comparator;

public class Tree <T extends Student> {
    private Node<T> root;
    private final Comparator<T> comparator;

    public Tree(Comparator<T> comparator) {
        this.root = null;
        this.comparator = comparator;
    }

    public void inorder() {
        inorderRecursive(root);
    }

    void inorderRecursive(Node<T> root) {
        if (root != null) {
            inorderRecursive(root.getLeft());
            System.out.println(root.getData());
            inorderRecursive(root.getRight());
        }
    }

    public void insert(T data)  {
        root = insertRecursive(root, data);
    }

    Node<T> insertRecursive(Node<T> root, T data) {
        if (root == null) {
            root = new Node<>(data);
            return root;
        }

        if (comparator.compare(data, root.getData()) < 0)     //insert in the left subtree
            root.setLeft(insertRecursive(root.getLeft(), data));
        if (comparator.compare(data, root.getData()) >= 0)    //insert in the right subtree
            root.setRight(insertRecursive(root.getRight(), data));

        return root;
    }

    public void search(String word, SearchType type)  {
        searchRecursive(root, word, type);
    }

    Node<T> searchRecursive(Node<T> root, String word, SearchType type)  {
        if (root == null)
            return null;

        if (root.getData().get(type).startsWith(word)) {
            System.out.println(root.getData());
            searchRecursive(root.getRight(), word, type);
        }

        if (root.getData().get(type).compareTo(word) >= 0)
            return searchRecursive(root.getLeft(), word, type);
        return searchRecursive(root.getRight(), word, type);
    }
}