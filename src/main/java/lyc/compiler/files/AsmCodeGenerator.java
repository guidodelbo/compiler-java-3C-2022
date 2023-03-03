package lyc.compiler.files;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import lyc.compiler.files.IntermediateCodeGenerator.Triple;
import lyc.compiler.files.SymbolTableGenerator.Symbol;

public class AsmCodeGenerator implements FileGenerator {

    private static boolean __logmsg = true;
    private static String aux_sum = "__aux_sum"; //aux que uso para almacenar resultados de operaciones aritmeticas de suma
    private static String aux_res = "__aux_res"; //aux que uso para almacenar resultados de operaciones aritmeticas de resta
    private static String aux_div = "__aux_div"; //aux que uso para almacenar resultados de operaciones aritmeticas de division
    private static String aux_mul = "__aux_mul"; //aux que uso para almacenar resultados de operaciones aritmeticas de multiplicacion
    private static String aux_dec_int = "0";     //aux que uso para indicar la cantidad de decimales de las variables de tipo int
    private static String aux_dec_float = "3";   //aux que uso para indicar la cantidad de decimales de las variables de tipo float
    private static String aux_uno = "__aux_uno"; //variable que uso para representar 1
    private static String aux_figuales_pivot = "__figuales_pivot";
    private static String aux_figuales_cont = "__figuales_cont";
    private static String aux_figuales_exp = "__figuales_exp";

    private static String _header =     "include macros.asm\n"  +
    "include number.asm\n"  +
    "\n.MODEL LARGE\n"      +
    ".386\n"                +
    ".STACK 200h\n";

    private static String _end =  "\n\nmov ax,4C00h\n" +
    "int 21h\n" +
    "END main";


    private static String _code =  "\n.CODE\n"       +
    "main:\n"         +
    "mov ax,@DATA\n"  +
    "mov ds,ax\n"     +
    "mov es,ax\n";

    private static String _data =  "\n.DATA" +
    "\n" + aux_sum + " dd ?" +
    "\n" + aux_res + " dd ?" +
    "\n" + aux_mul + " dd ?" +
    "\n" + aux_div + " dd ?" +
    "\n" + aux_uno + " dd 1.0" +
    "\n" + aux_figuales_pivot + " dd ?" +
    "\n" + aux_figuales_cont + " dd ?" +
    "\n" + aux_figuales_exp + " dd ?" +
    "\n";

    @Override
    public void generate(FileWriter fileWriter) throws IOException {
        String asm = _header + 
                     _data + 
                     _code + 
                     _end;
        fileWriter.write(asm);
    }

    public static void translate_symbolTable(){
        List<Symbol> symbolTable = SymbolTableGenerator.get_symbolTable();

        for(Symbol s: symbolTable){
            if(s.type == SymbolTableGenerator.Type.CTE_STRING || s.type == SymbolTableGenerator.Type.STRING){
                _data += s.name + " db ";
            }
            else{
                _data += s.name + " dd ";
            }

            //cte
            if(s.name.startsWith("_")){
                if(s.type == SymbolTableGenerator.Type.CTE_STRING){
                    _data += "\"" + s.value + "\",0";
                }
                else if(s.type == SymbolTableGenerator.Type.CTE_INT){
                    _data += s.value + ".0";
                }
                else{
                    _data += s.value;
                }
            }
            else{
                _data += "?";
            }
            
            _data += "\n";
        }
    }

    public static void translate_intermediateCode(){
        translate_symbolTable();

        boolean cmp = false;
        List<Triple> triples = IntermediateCodeGenerator.getTriples();
        Stack<String> stack = new Stack<>();
        Symbol symbol;

        //INSERCION DE ETIQUETAS
        triples = extract_labels(triples);

        if(__logmsg){
            System.out.println("*****************");
        }

        for(Triple triple: triples){
            //System.out.println("Valor: " + s);
            switch(triple.opcode){
                case "=":
                    if(__logmsg){
                        System.out.println("Desapilando por =");
                        System.out.println(stack);
                        System.out.println("*****************");
                    }

                    symbol = SymbolTableGenerator.get_symbol_fromTable_byValue(triple.src);

                    if(symbol != null && (symbol.type == SymbolTableGenerator.Type.CTE_STRING || symbol.type == SymbolTableGenerator.Type.STRING)){
                        _code += "\nSTRCPY " + triple.src + "," + triple.dest;
                    }
                    else{
                        _code += "\nfld " + stack.pop();
                        _code += "\nfstp " + triple.src;
                    }

                break;

                case "+":
                    if(__logmsg){
                        System.out.println("Desapilando por +");
                        System.out.println(stack);
                        System.out.println("*****************");
                    }
                    _code += "\nfld " + stack.pop();
                    _code += "\nfld " + stack.pop();
                    _code += "\nfadd";
                    _code += "\nfstp " + aux_sum;
                    stack.push(aux_sum);
                break;

                case "-":
                    if(__logmsg){
                        System.out.println("Desapilando por -");
                        System.out.println(stack);
                        System.out.println("*****************");
                    }
                    _code += "\nfld " + stack.pop();
                    _code += "\nfld " + stack.pop();
                    _code += "\nfxch";
                    _code += "\nfsub";
                    _code += "\nfstp " + aux_res;
                    stack.push(aux_res);
                break;

                case "*":
                    if(__logmsg){
                        System.out.println("Desapilando por *");
                        System.out.println(stack);
                        System.out.println("*****************");
                    }
                    _code += "\nfld " + stack.pop();
                    _code += "\nfld " + stack.pop();
                    _code += "\nfmul";
                    _code += "\nfstp " + aux_mul;
                    stack.push(aux_mul);
                break;

                case "/":
                    if(__logmsg){
                        System.out.println("Desapilando por /");
                        System.out.println(stack);
                        System.out.println("*****************");
                    }
                    _code += "\nfld " + stack.pop();
                    _code += "\nfld " + stack.pop();
                    _code += "\nfxch";
                    _code += "\nfdiv";
                    _code += "\nfstp " + aux_div;
                    stack.push(aux_div);
                break;

                case "CMP":
                    if(__logmsg){
                        System.out.println("cmp");
                        System.out.println("*****************");
                    }

                    _code += "\nfld " + stack.pop();
                    _code += "\nfld " + stack.pop();
                    _code += "\nfcom";
                    _code += "\nfstsw ax";
                    _code += "\nsahf";
                    cmp = true;
                break;

                case "BI":
                    if(__logmsg){
                        System.out.println("bi");
                        System.out.println("*****************");
                    }
                    _code += "\njmp ";
                    System.out.println("ANTES BI");
                    _code += "LBL"+Integer.parseInt(triple.src.substring(1, triple.src.length()-1));
                    System.out.println("DESPUES BI");
                    break;

                case "READ":
                    if(__logmsg){
                        System.out.println("read");
                        System.out.println("*****************");
                    }

                    _code += "\ngetString " + triple.opcode;
                break;

                case "WRITE":
                    if(__logmsg){
                        System.out.println("write");
                        System.out.println("*****************");
                    }

                    symbol = SymbolTableGenerator.get_symbol_fromTable_byValue(triple.src);

                    if(symbol == null){
                        System.out.println("ERROR");
                        System.exit(0);
                    }
                    if(symbol.type == SymbolTableGenerator.Type.CTE_STRING || symbol.type == SymbolTableGenerator.Type.STRING){
                        _code += "\ndisplayString " + triple.src;
                    }
                    else {
                        _code += "\nDisplayFloat " + triple.src + "," + aux_dec_float;
                    }
                    _code += "\nnewLine\n";

                break;

                case "WHILE":
                    if(__logmsg){
                        System.out.println("while");
                        System.out.println("*****************");
                    }
                    //dummy
                break;

                default:
                    if(cmp){
                        //System.out.println(s);
                        if(triple.opcode.equals("BLE")){
                            _code += "\njna ";
                        }
                        else if(triple.opcode.equals("BGT")){
                            _code += "\nja ";
                        }
                        else if(triple.opcode.equals("BGE")){
                            _code += "\njae ";
                        }
                        else if(triple.opcode.equals("BLT")){
                            _code += "\njb ";
                        }
                        else if(triple.opcode.equals("BNE")){
                            _code += "\njne ";
                        }
                        System.out.println("Antes lbl: " + triple.src + " POS: " + triple.position);
                        _code += "LBL"+Integer.parseInt(triple.src.substring(1, triple.src.length()-1));
                        System.out.println("Depues lbl");

                        cmp = false;
                        continue;
                    }
                    if(triple.opcode.equals("LABEL")){
                        if(__logmsg){
                            System.out.println("etiqueta");
                            System.out.println("*****************");
                        }
                        _code += "\n";
                        _code += triple.src + ":";
                        continue;
                    }
 
                    System.out.println("debug");

                    symbol = SymbolTableGenerator.get_symbol_fromTable_byValue(triple.opcode);
                    
                    String valueForStack = triple.opcode;
                    
                    System.out.println("Entro por " + triple.opcode);
                    if(symbol.type == SymbolTableGenerator.Type.CTE_STRING
                        || symbol.type == SymbolTableGenerator.Type.CTE_FLOAT
                        || symbol.type == SymbolTableGenerator.Type.CTE_INT)
                    {
                        valueForStack = "_" + valueForStack;
                    }
                    System.out.println("Pusheo " + valueForStack + " en la pila");

                    stack.push(valueForStack);
                break;
            }
        }
    }

    private static List<Triple> extract_labels(List<Triple> triples) {
        List<Triple> result = new ArrayList<Triple>();

        List<Integer> labels = new ArrayList<Integer>();

        for(Triple triple : triples){
            if(triple.opcode.equals("BLE")
            || triple.opcode.equals("BGE")
            || triple.opcode.equals("BNE")
            || triple.opcode.equals("BGT")
            || triple.opcode.equals("BLT")
            || triple.opcode.equals("BI"))
            {
                System.out.println(triple.src);
                int index = Integer.parseInt(triple.src.substring(1, triple.src.length()-1));
                labels.add(index);
            }
        }

        Integer count = 0;
        for(Triple triple : triples) {
            if(labels.contains(triple.position))
            {
                result.add(new Triple(count ++, "LABEL", "LBL" + triple.position, ""));
            }
            result.add(new Triple(count ++, triple.opcode, triple.src, triple.dest));
        }
        
        System.out.println("PRINTING RESULT ***********");

        for(Triple triple : result){
            System.out.println("[" + triple.position + "] " + "(" + triple.opcode + ", " + triple.src + ", " + triple.dest + ")\n");
        }

        System.out.println("END PRINTING RESULT ***********");
        return result;
    }

}
        
