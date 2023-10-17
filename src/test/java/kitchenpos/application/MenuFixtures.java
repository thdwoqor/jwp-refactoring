package kitchenpos.application;

import java.math.BigDecimal;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;

public class MenuFixtures {

    public static Menu 떡볶이메뉴() {
        MenuGroup 분식메뉴그룹 = MenuGroupFixtures.분식메뉴그룹();
        return new Menu(1L, "떡볶이", BigDecimal.valueOf(2100), 분식메뉴그룹.getId());
    }
}
