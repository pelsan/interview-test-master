package com.travel.busyflights.domain.busyflights.back;

import com.travel.busyflights.domain.busyflights.BusyFlightsRequest;
import com.travel.busyflights.domain.busyflights.BusyFlightsResponse;

import java.util.List;

public interface SupplierAbstractFactory {
    Airline getAirline();
}
