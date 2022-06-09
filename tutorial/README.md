
# SpringBoot Tutorial

Spring Docs

[REST API w. Spring MVC & Spring Hateoas](https://spring.io/guides/tutorials/rest/)

[Scheduling Tasks](https://spring.io/guides/gs/scheduling-tasks/)

Github

[REST API w. Spring MVC & Spring Hateoas](https://github.com/spring-projects/spring-hateoas-examples)

[Scheduling Tasks](https://github.com/spring-guides/gs-scheduling-tasks)

---

## 작업내역

1. REST API w. Spring MVC & Spring Hateoas
	* Employee & Order Entity/Repository/Controller
	* Spring HATEOAS RepresentationModelAssembler

2. Scheduling Tasks
    * Add scheduled task for print time every 5 seconds
---

### 작업 노트

1. REST API w. Spring MVC & Spring Hateoas

**Employee & Order Entity**

| 각 Entity 마다 JpaRepository 생성

```java
@Entity
public class Employee {

	private @Id
	@GeneratedValue Long id;
	private String firstName;
	private String lastName;
	private String role;
	...
}

@Entity
@Table(name = "CUSTOMER_ORDER")
public class Order {

	private @Id
	@GeneratedValue Long id;

	private String description;
	private Status status;
	...
}
```

**Controller**

EmployeeController

* [GET] `/employees`: `employeeRepository.findAll();`
* [GET] `/employees/{id}`: `employeeRepository.findById(id);`
* [POST] `/employees/{id}`: `employeeRepository.save(newEmployee);`
* [PUT] `/employees/{id}`: `employeeRepository.findById(id); employee.setAttrbute(); employee.save();`
* [DELETE] `/employees/{id}`: `employeeRepository.deleteById(id);`

OrderController

* [GET] `/orders`: `orderRepository.findAll();`
* [GET] `/orders/{id}`: `orderRepository.findById(id);`
* [POST] `/orders/{id}`: `orderRepository.save(newOrder);`
* [PUT] `/orders/{id}/complete`: `orderRepository.findById(id); employee.setStatus(Status.COMPLETED);`
* [DELETE] `/orders/{id}/cancel`: `orderRepository.findById(id); employee.setStatus(Status.CANCELLED);`


**Controller 마다 기존 반환값 Entity를 상황에 맞게 수정**

* [GET] list: Entity ArrayList -> `org.springframework.hateoas.CollectionModel<EntityModel<T>>`
* [GET/PUT] unit: Entity -> `org.springframework.hateoas.EntityModel<T>`
* [DELETE] unit: void -> `org.springframework.http.ResponseEntity.noContent().build()`

**Spring HATEOAS MVC link 적용**

```java
package com.example.tutorial.payroll.assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import com.example.tutorial.payroll.controller.EmployeeController;
import com.example.tutorial.payroll.model.Employee;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class EmployeeModelAssembler implements RepresentationModelAssembler<Employee, EntityModel<Employee>> {
    @Override
    public EntityModel<Employee> toModel(Employee employee) {
        return EntityModel.of(employee,
                linkTo(methodOn(EmployeeController.class).one(employee.getId())).withSelfRel(),
                linkTo(methodOn(EmployeeController.class).all()).withRel("employees"));
    }
}

...

@Component
public class OrderModelAssembler implements RepresentationModelAssembler<Order, EntityModel<Order>> {

	@Override
	public EntityModel<Order> toModel(Order order) {

		EntityModel<Order> orderModel = EntityModel.of(order,
				linkTo(methodOn(OrderController.class).one(order.getId())).withSelfRel(),
				linkTo(methodOn(OrderController.class).all()).withRel("orders"));

		if (order.getStatus() == Status.IN_PROGRESS) {
			orderModel.add(linkTo(methodOn(OrderController.class).cancel(order.getId())).withRel("cancel"));
			orderModel.add(linkTo(methodOn(OrderController.class).complete(order.getId())).withRel("complete"));
		}

		return orderModel;
	}
}
```

* 각 객체마다 self link relation URI
* 객체 목록 link relation URI
* OrderModelAssembler 는 Order status에 따른 `cancel`, `complete` link relation URI


2. Scheduling Tasks
```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class ScheduledTasks {

    private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Scheduled(fixedRate = 5000)
    public void reportCurrentTime() {
        log.info("The time is now {}", dateFormat.format(new Date()));
    }
}
```
* @Component로 5000ms(5sec) 마다 시간을 출력하도록 설정
* @SpringBootApplication에 @EnableScheduling annotation 적용
