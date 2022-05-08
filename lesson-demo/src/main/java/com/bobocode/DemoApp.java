package com.bobocode;

import java.util.*;

public class DemoApp {
    public static void main(String[] args) {
        String s = DemoApp.newList();
    }

    private static <T extends List<T>> T newList() {
        return (T) new ArrayList();
    }
}


