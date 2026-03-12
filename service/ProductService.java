package service;

import model.Product;
import util.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ProductService {

    private final String FILE = "products.csv";
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public List<Product> getAll() {

        List<Product> list = new ArrayList<>();
        List<String[]> data = CSVUtil.read(FILE);

        for (int i = 0; i < data.size(); i++) { // ข้าม header
            String[] row = data.get(i);

            LocalDateTime importedAt = null;

            if (!row[5].equals("null") && !row[5].isEmpty()) {
                importedAt = LocalDateTime.parse(row[5], FORMATTER);
            }

            list.add(new Product(
                    row[0],                         // id
                    row[1],                         // name
                    Double.parseDouble(row[2]),     // price
                    Double.parseDouble(row[3]),     // costPrice
                    Integer.parseInt(row[4]),       // stock
                    importedAt                      // importedAt
            ));
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
        lines.add("id,name,price,costPrice,stock,importedAt");

        for (Product p : list) {
            lines.add(p.toCSV());
        }

        CSVUtil.write(FILE, lines);
    }

    public void updateProduct(Product product) {

        List<Product> list = getAll();

        for (Product p : list) {

            if (p.getId().equals(product.getId())) {

                p.setName(product.getName());
                p.setPrice(product.getPrice());
                p.setCostPrice(product.getCostPrice());
                p.setStock(product.getStock());
                p.setImportedAt(product.getImportedAt());
            }
        }

        saveAll(list);
    }
}