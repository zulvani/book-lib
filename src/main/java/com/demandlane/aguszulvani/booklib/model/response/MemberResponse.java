package com.demandlane.aguszulvani.booklib.model.response;

import com.demandlane.aguszulvani.booklib.model.entity.Member;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MemberResponse {
    private Member member;
    private String message;
}
