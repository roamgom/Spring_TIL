package hello.core.beanfind;

import hello.core.AppConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Arrays;

class ApplicationContextInfoTest {

    AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);

    @Test
    @DisplayName("모든 빈 출력하기")
    void findAllBean() {
        String[] beanDefinitionNames = applicationContext.getBeanDefinitionNames();

        Arrays.stream(beanDefinitionNames).forEach(beanName -> {
            BeanDefinition beanDefinition = applicationContext.getBeanDefinition(beanName);

            if (beanDefinition.getRole() == BeanDefinition.ROLE_SUPPORT) {
                Object bean = applicationContext.getBean(beanName);
                System.out.println("name: " + beanName + " object: " + bean);
            }
        });

    }
}
