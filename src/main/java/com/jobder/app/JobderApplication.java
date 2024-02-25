package com.jobder.app;

import com.jobder.app.authentication.models.users.AvailabilityStatus;
import com.jobder.app.authentication.models.users.RoleName;
import com.jobder.app.authentication.models.users.SearchParameters;
import com.jobder.app.authentication.models.users.User;
import com.jobder.app.authentication.repositories.UserRepository;
import jakarta.annotation.Resource;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

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

		@Resource
		private BCryptPasswordEncoder passwordEncoder;

		@Override
		public void run(String... args) throws Exception {
			List<User> workers = new LinkedList<>();

			workers.add(new User("1", "Juan Perez", "juan@example.com", passwordEncoder.encode("test"), null, RoleName.WORKER, "123456789",
					"123 Main St", -38.70610414693878, -62.269059204081636, new Date(), false, "Plomero", AvailabilityStatus.AVAILABLE,
					"Plomero", "9 AM - 5 PM", "1.5",12, 0, null));

			workers.add(new User("2", "Maria Rodriguez", "maria@example.com", passwordEncoder.encode("test"), null, RoleName.WORKER, "987654321",
					"456 Oak St", -38.70610414693878, -62.269059204081636, new Date(),false, "Electricista", AvailabilityStatus.NOT_AVAILABLE,
					"Electricista", "10 AM - 6 PM", "4.8",5, 0, null));

			workers.add(new User("3", "Carlos Gutierrez", "carlos@example.com", passwordEncoder.encode("test"), null, RoleName.WORKER, "5551234567",
					"789 Elm St", -38.70610414693878, -62.269059204081636, new Date(),false, "Electricista", AvailabilityStatus.AVAILABLE,
					"Electricista", "8 AM - 4 PM", "4.2", 21, 0, null));

			workers.add(new User("4", "Laura Martinez", "laura@example.com", passwordEncoder.encode("test"), null, RoleName.WORKER, "3339876543",
					"567 Pine St", -38.70610414693878, -62.269059204081636, new Date(),false, "Plomero", AvailabilityStatus.NOT_AVAILABLE,
					"Plomero", "9 AM - 6 PM", "4.2", 88, 0, null));

			workers.add(new User("5", "Daniel Hernandez", "daniel@example.com", passwordEncoder.encode("test"), null, RoleName.WORKER, "6666543210",
					"890 Oak St", -38.70610414693878, -62.269059204081636, new Date(),false, "Plomero", AvailabilityStatus.AVAILABLE,
					"Plomero", "10 AM - 7 PM", "2.2", 67, 0, null));

			workers.add(new User("6", "Isabel Lopez", "isabel@example.com", passwordEncoder.encode("test"), null, RoleName.WORKER, "7777890123",
					"123 Cedar St", -38.70610414693878, -62.269059204081636, new Date(),false, "Electricista", AvailabilityStatus.NOT_AVAILABLE,
					"Electricista", "11 AM - 8 PM", "3.2", 54, 0, null));

			for(User user : workers){
				if(!userRepository.existsByEmail(user.getEmail())){
					userRepository.save(user);
				}
			}

			for (int i = 7; i < 30; i++){
				User worker = new User(i + "a", "Trabajador " + i, "worker" + i + "@example.com", passwordEncoder.encode("test"), null, RoleName.WORKER, "7777890123",
						"123 Cedar St", -38.70610414693878, -62.269059204081636, new Date(),false, "Electricista", AvailabilityStatus.AVAILABLE,
						"Electricista", "11 AM - 8 PM", "3.2", 54, 0, null);

				if(!userRepository.existsByEmail(worker.getEmail())){
					userRepository.save(worker);
				}
			}
		}
	}
}
