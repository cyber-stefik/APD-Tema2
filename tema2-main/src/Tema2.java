import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.concurrent.ForkJoinPool;

public class Tema2 {
    public static void main(String[] args) throws IOException,
            InterruptedException {
        // Parsarea datelor de intrare
        String inputFolder = new String(args[0]);
        String orders = new String(inputFolder + "/orders.txt");
        String order_products = new String(inputFolder + "/order_products.txt");
        int ordersSize = (int) Files.size(Path.of(orders));
        int P = Integer.parseInt(args[1]);

        // BufferedWritere si BufferedReader
        BufferedWriter orders_out = new BufferedWriter(new FileWriter("orders_out.txt"));
        BufferedWriter order_products_out = new BufferedWriter(new FileWriter("order_products_out.txt"));
        BufferedReader orders_in = new BufferedReader(new FileReader(orders));

        // ForkJoinPool
        ForkJoinPool forkJoinPool = new ForkJoinPool(P);
        // Threadurile de nivel 1
        ArrayList<OrderThread> orderThreads = new ArrayList<OrderThread>(P);

        for (int i = 0; i < P; i++) {
            // Zona pe care o proceseaza fiecare thread
            int start = (ordersSize / P * i);
            int end = (ordersSize / P * (i + 1));
            orderThreads.add(new OrderThread(orders, order_products, P,
                    start, end,
                    forkJoinPool, orders_out, order_products_out, orders_in));
            // Pornesc threadurile
            orderThreads.get(i).start();
        }

        // Join la threaduri
        for (int i = 0; i < P; i++) {
            try {
                orderThreads.get(i).join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // Inchidere fisiere si oprire ForkJoinPool
        orders_out.close();
        order_products_out.close();
        orders_in.close();
        forkJoinPool.shutdown();
    }
}