package com.group14.findeyourfriend.simulation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import com.group14.findeyourfriend.bracelet.Bracelet;
import com.group14.findeyourfriend.bracelet.DatabaseEntry;
import com.group14.findeyourfriend.bracelet.Person;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.util.concurrent.AtomicDouble;

@RestController("/statistics")
public class StatisticsCalculator {

	private Pair<Double, Integer> totalAverageAgeInDatabase;
	private Pair<Double, Integer> currentAverageAgeInDatabase;
	private Pair<Double, Integer> totalPercentagePeopleInDatabase;
	private Pair<Double, Integer> currentPercentagePeopleInDatabase;

	public StatisticsCalculator() {
		totalAverageAgeInDatabase = Pair.of(0d, 0);
		totalPercentagePeopleInDatabase = Pair.of(0d, 0);
	}

	public void calculate(Collection<Person> guests) {
		currentAverageAgeInDatabase = calculateAvgAgeLocationsDB(guests, System.currentTimeMillis());
		totalAverageAgeInDatabase = Pair.of(totalAverageAgeInDatabase.getLeft() + currentAverageAgeInDatabase.getLeft()//
				, totalAverageAgeInDatabase.getRight() + currentAverageAgeInDatabase.getRight());
		currentPercentagePeopleInDatabase = calculatePercentagePeopleInDatabase(guests);
		totalPercentagePeopleInDatabase = Pair
				.of(totalPercentagePeopleInDatabase.getLeft() + currentPercentagePeopleInDatabase.getLeft()//
						, totalPercentagePeopleInDatabase.getRight() + currentPercentagePeopleInDatabase.getRight());
		System.out.println("Current average age in DB: " + getCurrentAverageAgeInDatabase() / 1000 + " s");
		System.out.println("Total average age in DB: " + getTotalAverageAgeInDatabase() / 1000 + " s");
		System.out.println("Current average % people in DB: " + getCurrentPercentagePeopleInDatabase() + "%");
		System.out.println("Total average % people in DB: " + getTotalPercentagePeopleInDatabase() + " %");
	}

	private Pair<Double, Integer> calculatePercentagePeopleInDatabase(Collection<Person> guests) {
		List<Bracelet> bracelets = guests.stream().map(Person::getBracelet).collect(Collectors.toList());
		AtomicDouble avgPercentage = new AtomicDouble(0);
		bracelets.stream().map(Bracelet::getDataBase).forEach(db -> {
			avgPercentage.addAndGet(((double) db.keySet().size() / guests.size()) * 100);
		});
		return Pair.of(avgPercentage.get(), guests.size());
	}

	public Pair<Double, Integer> calculateAvgAgeLocationsDB(Collection<Person> guests, Long now) {
		List<Bracelet> bracelets = guests.stream().map(Person::getBracelet).collect(Collectors.toList());
		AtomicDouble avgAge = new AtomicDouble(0);
		List<DatabaseEntry> databaseEntries = bracelets.stream().map(Bracelet::getDataBase).map(HashMap::values)
				.flatMap(v -> v.stream()).collect(Collectors.toList());
		databaseEntries.forEach(de -> {
			avgAge.addAndGet((now - de.getTimeStamp()));
		});
		return Pair.of(avgAge.get(), databaseEntries.size());
	}

	@RequestMapping("/total-avg-age-in-db")
	@CrossOrigin
	public Double getTotalAverageAgeInDatabase() {
		if (totalAverageAgeInDatabase.getRight() <= 0) {
			return 0d;
		}
		return new BigDecimal(totalAverageAgeInDatabase.getLeft() / totalAverageAgeInDatabase.getRight())
				.setScale(2, RoundingMode.HALF_UP).doubleValue();
	}

	@RequestMapping("/current-avg-age-in-db")
	@CrossOrigin
	public Double getCurrentAverageAgeInDatabase() {
		if (currentAverageAgeInDatabase.getRight() <= 0) {
			return 0d;
		}
		return new BigDecimal(currentAverageAgeInDatabase.getLeft() / currentAverageAgeInDatabase.getRight())
				.setScale(2, RoundingMode.HALF_UP).doubleValue();
	}

	@RequestMapping("/current-percentage-people-in-db")
	@CrossOrigin
	public Double getCurrentPercentagePeopleInDatabase() {
		if (currentPercentagePeopleInDatabase.getRight() <= 0) {
			return 0d;
		}
		return new BigDecimal(
				currentPercentagePeopleInDatabase.getLeft() / currentPercentagePeopleInDatabase.getRight())
						.setScale(2, RoundingMode.HALF_UP).doubleValue();
	}

	@RequestMapping("/total-percentage-people-in-db")
	@CrossOrigin
	public Double getTotalPercentagePeopleInDatabase() {
		if (totalPercentagePeopleInDatabase.getRight() <= 0) {
			return 0d;
		}
		return new BigDecimal(totalPercentagePeopleInDatabase.getLeft() / totalPercentagePeopleInDatabase.getRight())
				.setScale(2, RoundingMode.HALF_UP).doubleValue();
	}
}
