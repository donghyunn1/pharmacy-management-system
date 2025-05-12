package com.example.pharmacy.medicine.controller;

import com.example.pharmacy.medicine.dto.MedicineDto;
import com.example.pharmacy.medicine.dto.MedicineFormDto;
import com.example.pharmacy.medicine.dto.MedicineSearchDto;
import com.example.pharmacy.medicine.entity.Medicine;
import com.example.pharmacy.medicine.entity.MedicineType;
import com.example.pharmacy.medicine.service.MedicineService;
import jakarta.persistence.EntityNotFoundException;
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
public class MedicineController {

    private final MedicineService medicineService;

    // 약품 관리자 페이지
    @GetMapping("/admin/medicines")
    public String medicineManagement(MedicineSearchDto medicineSearchDto, Model model) {
        List<MedicineDto> medicines;

        // 검색 조건이 모두 비어있는 경우 전체 약품 목록 표시
        if ((medicineSearchDto.getSearchQuery() == null || medicineSearchDto.getSearchQuery().isEmpty()) &&
                medicineSearchDto.getMedicineType() == null &&
                medicineSearchDto.getPrescriptionRequired() == null) {
            medicines = medicineService.getAllMedicines();
        } else {
            medicines = null; // todo: 나중에 검색처리기능 구현해야함
        }

        model.addAttribute("medicines", medicines);
        model.addAttribute("medicineSearchDto", medicineSearchDto);
        model.addAttribute("medicineTypes", MedicineType.values());

        return "medicine/medicineManagement";
    }


    // 약품 등록 폼
    @GetMapping("/admin/medicines/new")
    public String medicineForm(Model model) {
        model.addAttribute("medicineFormDto", new MedicineFormDto());
        model.addAttribute("medicineTypes", MedicineType.values());
        return "medicine/medicineForm";
    }

    // 약품 등록 처리
    @PostMapping("/admin/medicines/new")
    public String medicineForm(@Valid MedicineFormDto medicineFormDto,
                               BindingResult bindingResult,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("medicineTypes", MedicineType.values());
            return "medicine/medicineForm";
        }

        try {
            Medicine medicine = medicineService.saveMedicine(medicineFormDto);
            redirectAttributes.addFlashAttribute("successMessage", "약품이 성공적으로 등록되었습니다.");
            return "redirect:/admin/medicines";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "약품 등록 중 오류가 발생했습니다: " + e.getMessage());
            model.addAttribute("medicineTypes", MedicineType.values());
            return "medicine/medicineForm";
        }
    }

    // 약품 상세 페이지
    @GetMapping({"/admin/medicines/{medicineId}", "/medicines/{medicineId}"})
    public String medicineDetail(@PathVariable("medicineId") Long medicineId, Model model) {
        try {
            MedicineDto medicineDto = medicineService.getMedicineById(medicineId);
            model.addAttribute("medicine", medicineDto);
            return "medicine/medicineDetail";
        } catch (EntityNotFoundException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "error/404";
        }
    }

    // 약품 수정 폼
    @GetMapping("/admin/medicines/{medicineId}/edit")
    public String medicineEditForm(@PathVariable("medicineId") Long medicineId, Model model) {
        try {
            MedicineFormDto medicineFormDto = medicineService.getMedicineFormDto(medicineId);
            model.addAttribute("medicineFormDto", medicineFormDto);
            model.addAttribute("medicineTypes", MedicineType.values());
            return "medicine/medicineForm";
        } catch (EntityNotFoundException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "error/404";
        }
    }

    // 약품 수정 처리
    @PostMapping("/admin/medicines/{medicineId}/edit")
    public String medicineEdit(@PathVariable("medicineId") Long medicineId,
                               @Valid MedicineFormDto medicineFormDto,
                               BindingResult bindingResult,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("medicineTypes", MedicineType.values());
            return "medicine/medicineForm";
        }

        try {
            medicineFormDto.setId(medicineId);
            Medicine medicine = medicineService.updateMedicine(medicineFormDto);
            redirectAttributes.addFlashAttribute("successMessage", "약품이 성공적으로 수정되었습니다.");
            return "redirect:/admin/medicines";
        } catch (IllegalStateException | EntityNotFoundException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("medicineTypes", MedicineType.values());
            return "medicine/medicineForm";
        }
    }

    // 약품 삭제
    @PostMapping("/admin/medicines/{medicineId}/delete")
    public String medicineDelete(@PathVariable("medicineId") Long medicineId,
                                 RedirectAttributes redirectAttributes) {
        try {
            medicineService.deleteMedicine(medicineId);
            redirectAttributes.addFlashAttribute("successMessage", "약품이 성공적으로 삭제되었습니다.");
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/medicines";
    }
}