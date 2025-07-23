package ru.otus.annotations;

import ru.otus.annotations.testAnnotations.After;
import ru.otus.annotations.testAnnotations.Before;
import ru.otus.annotations.testAnnotations.Test;

public class TestsWithCustomAnnotations {

    @Before
    public void before1() {
        System.out.println("Before each 1");
    }

    @Before
    public void before2() {
        System.out.println("Before each 2");
    }

    @Test
    public void test1() {
        System.out.println("Test 1 passed");
    }

    @Test
    public void test2() {
        System.out.println("Test 2 passed");
    }

    @Test
    public void test3() {
        throw new RuntimeException("Test 3 failed");
    }

    @Test
    public void test4() {
        throw new RuntimeException("Test 4 failed");
    }

    @After
    public void after1() {
        System.out.println("After each 1");
    }

    @After
    public void after2() {
        System.out.println("After each 2");
    }
}
