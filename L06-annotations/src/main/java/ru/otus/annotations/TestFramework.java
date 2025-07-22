package ru.otus.annotations;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import ru.otus.annotations.testAnnotations.After;
import ru.otus.annotations.testAnnotations.Before;
import ru.otus.annotations.testAnnotations.Test;
import ru.otus.reflection.ReflectionHelper;

public class TestFramework {
    public static void run(String className) {
        int passed = 0;
        int failed = 0;

        try {
            Class<?> testClass = Class.forName(className);
            List<Method> beforeMethods = new ArrayList<>();
            List<Method> testMethods = new ArrayList<>();
            List<Method> afterMethods = new ArrayList<>();

            for (Method method : testClass.getDeclaredMethods()) {
                if (method.isAnnotationPresent(Before.class)) {
                    beforeMethods.add(method);
                } else if (method.isAnnotationPresent(Test.class)) {
                    testMethods.add(method);
                } else if (method.isAnnotationPresent(After.class)) {
                    afterMethods.add(method);
                }
            }

            for (Method testMethod : testMethods) {
                Object testInstance = ReflectionHelper.instantiate(testClass);
                boolean testPassed = true;
                boolean beforeFailed = false;

                System.out.println("Running test: " + testMethod.getName());

                try {
                    for (Method before : beforeMethods) {
                        ReflectionHelper.callMethod(testInstance, before.getName());
                    }
                } catch (Exception e) {
                    System.out.println("Error in @Before for test '" + testMethod.getName() + "': " + e.getCause());
                    testPassed = false;
                    beforeFailed = true;
                }

                // Run @Test only if @Before didn't fail
                if (!beforeFailed) {
                    try {
                        ReflectionHelper.callMethod(testInstance, testMethod.getName());
                    } catch (Exception e) {
                        testPassed = false;
                        System.out.println("Test '" + testMethod.getName() + "' failed: " + e.getCause());
                    }
                }

                for (Method after : afterMethods) {
                    try {
                        ReflectionHelper.callMethod(testInstance, after.getName());
                    } catch (Exception e) {
                        System.out.println("Error in @After for test '" + testMethod.getName() + "': " + e.getCause());
                    }
                }

                if (testPassed && !beforeFailed) {
                    passed++;
                    System.out.println("Test '" + testMethod.getName() + "' passed");
                } else {
                    failed++;
                }
            }

            // Summary
            System.out.println("------------------------------");
            System.out.println("Testing finished:");
            System.out.println("Total tests number: " + testMethods.size());
            System.out.println("Passed: " + passed);
            System.out.println("Failed: " + failed);
            System.out.println("------------------------------");

        } catch (Exception e) {
            System.out.println("Error while launching test framework: " + e.getMessage());
        }
    }
}
