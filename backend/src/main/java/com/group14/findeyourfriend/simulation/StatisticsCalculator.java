package com.group14.findeyourfriend.simulation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.util.concurrent.AtomicDouble;
import com.group14.findeyourfriend.Clock;
import com.group14.findeyourfriend.Constants;
import com.group14.findeyourfriend.bracelet.Bracelet;
import com.group14.findeyourfriend.bracelet.BraceletEvent;
import com.group14.findeyourfriend.bracelet.DatabaseEntry;
import com.group14.findeyourfriend.bracelet.Person;
import com.group14.findeyourfriend.utils.Graph;
import com.group14.findeyourfriend.utils.GraphNode;
import com.group14.findeyourfriend.utils.Utils;

@RestController("/statistics")
public class StatisticsCalculator {

	private Pair<Double, Integer> totalAverageAgeInDatabase;
	private Pair<Double, Integer> currentAverageAgeInDatabase;
	private Pair<Double, Integer> totalPercentagePeopleInDatabase;
	private Pair<Double, Integer> currentPercentagePeopleInDatabase;
	private Pair<Double, Integer> currentPercentageRecentLocationsInDatabase;
	private Pair<Double, Integer> totalPercentageRecentLocationsInDatabase;
	private Pair<Double, Integer> avarageTimeToFindAFriend;
	private Double percentagePeopleOutOfRange;
	private Integer failedFriendSearch;
	// test pursoses, leave default in prod
	Supplier<Long> getCurrentTime;
	private Long timeThreshold = Constants.RECENT_DATA_THRESHOLD;
	private double outOfRangeCoefficient = 0.5; // % of how many people in the system you need to reach (even
												// indirectly = thorugh others) in order to be considered in the
												// range
	private ConcurrentHashMap<Integer, Pair<BraceletEvent, Long>> mapBraceletEvents;

	public StatisticsCalculator() {
		totalAverageAgeInDatabase = Pair.of(0d, 0);
		totalPercentagePeopleInDatabase = Pair.of(0d, 0);
		totalPercentageRecentLocationsInDatabase = Pair.of(0d, 0);
		currentAverageAgeInDatabase = Pair.of(0d, 0);
		currentPercentagePeopleInDatabase = Pair.of(0d, 0);
		currentPercentageRecentLocationsInDatabase = Pair.of(0d, 0);
		avarageTimeToFindAFriend = Pair.of(0d, 0);
		failedFriendSearch = 0;
		getCurrentTime = () -> Clock.getClock();
		mapBraceletEvents = new ConcurrentHashMap<Integer, Pair<BraceletEvent, Long>>();
	}

	public void calculate(Collection<Person> guests) {
		currentAverageAgeInDatabase = calculateAvgAgeLocationsDB(guests, getCurrentTime.get());
		totalAverageAgeInDatabase = Pair.of(totalAverageAgeInDatabase.getLeft() + currentAverageAgeInDatabase.getLeft()//
				, totalAverageAgeInDatabase.getRight() + currentAverageAgeInDatabase.getRight());
		//
		currentPercentagePeopleInDatabase = calculatePercentagePeopleInDatabase(guests);
		totalPercentagePeopleInDatabase = Pair
				.of(totalPercentagePeopleInDatabase.getLeft() + currentPercentagePeopleInDatabase.getLeft()//
						, totalPercentagePeopleInDatabase.getRight() + currentPercentagePeopleInDatabase.getRight());
		//
		currentPercentageRecentLocationsInDatabase = calculatePercentageRecentLocationsInDatabase(guests,
				getCurrentTime.get(), getTimeThreshold());
		totalPercentageRecentLocationsInDatabase = Pair.of(totalPercentageRecentLocationsInDatabase.getLeft()
				+ currentPercentageRecentLocationsInDatabase.getLeft()//
				, totalPercentageRecentLocationsInDatabase.getRight()
						+ currentPercentageRecentLocationsInDatabase.getRight());
		percentagePeopleOutOfRange = calculatePercentagePeopleOutOfRange(guests);
		// System.out.println("Current average age in DB: " +
		// getCurrentAverageAgeInDatabase() / 1000 + " s");
		// System.out.println("Total average age in DB: " +
		// getTotalAverageAgeInDatabase() / 1000 + " s");
		// System.out.println("Current average % people in DB: " +
		// getCurrentPercentagePeopleInDatabase() + "%");
		// System.out.println("Total average % people in DB: " +
		// getTotalPercentagePeopleInDatabase() + " %");
	}

	public Double calculatePercentagePeopleOutOfRange(Collection<Person> guests) {
		// StopWatch watch = new StopWatch();
		// watch.start();
		HashMap<Person, GraphNode> nodeMap = new HashMap<>();
		guests.forEach(g -> {
			nodeMap.put(g, new GraphNode(g));
		});
		Graph graph = createGraph(guests, nodeMap);
		//
		AtomicInteger peopleOutOfRange = new AtomicInteger();
		guests.forEach(g -> {
			AtomicInteger reachablePeople = new AtomicInteger(1);// at least one can reach himself
			guests.forEach(g2 -> {
				if (!g.equals(g2)) {
					if (graph.areConneted(nodeMap.get(g), nodeMap.get(g2))) {
						reachablePeople.incrementAndGet();
					}
				}
			});
			if (reachablePeople.get() < guests.size() * getOutOfRangeCoefficient()) {
				peopleOutOfRange.incrementAndGet();
			}
		});
		// watch.stop();
		// System.out.println(watch.getTime());
		return ((double) peopleOutOfRange.get() / guests.size()) * 100;
	}

	private Pair<Double, Integer> calculatePercentageRecentLocationsInDatabase(Collection<Person> guests, Long now,
			Long timeThreshold) {
		List<Bracelet> bracelets = guests.stream().map(Person::getBracelet).collect(Collectors.toList());
		AtomicDouble avgPercentage = new AtomicDouble(0);
		AtomicInteger counter = new AtomicInteger(0);
		bracelets.stream().map(Bracelet::getDataBase).forEach(db -> {
			AtomicDouble localAvg = new AtomicDouble();
			db.values().forEach(de -> {
				if (now - de.getTimeStamp() < timeThreshold) {
					localAvg.addAndGet(1);
				}
			});
			counter.incrementAndGet();
			if (db.size() > 0) {
				localAvg.set(localAvg.get() / db.size() * 100);
				avgPercentage.set((avgPercentage.get() + localAvg.get()));
			}
		});
		return Pair.of(avgPercentage.get(), counter.get());
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
				// .divide(new BigDecimal(1000))// return seconds
				.setScale(2, RoundingMode.HALF_UP).doubleValue();
	}

	@RequestMapping("/current-avg-age-in-db")
	@CrossOrigin
	public Double getCurrentAverageAgeInDatabase() {
		if (currentAverageAgeInDatabase.getRight() <= 0) {
			return 0d;
		}
		return new BigDecimal(currentAverageAgeInDatabase.getLeft() / currentAverageAgeInDatabase.getRight())
				// .divide(new BigDecimal(1000))// return seconds
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

	@RequestMapping("/current-percentage-recent-locations-in-db")
	@CrossOrigin
	public Double getCurrentPercentageRecentLocationsInDatabase() {
		if (currentPercentageRecentLocationsInDatabase.getRight() <= 0) {
			return 0d;
		}
		return new BigDecimal(currentPercentageRecentLocationsInDatabase.getLeft()
				/ currentPercentageRecentLocationsInDatabase.getRight()).setScale(2, RoundingMode.HALF_UP)
						.doubleValue();
	}

	@RequestMapping("/total-percentage-recent-locations-in-db")
	@CrossOrigin
	public Double getTotalPercentageRecentLocationsInDatabase() {
		if (totalPercentageRecentLocationsInDatabase.getRight() <= 0) {
			return 0d;
		}
		return new BigDecimal(totalPercentageRecentLocationsInDatabase.getLeft()
				/ totalPercentageRecentLocationsInDatabase.getRight()).setScale(2, RoundingMode.HALF_UP).doubleValue();
	}

	@RequestMapping("/current-percentage-people-out-of-range")
	@CrossOrigin
	public Double getPercentagePeopleOutOfRange() {
		return percentagePeopleOutOfRange;
	}

	@RequestMapping("/failed-friend-search")
	@CrossOrigin
	public Integer getFailedFriendSearch() {
		return failedFriendSearch;
	}

	@RequestMapping("/avg-time-to-find-friend")
	@CrossOrigin
	public Double getAvarageTimeToFindAFriend() {
		if (avarageTimeToFindAFriend.getRight() <= 0) {
			return 0d;
		}
		return new BigDecimal(avarageTimeToFindAFriend.getLeft() / avarageTimeToFindAFriend.getRight())
				.setScale(2, RoundingMode.HALF_UP).doubleValue();
	}

	public void setGetCurrentTime(Supplier<Long> getCurrentTime) {
		this.getCurrentTime = getCurrentTime;
	}

	public void setTimeThreshold(Long timeThreshold) {
		this.timeThreshold = timeThreshold;
	}

	public Long getTimeThreshold() {
		return timeThreshold;
	}

	public void setOutOfRangeCoefficient(double outOfRangeCoefficient) {
		this.outOfRangeCoefficient = outOfRangeCoefficient;
	}

	public double getOutOfRangeCoefficient() {
		return outOfRangeCoefficient;
	}

	public Graph createGraph(Collection<Person> guests, HashMap<Person, GraphNode> nodeMap) {
		Graph graph = new Graph();

		guests.forEach(g -> {
			guests.forEach(g2 -> {
				if (!g.equals(g2)) {
					if (Utils.isReachable(g.getPosition(), g2.getPosition(), g.getBracelet().getRadioRange())) {
						nodeMap.get(g).addNode(nodeMap.get(g2));
					}
				}
			});
			graph.addNode(nodeMap.get(g));
		});

		return graph;
	}

	public void braceletEvent(Pair<Person, BraceletEvent> event) {
		Pair<BraceletEvent, Long> pair = mapBraceletEvents.get(event.getKey().getId());
		if (pair != null) {
			switch (event.getRight()) {
			case START_SEARCH:
				//
				break;
			case FRIEND_MET:
				if (pair.getLeft() == BraceletEvent.FRIEND_FOUND_IN_DB) {
					// search succeded
					avarageTimeToFindAFriend = Pair.of(
							avarageTimeToFindAFriend.getKey() + (Clock.getClock() - pair.getRight()),
							avarageTimeToFindAFriend.getValue() + 1);
				}
				break;
			case FRIEND_FOUND_IN_DB:
				// nothing to do
				break;
			case GO_TO_CONCERT:
				// nothing to do
				break;
			case FRIEND_NOT_FOUND_IN_DB:
				// search failed
				failedFriendSearch++;
				break;
			default:
				break;
			}
		}
		mapBraceletEvents.put(event.getKey().getId(), Pair.of(event.getRight(), Clock.getClock()));
	}
}
