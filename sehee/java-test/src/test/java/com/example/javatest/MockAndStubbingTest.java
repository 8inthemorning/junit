package com.example.javatest;

import com.example.javatest.domain.Member;
import com.example.javatest.domain.Study;
import com.example.javatest.domain.StudyStatus;
import com.example.javatest.member.MemberService;
import com.example.javatest.study.StudyRepository;
import com.example.javatest.study.StudyService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

//@Mock Annottion만 있다고 Mock 사용 불가하며, MockitoExtension을 @ExtendWith 사용해야 한다
@ExtendWith(MockitoExtension.class) //Extension 선언적 등록 방법
public class MockAndStubbingTest {

    @Mock MemberService memberService;

    @Mock StudyRepository studyRepository;

    @Disabled
    @DisplayName("Mock Test")
    void mockTest() {
//        MemberService memberService = mock(MemberService.class);
//        StudyRepository studyRepository = mock(StudyRepository.class);

        Optional<Member> optional = memberService.findById(1L);//Optinal.empty() return
        memberService.validate(1L); //validate는 구현체가 없기에 null return

        StudyService studyService = new StudyService(memberService, studyRepository);
        assertNotNull(studyService);
    }

    @Disabled
    @DisplayName("Stubbing Test")
    void stubbingTest(@Mock MemberService memberService
                            , @Mock StudyRepository studyRepository) {
        StudyService studyService = new StudyService(memberService, studyRepository);

        //Stubbing 할 객체 정의
        Member member = new Member();
        member.setId(1l);
        member.setEmail("ahns0206@gmail.com");

        //Mock 객체 Stubbing
        //파라미터에 memberId로 1l이 들어오면 명시한 Stubbing 객체가 return
//        when(memberService.findById(1l)).thenReturn(Optional.of(member));

        //파라미터로 어느값이 들어와도 Stubbing된걸 return
        when(memberService.findById(any()))
                .thenReturn(Optional.of(member)); //findById시, member return

        assertEquals("ahns0206@gmail.com", memberService.findById(1l).get().getEmail());
        assertEquals("ahns0206@gmail.com", memberService.findById(2l).get().getEmail());

        //예외를 던질 수 있도록 Stubbing
        //memberService에 memberId로 1l이 들어오면 명시한 Exception이 return
        doThrow(new IllegalArgumentException()).when(memberService).validate(1l);
        //예외 검사 (예외가 안일어나면 TestFaildException 발생)
        assertThrows(IllegalArgumentException.class, () -> {
            memberService.validate(1l); //예외 발생
        });

        //createNewStudy() 내 findById() 시 위에 선언한 Member 객체가 Stubbing됨
        Study study = new Study(10, "java");
        studyService.createNewStudy(1l, study);
    }

    @Disabled
    @DisplayName("여러개 Stubbing test")
    void multipleStubbingTest(@Mock MemberService memberService
            , @Mock StudyRepository studyRepository) {
        StudyService studyService = new StudyService(memberService, studyRepository);

        //Stubbing 할 객체 정의
        Member member = new Member();
        member.setId(1l);
        member.setEmail("ahns0206@gmail.com");

        //파라미터로 어느값이 들어와도 Stubbing된걸 return
        when(memberService.findById(any()))
                .thenReturn(Optional.of(member)) //1번째 findById시, member return
                .thenThrow(new IllegalArgumentException()) //2번째 findById시, Exception 발생
                .thenReturn(Optional.empty()); //3번째 findById 시, 빈 Optional 객체 return

        //1번째 호출
        assertEquals("ahns0206@gmail.com", memberService.findById(1l).get().getEmail());

        //2번째 호출
        assertThrows(IllegalArgumentException.class, () -> {
            memberService.validate(2l); //예외 발생
        });

        //3번째 호출
        assertEquals(Optional.empty(), memberService.findById(3l).get().getEmail());
    }

    @Disabled
    @DisplayName("Mock 객체 Stubbing 연습 문제")
    void MockStubbingTest() {
        Study study = new Study(10, "테스트");

        //Stubbing 할 객체 정의
        Member member = new Member();
        member.setId(1l);
        member.setEmail("ahns0206@gmail.com");

        // TODO memberService 객체에 findById 메소드를 1L 값으로 호출하면 Optional.of(member) 객체를 리턴하도록 Stubbing
        when(memberService.findById(1l)).thenReturn(Optional.of(member));

        // TODO studyRepository 객체에 save 메소드를 study 객체로 호출하면 study 객체 그대로 리턴하도록 Stubbing
        when(studyRepository.save(study)).thenReturn(study);

        StudyService studyService = new StudyService(memberService, studyRepository);
        studyService.createNewStudy(1L, study);

        assertNotNull(study.getOwner());
        assertEquals(member, study.getOwner());
    }

    @Disabled
    @DisplayName("Mock 객체 확인")
    void MockCallingValidate() {
        Study study = new Study(10, "테스트");

        //Stubbing 할 객체 정의
        Member member = new Member();
        member.setId(1l);
        member.setEmail("ahns0206@gmail.com");

        when(memberService.findById(1l)).thenReturn(Optional.of(member));
        when(studyRepository.save(study)).thenReturn(study);

        StudyService studyService = new StudyService(memberService, studyRepository);

        //memberService에서 notify(study) 가 호출안되었는지 확인 (호출 안했으면 오류 발생)
//        studyService.createNewStudy(1L, study);
//        verify(memberService, never()).notify(study);

        studyService.createAndNotifyNewStudy(1L, study);

        //몇번 호출되는지 확인
        //memberService에서 notify(study) 가 1번 호출되었는지 확인
        verify(memberService, times(1)).notify(study);
        //study로 notify 이후 다른 action이 없어야 오류 미발생 하나,
        //member로 notify를 하기에 오류 발생함
//        verifyNoMoreInteractions(memberService);
        verify(memberService, times(1)).notify(member);

        //순서대로 호출되는지 확인
        //notify func 이 호출될때 study, member 순으로 파라미터가 들어왔는지 확인
        InOrder inorder = inOrder(memberService);
        inorder.verify(memberService).notify(study);
        inorder.verify(memberService).notify(member);
    }

    @Test
    @DisplayName("Mockito BDD 스타일 API")
    void MockBDDStyle() {
        // BDD (Behavior-driven development)

        // 1. Given (주어진 상황)
        StudyService studyService = new StudyService(memberService, studyRepository);
        assertNotNull(studyService);

        Member member = new Member();//Stubbing 할 객체 정의
        member.setId(1l);
        member.setEmail("ahns0206@gmail.com");
        Study study = new Study(10, "테스트");

//        when(memberService.findById(1l)).thenReturn(Optional.of(member));
//        when(studyRepository.save(study)).thenReturn(study);
        given(memberService.findById(1l)).willReturn(Optional.of(member));
        given(studyRepository.save(study)).willReturn(study);

        // 2. When (발생한 행위)
        studyService.createAndNotifyNewStudy(1L, study);

        // 3. Then (행위에 따른 결과)
        assertEquals(member, study.getOwner());
//        verify(memberService, times(1)).notify(study);
//        verify(memberService, times(1)).notify(member);
//        verifyNoMoreInteractions(memberService);
        then(memberService).should(times(1)).notify(study);
        then(memberService).should(times(1)).notify(member);
        then(memberService).shouldHaveNoMoreInteractions();
    }

    @DisplayName("Mockito 연습문제 - 다른 사용자가 볼 수 있도록 스터디를 공개한다.")
    @Test
    void openStudy() {
        // Given
        StudyService studyService = new StudyService(memberService, studyRepository);
        Study study = new Study(10, "더 자바, 테스트");

        // TODO studyRepository Mock 객체의 save 메소드를호출 시 study를 리턴하도록 만들기.
        given(studyRepository.save(study)).willReturn(study);

        // When
        studyService.openStudy(study);

        // Then
        // TODO study의 status가 OPENED로 변경됐는지 확인
        // TODO study의 openedDataTime이 null이 아닌지 확인
        // TODO memberService의 notify(study)가 호출 됐는지 확인.
        assertEquals(study.getStatus(), StudyStatus.OPENED);
        assertNotNull(study.getOpenedDateTime());
        then(memberService).should().notify(study);
    }

}
