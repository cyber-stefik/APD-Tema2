import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class OrderThread extends Thread {
    public String orders;
    public String order_products;
    public int P;
    public int start;
    public int end;
    public ForkJoinPool forkJoinPool;
    public BufferedWriter orders_out;
    public BufferedWriter order_products_out;
    public BufferedReader orders_in;
    public ArrayList<String> processedOrders = new ArrayList<>();
    ArrayList<ProductTask> productTasks = new ArrayList<>();

    public OrderThread(String orders, String order_products, int p, int start
            , int end, ForkJoinPool forkJoinPool, BufferedWriter orders_out,
                       BufferedWriter order_products_out, BufferedReader orders_in) {
        this.orders = orders;
        this.order_products = order_products;
        this.P = p;
        this.start = start;
        this.end = end;
        this.forkJoinPool = forkJoinPool;
        this.orders_out = orders_out;
        this.order_products_out = order_products_out;
        this.orders_in = orders_in;
    }

    @Override
    public void run() {
        try {
            String line = "";
            while ((line = orders_in.readLine()) != null) {
                String[] processOrder = line.split(",");
                // Daca numarul de produse e diferit de 0, pornesc un task
                if (Integer.parseInt(processOrder[1]) != 0) {
                    ProductTask productTask = new ProductTask(processOrder[0],
                            Integer.parseInt(processOrder[1]), this.order_products,
                            this.order_products_out);
                    productTasks.add(productTask);
                    productTask.fork();
                    processedOrders.add(line);
                }
            }
            for (ProductTask task : productTasks) {
                task.join();
            }
            // Adaug ,shipped la comenzile care au avut un numar de produse diferit de 0
            for (String order : processedOrders) {
                orders_out.write(order + ",shipped\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
