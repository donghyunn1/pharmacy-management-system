// 양동현. 2025.06.18
package com.example.pharmacy.customer.controller;

import com.example.pharmacy.customer.dto.CustomerDto;
import com.example.pharmacy.customer.dto.CustomerFormDto;
import com.example.pharmacy.customer.dto.CustomerSearchDto;
import com.example.pharmacy.customer.entity.Customer;
import com.example.pharmacy.customer.service.CustomerService;
import com.example.pharmacy.sales.dto.SaleDto;
import com.example.pharmacy.sales.service.SaleService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/customers")
public class CustomerController {

    private final CustomerService customerService;
    private final SaleService saleService;

    /**
     * 고객 관리 페이지 (목록 + 검색)
     */
    @GetMapping
    public String customerManagement(@ModelAttribute("customerSearchDto") CustomerSearchDto customerSearchDto, Model model) {
        List<CustomerDto> customers;


        customers = customerService.getAllCustomers();

        model.addAttribute("customers", customers);
        model.addAttribute("customerSearchDto", customerSearchDto);
        return "customer/customerManagement";
    }

    /**
     * 고객 등록 폼
     */
    @GetMapping("/new")
    public String customerForm(Model model) {
        model.addAttribute("customerFormDto", new CustomerFormDto());
        return "customer/customerForm";
    }

    /**
     * 고객 등록 처리
     */
    @PostMapping("/new")
    public String customerForm(@Valid CustomerFormDto customerFormDto,
                               BindingResult bindingResult,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "customer/customerForm";
        }

        try {
            Customer customer = customerService.saveCustomer(customerFormDto);
            redirectAttributes.addFlashAttribute("successMessage", "고객이 성공적으로 등록되었습니다.");
            return "redirect:/admin/customers";
        } catch (IllegalStateException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "customer/customerForm";
        }
    }

    /**
     * 고객 상세 페이지
     */
    @GetMapping("/{customerId}")
    public String customerDetail(@PathVariable("customerId") Long customerId, Model model) {
        try {
            CustomerDto customerDto = customerService.getCustomerById(customerId);
            List<SaleDto> customerSales = saleService.getSalesByCustomerId(customerId);

            // 구매 통계 계산
            if (!customerSales.isEmpty()) {
                BigDecimal totalAmount = customerSales.stream()
                        .filter(sale -> sale.getStatus().name().equals("COMPLETED"))
                        .map(SaleDto::getTotalAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                int completedSalesCount = (int) customerSales.stream()
                        .filter(sale -> sale.getStatus().name().equals("COMPLETED"))
                        .count();

                BigDecimal avgAmount = completedSalesCount > 0 ?
                        totalAmount.divide(BigDecimal.valueOf(completedSalesCount), 2, RoundingMode.HALF_UP) :
                        BigDecimal.ZERO;

                model.addAttribute("totalPurchaseAmount", totalAmount);
                model.addAttribute("completedSalesCount", completedSalesCount);
                model.addAttribute("avgPurchaseAmount", avgAmount);
            } else {
                model.addAttribute("totalPurchaseAmount", BigDecimal.ZERO);
                model.addAttribute("completedSalesCount", 0);
                model.addAttribute("avgPurchaseAmount", BigDecimal.ZERO);
            }

            model.addAttribute("customer", customerDto);
            model.addAttribute("sales", customerSales);
            return "customer/customerDetail";
        } catch (EntityNotFoundException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "error/404";
        }
    }

    /**
     * 고객 수정 폼
     */
    @GetMapping("/{customerId}/edit")
    public String customerEditForm(@PathVariable("customerId") Long customerId, Model model) {
        try {
            CustomerFormDto customerFormDto = customerService.getCustomerFormDto(customerId);
            model.addAttribute("customerFormDto", customerFormDto);
            return "customer/customerForm";
        } catch (EntityNotFoundException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "error/404";
        }
    }

    /**
     * 고객 수정 처리
     */
    @PostMapping("/{customerId}/edit")
    public String customerEdit(@PathVariable("customerId") Long customerId,
                               @Valid CustomerFormDto customerFormDto,
                               BindingResult bindingResult,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "customer/customerForm";
        }

        try {
            customerFormDto.setId(customerId);
            Customer customer = customerService.updateCustomer(customerFormDto);
            redirectAttributes.addFlashAttribute("successMessage", "고객 정보가 성공적으로 수정되었습니다.");
            return "redirect:/admin/customers/" + customerId;
        } catch (IllegalStateException | EntityNotFoundException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "customer/customerForm";
        }
    }

    /**
     * 고객 삭제 처리
     */
    @PostMapping("/{customerId}/delete")
    public String customerDelete(@PathVariable("customerId") Long customerId,
                                 RedirectAttributes redirectAttributes) {
        try {
            customerService.deleteCustomer(customerId);
            redirectAttributes.addFlashAttribute("successMessage", "고객이 성공적으로 삭제되었습니다.");
        } catch (IllegalStateException | EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/customers";
    }
}
