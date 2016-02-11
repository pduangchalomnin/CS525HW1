import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedHashMap;

import java.util.Scanner;

// TODO: Auto-generated Javadoc
/**
 * This Program intention is to retrieve 2 text files which we assume as 2 database tables and
 * perform 3 database operations i.e. Cartesian product, natural join, left outer join.
 * 
 * File pattern:
 * Header1,Header2,...,HeaderN
 * col1row1,col2row1,...,colNrow1
 * col1row2,col2row2,...,colNrow2
 * 
 * !!Limitation#1 Given file have to be complete in each row!!
 * !!Limitation#2 Assume all given data is a string type (Compare schema only by name)!!
 * 
 * @author Patipat Duangchalomnin A20351140
 */
public class DBMS_HW1 {

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		List<LinkedHashMap<String,String>> input1 = null;
		List<LinkedHashMap<String,String>> input2 = null;

		while(input1 == null)
		{
			System.out.print("Please input file#1 name (Table R):");
			input1 = createLinkedHashMap(sc.nextLine());
		}
		while(input2 == null)
		{
			System.out.print("Please input file#2 name (Table S):");
			input2 = createLinkedHashMap(sc.nextLine());
		}

		int input = 0;
		while(input != 5)
		{
			System.out.println("*********************************");
			System.out.println("*    Please select operation    *");
			System.out.println("*********************************");
			System.out.println("1. Cartesian Product");
			System.out.println("2. Netural Join");
			System.out.println("3. Left Outer Join");
			System.out.println("4. Change input files");
			System.out.println("5. Exit");
			System.out.print("Please enter your choice (1-4): ");
			input = sc.nextInt();
			if(input == 1)
			{
				saveFile("outputCartesian.txt",cartesianProduct(input1, input2));
			}
			else if(input==2)
			{
				saveFile("outputNeturalJoin.txt", naturalJoin(input1, input2));
			}
			else if(input==3)
			{
				saveFile("outputLeftOuterJoin.txt", leftJoin(input1, input2));
			}
			else if(input==4)
			{
				input1=null;
				input2=null;
				sc.nextLine();
				while(input1 == null)
				{
					System.out.print("Please input file#1 name (Table R):");
					String input1Path = sc.nextLine();
					input1 = createLinkedHashMap(input1Path);
				}
				while(input2 == null)
				{
					System.out.print("Please input file#2 name (Table S):");
					String input2Path = sc.nextLine();
					input2 = createLinkedHashMap(input2Path);
				}
			}
			else if(input==5)
			{
				System.out.println("Exiting program");
			}
			else
			{
				System.out.println("Incorrect input please try again.");
			}
		}
		sc.close();

	}

	/**
	 * Create Entity of given file into memory.
	 *
	 * @param fileName File's Name wanted to be read
	 * @return Entity of file
	 */
	public static List<LinkedHashMap<String,String>> createLinkedHashMap(String fileName)
	{
		List<LinkedHashMap<String,String>> output = new ArrayList<LinkedHashMap<String,String>>();
		int lineCount = 0;
		String[] columnsName=null;

		try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
			String line;
			while ((line = br.readLine()) != null) {
				if(lineCount == 0)
				{
					columnsName = line.split(",");
				}
				else
				{
					String[] value = line.split(",");
					LinkedHashMap<String,String> row = new LinkedHashMap<>();
					for(int i=0; i<value.length;i++)
					{
						row.put(columnsName[i], value[i]);
					}
					output.add(row);
				}
				lineCount++;
			}
		} catch (Exception e) {
			System.out.println("Cannot read file "+fileName+" "+e.toString());
			return null;
		} 
		System.out.println(lineCount-1+" tuple(s) loaded");
		return output;
	}

	/**
	 * Perform Cartesian product between 2 entities.
	 *
	 * @param entity1 the entity1
	 * @param entity2 the entity2
	 * @return Cartesian product
	 */
	public static List<LinkedHashMap<String,String>> cartesianProduct(List<LinkedHashMap<String,String>> entity1,List<LinkedHashMap<String,String>> entity2) {
		List<LinkedHashMap<String,String>> output = new ArrayList<LinkedHashMap<String,String>>();
		Iterator<LinkedHashMap<String,String>> itEntity1 = entity1.iterator();
		while(itEntity1.hasNext())
		{
			LinkedHashMap<String,String> leftTmp = itEntity1.next();
			Iterator<LinkedHashMap<String,String>> itEntity2 = entity2.iterator();
			while(itEntity2.hasNext())
			{
				LinkedHashMap<String,String> rightTmp = itEntity2.next();
				LinkedHashMap<String,String> conTmp = new LinkedHashMap<String,String>();
				Iterator<String> itLeftHeader = leftTmp.keySet().iterator();
				while(itLeftHeader.hasNext())
				{
					String leftHeader = itLeftHeader.next();
					Iterator<String> itRightHeader = rightTmp.keySet().iterator();
					while(itRightHeader.hasNext())
					{
						String rightHeader =itRightHeader.next();
						if(leftHeader.equals(rightHeader))
						{
							leftTmp = replaceKey(leftHeader, "R."+leftHeader, leftTmp);
							rightTmp = replaceKey(rightHeader,"S."+rightHeader, rightTmp);
						}
					}
				}
				conTmp.putAll(leftTmp);
				conTmp.putAll(rightTmp);
				output.add(conTmp);
			}
		}
		System.out.println("Done perfom cartesian product");
		return output;
	}

	/**
	 * Prints entity's tuples.
	 *
	 * @param entity entity which wanted to be print
	 */
	public void printEntity(List<LinkedHashMap<String,String>> entity) {
		Iterator<LinkedHashMap<String,String>> iterator = entity.iterator();
		int counter = 0;
		while(iterator.hasNext())
		{
			LinkedHashMap<String,String> line = iterator.next();
			Iterator<String> i =line.keySet().iterator();
			System.out.println("Line:"+ ++counter);
			while(i.hasNext())
			{
				String key = i.next();
				System.out.println(key + " - "+ line.get(key));
			}
		}

	}

	/**
	 * Save entity to file.
	 *
	 * @param fileName file name wanted to be create
	 * @param outData entity wanted to be persist
	 */
	public static void saveFile(String fileName,List<LinkedHashMap<String,String>> outData) {
		PrintWriter pw;
		try {
			pw = new PrintWriter(new FileOutputStream(fileName));
			String output = "";

			//Create Header
			LinkedHashMap<String,String> header = outData.get(0);
			Iterator<String> itHeader = header.keySet().iterator();
			while(itHeader.hasNext())
			{
				output += itHeader.next()+",";
			}
			output = output.substring(0, output.length()-1)+"\r\n";

			//Create Body
			Iterator<LinkedHashMap<String, String>> itBody = outData.iterator();
			while(itBody.hasNext())
			{
				LinkedHashMap<String,String> line = itBody.next();
				Collection<String> c = line.values();
				Iterator<String> i =c.iterator();
				while(i.hasNext())
				{
					String value = i.next();
					output += value+",";
				}
				output = output.substring(0, output.length()-1)+"\r\n";
			}
			pw.write(output);
			pw.close();
			System.out.println("Saved into file "+fileName);
		} catch (FileNotFoundException e) {
			System.out.println("Cannot save file");
		}

	}

	/**
	 * Replace key into new key name.
	 *
	 * @param oldKey the old key
	 * @param newKey the new key
	 * @param target the target entity
	 * @return entity which replaced key name
	 */
	public static LinkedHashMap<String,String> replaceKey(String oldKey,String newKey,LinkedHashMap<String, String> target)
	{
		LinkedHashMap<String, String> tmp = new LinkedHashMap<>();
		Iterator<String> itTarget = target.keySet().iterator();
		while(itTarget.hasNext())
		{
			String key = itTarget.next();
			if(key == oldKey)
			{
				tmp.put(newKey, target.get(oldKey));
			}
			else
			{
				tmp.put(key,target.get(key));
			}
		}
		return tmp;
	}

	/**
	 * Perform a Natural join to given entities.
	 *
	 * @param entity1 
	 * @param entity2
	 * @return entity which completed natural join
	 */
	public static List<LinkedHashMap<String, String>> naturalJoin(List<LinkedHashMap<String, String>> entity1 ,List<LinkedHashMap<String, String>> entity2)
	{
		boolean commonAtt = false;
		List<LinkedHashMap<String, String>> output = new ArrayList<LinkedHashMap<String, String>>();
		Iterator<LinkedHashMap<String, String>> rEntity = entity1.iterator();
		while(rEntity.hasNext())
		{
			LinkedHashMap<String,String> rTuple = rEntity.next();
			Iterator<LinkedHashMap<String, String>> sEntity = entity2.iterator();
			while(sEntity.hasNext())
			{
				boolean notEqual = false;
				LinkedHashMap<String,String> sTuple = sEntity.next();
				boolean haveToAdd = false;
				Iterator<String> rAtt = rTuple.keySet().iterator();
				while(rAtt.hasNext())
				{
					String rHeader = rAtt.next();
					Iterator<String> sAtt = sTuple.keySet().iterator();
					while(sAtt.hasNext())
					{
						String sHeader = sAtt.next();
						if(rHeader.equals(sHeader)) // R.col & S.col have to same scheme(only name in this case)
						{
							commonAtt = true;
							if(rTuple.get(rHeader).equals(sTuple.get(sHeader)))
							{
								haveToAdd = true;
								break;
							}
							else // S tuple is not match anymore break until get new S tuple
							{
								haveToAdd = false;
								notEqual = true;
								break;
							}
						}
					}
					if(notEqual)
					{
						break;
					}
				}
				if(haveToAdd)
				{
					LinkedHashMap<String,String> conTmp = new LinkedHashMap<String,String>();
					conTmp.putAll(rTuple);
					conTmp.putAll(sTuple);
					output.add(conTmp);
				}
			}
		}
		if(commonAtt == false)
		{
			output = cartesianProduct(entity1, entity2);
		}
		return output;
	}
	
	/**
	 * Perform a left outer join to given entities
	 *
	 * @param entity1
	 * @param entity2
	 * @return entity which completed left outer join
	 */
	public static List<LinkedHashMap<String, String>> leftJoin(List<LinkedHashMap<String, String>> entity1 ,List<LinkedHashMap<String, String>> entity2) {
		List<LinkedHashMap<String, String>> output = new ArrayList<LinkedHashMap<String, String>>();
		Iterator<LinkedHashMap<String, String>> rEntity = entity1.iterator();
		while(rEntity.hasNext())
		{
			LinkedHashMap<String,String> rTuple = rEntity.next();
			Iterator<LinkedHashMap<String, String>> sEntity = entity2.iterator();
			boolean LeftSide = false;
			while(sEntity.hasNext())
			{
				boolean haveToAdd = false;
				boolean notEqual = false;
				LinkedHashMap<String,String> sTuple = sEntity.next();
				Iterator<String> rAtt = rTuple.keySet().iterator();
				while(rAtt.hasNext())
				{
					String rHeader = rAtt.next();
					Iterator<String> sAtt = sTuple.keySet().iterator();
					while(sAtt.hasNext())
					{
						
						String sHeader = sAtt.next();
						if(rHeader.equals(sHeader)) // R.col & S.col have to same scheme(only name in this case)
						{
							if(rTuple.get(rHeader).equals(sTuple.get(sHeader)))
							{
								haveToAdd = true;
								break;
							}
							else // S tuple is not match anymore break until get new S tuple
							{
								haveToAdd = false;
								notEqual = true;
								break;
							}
						}
					}
					if(notEqual)
					{
						break;
					}
				}
				if(haveToAdd)
				{
					LeftSide = true;
					LinkedHashMap<String,String> conTmp = new LinkedHashMap<String,String>();
					conTmp.putAll(rTuple);
					conTmp.putAll(sTuple);
					output.add(conTmp);
				}
			}
			if(!LeftSide)
			{
				LinkedHashMap<String,String> conTmp = new LinkedHashMap<String,String>();
				conTmp.putAll(rTuple);
				conTmp.putAll(findDifferentHeader(rTuple, entity2.get(0)));
				output.add(conTmp);
			}
		}
		return output;
		
	}
	
	/**
	 * Find difference schema (name in this case) between two entities.
	 *
	 * @param entity1 
	 * @param entity2 
	 * @return Summation of differentiation entity.
	 */
	public static LinkedHashMap<String,String> findDifferentHeader(LinkedHashMap<String, String> entity1,LinkedHashMap<String, String>entity2) {
		LinkedHashMap<String,String> output = new LinkedHashMap<>();
		Iterator<String> itHeader2 = entity2.keySet().iterator();
		while(itHeader2.hasNext())
		{
			String header2 = itHeader2.next();
			boolean found = false;
			Iterator<String> itHeader1 = entity1.keySet().iterator();
			while(itHeader1.hasNext())
			{
				String header1 = itHeader1.next();
				if(header1.equals(header2))
				{
					found = true;
					break;
				}
			}
			if(!found)
			{
				output.put(header2,"null");
			}
		}
		return output;	
	}
}
