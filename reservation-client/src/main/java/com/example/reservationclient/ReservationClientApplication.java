package com.example.reservationclient;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.ribbon.proxy.annotation.Hystrix;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

@SpringBootApplication
@EnableDiscoveryClient
@EnableZuulProxy
@EnableCircuitBreaker

public class ReservationClientApplication {

	@Bean
	@LoadBalanced
	RestTemplate restTemplate(){
		return new RestTemplate();
	}
	public static void main(String[] args) {
		SpringApplication.run(ReservationClientApplication.class, args);
	}

}


@RestController
@RequestMapping("/reservations")
class ReservationApiGatewayRestController{


	private final RestTemplate restTemplate;

	@Autowired
	ReservationApiGatewayRestController(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	public Collection<String> getReservationNameFallback(){
		return new ArrayList<>();
	}




	@GetMapping("/names")
	@HystrixCommand(fallbackMethod = "getReservationNameFallback")
	public Collection<String> getReservationNames(){

		ParameterizedTypeReference<CollectionModel<Reservation>> ptr=
				new ParameterizedTypeReference<CollectionModel<Reservation>>() {
		};

			ResponseEntity<CollectionModel<Reservation>> entity =
					restTemplate.exchange("http://reservation-service/reservations",
						HttpMethod.GET,
						null,
							ptr
						);
			return entity.getBody().getContent()
					.stream()
					.map(Reservation::getReservationName)
					.collect(Collectors.toList());
	}
}

class Reservation{
	private String reservationName;

	public String getReservationName() {
		return reservationName;
	}
}
