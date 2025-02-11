package com.akotako.loanservice.service;

import com.akotako.loanservice.model.entity.Customer;
import com.akotako.loanservice.model.exception.CustomerNotFoundException;
import com.akotako.loanservice.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    public boolean isOwnCustomer(Long customerId, String username) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found."));
        return customer.getUsername().equals(username);
    }
}
