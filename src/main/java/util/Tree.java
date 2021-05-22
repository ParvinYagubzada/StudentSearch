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

    /**
     * Prints all elements of Tree.
     *
     * @param root top Node of tree.
     **/
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

    /**
     * Inserts data (Node) to their place.
     * If current Node is null that means you cannot go left or right direction.
     * In other words you are reached place where you wanted to be.
     * If current Node is not null then we compare new data with current Node data if it is equal or larger than
     * current Node data then next current Node will be right Node of current.
     *
     * @param root current Node.
     * @param data new data that we want to insert.
     **/
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

    /**
     * If current node meets our search condition then we will print it and search next occurrence
     * on Right branch of current. That is because we were placing same or alphabetically larger
     * Students on right side of Nodes. This means you will skip every left branch of Nodes if you
     * find at least 1 occurrence.
     *
     * @param root current Node.
     * @param word that user wants to search.
     * @param type search type which indicates how search operated on Students tree.
     **/
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