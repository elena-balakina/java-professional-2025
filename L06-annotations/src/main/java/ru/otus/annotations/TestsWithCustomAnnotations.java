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
    public void test1Positive() {
        System.out.println("Test 1 positive test");
    }

    @Test
    public void test2Positive() {
        System.out.println("Test 2 positive test");
    }

    @Test
    public void test3Negative() {
        System.out.println("Test 3 negative test");
    }

    @Test
    public void test4Negative() {
        System.out.println("Test 4 negative test");
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
