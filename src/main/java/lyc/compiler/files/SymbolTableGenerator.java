package lyc.compiler.files;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SymbolTableGenerator implements FileGenerator{

    private static List<Symbol> symbolTable = new ArrayList<Symbol>();

    public static enum Type {
        ID,
        INT,
        FLOAT,
        STRING
    };

    class Symbol {
        String name;
        Type type;
        String value;
        Integer longitude;
    }

    public static void addSymbol(String name, Type type, String value) {
        Symbol symbol = new SymbolTableGenerator().new Symbol();
        symbol.name = name;
        symbol.type = type;
        symbol.value = value;
        symbol.longitude = value.length();
        symbolTable.add(symbol);
    }

    //Generates the symbol table to a file using a FileWriter as input
    @Override
    public void generate(FileWriter fileWriter) throws IOException {
        fileWriter.write("Symbol Table");
        fileWriter.write("Name\tType\tValue\tLongitude");
        for (Symbol symbol : symbolTable) {
            fileWriter.write(symbol.name + "\t" + symbol.type + "\t" + symbol.value + "\t" + symbol.longitude);
        }
    }
}
    

