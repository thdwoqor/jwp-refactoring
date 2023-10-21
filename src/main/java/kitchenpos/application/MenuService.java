package kitchenpos.application;

import java.math.BigDecimal;
import java.util.List;
import kitchenpos.application.dto.MenuCreateRequest;
import kitchenpos.application.dto.MenuCreateRequest.MenuProductRequest;
import kitchenpos.dao.MenuGroupRepository;
import kitchenpos.dao.MenuProductRepository;
import kitchenpos.dao.MenuRepository;
import kitchenpos.dao.ProductRepository;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Price;
import kitchenpos.domain.Product;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MenuService {

    private final MenuRepository menuRepository;
    private final MenuGroupRepository menuGroupRepository;
    private final MenuProductRepository menuProductRepository;
    private final ProductRepository productRepository;

    public MenuService(
            final MenuRepository menuRepository,
            final MenuGroupRepository menuGroupRepository,
            final MenuProductRepository menuProductRepository,
            final ProductRepository productRepository
    ) {
        this.menuRepository = menuRepository;
        this.menuGroupRepository = menuGroupRepository;
        this.menuProductRepository = menuProductRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public Menu create(final MenuCreateRequest request) {
        validateMenuPrice(request);
        return saveMenu(request);
    }

    private Menu saveMenu(final MenuCreateRequest request) {
        MenuGroup menuGroup = menuGroupRepository.findById(request.getMenuGroupId())
                .orElseThrow(()->new IllegalArgumentException("해당 메뉴 그룹 ID가 존재하지 않습니다."));
        Menu menu = menuRepository.save(new Menu(request.getName(), Price.of(request.getPrice()), menuGroup));

        for (MenuProductRequest menuProduct : request.getMenuProducts()) {
            Product product = productRepository.findById(menuProduct.getProductId())
                    .orElseThrow(()->new IllegalArgumentException("해당 제품 ID가 존재하지 않습니다."));
            menuProductRepository.save(new MenuProduct(menu, product, menuProduct.getQuantity()));
        }

        return menu;
    }

    /*
    국물 떡볶이 세트 (국물 떡볶이 1인분, 순대 1인분) 8000원
    국물 떡볶이 6000원
    순대 3000원
    세트 메뉴가 단품을 시킨것 보다 가격이 높은지 검증
     */
    private void validateMenuPrice(final MenuCreateRequest request) {
        final List<MenuProductRequest> menuProducts = request.getMenuProducts();
        final BigDecimal price = BigDecimal.valueOf(request.getPrice());

        BigDecimal sum = BigDecimal.ZERO;
        for (final MenuProductRequest menuProduct : menuProducts) {
            final Product product = productRepository.findById(menuProduct.getProductId())
                    .orElseThrow(()->new IllegalArgumentException("해당 제품 ID가 존재하지 않습니다."));
            sum = sum.add(product.getPrice().multiply(BigDecimal.valueOf(menuProduct.getQuantity())));
        }

        if (price.compareTo(sum) > 0) {
            throw new IllegalArgumentException("메뉴 가격은 단품을 가격보다 높을 수 없습니다.");
        }
    }

    public List<Menu> list() {
        return menuRepository.findAll();
    }
}
