package com.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AppTest {

    @Test
    void testAdd() {
        assertEquals(5, App.add(2, 3));
    }

    @Test
    void testMultiply() {
        assertEquals(6, App.multiply(2, 3));
    }
}

