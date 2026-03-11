package util;

import java.io.*;
import java.util.*;

public class CSVUtil {

    public static List<String[]> read(String file) {
        List<String[]> data = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            br.readLine(); // skip header

            while ((line = br.readLine()) != null) {
                data.add(line.split(","));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
    }

    public static void write(String file, List<String> lines) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            for (String s : lines) {
                pw.println(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
