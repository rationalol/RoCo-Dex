# RoCoDex (洛克王国精灵图鉴)

RoCoDex 是一款基于 Jetpack Compose 开发的《洛克王国》同人图鉴应用。它提供了全面的精灵信息查询、招式列表、属性克制分析等功能，旨在为玩家提供流畅且现代化的使用体验。

## 🌟 功能特点

-   **精灵图鉴 (Gallery)**
    -   展示全系列精灵列表。
    -   支持按名称或编号快速搜索。
    -   **多属性筛选**：可同时勾选多个属性进行复合筛选。
    -   **异色筛选**：一键切换查看异色（Shiny）精灵。
    -   响应式网格布局，适配手机、折叠屏和平板。
-   **招式图鉴 (Skills)**
    -   完整的技能数据库查询。
    -   支持按属性和技能类型过滤。
-   **属性克制 (Weakness)**
    -   直观的属性克制表。
    -   支持多属性组合防御分析，精准计算弱点。
-   **现代化 UI/UX**
    -   全量使用 **Jetpack Compose** 打造。
    -   遵循 **Material 3** 设计规范。
    -   支持深色模式（根据系统设置适配）。
    -   流畅的动画过渡（如筛选栏的展开与折叠）。

## 🛠️ 技术栈

-   **开发语言**：Kotlin
-   **UI 框架**：Jetpack Compose (Material 3)
-   **架构模式**：MVVM (ViewModel, Flow, StateFlow)
-   **图片加载**：Coil (支持加载 Assets 及网络资源)
-   **依赖注入**：原生 ViewModel 配合 AndroidViewModel
-   **数据处理**：Gson, Kotlin Coroutines
-   **屏幕适配**：Material 3 Adaptive (WindowSizeClass)

## 📁 项目结构

```text
app/src/main/java/com/yinpei/rocodex/
├── data/               # 数据层
│   ├── model/         # 数据模型 (Pet, Skill, etc.)
│   ├── repository/    # 数据仓库 (数据加载逻辑)
│   └── ...            # 属性数据、工具类 (ImageUtils)
├── ui/                 # UI 层
│   ├── components/    # 通用组件 (FilterBar, StatBar, ElementBadge)
│   ├── gallery/       # 精灵图鉴模块
│   ├── skills/        # 招式图鉴模块
│   ├── weakness/      # 属性克制模块
│   └── theme/         # 主题样式 (Color, Type, Theme)
└── MainActivity.kt    # 入口 Activity
```

## 🚀 快速开始

1.  **克隆项目**
    ```bash
    git clone https://github.com/your-username/roco-kingdom-pet-compose.git
    ```
2.  **在 Android Studio 中打开**
    -   推荐使用最新稳定版 Android Studio (Ladybug 或更高版本)。
    -   Gradle 版本：8.7
3.  **运行应用**
    -   连接 Android 模拟器或真机。
    -   点击 "Run" 按钮。

## 📄 开源协议

本项目仅供学习交流使用，相关素材版权归《洛克王国:世界》官方所有。

