# daily-exercise

> 基于 Android + Spring Boot 的全栈项目

## 📁 项目结构

```
daily-exercise/
├── android/
│   └── MyApplication/     # Android 客户端 (Android Studio)
│       ├── app/           # 应用模块
│       ├── .gradle/       # Gradle 构建缓存
│       ├── .idea/         # IntelliJ IDEA 配置
│       └── .gitignore
├── server/
│   └── daily-exercise-auth/  # 后端服务 (Spring Boot)
│       ├── src/main/java/    # Java 源代码
│       ├── src/main/resources/
│       │   └── application.properties  # 应用配置
│       ├── pom.xml           # Maven 依赖管理
│       └── mvnw*             # Maven 包装器
├── .gitignore
└── README.md
```

------

## 🛠 技术栈

### Mobile (Android)

- Android Studio
- Java / Kotlin
- Retrofit / OkHttp
- MVVM 架构

### Backend (Server)

- IntelliJ IDEA
- Java 17
- Spring Boot 3.2.5
- MyBatis
- MySQL 8.0

------

## 🚀 快速开始

### 1️⃣ 克隆项目

```bash
git clone https://github.com/q62402438-cloud/MyProject.git
cd daily-exercise
```

------

### 2️⃣ 数据库配置

```bash
# 创建数据库
mysql -u root -p
CREATE DATABASE daily_exercise_db CHARACTER SET utf8mb4;
```

数据库连接信息（位于 `server/daily-exercise-auth/src/main/resources/application.properties`）：
- 数据库名：`daily_exercise_db`
- 用户名：`root`
- 密码：`123456`
- 端口：`3306`

------

### 3️⃣ 启动后端服务

```bash
cd server/daily-exercise-auth
./mvnw spring-boot:run
# 或使用 Maven
mvn spring-boot:run
```

后端接口地址：
```
http://localhost:8082/
```

------

### 4️⃣ 启动 Android 客户端

1. 使用 Android Studio 打开 `android/MyApplication/` 目录
2. 配置 `local.properties`（如有需要）
3. 连接真机或模拟器
4. 点击 Run ▶️

------

## 🔐 安全说明

⚠️ **请勿将敏感信息提交到 GitHub**

- 数据库密码
- API Key
- 服务器 IP

推荐使用环境变量或本地配置文件管理。

------

## 📜 License

本项目采用 [MIT License](https://yuanbao.tencent.com/chat/naQivTmsDa/LICENSE)

------

## 👤 作者

- GitHub: https://github.com/q62402438-cloud/
- Email: 1841205609@qq.com

------

> ✨ 如果这个项目对你有帮助，欢迎 Star ⭐️