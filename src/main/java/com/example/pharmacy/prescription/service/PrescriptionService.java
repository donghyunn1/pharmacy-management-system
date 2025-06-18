// 양동현. 2025.06.18
package com.example.pharmacy.prescription.service;

import com.example.pharmacy.customer.entity.Customer;
import com.example.pharmacy.customer.repository.CustomerRepository;
import com.example.pharmacy.medicine.entity.Medicine;
import com.example.pharmacy.medicine.repository.MedicineRepository;
import com.example.pharmacy.prescription.dto.PrescriptionDto;
import com.example.pharmacy.prescription.dto.PrescriptionFormDto;
import com.example.pharmacy.prescription.dto.PrescriptionItemFormDto;
import com.example.pharmacy.prescription.entity.Prescription;
import com.example.pharmacy.prescription.entity.PrescriptionItem;
import com.example.pharmacy.prescription.entity.PrescriptionStatus;
import com.example.pharmacy.prescription.repository.PrescriptionItemRepository;
import com.example.pharmacy.prescription.repository.PrescriptionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final PrescriptionItemRepository prescriptionItemRepository;
    private final CustomerRepository customerRepository;
    private final MedicineRepository medicineRepository;

    /**
     * 처방전 등록
     */
    public Prescription savePrescription(PrescriptionFormDto prescriptionFormDto) {
        // 고객 조회
        Customer customer = customerRepository.findById(prescriptionFormDto.getCustomerId())
                .orElseThrow(() -> new EntityNotFoundException("고객을 찾을 수 없습니다."));

        // 처방전 생성
        Prescription prescription = new Prescription();
        prescription.setCustomer(customer);
        prescription.setDoctorName(prescriptionFormDto.getDoctorName());
        prescription.setHospitalName(prescriptionFormDto.getHospitalName());
        prescription.setIssueDate(prescriptionFormDto.getIssueDate());
        prescription.setExpiryDate(prescriptionFormDto.getExpiryDate());
        prescription.setStatus(PrescriptionStatus.active);

        // 처방 항목 추가
        for (PrescriptionItemFormDto itemDto : prescriptionFormDto.getPrescriptionItems()) {
            Medicine medicine = medicineRepository.findById(itemDto.getMedicineId())
                    .orElseThrow(() -> new EntityNotFoundException("약품을 찾을 수 없습니다."));

            // 처방전 필요 약품인지 확인
            if (!medicine.getPrescriptionRequired()) {
                throw new IllegalArgumentException(medicine.getMedicineName() + "은(는) 처방전이 필요하지 않은 약품입니다.");
            }

            PrescriptionItem item = new PrescriptionItem();
            item.setMedicine(medicine);
            item.setPrescribedQuantity(itemDto.getPrescribedQuantity());
            item.setRemainingQuantity(itemDto.getPrescribedQuantity());

            prescription.addPrescriptionItem(item);
        }

        return prescriptionRepository.save(prescription);
    }


    /**
     * 처방전 삭제
     */
    public void deletePrescription(Long prescriptionId) {
        Prescription prescription = prescriptionRepository.findById(prescriptionId)
                .orElseThrow(() -> new EntityNotFoundException("처방전을 찾을 수 없습니다."));

        // 사용된 처방전은 삭제 불가
        if (prescription.getStatus() == PrescriptionStatus.partially_used || prescription.getStatus() == PrescriptionStatus.completed) {
            throw new IllegalStateException("이미 사용된 처방전은 삭제할 수 없습니다.");
        }

        prescriptionRepository.delete(prescription);
    }

    /**
     * 처방전 조회 (단건)
     */
    @Transactional(readOnly = true)
    public PrescriptionDto getPrescriptionById(Long prescriptionId) {
        Prescription prescription = prescriptionRepository.findById(prescriptionId)
                .orElseThrow(() -> new EntityNotFoundException("처방전을 찾을 수 없습니다."));

        return PrescriptionDto.of(prescription);
    }

    /**
     * 모든 처방전 조회
     */
    @Transactional(readOnly = true)
    public List<PrescriptionDto> getAllPrescriptions() {
        List<Prescription> prescriptions = prescriptionRepository.findAll();
        return prescriptions.stream()
                .map(PrescriptionDto::of)
                .collect(Collectors.toList());
    }

    /**
     * 유효한 처방전 조회
     */
    @Transactional(readOnly = true)
    public List<PrescriptionDto> getValidPrescriptions() {
        List<Prescription> prescriptions = prescriptionRepository.findValidPrescriptions();
        return prescriptions.stream()
                .map(PrescriptionDto::of)
                .collect(Collectors.toList());
    }

    /**
     * 처방전 항목 수량 소진 처리 (판매 시 호출)
     */
    public void consumePrescriptionItem(Long prescriptionItemId, int quantity) {
        PrescriptionItem item = prescriptionItemRepository.findById(prescriptionItemId)
                .orElseThrow(() -> new EntityNotFoundException("처방전 항목을 찾을 수 없습니다."));

        item.consumeQuantity(quantity);
        prescriptionItemRepository.save(item);

        // 처방전 상태 업데이트
        Prescription prescription = item.getPrescription();
        prescription.updateStatus();
        prescriptionRepository.save(prescription);
    }

    /**
     * 처방전 항목 수량 복원 (판매 취소 시 호출)
     */
    public void restorePrescriptionItem(Long prescriptionItemId, int quantity) {
        PrescriptionItem item = prescriptionItemRepository.findById(prescriptionItemId)
                .orElseThrow(() -> new EntityNotFoundException("처방전 항목을 찾을 수 없습니다."));

        item.restoreQuantity(quantity);
        prescriptionItemRepository.save(item);

        // 처방전 상태 업데이트
        Prescription prescription = item.getPrescription();
        prescription.updateStatus();
        prescriptionRepository.save(prescription);
    }

    /**
     * 처방전 유효성 검증
     */
    @Transactional(readOnly = true)
    public boolean validatePrescription(Long prescriptionId, Long medicineId, int requestedQuantity) {
        Prescription prescription = prescriptionRepository.findById(prescriptionId)
                .orElseThrow(() -> new EntityNotFoundException("처방전을 찾을 수 없습니다."));

        // 처방전 유효성 확인
        if (!prescription.isValid()) {
            return false;
        }

        // 해당 약품이 처방전에 있는지 확인
        PrescriptionItem item = prescriptionItemRepository.findByPrescriptionIdAndMedicineId(prescriptionId, medicineId);
        if (item == null) {
            return false;
        }

        // 남은 수량 확인
        return item.getRemainingQuantity() >= requestedQuantity;
    }
}