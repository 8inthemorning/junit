package com.example.junit.domain;

import com.example.junit.study.StudyStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class Study {

    private StudyStatus status = StudyStatus.DRAFT;
    private int limit;
    private String name;
    private Member member;
    private LocalDateTime openedDateTime;

    public Study(int limit, String name) {
        this.limit = limit;
        this.name = name;
    }


    public Study(int limit) {
        if(limit < 0) {
            throw new IllegalArgumentException("limit은 0 보다 커야함!!");
        }
        this.limit = limit;
    }

    public StudyStatus getStatus() {
        return this.status;
    }

    public int getLimit() { return this.limit; }

    public String getName() {
        return name;
    }

    public void setOwner(Member member) {
        this.member = member;
    }

    public Member getOwner() {
        return this.member;
    }

    public void open() {
        this.openedDateTime = LocalDateTime.now();
        this.status = StudyStatus.OPEND;
    }
}
