package hello.core.scope;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Scope;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import static org.assertj.core.api.Assertions.*;

public class SingletonTest {

    @Test
    void singletonBeanFind() {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(SingletonBean.class);

        SingletonBean bean1 = applicationContext.getBean(SingletonBean.class);
        System.out.println("bean1 = " + bean1);
        SingletonBean bean2 = applicationContext.getBean(SingletonBean.class);
        System.out.println("bean2 = " + bean2);


        assertThat(bean1).isSameAs(bean2);

        applicationContext.close();
    }

    @Scope("singleton")
    static class SingletonBean {

        @PostConstruct
        public void init() {
            System.out.println("singletonBean.init");
        }

        @PreDestroy
        public void destroy() {
            System.out.println("singletonBean.destroy");
        }
    }
}
