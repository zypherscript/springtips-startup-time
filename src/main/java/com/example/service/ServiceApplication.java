package com.example.service;

import static com.example.service.ServiceApplication.START;
import static com.example.service.ServiceApplication.STOP;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.annotation.Id;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class ServiceApplication {

  static final AtomicReference<Instant> START = new AtomicReference<>(Instant.now());
  static final AtomicReference<Instant> STOP = new AtomicReference<>();

  public static void main(String[] args) {
    START.set(Instant.now());
    SpringApplication.run(ServiceApplication.class, args);
  }

  @Component
  static class StartupListener {

    @EventListener(ApplicationReadyEvent.class)
    void ready() {
      STOP.set(Instant.now());
    }
  }

}

@RestController
@ImportRuntimeHints(Endpoints.Hints.class)
class Endpoints {

  static class Hints implements RuntimeHintsRegistrar {

    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
      hints.resources().registerResource(HELLO);
    }
  }

  private final CustomerRepository customerRepository;

  static final Resource HELLO = new ClassPathResource("/hello");

  Endpoints(CustomerRepository customerRepository) {
    this.customerRepository = customerRepository;
  }

  @GetMapping("/hello")
  Resource hello() {
    return HELLO;
  }

  @GetMapping("/startup")
  String startup() {
    return Duration.between(START.get(), STOP.get()).toString().substring(2);
  }

  @GetMapping("/customers")
  List<Customer> customer() {
    return this.customerRepository.findAll();
  }
}

interface CustomerRepository extends ListCrudRepository<Customer, Integer> {

}

record Customer(@Id Integer id, String name) {

}
