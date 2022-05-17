package com.bobocode;

import java.util.Objects;
import java.util.Stack;

public class DemoApp {
    public static void main(String[] args) {
        var head = createLinkedList(4, 3, 9, 1);
        printReversedRecursively(head);
        printReversedUsingStack(head);
    }

    /**
     * Creates a list of linked {@link Node} objects based on the given array of elements and returns a head of the list.
     *
     * @param elements an array of elements that should be added to the list
     * @param <T>      elements type
     * @return head of the list
     */
    @SafeVarargs
    public static <T> Node<T> createLinkedList(T... elements) {
        validateElements(elements);
        var head = new Node<>(elements[0]);
        var current = head;
        for (int i = 1; i < elements.length; i++) {
            current.next = new Node<>(elements[i]);
            current = current.next;
        }
        return head;
    }

    private static <T> void validateElements(T[] elements) {
        Objects.requireNonNull(elements);
        if (elements.length == 0) {
            throw new IllegalArgumentException("There must be at least 1 element");
        }
    }

    /**
     * Prints a list in a reserved order using a recursion technique. Please note that it should not change the list,
     * just print its elements.
     * <p>
     * Imagine you have a list of elements 4,3,9,1 and the current head is 4. Then the outcome should be the following:
     * 1 -> 9 -> 3 -> 4
     *
     * @param head the first node of the list
     * @param <T>  elements type
     */
    public static <T> void printReversedRecursively(Node<T> head) {
        printReversed(head);
        System.out.println();
    }

    private static void printReversed(Node<?> head) {
        if (head == null) {
            return;
        }
        printReversed(head.next);
        if (head.next != null) {
            System.out.print(" -> ");
        }
        System.out.print(head.element);
    }

    /**
     * Prints a list in a reserved order using a {@link java.util.Stack} instance. Please note that it should not change
     * the list, just print its elements.
     * <p>
     * Imagine you have a list of elements 4,3,9,1 and the current head is 4. Then the outcome should be the following:
     * 1 -> 9 -> 3 -> 4
     *
     * @param head the first node of the list
     * @param <T>  elements type
     */
    public static <T> void printReversedUsingStack(Node<T> head) {
        var stack = convertToStack(head);
        while (!stack.isEmpty()) {
            System.out.print(stack.pop());
            if (!stack.isEmpty()) {
                System.out.print(" -> ");
            }
        }
        System.out.println();
    }

    private static Stack<?> convertToStack(Node<?> head) {
        var stack = new Stack<>();
        while (head != null) {
            stack.push(head.element);
            head = head.next;
        }
        return stack;
    }
}
