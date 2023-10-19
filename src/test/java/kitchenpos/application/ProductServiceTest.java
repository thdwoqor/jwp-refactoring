package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;

import kitchenpos.dao.ProductRepository;
import kitchenpos.domain.Product;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;
    @InjectMocks
    private ProductService productService;

    @Test
    void 상품_생성할_수_있다() {
        Product 로제떡볶이 = ProductFixtures.로제떡볶이();

        productService.create(로제떡볶이);

        verify(productRepository).save(로제떡볶이);
    }

    @Test
    void 전체_상품_조회할_수_있다() {
        productService.list();

        verify(productRepository).findAll();
    }
}
