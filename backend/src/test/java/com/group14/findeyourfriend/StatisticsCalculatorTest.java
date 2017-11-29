package com.group14.findeyourfriend;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class StatisticsCalculatorTest {

	@Test
	public void testAvarageAgeInDatabase() {
		List<Person> people = new ArrayList<>();
		Person person = new Person();
		Bracelet bracelet = new Bracelet(null, null, person);
		person.setBracelet(bracelet);
		DatabaseEntry databaseEnty = new DatabaseEntry();
		databaseEnty.setPosition(null);
		databaseEnty.setTimeStamp(System.currentTimeMillis() - 10);
		bracelet.getDataBase().put(1, databaseEnty); // add or overwrite
		databaseEnty = new DatabaseEntry();
		databaseEnty.setPosition(null);
		databaseEnty.setTimeStamp(System.currentTimeMillis() - 20);
		bracelet.getDataBase().put(2, databaseEnty); // add or overwrite

		people.add(person);

		person = new Person();
		bracelet = new Bracelet(null, null, person);
		person.setBracelet(bracelet);
		databaseEnty = new DatabaseEntry();
		databaseEnty.setPosition(null);
		databaseEnty.setTimeStamp(System.currentTimeMillis() - 50);
		bracelet.getDataBase().put(1, databaseEnty); // add or overwrite
		databaseEnty = new DatabaseEntry();
		databaseEnty.setPosition(null);
		databaseEnty.setTimeStamp(System.currentTimeMillis() - 80);
		bracelet.getDataBase().put(2, databaseEnty); // add or overwrite

		people.add(person);

		StatisticsCalculator calculator = new StatisticsCalculator();
		calculator.calculate(people);
		Assert.assertEquals(40l, calculator.getTotalAverageAgeInDatabase(), 50);
		Assert.assertEquals(40l, calculator.getCurrentAverageAgeInDatabase(), 50);
		Assert.assertEquals(100, calculator.getCurrentPercentagePeopleInDatabase(), 0);
		Assert.assertEquals(100, calculator.getTotalPercentagePeopleInDatabase(), 0);

		person = new Person();
		bracelet = new Bracelet(null, null, person);
		person.setBracelet(bracelet);
		databaseEnty = new DatabaseEntry();
		databaseEnty.setPosition(null);
		databaseEnty.setTimeStamp(System.currentTimeMillis() - 20);
		bracelet.getDataBase().put(1, databaseEnty); // add or overwrite
		databaseEnty = new DatabaseEntry();
		databaseEnty.setPosition(null);
		databaseEnty.setTimeStamp(System.currentTimeMillis() - 20l);
		bracelet.getDataBase().put(2, databaseEnty); // add or overwrite

		people.add(person);

		calculator.calculate(people);
		Assert.assertEquals(360l / 10, calculator.getTotalAverageAgeInDatabase(), 50);
		Assert.assertEquals(200l / 6, calculator.getCurrentAverageAgeInDatabase(), 50);
		Assert.assertEquals(((double) 2 / 3 * 100 * 3 / 3), calculator.getCurrentPercentagePeopleInDatabase(), 0);
		Assert.assertEquals(((double) (200 + 200) / 5), calculator.getTotalPercentagePeopleInDatabase(), 2);
	}
}
