// 양동현. 2025.06.18
package com.example.pharmacy.customer.service;

import com.example.pharmacy.customer.dto.CustomerDto;
import com.example.pharmacy.customer.dto.CustomerFormDto;
import com.example.pharmacy.customer.dto.CustomerSearchDto;
import com.example.pharmacy.customer.entity.Customer;
import com.example.pharmacy.customer.repository.CustomerRepository;
import com.example.pharmacy.sales.repository.SaleRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final SaleRepository saleRepository;

    /**
     * 고객 등록
     */
    public Customer saveCustomer(CustomerFormDto customerFormDto) {
        // 전화번호 중복 체크 (전화번호가 있는 경우)
        if (customerFormDto.getPhone() != null && !customerFormDto.getPhone().trim().isEmpty()) {
            if (customerRepository.existsByPhone(customerFormDto.getPhone())) {
                throw new IllegalStateException("이미 등록된 전화번호입니다.");
            }
        }

        // 이메일 중복 체크 (이메일이 있는 경우)
        if (customerFormDto.getEmail() != null && !customerFormDto.getEmail().trim().isEmpty()) {
            if (customerRepository.existsByEmailAndEmailIsNotNull(customerFormDto.getEmail())) {
                throw new IllegalStateException("이미 등록된 이메일입니다.");
            }
        }

        Customer customer = new Customer();
        customer.setName(customerFormDto.getName());
        customer.setBirthdate(customerFormDto.getBirthdate());
        customer.setPhone(customerFormDto.getPhone());
        customer.setEmail(customerFormDto.getEmail());
        customer.setAddress(customerFormDto.getAddress());
        customer.setIsMember(customerFormDto.getIsMember());

        return customerRepository.save(customer);
    }

    /**
     * 고객 수정
     */
    public Customer updateCustomer(CustomerFormDto customerFormDto) {
        Customer customer = customerRepository.findById(customerFormDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("고객을 찾을 수 없습니다."));

        // 전화번호 중복 체크 (다른 고객과 중복되는지 확인)
        if (customerFormDto.getPhone() != null && !customerFormDto.getPhone().trim().isEmpty()) {
            Customer existingCustomerWithPhone = customerRepository.findByPhone(customerFormDto.getPhone());
            if (existingCustomerWithPhone != null && !existingCustomerWithPhone.getId().equals(customer.getId())) {
                throw new IllegalStateException("이미 등록된 전화번호입니다.");
            }
        }

        // 이메일 중복 체크 (다른 고객과 중복되는지 확인)
        if (customerFormDto.getEmail() != null && !customerFormDto.getEmail().trim().isEmpty()) {
            Customer existingCustomerWithEmail = customerRepository.findByEmail(customerFormDto.getEmail());
            if (existingCustomerWithEmail != null && !existingCustomerWithEmail.getId().equals(customer.getId())) {
                throw new IllegalStateException("이미 등록된 이메일입니다.");
            }
        }

        customer.setName(customerFormDto.getName());
        customer.setBirthdate(customerFormDto.getBirthdate());
        customer.setPhone(customerFormDto.getPhone());
        customer.setEmail(customerFormDto.getEmail());
        customer.setAddress(customerFormDto.getAddress());
        customer.setIsMember(customerFormDto.getIsMember());

        return customerRepository.save(customer);
    }

    /**
     * 고객 삭제
     */
    public void deleteCustomer(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("고객을 찾을 수 없습니다."));

        // 판매 이력이 있는지 확인
        boolean hasSales = !saleRepository.findByCustomerIdOrderBySaleDateDesc(customerId).isEmpty();
        if (hasSales) {
            throw new IllegalStateException("판매 이력이 있는 고객은 삭제할 수 없습니다. 대신 비활성화를 고려해주세요.");
        }

        customerRepository.delete(customer);
    }

    /**
     * 고객 조회 (단건)
     */
    @Transactional(readOnly = true)
    public CustomerDto getCustomerById(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("고객을 찾을 수 없습니다."));
        return CustomerDto.of(customer);
    }

    /**
     * 고객 수정용 폼 데이터 조회
     */
    @Transactional(readOnly = true)
    public CustomerFormDto getCustomerFormDto(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("고객을 찾을 수 없습니다."));

        CustomerFormDto customerFormDto = new CustomerFormDto();
        customerFormDto.setId(customer.getId());
        customerFormDto.setName(customer.getName());
        customerFormDto.setBirthdate(customer.getBirthdate());
        customerFormDto.setPhone(customer.getPhone());
        customerFormDto.setEmail(customer.getEmail());
        customerFormDto.setAddress(customer.getAddress());
        customerFormDto.setIsMember(customer.getIsMember());

        return customerFormDto;
    }

    /**
     * 전체 고객 목록 조회
     */
    @Transactional(readOnly = true)
    public List<CustomerDto> getAllCustomers() {
        List<Customer> customers = customerRepository.findAll();
        return customers.stream()
                .map(CustomerDto::of)
                .collect(Collectors.toList());
    }

    /**
     * 회원/비회원 목록 조회
     */
    @Transactional(readOnly = true)
    public List<CustomerDto> getCustomersByMemberStatus(Boolean isMember) {
        List<Customer> customers = customerRepository.findByIsMember(isMember);
        return customers.stream()
                .map(CustomerDto::of)
                .collect(Collectors.toList());
    }
}
