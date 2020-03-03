package com.example.reservationservice;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.CollectionTable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Collection;
import java.util.stream.Stream;

@SpringBootApplication
@EnableDiscoveryClient
public class ReservationServiceApplication {

	@Bean
	CommandLineRunner commandLineRunner(ReservationRepository reservationRepository){

		return  strings ->{
			Stream.of("Apollo","Prometheus","Helios","Heracles")
					.forEach(r -> reservationRepository.save(new Reservation(r)));
		};
	}

	public static void main(String[] args) {
		SpringApplication.run(ReservationServiceApplication.class, args);
	}


}



@RestResource
interface ReservationRepository extends JpaRepository<Reservation,Long>{
	@RestResource(path="by-name")
	Collection<Reservation> findByReservationName(@Param("reservationName") String reservationName);
}

@RestController
@RefreshScope
class MessageController{

	@Value("${message}")
	private String msg;

	@GetMapping("/message")
	String message(){
		return  this.msg;
	}

}

@Entity
class Reservation{

	@Id
	@GeneratedValue
	private Long id;

	private String reservationName;


	@Override
	public String toString(){
		return "Reservation = {"+
				"id = " +id+
				", reservationName='" + reservationName +'\''+
				'}';

	}

	public Reservation() {
	}

	public Reservation(String reservationName) {
		this.reservationName = reservationName;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getReservationName() {
		return reservationName;
	}

	public void setReservationName(String reservationName) {
		this.reservationName = reservationName;
	}

}