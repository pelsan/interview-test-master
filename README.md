**Travel - Problem to be solved**

**Background:**

BusyFlights is a flights search solution which aggregates flight results initially from 2 different suppliers (CrazyAir and ToughJet). A future iteration (not part of the test) may add more suppliers.


**What is required:**

Use this repository as a base to implement the Busy Flights service that should produce an aggregated result from both CrazyAir and ToughJet.

The use of JPA and database is not required; you can hardcode/mock data instead.

The result should be a JSON response which contains a list of flights ordered by fare which has the following attributes:

**Busy Flights API**

**Request**

| Name | Description |
| ------ | ------ |
| origin | 3 letter IATA code(eg. LHR, AMS) |
| destination | 3 letter IATA code(eg. LHR, AMS) |
| departureDate | ISO_LOCAL_DATE format |
| returnDate | ISO_LOCAL_DATE format |
| numberOfPassengers | Maximum 4 passengers |

**Response**

| Name | Description |
| ------ | ------ |
| airline | Name of Airline |
| supplier | Eg: CrazyAir or ToughJet |
| fare | Total price rounded to 2 decimals |
| departureAirportCode | 3 letter IATA code(eg. LHR, AMS) |
| destinationAirportCode | 3 letter IATA code(eg. LHR, AMS) |
| departureDate | ISO_DATE_TIME format |
| arrivalDate | ISO_DATE_TIME format |

The service should connect to the both the suppliers using HTTP.

**CrazyAir API**

**Request**

| Name | Description |
| ------ | ------ |
| origin | 3 letter IATA code(eg. LHR, AMS) |
| destination | 3 letter IATA code(eg. LHR, AMS) |
| departureDate | ISO_LOCAL_DATE format |
| returnDate | ISO_LOCAL_DATE format |
| passengerCount | Number of passengers |

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

**ToughJet API**

**Request**

| Name | Description |
| ------ | ------ |
| from | 3 letter IATA code(eg. LHR, AMS) |
| to | 3 letter IATA code(eg. LHR, AMS) |
| outboundDate |ISO_LOCAL_DATE format |
| inboundDate | ISO_LOCAL_DATE format |
| numberOfAdults | Number of passengers |

**Response**

| Name | Description |
| ------ | ------ |
| carrier | Name of the Airline |
| basePrice | Price without tax(doesn't include discount) |
| tax | Tax which needs to be charged along with the price |
| discount | Discount which needs to be applied on the price(in percentage) |
| departureAirportName | 3 letter IATA code(eg. LHR, AMS) |
| arrivalAirportName | 3 letter IATA code(eg. LHR, AMS) |
| outboundDateTime | ISO_INSTANT format |
| inboundDateTime | ISO_INSTANT format |

**What you need to provide:**

- A solution that meets the above requirements.
- The implementation should be made as close to 'production ready' as possible within the time constraints.

It is fine to change any of the supplied application code, if you choose to do so please add comments to indicate what has changed and why.

**Note**

Please clone this project then create your own repository from it. Do not fork/branch this project when creating your solution as it will be visible to other applicants.



**Comments about what was done in the code challenge**



about pom.xml file:
I update the version number of spring-boot from 1.5.3.RELEASE to 2.6.2 in order to have working the dependencies that I added.
<artifactId>spring-boot-starter-parent</artifactId>
<!-- UPDATED IN ORDER TO USE WEBFLUX
<version>1.5.3.RELEASE</version>
last 2.7.0
-->
<version>2.6.2</version>
The I added these dependences: Webflux and Swagger
Webflux as I use the webclient to communicate to other services
<dependency>
<groupId>org.springframework.boot</groupId>
<artifactId>spring-boot-starter-webflux</artifactId>
</dependency>

Swagger to support open-api org.springdoc version 1.6.4
<dependency>
<groupId>org.springdoc</groupId>
<artifactId>springdoc-openapi-ui</artifactId>
<version>1.6.4</version>
</dependency>

For swagger on application.properties file y added:
springdoc.swagger-ui.path=/swagger/index.html
springdoc.swagger-ui.enabled=true

So we have http://localhost:8080/swagger/index.html expose the information about the microservices, I set temporally springdoc.swagger-ui.enabled=true
, but on production stage it will be set to false.
The classes Response and Request of three services I reused them with no modification, but I had to add some code to BusyFlightsResponse as it was empty, nut i code that class as requirements in README.md file
I code using IntelliJ IDEA IDE  2002.1.1 Community Edition
I coded two packages named:
com.travel.busyflights.domain.busyflights.back
com.travel.busyflights.domain.busyflights.exceptions
the first one has the Suppliers class that manage communication, process data, an order responses, the second one package is exception that’s for a centralized exception management, with that I catch input data exception , for instance if the request have 5 passenger then throws an exception and the response is like:
HTTP Error Code 400
{
"type": "/errors/uncategorized",
"title": "Validation error",
"code": "001",
"detail": "Maximum 4 passengers and minimum 1",
"instance": "/errors/uncategorized/bank",
"status": "001"
}

So, we validate all request that is like
Busy Flights API
Request
Name	Description
origin	3 letter IATA code(eg. LHR, AMS)
destination	3 letter IATA code(eg. LHR, AMS)
departureDate	ISO_LOCAL_DATE format
returnDate	ISO_LOCAL_DATE format
numberOfPassengers	Maximum 4 passengers

About consuming services, the response of CrazyAirRestController
And ToughJetController is hardcoded, but it has data like the specification and date types as well.

although the response is hardcoded, the consumer service validates date types

About Zone time for have the same I selected ZoneId.systemDefault()so all date on response is standardized with [America/Mexico_City]

About rounding mode, I used ceiling so 1.001 convert to 1.01


The sorting of the response I used collection as I coded a comparator class to be used to sort by fare


The web client has a time-out set on 5000 milliseconds

Things to improve the project:

Move all the services URLs to application.properties file, add a specific host connection exception o=in the exception manager

Improve the Supplier class that manage communication and process data to separated classes in order that add more services easily and add some junit test to the project