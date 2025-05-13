package com.example.pharmacy.inventory.service;

import com.example.pharmacy.inventory.dto.InventoryDto;
import com.example.pharmacy.inventory.dto.InventoryFormDto;
import com.example.pharmacy.inventory.entity.MedicineInventory;
import com.example.pharmacy.inventory.repository.InventoryRepository;
import com.example.pharmacy.medicine.entity.Medicine;
import com.example.pharmacy.medicine.repository.MedicineRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final MedicineRepository medicineRepository;

    // 입고 등록
    public MedicineInventory saveInventory(InventoryFormDto inventoryFormDto) {
        Medicine medicine = medicineRepository.findById(inventoryFormDto.getMedicineId())
                .orElseThrow(() -> new EntityNotFoundException("약품을 찾을 수 없습니다: " + inventoryFormDto.getMedicineId()));

        MedicineInventory inventory = new MedicineInventory();
        inventory.setMedicine(medicine);
        inventory.setBatchNumber(inventoryFormDto.getBatchNumber());
        inventory.setPurchaseDate(inventoryFormDto.getPurchaseDate());
        inventory.setExpirationDate(inventoryFormDto.getExpirationDate());
        inventory.setPrice(inventoryFormDto.getPrice());
        inventory.setStockQuantity(inventoryFormDto.getStockQuantity());
        inventory.setSupplierName(inventoryFormDto.getSupplierName());

        return inventoryRepository.save(inventory);
    }

    // 입고 정보 조회 (단건)
    @Transactional(readOnly = true)
    public InventoryFormDto getInventoryFormDto(Long inventoryId) {
        MedicineInventory inventory = inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new EntityNotFoundException("입고 내역을 찾을 수 없습니다: " + inventoryId));

        InventoryFormDto inventoryFormDto = new InventoryFormDto();
        inventoryFormDto.setId(inventory.getId());
        inventoryFormDto.setMedicineId(inventory.getMedicine().getId());
        inventoryFormDto.setMedicineName(inventory.getMedicine().getMedicineName());
        inventoryFormDto.setBatchNumber(inventory.getBatchNumber());
        inventoryFormDto.setPurchaseDate(inventory.getPurchaseDate());
        inventoryFormDto.setExpirationDate(inventory.getExpirationDate());
        inventoryFormDto.setPrice(inventory.getPrice());
        inventoryFormDto.setStockQuantity(inventory.getStockQuantity());
        inventoryFormDto.setSupplierName(inventory.getSupplierName());

        return inventoryFormDto;
    }

    // 입고 수정
    public MedicineInventory updateInventory(InventoryFormDto inventoryFormDto) {
        MedicineInventory inventory = inventoryRepository.findById(inventoryFormDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("입고 내역을 찾을 수 없습니다: " + inventoryFormDto.getId()));

        // 약품 ID가 변경된 경우 새 약품 객체 설정
        if (!inventory.getMedicine().getId().equals(inventoryFormDto.getMedicineId())) {
            Medicine medicine = medicineRepository.findById(inventoryFormDto.getMedicineId())
                    .orElseThrow(() -> new EntityNotFoundException("약품을 찾을 수 없습니다: " + inventoryFormDto.getMedicineId()));
            inventory.setMedicine(medicine);
        }

        inventory.setBatchNumber(inventoryFormDto.getBatchNumber());
        inventory.setPurchaseDate(inventoryFormDto.getPurchaseDate());
        inventory.setExpirationDate(inventoryFormDto.getExpirationDate());
        inventory.setPrice(inventoryFormDto.getPrice());
        inventory.setStockQuantity(inventoryFormDto.getStockQuantity());
        inventory.setSupplierName(inventoryFormDto.getSupplierName());

        return inventoryRepository.save(inventory);
    }

    // 입고 삭제
    public void deleteInventory(Long inventoryId) {
        MedicineInventory inventory = inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new EntityNotFoundException("입고 내역을 찾을 수 없습니다: " + inventoryId));

        inventoryRepository.delete(inventory);
    }

    // 모든 재고 목록 조회
    @Transactional(readOnly = true)
    public List<InventoryDto> getAllInventories() {
        List<MedicineInventory> inventories = inventoryRepository.findAll();
        return convertToInventoryDtoList(inventories);
    }

    // 특정 약품의 판매 가능한 재고 조회 (FIFO 원칙에 따름)
    @Transactional(readOnly = true)
    public List<InventoryDto> getAvailableInventoriesByMedicineId(Long medicineId) {
        List<MedicineInventory> inventories =
                inventoryRepository.findAvailableInventoryByMedicineIdOrderByPurchaseDate(medicineId);
        return convertToInventoryDtoList(inventories);
    }

    // 약품의 총 재고량 계산
    @Transactional(readOnly = true)
    public Integer getTotalStockByMedicineId(Long medicineId) {
        return inventoryRepository.getTotalValidStockByMedicineId(medicineId);
    }

    // 재고 관리 경고 여부 확인 (최소 재고량 미달 또는 유통기한 임박)
    @Transactional(readOnly = true)
    public boolean hasWarningForMedicine(Long medicineId) {
        Medicine medicine = medicineRepository.findById(medicineId)
                .orElseThrow(() -> new EntityNotFoundException("약품을 찾을 수 없습니다: " + medicineId));

        Integer totalStock = inventoryRepository.getTotalValidStockByMedicineId(medicineId);
        if (totalStock == null) totalStock = 0;

        // 최소 재고량 미달 확인
        if (totalStock < medicine.getMinimumStock()) {
            return true;
        }

        // 유통기한 임박 확인
        LocalDate thirtyDaysFromNow = LocalDate.now().plusDays(30);
        List<MedicineInventory> expiringItems =
                inventoryRepository.findByMedicineAndExpirationDateBetweenAndStockQuantityGreaterThan(
                        medicine, LocalDate.now(), thirtyDaysFromNow, 0);

        return !expiringItems.isEmpty();
    }


    // Entity → DTO 변환 메서드
    private InventoryDto convertToInventoryDto(MedicineInventory inventory) {
        InventoryDto dto = new InventoryDto();
        dto.setId(inventory.getId());
        dto.setMedicineId(inventory.getMedicine().getId());
        dto.setMedicineName(inventory.getMedicine().getMedicineName());
        dto.setMedicineType(inventory.getMedicine().getMedicineType().getDisplayName());
        dto.setBatchNumber(inventory.getBatchNumber());
        dto.setPurchaseDate(inventory.getPurchaseDate());
        dto.setExpirationDate(inventory.getExpirationDate());
        dto.setPrice(inventory.getPrice());
        dto.setStockQuantity(inventory.getStockQuantity());
        dto.setSupplierName(inventory.getSupplierName());
        dto.setMinimumStock(inventory.getMedicine().getMinimumStock());

        // 경고 상태 설정
        dto.setExpirationImminent(inventory.isExpirationImminent());
        dto.setExpired(inventory.isExpired());

        Integer totalStock = inventoryRepository.getTotalValidStockByMedicineId(inventory.getMedicine().getId());
        if (totalStock == null) totalStock = 0;
        dto.setLowStock(totalStock < inventory.getMedicine().getMinimumStock());

        return dto;
    }

    private List<InventoryDto> convertToInventoryDtoList(List<MedicineInventory> inventories) {
        return inventories.stream()
                .map(this::convertToInventoryDto)
                .collect(Collectors.toList());
    }
}
