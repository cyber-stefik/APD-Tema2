import java.io.*;
import java.util.concurrent.Callable;
import java.util.concurrent.RecursiveTask;

public class ProductTask extends RecursiveTask<Void> {
    public String orderID;
    public int numberOfProducts;
    public String order_products;
    public BufferedWriter order_products_out;

    public ProductTask(String orderID, int numberOfProducts, String order_products,
                       BufferedWriter order_products_out) {
        this.orderID = orderID;
        this.numberOfProducts = numberOfProducts;
        this.order_products = order_products;
        this.order_products_out = order_products_out;
    }

    @Override
    protected Void compute() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(this.order_products));
            // Procesez pana numarul de produse e 0
            while (numberOfProducts > 0) {
                String line;
                line = reader.readLine();
                String[] processProduct = line.split(",");
                // Verific id-ul comenzii
                if (processProduct[0].equals(orderID)) {
                    order_products_out.write(processProduct[0] + "," + processProduct[1] + ",shipped\n");
                    numberOfProducts--;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
