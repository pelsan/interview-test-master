package com.travel.busyflights.domain.busyflights.rest;

import com.travel.busyflights.domain.busyflights.BusyFlightsRequest;
import com.travel.busyflights.domain.busyflights.BusyFlightsResponse;
import com.travel.busyflights.domain.busyflights.back.Suppliers;
import com.travel.busyflights.domain.busyflights.exceptions.BussinesRuleException;
import com.travel.busyflights.domain.crazyair.CrazyAirRequest;
import com.travel.busyflights.domain.crazyair.CrazyAirResponse;
import com.travel.busyflights.domain.toughjet.ToughJetRequest;
import com.travel.busyflights.domain.toughjet.ToughJetResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("busyfligthsapi")
public class BusyFlightsController {
    @Autowired
    Suppliers sp;

    @PostMapping("fligths")
    public List<BusyFlightsResponse> getFligths(@RequestBody BusyFlightsRequest myRequest) throws BussinesRuleException {
                /*
        **Response**
        | Name                      | Description |
        | ------                    | ------ |
        | airline                   | Name of Airline |
        | supplier                  | Eg: CrazyAir or ToughJet |
        | fare                      | Total price rounded to 2 decimals |
        | departureAirportCode      | 3 letter IATA code(eg. LHR, AMS) |
        | destinationAirportCode    | 3 letter IATA code(eg. LHR, AMS) |
        | departureDate             | ISO_DATE_TIME format |
        | arrivalDate               | ISO_DATE_TIME format |
         */
        List<BusyFlightsResponse> myList = new ArrayList<BusyFlightsResponse>();
        // Validate Request
        int iPassengers = myRequest.getNumberOfPassengers();
        if (iPassengers <1 || iPassengers > 4){
            BussinesRuleException exception = new BussinesRuleException("001","Maximum 4 passengers and minimum 1", HttpStatus.BAD_REQUEST);
            throw exception;
        }
        // Validate Date format ISO_LOCAL_DATE
        try {
            DateTimeFormatter dformatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            LocalDateTime ldtDate = LocalDateTime.parse(myRequest.getDepartureDate(), dformatter);
            ldtDate = LocalDateTime.parse(myRequest.getReturnDate(), dformatter);
        }
        catch (Exception ex){
            BussinesRuleException exception = new BussinesRuleException("002","Format date incorrect", HttpStatus.BAD_REQUEST);
            throw exception;
            }
        // Validate 3 Letters IATA code
        if(myRequest.getDestination().length()!=3 || myRequest.getOrigin().length()!=3 ){
            BussinesRuleException exception = new BussinesRuleException("003","IATA CODE length Incorrect, it has to be 3 Letters", HttpStatus.BAD_REQUEST);
            throw exception;
        }
        myList = sp.aggregate(myRequest);
        return myList;
    }
}
