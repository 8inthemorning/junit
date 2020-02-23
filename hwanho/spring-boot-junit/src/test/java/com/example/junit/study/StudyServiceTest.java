package com.example.junit.study;

import com.example.junit.domain.Member;
import com.example.junit.domain.Study;
import com.example.junit.member.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudyServiceTest {

    @Mock
    MemberService memberService;

    @Mock
    StudyRepository studyRepository;

    @Test
    void createStudyService1() {
        //StudyService 인스턴스를 생성 못함 -> MeberService, StudyService 가 아직 interface만 있고 구현체가 없음 -> mock 사용
        MemberService memberService = mock(MemberService.class);
        StudyRepository studyRepository = mock(StudyRepository.class);

        StudyService studyService = new StudyService(memberService, studyRepository);

        assertNotNull(studyService);
    }

    //특정한 매개변수를 받은 경우 특정한 값을 리턴하거나 예외를 던지도록 만들 수 있다.
    @Test
    void createStudyService2(@Mock MemberService memberService, @Mock StudyRepository studyRepository) {

        //Optional<Member> optional = memberService.findById(1L);
        //assertNull(optional); //optional 타입인 경우 Optional.empty 리턴
        //memberService.validate(2L); //void 메소드는 예외를 던지지 않고 아무런 일동 발생 안한

        StudyService studyService = new StudyService(memberService, studyRepository);
        assertNotNull(studyService);

        Member member = new Member();
        member.setId(1L);
        member.setEmail("allmind75@gmail.com");

        Study study = new Study(10, "java");

        //stubbing
        //when(memberService.findById(1L)).thenReturn(Optional.of(member)); //findById 에 1이 오면 적용
        when(memberService.findById(any())).thenReturn(Optional.of(member)); //어떤 값이 오든 적용

        assertEquals("allmind75@gmail.com", memberService.findById(1L).get().getEmail());
        studyService.createNewStudy(1L, study); //memberId가 1인 경우 stubbing, 1이 아닌 경우 에러
    }

    //Void메소드특정매개변수를받거나호출된경우예외를발생시킬수있다.
    @Test
    void createStudyService3(@Mock MemberService memberService, @Mock StudyRepository studyRepository) {
        StudyService studyService = new StudyService(memberService, studyRepository);
        assertNotNull(studyService);

        Member member = new Member();
        member.setId(1L);
        member.setEmail("allmind75@gmail.com");

        Study study = new Study(10, "java");

        when(memberService.findById(any())).thenReturn(Optional.of(member)); //어떤 값이 오든 적용

        assertEquals("allmind75@gmail.com", memberService.findById(1L).get().getEmail());
        doThrow(new IllegalArgumentException()).when(memberService).validate(1L); //void 메소드에서 특정 변수인 경우 에외 던지기

        assertThrows(IllegalArgumentException.class, () -> {
            memberService.validate(1L);
        });

        memberService.validate(2L);

        studyService.createNewStudy(1L, study); //memberId가 1인 경우 stubbing, 1이 아닌 경우 에러
    }

    // 메소드가 동일한 매개변수로 여러번 호출될 때 각기 다르게 행동호도록 조작할 수도 있다.
    @Test
    void createStudyService4(@Mock MemberService memberService, @Mock StudyRepository studyRepository) {
        StudyService studyService = new StudyService(memberService, studyRepository);
        assertNotNull(studyService);

        Member member = new Member();
        member.setId(1L);
        member.setEmail("allmind75@gmail.com");

        Study study = new Study(10, "java");

        //stubbing, 여러번 호출될 때
        when(memberService.findById(any()))         //1번째 호출
                .thenReturn(Optional.of(member))    //2번째 호출
                .thenThrow(new RuntimeException())  //3번째 호출
                .thenReturn(Optional.empty()); //어떤 값이 오든 적용

        Optional<Member> byId = memberService.findById(1L);
        assertEquals("allmind75@gmail.com", byId.get().getEmail());
        assertThrows(RuntimeException.class, () -> {
           memberService.findById(2L);
        });
        assertEquals(Optional.empty(), memberService.findById(1L));

    }


}