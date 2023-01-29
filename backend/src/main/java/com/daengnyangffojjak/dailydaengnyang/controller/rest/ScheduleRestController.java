package com.daengnyangffojjak.dailydaengnyang.controller.rest;

import com.daengnyangffojjak.dailydaengnyang.domain.dto.Response;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.schedule.*;
import com.daengnyangffojjak.dailydaengnyang.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Slf4j
public class ScheduleRestController {

	private final ScheduleService scheduleService;

	// 일정 등록
	@PostMapping(value = "/pets/{petId}/schedules")
	public ResponseEntity<Response<ScheduleCreateResponse>> createSchedule(@PathVariable Long petId,
			@RequestBody ScheduleCreateRequest scheduleCreateRequest,
			@AuthenticationPrincipal UserDetails user) {
		log.debug("petId : {} / scheduleCreateRequest : {} / authentication : {} ", petId,
				scheduleCreateRequest, user.getUsername());
		ScheduleCreateResponse scheduleCreateResponse = scheduleService.create(petId,
				scheduleCreateRequest, user.getUsername());
		return ResponseEntity.created(
						URI.create("api/v1/pets/" + petId + "/schedules" + scheduleCreateResponse.getId()))
				.body(Response.success(scheduleCreateResponse));

	}

	// 일정 수정
	@PutMapping(value = "/pets/{petId}/schedules/{scheduleId}")
	public ResponseEntity<Response<ScheduleModifyResponse>> modifySchedule(@PathVariable Long petId,
			@PathVariable Long scheduleId, @RequestBody ScheduleModifyRequest scheduleModifyRequest,
			@AuthenticationPrincipal UserDetails user) {
		log.debug("scheduleId : {} / scheduleModifyRequest : {} / authentication : {} ", scheduleId,
				scheduleModifyRequest, user.getUsername());
		ScheduleModifyResponse scheduleModifyResponse = scheduleService.modify(petId, scheduleId,
				scheduleModifyRequest, user.getUsername());
		return ResponseEntity.created(
						URI.create("api/v1/pets/" + petId + "/schedules/" + scheduleId))
				.body(Response.success(scheduleModifyResponse));

	}

	// 일정 삭제
	@DeleteMapping(value = "/pets/{petId}/schedules/{scheduleId}")
	public ResponseEntity<Response<ScheduleDeleteResponse>> deleteSchedule(@PathVariable Long petId,
			@PathVariable Long scheduleId, @AuthenticationPrincipal UserDetails user) {
		ScheduleDeleteResponse scheduleDeleteResponse = scheduleService.delete(petId, scheduleId,
				user.getUsername());
		return ResponseEntity.ok().body(Response.success(scheduleDeleteResponse));
	}

	// 일정 상세 조회(단건)
	@GetMapping(value = "/pets/{petId}/schedules/{scheduleId}")
	public ResponseEntity<Response<ScheduleResponse>> getSchedule(@PathVariable Long petId,
			@PathVariable Long scheduleId, @AuthenticationPrincipal UserDetails user) {
		ScheduleResponse scheduleResponse = scheduleService.get(petId, scheduleId,
				user.getUsername());
		return ResponseEntity.ok().body(Response.success(scheduleResponse));
	}

	// 일정 전체 조회
	@GetMapping(value = "/pets/{petId}/schedules")
	public ResponseEntity<Response<Page<ScheduleListResponse>>> listSchedule(
			@PathVariable Long petId, @AuthenticationPrincipal UserDetails user,
			@PageableDefault(size = 20) @SortDefault(sort = "category", direction = Sort.Direction.DESC) Pageable pageable) {
		Page<ScheduleListResponse> scheduleListResponses = scheduleService.list(petId,
				user.getUsername(), pageable);
		return ResponseEntity.ok().body(Response.success(scheduleListResponses));
	}

}
