package hello.core.singleton;

import hello.core.AppConfig;
import hello.core.member.MemberRepository;
import hello.core.member.MemberServiceImpl;
import hello.core.order.OrderServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class ConfigurationSingletonTest {

    @Test
    @DisplayName("싱글톤 Configuration 확인")
    void configurationTest() {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);

        MemberServiceImpl memberService = applicationContext.getBean("memberService", MemberServiceImpl.class);
        OrderServiceImpl orderService = applicationContext.getBean("orderService", OrderServiceImpl.class);
        MemberRepository memberRepository = applicationContext.getBean("memberRepository", MemberRepository.class);


        MemberRepository memberRepository1 = memberService.getMemberRepository();
        MemberRepository memberRepository2 = orderService.getMemberRepository();

        System.out.println("memberService -> memberRepository = " + memberRepository1);
        System.out.println("orderService -> memberRepository = " + memberRepository2);
        System.out.println("memberRepository = " + memberRepository);

        Assertions.assertThat(memberRepository).isSameAs(memberRepository1).isSameAs(memberRepository2);
    }
    
    @Test
    void configurationDeep() {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);

        AppConfig bean = applicationContext.getBean(AppConfig.class);
        System.out.println("bean.getClass() = " + bean.getClass());
    }
}
