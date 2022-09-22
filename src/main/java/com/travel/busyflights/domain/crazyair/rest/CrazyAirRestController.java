package com.travel.busyflights.domain.crazyair.rest;

import com.travel.busyflights.domain.crazyair.CrazyAirRequest;
import com.travel.busyflights.domain.crazyair.CrazyAirResponse;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("crazyairapi")
public class CrazyAirRestController {
    @PostMapping("fligths")
    public List<CrazyAirResponse> getFligths(@RequestBody CrazyAirRequest myRequest)
    {
        List<CrazyAirResponse> myList = new ArrayList<CrazyAirResponse>();
        CrazyAirResponse fligth01 = new CrazyAirResponse();
        /*
        **Response**

        | Name | Description |
        | ------ | ------ |
        | airline | Name of the airline |
        | price | Total price |
        | cabinclass | E for Economy and B for Business |
        | departureAirportCode | Eg: LHR |
        | destinationAirportCode | Eg: LHR |
        | departureDate | ISO_LOCAL_DATE_TIME format |
        | arrivalDate | ISO_LOCAL_DATE_TIME format |

         */
        fligth01.setAirline("AMERICAN AIRLINES");
        fligth01.setPrice(109.00);
        fligth01.setCabinclass("E");
        fligth01.setDepartureAirportCode("AMS");
        fligth01.setDestinationAirportCode("MEX");
        fligth01.setDepartureDate("2022-12-03T10:15:30");
        fligth01.setArrivalDate("2022-12-03T10:17:30");

        CrazyAirResponse fligth02 = new CrazyAirResponse();
        fligth02.setAirline("DELTA");
        fligth02.setPrice(19.001);
        fligth02.setCabinclass("B");
        fligth02.setDepartureAirportCode("AMS");
        fligth02.setDestinationAirportCode("MEX");
        fligth02.setDepartureDate("2022-12-03T10:16:30");
        fligth02.setArrivalDate("2022-12-03T10:18:30");

        myList.add(fligth01);
        myList.add(fligth02);

        return myList;
    }

}
