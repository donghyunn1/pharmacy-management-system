package com.example.pharmacy.inventory.controller;

import com.example.pharmacy.inventory.dto.InventoryDto;
import com.example.pharmacy.inventory.dto.InventoryFormDto;
import com.example.pharmacy.inventory.entity.MedicineInventory;
import com.example.pharmacy.inventory.service.InventoryService;
import com.example.pharmacy.medicine.dto.MedicineDto;
import com.example.pharmacy.medicine.service.MedicineService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;
    private final MedicineService medicineService;

    // 재고 목록 페이지
    @GetMapping("/admin/inventory")
    public String inventoryList(Model model) {
        List<InventoryDto> inventories = inventoryService.getAllInventories();
        model.addAttribute("inventories", inventories);
        return "inventory/inventoryList";
    }

    // 입고 등록 페이지
    @GetMapping("/admin/inventory/new")
    public String inventoryForm(Model model) {
        model.addAttribute("inventoryFormDto", new InventoryFormDto());
        model.addAttribute("medicines", medicineService.getAllMedicines());
        model.addAttribute("today", LocalDate.now());
        return "inventory/inventoryForm";
    }

    // 입고 등록 처리
    @PostMapping("/admin/inventory/new")
    public String inventoryNew(@Valid InventoryFormDto inventoryFormDto,
                               BindingResult bindingResult,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("medicines", medicineService.getAllMedicines());
            model.addAttribute("today", LocalDate.now());
            return "inventory/inventoryForm";
        }

        try {
            MedicineInventory inventory = inventoryService.saveInventory(inventoryFormDto);
            redirectAttributes.addFlashAttribute("successMessage", "입고가 성공적으로 등록되었습니다.");
            return "redirect:/admin/inventory";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "입고 등록 중 오류가 발생했습니다: " + e.getMessage());
            model.addAttribute("medicines", medicineService.getAllMedicines());
            model.addAttribute("today", LocalDate.now());
            return "inventory/inventoryForm";
        }
    }

    // 입고 수정 페이지
    @GetMapping("/admin/inventory/{id}/edit")
    public String inventoryEditForm(@PathVariable("id") Long id, Model model) {
        try {
            InventoryFormDto inventoryFormDto = inventoryService.getInventoryFormDto(id);
            model.addAttribute("inventoryFormDto", inventoryFormDto);
            model.addAttribute("medicines", medicineService.getAllMedicines());
            model.addAttribute("today", LocalDate.now());

            // 일단 서비스에 getInventoryFormDto 메소드가 없으므로 에러 메시지 표시
            model.addAttribute("errorMessage", "현재 입고 수정 기능은 구현되지 않았습니다.");
            return "error";
        } catch (EntityNotFoundException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "error";
        }
    }

    // 입고 수정 처리
    @PostMapping("/admin/inventory/{id}/edit")
    public String inventoryEdit(@PathVariable("id") Long id,
                                @Valid InventoryFormDto inventoryFormDto,
                                BindingResult bindingResult,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("medicines", medicineService.getAllMedicines());
            model.addAttribute("today", LocalDate.now());
            return "inventory/inventoryForm";
        }

        try {
            inventoryFormDto.setId(id);
            MedicineInventory inventory = inventoryService.updateInventory(inventoryFormDto);
            redirectAttributes.addFlashAttribute("successMessage", "입고가 성공적으로 수정되었습니다.");
            return "redirect:/admin/inventory";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "입고 수정 중 오류가 발생했습니다: " + e.getMessage());
            model.addAttribute("medicines", medicineService.getAllMedicines());
            model.addAttribute("today", LocalDate.now());
            return "inventory/inventoryForm";
        }
    }

    // 입고 삭제 처리
    @PostMapping("/admin/inventory/{id}/delete")
    public String inventoryDelete(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            inventoryService.deleteInventory(id);
            redirectAttributes.addFlashAttribute("successMessage", "입고가 성공적으로 삭제되었습니다.");
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/inventory";
    }

    // 특정 약품의 재고 목록 조회
    @GetMapping("/admin/inventory/medicine/{medicineId}")
    public String medicineInventory(@PathVariable("medicineId") Long medicineId, Model model) {
        try {
            MedicineDto medicineDto = medicineService.getMedicineById(medicineId);
            List<InventoryDto> availableInventories = inventoryService.getAvailableInventoriesByMedicineId(medicineId);
            Integer totalStock = inventoryService.getTotalStockByMedicineId(medicineId);
            boolean hasWarning = inventoryService.hasWarningForMedicine(medicineId);

            model.addAttribute("medicine", medicineDto);
            model.addAttribute("inventories", availableInventories);
            model.addAttribute("totalStock", totalStock != null ? totalStock : 0);
            model.addAttribute("hasWarning", hasWarning);

            return "inventory/medicineInventory";
        } catch (EntityNotFoundException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "error";
        }
    }
}
