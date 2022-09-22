package com.travel.busyflights.domain.busyflights.back;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.time.ZoneId;
import com.travel.busyflights.domain.busyflights.BusyFlightsRequest;
import com.travel.busyflights.domain.busyflights.BusyFlightsResponse;
import com.travel.busyflights.domain.crazyair.CrazyAirRequest;
import com.travel.busyflights.domain.crazyair.CrazyAirResponse;
import com.travel.busyflights.domain.toughjet.ToughJetRequest;
import com.travel.busyflights.domain.toughjet.ToughJetResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;
@Service
public class Suppliers {
    private final WebClient.Builder webClientBuilder;

    public Suppliers(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    TcpClient tcpClient = TcpClient.create().option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
            .doOnConnected(connection -> {
                connection.addHandlerLast(new ReadTimeoutHandler(5000, TimeUnit.MILLISECONDS));
                connection.addHandlerLast(new WriteTimeoutHandler(5000, TimeUnit.MILLISECONDS));
            });
    private <T> List<T> getCrazyAirFligths(CrazyAirRequest myRequest) {
        List<T> fligths = new ArrayList<>();
        try {
            WebClient client = webClientBuilder
                    .clientConnector(new ReactorClientHttpConnector(HttpClient.from(tcpClient)))
                    .baseUrl("http://localhost:8080/crazyairapi")
                    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).defaultUriVariables(
                            Collections.singletonMap("url", "http://localhost:8080/crazyairapi"))
                    .build();

            WebClient.ResponseSpec myRespo = client.post().uri("http://localhost:8080/crazyairapi/fligths").bodyValue(myRequest).retrieve();
            fligths = (List<T>) myRespo.bodyToFlux(Object.class).collectList().block();
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

    private List<BusyFlightsResponse> convertCrazyAirFligths(BusyFlightsRequest myRequest){
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


    private <T>List<T> getToughJetFligths(ToughJetRequest myRequest) {
        List<T> fligths = new ArrayList<>();
        try {
            WebClient client = webClientBuilder
                    .clientConnector(new ReactorClientHttpConnector(HttpClient.from(tcpClient)))
                    .baseUrl("http://localhost:8080/toughjetapi")
                    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).defaultUriVariables(
                            Collections.singletonMap("url", "http://localhost:8080/toughjetapi"))
                    .build();

            WebClient.ResponseSpec myRespo = client.post().uri("http://localhost:8080/toughjetapi/fligths").bodyValue(myRequest).retrieve();
            fligths = (List<T>) myRespo.bodyToFlux(Object.class).collectList().block();
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

    private List<BusyFlightsResponse> convertToughJetFligths(BusyFlightsRequest myRequest){
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
    public List<BusyFlightsResponse> aggregate (BusyFlightsRequest myRequest){
        List<BusyFlightsResponse> myList = new ArrayList<BusyFlightsResponse>(convertCrazyAirFligths(myRequest));
        myList.addAll(convertToughJetFligths(myRequest));
        Collections.sort(myList, new Sortbyfare());
        return myList;
    }
    class Sortbyfare implements Comparator<BusyFlightsResponse> {

        public int compare(BusyFlightsResponse a, BusyFlightsResponse b)
        {
            if (a.getFare() == b.getFare()) return 0;
            if (a.getFare() > b.getFare()){
                return 1;
            }else {
                return -1;
            }
        }
    }
}
