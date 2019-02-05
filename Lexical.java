
import java.awt.Component;
import java.awt.List;
import java.io.*; // needed for buffered reader 
import java.util.ArrayList;
import java.util.LinkedList;
public class Lexical
{	
	static int lineRead = 1; // variable used to print the line being read 
	
	//keywords 
  	static String[] keywords = {"specifications", "symbol", "forward", "references", "function", "pointer", "array", "type", "struct", "integer", "enum",
  						"global", "declarations", "implementations", "main", "parameters", "constant", "begin", "endfun", "if", "then", "else", "endif", 
  						"repeat", "until", "endrepeat", "display", "set", "return", "while", "do", "endwhile", "void" , "short", "tbool", "char", "define"};
  	
  	//operators 
  	static String[] operators = {"=", "<=", "<", ">=", ">", "==", "~=", "+", "-", "*", "/"};
  	
  	static List tokenized = new List();
  	
  	public static boolean isInteger(String s) // this will see if the lexeme is an int 
  	{ 
        try
        {
           Integer.parseInt(s);
           							// s is a valid integer
           return true;
        }
        catch (NumberFormatException ex)
        {
           // s is not an integer
        }
   
        return false;
     }
  	
  	private static boolean checkKeywords(String string) {
		
  		for(int i = 0; i < keywords.length; i++)//checking for match in keywords
  		{
  			if(string.equals(keywords[i])){
  				int token = (i + 1);
  				System.out.print("Line: " + lineRead + " ");
  				System.out.print("Token found: " + token + " "); // token value is based on position in table 
  				tokenized.add(lineRead + " " + token + " " + string);
  				return true;
  			}
  		}
  		
  		return false;
	}
  	
  	private static boolean checkOperators(String string) {
		
  		for(int i = 0; i < operators.length; i++)//checking for match in keywords
  		{
  			if(string.equals(operators[i])){
  				int token = ((i) + (keywords.length + 1));
  				System.out.print("Line: " + lineRead + " ");
  				System.out.print("Token found: " + token + " "); //token value is based on position in table plus length of key words
  				tokenized.add(lineRead + " " + token + " " + string);
  				return true;
  			}
  		}
  		
		return false;
	}
  	
  	private static boolean checkint(String string) {
		
  		
  			if(isInteger(string)) { 
  				int token = (((operators.length) + (keywords.length + 1))+1);
  				System.out.print("Line: " + lineRead + " ");
  				System.out.print("Token found: " + token + " "); //obtains assigned token value
  				tokenized.add(lineRead + " " + token + " " + string);
  				return true;
  			
  			
  		}
  		
		return false;
	}
  	
  	private static boolean checkFloat(String string) {
		
  		try
        {
            // checking valid float using parseInt() method
            Float.parseFloat(string);
            int token = (((operators.length) + (keywords.length + 1))+2);
            System.out.print("Line: " + lineRead + " ");
            System.out.print("Token found: " + token + " "); //obtains assigned token value 
            tokenized.add(lineRead + " " + token + " " + string);
            return true;
        } 
        catch (NumberFormatException e)
        {
            return false;
        }
		
			
  	}
  	
  	public static void printLiteral(String string)
  	{
  		int token = (((operators.length) + (keywords.length + 1))+3);
  		System.out.print("Line: " + lineRead + " ");
        System.out.print("Token found: " + token + " "); //obtains assigned token value
        tokenized.add(lineRead + " " + token + " " + string);
  	}
  	public static void printOther(String string)
  	{
  		int token = (((operators.length) + (keywords.length + 1))+4);
  		System.out.print("Line: " + lineRead + " ");
        System.out.print("Token found: " + token + " "); //obtains assigned token value
        tokenized.add(lineRead + " " + token + " " + string);
  	}
  	
  	public static void check(String[] splited)
  	{
  		for(int i = 0; i < splited.length; i++)//going through all substrings
  		{
  			String st = splited[i];
  			
  			if(checkKeywords(st))//checks if lexeme is a keyword 
  			{
  				
  				System.out.println("Lexeme found: " + st);
  			}
  			else if(checkOperators(st))//checks if lexeme is an operator 
  			{
  				System.out.println("Lexeme found: " + st);
  			}
  			else if(checkint(st)) //checks if lexeme is an int
  			{
  				System.out.println("Lexeme found: " + st);
  			}
  			else if(checkFloat(st)) //checks if lexeme is an int
  			{
  				System.out.println("Lexeme found: " + st);
  			}
  			else if(st.startsWith("\""))
  			{
  				int x;
  				    for(x = i; x < splited.length; x++)
  				    {
  				    	if(splited[x].endsWith("\"") && x == i )
  				    		break;
  				    	
  				    	if(splited[x].endsWith("\""))      //loop used to construct literal 
  				    	{
  				    		st = st + " " + splited[x];
  				    		break;
  				    	}
  				    	if(i == x)
  				    		st = splited[x] + " ";
  				    	else 
  				    		st = st + " " + splited[x] + " ";
  				    }
  				i = x; //doesnt analyze same part of scentence again 
  				printLiteral(st); //print literal 
  				System.out.println("Lexeme found: " + st);
  			}
  			else
  			{
  				printOther(st);
  				System.out.println("Lexeme found: " + st);
  			}
  		}
  	}
	
	public static List getList() throws IOException {
		
		 File file = new File("C:\\Users\\Public\\eclipse-workspace\\Interpretor\\src\\ScannedFile"); //file reading from 
		  FileReader fileReader = new FileReader(file);
	      BufferedReader br = new BufferedReader(new FileReader(file));
			String line;
			while ((line = br.readLine()) != null) { //while some of file is remaing 
				StringBuffer stringBuffer = new StringBuffer();
				line = line.trim(); //remove extra white space 
				stringBuffer.append(line); //seperates by white space 
				stringBuffer.append(" "); //space used as delimeter 
				
				String str = stringBuffer.toString();
	 		    String[] splited = str.split(" "); //splits string into several for analysis
			    check(splited);// checks 
			    lineRead++; //used to display the line from file read from 
			}
			fileReader.close();
  		
  		return tokenized;
  		
  	}
}