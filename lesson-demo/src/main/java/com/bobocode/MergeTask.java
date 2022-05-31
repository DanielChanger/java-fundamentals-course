package com.bobocode;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.RecursiveTask;

public class MergeTask<T extends Comparable<? super T>> extends RecursiveTask<List<T>> {

    private final List<T> elements;

    public MergeTask(List<T> elements) {
        Objects.requireNonNull(elements);
        this.elements = elements;
    }

    @Override
    public List<T> compute() {
        if (elements.size() <= 1) {
            return elements;
        }

        var left = new MergeTask<>(new ArrayList<>(elements.subList(0, elements.size() / 2)));
        var right = new MergeTask<>(new ArrayList<>(elements.subList(elements.size() / 2, elements.size())));
        right.fork();

        return merge(
                left.compute(),
                right.join());
    }

    private List<T> merge(List<T> left, List<T> right) {
        int index = 0;
        int leftIndex = 0;
        int rightIndex = 0;

        while (leftIndex < left.size() && rightIndex < right.size()) {
            T leftElement = left.get(leftIndex);
            T rightElement = right.get(rightIndex);
            if (leftElement.compareTo(rightElement) <= 0) {
                elements.set(index, leftElement);
                leftIndex++;
            } else {
                elements.set(index, rightElement);
                rightIndex++;
            }
            index++;
        }

        while (leftIndex < left.size()) {
            elements.set(index++, left.get(leftIndex++));
        }

        while (rightIndex < right.size()) {
            elements.set(index++, right.get(rightIndex++));
        }

        return elements;
    }
}
