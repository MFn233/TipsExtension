# Changelog

## [1.1.6] - 2026-11-20

### Fixed
- 呼吸效果此前同时作用于标题「提示」和正文，现在只作用于正文；标题始终保持完全不透明。

### Note
- 这是 1.1.1 引入的回归：当时为了让标题和正文视觉统一，把同一个 alpha 通道写进了两个颜色。现在按最初的设计分开处理。
## [1.1.5] - 2026-11-20

### Fixed
- `mcmod.info` 是**非法 JSON**：`description` 里含真实换行符（`gradle.properties` 会把 `\n` 解析成换行，再原样写进模板），JSON 解析在第 6 行 73 列失败。
- 后果是 Forge/Cleanroom 会忽略本模组的全部元数据，`dependencies: ["tips"]` 因此失效——玩家未安装 Tips 时不会得到"缺少前置"的友好提示，而是直接崩在类加载阶段。
- 现在 `mod_description` 改为单行，重新生成的 `mcmod.info` 可正常解析。

### Note
- 版本号、作者、致谢等字段不受影响；游戏内表现也没有变化，只是元数据恢复可用。
## [1.1.4] - 2026-11-20

### Fixed
- 配置界面里「正文最大宽度」的标签显示为 `Format error: 正文最大宽度（%）`。原因是 Forge 渲染配置项名称时走 `String.format`，标签里的 ASCII 百分号被当成格式符，`%）` 不是合法转换，异常被捕获后返回 `"Format error: " + 原文`。
- 三个语言文件里的标签改为不含 `%` 的写法（中文「占屏幕宽的百分比」、英文 `percent of screen width`）。

### Note
- 数值项本身一直是正常工作的，只是标签显示被破坏。若你已经改过 `pauseWidthPercent` 的值，不受影响。
## [1.1.3] - 2026-11-20

### Fixed
- 游戏内配置界面（模组列表 → Tips Extension → Config）里看不到新增的 `pause` 分类。原因是 `GuiFactory` 硬编码了 `general` 与 `breathing` 两个分类，新增分类没有登记。
- 现在改为「顺序表 + 兜底遍历」：先按 `CATEGORY_ORDER` 排序显示，再把未登记的分类补在后面。以后新增分类只需要加进这个数组，不会再出现配置项存在但界面里找不到的情况。
- 顺带补了配置尚未加载时的空指针保护（退回默认界面而不是崩溃）。
## [1.1.2] - 2026-11-20

### Added
- 暂停界面提示语有了**独立的坐标设置**，位于新增的 `pause` 分类：
  - `pauseX`：距屏幕左边缘（GUI 缩放后的像素）
  - `pauseY`：距屏幕底边（整块底部对齐，标题向上展开）
  - `pauseWidthPercent`：正文最大宽度占屏幕宽度的百分比
- 新增 `pause` 分类的中 / 英 / 繁语言条目。

### Changed
- 默认位置改为 `pauseX = 4`、`pauseY = 4`，把提示语放到暂停按钮下方的空白区。
- 致谢更新为：原始 Tips 模组由 Darkhax 制作；暂停界面渲染、呼吸效果与 1.1.x 维护由 MFn233 完成。

### Fixed
- 提示语会压在「保存并退回到标题画面」按钮上。此前直接复用了 Tips 的 `xOffset / yOffset`，而那组数值是给加载界面调校的（默认值较大），在暂停菜单里就会顶到按钮行。

### Note
- 升级后首次启动会在 `config/tipsext.cfg` 中自动生成 `pause` 分类，原有配置项不受影响。

## [1.1.1] - 2026-11-20

### Fixed
- 暂停菜单提示语的纵向位置会随 GUI 缩放（小/普通/大/自动）漂移：旧实现把 x 乘了 `scaleFactor` 而 y 没有，两者单位不一致。现在统一使用 scaled 坐标，并把整块文案改为底部对齐（块底距屏幕底部 = `yOffset`）。
- 呼吸灯不再改写 Tips 模组的静态 `Config.textColor`。旧做法会污染游戏内所有使用 Tips 的渲染点，并覆盖玩家自己在 Tips 配置里设置的透明度。现在把 alpha 直接编码进 `drawString` / `drawSplitString` 的颜色参数（依据 `FontRenderer#drawString` 从颜色高 8 位读取 alpha 的行为）。
- 呼吸灯动画改用 `System.nanoTime()`，避免系统时钟调整导致动画跳变。
- `minAlpha = 0` 时不再出现「本该完全透明却变成完全不透明」：`FontRenderer` 会把 alpha 0 强制改回 255，现已在插件侧限制下限。

### Changed
- 移除 `onClientTick` 处理器：透明度改为绘制时实时计算，少一个每 tick 执行的监听器。
- 标题与正文一起做呼吸效果（旧版只有正文变化）。

### Removed
- `ExampleMod.java.bak`（MDK 模板残留）。
- `mixins.modid.json`（空 mixin 配置，且 `use_mixins = false`，从未被引用）。

## [1.0.0] - 2023-09-15

### Added
- This is a default template changelog that follows the [KeepAChangelog Convention](https://keepachangelog.com/en/1.1.0/)




