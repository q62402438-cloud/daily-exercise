# 日常运动监测系统

> 基于 **Android + Spring Boot + Web** 构建的全栈运动管理平台，提供训练计划管理、运动打卡、社区互动等核心功能。

---

## 📁 项目结构

```
daily-exercise/
├── android/                    # Android 移动端
│   └── MyApplication/         # Android Studio 项目
│       ├── app/src/main/java/com/example/myapplication/
│       │   ├── Activity类     # 页面活动（首页、计划管理、帖子、收藏等）
│       │   ├── Adapter类      # 数据适配器
│       │   └── Entity类       # 数据实体
│       └── app/src/main/res/  # 资源文件（布局、图标、样式）
├── server/                     # 后端服务
│   └── daily-exercise-auth/   # Spring Boot 项目
│       ├── src/main/java/com/example/dailyexerciseauth/
│       │   ├── controller/    # REST API 控制层
│       │   ├── service/       # 业务逻辑层
│       │   ├── mapper/        # MyBatis 数据访问层
│       │   ├── entity/        # 数据库实体
│       │   └── config/        # 配置类
│       └── src/main/resources/
│           └── application.properties  # 应用配置
├── web/                       # Web 前端
│   ├── asserts/               # 静态资源（CSS、JS、图片）
│   ├── components/            # 可复用组件（header、footer）
│   └── pages/                 # 页面目录
│       ├── admin/             # 管理员页面
│       ├── plan/              # 计划管理
│       ├── post/              # 帖子管理
│       ├── record/            # 打卡记录
│       ├── user/              # 用户管理
│       ├── collect/           # 收藏管理
│       ├── favoriate/         # 收藏帖子
│       └── comment/           # 评论管理
├── docs/                      # 项目文档
│   ├── 开题报告.docx          # 项目立项文档
│   ├── 需求获取报告.docx      # 需求调研报告
│   ├── 需求分析报告.docx       # 功能需求分析
│   ├── 系统设计报告.docx      # 系统架构设计
│   ├── 数据库设计报告.doc     # 数据库结构设计
│   ├── 项目计划.doc           # 项目进度计划
│   ├── 系统测试计划.doc       # 测试计划文档
│   ├── 结项报告.doc           # 项目总结报告
│   ├── 建表.sql              # 数据库建表语句
│   └── 数据.sql              # 初始测试数据
├── .gitignore
└── README.md
```

---

## 🛠 技术栈

### 移动端 (Android)
| 技术 | 版本 | 说明 |
|------|------|------|
| Android Studio | Hedgehog | 开发IDE |
| Java | 17 | 编程语言 |
| Retrofit | 2.9.0 | HTTP 客户端 |
| OkHttp | 4.12.0 | 网络请求 |
| MVVM | - | 架构模式 |
| Glide | 4.12.0 | 图片加载 |

### 后端服务 (Server)
| 技术 | 版本 | 说明 |
|------|------|------|
| Java | 17 | 编程语言 |
| Spring Boot | 3.2.5 | 应用框架 |
| MyBatis | 3.0.3 | ORM 框架 |
| MySQL | 8.0+ | 数据库 |
| Maven | 3.9+ | 依赖管理 |

### Web 前端
| 技术 | 说明 |
|------|------|
| HTML5 | 页面结构 |
| CSS3 | 样式设计 |
| JavaScript (ES6+) | 交互逻辑 |
| Python HTTP Server | 开发服务器 |

---

## 🚀 快速开始

### 1️⃣ 环境准备

确保系统已安装：
- **JDK 17+** - Java 开发环境
- **MySQL 8.0+** - 数据库
- **Python 3** - Web 前端开发服务器
- **Android Studio** - Android 开发（可选）

### 2️⃣ 数据库配置

```sql
-- 创建数据库
CREATE DATABASE daily_exercise_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE daily_exercise_db;

-- 导入建表语句
SOURCE docs/建表.sql;

-- 导入测试数据（可选）
SOURCE docs/数据.sql;
```

**连接配置** (`server/daily-exercise-auth/src/main/resources/application.properties`)：
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/daily_exercise_db?useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=123456
```

### 3️⃣ 启动后端服务

**Windows:**
```bash
cd server/daily-exercise-auth
.\mvnw.cmd spring-boot:run
```

**Linux/Mac:**
```bash
cd server/daily-exercise-auth
./mvnw spring-boot:run
```

或使用启动脚本：
```bash
# Windows
start_server.bat

# Linux/Mac
chmod +x start_server.sh && ./start_server.sh
```

后端服务地址：`http://localhost:8082/`

### 4️⃣ 启动 Web 前端

**Windows:**
```bash
cd web
python -m http.server 8081
```

**Linux/Mac:**
```bash
cd web
python3 -m http.server 8081
```

Web 访问地址：`http://localhost:8081/`

**主要页面:**
- 首页: `index.html`
- 用户登录: `pages/user/signin.html`
- 用户注册: `pages/user/signupOrdinaryUser.html`
- 我的计划: `pages/plan/MyPlans.html`
- 管理员首页: `pages/admin/index.html`

### 5️⃣ 启动 Android 客户端

1. 使用 Android Studio 打开 `android/MyApplication/`
2. 配置 `local.properties`（指定 SDK 路径）
3. 连接 Android 设备或启动模拟器
4. 点击 **Run** ▶️ 按钮运行

---

## ✅ 完整启动流程 (Windows)

```bash
# 终端 1 - 启动后端服务（建议使用IntelliJ IDEA打开）
cd server/daily-exercise-auth
.\mvnw.cmd spring-boot:run

# 终端 2 - 启动 Web 前端
cd web
python -m http.server 8081

# 使用 Android Studio 打开 Android 项目（可选）
```

---

## 📱 功能特性

### 👤 用户功能
- **用户管理** - 注册、登录、修改密码、个人信息管理
- **计划管理** - 创建、编辑、删除训练计划，查看计划详情和完成进度
- **运动打卡** - 每日打卡记录，查看历史记录
- **社区互动** - 发布帖子、评论互动、点赞收藏
- **搜索功能** - 搜索计划、帖子、用户

### 🔧 管理员功能
- **内容审核** - 审核训练计划、审核帖子

### 📊 计划管理
- 支持多种运动类型（跑步、健身、瑜伽等）
- 自动计算完成进度
- 日期范围管理
- 自动标记已完成计划

### 💾 数据持久化
- 所有数据存储于 MySQL 数据库
- 支持数据导入导出

---

## 🔌 API 接口

### 用户认证
| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/user/login` | POST | 用户登录 |
| `/api/user/register` | POST | 用户注册 |
| `/api/user/logout` | POST | 用户退出 |

### 训练计划
| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/plan/list` | GET | 获取计划列表 |
| `/api/plan/detail/{id}` | GET | 获取计划详情 |
| `/api/plan/create` | POST | 创建计划 |
| `/api/plan/update` | PUT | 更新计划 |
| `/api/plan/delete/{id}` | DELETE | 删除计划 |

### 运动记录
| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/record/checkin` | POST | 打卡记录 |
| `/api/record/list` | GET | 获取打卡列表 |

---

## 🔐 安全说明

⚠️ **请勿将敏感信息提交到版本控制系统**

- 数据库密码
- API Key 和密钥
- 服务器 IP 地址
- 配置文件中的敏感配置

**推荐做法:**
1. 使用环境变量管理敏感配置
2. 将配置文件加入 `.gitignore`
3. 使用配置中心管理生产环境配置

---

## 📜 License

本项目采用 [MIT License](https://opensource.org/licenses/MIT)

---

## 👤 作者

- GitHub: [q62402438-cloud](https://github.com/q62402438-cloud/)
- Email: 1841205609@qq.com

---

> ✨ 如果这个项目对你有帮助，欢迎 Star ⭐️