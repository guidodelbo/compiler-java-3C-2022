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
        boolean isDeclared;
        
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
        symbol.isDeclared = false;

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

    //UpdateVariablesType receives a list of variables and updates the value in SymbolTable
    public static void UpdateVariablesType(List<String> variables, Type type) {
        for (String variable : variables) {
            for (Symbol symbol : symbolTable) {
                if (isDeclared(variable)){
                    System.out.println("Error: Variable " + variable + " ya ha sido declarada");
                    System.exit(1);
                }

                if (symbol.name.equals(variable)) {
                    symbol.type = type;
                    symbol.isDeclared = true;
                    break;
                }
            }
        }
    }

    public static void checkSymbol(String symbolName, Type type){
        if(isDeclared(symbolName))
        {
            Symbol symbolToValidate = symbolTable.stream().filter(symbol -> symbolName.equals(symbol.name))
            .findFirst()
            .orElseThrow(RuntimeException::new);
            
            if(symbolToValidate.type != type) {
                System.out.println("Error: Error de tipo de dato en " + symbolName + ".");
                System.exit(1);   
            }
        }
    }

    public static void checkSymbol(Object symbolName, Type type){
        checkSymbol((String) symbolName, type);
    }

    public static void validateVariableDeclared(String variable) {
        if (!isDeclared(variable)){
            System.out.println("Error: Variable " + variable + " no ha sido declarada");
            System.exit(1);
        }
    }

    public static void validateVariableDeclared(Object variable) {
        validateVariableDeclared((String) variable);
    }

    public static boolean isDeclared(String symbolName){
        return symbolTable.stream().filter(symbol -> symbolName.equals(symbol.name)).findFirst().orElse(null).isDeclared;
    }

    public static boolean isDeclared(Object symbolName){
        return isDeclared((String) symbolName);
    }

    public static List<Symbol> get_symbolTable(){
       return symbolTable;
    }

    public static Symbol get_symbol_fromTable(Object lexeme){
        String str_lexeme = lexeme.toString();
        String val = str_lexeme.replace(".","_");
        val = val.replace("-","_");

        for(Symbol s: symbolTable){
            if(s.name.equals(str_lexeme) || s.name.equals(val)){
                return s;
            }
        }
        return null;
    }
            
    
}
    

