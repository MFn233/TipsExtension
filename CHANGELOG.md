# Changelog

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
