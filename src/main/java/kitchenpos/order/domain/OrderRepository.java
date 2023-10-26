package kitchenpos.order.domain;

import java.util.List;
import kitchenpos.order.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {

    default Order getById(final Long orderId){
        return findById(orderId)
                .orElseThrow(()->new RuntimeException("존재하지 않는 주문입니다."));
    }
}
