import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class CalculatorServer implements Calculator {

    public int add(int a, int b) { return a + b; }
    public int subtract(int a, int b) { return a - b; }
    public int multiply(int a, int b) { return a * b; }

    public double divide(int a, int b) {
        if (b == 0) return Double.NaN;
        return (double) a / b;
    }

    public static void main(String[] args) {
        try {
            CalculatorServer server = new CalculatorServer();
            Calculator stub =
                    (Calculator) UnicastRemoteObject.exportObject(server, 0);

            LocateRegistry.createRegistry(1099);
            Registry registry = LocateRegistry.getRegistry();
            registry.rebind("CalcService", stub);

            System.out.println("Server is running...");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}