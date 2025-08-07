package proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.Set;

public class Ioc {

    public static TestLoggingInterface createTestLogging() {
        TestLogging original = new TestLogging();
        Set<String> loggableMethodSignatures = new HashSet<>();

        for (Method method : original.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(Log.class)) {
                loggableMethodSignatures.add(buildSignature(method));
            }
        }

        InvocationHandler handler = new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                String signature = buildSignature(method);
                if (loggableMethodSignatures.contains(signature)) {
                    StringBuilder log = new StringBuilder("executed method: " + method.getName());
                    if (args != null && args.length > 0) {
                        for (int i = 0; i < args.length; i++) {
                            log.append(i == 0 ? ", param" : ", param" + (i + 1))
                                    .append(": ")
                                    .append(args[i]);
                        }
                    }
                    System.out.println(log);
                }
                return method.invoke(original, args);
            }
        };
        return (TestLoggingInterface) Proxy.newProxyInstance(
                Ioc.class.getClassLoader(), new Class<?>[] {TestLoggingInterface.class}, handler);
    }

    /**
     * Builds a unique string signature for the given method based on its name and parameter types.
     * This signature can be used to identify and compare methods across different classes/interfaces.
     *
     * @param method the method to generate a signature for
     * @return a string representing the method's signature in the format: methodName|paramType1|paramType2...
     */
    private static String buildSignature(Method method) {
        StringBuilder sb = new StringBuilder(method.getName());
        for (Class<?> paramType : method.getParameterTypes()) {
            sb.append("|").append(paramType.getName());
        }
        return sb.toString();
    }
}
