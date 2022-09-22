package com.travel.busyflights.domain.toughjet.rest;

import com.travel.busyflights.domain.toughjet.ToughJetRequest;
import com.travel.busyflights.domain.toughjet.ToughJetResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping("toughjetapi")
public class ToughJetController {
    @PostMapping("fligths")
    public List<ToughJetResponse> getFligths(@RequestBody ToughJetRequest myRequest)
    {
        List<ToughJetResponse> myList = new ArrayList<ToughJetResponse>();
        ToughJetResponse fligth01 = new ToughJetResponse();

        /*
        **Response**
        | Name                  | Description |
        | ------                | ------ |
        | carrier               | Name of the Airline |
        | basePrice             | Price without tax(doesn't include discount) |
        | tax                   | Tax which needs to be charged along with the price |
        | discount              | Discount which needs to be applied on the price(in percentage) |
        | departureAirportName  | 3 letter IATA code(eg. LHR, AMS) |
        | arrivalAirportName    | 3 letter IATA code(eg. LHR, AMS) |
        | outboundDateTime      | ISO_INSTANT format |
        | inboundDateTime       | ISO_INSTANT format |
         */
        fligth01.setCarrier("SOUTHWEST AIRLINES");
        fligth01.setBasePrice(100.00);
        fligth01.setTax(5.00);
        fligth01.setDiscount(7.00);
        fligth01.setDepartureAirportName("AMS");
        fligth01.setArrivalAirportName("MEX");
        fligth01.setOutboundDateTime("2022-12-03T10:15:30Z");
        fligth01.setInboundDateTime("2022-12-03T11:15:30Z");

        ToughJetResponse fligth02 = new ToughJetResponse();
        fligth02.setCarrier("DELTA AIRLINES");
        fligth02.setBasePrice(110.00);
        fligth02.setTax(5.00);
        fligth02.setDiscount(7.00);
        fligth02.setDepartureAirportName("AMS");
        fligth02.setArrivalAirportName("MEX");
        fligth02.setOutboundDateTime("2022-12-03T10:15:30Z");
        fligth02.setInboundDateTime("2022-12-03T11:15:30Z");

        myList.add(fligth01);
        myList.add(fligth02);

        return myList;
    }
}
