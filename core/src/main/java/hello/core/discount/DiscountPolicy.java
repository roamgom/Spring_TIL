package hello.core.discount;

import hello.core.member.Member;

public interface DiscountPolicy {

    /**
     *
     * @param member 멤버 객체
     * @param price 금액
     * @return 할인 대상 금액
     */
    int discount(Member member, int price);
}
