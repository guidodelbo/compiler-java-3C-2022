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
        CTE_INT,
        CTE_FLOAT,
        CTE_STRING
    };

    class Symbol {
        String name;
        Type type;
        String value;
        Integer longitude;
        
        //equals
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Symbol symbol = (Symbol) obj;
            return name.equals(symbol.name) && type == symbol.type && value.equals(symbol.value) && longitude.equals(symbol.longitude);
        }

        //hashCode
        @Override
        public int hashCode() {
            int result = 17;
            result = 31 * result + name.hashCode();
            result = 31 * result + type.hashCode();
            result = 31 * result + value.hashCode();
            result = 31 * result + longitude.hashCode();
            return result;
        }
    }
    
    private Map<Type, Integer> maxLenByType = Map.of(
        Type.ID, Constants.MAX_ID,
        Type.CTE_INT, Integer.toString(Constants.MAX_INT).length(),
        Type.CTE_FLOAT, Double.toString(Constants.MAX_FLOAT).length(),
        Type.CTE_STRING, Constants.MAX_STRING
    );

    public static void addSymbol(String name, Type type, String value) {
        Symbol symbol = new SymbolTableGenerator().new Symbol();

        symbol.name = name;
        symbol.type = type;
        symbol.value = value;
        symbol.longitude = value.length();

        if(!symbolTable.contains(symbol))
            symbolTable.add(symbol);
    }

    @Override
    public void generate(FileWriter fileWriter) throws IOException {
        fileWriter.write("Symbol Table \n");
        fileWriter.write(String.format("%-10s %-15s %-50s %-10s%n", "Name", "Type", "Value", "Longitude"));
        for (Symbol symbol : symbolTable) {
            int maxLen = maxLenByType.get(symbol.type);
            String value = symbol.value.length() > maxLen ? symbol.value.substring(0, maxLen) : symbol.value;
            fileWriter.write(String.format("%-10s %-15s %-50s %-10d%n", symbol.name, symbol.type, value, symbol.longitude));
        }
    }
}
    

