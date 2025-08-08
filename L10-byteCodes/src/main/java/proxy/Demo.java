package proxy;

public class Demo {
    public static void main(String[] args) {
        TestLoggingInterface test = Ioc.createTestLogging();

        test.calculation(6);
        test.calculation(2, 3);
        test.calculation(1, 2, "example");
    }
}
