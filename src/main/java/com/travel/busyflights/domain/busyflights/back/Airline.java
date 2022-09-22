package com.travel.busyflights.domain.busyflights.back;

import com.travel.busyflights.domain.busyflights.BusyFlightsRequest;
import com.travel.busyflights.domain.busyflights.BusyFlightsResponse;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

public interface Airline {
        String getFligths();
        public List<BusyFlightsResponse> convert(BusyFlightsRequest myRequest);
}
