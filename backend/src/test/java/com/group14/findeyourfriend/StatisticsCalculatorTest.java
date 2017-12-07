package com.group14.findeyourfriend;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.group14.common_interface.Position;
import com.group14.findeyourfriend.bracelet.Bracelet;
import com.group14.findeyourfriend.bracelet.DatabaseEntry;
import com.group14.findeyourfriend.bracelet.Person;
import com.group14.findeyourfriend.bracelet.Radio;
import com.group14.findeyourfriend.simulation.StatisticsCalculator;

public class StatisticsCalculatorTest {

	@Test
	public void testAvarageAgeInDatabase() {
		List<Person> people = new ArrayList<>();
		Person person = new Person();
		Bracelet bracelet = new Bracelet(null, null, null, person, 0l);
		person.setBracelet(bracelet);
		DatabaseEntry databaseEnty = new DatabaseEntry();
		databaseEnty.setPosition(null);
		databaseEnty.setTimeStamp(-10l);
		bracelet.getDataBase().put(1, databaseEnty); // add or overwrite
		databaseEnty = new DatabaseEntry();
		databaseEnty.setPosition(null);
		databaseEnty.setTimeStamp(-20l);
		bracelet.getDataBase().put(2, databaseEnty); // add or overwrite

		people.add(person);

		person = new Person();
		bracelet = new Bracelet(null, null, null, person, 0l);
		person.setBracelet(bracelet);
		databaseEnty = new DatabaseEntry();
		databaseEnty.setPosition(null);
		databaseEnty.setTimeStamp(-50l);
		bracelet.getDataBase().put(1, databaseEnty); // add or overwrite
		databaseEnty = new DatabaseEntry();
		databaseEnty.setPosition(null);
		databaseEnty.setTimeStamp(-80l);
		bracelet.getDataBase().put(2, databaseEnty); // add or overwrite

		people.add(person);

		StatisticsCalculator calculator = new StatisticsCalculator();
		calculator.setTimeThreshold(30l);
		calculator.setGetCurrentTime(() -> 0l);
		calculator.calculate(people);
		Assert.assertEquals(40d, calculator.getTotalAverageAgeInDatabase(), 0);
		Assert.assertEquals(40l, calculator.getCurrentAverageAgeInDatabase(), 0);
		Assert.assertEquals(100, calculator.getCurrentPercentagePeopleInDatabase(), 0);
		Assert.assertEquals(100, calculator.getTotalPercentagePeopleInDatabase(), 0);
		Assert.assertEquals(50, calculator.getCurrentPercentageRecentLocationsInDatabase(), 0);
		Assert.assertEquals(50, calculator.getTotalPercentageRecentLocationsInDatabase(), 0);

		person = new Person();
		bracelet = new Bracelet(null, null, null, person, 0l);
		person.setBracelet(bracelet);
		databaseEnty = new DatabaseEntry();
		databaseEnty.setPosition(null);
		databaseEnty.setTimeStamp(-20l);
		bracelet.getDataBase().put(1, databaseEnty); // add or overwrite
		databaseEnty = new DatabaseEntry();
		databaseEnty.setPosition(null);
		databaseEnty.setTimeStamp(-20l);
		bracelet.getDataBase().put(2, databaseEnty); // add or overwrite

		people.add(person);

		calculator.calculate(people);
		Assert.assertEquals(360d / 10, calculator.getTotalAverageAgeInDatabase(), 1);
		Assert.assertEquals(200d / 6, calculator.getCurrentAverageAgeInDatabase(), 1);
		Assert.assertEquals(((double) 2 / 3 * 100 * 3 / 3), calculator.getCurrentPercentagePeopleInDatabase(), 1);
		Assert.assertEquals(((double) (200 + 200) / 5), calculator.getTotalPercentagePeopleInDatabase(), 2);
		Assert.assertEquals(200d / 3, calculator.getCurrentPercentageRecentLocationsInDatabase(), 1);
		Assert.assertEquals(300d / 5, calculator.getTotalPercentageRecentLocationsInDatabase(), 1);
	}

	@Test
	public void graphTester() {
		List<Person> people = new ArrayList<>();
		Person person = new Person(0);
		Radio radio = new Radio(2, 0, 0, 0);
		Bracelet bracelet = new Bracelet(null, radio, null, person, 0l);
		person.setBracelet(bracelet);
		person.setPosition(new Position(0, 0));
		people.add(person);

		person = new Person(1);
		bracelet = new Bracelet(null, radio, null, person, 0l);
		person.setBracelet(bracelet);
		person.setPosition(new Position(1, 1));
		people.add(person);

		person = new Person(7);
		bracelet = new Bracelet(null, radio, null, person, 0l);
		person.setBracelet(bracelet);
		person.setPosition(new Position(7, 7));
		people.add(person);

		StatisticsCalculator calculator = new StatisticsCalculator();
		calculator.setOutOfRangeCoefficient(0.5);
		Assert.assertEquals(1d / 3 * 100, calculator.calculatePercentagePeopleOutOfRange(people), 1);
	}
}
