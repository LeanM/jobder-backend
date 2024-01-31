package com.jobder.app;

import com.jobder.app.authentication.models.RoleName;
import com.jobder.app.authentication.models.User;
import com.jobder.app.authentication.repositories.UserRepository;
import com.jobder.app.authentication.services.UserService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@SpringBootApplication
public class JobderApplication {

	public static void main(String[] args) {
		SpringApplication.run(JobderApplication.class, args);
	}

	@Bean
	public CommandLineRunner populateWorkers() {
		return new WorkerPopulatorRunner();
	}

	public class WorkerPopulatorRunner implements CommandLineRunner {
		@Resource
		private UserRepository userRepository;

		@Override
		public void run(String... args) throws Exception {
			List<User> workers = new LinkedList<>();

			workers.add(new User("1", "Juan Perez", "juan@example.com", "password123", "profile1.jpg", RoleName.WORKER, "123456789",
					"123 Main St", -38.8562944, -60.0670208, new Date(), "Plomero", "Available",
					"Plomero", "9 AM - 5 PM", "4.5",12));

			workers.add(new User("2", "Maria Rodriguez", "maria@example.com", "password456", "profile2.jpg", RoleName.WORKER, "987654321",
					"456 Oak St", -38.8562944, -60.0670208, new Date(), "Electricista", "Not Available",
					"Electricista", "10 AM - 6 PM", "4.8",5));

			workers.add(new User("3", "Carlos Gutierrez", "carlos@example.com", "pass789", "profile3.jpg", RoleName.WORKER, "5551234567",
					"789 Elm St", -38.8562944, -60.0670208, new Date(), "Electricista", "Available",
					"Electricista", "8 AM - 4 PM", "4.2", 21));

			workers.add(new User("4", "Laura Martinez", "laura@example.com", "passabc", "profile4.jpg", RoleName.WORKER, "3339876543",
					"567 Pine St", -38.8562944, -60.0670208, new Date(), "Plomero", "Not Available",
					"Plomero", "9 AM - 6 PM", "4.6", 88));

			workers.add(new User("5", "Daniel Hernandez", "daniel@example.com", "passxyz", "profile5.jpg", RoleName.WORKER, "6666543210",
					"890 Oak St", -38.8562944, -60.0670208, new Date(), "Plomero", "Available",
					"Plomero", "10 AM - 7 PM", "4.9", 67));

			workers.add(new User("6", "Isabel Lopez", "isabel@example.com", "pass456", "profile6.jpg", RoleName.WORKER, "7777890123",
					"123 Cedar St", -38.8562944, -60.0670208, new Date(), "Electricista", "Not Available",
					"Electricista", "11 AM - 8 PM", "4.7", 54));

			for(User user : workers){
				if(!userRepository.existsByEmail(user.getEmail())){
					userRepository.save(user);
				}
			}

		}
	}
}
