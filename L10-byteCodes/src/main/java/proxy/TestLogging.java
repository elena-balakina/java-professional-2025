package proxy;

public class TestLogging implements TestLoggingInterface {

    @Log
    public void calculation(int param) {
        System.out.println("Calculation for one param is done: " + param);
    }

    public void calculation(int param1, int param2) {
        System.out.println("Calculation for two params is done, sum = : " + (param1 + param2));
    }

    @Log
    public void calculation(int param1, int param2, String param3) {
        System.out.println(
                "Calculation for three params is done, sum = : " + (param1 + param2) + ", param3 = " + param3);
    }
}
