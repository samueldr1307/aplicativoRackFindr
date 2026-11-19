package co.edu.unbosque.rackFindrBack;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class RackFindrBackApplication {

	public static void main(String[] args) {
		SpringApplication.run(RackFindrBackApplication.class, args);
	}
	@Bean
	public ModelMapper getModelMapper() {
	    return new ModelMapper();
	}

}
