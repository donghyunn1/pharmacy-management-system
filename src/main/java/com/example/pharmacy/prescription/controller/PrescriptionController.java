// PrescriptionController.java
package com.example.pharmacy.prescription.controller;

import com.example.pharmacy.customer.dto.CustomerDto;
import com.example.pharmacy.customer.service.CustomerService;
import com.example.pharmacy.medicine.dto.MedicineDto;
import com.example.pharmacy.medicine.service.MedicineService;
import com.example.pharmacy.prescription.dto.PrescriptionDto;
import com.example.pharmacy.prescription.dto.PrescriptionFormDto;
import com.example.pharmacy.prescription.entity.Prescription;
import com.example.pharmacy.prescription.service.PrescriptionService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/prescriptions")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;
    private final CustomerService customerService;
    private final MedicineService medicineService;

    /**
     * 처방전 관리 페이지 (목록)
     */
    @GetMapping
    public String prescriptionManagement(Model model) {
        List<PrescriptionDto> prescriptions = prescriptionService.getAllPrescriptions();
        model.addAttribute("prescriptions", prescriptions);
        return "prescription/prescriptionManagement";
    }

    /**
     * 처방전 등록 폼
     */
    @GetMapping("/new")
    public String prescriptionForm(Model model) {
        model.addAttribute("prescriptionFormDto", new PrescriptionFormDto());

        // 고객 목록 (회원만)
        List<CustomerDto> customers = customerService.getCustomersByMemberStatus(true);
        model.addAttribute("customers", customers);

        // 처방전 필요 약품만 조회
        List<MedicineDto> prescriptionMedicines = medicineService.getAllMedicines().stream()
                .filter(medicine -> medicine.getPrescriptionRequired())
                .collect(Collectors.toList());
        model.addAttribute("medicines", prescriptionMedicines);

        return "prescription/prescriptionForm";
    }

    /**
     * 처방전 등록 처리
     */
    @PostMapping("/new")
    public String prescriptionForm(@Valid PrescriptionFormDto prescriptionFormDto,
                                   BindingResult bindingResult,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            List<CustomerDto> customers = customerService.getCustomersByMemberStatus(true);
            model.addAttribute("customers", customers);

            List<MedicineDto> prescriptionMedicines = medicineService.getAllMedicines().stream()
                    .filter(medicine -> medicine.getPrescriptionRequired())
                    .collect(Collectors.toList());
            model.addAttribute("medicines", prescriptionMedicines);

            return "prescription/prescriptionForm";
        }

        try {
            // 처방 항목이 없는 경우 검증
            if (prescriptionFormDto.getPrescriptionItems() == null || prescriptionFormDto.getPrescriptionItems().isEmpty()) {
                throw new IllegalArgumentException("최소 하나 이상의 처방 항목이 필요합니다.");
            }

            Prescription prescription = prescriptionService.savePrescription(prescriptionFormDto);
            redirectAttributes.addFlashAttribute("successMessage", "처방전이 성공적으로 등록되었습니다.");
            return "redirect:/admin/prescriptions";
        } catch (Exception e) {
            List<CustomerDto> customers = customerService.getCustomersByMemberStatus(true);
            model.addAttribute("customers", customers);

            List<MedicineDto> prescriptionMedicines = medicineService.getAllMedicines().stream()
                    .filter(medicine -> medicine.getPrescriptionRequired())
                    .collect(Collectors.toList());
            model.addAttribute("medicines", prescriptionMedicines);

            model.addAttribute("errorMessage", "처방전 등록 중 오류가 발생했습니다: " + e.getMessage());
            return "prescription/prescriptionForm";
        }
    }

    /**
     * 처방전 상세 페이지
     */
    @GetMapping("/{prescriptionId}")
    public String prescriptionDetail(@PathVariable("prescriptionId") Long prescriptionId, Model model) {
        try {
            PrescriptionDto prescriptionDto = prescriptionService.getPrescriptionById(prescriptionId);
            model.addAttribute("prescription", prescriptionDto);
            return "prescription/prescriptionDetail";
        } catch (EntityNotFoundException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "error/404";
        }
    }

    /**
     * 처방전 삭제 처리
     */
    @PostMapping("/{prescriptionId}/delete")
    public String prescriptionDelete(@PathVariable("prescriptionId") Long prescriptionId,
                                     RedirectAttributes redirectAttributes) {
        try {
            prescriptionService.deletePrescription(prescriptionId);
            redirectAttributes.addFlashAttribute("successMessage", "처방전이 성공적으로 삭제되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/prescriptions";
    }

    /**
     * AJAX - 처방전 유효성 검증
     */
    @GetMapping("/{prescriptionId}/validate")
    @ResponseBody
    public boolean validatePrescription(@PathVariable("prescriptionId") Long prescriptionId,
                                        @RequestParam("medicineId") Long medicineId,
                                        @RequestParam("quantity") int quantity) {
        return prescriptionService.validatePrescription(prescriptionId, medicineId, quantity);
    }

    /**
     * AJAX - 유효한 처방전 목록 조회
     */
    @GetMapping("/valid")
    @ResponseBody
    public List<PrescriptionDto> getValidPrescriptions() {
        return prescriptionService.getValidPrescriptions();
    }
}