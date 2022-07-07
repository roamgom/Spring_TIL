package hello.core;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@ToString(exclude = "id")
@EqualsAndHashCode
public class HelloLombok {

    @NonNull
    private Long id;
    @NonNull
    private String name;
    private final String korean = "Yes";
    private int age;

    public static void main(String[] args) {
        HelloLombok helloLombok = new HelloLombok();
        helloLombok.setName("asdf");

        System.out.println("helloLombok.getName() = " + helloLombok.getName());
        
        HelloLombok nameOnly = new HelloLombok(1L, "HongGilDong");

        System.out.println("nameOnly.getId() = " + nameOnly.getId());
        System.out.println("nameOnly.getName() = " + nameOnly.getName());

        System.out.println("nameOnly.toString() = " + nameOnly.toString());

        System.out.println("helloLombok.hashCode() = " + helloLombok.hashCode());
        System.out.println("nameOnly.hashCode() = " + nameOnly.hashCode());

        System.out.println("nameOnly.equals(helloLombok) = " + nameOnly.equals(helloLombok));
    }
}
