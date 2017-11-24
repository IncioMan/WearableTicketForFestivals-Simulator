package com.group14.findeyourfriend;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

//========================================================================
// This conversion was produced by the Free Edition of
// C# to Java Converter courtesy of Tangible Software Solutions.
// Order the Premium Edition at https://www.tangiblesoftwaresolutions.com
//========================================================================

public class Map
{
	private static final char SIDE_BORDER_CHAR = '|';
	private static final char TOP_BORDER_CHAR = '-';

	private Set<Person> people;
	private int height;
	private int width;

	private Simulation sim;

	public Map(int height, int width) {
		this.height = height;
		this.width = width;
	}

	public final void Print() {
		char[] topBottomRepeat = new char[getWidth() + 2];
		char[] blankRepeat = new char[getWidth()];
		Arrays.fill(topBottomRepeat, TOP_BORDER_CHAR);
		Arrays.fill(blankRepeat, ' ');
		String topBorder = new String(topBottomRepeat);
		String bottomBorder = new String(topBottomRepeat);
		String sideBorders = SIDE_BORDER_CHAR + new String(blankRepeat) + SIDE_BORDER_CHAR;

		// Prints top of map
		System.out.println(topBorder);

		for (int row = 0; row < getHeight(); row++) {
			ArrayList<Person> personsOnRow = new ArrayList<>();
			for (Person p : sim.getGuests()) {
				if ((int) Math.floor(p.getPosition().getCoordinates().y) == row) {
					personsOnRow.add(p);
				}
			}

			if (!personsOnRow.isEmpty()) {
				// create array with spaces
				// + 2 because of borders
				char[] charArray = new char[Constants.MAX_WIDTH + 2];
				populateWithSpaces(charArray);

				charArray[0] = SIDE_BORDER_CHAR;
				charArray[Constants.MAX_WIDTH + 1] = SIDE_BORDER_CHAR;

				for (Person person : personsOnRow) {
					// if two persons on same field only one is shown.
					// + 1 because array is 0-based
					int xCoordinate = (int) Math.floor(person.getPosition().getCoordinates().x);
					xCoordinate = xCoordinate == 0 ? xCoordinate + 1 : xCoordinate;
					charArray[xCoordinate] = person.getName().charAt(0);

				}
				// Print the entire line
				System.out.println(charArray);
			} else {
				System.out.println(sideBorders);
			}
		}
		// Prints bottom of map
		System.out.println(bottomBorder);
	}

	private void populateWithSpaces(char[] arr)
	{
		for (int i = 0; i < arr.length; i++)
		{
			arr[i] = ' ';
		}
	}

	public final boolean AllInBound()
	{
		for (Person p : sim.getGuests())
		{
			if (p.getPosition().getCoordinates().x < 0 || p.getPosition().getCoordinates().x > Constants.MAX_WIDTH || p.getPosition().getCoordinates().y < 0 || p.getPosition().getCoordinates().y > Constants.MAX_HEIGHT)
			{
				return false;
			}
		}
		return true;
	}

	public static void clrscr(){
		//Clears Screen in java
		try {
			if (System.getProperty("os.name").contains("Windows"))
				new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
			else
				Runtime.getRuntime().exec("clear");
		} catch (IOException | InterruptedException ex) {}
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public void setSimulation(Simulation simulation) {
		this.sim = simulation;
	}
}