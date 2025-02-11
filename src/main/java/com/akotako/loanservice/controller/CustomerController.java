package com.akotako.loanservice.controller;

import com.akotako.loanservice.model.entity.Customer;
import com.akotako.loanservice.model.response.ResponseObject;
import com.akotako.loanservice.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerRepository customerRepository;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ResponseObject<Customer>> createCustomer(@RequestBody Customer customer) {
        Customer savedCustomer = customerRepository.save(customer);
        return ResponseEntity.ok(ResponseObject.success(savedCustomer));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<ResponseObject<List<Customer>>> listCustomers() {
        List<Customer> customers = customerRepository.findAll();
        return ResponseEntity.ok(ResponseObject.success(customers));
    }

    @PreAuthorize("hasRole('ADMIN') or @customerService.isOwnCustomer(#id, authentication.name)")
    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject<Customer>> getCustomerById(@PathVariable Long id) {
        return customerRepository.findById(id)
                .map(customer -> ResponseEntity.ok(ResponseObject.success(customer)))
                .orElse(ResponseEntity.badRequest().body(ResponseObject.failure("Customer not found")));
    }
}
