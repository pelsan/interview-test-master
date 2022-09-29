package com.travel.busyflights.domain.busyflights.back;

import com.travel.busyflights.domain.busyflights.BusyFlightsRequest;
import com.travel.busyflights.domain.busyflights.BusyFlightsResponse;
import com.travel.busyflights.domain.toughjet.ToughJetRequest;
import com.travel.busyflights.domain.toughjet.ToughJetResponse;
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
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AirlineToughJet implements Airline{


    @Override
    public String getFligths(){
        return "Fligths ToughJet";
    }

    public <T>List<T> getToughJetFligths(ToughJetRequest myRequest) {
        List<T> fligths = new ArrayList<>();
        TcpClient tcpClient = TcpClient.create().option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .doOnConnected(connection -> {
                    connection.addHandlerLast(new ReadTimeoutHandler(5000, TimeUnit.MILLISECONDS));
                    connection.addHandlerLast(new WriteTimeoutHandler(5000, TimeUnit.MILLISECONDS));
                });
        try {
            WebClient webClient = WebClient.builder()
                    .baseUrl("http://localhost:8080/toughjetapi")
                    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                    .clientConnector(new ReactorClientHttpConnector(HttpClient.from(tcpClient)))  // timeout
                    .build();

            WebClient.ResponseSpec myRespo = webClient.post().uri("http://localhost:8080/toughjetapi/fligths").bodyValue(myRequest).retrieve();
            fligths = (List<T>) myRespo.bodyToFlux(Object.class).collectList().block();
           // System.out.println(fligths.get(0).toString());
        } catch (Exception e) {
            System.out.println(e);
            return fligths;
        }
        return fligths;
    }
    private List<ToughJetResponse> processToughJetFligths(ToughJetRequest myRequest){
        List<ToughJetResponse> myList = new ArrayList<ToughJetResponse>();
        List<Object> fligths = getToughJetFligths(myRequest);
        for (Object fligth : fligths)
        {
            java.util.LinkedHashMap tempo  = (java.util.LinkedHashMap)fligth;
            ToughJetResponse tempoResponse = new ToughJetResponse();
            tempoResponse.setBasePrice((double)tempo.get("basePrice"));
            tempoResponse.setCarrier(tempo.get("carrier").toString());
            tempoResponse.setTax((double)tempo.get("tax"));
            tempoResponse.setDiscount((double)tempo.get("discount"));
            tempoResponse.setDepartureAirportName(tempo.get("departureAirportName").toString());
            tempoResponse.setArrivalAirportName(tempo.get("arrivalAirportName").toString());
            tempoResponse.setInboundDateTime(tempo.get("outboundDateTime").toString());
            tempoResponse.setOutboundDateTime(tempo.get("inboundDateTime").toString());
            myList.add(tempoResponse);
        }
        return myList;
    }

    public List<BusyFlightsResponse> convert(BusyFlightsRequest myRequest){
        List<BusyFlightsResponse> myList = new ArrayList<BusyFlightsResponse>();
        ToughJetRequest myRequestTJR = new ToughJetRequest();
        myRequestTJR.setFrom(myRequest.getOrigin());
        myRequestTJR.setFrom(myRequest.getDestination());
        myRequestTJR.setOutboundDate(myRequest.getDepartureDate());
        myRequestTJR.setInboundDate(myRequest.getReturnDate());
        myRequestTJR.setNumberOfAdults(myRequest.getNumberOfPassengers());

        List<ToughJetResponse> myListCAR = processToughJetFligths(myRequestTJR);
        for (ToughJetResponse TJR : myListCAR){
            BusyFlightsResponse tempoResponse = new BusyFlightsResponse();
            tempoResponse.setSupplier("ToughJet");
            tempoResponse.setAirline(TJR.getCarrier());
            // Calculate price
            double dBasePrice = TJR.getBasePrice();
            double dDiscount = TJR.getDiscount();
            double dTax = TJR.getTax();
            double dWithDiscount = dBasePrice *(1 - dDiscount/100);
            double dFare = dWithDiscount *(1 + dTax/100);

            // Round to 2 decimals
            DecimalFormat df = new DecimalFormat("#.##");
            df.setRoundingMode(RoundingMode.CEILING);
            tempoResponse.setFare(Double.valueOf(df.format(dFare)));
            tempoResponse.setDepartureAirportCode(TJR.getDepartureAirportName());
            tempoResponse.setDestinationAirportCode(TJR.getArrivalAirportName());

            // Convert DATE FORMAT From ISO_INSTANT To ISO_DATE_TIME  Assign ZoneId.systemDefault()
            DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT.withZone(ZoneId.systemDefault());
            ZonedDateTime zonedDateTime = ZonedDateTime.parse(TJR.getOutboundDateTime(), formatter);
            DateTimeFormatter dformatterConv = DateTimeFormatter.ISO_DATE_TIME;
            tempoResponse.setDepartureDate(dformatterConv.format(zonedDateTime));
            zonedDateTime = ZonedDateTime.parse(TJR.getInboundDateTime(), formatter);
            tempoResponse.setArrivalDate(dformatterConv.format(zonedDateTime));
            myList.add(tempoResponse);
        }
        return myList;
    }
}
