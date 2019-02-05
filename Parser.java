/*
 * Justin Comer, Marissa Hudson, Jacob Elijah Martinez
 * cs 4308 W01
 * 7/10/2018
 */

import java.awt.Component;

import java.awt.List;
import java.io.*; // needed for buffered reader 
import java.util.ArrayList;
import java.util.LinkedList;

public class Parser {	

  static int token = 0; //token index, used to keep track of analysis and display information if there is an error
  static List IDs = new List(); //identifier list
  static List FunName = new List(); //function list
  
  static List execList = new List(); //will be used by the executer 

  //keyword tokens 
  static String begin = "18"; static String set = "28"; static String until = "25"; static String pointer = "6"; static String specifications = "1";
  static String endfun = "19"; static String While = "30"; static String endrepeat = "26"; static String type = "8"; static String Enum = "11";
  static String If = "20"; static String Do = "31"; static String implement = "14"; static String mvoid = "33"; static String struct = "9";
  static String then = "21"; static String endwhile = "32"; static String functions = "5"; static String Short = "34"; static String global = "12";
  static String Else = "22"; static String display = "27"; static String Main = "15"; static String Int = "10"; static String forward = "3";
  static String endif = "23"; static String repeat = "24"; static String parameters = "16"; static String symbol = "2"; static String references = "4";
  static String Global = "12"; static String declarations = "13"; static String Return = "29"; static String define = "37"; static String Char = "36";

  //operator or boolean tokens
  static String equal = "38"; static String ge = "41"; static String ne = "44"; static String mul = "47";
  static String le = "39"; static String gt = "42"; static String plus = "45"; static String div = "48";
  static String lt = " 40"; static String eq = "43"; static String minus = "46";

  //Integer token
  static String integer = "50";

  //float token
  static String Float = "51";

  //String token
  static String literal = "52";

  //token for IDs
  static String Ident = "53";
	
  static boolean MainUsed = false; // used to ensure only one main function
	
  //lists that will be used to store important values as we progress 
  static List lines = new List();
  static List tokens = new List();
  static List lexemes = new List();

  public static boolean checkGrammar(){
    System.out.println("<start>"); //start 
    System.out.println("<symbol>");
    if (tokens.getItem(token).equals(symbol)) { //if symboles are declared, they are not required for correct syntax 
      token++;
      if (checkSymbol()) {
        //coninue
      }
      else {
        System.out.println("Error on line: " + lines.getItem(token));
        return false;
      }
    }
    System.out.println("<forward_refs>"); //check forwards 
    if (tokens.getItem(token).equals(forward)){
      System.out.println("FORWARD");
      System.out.println("<frefs>");
      token++;
      if (tokens.getItem(token).equals(references)){ //check method references 
        System.out.println("REFERENCES");
        token++;
        if(checkFwdList()){
          //continue
        }
        else
          return false;
      }
      else
        return false;
    }
    System.out.println("<specifications>");
    if (tokens.getItem(token).equals(specifications)){ //checks for specifications, which are not required for correct syntax
      System.out.println("SPECIFICATIONS");
      System.out.println("<spec_list>");
      token++;
      if(checkSpec()){
        //continue
      }
      else
        return false;
    }
    System.out.println("<global>");
    if(tokens.getItem(token).equals(Global)){ //checks for global, which are not required for correct syntax
      System.out.println("GLOBAL");
      token++;				
      if(tokens.getItem(token).equals(declarations)){
        System.out.println("<declarations>");
        System.out.println("DECLARATION"); //variable declarations 
        token++;
        if(checkDec()){
          //continue
        }
        else
          return false;
      }
    }
    if(tokens.getItem(token).equals(implement)){ //implementation
      System.out.println("<implement>");
      System.out.println("IMPLEMENTATIONS");
      token++;
      if(tokens.getItem(token).equals(functions)){ //function list
        System.out.println("<funct_list>");
        System.out.println("<funct_def>");
        System.out.println("<funct_body>");
        token++;
	if(!checkFunction()){
          if(token != (tokens.getItemCount()-1)) { 
            System.out.println("Error on line: " + lines.getItem(token));
            return false;
          }
        }
      }
      else{
        System.out.println("Error on line: " + lines.getItem(token));
        return false;
      }
    }
    else{
      System.out.println("Error on line: " + lines.getItem(token));
      return false;
    }
    return true;
  }

  public static boolean checkDec() { //checking global declarations
    if (tokens.getItem(token).equals(define)){
      System.out.println("DEFINE");
      token++;
      if (!checkIdentifier(lexemes.getItem(token)) && tokens.getItem(token).equals(Ident)){	
        IDs.add(lexemes.getItem(token));
        System.out.println("IDENTIFIER");
        token++;
	if(tokens.getItem(token).equals(define)){ //defining a variable 
          if(!checkDec()){ //recursive call since definitions remain 
            return false;
          }
        }
        else //no other definitions 
          return true;
      }
      else //nothing follows define 
        return false;			
    }
    return true;
  }

  public static boolean checkFwdList() { //checking fwd list
    System.out.println("<forward_list>");
    System.out.println("<forwards>");		
    if(checkFunct_Main()){
      return true;
    }
    return false;
  }

  public static boolean checkFunct_Main() {
    System.out.println("<funct_main>");
    if (tokens.getItem(token).equals(functions)) {
      System.out.println("FUNCTION"); //first option 
      token++;
    if (!checkIdentifier(lexemes.getItem(token)) && !checkFunName(lexemes.getItem(token))){ //ID isnt taken
      IDs.add(lexemes.getItem(token));
      FunName.add(lexemes.getItem(token));
      System.out.println("IDENTIFIER"); //fun name
      token++;
    if (tokens.getItem(token).equals(parameters)){ // parameters are optional, so if not present, will not end analysis
      System.out.println("<parameters>");
      token++;
      if (!addParamets())
        return false;
    }
    if (tokens.getItem(token).equals(Return)){ //looking for return type 
      System.out.println("<oper_type>");
      System.out.println("RETURN");
      token++;
      if (!ret_type()) //if return syntax wrong, compilation stops 
        return false;
    }
    else
      return false;				
    }
    else
      return false;
    }
    if(tokens.getItem(token).equals(functions)) { //recursive call
      if(checkFunct_Main())
        return true;
      else 
        return false;
    }
    return true;
  }

  public static boolean ret_type() {
    //checking return type 
    System.out.println("<check_ptr>");
    if (lexemes.getItem(token).equals("*")){ //checking pointer
      System.out.println("POINTER");
      return true;
    }		
    System.out.println("<check_array>");
    if (tokens.getItem(token).equals(type)) {
      System.out.println("<ret_type>");
      System.out.println("TYPE");
      token++;	
      if (tokens.getItem(token).equals(mvoid)) { //void
        System.out.println("MVOID");
        token++;
        System.out.println(lexemes.getItem(token));
        return true;
      }
      if (tokens.getItem(token).equals(Short)) { //short
      System.out.println("SHORT");
      token++;
      return true;
      }			
      if (tokens.getItem(token).equals(integer)) { //short
        System.out.println("INTEGER");
        token++;
        return true;
      }
      if (tokens.getItem(token).equals(Char)) { //CHAR
        System.out.println("CHAR");
        token++;
        return true;
      }
    }
    return false;
  }

  public static boolean checkSpec() { //checking specificaions
    System.out.println("<spec_def>");
    if (tokens.getItem(token).equals(Enum)){
      System.out.println("ENUM");
      token++;
      return true;
    }
    else if(tokens.getItem(token).equals(struct)){ 
      System.out.println("STRUCT");
      token++;
      return true;
    }		
    return false;
  }

  public static boolean checkSymbol() { //checking symbols 
    System.out.println("SYMBOL");
    System.out.println("<symbol_def>");
    if (!checkIdentifier(lexemes.getItem(token))){
      if (tokens.getItem(token).equals(Ident)){
        System.out.println("IDENTIFIER");
        IDs.add(lexemes.getItem(token));
        token++;
        if (tokens.getItem(token).equals(integer) || tokens.getItem(token).equals(Float) || tokens.getItem(token).equals(literal)){ //checking constant
          System.out.println("HCON");
          token++;
          if (tokens.getItem(token).equals(symbol)){
            System.out.println("<symbol>");
            token++;
            if (checkSymbol()) //recursive check 
             return true;
            else
             return false;
          }
          else
            return true;
        }
      }
    }
    return false;		
  }
	
  public static boolean checkFunction(){
    if (checkHeader()){ //checking header 
      if (tokens.getItem(token).equals(parameters)){ // parameters are optional, so if not present, will not end analysis
	System.out.println("<parameters>");
	token++;
        if (!checkParamets()) //if parameters exist 
          return false;
      }
      if(tokens.getItem(token).equals(Return)) {
        //looking for return type 
        System.out.println("<oper_type>");
        System.out.println("RETURN");
        token++;				
        if (!ret_type())//if return syntax wrong, compilation stops 
          return false;
      }
      else
        return false;
      if (tokens.getItem(token).equals(begin)) {// starts with begin
        System.out.println("<f_body>");
        System.out.println("BEGIN");
        token++; 
        System.out.println("<statement>");
        if (checkStatement()){ // checks statement list
          if (tokens.getItem(token).equals(endfun)){
            System.out.println("ENDFUN");
            if (token == tokens.getItemCount() - 1)//used for recursive call
              return true;
            else{
              token++;	
              if (tokens.getItem(token).equals(functions)){
                token++;
                System.out.println("<funct_def>");
                System.out.println("<funct_body>");
                if (checkFunction())
                  return false;
                else
                  return false; //recursive call if there remains functions in the function list
              }
              else
                return false;
            } //if the function ends with endfun, its grammar is correct, will return to original loop for continued analysis
          }
        }
      }
    }
    return false;
  }

  public static boolean checkParamets() { //check to make sure parameters were used in forwards 
    if (lexemes.getItem(token).startsWith("*")){ //check pointer 
      System.out.println("POINTER");
      String temp = lexemes.getItem(token).replace("*", "");
      if (checkIdentifier(temp))
        token++;
    }
    else if (lexemes.getItem(token).endsWith("[]")){ //check array
      System.out.println("ARRAY");
      String temp = lexemes.getItem(token).replace("[]", "");
      if (checkIdentifier(temp))
        token++;
    }
    else if (checkIdentifier(lexemes.getItem(token))){ //checking constant
      System.out.println("CONSTANT");
      token++;
    }
    else 
      return false;
    if (tokens.getItem(token).equals(type)){ // must specify type
      System.out.println("TYPE");
      token++;
      if (!checktype())
        return false;
    }
    else
      return false;
    if (lexemes.getItem(token).equals(",")){ //if a comma, check there are valid parameters after
      System.out.println("COMMA");
      token++;
      if (checkParamets()) //recursive call 
        return true;
    }
    else
      return true;
    return false;
  }

  public static boolean addParamets() {
    if (lexemes.getItem(token).startsWith("*")){ //check pointer 
      System.out.println("POINTER");
      String temp = lexemes.getItem(token).replace("*", "");
      if (!checkIdentifier(temp)){
        IDs.add(temp); //if identifier without pointer flag is good, add to table 
        token++;
      }
    }
    else if (lexemes.getItem(token).endsWith("[]")){ //check array
      System.out.println("ARRAY");
      String temp = lexemes.getItem(token).replace("[]", "");
      if (!checkIdentifier(temp)){
        IDs.add(temp); //if identifier without brackets is good, add to table 
        token++;
      }
    }
    else if (!checkIdentifier(lexemes.getItem(token))){ //checking constant 
      System.out.println("CONSTANT");
      IDs.add(lexemes.getItem(token));
      token++;
    }
    else 
      return false;
    if (tokens.getItem(token).equals(type)){ // must specify type
      System.out.println("TYPE");
      token++;
      if (!checktype())
        return false;
    }
    else
      return false;
    if (lexemes.getItem(token).equals(",")){ //if a comma, check there are valid parameters after 
      System.out.println("COMMA");
      token++;
      if (checkParamets()) //recursive call 
        return true;
    }
    else
      return true;		
    return false;
  }

  public static boolean checkHeader() {		
    System.out.println("<main_head>");
    if (tokens.getItem(token).equals(Main)){
      System.out.println("MAIN");
      if (MainUsed) //main can only be used once 
        return false; //main was used 
      else{
        token++;
        MainUsed = false; 
        return true; //main declared 
      }
    }
    else if(checkFunName(lexemes.getItem(token))){
      System.out.println("IDENTIFIER"); //making sure function was listed in forwards 
      execList.add(lexemes.getItem(token)); //needed for execution 
      token++;
      return true;
    }
    else
      return false; //function name was not declared in forwards 
  }

  public static boolean checkStatement() {
    System.out.println("<statement_list>");
    if (tokens.getItem(token).equals(If)){ // if
      System.out.println("<if_statement>");
      System.out.println("IF");
      execList.add(lexemes.getItem(token)); //needed for execution 
      token++;
      if (checkBoolean()){ //check boolean statement
        if (tokens.getItem(token).equals(then)){ //then
          System.out.println("THEN");
          execList.add(lexemes.getItem(token)); //needed for execution 
          token++; 
          if (checkStatement()){ //check statement list
            if (tokens.getItem(token).equals(Else)){ //else
              System.out.println("ELSE");
              execList.add(lexemes.getItem(token)); //needed for execution 
              token++;
              if (checkStatement()){ //check statement list
                if (tokens.getItem(token).equals(endif)){ //endif
                  System.out.println("ENDIF");
                  execList.add(lexemes.getItem(token)); //needed for execution 
                  token++;
                  if (!checkStatement() && !tokens.getItem(token).equals(endfun))
                    return !checkStatement(); //check following statements
                  else
                    return true;
                }
              }
            }
          }
        }
      }
    }
    else if(tokens.getItem(token).equals(set)){ //set
      System.out.println("<assignement_statement>");
      System.out.println("SET");
      execList.add(lexemes.getItem(token)); // needed for execution 
      token++;
      if (checkIdentifier(lexemes.getItem(token))){ //makes sure it was declared 
        System.out.println("IDENTIFIER");
        int addition = token;
        execList.add(lexemes.getItem(token));
        token++;
        if (tokens.getItem(token).equals(equal)){ // =
          System.out.println("ASSIGNEMENT_OPERATOR");
          token++;
          if (checkArithmatic()){
            IDs.add(lexemes.getItem(addition)); // add lexeme to identifier after making sure it is not already declared
            if(tokens.getItem(token).equals(endfun) || tokens.getItem(token).equals(until) || tokens.getItem(token).equals(endwhile) || tokens.getItem(token).equals(Else) || tokens.getItem(token).equals(endif))
              return true; //checking to see if end of statment list
            else //check following statements
              return checkStatement();
          }
          else 
            return true;
        }
      }
    }
    else if (tokens.getItem(token).equals(While)){ //while 
      System.out.println("<while_statement>");
      System.out.println("WHILE");
      execList.add(lexemes.getItem(token)); //needed for execution 
      token++;
      if (checkBoolean()){ //check boolean statement
        if (tokens.getItem(token).equals(Do)){ // do
          System.out.println("DO");
          execList.add(lexemes.getItem(token)); //needed for execution 
          token++;
          if (checkStatement()){
            while (tokens.getItem(token).equals(endwhile)) //checking following statements 
              if (!checkStatement())
                return false;
              if (tokens.getItem(token).equals(endwhile)){ //endwhile
                System.out.println("ENDWHILE");
                execList.add(lexemes.getItem(token)); //needed for execution 
                if (tokens.getItem(token).equals(endfun) || tokens.getItem(token).equals(until) || tokens.getItem(token).equals(endwhile) || tokens.getItem(token).equals(Else) || tokens.getItem(token).equals(endif))
                  return true; //checking to see if end of statment list
                else  //check following statements
                  return checkStatement();
              }
              else 
                return true;
          }
          else
            return false;
        }
        else // example of one incase we need to put them back
          System.out.println("Error on line: " + lines.getItem(token));
      }
    }
    else if(tokens.getItem(token).equals(display)){ //display
      System.out.println("<print_statement>");
      System.out.println("DISPLAY");
      execList.add(lexemes.getItem(token)); //needed for execution 
      token++;
      if(checkArgs()){
        System.out.println("<arg_list>");
        if(tokens.getItem(token).equals(endfun) || tokens.getItem(token).equals(until) || tokens.getItem(token).equals(endwhile) || tokens.getItem(token).equals(Else) || tokens.getItem(token).equals(endif))
            return true; //checking to see if end of statment list
          else //check following statements
            return checkStatement();
      }
      else
        return false;
    }
    else if(tokens.getItem(token).equals(repeat)){ // repeat
      System.out.println("<repeat_statment>");
      System.out.println("REPEAT");
      execList.add(lexemes.getItem(token)); //needed for execution 
      token++;
      if (checkStatement()){
        while (!tokens.getItem(token).equals(until))
          if (!checkStatement())
            return false;
          System.out.println("<statment>");
        if (tokens.getItem(token).equals(until)){ //until
          System.out.println("UNTIL");
          execList.add(lexemes.getItem(token)); //needed for execution 
          token++;
          if (checkBoolean()){
            if (tokens.getItem(token).equals(endrepeat)){ // endrepeat
              System.out.println("ENDREPEAT");
              execList.add(lexemes.getItem(token)); //needed for execution 
              token++;
              if (tokens.getItem(token).equals(endfun) || tokens.getItem(token).equals(until) || tokens.getItem(token).equals(endwhile) || tokens.getItem(token).equals(Else) || tokens.getItem(token).equals(endif))
                return true; //checking to see if end of statment list
              else //check following statements
                return checkStatement();
            }
            else 
              return true;
          }
        }
      }
    }
    else if(checkFunName(lexemes.getItem(token))){ //checking to see if function call
      System.out.println("FUNCTION CALL");
      execList.add(lexemes.getItem(token)); //needed for execution 
      token++;
      if(tokens.getItem(token).equals(endfun) || tokens.getItem(token).equals(until) || tokens.getItem(token).equals(endwhile) || tokens.getItem(token).equals(Else) || tokens.getItem(token).equals(endif))
        return true; //checking to see if end of statment list
      else //check following statements
        return checkStatement();
    }		
    return false;
  }

  public static boolean checkArgs() { //checking arguments validation
    System.out.println("<args>");
    if (checkIdentifier(lexemes.getItem(token))){ //if it is in identifier 
    	execList.add(lexemes.getItem(token)); //needed for execution 
      token++;
      System.out.println("IDENTIFIER");
    }
    else if (tokens.getItem(token).equals(Float)   || //constants or a string
             tokens.getItem(token).equals(integer) ||
             tokens.getItem(token).equals(literal)) {
    	execList.add(lexemes.getItem(token)); //needed for execution 
      token++;
      System.out.println("CONSTANT");
    }
    else
      return false; 
    if (lexemes.getItem(token).equals(",")){ //check to make sure comma is followed by valid argument 
      System.out.println("COMMMA");
      token++;
      if (checkArgs())
        return true;
    }		
    return true; 
  }

  public static boolean checkRelOp(){ //relative operators 		
    if (tokens.getItem(token).equals(ge) ||
        tokens.getItem(token).equals(lt) ||
        tokens.getItem(token).equals(le) ||
        tokens.getItem(token).equals(eq) ||
        tokens.getItem(token).equals(gt) ||
        tokens.getItem(token).equals(ne)){
    	execList.add(lexemes.getItem(token)); //needed for execution
      token++;
      System.out.println("RELATIVE OPERATOR");
      return true;
      }
    return false; //not a relative operateor 
  }

  public static boolean checkBoolean() {
    System.out.println("<boolean expression>");
    if(checkArithmatic())
	{						//check to make sure a relative operator is between two arithmatic scentences 
		if(checkRelOp()) 
		{
			if(checkArithmatic())
			{
				return true; //correct booloean expression 
			}	
		}
	}
	
	return false; //check to make sure a relative operator is between two arithmatic scentences 
  }

  public static boolean checkArithmatic() {
    System.out.println("<arithmatic_expression>");
    if (checkPrimary()){ //checks to make sure it starts with a number / id
    	execList.add(lexemes.getItem(token-1)); //needed for execution 
      if (tokens.getItem(token).equals(plus) || tokens.getItem(token).equals(minus)){ // checks +/-
        System.out.println("OPERATOR");
        execList.add(lexemes.getItem(token)); //needed for execution 
        token++;
        checkArithmatic(); //recursive call 
      }
      else
        return checkMulexp(); //sends to *|/ method
    }
    return false;
  }

  public static boolean checkMulexp(){
    System.out.println("<mulexp>");
    if (tokens.getItem(token).equals(mul) || tokens.getItem(token).equals(div)){
      System.out.println("OPERATOR");
      execList.add(lexemes.getItem(token)); //needed for execution 
      token++;
      if (checkPrimary()) {
    	  execList.add(lexemes.getItem(token-1));
        return checkMulexp();}
      else
        return false; // will return false if operator is not followed by a number 
    }
    return true; // should only reach this point if a number is not followed by an operator, making it valid 	
  }

  public static boolean checkPrimary() {
    System.out.println("<primary>");
      if (checkConst()){ //checks to see if number 
        System.out.println("CONSTANT");
        token++;
        return true;
      }
      else if (checkIdentifier(lexemes.getItem(token))){ // see if id is in table 
        System.out.println("IDENTIFIER");
        token++;
        return true;
      }
      else if (tokens.getItem(token).equals("(")){ //check parentheses 
        System.out.println("LEFT PAREN");
        token++;
        checkArithmatic();
        if (tokens.getItem(token).equals(")")) { 
          System.out.println("RIGHT_PAREN");//makes sure the parentheses is closed 
          token++;
          return true;
        }
      }
    return false;
  }

  public static boolean checkIdentifier(String string) { //scans id table
    for (int i = 0; i < IDs.getItemCount(); i++)
      if (string.equals(IDs.getItem(i)))
        return true;
    return false;
  }

  public static boolean checkFunName(String string) { //scans id table
    for (int i = 0; i < FunName.getItemCount(); i++)
      if (string.equals(FunName.getItem(i)))
        return true;
    return false;		
  }	

  public static boolean checkConst() { //checking to see if the token is a constant 
    return (tokens.getItem(token).equals(Float) || tokens.getItem(token).equals(integer));
  }

  public static boolean checktype() {//this will check return type 	
    if (tokens.getItem(token).equals(mvoid)){
      System.out.println("MVOID"); 
      token++;
      return true;
    }
    else if (tokens.getItem(token).equals(Int)){
      System.out.println("INTEGER");
      token++;
      return true;
    }
    else if (tokens.getItem(token).equals(Short)){
      System.out.println("SHORT");
      token++;
      return true;
    }
    return false;
  }

  // M A I N   M E T H O D \\
  public static List parse(){		
    Lexical list = new Lexical(); //creates new Lexical Analyzer
    List tokenized = null;
	try {
		tokenized = list.getList();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} //gets tokenized list from scanner 
		
    for (int i = 0; i < tokenized.getItemCount(); i++){
      String str = tokenized.getItem(i);
      String[] split = str.split(" ");
        //split list from the lexical analyzer into the lists we will use here
        lines.add(split[0]); //line being analyzed 
        tokens.add(split[1]); //token being analyzed  ALL GLOBALS 
        lexemes.add(split[2]); //lexeme being analyzed 
    }

    if(checkGrammar()) //starts analysis 
      System.out.println("Syntax is Correct"); //will prevent upon successful return, signaling correct syntax 
    
    
    	return execList;
  }

}
