// Lab Ex3 2018- Anthony Moore G00170900
// Corrib water usage

// imports
import java.io.*;
import java.util.*;

public class MooreAnthony18LabEx3
{
    public static void main(String[] args)throws FileNotFoundException
    {
		// Constants
		final int MAX_REC = 70;
		final int EOF_1 = -1;
		final int MAX_2DIM_COL = 5;
		final String SENTINEL_QUIT = "QUIT";

		// File Objects
		Scanner inWaterMasterFile = new Scanner(new FileReader("WaterMaster.dat"));
		Scanner inWaterTxFile = new Scanner(new FileReader("WaterTx.dat"));
		PrintWriter outWaterRejectRep = new PrintWriter("WaterRejected.dat");

		// File Variables
		int custNumRead;
		char custTypeRead;
		String custRegRead;
		char custStatRead;
		int acceptCount = 0;
		int rejCount = 0;
		String restOfFile;

		int txCustNum;
		String txCode;
		int txQuarter;
		int txAmount;
		String txName;

		// 1/2 dim Arrays
		int[][] custNumArr = new int [MAX_REC][MAX_2DIM_COL];
		char [] custTypeArr = new char [MAX_REC];
		String[] custRegArr = new String [MAX_REC];
		char [] custStatArr = new char [MAX_REC];
		String[] custNamesArr = new String [MAX_REC];

		// Other Variables
		int i;
		int j;
		int pos;
		boolean found = false;
		boolean validTxCode = false;
		String findCustName, errorMsg;
		String firstName, secondName, fullName;
		int txCounter = 0;
		int txRejCounter = 0;
		int txAccCounter= 0;
		char option;
		int optionNum;
		Scanner console = new Scanner(System.in);

		// while/for Read Master File & Populate arrays
		//inital read
		custNumRead = inWaterMasterFile.nextInt();
		while (custNumRead != EOF_1)
		{
			custTypeRead = inWaterMasterFile.next().toUpperCase().charAt(0);
			custRegRead = inWaterMasterFile.next().toLowerCase();
			custStatRead = inWaterMasterFile.next().toUpperCase().charAt(0);
			//reject non active
			if(custStatRead != 'A')
			{
				rejCount++;
				restOfFile = inWaterMasterFile.nextLine();
			}
			else
			{
				//active only
				custNumArr[acceptCount][0] = custNumRead;
				custTypeArr[acceptCount] = custTypeRead;
				custRegArr[acceptCount] = custRegRead;
				custStatArr[acceptCount] = custStatRead;
				for (i = 1; i < MAX_2DIM_COL; i++)//start at 1 because 0 is the IDnumber column
				{
					custNumArr[acceptCount][i] = inWaterMasterFile.nextInt();
				}
				firstName = inWaterMasterFile.next();
				secondName = inWaterMasterFile.next();
				fullName = firstName + " " + secondName;
				custNamesArr[acceptCount] = fullName;
				++acceptCount;//increase counter
			}//else
			//subsequent read
			custNumRead = inWaterMasterFile.nextInt();
		}//while

		// Nested fors/Output/Verify Master File arrays
		//header with counters
		System.out.println("Anthony Moore - Water Maintenance - Lab Ex 3 - Feb 2018");
		System.out.println("============================================================");
		System.out.println("Customer Count:  " + (acceptCount + rejCount) + "  Rejected:  " + rejCount + "  Accepted:  " + acceptCount);
		System.out.println();
		//main header
		System.out.println("Cust Cust Cust   Cust    Q1    Q2    Q3    Q4          Cust");
		System.out.println(" Num Stat Type Region Usage Usage Usage Usage          Name");
		System.out.println("============================================================");
		//output arrays with nested for loop
		for (i = 0; i < acceptCount; i++)
		{
			//values before 2dim array
			System.out.printf("%4d %3c %3c %-7s",custNumArr[i][0],custStatArr[i],custTypeArr[i],custRegArr[i]);
			for (j = 1; j < MAX_2DIM_COL; j++)
			{
				//2dim array values
				System.out.printf("%6d",custNumArr[i][j]);
			}
			//values after 2dim array
			System.out.printf("%16s \n",custNamesArr[i]);
		}//for
		System.out.println("============================================================\n");

		// while/Output/Verify Tx File (without rejected Tx)
		System.out.println("Cust   TX  TX   TX  New");
		System.out.println(" Num Code Qtr  Amt  Name");
		System.out.println("=============================");
		//inital read
		txCustNum = inWaterTxFile.nextInt();
		while (txCustNum != EOF_1)
		{
			txCounter++;
			txCode = inWaterTxFile.next().toLowerCase();
			txQuarter = inWaterTxFile.nextInt();
			txAmount = inWaterTxFile.nextInt();
			txName = inWaterTxFile.nextLine().toLowerCase().trim();

			System.out.printf("%4d %6s %4d %4d %35s\n",txCustNum, txCode, txQuarter, txAmount, txName);
			//subsequent read
			txCustNum = inWaterTxFile.nextInt();
		}//while
		//footer with count
		System.out.println("\nTx:  " + txCounter + " Reject:  " + txRejCounter + " Accept:   " + txCounter);//no rejects yet

		// while/Output/Verify Tx File (with rejected Tx)
		//close/reOpen infile
		inWaterTxFile.close();
		inWaterTxFile = new Scanner(new FileReader("WaterTx.dat"));

		//header for reject file
		outWaterRejectRep.println("Mismatched Rejected Tx Report");
		outWaterRejectRep.println("=============================");
		outWaterRejectRep.println("Cust   TX  TX   TX  New");
		outWaterRejectRep.println(" Num Code Qtr  Amt  Name");
		outWaterRejectRep.println("=============================");

		//header for updated transaction
		System.out.println("\n=============================");
		System.out.println("Cust   TX  TX   TX  New");
		System.out.println(" Num Code Qtr  Amt  Name");
		System.out.println("=============================");

		//inital read
		txCustNum = inWaterTxFile.nextInt();
		while (txCustNum != EOF_1)
		{
			txCode = inWaterTxFile.next().toLowerCase();
			txQuarter = inWaterTxFile.nextInt();
			txAmount = inWaterTxFile.nextInt();
			txName = inWaterTxFile.nextLine().toLowerCase().trim();

			pos=-1;
			found = false;
			//search for customerNumber
			while (pos < acceptCount -1 && found == false)
			{
				++pos;
				if (custNumArr[pos][0] == txCustNum)
				{
					found = true;
				}
			}//while
			if (found == false)
			{
				txRejCounter++;
				errorMsg = " - Mismatched Customer ID";
				outWaterRejectRep.printf("%4d %4s %3d %4d %35s %-20s\n",txCustNum, txCode, txQuarter, txAmount, txName,errorMsg);
			}
			else
			{
				validTxCode = true;
				txAccCounter++;
				//check txCode and update array with the amounts
				switch(txCode)
				{
					case "set":
						if (txQuarter >= 1 && txQuarter <= 4)
						{
							custNumArr [pos][txQuarter] = txAmount;
						}
						else
						{
							txRejCounter++;
							txAccCounter--;
							validTxCode = false;
							errorMsg = " - Invalid Quarter";
							outWaterRejectRep.printf("%4d %4s %3d %4d %35s %-20s\n",txCustNum, txCode, txQuarter, txAmount, txName,errorMsg);
						}
						break;
					case "add":
						if (txQuarter >= 1 && txQuarter <= 4)
						{
							custNumArr [pos][txQuarter] += txAmount;
						}
						else
						{
							txRejCounter++;
							txAccCounter--;
							validTxCode = false;
							errorMsg = " - Invalid Quarter";
							outWaterRejectRep.printf("%4d %4s %3d %4d %35s %-20s\n",txCustNum, txCode, txQuarter, txAmount, txName,errorMsg);
						}
						break;
					case "sub":
						if (txQuarter >= 1 && txQuarter <= 4)//check between 1 and 4
						{
							custNumArr [pos][txQuarter] -= txAmount;
							if(custNumArr [pos][txQuarter] < 0)
							{
								custNumArr [pos][txQuarter] = 0;
							}
						}
						else
						{
							txRejCounter++;
							txAccCounter--;
							validTxCode = false;
							errorMsg = " - Invalid Quarter";
							outWaterRejectRep.printf("%4d %4s %3d %4d %35s %-20s\n",txCustNum, txCode, txQuarter, txAmount, txName,errorMsg);
						}
						break;
					case "nil":
						for (j = 1; j < MAX_2DIM_COL; j++)
						{
							//2dim array values
							custNumArr[pos][j] = 0;
						}//for
						break;
					case "fix":
						for (j = 1; j < MAX_2DIM_COL; j++)
						{
							//2dim array values
							custNumArr[pos][j] = txAmount;
						}//for
						break;
					case "reg":
						switch (txQuarter)
						{
							case 1:
								custRegArr[pos] = "north";
								break;
							case 2:
								custRegArr[pos] = "south";
								break;
							case 3:
								custRegArr[pos] = "east";
								break;
							case 4:
								custRegArr[pos] = "west";
								break;
							default:
						txRejCounter++;
						txAccCounter--;
							validTxCode = false;
							errorMsg = " - Invalid Quarter";
							outWaterRejectRep.printf("%4d %4s %3d %4d %35s %-20s\n",txCustNum, txCode, txQuarter, txAmount, txName,errorMsg);
						}//switch
						break;
					case "nme":
						custNamesArr[pos] = txName;
						break;
					case "st":
						if (txAmount >= 1 && txAmount <=2)
						{
							switch (txAmount)
							{
								case 1:
									custStatArr[pos] = 'S';
									break;
								case 2:
									custStatArr[pos] = 'X';
									break;
							}//switch
						}
						else
						{
							txRejCounter++;
							txAccCounter--;
							validTxCode = false;
							errorMsg = " - Must be 1/Suspended or 2/Ex";
							outWaterRejectRep.printf("%4d %4s %3d %4d %35s %-20s\n",txCustNum, txCode, txQuarter, txAmount, txName,errorMsg);
						}
						break;
					default:
						txRejCounter++;
						txAccCounter--;
						validTxCode = false;
						errorMsg = " - Invalid Tx Code";
						outWaterRejectRep.printf("%4d %4s %3d %4d %35s %-20s\n",txCustNum, txCode, txQuarter, txAmount, txName,errorMsg);
				}//switch
				//output valid transactions
				if(validTxCode == true)
				{
					System.out.printf("%4d %4s %3d %4d %35s\n",txCustNum, txCode, txQuarter, txAmount, txName);
				}
			}//else
			//subsequent read
			txCustNum = inWaterTxFile.nextInt();
		}//while
		//footer
		System.out.println("\nTx:  " + txCounter + " Reject:  " + txRejCounter + " Accept:   " + txAccCounter);

		//output updated arrays
		System.out.println("\nCust Cust Cust   Cust    Q1    Q2    Q3    Q4          Cust");
		System.out.println(" Num Stat Type Region Usage Usage Usage Usage          Name");
		System.out.println("============================================================");
		//output arrays with nested for loop
		for (i = 0; i < acceptCount; i++)
		{
			//values before 2dim array
			System.out.printf("%4d %3c %3c %-7s",custNumArr[i][0],custStatArr[i],custTypeArr[i],custRegArr[i]);
			for (j = 1; j < MAX_2DIM_COL; j++)
			{
				//2dim array values
				System.out.printf("%6d",custNumArr[i][j]);
			}
			//values after 2dim array
			System.out.printf("%16s \n",custNamesArr[i]);
		}//for
		System.out.println("============================================================\n");

		//name string search
		System.out.print("Enter Customer Name (or Quit): ");
		findCustName = console.nextLine();
		while (findCustName.equalsIgnoreCase(SENTINEL_QUIT) == false)
		{
			found = false;//reset boolean
			i=-1;
			//search for customerNumber
			while (i < acceptCount - 1 && found == false)
			{
				++i;
				if (custNamesArr[i].equalsIgnoreCase(findCustName))
				{
					found = true;
				}
			}//while
			//output search results
			if (found == false)
			{
				System.out.println("  Sorry Name NOT Found - Please try again!");
			}
			else
			{
				//prompt user for input
				System.out.println("  Select A/dd, N/ame, R/egion, S/earch, V/iew or eXit ");
				option = console.next().toUpperCase().charAt(0);
				switch (option)
				{
					case 'A':
						break;
					case 'N':
					do
					{
						System.out.println("Current Name: " + custNamesArr[i]);
						System.out.print("Enter new Name(1..30): ");
						firstName = console.next();
						secondName = console.next();
						fullName = firstName + " " + secondName;
					}while(fullName.length() < 1 || fullName.length() > 30);
						custNamesArr[i] = fullName;
						break;
					case 'R':
						do
						{
							System.out.println("Current Region: " + custRegArr[i]);
							System.out.print("Enter new Region (1/North 2/South 3/East 4/west): ");
							optionNum = console.nextInt();
							switch (optionNum)
							{
								case 1:
									custRegArr[i] = "north";
									break;
								case 2:
									custRegArr[i] = "south";
									break;
								case 3:
									custRegArr[i] = "east";
									break;
								case 4:
									custRegArr[i] = "west";
									break;
								default:
									//errorMsg = " - Invalid Quarter";
							}//switch
						}while(optionNum > 4 || optionNum < 1);
						break;
					case 'S':
						break;
					case 'V':
						System.out.println("\nCust Cust Cust   Cust    Q1    Q2    Q3    Q4          Cust");
						System.out.println(" Num Stat Type Region Usage Usage Usage Usage          Name");
						System.out.println("============================================================");
						//values before 2dim array
						System.out.printf("%4d %3c %3c %-7s",custNumArr[i][0],custStatArr[i],custTypeArr[i],custRegArr[i]);
						for (j = 1; j < MAX_2DIM_COL; j++)
						{
							//2dim array values
							System.out.printf("%6d",custNumArr[i][j]);
						}
						//values after 2dim array
						System.out.printf("%16s \n",custNamesArr[i]);
						break;
					case 'X':
						break;
				}//switch
			}//else
			console.nextLine();//flush buffer after string inputs
			System.out.print("Enter Customer Name (or Quit): ");
			findCustName = console.nextLine();
		}//while

		//main header (final output)
		System.out.println("Cust Cust Cust   Cust    Q1    Q2    Q3    Q4          Cust");
		System.out.println(" Num Stat Type Region Usage Usage Usage Usage          Name");
		System.out.println("============================================================");
		//output arrays with nested for loop
		for (i = 0; i < acceptCount; i++)
		{
			//values before 2dim array
			System.out.printf("%4d %3c %3c %-7s",custNumArr[i][0],custStatArr[i],custTypeArr[i],custRegArr[i]);
			for (j = 1; j < MAX_2DIM_COL; j++)
			{
				//2dim array values
				System.out.printf("%6d",custNumArr[i][j]);
			}
			//values after 2dim array
			System.out.printf("%16s \n",custNamesArr[i]);
		}//for
		System.out.println("============================================================\n");

		//close files
		outWaterRejectRep.close();
		inWaterMasterFile.close();
		inWaterTxFile.close();
    }  // main

} // MooreAnthony18LabEx3