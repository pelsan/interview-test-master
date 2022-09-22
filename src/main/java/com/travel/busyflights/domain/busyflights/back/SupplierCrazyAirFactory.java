package com.travel.busyflights.domain.busyflights.back;

import org.springframework.web.reactive.function.client.WebClient;

public class SupplierCrazyAirFactory implements SupplierAbstractFactory{
    //private Object webClientBuilder;


    @Override
    public Airline getAirline(){
        return new AirlineCrazyAir ();

    }

}
