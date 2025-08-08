package proxy;

import java.lang.reflect.*;
import java.util.*;

public class Ioc {

    public static TestLoggingInterface createTestLogging() {
        TestLogging original = new TestLogging();
        Set<Method> loggableMethods = new HashSet<>();

        for (Method interfaceMethod : TestLoggingInterface.class.getMethods()) {
            try {
                Method implMethod =
                        original.getClass().getMethod(interfaceMethod.getName(), interfaceMethod.getParameterTypes());
                if (implMethod.isAnnotationPresent(Log.class)) {
                    loggableMethods.add(interfaceMethod);
                }
            } catch (NoSuchMethodException e) {
            }
        }

        InvocationHandler handler = new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                if (loggableMethods.contains(method)) {
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
}
