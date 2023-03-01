package lyc.compiler.files;

import java.io.FileWriter;
import java.io.IOException;

public class AsmCodeGenerator implements FileGenerator {

    private boolean __logmsg = false;
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

    private String _header =     "include macros.asm\n"  +
    "include number.asm\n"  +
    "\n.MODEL LARGE\n"      +
    ".386\n"                +
    ".STACK 200h\n";

    private String _end =  "\n\nmov ax,4C00h\n" +
    "int 21h\n" +
    "END main";


    private String _code =  "\n.CODE\n"       +
    "main:\n"         +
    "mov ax,@DATA\n"  +
    "mov ds,ax\n"     +
    "mov es,ax\n";

    private String _data =  "\n.DATA" +
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
        String asm = _header + _data + _code + _end;
        fileWriter.write(asm);
    }




}
        
