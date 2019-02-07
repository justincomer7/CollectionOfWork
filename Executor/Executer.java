import java.awt.List;
import java.io.*; // needed for buffered reader 
import java.util.ArrayList;
import java.util.LinkedList;

public class Executer {
	
	static List execList = new List(); // all statements necessary for execution
	static List IDs = new List(); //Identifiers besides function names
	static ArrayList values = new ArrayList(); //Values, they will have a corresponding index 
	//as there variable name 
	
	static int unit = 0; //will keep position during execution 
	
	public static void execute() //checking statments 
	{
		while (unit < execList.getItemCount()){
		switch  (execList.getItem(unit)) {
		case "set":
			set(); 
			break;
		case "display":
			unit++;
			if(checkIdentifier(execList.getItem(unit))) {
				int index = getIndex(execList.getItem(unit)); //print value of identifier
				System.out.println(values.get(index));
			}
			else
				System.out.println(execList.getItem(unit)); //print literal
			unit++;
			break; 
		case "if" :
			unit++;
			if(doBoolean())
			{
				List subList = new List(); //statements that will be executed
				int size = 0;
				
				while(!execList.getItem(unit).equals("else"))
				{
					subList.add(execList.getItem(unit));
					size++;
					unit++;
				}
				executeSubStatements(size, subList);
				while(!execList.getItem(unit).equals("endif"))
				{
					unit++;
				}
				
			}
			else
			{
				List subList = new List(); //statements that will be executed
				int size = 0;
				
				while(!execList.getItem(unit).equals("else"))
				{
					unit++;
				}
				while(!execList.getItem(unit).equals("endif"))
				{
					subList.add(execList.getItem(unit));
					size++;
					unit++;
				}
				executeSubStatements(size, subList);
			}
			unit++;
			break;
		case "repeat": 
			unit++;
			
			List subList = new List(); //statements that will be executed
			int size = 0;
			
			//will assemble the repeated statement 
			while(!execList.getItem(unit).equals("until"))
			{
				subList.add(execList.getItem(unit));
				size++;
				unit++;
			}
			
			unit++;
			while(!doBoolean())
			{
				while(!execList.getItem(unit).equals("until"))
					unit--;
				
				//System.out.println(execList.getItem(unit));
				executeSubStatements(size, subList);
				unit++;
			}
			unit++;
			break;
			}
		}
		
	}
	public static void set()
	{
		unit++;
		if(!checkIdentifier(execList.getItem(unit))) {
			IDs.add(execList.getItem(unit)); //if id is not in table/ it is added 
			unit++;
			if(checkIdentifier(execList.getItem(unit)))
				values.add(doMath(Integer.parseInt( (String) values.get(getIndex(execList.getItem(unit)))))); //Mathematical operation are now being done
																											  //once value is found, it will be added to value table																									
			else																									
				values.add(doMath(Integer.parseInt(execList.getItem(unit))));	
		}
		else {
			int index = getIndex(execList.getItem(unit)); //if id exists, index is found so the value in value table can be changed 
			unit++;
			if(checkIdentifier(execList.getItem(unit)))                                                                   //Mathematical operation are now being done
				values.set(index, doMath(Integer.parseInt((String) values.get(getIndex(execList.getItem(unit))))));       //once value is found, it will be changed in value table 
			else
				values.set(index, doMath(Integer.parseInt(execList.getItem(unit))));	
		}
	}
	public static void executeSubStatements(int size, List sub)
	{
		//this will execute nested statements 
		
		int exec = 0;
		while (exec < size) {
			
			switch (sub.getItem(exec)) {
			case "set":
				while(!execList.getItem(unit).equals("set"))
					unit--;//back up unit so that the statement can be executed 
				set(); 
				break;
			case "display":
				exec++;
				if(checkIdentifier(sub.getItem(exec))) {
					int index = getIndex(sub.getItem(exec)); //print value of identifier
					System.out.println(values.get(index));
				}
				else
					System.out.println(sub.getItem(exec)); //print literal
				exec++;
				break;
			}
			exec++;
		}
	}
	
	public static boolean doBoolean()

	{
		/*
		 * This method will return the value of the boolean statement 
		 * The first thng this statment will do is calculate the value of the left side of the statement
		 * After, using case statements, it will be matched to the corresponding operator 
		 * The right side of the statement will bethen be calculated 
		 * The value will then be returned 
		 */
		String First; //left side 
		String Second; //right side 
		if(checkIdentifier(execList.getItem(unit)))
		{
			int value = Integer.parseInt( (String) values.get(getIndex(execList.getItem(unit))));
			First = (doMath(value));
		}
		else {
			int value = Integer.parseInt((execList.getItem(unit)));
			First = (doMath(value));
		}
		switch (execList.getItem(unit)){
		
		case "==":
		{
			unit++;
			if(checkIdentifier(execList.getItem(unit)))
			{
				int value = Integer.parseInt( (String) values.get(getIndex(execList.getItem(unit))));
				Second = (doMath(value));
			}
			else {
				int value = Integer.parseInt((execList.getItem(unit)));
				Second = (doMath(value));
			}
			
			if(Integer.parseInt(First) == Integer.parseInt(Second))
				return true;
			else
				return false;
		}
		case "~=":
		{
			unit++;
			if(checkIdentifier(execList.getItem(unit)))
			{
				int value = Integer.parseInt( (String) values.get(getIndex(execList.getItem(unit))));
				Second = (doMath(value));
			}
			else {
				int value = Integer.parseInt((execList.getItem(unit)));
				Second = (doMath(value));
			}
			
			if(Integer.parseInt(First) != Integer.parseInt(Second))
				return true;
			else
				return false;
		}
		case ">=":
		{
			unit++;
			if(checkIdentifier(execList.getItem(unit)))
			{
				int value = Integer.parseInt( (String) values.get(getIndex(execList.getItem(unit))));
				Second = (doMath(value));
			}
			else {
				int value = Integer.parseInt((execList.getItem(unit)));
				Second = (doMath(value));
			}
			
			if(Integer.parseInt(First) >= Integer.parseInt(Second))
				return true;
			else
				return false;
		}
		case "<=":
		{
			unit++;
			if(checkIdentifier(execList.getItem(unit)))
			{
				int value = Integer.parseInt( (String) values.get(getIndex(execList.getItem(unit))));
				Second = (doMath(value));
			}
			else {
				int value = Integer.parseInt((execList.getItem(unit)));
				Second = (doMath(value));
			}
			
			if(Integer.parseInt(First) <= Integer.parseInt(Second))
				return true;
			else
				return false;
		}
		case ">":
		{
			unit++;
			if(checkIdentifier(execList.getItem(unit)))
			{
				int value = Integer.parseInt( (String) values.get(getIndex(execList.getItem(unit))));
				Second = (doMath(value));
			}
			else {
				int value = Integer.parseInt((execList.getItem(unit)));
				Second = (doMath(value));
			}
			
			if(Integer.parseInt(First) > Integer.parseInt(Second))
				return true;
			else
				return false;
		}
		case "<":
		{
			unit++;
			if(checkIdentifier(execList.getItem(unit)))
			{
				int value = Integer.parseInt( (String) values.get(getIndex(execList.getItem(unit))));
				Second = (doMath(value));
			}
			else {
				int value = Integer.parseInt((execList.getItem(unit)));
				Second = (doMath(value));
			}
			
			if(Integer.parseInt(First) < Integer.parseInt(Second))
				return true;
			else
				return false;
		}
		}
		
		return false;
	}
	
	public static String doMath(int value)
	{
		/* In this method, an initial value is passed 
		 * If the next unit is an operator, the case corresponding to the operator will be executed
		 * If the next operand is a number, the operation will be executed, and function recursively called
		 * If the next oeprand is an identifier, its value will be pulled from the value table, and the same action completed
		 * If their is now proceeding operator, than the value passed will be returned, this is the base case for the recursion
		 */
		unit++;
		switch (execList.getItem(unit)){
		case "+" :
			unit++;
			if(checkIdentifier(execList.getItem(unit)))
			{
				int index = getIndex(execList.getItem(unit));
				return Integer.toString((value + Integer.parseInt(doMath((int) values.get(index)))));
			}
			else
			return Integer.toString((value + Integer.parseInt(doMath(Integer.parseInt(execList.getItem(unit))))));
		}
		switch (execList.getItem(unit)){
		case "-" :
			unit++;
			if(checkIdentifier(execList.getItem(unit)))
			{
				int index = getIndex(execList.getItem(unit));
				return Integer.toString((value - Integer.parseInt(doMath((int) values.get(index)))));
			}
			else
			return Integer.toString((value - Integer.parseInt(doMath(Integer.parseInt(execList.getItem(unit))))));
		}
		switch (execList.getItem(unit)){
		case "*" :
			unit++;
			if(checkIdentifier(execList.getItem(unit)))
			{
				int index = getIndex(execList.getItem(unit));
				return Integer.toString((value * Integer.parseInt(doMath((int) values.get(index)))));
			}
			else
			return Integer.toString((value * Integer.parseInt(doMath(Integer.parseInt(execList.getItem(unit))))));
		}
		switch (execList.getItem(unit)){
		case "/" :
			unit++;
			if(checkIdentifier(execList.getItem(unit)))
			{
				int index = getIndex(execList.getItem(unit));
				return Integer.toString((value / Integer.parseInt(doMath(Integer.parseInt( (String) values.get(index))))));
			}
			else
			return Integer.toString((value / Integer.parseInt(doMath(Integer.parseInt(execList.getItem(unit))))));
		}
		
		return Integer.toString(value);
		
	}
	
	public static int getIndex(String string) { //scans id table
	    for (int i = 0; i < IDs.getItemCount(); i++)
	      if (string.equals(IDs.getItem(i)))
	        return i;//returns index
	    
	    return 0;
	    
	  }
	
	public static boolean checkIdentifier(String string) { //scans id table
	    for (int i = 0; i < IDs.getItemCount(); i++)
	      if (string.equals(IDs.getItem(i)))
	        return true; //returns whether or not found 
	    return false;
	  }

	
	// M A I N  M E T H O D \\
	public static void main(String[] args) {
		
		Parser parsed = new Parser(); //instantiate new parser
	    execList = parsed.parse(); //get execution list
	    System.out.println("");
	    System.out.println("");
	    System.out.println("OUTPUT:");
		execute();

	}

}