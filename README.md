# daily-exercise

> 基于 Android + Spring Boot + Web 的全栈运动管理平台

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
├── web/
│   ├── asserts/         # Web 前端资源
│   │   ├── css/       # 样式文件
│   │   ├── js/        # JavaScript 文件
│   │   └── images/     # 图片资源
│   ├── components/       # 可复用组件
│   └── pages/          # 页面文件
│       ├── admin/    # 管理员页面
│       ├── plan/       # 计划管理页面
│       ├── post/       # 帖子管理页面
│       ├── record/     # 打卡记录页面
│       ├── user/       # 用户管理页面
│       └── index.html   # 首页
├── .gitignore
└── README.md
```

------

## 🛠 技术栈

### Mobile (Android)

- Android Studio
- Java
- Retrofit / OkHttp
- MVVM 架构

### Backend (Server)

- IntelliJ IDEA
- Java 17
- Spring Boot 3.2.5
- MyBatis
- MySQL 8.0

### Web 前端

- 纯 HTML/CSS/JavaScript
- Python HTTP Server (开发环境)

------

## 🚀 快速开始

### 1️⃣ 环境准备

确保您的系统需要安装：
- JDK 17+
- MySQL 8.0+
- Python 3 (用于 Web 前端)
- Android Studio (用于 Android 开发)

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

**重要提示：请根据您的实际 MySQL 密码修改配置文件中的密码。

------

### 3️⃣ 启动后端服务

#### Windows:
```bash
cd server/daily-exercise-auth
.\mvnw.cmd spring-boot:run
```

#### Linux/Mac:
```bash
cd server/daily-exercise-auth
./mvnw spring-boot:run
```

或者使用 Maven:
```bash
mvn spring-boot:run
```

后端接口地址：
```
http://localhost:8082/
```

------

### 4️⃣ 启动 Web 前端

#### Windows:
```bash
cd web
python -m http.server 8081
```

#### Linux/Mac:
```bash
cd web
python3 -m http.server 8081
```

Web 前端访问地址：
```
http://localhost:8081/
```

**Web 主要页面：
- 首页: `index.html`
- 我的计划: `pages/plan/MyPlans.html`
- 用户登录: `pages/user/signin.html`
- 用户注册: `pages/user/signupOrdinaryUser.html`

------

### 5️⃣ 启动 Android 客户端

1. 使用 Android Studio 打开 `android/MyApplication/` 目录
2. 配置 `local.properties` (如有需要)
3. 配置后端服务器地址（如需要修改)
4. 连接真机或模拟器
5. 点击 Run ▶️

Android 客户端功能包括：
- 用户注册/登录
- 训练计划管理
- 运动打卡
- 帖子发布
- 收藏功能

------

## ✅ 完整启动流程 (Windows 示例)

```bash
# 1. 打开终端 1 - 启动后端
cd c:\Users\HP\Downloads\daily-exercise-main\daily-exercise\server\daily-exercise-auth
.\mvnw.cmd spring-boot:run

# 2. 打开终端 2 - 启动 Web 前端
cd c:\Users\HP\Downloads\daily-exercise-main\daily-exercise\web
python -m http.server 8081

# 3. (可选) 使用 Android Studio 打开 Android 项目
```

------

## 📱 功能特性

### 用户功能
- 用户注册/登录
- 创建、编辑、删除训练计划
- 查看计划详情和完成进度
- 运动打卡记录
- 发布和管理帖子
- 收藏计划和帖子
- 评论和互动

### 管理员功能
- 审核训练计划
- 审核帖子
- 用户管理

### 计划管理
- 支持多种运动类型
- 自动计算完成进度
- 支持日期范围管理
- 自动标记已完成计划

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
