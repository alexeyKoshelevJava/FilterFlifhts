package com.gridnine.testing;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        List<Flight> listFlights = FlightBuilder.createFlights();
        LocalDateTime today = LocalDateTime.now();

        System.out.println("Текущее время: " + today);

//      Departure before The Current time
        WorkingWithSegmentsByRules working = new WorkingWithSegmentsByRules(listFlights, removeDepartureBeforeTheCurrentTime());
        System.out.println(" Departure before The Current time: " + working.filter());

//        removeArrivalBeforeDeparture
        WorkingWithSegmentsByRules working1 = new WorkingWithSegmentsByRules(listFlights, removeArrivalBeforeDeparture());
        System.out.println("removeArrivalBeforeDeparture: " + working1.filter());

// output of flights that have more than 2 hours on the ground.
        WorkingWithSegmentsByRules working2 = new WorkingWithSegmentsByRules(listFlights, removeWhoHaveTimeOnEarthIsMoreThanTwoHours());
        System.out.println("output of flights that have more than 2 hours on the ground: " + working2.filter());
    }

    public static Rule removeDepartureBeforeTheCurrentTime() {
//        Create new Rule
        return z -> z.stream().filter((x) -> {
            boolean b = x.getSegments().stream()
                    .anyMatch(y -> y.getDepartureDate().compareTo(LocalDateTime.now()) > 0);
            return b;
        }).collect(Collectors.toList());
    }

    public static Rule removeArrivalBeforeDeparture() {
//        Create new Rule
        return z -> z.stream().filter((x) -> {
            boolean b = x.getSegments().stream()
                    .anyMatch(y -> y.getArrivalDate().compareTo(y.getDepartureDate()) > 0);
            return b;
        }).collect(Collectors.toList());
    }

    //Here I create new Rule and transfer to WorkingWithSegmentsBy Rules Class
    public static Rule removeWhoHaveTimeOnEarthIsMoreThanTwoHours() {
//           Create new Rule
        return t -> {
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
    }
}
