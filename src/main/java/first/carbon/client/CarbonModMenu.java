package first.carbon.client;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

// Mod Menu 的“配置”按钮入口；未安装 Mod Menu 时此类不会被加载。
public final class CarbonModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return ItemConfigScreen::new;
    }
}
