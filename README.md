# Carbon（碳）

Minecraft 1.17.1 Fabric 客户端 GPU 物品模型渲染优化模组，Java 16+，MIT 许可证。

- Fabric Loader 0.12+，Fabric API 0.46.x（模组 id 为 `fabric`），Mod Menu 2.x 可选。
- 构建：Java 17 toolchain + `--release 16`，Loom 1.7.4 官方 Mojang 映射，输出 `carbon-1.17.1.9-A-fabric.jar`。

![Carbon](branding/mod-cover.svg)

缓存重复物品的模型几何，通过 GPU 实例化合并绘制。标准 `ItemRenderer.renderModelLists` 提交保留调用者的变换、光照和叠加参数，覆盖掉落物、手持、界面及其他调用标准接口的渲染。特殊投影光效、自定义材质和需排序的半透明模型保留原生处理。

默认启用掉落物专用管线，在 GPU 计算自旋、浮动和缩放，复用静止场景记录；普通附魔光效与地面阴影同样参与实例化。可选物理化沿模型局部轴翻滚，停止后平滑躺平，并加入每个实体固定的微小高度偏移。物理效果仅改变视觉，不修改速度、碰撞或拾取。

## 配置

配置文件为 `config/carbon-client.toml`（极简键值格式，与 Forge 版键名一致）。

```toml
enabled = true
optimizeDroppedItems = true
physicalDroppedItems = false
skipVisibilityChecks = true
cacheUpdateBudgetMs = 2.0
```

- `enabled`：优化总开关。
- `optimizeDroppedItems`：默认开启掉落物专用 GPU 管线；关闭后保留原有掉落物变换，通过标准模型提交继续优化。
- `physicalDroppedItems`：默认关闭，需要开启掉落物专用管线；落地姿态收稳后停止派发变换计算。
- `skipVisibilityChecks`：专用管线默认跳过遮挡与视锥检查，完整提交距离内的普通掉落物；距离限制始终有效。
- `cacheUpdateBudgetMs`：每帧缓存更新预算，默认 2 毫秒，可设为 0–20 毫秒，0 表示立即完成；队列完成前绘制旧实例与阴影。单个物品更新可能超预算。

`/carbon enabled false` 停用优化，`/carbon stats` 查看统计。安装 Mod Menu 后可在模组列表的“配置”按钮打开设置界面。

## 光影

Iris / Oculus 访问集中在 `IrisCompatibility`，未向光影模组注入 Mixin；无法保持原生通道语义时保留原有提交。

## 与 Forge 版的差异

- 配置系统由 Forge ConfigSpec 改为内置轻量读写器，键名与旧配置一致并自动迁移 `item_model_optimization-client.toml`。
- 事件（资源重载、连接断开、渲染阶段、客户端命令）改用 Fabric API 对应入口；`/carbon config` 打开配置界面，安装 Mod Menu 后模组列表“配置”按钮同样可用。
- 原版 1.17 无 `shouldSpreadItems`/`shouldBob` 开关，掉落物始终按原版散布与浮动。
- Forge 的 `getRenderPasses`/`getRenderTypes` 扩展点不可用，仅接管标准实体通道的普通模型；图集原始图像与顶点格式偏移通过 Accessor Mixin 读取（1.17 无 `SpriteContents`，用 `TextureAtlasSprite.mainImage`）。
- 静止场景复用不再受 Forge `IClientItemExtensions` 影响。
- Fabric API 为 command api v1（无 v2）：直接向 `ClientCommandManager.DISPATCHER` 注册，`/carbon enabled|config|stats` 功能一致，反馈消息单参数。
- 数学与渲染 API 为 1.17 形态：`com.mojang.math` 矩阵/向量、`BufferBuilder.end()`+`popNextBuffer()`、`ItemTransforms.TransformType`，实例数据以列主序写入对齐 GLSL mat 属性。
- 实体世界访问用 `Entity.level` 字段、`isOnGround()`；`ItemTags.COMPASS` 不存在，附魔判断按 `Items.COMPASS`/`Items.CLOCK` 枚举比较。
- 着色器 `FogShape` uniform 保留（默认 0 = 球形雾），1.17 无圆柱雾概念，行为等价。

## 验证与构建

使用 Java 17 执行 `gradlew.bat build`，输出 `build/libs/carbon-1.17.1.9-A-fabric.jar`。原 Forge 工程备份在 `forge/` 目录。
