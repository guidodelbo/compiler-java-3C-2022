package lyc.compiler.files;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class IntermediateCodeGenerator implements FileGenerator {

    public static class Triple {
        public Integer position;
        public String opcode;
        public String src;
        public String dest;
    
        public Triple(String opcode, String src, String dest) {
            this.position = triples.size();
            this.opcode = opcode;
            this.src = src;
            this.dest = dest;
        }
    }

    public static int getTripleCount() {
        return triples.size();
    }

    //Update the triple
    public static void updateTriple(Integer position, String src, String dest) {
        triples.get(position).src = src;
        triples.get(position).dest = dest;
    }

    //Update the triple
    public static void updateTriple(Integer position, String src) {
        triples.get(position).src = src;
    }

    //Triples
    public static List<Triple> triples = new ArrayList<>();

    public static Integer addTriple(Object opcode, Object src, Object dest) {
        Triple newTriple = new Triple(opcode.toString(), src.toString(), dest.toString());

        triples.add(newTriple);

        return newTriple.position;
    }

    public static Integer addTriple(Object opcode, Object src) {
        Triple newTriple = new Triple(opcode.toString(), src.toString(), "_");

        triples.add(newTriple);

        return newTriple.position;
    }

    public static Integer addTriple(Object opcode) {
        Triple newTriple = new Triple(opcode.toString(), "_", "_");

        triples.add(newTriple);
        
        return newTriple.position;
    }

    @Override
    public void generate(FileWriter fileWriter) throws IOException {
        for (Triple triple : triples) {
            fileWriter.write("[" + triple.position + "] " + "(" + triple.opcode + ", " + triple.src + ", " + triple.dest + ")\n");
        }
    }
}
