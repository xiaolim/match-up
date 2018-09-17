package project1;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class project 
{
	private static String file_location = "/home/andrew/Desktop/log.txt";
	private static String data_location = "/home/andrew/Desktop/data.csv";
	private static String [] filtered_chars = {"[", "]", ","};
	
	public static void main(String [] args)
	{
		File file = new File(file_location);
		Scanner scr = null;
		String line;
		String [] line_parts;
		// Goal get List of both sides... (X for data Matrix)
		// For each team combat, Y = -1, 0, 1 (Loss, Tie, Win)
		ArrayList<Integer []> rows = new ArrayList<Integer []>();
		
		try
		{
			scr = new Scanner(file);
			while(scr.hasNext())
			{
				line = scr.nextLine();
				if (line.contains("Round"))
				{	
					Integer [] tuple = new Integer[11];
					// Next 3 Lines got data I want to have!
					// Get the first team's list! index 4, 5, 6, 7, 8 are what I want!
					line = scr.nextLine();
					line = filter_characters(line);
					line_parts = line.split(" ");
					for(int i = 0; i < 5; i++)
					{
						tuple[i] = Integer.parseInt(line_parts[4+i]);
					}
					
					// Get the second team's list!
					line = scr.nextLine();
					line = filter_characters(line);
					line_parts = line.split(" ");
					for(int i = 0; i < 5; i++)
					{
						tuple[i+5] = Integer.parseInt(line_parts[4+i]);
					}
					
					// Get the scores and apply the label -1, 0, 1
					// Scores will be in index 2 and 4. Beware of stray comma!
					line = scr.nextLine();
					line = line.replace(",", "");
					line_parts = line.split(" ");
					int team_one = Integer.parseInt(line_parts[2]);
					int team_two = Integer.parseInt(line_parts[4]);
					if(team_one > team_two)
					{
						tuple[10] = -1;
					}
					else if(team_one == team_two)
					{
						tuple[10] = 0;
					}
					else
					{
						tuple[10] = 1;
					}
					rows.add(tuple);
					//System.out.println(Arrays.toString(tuple));
				}
			}
			try 
			{
				print_csv(rows);
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		} 
		catch (FileNotFoundException ex) 
		{
			ex.printStackTrace();
		}
	}
	
	private static void print_csv(ArrayList<Integer []> tuples) throws IOException
	{
		//FileOutputStream outputStream = new FileOutputStream(data_location);
		BufferedWriter writer = new BufferedWriter(new FileWriter(data_location));
		String output = null;
		for (int i = 0; i < tuples.size();i++)
		{
			output = Arrays.toString(tuples.get(i));
			output = output.replace(" ", "");
			output = output.replace("]", "");
			output = output.replace("[", "");
			writer.write(output+'\n');
			writer.flush();
		}
		writer.close();
	}
	
	private static String filter_characters(String input)
	{
		for(int i = 0; i < filtered_chars.length; i++)
		{
			if(input.contains(filtered_chars[i]))
			{
				input = input.replace(filtered_chars[i], "");
			}
		}
		return input;
	}
}
