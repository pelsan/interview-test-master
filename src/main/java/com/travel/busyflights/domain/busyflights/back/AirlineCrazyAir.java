package com.travel.busyflights.domain.busyflights.back;

import com.travel.busyflights.domain.busyflights.BusyFlightsRequest;
import com.travel.busyflights.domain.busyflights.BusyFlightsResponse;
import com.travel.busyflights.domain.crazyair.CrazyAirRequest;
import com.travel.busyflights.domain.crazyair.CrazyAirResponse;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AirlineCrazyAir implements Airline {

    @Override
    public String getFligths(){



        return "Fligths CrazyAir";
    }

    private <T> List<T> getCrazyAirFligths(CrazyAirRequest myRequest) {
        List<T> fligths = new ArrayList<>();
        TcpClient tcpClient = TcpClient.create().option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .doOnConnected(connection -> {
                    connection.addHandlerLast(new ReadTimeoutHandler(5000, TimeUnit.MILLISECONDS));
                    connection.addHandlerLast(new WriteTimeoutHandler(5000, TimeUnit.MILLISECONDS));
                });
        try {
            /*
            WebClient client = webClientBuilder
                    .clientConnector(new ReactorClientHttpConnector(HttpClient.from(tcpClient)))
                    .baseUrl("http://localhost:8080/crazyairapi")
                    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).defaultUriVariables(
                            Collections.singletonMap("url", "http://localhost:8080/crazyairapi"))
                    .build();
             */
            WebClient webClient = WebClient.builder()
                    .baseUrl("http://localhost:8080/crazyairapi")
                    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                    .clientConnector(new ReactorClientHttpConnector(HttpClient.from(tcpClient)))  // timeout
                    .build();

            WebClient.ResponseSpec myRespo = webClient.post().uri("http://localhost:8080/crazyairapi/fligths").bodyValue(myRequest).retrieve();
            fligths = (List<T>) myRespo.bodyToFlux(Object.class).collectList().block();
            //System.out.println(fligths.get(0).toString());

        } catch (Exception e) {
            System.out.println(e);
            return fligths;
        }
        return fligths;
    }
    private List<CrazyAirResponse> processCrazyAirFligths(CrazyAirRequest myRequest){
        List<CrazyAirResponse> myList = new ArrayList<CrazyAirResponse>();
        List<Object> fligths = getCrazyAirFligths(myRequest);
        for (Object fligth : fligths)
        {
            java.util.LinkedHashMap tempo  = (java.util.LinkedHashMap)fligth;
            CrazyAirResponse tempoResponse = new CrazyAirResponse();
            tempoResponse.setAirline(tempo.get("airline").toString());
            tempoResponse.setPrice((double)tempo.get("price"));
            tempoResponse.setCabinclass(tempo.get("cabinclass").toString());
            tempoResponse.setDepartureAirportCode(tempo.get("departureAirportCode").toString());
            tempoResponse.setDestinationAirportCode(tempo.get("destinationAirportCode").toString());
            tempoResponse.setDepartureDate(tempo.get("departureDate").toString());
            tempoResponse.setArrivalDate(tempo.get("arrivalDate").toString());
            myList.add(tempoResponse);
        }
        return myList;
    }
    public List<BusyFlightsResponse> convert(BusyFlightsRequest myRequest){
        List<BusyFlightsResponse> myList = new ArrayList<BusyFlightsResponse>();
        CrazyAirRequest myRequestCAR = new CrazyAirRequest();
        myRequestCAR.setOrigin(myRequest.getOrigin());
        myRequestCAR.setDestination(myRequest.getDestination());
        myRequestCAR.setDepartureDate(myRequest.getDepartureDate());
        myRequestCAR.setReturnDate(myRequest.getReturnDate());
        myRequestCAR.setPassengerCount(myRequest.getNumberOfPassengers());

        List<CrazyAirResponse> myListCAR = processCrazyAirFligths(myRequestCAR);
        for (CrazyAirResponse CAR : myListCAR){
            BusyFlightsResponse tempoResponse = new BusyFlightsResponse();
            tempoResponse.setSupplier("CrazyAir");
            tempoResponse.setAirline(CAR.getAirline());
            // Round Fare to 2 decimals
            DecimalFormat df = new DecimalFormat("#.##");
            df.setRoundingMode(RoundingMode.CEILING);
            tempoResponse.setFare(Double.valueOf(df.format(CAR.getPrice())));
            tempoResponse.setDepartureAirportCode(CAR.getDepartureAirportCode());
            tempoResponse.setDestinationAirportCode(CAR.getDestinationAirportCode());

            // Convert DATE FORMAT From ISO_LOCAL_DATE_TIME To ISO_DATE_TIME, Assign ZoneId.systemDefault()
            DateTimeFormatter dformatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            LocalDateTime ldtDate = LocalDateTime.parse(CAR.getDepartureDate(),dformatter);
            DateTimeFormatter dformatterConv = DateTimeFormatter.ISO_DATE_TIME;
            tempoResponse.setDepartureDate(dformatterConv.format(ldtDate.atZone(ZoneId.systemDefault())));
            ldtDate = LocalDateTime.parse(CAR.getArrivalDate(),dformatter);
            tempoResponse.setArrivalDate(dformatterConv.format(ldtDate.atZone(ZoneId.systemDefault())));

            myList.add(tempoResponse);
        }
        return myList;
    }
}
