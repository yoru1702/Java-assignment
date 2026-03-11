package service;

import model.Product;
import util.*;

import java.util.*;

public class ProductService {

    private final String FILE = "products.csv";

    public List<Product> getAll() {
        List<Product> list = new ArrayList<>();

        for (String[] row : CSVUtil.read(FILE)) {
            list.add(new Product(
                    row[0],
                    row[1],
                    Double.parseDouble(row[2]),
                    Integer.parseInt(row[3])));
        }
        return list;
    }

    public Product findById(String id) {
        for (Product p : getAll()) {
            if (p.getId().equals(id))
                return p;
        }
        return null;
    }

    public void saveAll(List<Product> list) {
        List<String> lines = new ArrayList<>();
        lines.add("id,name,price,stock");

        for (Product p : list) {
            lines.add(p.toCSV());
        }

        CSVUtil.write(FILE, lines);
    }

    public void updateProduct(Product product) {

        List<String[]> data = CSVUtil.read("products.csv");

        List<String> lines = new ArrayList<>();

        // header
        lines.add("id,name,price,stock");

        for (String[] row : data) {

            if (row[0].equals(product.getId())) {

                row[1] = product.getName();
                row[2] = String.valueOf(product.getPrice());
                row[3] = String.valueOf(product.getStock());
            }

            lines.add(String.join(",", row));
        }

        CSVUtil.write("products.csv", lines);
    }
}
