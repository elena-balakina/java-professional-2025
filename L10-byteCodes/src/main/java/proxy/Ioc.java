package proxy;

import java.lang.reflect.*;

public class Ioc {

    public static TestLoggingInterface createTestLogging() {
        InvocationHandler handler = new InvocationHandler() {
            private final TestLogging original = new TestLogging();

            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                Method realMethod = original.getClass().getMethod(method.getName(), method.getParameterTypes());
                if (realMethod.isAnnotationPresent(Log.class)) {
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
