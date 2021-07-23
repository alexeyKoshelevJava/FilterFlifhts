package com.gridnine.testing;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class WorkingWithSegmentsByRulesTest {

    @Test
    void filterWhenDepartureBeforeTheCurrentTime() {
        //        given
        List<Flight> flightList = FlightBuilder.createFlights();
        Rule rule = z -> z.stream().filter((x) -> {
            boolean b = x.getSegments().stream()
                    .anyMatch(y -> y.getDepartureDate().compareTo(LocalDateTime.now()) > 0);
            return b;
        }).collect(Collectors.toList());
        WorkingWithSegmentsByRules working = new WorkingWithSegmentsByRules(flightList, rule);

//expected
        List<Flight> expected = new ArrayList<>();
        expected.add(flightList.get(0));
        expected.add(flightList.get(1));
        expected.add(flightList.get(3));
        expected.add(flightList.get(4));
        expected.add(flightList.get(5));
//       when
        List<Flight> actualList = working.filter();
//       then
        Assertions.assertEquals(expected, actualList);
    }

    @Test
    void filterWhenRemoveArrivalBeforeDeparture() {
        //        given
        List<Flight> flightList = FlightBuilder.createFlights();
        Rule rule1 = z -> z.stream().filter((x) -> {
            boolean b = x.getSegments().stream()
                    .anyMatch(y -> y.getArrivalDate().compareTo(y.getDepartureDate()) > 0);
            return b;
        }).collect(Collectors.toList());
        WorkingWithSegmentsByRules working = new WorkingWithSegmentsByRules(flightList, rule1);

//expected
        List<Flight> expected = new ArrayList<>();
        expected.add(flightList.get(0));
        expected.add(flightList.get(1));
        expected.add(flightList.get(2));
        expected.add(flightList.get(4));
        expected.add(flightList.get(5));
//       when
        List<Flight> actualList = working.filter();
        //       then
        Assertions.assertEquals(expected, actualList);


    }

    @Test
    void filterWhenRemoveWhoHaveTimeOnEarthIsMoreThanTwoHours() {
        //        given
        List<Flight> flightList = FlightBuilder.createFlights();
//        This is Rule
        Rule rule2 = t -> {
            boolean b = false;
            List<Flight> newListFlight = new ArrayList<>();
            for (Flight flight : t) {
                if (flight.getSegments().size() != 1) {
                    long millisDepartureDateMemory = 0;
                    long millisArrivalDateMemory = 0;
                    long differenceInHours = 0;
                    int count = 1;
                    for (Segment segment : flight.getSegments()) {
                        if (millisDepartureDateMemory == 0 && millisArrivalDateMemory == 0) {
                            millisArrivalDateMemory = segment.getArrivalDate().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                        } else {
                            long millisDeparture = segment.getDepartureDate().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                            long difference = millisDeparture - millisArrivalDateMemory;
                            differenceInHours += TimeUnit.MILLISECONDS.toHours(difference);
                            count++;
                            millisDepartureDateMemory = segment.getDepartureDate().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                            millisArrivalDateMemory = segment.getArrivalDate().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                            if (differenceInHours < 2 && count == flight.getSegments().size()) {
                                b = true;
                            }
                        }
                    }
                } else {
                    newListFlight.add(flight);
                }
                if (b)
                    newListFlight.add(flight);
                b = false;
            }
            return newListFlight;
        };

        WorkingWithSegmentsByRules working = new WorkingWithSegmentsByRules(flightList, rule2);

//expected
        List<Flight> expected = new ArrayList<>();
        expected.add(flightList.get(0));
        expected.add(flightList.get(1));
        expected.add(flightList.get(2));
        expected.add(flightList.get(3));
        //      when
        List<Flight> actualList = working.filter();
        //       then
        Assertions.assertEquals(expected, actualList);
    }
//    TEST FAILED
    @Test
    void testFilterWhenDepartureBeforeTheCurrentTimeIsFailed() {
        //        given
        List<Flight> flightList = FlightBuilder.createFlights();
        Rule rule = z -> z.stream().filter((x) -> {
            boolean b = x.getSegments().stream()
                    .anyMatch(y -> y.getDepartureDate().compareTo(LocalDateTime.now()) > 0);
            return b;
        }).collect(Collectors.toList());
        WorkingWithSegmentsByRules working = new WorkingWithSegmentsByRules(flightList, rule);

//expected
        List<Flight> expected = new ArrayList<>();
        expected.add(flightList.get(0));
        expected.add(flightList.get(2));
        expected.add(flightList.get(3));
        expected.add(flightList.get(4));
        expected.add(flightList.get(5));
//       when
        List<Flight> actualList = working.filter();
//       then
        Assertions.assertNotEquals(expected, actualList);
    }


}