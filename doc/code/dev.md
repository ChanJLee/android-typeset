# 开发规范

## 编码规范

1. 标识符使用驼峰命名（camelCase）
2. 资源文件使用小写字母加下划线的方式命名
3. 日志使用 Texas 内部的日志工具打点，不要直接使用 `android.util.Log`
4. 内部 API 请使用 `@Hidden` 进行标注
5. 每添加一个新组件，在对应的 test 目录下创建测试代码；修改了 API 也需要补充测试并通过后才能提交
6. 定期使用 lint 进行静态检查

## 常用命令

```bash
# 构建 library（产出 AAR）
./gradlew :library:assemble

# 构建 demo 应用
./gradlew :app:assembleDebug

# 运行单元测试
./gradlew :library:test

# 运行单个测试类
./gradlew :library:test --tests me.chan.texas.misc.BitBucket8UnitTest

# 运行仪器测试（需要连接设备/模拟器）
./gradlew :library:connectedAndroidTest

# 静态检查
./gradlew :library:lint
```

## 配套工具

性能分析（async-profiler）与排版质量可视化工具的使用方法见 [tools.md](../tools/tools.md)。
