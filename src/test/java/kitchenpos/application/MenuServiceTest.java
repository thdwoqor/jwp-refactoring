package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import javax.persistence.EntityManager;
import kitchenpos.application.dto.MenuCreateRequest;
import kitchenpos.application.dto.MenuCreateRequest.MenuProductRequest;
import kitchenpos.application.dto.MenuResponse;
import kitchenpos.dao.MenuGroupRepository;
import kitchenpos.dao.MenuProductRepository;
import kitchenpos.dao.MenuRepository;
import kitchenpos.dao.ProductRepository;
import kitchenpos.domain.Menu;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

@DataJpaTest
class MenuServiceTest {

    private MenuService menuService;
    @Autowired
    private MenuRepository menuRepository;
    @Autowired
    private MenuGroupRepository menuGroupRepository;
    @Autowired
    private MenuProductRepository menuProductRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private EntityManager manager;

    @BeforeEach
    void setUp() {
        menuService = new MenuService(menuRepository, menuGroupRepository, menuProductRepository, productRepository);
    }

    @Test
    void 메뉴_그룹_ID_가_존재하지_않은_경우_예외가_발생한다() {
        MenuProductRequest menuProductRequest = new MenuProductRequest(1L, 2);
        MenuCreateRequest menuCreateRequest = new MenuCreateRequest(
                "후라이드+후라이드",
                19000,
                0L,
                List.of(menuProductRequest)
        );

        assertThatThrownBy(() -> menuService.create(menuCreateRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("해당 메뉴 그룹 ID가 존재하지 않습니다.");
    }

    @Test
    void 제품_ID가_존재하지_않는_경우_예외가_발생한다() {
        MenuProductRequest menuProductRequest = new MenuProductRequest(0L, 2);
        MenuCreateRequest menuCreateRequest = new MenuCreateRequest(
                "후라이드+후라이드",
                19000,
                1L,
                List.of(menuProductRequest)
        );

        assertThatThrownBy(() -> menuService.create(menuCreateRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("해당 제품 ID가 존재하지 않습니다.");
    }

    @Test
    void 전체_제품의_총_가격_보다_메뉴가격이_크면_예외가_발생한다() {
        MenuProductRequest menuProductRequest = new MenuProductRequest(1L, 2);
        MenuCreateRequest menuCreateRequest = new MenuCreateRequest(
                "후라이드+후라이드",
                32001,
                1L,
                List.of(menuProductRequest)
        );

        assertThatThrownBy(() -> menuService.create(menuCreateRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("메뉴 가격은 단품을 가격보다 높을 수 없습니다.");
    }

    @Test
    void 메뉴_생성를_생성할_수_있다() {
        MenuProductRequest menuProductRequest = new MenuProductRequest(1L, 2);
        MenuCreateRequest menuCreateRequest = new MenuCreateRequest(
                "후라이드+후라이드",
                19000,
                1L,
                List.of(menuProductRequest)
        );

        MenuResponse menu = menuService.create(menuCreateRequest);
        manager.flush();
        manager.clear();
        Menu saveMenu = menuRepository.findById(menu.getId()).orElseThrow();
        Assertions.assertThat(menu.getId()).isEqualTo(saveMenu.getId());
    }
}
