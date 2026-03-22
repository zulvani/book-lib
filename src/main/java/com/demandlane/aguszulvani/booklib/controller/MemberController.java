package com.demandlane.aguszulvani.booklib.controller;

import com.demandlane.aguszulvani.booklib.exception.BusinessException;
import com.demandlane.aguszulvani.booklib.model.entity.Member;
import com.demandlane.aguszulvani.booklib.model.request.MemberRequest;
import com.demandlane.aguszulvani.booklib.model.response.MemberResponse;
import com.demandlane.aguszulvani.booklib.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping
    public ResponseEntity<List<Member>> getAllMembers() {
        return ResponseEntity.ok(memberService.getAllMembers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MemberResponse> getMemberById(@PathVariable UUID id) {
        try {
            Member member = memberService.getMemberById(id);
            return ResponseEntity.ok(MemberResponse.builder().member(member).build());
        } catch (BusinessException e) {
            return ResponseEntity.status(e.getHttpStatus()).body(MemberResponse.builder().message(e.getMessage()).build());
        }
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<MemberResponse> getMemberByEmail(@PathVariable String email) {
        try {
            Member member = memberService.getMemberByEmail(email);
            return ResponseEntity.ok(MemberResponse.builder().member(member).build());
        } catch (BusinessException e) {
            return ResponseEntity.status(e.getHttpStatus()).body(MemberResponse.builder().message(e.getMessage()).build());
        }
    }

    @PostMapping
    public ResponseEntity<MemberResponse> createMember(@RequestBody MemberRequest request) {
        try {
            Member member = memberService.createMember(request);
            return ResponseEntity.status(201).body(MemberResponse.builder().member(member).build());
        } catch (BusinessException e) {
            return ResponseEntity.status(e.getHttpStatus()).body(MemberResponse.builder().message(e.getMessage()).build());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<MemberResponse> updateMember(@PathVariable UUID id, @RequestBody MemberRequest request) {
        try {
            Member member = memberService.updateMember(id, request);
            return ResponseEntity.ok(MemberResponse.builder().member(member).build());
        } catch (BusinessException e) {
            return ResponseEntity.status(e.getHttpStatus()).body(MemberResponse.builder().message(e.getMessage()).build());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MemberResponse> deleteMember(@PathVariable UUID id) {
        try {
            memberService.deleteMember(id);
            return ResponseEntity.ok(MemberResponse.builder().message("Member deleted successfully").build());
        } catch (BusinessException e) {
            return ResponseEntity.status(e.getHttpStatus()).body(MemberResponse.builder().message(e.getMessage()).build());
        }
    }
}
