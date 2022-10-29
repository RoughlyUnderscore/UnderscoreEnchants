package com.roughlyunderscore.enchs.util;

import lombok.Data;

@Data
public class Triple<A, B, C> {
    private final A a;
    private final B b;
    private final C c;

    public static <A, B, C> Triple<A, B, C> of(A a, B b, C c) {
        return new Triple<>(a, b, c);
    }
}
