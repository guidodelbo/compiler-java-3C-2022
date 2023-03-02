package lyc.compiler.files;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Stack;

import lyc.compiler.files.SymbolTableGenerator;
import lyc.compiler.files.IntermediateCodeGenerator.Triple;
import lyc.compiler.files.SymbolTableGenerator.Symbol;
import lyc.compiler.files.IntermediateCodeGenerator;

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
            if(s.type == SymbolTableGenerator.Type.CTE_STRING){
                _data += s.name + " db ";
            }
            else{
                _data += s.name + " dd ";
            }

            //cte
            if(s.name.startsWith("_")){
                if(s.type == SymbolTableGenerator.Type.CTE_STRING){
                    _data += "\"" + s.value + "\",\"$\"";
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
        boolean asig = false;
        boolean read = false;
        boolean write = false;
        List<Triple> triples = IntermediateCodeGenerator.getTriples();
        Stack<String> stack = new Stack<>();

        if(__logmsg){
            System.out.println("*****************");
            //System.out.println(polaca);
        }

        for(Triple triple: triples){
            //System.out.println("Valor: " + s);
            switch(triple.opcode){
                case "=":
                    asig = true;
                    if(__logmsg){
                        System.out.println("Desapilando por =");
                        System.out.println(stack);
                        System.out.println("*****************");
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
                break;

                case "READ":
                    if(__logmsg){
                        System.out.println("read");
                        System.out.println("*****************");
                    }
                    read = true;
                break;

                case "WRITE":
                    if(__logmsg){
                        System.out.println("write");
                        System.out.println("*****************");
                    }
                    write = true;
                break;

                case "WHILE":
                    if(__logmsg){
                        System.out.println("while");
                        System.out.println("*****************");
                    }
                    //dummy
                break;

                case "INC_+1":
                    //_code += "\nfld " + aux_uno;
                    stack.push(aux_uno);
                break;

                case "NOT":
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
                        cmp = false;
                        continue;
                    }
                    if(triple.opcode.startsWith("__etiqueta")){
                        if(__logmsg){
                            System.out.println("etiqueta");
                            System.out.println("*****************");
                        }
                        if(triple.opcode.contains(":")){
                            _code += "\n" + triple;
                            continue;
                        }
                        _code += triple.opcode;
                        continue;
                    }
                    if(asig){
                        
                        String val = stack.pop();
                        
                        System.out.println(val);

                        Symbol symbol = SymbolTableGenerator.get_symbol_fromTable(val);
                        if(symbol != null && (val.startsWith("_str") || symbol.type == SymbolTableGenerator.Type.CTE_STRING)){
                            _code += "STRCPY " + triple.opcode + "," + val;
                            asig = false;
                            continue;
                        }
                        else{
                            _code += "\nfld " + val;
                            _code += "\nfstp " + triple.opcode;
                            asig = false;
                            continue;
                        }
                    }
                    if(read){
                      _code += "\ngetString " + triple.opcode;
                      read = false;
                      continue;
                    }
                    if(write){
                        Symbol symbol = SymbolTableGenerator.get_symbol_fromTable(triple.opcode);
                        if(symbol == null){
                            System.out.println("ERROR");
                            System.exit(0);
                        }
                        if(symbol.type == SymbolTableGenerator.Type.CTE_STRING){
                            _code += "\ndisplayString " + triple.opcode;
                        }
                        else if(symbol.type == SymbolTableGenerator.Type.CTE_STRING){
                            _code += "\nDisplayFloat " + triple.opcode + "," + aux_dec_int;
                        }
                        else if(symbol.type == SymbolTableGenerator.Type.CTE_STRING){
                            _code += "\nDisplayFloat " + triple.opcode + "," + aux_dec_float;
                        }
                        _code += "\nnewLine\n";
                        write = false;
                        continue;
                    }
                    stack.push(triple.opcode);
                break;
            }
        }
    }



}
        
