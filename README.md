# 经典俄罗斯方块安卓游戏

一款基于Android平台的经典俄罗斯方块游戏，使用Kotlin语言开发。

## 功能特性

- 🎮 7种经典俄罗斯方块形状（I、J、L、O、S、T、Z）
- ⭐ 分数系统和等级提升机制
- ⏯️ 游戏暂停/继续功能
- 🎯 方块旋转和移动控制
- 📊 实时分数和等级显示
- 🎨 彩色方块和网格界面

## 项目结构

```
ClassicTetris/
├── app/
│   ├── src/main/java/com/classic/tetris/
│   │   ├── MainActivity.kt      # 主Activity，游戏控制逻辑
│   │   ├── GameView.kt          # 游戏视图，渲染和游戏逻辑
│   │   └── Block.kt             # 方块类和形状定义
│   ├── src/main/res/
│   │   ├── layout/activity_main.xml  # 游戏界面布局
│   │   ├── values/colors.xml          # 颜色资源
│   │   ├── values/strings.xml         # 字符串资源
│   │   └── values/themes.xml          # 应用主题
│   └── build.gradle             # 应用模块配置
├── build.gradle                 # 项目级配置
├── settings.gradle             # 项目设置
└── gradle.properties           # Gradle属性配置
```

## 运行要求

### 必需软件
1. **Java JDK 17+** - 下载地址: https://adoptium.net/
2. **Android Studio** - 下载地址: https://developer.android.com/studio

### 环境配置问题解决

如果您遇到构建错误，特别是关于 `com.android.application` 插件未找到的错误，请按照以下步骤解决：

#### 错误原因
该错误通常是由于以下原因：
1. Android Gradle 插件未正确配置
2. Gradle 包装器文件缺失或损坏
3. JAVA_HOME 环境变量未正确设置

#### 解决方案

**推荐方案（最简单）：**
1. 安装 Android Studio
2. 使用 Android Studio 打开本项目目录 `d:\elsfk`
3. Android Studio 会自动下载所需的 Gradle 版本和依赖
4. 等待项目同步完成
5. 点击运行按钮即可编译和安装应用

**手动方案：**
1. 确保 Java JDK 17+ 已安装
  2. 设置 JAVA_HOME 环境变量指向 JDK 安装目录
  3. 下载 Gradle 9.0+ 并添加到 PATH
  4. 在项目根目录运行: `gradle build`

### 快速验证
运行项目根目录的 `build.bat` 文件可以检查环境配置状态：

- Android Studio 2022+ 
- JDK 17+
- Android SDK 34
- Gradle 9.0+

## 安装和运行

1. 克隆或下载项目到本地
2. 使用Android Studio打开项目
3. 确保已安装所需的Android SDK和JDK
4. 连接Android设备或启动模拟器
5. 点击运行按钮编译并安装应用

## 游戏控制

- **开始按钮**: 开始新游戏
- **暂停/继续**: 暂停或继续游戏
- **左/右按钮**: 水平移动方块
- **旋转按钮**: 旋转方块
- **加速下落**: 快速下落方块

## 游戏规则

1. 方块从顶部随机生成并下落
2. 使用控制按钮移动和旋转方块
3. 当一行被完全填满时，该行消除并获得分数
4. 消除行数越多，获得的分数越高
5. 随着等级提升，方块下落速度加快
6. 当方块堆叠到顶部无法放置新方块时，游戏结束

## 分数系统

- 消除1行: 100 × 当前等级
- 消除2行: 300 × 当前等级  
- 消除3行: 500 × 当前等级
- 消除4行: 800 × 当前等级

每获得1000分，等级提升1级，方块下落速度加快

## 技术实现

- **语言**: Kotlin
- **架构**: MVC模式
- **视图**: 自定义View绘制游戏界面
- **逻辑**: 独立的游戏引擎处理方块移动和碰撞检测
- **数据**: 二维数组表示游戏棋盘状态

## 开发说明

项目使用标准的Android开发架构，易于扩展和维护。主要类包括：

- `MainActivity`: 处理用户界面交互和游戏状态管理
- `GameView`: 负责游戏渲染、方块移动和碰撞检测
- `Block`: 定义方块形状和旋转逻辑

## 许可证

MIT License