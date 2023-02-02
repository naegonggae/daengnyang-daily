package com.daengnyangffojjak.dailydaengnyang.service;

import com.daengnyangffojjak.dailydaengnyang.domain.dto.record.RecordResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.record.RecordWorkRequest;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.record.RecordWorkResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.Pet;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.Record;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.User;
import com.daengnyangffojjak.dailydaengnyang.exception.RecordException;
import com.daengnyangffojjak.dailydaengnyang.repository.PetRepository;
import com.daengnyangffojjak.dailydaengnyang.repository.RecordRepository;
import com.daengnyangffojjak.dailydaengnyang.repository.UserRepository;

import com.daengnyangffojjak.dailydaengnyang.utils.Validator;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.daengnyangffojjak.dailydaengnyang.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class RecordService {

	private final UserRepository userRepository;
	private final PetRepository petRepository;
	private final RecordRepository recordRepository;
	private final Validator validator;

	// 일기 상세(1개) 조회
	public RecordResponse getOneRecord(Long petId, Long recordId, String userName) {

		// 유저가 없는 경우 예외발생
		User user = validator.getUserByUserName(userName);

		// 펫이 없는 경우 예외발생
		Pet pet = validator.getPetById(petId);

		// 일기가 없는 경우 예외발생
		Record record = validator.getRecordById(recordId);

		return RecordResponse.of(user, pet, record);
	}

	// 전체 피드 조회
	@Transactional
	public Page<RecordResponse> getAllRecords(Pageable pageable) {

		return recordRepository.findAllByIsPublicTrue(pageable)
				.map(RecordResponse::from);
	}

	// 일기 작성
	@Transactional
	public RecordWorkResponse createRecord(Long petId, RecordWorkRequest recordWorkRequest,
			String userName) {

		// 유저가 없는 경우 예외발생
		User user = validator.getUserByUserName(userName);

		// 펫이 없는 경우 예외발생
		Pet pet = validator.getPetById(petId);

		Record savedRecord = recordRepository.save(recordWorkRequest.toEntity(user, pet));

		return RecordWorkResponse.builder()
				.message("일기 작성 완료")
				.recordId(savedRecord.getId())
				.build();
	}

	// 일기 수정
	@Transactional
	public RecordWorkResponse modifyRecord(Long petId, Long recordId,
			RecordWorkRequest recordWorkRequest, String userName) {

		// 유저가 없는 경우 예외발생
		User user = validator.getUserByUserName(userName);

		// 펫이 없는 경우 예외발생
		Pet pet = validator.getPetById(petId);

		// 일기가 없는 경우 예외발생
		Record record = validator.getRecordById(recordId);

		// 일기 작성 유저와 로그인 유저가 같지 않을 경우 예외발생
		if (!record.getUser().getId().equals(user.getId())) {
			throw new RecordException(INVALID_PERMISSION);
		}

		record.modifyRecord(recordWorkRequest);
		Record updated = recordRepository.saveAndFlush(record);

		return RecordWorkResponse.builder()
				.message("일기 수정 완료")
				.recordId(updated.getId())
				.build();
	}

	// 일기 삭제
	@Transactional
	public RecordWorkResponse deleteRecord(Long petId, Long recordId, String userName) {

		// 유저가 없는 경우 예외발생
		User user = validator.getUserByUserName(userName);

		// 펫이 없는 경우 예외발생
		Pet pet = validator.getPetById(petId);

		// 일기가 없는 경우 예외발생
		Record record = validator.getRecordById(recordId);

		// 일기 작성 유저와 로그인 유저가 같지 않을 경우 예외발생
		if (!Objects.equals(record.getUser().getId(), user.getId())) {
			throw new RecordException(INVALID_PERMISSION);
		}

		record.deleteSoftly();
		return RecordWorkResponse.builder()
				.message("일기 삭제 완료")
				.recordId(recordId)
				.build();
	}

}