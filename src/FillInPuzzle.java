import java.io.*;
import java.util.*;

/**
 * @author Simranbanu Roshansha Diwan
 *class to store puzzle details which gives slots to be filled in puzzle
 *we also have number of words to be filled
 *program will try to fit words into given slots
 */
public class FillInPuzzle {
char[][] puzzle; //array to represent current state of puzzle
int noOfColumns; //number of columns in puzzle
int[][] noofCatpos; //array to keep track of count of how many times code has put the value at position in puzzle array
int choices; //number of guesses program has to undo
int[][] intersection; //to keep track of intersections
Stack<int[]> history=new Stack<>(); //x,y,size,direction are stored into stack when we find some place to put word in puzzle
Stack<String> filledWord=new Stack<>(); //stack to store word when we find its place in puzzle
int colno[]; //array to store column numbers
int rowno[]; //array to store row number
int noOfLetters[]; //array to store number of letters that can be filled in slot
HashMap<Integer,ArrayList<Integer>> slotPool; //to store pool of slots
int direction[]; //array to store direction of slot
int noOfRows; //number of rows in puzzle
int noOfWords; //number of words
ArrayList<String> words=new ArrayList<>(); //list of words

	/**
	 * function to load puzzle (pre-processing)
	 * @param stream input stream
	 * @return it will return true if puzzle has been successfully loaded, otherwise false
	 */
	Boolean loadPuzzle(BufferedReader stream) {
		Boolean added=true;
		String inp;
		try {
			inp=stream.readLine(); //reading line
			String[] inpArray=inp.split(" "); //split line by space
			try {
			noOfColumns=Integer.parseInt(inpArray[0]); //store number of columns
			noOfRows=Integer.parseInt(inpArray[1]); //store number of rows
			noOfWords=Integer.parseInt(inpArray[2]); //store number of words
			}catch(NumberFormatException n) {
				return false;
			}
			puzzle=new char[noOfRows][noOfColumns]; //initialize puzzle
			noofCatpos=new int[noOfRows][noOfColumns]; //initialize character count 
			/*initialize whole puzzle array with '*'
			 * '*' represents places that can not be filled
			 * '-' represents places that can be filled*/
			for(int i=0;i<noOfRows;i++) {
				for(int j=0;j<noOfColumns;j++) {
					puzzle[i][j]=' ';  
				}
			}
			colno=new int[noOfWords];
			rowno=new int[noOfWords];
			noOfLetters=new int[noOfWords];
			intersection=new int[noOfRows][noOfColumns];
			direction=new int[noOfWords];
			slotPool=new HashMap<Integer,ArrayList<Integer>>();
			for(int i=0;i<noOfWords;i++) {
				inp=stream.readLine();
				inpArray=inp.split(" "); //split input by space
				char c;
				try {
				colno[i]=Integer.parseInt(inpArray[0]); //save column number
				rowno[i]=noOfRows-Integer.parseInt(inpArray[1])-1; //save row number
				noOfLetters[i]=Integer.parseInt(inpArray[2]); //save count of length of slot
				c=inpArray[3].charAt(0);
				}catch(NumberFormatException n) {
					return false;
				}
				/*put 0 for H or h,put 1 for v or V*/
				if(c=='h'||c=='H') {
					direction[i]=0;
				}else if(c=='v' || c=='V') {
					direction[i]=1;
				}else {
					return false;
				}
				/*put dashes where words will fit*/
				if(direction[i]==0) {
					for(int j=colno[i];j<colno[i]+noOfLetters[i];j++) {
						puzzle[rowno[i]][j]='-';
						intersection[rowno[i]][j]++;
					}
				}
				
				if(direction[i]==1) {
					for(int j=rowno[i];j<rowno[i]+noOfLetters[i];j++) {
						puzzle[j][colno[i]]='-';
						intersection[rowno[i]][j]++;
					}
				}
			}
			/*insert slots in slots pool*/
			for(int i=0;i<noOfWords;i++) {
				words.add(stream.readLine().toLowerCase());
			}
			
			words.sort(null); //sort words
		
		} catch (Exception e) {
			added=false;
			return added;
		}finally {
			try {
				stream.close();
			} catch (Exception e) {
				added=false;
				return added;
			}
		}
		return added;
	}
	
	/**
	 * Method to check that word fits or not at specific horizontal slot
	 * @param i: row number of slot start
	 * @param j: column number of slot start
	 * @param size: size of slot
	 * @param dir: direction of the block available 
	 * @param current:current word to be processed
	 * @param pos: index of slot details arrays
	 * @return : 1 if word is fitted, 0 if word is not fitted
	 */
	int fillHorizontal(int i,int j,int size,int dir,String current,int pos) {
		int flag=0;
		try {
			/*check for conflicts*/
			for(int k=0;k<size;k++) {
				if(puzzle[i][k+j]=='-') {
					flag=1;
				}else if(puzzle[i][k+j]==current.charAt(k)) {
					flag=1;
					intersection[i][k+j]=3;
				}
				else {
					flag=0; // if conflict then set flag=0
					break;
				}
			}
			/*if flag=1 means word can be fitted that positions so fit it*/
			if(flag==1) {
				filledWord.push(current); //push current word to stack of filled words
				words.remove(current);  //remove current word from words list
				history.push(new int[] {pos}); //push index of slot details arrays to history stack
				//fit word to slot
				for(int k=0;k<size;k++) {
					if(puzzle[i][k+j]==current.charAt(k)) {
						noofCatpos[i][k+j]=2;
					}else {
						noofCatpos[i][k+j]=1;
					}
					puzzle[i][k+j]=current.charAt(k);
				}
			}
			return flag;
		}catch(Exception e) {
			flag=0;
			return 0;
		}
	}
	
	/**
	 * Method to check that word fits or not at specific vertical slot
	 * @param i: row number of slot start
	 * @param j: column number of slot start
	 * @param size: size of slot
	 * @param dir: direction of the block available 
	 * @param current:current word to be processed
	 * @param pos: index of slot details arrays
	 * @return : 1 if word is fitted, 0 if word is not fitted
	 */
	int fillVertical(int i,int j,int size, int dir, String current, int pos) {
		int flag=0;
		try {
			/*check for conflicts*/
			for(int k=0;k<size;k++) {
				if(puzzle[i+k][j]=='-') {
					flag=1;
				}else if( puzzle[i+k][j]==current.charAt(k)) {
					flag=1;
					intersection[i+k][j]=3;
				}else {
					flag=0; // if conflict then set flag=0
					break;
				}
			}
			/*if flag=1 means word can be fitted that positions so fit it*/
			if(flag==1) {
				filledWord.push(current); //push current word to stack of filled words
				words.remove(current);  //remove current word from words list
				history.push(new int[] {pos}); //push index of slot details arrays to history stack
				//fit word to slot
				for(int k=0;k<size;k++) {
					if(puzzle[i+k][j]==current.charAt(k)) {
						noofCatpos[i+k][j]=2;
					}else {
						noofCatpos[i+k][j]=1;
					}
					puzzle[i+k][j]=current.charAt(k);
				}
			}
			return flag;
		}catch(Exception e) {
			flag=0;
			return flag;
		}
	}
	
	
	/**
	 * function to again inserting spaces empty where wrong word was fitted
	 * @param i : row number of start position of slot
	 * @param j :row number of start position of slot
	 * @param dir : direction of slot
	 * @param size : length of slot
	 */
	void addDash(int i,int j,int dir,int size) {
		if(dir==0) {
			for(int k=j;k<j+size;k++) {
				if(noofCatpos[i][k]==2) {
					noofCatpos[i][k]=1; //dont put '-' if the word is at intersection and both the words and insert 1 in count array
				}
				else {
					puzzle[i][k]='-'; //put '-'
				}
			}
		}else {
			for(int k=i;k<i+size;k++) {
				if(noofCatpos[k][j]==2) {
					noofCatpos[k][j]=1;
				}else {
					puzzle[k][j]='-';
				}
			}
		}
	} 
	
	/**
	 * function to solve puzzle
	 * @return true is puzzle solved otherwise return false
	 */
	Boolean solve() {
		Boolean solved=true;
		try{
		/*process the words until there are unprocessed words*/
		while(words.size()>0)
		{
			String last=null;
			String current=words.get(0);
			/*pop from stack if last word popped matches the current word*/
			if(current.equals(last)) {
				int pos=history.pop()[0];
				addDash(rowno[pos],colno[pos],direction[pos],noOfLetters[pos]);
				last=filledWord.pop();
				words.add(last);
				choices++; //count of backtrack
			}
			int len=current.length();  //length of current word
			int flag2=0; //word is fitted horizontally then 1 otherwise 0
			int flag3=0;  //word is fitted vertically then 1 otherwise 0
			for(int j=0;j<rowno.length;j++) {
				//fit word horizontally if it can be fitted
				if((direction[j]==0) && noOfLetters[j]==len) {
					flag2=fillHorizontal(rowno[j],colno[j],len,direction[j],current,j);
				}//fit word vertically 
				else if((direction[j]==1 || direction[j]==1) && noOfLetters[j]==len) {
					flag3=fillVertical(rowno[j],colno[j],len,direction[j],current,j);
				}//if the slot is found then break
				if(flag2==1 || flag3==1) {
					break;
				}
			}//if the horizontal or vertical slot can not be found for word then trace back
			if(flag2==0 && flag3==0) {
					int pos=history.pop()[0]; //pop the last changes which u have made to puzzle 
					addDash(rowno[pos],colno[pos],direction[pos],noOfLetters[pos]); 
					last=filledWord.pop();
					words.add(last); //add word to word list which stores word which are not yet processed
					choices++;
			}
		}
		
		//return false if there are any dashes remaining
		for(int i=0;i<noOfRows;i++) {
			for(int j=0;j<noOfColumns;j++) {
				if(puzzle[i][j]=='-') {
					return false;
				}
			}
		}
		return solved;
		}catch(Exception e) {
			solved=false;
			return solved;
		}
	}
	
	/*function to return choices*/
	int choices() {
		return choices;
	}
	
	/*function to copy matrix 2 into matrix 1*/
	void copyMatrix(char[][] matrix1,char[][] matrix2) {
		for(int i=0;i<noOfRows;i++) {
			for(int j=0;j<noOfColumns;j++) {
				matrix1[i][j]=matrix2[i][j];
			}
		}
	}
	
	/*print the puzzle*/
	void print(PrintWriter outstream) {
		
		for(int i=0;i<noOfRows;i++) {
			for(int j=0;j<noOfColumns;j++) {
				outstream.print(puzzle[i][j]);
				outstream.flush();
			}
			outstream.println();
			outstream.flush();
		}
	}
}