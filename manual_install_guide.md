# 手动安装APK指南

## 当前状态
✅ APK构建成功！文件位置：`D:\elsfk\build\outputs\apk\debug\ClassicTetris-debug.apk`

## 安装方法

### 方法1：拖拽安装（最简单）
1. 用USB线连接手机到电脑
2. 在手机上允许"文件传输"模式
3. 打开`D:\elsfk\build\outputs\apk\debug\`文件夹
4. 将`ClassicTetris-debug.apk`文件拖拽到手机存储
5. 在手机上找到该文件并点击安装

### 方法2：ADB手动安装
1. **启用手机开发者选项**：
   - 设置 → 关于手机 → 连续点击"版本号"7次
   - 返回设置 → 系统 → 开发者选项

2. **启用必要权限**：
   - 开启"USB调试"
   - 开启"允许安装未知来源应用"
   - (小米/红米用户) 额外开启"USB安装"和"USB调试(安全设置)"

3. **连接手机**：
   - 用USB线连接手机和电脑
   - 手机上选择"文件传输"模式
   - 当弹出"允许USB调试"时，点击"允许"

4. **安装APK**：
   - 使用以下命令安装（在PowerShell中运行）：
   ```powershell
   adb install D:\elsfk\build\outputs\apk\debug\ClassicTetris-debug.apk
   ```

### 方法3：使用Android Studio
1. 打开Android Studio
2. 点击"Device Manager"
3. 连接手机并确保被识别
4. 点击运行按钮直接安装

## 常见问题解决

### 问题1：安装被阻止
- **现象**："安装被阻止"
- **解决**：设置 → 安全 → 允许安装未知来源应用

### 问题2：INSTALL_FAILED_USER_RESTRICTED
- **现象**：安装被用户取消
- **解决**：
  - 小米/红米：设置 → 更多设置 → 开发者选项 → USB安装
  - 华为：设置 → 安全 → 更多安全设置 → 安装未知应用

### 问题3：手机未识别
- **检查**：更换USB线，使用原装线
- **检查**：更换USB端口
- **检查**：重启手机和电脑

## 验证安装
安装完成后，在手机应用列表中查找"ClassicTetris"应用并打开测试。