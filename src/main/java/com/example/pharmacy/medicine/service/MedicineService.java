package com.example.pharmacy.medicine.service;

import com.example.pharmacy.medicine.dto.MedicineDto;
import com.example.pharmacy.medicine.dto.MedicineFormDto;
import com.example.pharmacy.medicine.dto.MedicineSearchDto;
import com.example.pharmacy.medicine.entity.Medicine;
import com.example.pharmacy.medicine.repository.MedicineRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class MedicineService {

    private final MedicineRepository medicineRepository;

    // 약품 등록
    public Medicine saveMedicine(MedicineFormDto medicineFormDto) {
        Medicine medicine = new Medicine();
        medicine.setMedicineName(medicineFormDto.getMedicineName());
        medicine.setManufacturerName(medicineFormDto.getManufacturerName());
        medicine.setMedicineType(medicineFormDto.getMedicineType());
        medicine.setDescription(medicineFormDto.getDescription());
        medicine.setEffects(medicineFormDto.getEffects());
        medicine.setPrescriptionRequired(medicineFormDto.getPrescriptionRequired());
        medicine.setMinimumStock(medicineFormDto.getMinimumStock());

        return medicineRepository.save(medicine);
    }

    // 약품 수정
    public Medicine updateMedicine(MedicineFormDto medicineFormDto) {
        Medicine medicine = medicineRepository.findById(medicineFormDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("약품을 찾을 수 없습니다."));

        medicine.setMedicineName(medicineFormDto.getMedicineName());
        medicine.setManufacturerName(medicineFormDto.getManufacturerName());
        medicine.setMedicineType(medicineFormDto.getMedicineType());
        medicine.setDescription(medicineFormDto.getDescription());
        medicine.setEffects(medicineFormDto.getEffects());
        medicine.setPrescriptionRequired(medicineFormDto.getPrescriptionRequired());
        medicine.setMinimumStock(medicineFormDto.getMinimumStock());

        return medicineRepository.save(medicine);
    }

    // 약품 삭제
    public void deleteMedicine(Long medicineId) {
        Medicine medicine = medicineRepository.findById(medicineId)
                .orElseThrow(() -> new EntityNotFoundException("약품을 찾을 수 없습니다."));

        // 실제 삭제 전에 관련 재고나 판매 데이터 확인 필요할 수 있음
        medicineRepository.delete(medicine);
    }

    // 약품 조회
    @Transactional(readOnly = true)
    public MedicineDto getMedicineById(Long medicineId) {
        Medicine medicine = medicineRepository.findById(medicineId)
                .orElseThrow(() -> new EntityNotFoundException("약품을 찾을 수 없습니다."));

        return MedicineDto.of(medicine);
    }

    // 약품 수정을 위한 정보 로드
    @Transactional(readOnly = true)
    public MedicineFormDto getMedicineFormDto(Long medicineId) {
        Medicine medicine = medicineRepository.findById(medicineId)
                .orElseThrow(() -> new EntityNotFoundException("약품을 찾을 수 없습니다."));

        MedicineFormDto medicineFormDto = new MedicineFormDto();
        medicineFormDto.setId(medicine.getId());
        medicineFormDto.setMedicineName(medicine.getMedicineName());
        medicineFormDto.setManufacturerName(medicine.getManufacturerName());
        medicineFormDto.setMedicineType(medicine.getMedicineType());
        medicineFormDto.setDescription(medicine.getDescription());
        medicineFormDto.setEffects(medicine.getEffects());
        medicineFormDto.setPrescriptionRequired(medicine.getPrescriptionRequired());
        medicineFormDto.setMinimumStock(medicine.getMinimumStock());

        return medicineFormDto;
    }

    // 모든 약품 조회
    @Transactional(readOnly = true)
    public List<MedicineDto> getAllMedicines() {
        List<Medicine> medicines = medicineRepository.findAll();
        List<MedicineDto> medicineDtoList = new ArrayList<>();

        for (Medicine medicine : medicines) {
            MedicineDto medicineDto = MedicineDto.of(medicine);
            medicineDtoList.add(medicineDto);
        }

        return medicineDtoList;
    }
}