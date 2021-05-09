package woolfel.rulebenchmark;

import java.io.FileWriter;
import java.io.IOException;

public class ParserTestGenerator {

    public static final String fact = "myObject";
    public static final String ASSERT = "assert";
    
    public ParserTestGenerator() {
    }

    public String generateFact(int counter) {
        return "(" + ASSERT + " (" + fact + " (attribute1 \"attr" + counter + "\") ) )\r\n";
    }
    
    public String generateLoadFact(int counter) {
        return "(" + fact + " (attribute1 \"attr" + counter + "\") )\r\n";
    }
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args == null || args.length < 2) {
            System.out.println("No parameters were provided.");
            System.out.println("The parameters are:");
            System.out.println("number facts");
            System.out.println("filename");
        } else {
            int count = Integer.parseInt(args[0]);
            String filename = args[1];
            boolean load = false;
            if (args[2] != null && args[2].equals("load")) {
                load = true;
            }
            ParserTestGenerator ptg = new ParserTestGenerator();
            try {
                FileWriter writer = new FileWriter(filename);
                for (int idx=0; idx < count; idx++) {
                    if (load) {
                        writer.write(ptg.generateLoadFact(idx));
                    } else {
                        writer.write(ptg.generateFact(idx));
                    }
                }
                writer.close();
                System.out.println("Generated " + count + " facts");
            } catch (IOException e) {
                e.printStackTrace();
            }
            
        }
    }

}
