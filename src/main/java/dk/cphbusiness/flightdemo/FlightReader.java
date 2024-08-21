package dk.cphbusiness.flightdemo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dk.cphbusiness.utils.Utils;
import lombok.*;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Purpose:
 *
 * @author: Thomas Hartmann
 */
public class FlightReader {
//her streamer man.
    public static void main(String[] args) {
        FlightReader flightReader = new FlightReader();
        List<DTOs.FlightInfo> flightInfoList = new ArrayList<>();
        try {
            List<DTOs.FlightDTO> flightList = flightReader.getFlightsFromFile("flights.json");
            flightInfoList = flightReader.getFlightInfoDetails(flightList);
            flightInfoList.forEach(f->{
                System.out.println("\n"+f);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        //AverageDuration for Turkish Airline

        List<Duration> durations = flightInfoList.stream()
                .filter(flightInfo -> "Royal Jordanian".equals(flightInfo.getAirline()))
                .map(DTOs.FlightInfo::getDuration)
                .collect(Collectors.toList());

        OptionalDouble averageDuration = durations.stream()
                .mapToLong(Duration::toMinutes)
                .average();
        System.out.println(durations);

        averageDuration.ifPresent(System.out::println);
    }


//    public List<FlightDTO> jsonFromFile(String fileName) throws IOException {
//        List<FlightDTO> flights = getObjectMapper().readValue(Paths.get(fileName).toFile(), List.class);
//        return flights;
//    }

//her benytter man sig af attributerne for at lave listen. Her er man fri for at instansier hvert objekt.
    public List<DTOs.FlightInfo> getFlightInfoDetails(List<DTOs.FlightDTO> flightList) {
        List<DTOs.FlightInfo> flightInfoList = flightList.stream().map(flight -> {
            Duration duration = Duration.between(flight.getDeparture().getScheduled(), flight.getArrival().getScheduled());
            DTOs.FlightInfo flightInfo = DTOs.FlightInfo.builder()
                    .name(flight.getFlight().getNumber())
                    .iata(flight.getFlight().getIata())
                    .airline(flight.getAirline().getName())
                    .duration(duration)
                    .departure(flight.getDeparture().getScheduled().toLocalDateTime())
                    .arrival(flight.getArrival().getScheduled().toLocalDateTime())
                    .origin(flight.getDeparture().getAirport())
                    .destination(flight.getArrival().getAirport())
                    .build();

            return flightInfo;
        }).toList();
        return flightInfoList;
    }

//konvertere fra json fil til java sprog.
    public List<DTOs.FlightDTO> getFlightsFromFile(String filename) throws IOException {
        DTOs.FlightDTO[] flights = new Utils().getObjectMapper().readValue(Paths.get(filename).toFile(), DTOs.FlightDTO[].class);

        List<DTOs.FlightDTO> flightList = Arrays.stream(flights).toList();
        return flightList;
    }

}
