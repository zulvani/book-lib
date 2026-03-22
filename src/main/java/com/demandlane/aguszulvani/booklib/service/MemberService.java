package com.demandlane.aguszulvani.booklib.service;

import com.demandlane.aguszulvani.booklib.exception.BusinessException;
import com.demandlane.aguszulvani.booklib.model.entity.Member;
import com.demandlane.aguszulvani.booklib.model.request.MemberRequest;
import com.demandlane.aguszulvani.booklib.repository.LoanRepository;
import com.demandlane.aguszulvani.booklib.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final LoanRepository loanRepository;

    public MemberService(MemberRepository memberRepository, LoanRepository loanRepository) {
        this.memberRepository = memberRepository;
        this.loanRepository = loanRepository;
    }

    public List<Member> getAllMembers() {
        log.info("Fetching all members");
        return memberRepository.findAll();
    }

    public Member getMemberById(UUID id) throws BusinessException {
        log.info("Fetching member by id: {}", id);
        return memberRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Member not found: {}", id);
                    return new BusinessException(HttpStatus.NOT_FOUND, "Member not found");
                });
    }

    public Member getMemberByEmail(String email) throws BusinessException {
        log.info("Fetching member by email: {}", email);
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("Member not found with email: {}", email);
                    return new BusinessException(HttpStatus.NOT_FOUND, "Member not found");
                });
    }

    @Transactional
    public Member createMember(MemberRequest request) throws BusinessException {
        log.info("Creating member: {}", request);

        if (memberRepository.existsByEmail(request.getEmail())) {
            log.error("Member already exists with email: {}", request.getEmail());
            throw new BusinessException(HttpStatus.CONFLICT, "Member already exists with this email");
        }

        Member member = Member.builder()
                .name(request.getName())
                .email(request.getEmail())
                .maxActiveLoans(request.getMaxActiveLoans())
                .allowMemberToBorrowWhenOverdueLoan(request.isAllowMemberToBorrowWhenOverdueLoan())
                .loanDueDays(request.getLoanDueDays())
                .build();

        log.info("Member created: {}", member);
        return memberRepository.save(member);
    }

    @Transactional
    public Member updateMember(UUID id, MemberRequest request) throws BusinessException {
        log.info("Updating member: {}", id);

        Member existingMember = memberRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Member not found: {}", id);
                    return new BusinessException(HttpStatus.NOT_FOUND, "Member not found");
                });

        // check if email is taken by another member
        Optional<Member> memberWithSameEmail = memberRepository.findByEmail(request.getEmail());
        if (memberWithSameEmail.isPresent() && !memberWithSameEmail.get().getId().equals(id)) {
            log.error("Email already used by another member: {}", request.getEmail());
            throw new BusinessException(HttpStatus.CONFLICT, "Email already used by another member");
        }

        existingMember.setName(request.getName());
        existingMember.setEmail(request.getEmail());
        existingMember.setMaxActiveLoans(request.getMaxActiveLoans());
        existingMember.setAllowMemberToBorrowWhenOverdueLoan(request.isAllowMemberToBorrowWhenOverdueLoan());
        existingMember.setLoanDueDays(request.getLoanDueDays());

        log.info("Member updated: {}", existingMember);
        return memberRepository.save(existingMember);
    }

    @Transactional
    public void deleteMember(UUID id) throws BusinessException {
        log.info("Deleting member: {}", id);

        Member member = memberRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Member not found: {}", id);
                    return new BusinessException(HttpStatus.NOT_FOUND, "Member not found");
                });

        long activeLoans = loanRepository.countByMemberIdAndReturnedAtIsNull(id);
        if (activeLoans > 0) {
            log.error("Cannot delete member with active loans: {}", id);
            throw new BusinessException(HttpStatus.CONFLICT, "Cannot delete member with active loans");
        }

        memberRepository.delete(member);
        log.info("Member deleted: {}", id);
    }
}
