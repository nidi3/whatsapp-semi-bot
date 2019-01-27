package codeanticode.eliza;

import java.io.*;

public class Main {
    public static void main(String[] args) throws IOException {
        final Eliza eliza = new Eliza();
        final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            final String line = in.readLine();
            final String out = eliza.processInput(line);
            System.out.println(out);
        }
    }
}
