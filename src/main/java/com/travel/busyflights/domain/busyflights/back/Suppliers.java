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

    public List<BusyFlightsResponse> aggregate (BusyFlightsRequest myRequest){
        // Abstract factory Pattern applied to add more Suppliers
        List<BusyFlightsResponse> myList = new ArrayList<BusyFlightsResponse>();
        SupplierAbstractFactory factory = new SupplierCrazyAirFactory();
        Airline myAirLine = factory.getAirline();
        myList.addAll(myAirLine.convert(myRequest));
        factory = new SupplierToughJetFactory();
        myAirLine = factory.getAirline();
        myList.addAll(myAirLine.convert(myRequest));
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
