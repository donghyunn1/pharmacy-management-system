// 양동현. 2025.06.18
package com.example.pharmacy.sales.service;

import com.example.pharmacy.inventory.entity.MedicineInventory;
import com.example.pharmacy.inventory.repository.InventoryRepository;
import com.example.pharmacy.medicine.entity.Medicine;
import com.example.pharmacy.medicine.repository.MedicineRepository;
import com.example.pharmacy.member.entity.Member;
import com.example.pharmacy.member.repository.MemberRepository;
import com.example.pharmacy.prescription.repository.PrescriptionItemRepository;
import com.example.pharmacy.prescription.service.PrescriptionService;
import com.example.pharmacy.sales.dto.SaleDto;
import com.example.pharmacy.sales.dto.SaleFormDto;
import com.example.pharmacy.sales.dto.SaleItemDto;
import com.example.pharmacy.sales.dto.SaleItemFormDto;
import com.example.pharmacy.sales.entity.Sale;
import com.example.pharmacy.sales.entity.SaleItem;
import com.example.pharmacy.sales.entity.SaleStatus;
import com.example.pharmacy.sales.repository.SaleItemRepository;
import com.example.pharmacy.sales.repository.SaleRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class SaleService {

    private final SaleRepository saleRepository;
    private final SaleItemRepository saleItemRepository;
    private final MedicineRepository medicineRepository;
    private final InventoryRepository inventoryRepository;
    private final MemberRepository memberRepository;
    private final PrescriptionService prescriptionService;
    private final PrescriptionItemRepository prescriptionItemRepository;

    /**
     * 판매 등록
     * - FIFO 원칙에 따라 재고 소진
     * - 처방전 필요 약품은 처방전 ID 필수
     */
    public Sale createSale(SaleFormDto saleFormDto) {
        // 현재 로그인한 회원 정보 가져오기
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Member member = memberRepository.findByUsername(auth.getName());
        if (member == null) {
            throw new IllegalStateException("로그인 정보를 찾을 수 없습니다.");
        }

        // 처방전 필요 약품이 있는지 확인
        boolean hasPrescriptionMedicine = saleFormDto.getSaleItems().stream()
                .anyMatch(item -> {
                    Medicine medicine = medicineRepository.findById(item.getMedicineId())
                            .orElseThrow(() -> new EntityNotFoundException("약품을 찾을 수 없습니다."));
                    return medicine.getPrescriptionRequired();
                });

        // 처방전 필요 약품이 있는데 처방전 ID가 없는 경우
        if (hasPrescriptionMedicine && saleFormDto.getPrescriptionId() == null) {
            throw new IllegalStateException("처방전이 필요한 약품이 포함되어 있습니다. 처방전을 선택해주세요.");
        }

        // 새 판매 객체 생성
        Sale sale = new Sale();
        sale.setCustomerId(saleFormDto.getCustomerId());
        sale.setPrescriptionId(saleFormDto.getPrescriptionId());
        sale.setMember(member);
        sale.setSaleDate(LocalDateTime.now());
        sale.setPaymentMethod(saleFormDto.getPaymentMethod());
        sale.setStatus(SaleStatus.completed);

        // 판매 항목 처리
        for (SaleItemFormDto itemFormDto : saleFormDto.getSaleItems()) {
            processSaleItem(sale, itemFormDto);
        }

        // 총액 계산
        sale.calculateTotalAmount();

        // 판매 저장
        return saleRepository.save(sale);
    }

    /**
     * 판매 항목 처리 (FIFO 원칙 적용 + 처방전 검증)
     */
    private void processSaleItem(Sale sale, SaleItemFormDto itemFormDto) {
        Medicine medicine = medicineRepository.findById(itemFormDto.getMedicineId())
                .orElseThrow(() -> new EntityNotFoundException("약품을 찾을 수 없습니다: " + itemFormDto.getMedicineId()));

        // 처방전 필요 약품인 경우 처방전 검증
        if (medicine.getPrescriptionRequired()) {
            if (sale.getPrescriptionId() == null) {
                throw new IllegalStateException(medicine.getMedicineName() + "은(는) 처방전이 필요한 약품입니다.");
            }

            // 처방전 유효성 및 수량 검증
            if (!prescriptionService.validatePrescription(sale.getPrescriptionId(), medicine.getId(), itemFormDto.getQuantity())) {
                throw new IllegalStateException(medicine.getMedicineName() + "의 처방전이 유효하지 않거나 수량이 부족합니다.");
            }
        }

        // 판매할 총 수량
        int remainingQuantity = itemFormDto.getQuantity();

        // FIFO 원칙에 따라 재고 소진 (먼저 들어온 재고부터 소진)
        List<MedicineInventory> availableInventories =
                inventoryRepository.findAvailableInventoryByMedicineIdOrderByPurchaseDate(medicine.getId());

        if (availableInventories.isEmpty()) {
            throw new IllegalStateException(medicine.getMedicineName() + "의 재고가 없습니다.");
        }

        // 총 가용 재고량 계산 (만료되지 않은 것만)
        int totalAvailableStock = availableInventories.stream()
                .filter(inventory -> !inventory.isExpired())
                .mapToInt(MedicineInventory::getStockQuantity)
                .sum();

        // 재고가 부족한 경우 확인
        if (totalAvailableStock < remainingQuantity) {
            throw new IllegalStateException(medicine.getMedicineName() + "의 재고가 부족합니다. 현재 재고: "
                    + totalAvailableStock + ", 요청 수량: " + remainingQuantity);
        }

        // FIFO 원칙에 따라 순차적으로 재고 소진
        for (MedicineInventory inventory : availableInventories) {
            if (remainingQuantity <= 0) break;
            if (inventory.isExpired()) continue; // 만료된 재고는 건너뛰기

            int quantityFromThisInventory = Math.min(remainingQuantity, inventory.getStockQuantity());

            // 판매 항목 생성
            SaleItem saleItem = new SaleItem();
            saleItem.setSale(sale);
            saleItem.setInventory(inventory);
            saleItem.setMedicine(medicine);
            saleItem.setQuantity(quantityFromThisInventory);
            saleItem.setUnitPrice(inventory.getPrice());
            saleItem.setPrescriptionItemId(itemFormDto.getPrescriptionItemId());
            saleItem.calculateTotalPrice();

            // 재고 차감
            inventory.setStockQuantity(inventory.getStockQuantity() - quantityFromThisInventory);
            inventoryRepository.save(inventory);

            // 판매에 항목 추가
            sale.addSaleItem(saleItem);

            // 남은 수량 업데이트
            remainingQuantity -= quantityFromThisInventory;
        }

        // 처방전 필요 약품인 경우 처방전 수량 차감
        if (medicine.getPrescriptionRequired() && itemFormDto.getPrescriptionItemId() != null) {
            prescriptionService.consumePrescriptionItem(itemFormDto.getPrescriptionItemId(), itemFormDto.getQuantity());
        }
    }

    /**
     * 판매 취소
     * - 재고 복원
     * - 처방전 수량 복원
     */
    public void cancelSale(Long saleId) {
        Sale sale = saleRepository.findById(saleId)
                .orElseThrow(() -> new EntityNotFoundException("판매 정보를 찾을 수 없습니다: " + saleId));

        // 이미 취소된 판매인지 확인
        if (sale.getStatus() == SaleStatus.cancelled) {
            throw new IllegalStateException("이미 취소된 판매입니다.");
        }

        // 판매 상태 변경
        sale.cancel();

        // 재고 복원 및 처방전 수량 복원
        for (SaleItem saleItem : sale.getSaleItems()) {
            // 재고 복원
            MedicineInventory inventory = saleItem.getInventory();
            inventory.setStockQuantity(inventory.getStockQuantity() + saleItem.getQuantity());
            inventoryRepository.save(inventory);

            // 처방전 수량 복원 (처방전 항목이 있는 경우)
            if (saleItem.getPrescriptionItemId() != null) {
                prescriptionService.restorePrescriptionItem(saleItem.getPrescriptionItemId(), saleItem.getQuantity());
            }
        }

        saleRepository.save(sale);
    }

    /**
     * 판매 조회
     */
    @Transactional(readOnly = true)
    public SaleDto getSaleById(Long saleId) {
        Sale sale = saleRepository.findById(saleId)
                .orElseThrow(() -> new EntityNotFoundException("판매 정보를 찾을 수 없습니다: " + saleId));

        return convertToSaleDto(sale);
    }

    /**
     * 모든 판매 내역 조회
     */
    @Transactional(readOnly = true)
    public List<SaleDto> getAllSales() {
        List<Sale> sales = saleRepository.findAll();
        return sales.stream()
                .map(this::convertToSaleDto)
                .collect(Collectors.toList());
    }

    /**
     * 특정 고객의 판매 내역 조회
     */
    @Transactional(readOnly = true)
    public List<SaleDto> getSalesByCustomerId(Long customerId) {
        List<Sale> sales = saleRepository.findByCustomerIdOrderBySaleDateDesc(customerId);
        return sales.stream()
                .map(this::convertToSaleDto)
                .collect(Collectors.toList());
    }

    /**
     * Entity -> DTO 변환
     */
    private SaleDto convertToSaleDto(Sale sale) {
        SaleDto saleDto = new SaleDto();
        saleDto.setId(sale.getId());
        saleDto.setCustomerId(sale.getCustomerId());
        saleDto.setPrescriptionId(sale.getPrescriptionId());
        saleDto.setMemberName(sale.getMember().getName());
        saleDto.setSaleDate(sale.getSaleDate());
        saleDto.setTotalAmount(sale.getTotalAmount());
        saleDto.setPaymentMethod(sale.getPaymentMethod());
        saleDto.setPaymentMethodDisplay(sale.getPaymentMethod().getDisplayName());
        saleDto.setStatus(sale.getStatus());
        saleDto.setStatusDisplay(sale.getStatus().getDisplayName());

        // 판매 항목 변환
        List<SaleItemDto> saleItemDtos = new ArrayList<>();
        for (SaleItem saleItem : sale.getSaleItems()) {
            SaleItemDto saleItemDto = new SaleItemDto();
            saleItemDto.setId(saleItem.getId());
            saleItemDto.setMedicineId(saleItem.getMedicine().getId());
            saleItemDto.setMedicineName(saleItem.getMedicine().getMedicineName());
            saleItemDto.setMedicineType(saleItem.getMedicine().getMedicineType().getDisplayName());
            saleItemDto.setQuantity(saleItem.getQuantity());
            saleItemDto.setUnitPrice(saleItem.getUnitPrice());
            saleItemDto.setTotalPrice(saleItem.getTotalPrice());
            saleItemDto.setIsPrescriptionRequired(saleItem.getMedicine().getPrescriptionRequired());
            saleItemDto.setPrescriptionItemId(saleItem.getPrescriptionItemId());

            saleItemDtos.add(saleItemDto);
        }
        saleDto.setSaleItems(saleItemDtos);

        return saleDto;
    }
}