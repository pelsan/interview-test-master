package com.travel.busyflights;
import com.travel.busyflights.domain.busyflights.BusyFlightsRequest;
import com.travel.busyflights.domain.busyflights.back.AirlineCrazyAir;
import com.travel.busyflights.domain.busyflights.back.AirlineToughJet;
import com.travel.busyflights.domain.busyflights.back.Suppliers;
import com.travel.busyflights.domain.crazyair.CrazyAirRequest;
import com.travel.busyflights.domain.toughjet.ToughJetRequest;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static reactor.core.publisher.Mono.when;

@TestPropertySource("/application-test.properties")
@AutoConfigureMockMvc
@SpringBootTest
public class BusyFlightsApplicationTests {
	private static MockHttpServletRequest request;
	public static final MediaType APPLICATION_JSON_UTF8 = MediaType.APPLICATION_JSON;
	private BusyFlightsRequest requestBFR;
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	ObjectMapper objectMapper;

	@Test
	public void getBusyFligthsServerUP() throws Exception {
		requestBFR = new BusyFlightsRequest();
		requestBFR.setOrigin("ORI");
		requestBFR.setDestination("TOT");
		requestBFR.setDepartureDate("2011-12-03T10:15:30");
		requestBFR.setReturnDate("2011-12-03T10:15:30");
		requestBFR.setNumberOfPassengers(4);
		mockMvc.perform(MockMvcRequestBuilders.post("/busyfligthsapi/fligths")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(requestBFR)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$",hasSize(4)));

	}
	@Test
	public void getBusyFligthsDateError() throws Exception {
		requestBFR = new BusyFlightsRequest();
		requestBFR.setOrigin("ORI");
		requestBFR.setDestination("TOT");
		requestBFR.setDepartureDate("2011-12-03T10:15:30X");
		requestBFR.setReturnDate("2011-12-03T10:15:30X");
		requestBFR.setNumberOfPassengers(4);
		mockMvc.perform(MockMvcRequestBuilders.post("/busyfligthsapi/fligths")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(requestBFR)))
				.andExpect(status().is4xxClientError())
				.andExpect(jsonPath("$.detail", is("Format date incorrect")));
	}
	@Test
	public void getBusyFligthsIATACodeError() throws Exception {

		requestBFR = new BusyFlightsRequest();
		requestBFR.setOrigin("ORI");
		requestBFR.setDestination("TOT2");
		requestBFR.setDepartureDate("2011-12-03T10:15:30");
		requestBFR.setReturnDate("2011-12-03T10:15:30");
		requestBFR.setNumberOfPassengers(4);
		mockMvc.perform(MockMvcRequestBuilders.post("/busyfligthsapi/fligths")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(requestBFR)))
				.andExpect(status().is4xxClientError())
				.andExpect(jsonPath("$.detail", is("IATA CODE length Incorrect, it has to be 3 Letters")));
	}

	@Mock
	AirlineToughJet airlineToughJetMock;
	@Mock
	AirlineCrazyAir airlineCrazyAirMock;
	@InjectMocks
	private Suppliers suppliersService;
/*
	@Test
	public void getBusyFligths() throws Exception{
		ToughJetRequest requestTJ = new ToughJetRequest();
		List<Object> fligths = new ArrayList<>();
		when(airlineToughJetMock.getToughJetFligths(requestTJ)).thenReturn(fligths);

		CrazyAirRequest requestCA = new CrazyAirRequest();
		fligths = new ArrayList<>();
		when(airlineCrazyAirMock.getCrazyAirFligths(requestCA)).thenReturn(fligths);



		requestBFR = new BusyFlightsRequest();
		requestBFR.setOrigin("ORI");
		requestBFR.setDestination("TOT");
		requestBFR.setDepartureDate("2011-12-03T10:15:30");
		requestBFR.setReturnDate("2011-12-03T10:15:30");
		requestBFR.setNumberOfPassengers(4);
		mockMvc.perform(MockMvcRequestBuilders.post("/busyfligthsapi/fligths")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(requestBFR)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$",hasSize(4)));


				*/



}
