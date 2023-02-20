package lyc.compiler;

import java_cup.runtime.Symbol;
import lyc.compiler.ParserSym;
import lyc.compiler.model.*;
import static lyc.compiler.constants.Constants.*;
import lyc.compiler.files.SymbolTableGenerator;

%%

%public
%class Lexer
%unicode
%cup
%line
%column
%throws CompilerException
%eofval{
  return symbol(ParserSym.EOF);
%eofval}


%{
  private boolean debug = false;

  private Symbol symbol(int type) {
    return new Symbol(type, yyline, yycolumn);
  }
  private Symbol symbol(int type, Object value) {
    return new Symbol(type, yyline, yycolumn, value);
  }
%}

//Reserved words 
If = "if"
Else = "else"
While = "while"
Write = "write"
Read = "read"
Init = "init"
IntegerType = "Int"
FloatType = "Float"
StringType = "String"
ElementInTheMiddle = "ElementInTheMiddle"
Fibonacci = "FIB"

//Comparison operators
Not = "not"
Greater = ">"
Less = "<"
And = "&"
Or = "||"

//Arithmetic operators
Plus = "+"
Mult = "*"
Sub = "-"
Div = "/"
Assig = "="

//Other symbols
BracketOpen = "("
BracketClose = ")"
CurlyBracketOpen = "{"
CurlyBracketClose = "}"
Comma = ","
Dot = "."
Colon = ":"

Letter = [a-zA-Z]
Digit = [0-9]

StartComment = "/*"
EndComment = "*/"
Comment = {StartComment} {InputCharacter}* {EndComment}

//Identificadores y Constantes
Identifier = {Letter} ({Letter}|{Digit})*
IntegerConstant = {Sub}?{Digit}+
FloatConstant = {Sub}?({Digit}+ {Dot} {Digit}+ | {Digit}+ {Dot} | {Dot} {Digit}+)
StringConstant = \"[^\n\"]*\"

LineTerminator = \r|\n|\r\n
InputCharacter = [^\r\n]
Identation =  [ \t\f]
WhiteSpace = {LineTerminator} | {Identation}

%%

/* keywords */

<YYINITIAL> {

  /// Operadores
  {Plus}                                    { if(debug){  System.out.println("PLUS");                 } return symbol(ParserSym.PLUS); }
  {Sub}                                     { if(debug){  System.out.println("SUB");                  } return symbol(ParserSym.SUB); }
  {Mult}                                    { if(debug){  System.out.println("MULT");                 } return symbol(ParserSym.MULT); }
  {Div}                                     { if(debug){  System.out.println("DIV");                  } return symbol(ParserSym.DIV); }
  {Assig}                                   { if(debug){  System.out.println("ASSIG");                } return symbol(ParserSym.ASSIG); }

  /// Operadores Logicos
  {Not}                                     { if(debug){  System.out.println("NOT");                  } return symbol(ParserSym.NOT); }
  {Greater}                                 { if(debug){  System.out.println("GREATER");              } return symbol(ParserSym.GREATER); }
  {Less}                                    { if(debug){  System.out.println("LESS");                 } return symbol(ParserSym.LESS); }
  {And}                                     { if(debug){  System.out.println("AND");                  } return symbol(ParserSym.AND); }
  {Or}                                      { if(debug){  System.out.println("OR");                   } return symbol(ParserSym.OR); }

  /// Palabras reservadas 
  {If}                                      { if(debug){  System.out.println("IF");                   } return symbol(ParserSym.IF); }
  {Else}                                    { if(debug){  System.out.println("ELSE");                 } return symbol(ParserSym.ELSE); }
  {While}                                   { if(debug){  System.out.println("WHILE");                } return symbol(ParserSym.WHILE); }
  {Write}                                   { if(debug){  System.out.println("WRITE");                } return symbol(ParserSym.WRITE); }
  {Read}                                    { if(debug){  System.out.println("READ");                 } return symbol(ParserSym.READ); }
  {Init}                                    { if(debug){  System.out.println("INIT");                 } return symbol(ParserSym.INIT); }
  {IntegerType}                             { if(debug){  System.out.println("INTEGERTYPE");          } return symbol(ParserSym.INTEGERTYPE); }
  {FloatType}                               { if(debug){  System.out.println("FLOATTYPE");            } return symbol(ParserSym.FLOATTYPE); }
  {StringType}                              { if(debug){  System.out.println("STRINGTYPE");           } return symbol(ParserSym.STRINGTYPE); }
  {ElementInTheMiddle}                      { if(debug){  System.out.println("ELEMENTINTHEMIDDLE");   } return symbol(ParserSym.ELEMENTINTHEMIDDLE); }
  {Fibonacci}                               { if(debug){  System.out.println("FIBONACCI");            } return symbol(ParserSym.FIBONACCI); }
 
  /// Simbolos
  {BracketOpen}                             { if(debug){  System.out.println("OPEN_BRACKET");         } return symbol(ParserSym.OPEN_BRACKET); }
  {BracketClose}                            { if(debug){  System.out.println("CLOSE_BRACKET");        } return symbol(ParserSym.CLOSE_BRACKET); }
  {CurlyBracketOpen}                        { if(debug){  System.out.println("OPEN_CURLYBRACKET");    } return symbol(ParserSym.OPEN_CURLYBRACKET); }
  {CurlyBracketClose}                       { if(debug){  System.out.println("CLOSE_CURLYBRACKET");   } return symbol(ParserSym.CLOSE_CURLYBRACKET); }
  {Comma}                                   { if(debug){  System.out.println("COMMA");                } return symbol(ParserSym.COMMA); }
  {Colon}                                   { if(debug){  System.out.println("COLON");                } return symbol(ParserSym.COLON); }

  /// Identificadores y Constantes
  {Identifier}                              { 
                                              if(debug){ System.out.println("Identifier"); }

                                              //Check range
                                              if(!checkRangeId(yytext())){
                                                  throw new InvalidLengthException("\n------------\n" +
                                                                                  "El id ( " + yytext() + " )" +
                                                                                  " se encuentra fuera del rango permitido ( " + MIN_ID + " ; " + MAX_ID + " )" +
                                                                                  "\n------------\n"
                                                                                  );
                                                }

                                              SymbolTableGenerator.addSymbol(yytext(), SymbolTableGenerator.Type.ID, "");
                                              
                                              return symbol(ParserSym.IDENTIFIER, yytext()); 
                                            }
  {IntegerConstant}                         { 
                                              if(debug){ System.out.println("IntegerConstant"); }

                                              //Check range
                                              if(!checkRangeInt(yytext())){
                                                throw new InvalidIntegerException("\n------------\n" +
                                                                                  "La constante int ( " + yytext() + " )" +
                                                                                  " se encuentra fuera del rango permitido ( " + MIN_INT + " ; " + MAX_INT + " )" +
                                                                                  "\n------------\n"
                                                                                );
                                              }

                                              SymbolTableGenerator.addSymbol("_" + yytext(), SymbolTableGenerator.Type.CTE_INT, yytext());
                                              
                                              return symbol(ParserSym.INTEGER_CONSTANT, yytext()); 
                                            }
                                            
  {FloatConstant}                           { 
                                              if(debug){ System.out.println("FloatConstant"); }

                                              //Check range
                                              if(!checkRangeFlo(yytext())){
                                                throw new InvalidFloatException("\n------------\n" +
                                                                  "La constante float ( " + yytext() + " )" +
                                                                  " se encuentra fuera del rango permitido (+-)( " + MIN_FLOAT + " ; " + MAX_FLOAT + " )" +
                                                                  "\n------------\n"
                                                                  );
                                              }

                                              SymbolTableGenerator.addSymbol("_" + yytext(), SymbolTableGenerator.Type.CTE_FLOAT, yytext());
                                              return symbol(ParserSym.FLOAT_CONSTANT, yytext()); 
                                            }

  {StringConstant}                          { 
                                              if(debug){ System.out.println("StringConstant"); }

                                              //Check range
                                              if(!checkRangeStr(yytext())){
                                                throw new InvalidLengthException("\n------------\n" +
                                                                                "La constante string " + yytext() +
                                                                                " se encuentra fuera del rango permitido ( " + MIN_STRING + " ; " + MAX_STRING + " )" +
                                                                                "\n------------\n"
                                                                                );
                                              }

                                              SymbolTableGenerator.addSymbol("_" + yytext(),SymbolTableGenerator.Type.CTE_STRING, yytext());
                                              return symbol(ParserSym.STRING_CONSTANT, yytext()); 
                                            }
                
  /// Omitir
  {WhiteSpace}                   { /* ignore */ }
  {Comment}                      { /* ignore */ }
}

/* error fallback */
[^]                              { throw new UnknownCharacterException(yytext()); }
