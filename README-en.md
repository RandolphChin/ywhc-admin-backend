## Project Introduction - Backend

A modern enterprise-level backend management system built with Spring Boot 3.2, Java 21, Spring Security, MyBatis Plus, JWT, and Quasar Framework V2 (Vue3).

## 🌐 Language / 语言

**English** | [中文](README.md)

## 🔗 Quick Navigation

| Project | Description | Link |
|---------|-------------|------|
| 🖥️ **Backend Project** | Spring Boot3 Backend Service | [ywhc-admin-backend](https://github.com/your-org/ywhc-admin/tree/main/ywhc-admin-backend) |
| 🌐 **Frontend Project** | Vue3 + Quasar Framework Frontend Application | [ywhc-admin-frontend](https://github.com/your-org/ywhc-admin/tree/main/ywhc-admin-frontend) |

## 📋 Features

- 👥 **User Management** - User configuration with default password admin123
- 🏢 **Department Management** - Organizational structure management with tree table format
- 🎯 **Role & Permissions** - Flexible RBAC permission control
- 📊 **Data Permissions** - Data scope control (All/Department/Department & Sub/Personal Only)
- 📋 **Menu Management** - Dynamic menu configuration with frontend dynamic menu routing
- 🔐 **Authentication & Authorization** - JWT-based security authentication system
- 📝 **Operation Logs** - Complete system operation audit
- 📚 **Data Dictionary** - System configuration management
- 🖼️ **Slide Captcha** - Secure graphical verification
- 📈 **Online User Monitoring** - Real-time user status management
- 📊 **Code Generation** - Code preview, download, and generate frontend/backend code

## 🛠️ Tech Stack

- **Spring Boot** 3.2.0 - Application framework
- **Spring Security** 6.x - Security framework
- **MyBatis Plus** 3.5.5 - ORM framework
- **MySQL** 8.0+ - Relational database
- **JWT** 0.12.3 - JSON Web Token
- **Hutool** 5.8.22 - Java utility library
- **Apache POI** 5.2.4 - Excel operations
- **Jackson** - JSON processing
- **Knife4j** 4.3.0 - API documentation generation
- **Freemarker** - Template engine
- **Quasar Framework v2** - Frontend Vue3 framework

## 📋 Requirements

### Required Environment
- **JDK** 21 or higher
- **Maven** 3.6+ 
- **MySQL** 8.0+
- **Redis** 6.0+

## 📁 Project Structure

```
ywhc-admin-backend/
├── src/main/java/com/ywhc/admin/
│   ├── YwhcAdminApplication.java    # Main application class
│   ├── common/                      # Common modules
│   │   ├── annotation/              # Custom annotations
│   │   ├── aspect/                  # Aspect processing
│   │   ├── config/                  # Common configuration
│   │   ├── context/                 # Context management
│   │   ├── dto/                     # Common DTOs
│   │   ├── enums/                   # Enum classes
│   │   ├── exception/               # Exception handling
│   │   ├── interceptor/             # Interceptors
│   │   ├── result/                  # Unified response results
│   │   ├── security/                # Security related
│   │   ├── task/                    # Scheduled tasks
│   │   ├── util/                    # Utility classes
│   │   └── utils/                   # Utility classes
│   └── modules/                     # Business modules
│       ├── auth/                    # Authentication module
│       ├── captcha/                 # Captcha module
│       ├── generator/               # Code generation module
│       ├── monitor/                 # System monitoring module
│       ├── system/                  # System management module
│       │   ├── dept/                # Department management
│       │   ├── dict/                # Dictionary management
│       │   ├── log/                 # Log management
│       │   ├── menu/                # Menu management
│       │   ├── role/                # Role management
│       │   └── user/                # User management
│       └── test/                    # Test module
├── src/main/resources/
│   ├── mapper/                      # MyBatis XML mapping files
│   │   ├── system/                  # System module mapping files
│   │   └── test/                    # Test module mapping files
│   ├── templates/                   # Code generation templates
│   │   ├── controller.java.ftl      # Controller template
│   │   ├── entity.java.ftl          # Entity template
│   │   ├── mapper.java.ftl          # Mapper template
│   │   ├── service.java.ftl         # Service template
│   │   ├── vue-page.vue.ftl         # Vue page template
│   │   └── ...                      # Other template files
│   ├── application.yml              # Main configuration file
│   ├── application-dev.yml          # Development environment configuration
│   └── application-prod.yml         # Production environment configuration
├── src/test/                        # Test code
└── pom.xml                          # Maven configuration file
```

## 📸 System Screenshots

### Login Interface
![Login Interface](images/登录.png)

### User Interface
![User Interface](images/用户.png)

### Role Interface
![Role Interface](images/角色.png)

### Dictionary Interface
![Dictionary Interface](images/字典.png)

### Menu Interface
![Menu Interface](images/菜单.png)

### Log Interface
![Log Interface](images/日志.png)

### Code Generation Feature
![Code Generation](images/代码生成.png)

### Business Example
![Business Example](images/业务示例.png)

## 📄 License

This project is open source under the MIT License - see the [LICENSE](LICENSE) file for details.

---

⭐ If this project helps you, please give me a Star!
