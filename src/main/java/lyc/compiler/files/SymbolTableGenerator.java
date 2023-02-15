package lyc.compiler.files;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lyc.compiler.constants.Constants;

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
    
    private Map<Type, Integer> maxLenByType = Map.of(
        Type.ID, Constants.MAX_ID,
        Type.INT, Integer.toString(Constants.MAX_INT).length(),
        Type.FLOAT, Double.toString(Constants.MAX_FLOAT).length(),
        Type.STRING, Constants.MAX_STRING
    );

    public static void addSymbol(String name, Type type, String value) {
        Symbol symbol = new SymbolTableGenerator().new Symbol();
        symbol.name = name;
        symbol.type = type;
        symbol.value = value;
        symbol.longitude = value.length();
        symbolTable.add(symbol);
    }

    @Override
    public void generate(FileWriter fileWriter) throws IOException {
        fileWriter.write("Symbol Table \n");
        fileWriter.write(String.format("%-10s %-10s %-50s %-10s%n", "Name", "Type", "Value", "Longitude"));
        for (Symbol symbol : symbolTable) {
            int maxLen = maxLenByType.get(symbol.type);
            String value = symbol.value.length() > maxLen ? symbol.value.substring(0, maxLen) : symbol.value;
            fileWriter.write(String.format("%-10s %-10s %-50s %-10d%n", symbol.name, symbol.type, value, symbol.longitude));
        }
    }
}
    

