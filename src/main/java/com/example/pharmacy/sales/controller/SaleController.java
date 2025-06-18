// 양동현. 2025.06.18
package com.example.pharmacy.sales.controller;

import com.example.pharmacy.medicine.dto.MedicineDto;
import com.example.pharmacy.medicine.service.MedicineService;
import com.example.pharmacy.prescription.dto.PrescriptionDto;
import com.example.pharmacy.prescription.service.PrescriptionService;
import com.example.pharmacy.sales.dto.SaleDto;
import com.example.pharmacy.sales.dto.SaleFormDto;
import com.example.pharmacy.sales.entity.PaymentMethod;
import com.example.pharmacy.sales.entity.Sale;
import com.example.pharmacy.sales.service.SaleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/sales")
public class SaleController {

    private final SaleService saleService;
    private final MedicineService medicineService;
    private final PrescriptionService prescriptionService;

    /**
     * 판매 목록 페이지
     */
    @GetMapping
    public String salesList(Model model) {
        List<SaleDto> sales = saleService.getAllSales();
        model.addAttribute("sales", sales);
        return "sales/salesList";
    }

    /**
     * 판매 상세 페이지
     */
    @GetMapping("/{saleId}")
    public String saleDetail(@PathVariable("saleId") Long saleId, Model model) {
        try {
            SaleDto saleDto = saleService.getSaleById(saleId);
            model.addAttribute("sale", saleDto);
            return "sales/saleDetail";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "error/404";
        }
    }

    /**
     * 새 판매 등록 페이지
     */
    @GetMapping("/new")
    public String newSaleForm(Model model) {
        List<MedicineDto> medicines = medicineService.getAllMedicines();
        List<PrescriptionDto> validPrescriptions = prescriptionService.getValidPrescriptions();

        model.addAttribute("saleFormDto", new SaleFormDto());
        model.addAttribute("medicines", medicines);
        model.addAttribute("paymentMethods", PaymentMethod.values());
        model.addAttribute("validPrescriptions", validPrescriptions);

        return "sales/saleForm";
    }

    /**
     * 판매 등록 처리
     */
    @PostMapping("/new")
    public String createSale(@Valid @ModelAttribute("saleFormDto") SaleFormDto saleFormDto,
                             BindingResult bindingResult,
                             Model model,
                             RedirectAttributes redirectAttributes) {

        // 유효성 검사 실패 시
        if (bindingResult.hasErrors()) {
            List<MedicineDto> medicines = medicineService.getAllMedicines();
            List<PrescriptionDto> validPrescriptions = prescriptionService.getValidPrescriptions();

            model.addAttribute("medicines", medicines);
            model.addAttribute("paymentMethods", PaymentMethod.values());
            model.addAttribute("validPrescriptions", validPrescriptions);
            return "sales/saleForm";
        }

        try {
            // 판매 항목이 비어있는지 확인
            if (saleFormDto.getSaleItems() == null || saleFormDto.getSaleItems().isEmpty()) {
                throw new IllegalArgumentException("최소 하나 이상의 판매 항목이 필요합니다.");
            }

            Sale sale = saleService.createSale(saleFormDto);
            redirectAttributes.addFlashAttribute("successMessage", "판매가 성공적으로 등록되었습니다.");
            return "redirect:/sales/" + sale.getId();
        } catch (Exception e) {
            List<MedicineDto> medicines = medicineService.getAllMedicines();
            List<PrescriptionDto> validPrescriptions = prescriptionService.getValidPrescriptions();

            model.addAttribute("medicines", medicines);
            model.addAttribute("paymentMethods", PaymentMethod.values());
            model.addAttribute("validPrescriptions", validPrescriptions);
            model.addAttribute("errorMessage", "판매 등록 중 오류가 발생했습니다: " + e.getMessage());
            return "sales/saleForm";
        }
    }

    /**
     * 판매 취소 처리
     */
    @PostMapping("/{saleId}/cancel")
    public String cancelSale(@PathVariable("saleId") Long saleId, RedirectAttributes redirectAttributes) {
        try {
            saleService.cancelSale(saleId);
            redirectAttributes.addFlashAttribute("successMessage", "판매가 성공적으로 취소되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "판매 취소 중 오류가 발생했습니다: " + e.getMessage());
        }
        return "redirect:/sales/" + saleId;
    }

    /**
     * AJAX - 유효한 처방전 목록 조회
     */
    @GetMapping("/prescriptions/valid")
    @ResponseBody
    public List<PrescriptionDto> getValidPrescriptions() {
        return prescriptionService.getValidPrescriptions();
    }

    /**
     * AJAX - 처방전 유효성 검증
     */
    @GetMapping("/prescriptions/{prescriptionId}/validate")
    @ResponseBody
    public boolean validatePrescriptionForSale(@PathVariable("prescriptionId") Long prescriptionId,
                                               @RequestParam("medicineId") Long medicineId,
                                               @RequestParam("quantity") int quantity) {
        return prescriptionService.validatePrescription(prescriptionId, medicineId, quantity);
    }
}