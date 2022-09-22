package com.travel.busyflights.domain.busyflights.back;

public class SupplierToughJetFactory implements SupplierAbstractFactory{
    @Override
    public Airline getAirline(){
        return new AirlineToughJet ();
    }
}
