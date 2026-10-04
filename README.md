# Calculator Backend

前后端分离计算器系统 —— 后端部分

---

## 1. 项目介绍（Project Introduction）

本项目是"前后端分离计算器系统"的**后端**，基于 Spring Boot 开发。

后端负责：
- 接收前端发送的表达式
- 校验输入（非空、长度限制）
- **安全地解析**数学表达式（**不使用 eval / ScriptEngine**）
- 按数学规则计算（运算符优先级、括号、小数、一元正负号）
- 处理异常（非法表达式、除零、括号不匹配）
- 把成功的计算记录持久化到数据库
- 提供历史记录的查询、删除接口
- 返回统一的 JSON 响应

**核心计算逻辑完全在后端**，前端只负责收集和展示。

---

## 2. 技术栈（Technology Stack）

- Java 17
- Spring Boot 3.2.5
- Spring Web
- Spring Data JPA
- Spring Validation
- H2 文件数据库（可切换 MySQL）
- Maven 构建

---

## 3. 运行环境（Runtime Environment）

- JDK 17 或更高版本
- Maven 3.8 或更高版本
- 无需额外安装数据库（H2 内嵌，文件模式）

---

## 4. 安装方法（Installation Method）

### 4.1 环境准备

- 安装 JDK 17：https://adoptium.net/
- 安装 Maven：https://maven.apache.org/download.cgi

验证：

```bash
java -version    # 应显示 17 或更高
mvn -version     # 应显示 3.8 或更高
```

### 4.2 获取本项目

```bash
git clone https://github.com/你的用户名/832401124_calculator_backend.git
cd 832401124_calculator_backend
```

---

## 5. 启动方法（Startup Method）

### 方式一：Maven 命令

```bash
mvn clean package
java -jar target/calculator-backend-1.0.0.jar
```

### 方式二：IDE 直接运行（推荐）

1. 用 IntelliJ IDEA 打开本项目
2. 等待 Maven 依赖下载完成
3. 找到 `src/main/java/com/yx/calculator/CalculatorApplication.java`
4. 点击 `main` 方法左侧的绿色三角 ▶ 运行

### 启动成功标志

控制台输出：

```
Tomcat started on port(s): 8080 (http)
Started CalculatorApplication in X.XXX seconds
```

默认端口：**8080**

---

## 6. 配置说明（Configuration Instructions）

所有配置在 `src/main/resources/application.yml`：

```yaml
spring:
  application:
    name: calculator
  datasource:
    url: jdbc:h2:file:./data/calculator;DB_CLOSE_DELAY=-1;AUTO_SERVER=TRUE
    driver-class-name: org.h2.Driver
    username: sa
    password:
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false
    properties:
      hibernate:
        format_sql: true
  h2:
    console:
      enabled: true
      path: /h2-console

server:
  port: 8080
```

### 修改端口

把 `server.port` 改成其他值（如 8081），同时记得修改前端 `API_BASE`。

### 切换为 MySQL

1. 在 `pom.xml` 中加入 MySQL 驱动依赖
2. 修改 `application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/calculator?useSSL=false&serverTimezone=UTC
    driver-class-name: com.mysql.cj.jdbc.Driver
    username: root
    password: 你的密码
```

---

## 7. 数据库初始化方法（Database Initialization）

### 默认方案：H2 文件数据库（推荐）

**无需手动初始化**，程序启动时自动完成：

- 数据文件位置：`./data/calculator.mv.db`
- 表结构由 JPA 自动创建（`ddl-auto: update`）
- **重启后数据不丢失**

### 表结构（由 JPA 自动生成）

表名：`calculation_history`

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键，自增 |
| expression | VARCHAR(512) | 计算表达式 |
| result | DOUBLE | 计算结果 |
| created_at | TIMESTAMP | 计算时间 |

### 查看数据库内容

访问 H2 控制台：http://localhost:8080/h2-console

填写：
- **JDBC URL**：`jdbc:h2:file:./data/calculator`
- **User Name**：`sa`
- **Password**：留空

点 **Connect**，即可在网页里查看 `CALCULATION_HISTORY` 表。

---

## 8. 前后端连接方式（Front-end / Back-end Connection Method）

后端以 **RESTful API** 形式提供服务，前端通过 HTTP + JSON 调用。

### 基础路径

`/api`

### 接口一览

| 方法 | 路径 | 说明 | 成功状态码 |
|------|------|------|-----------|
| POST | `/api/calculate` | 计算表达式并保存历史 | 200 |
| GET | `/api/history` | 查询全部历史（按时间倒序）| 200 |
| DELETE | `/api/history/{id}` | 删除指定 id 的历史 | 204 |
| DELETE | `/api/history` | 清空全部历史 | 204 |

### 请求示例（curl）

```bash
curl -X POST http://localhost:8080/api/calculate \
     -H "Content-Type: application/json" \
     -d '{"expression":"(1+2)*3"}'
```

### 成功响应

```json
{
  "success": true,
  "id": 1,
  "expression": "(1+2)*3",
  "result": 9.0,
  "createdAt": "2026-10-01T10:20:00"
}
```

### 错误响应（HTTP 400）

```json
{
  "success": false,
  "message": "除数不能为零"
}
```

### 跨域配置

后端已在 `CalculatorController` 上添加：

```java
@CrossOrigin(origins = "*")
```

允许前端（5501 端口）跨域访问后端（8080 端口）。

---

## 9. 其他必要信息（Other Information）

### 9.1 表达式解析说明（安全实现）

**本项目未使用 `eval`、`ScriptEngine`、动态类加载等任何执行用户输入的方式。**

自行实现了**递归下降解析器**（`parser` 包），流程：

1. **词法分析（Tokenizer）**：将输入串拆为 Token
    - 数字、`+`、`-`、`*`、`/`、`(`、`)`、EOF
    - 检测非法字符、多个小数点

2. **语法分析（ExpressionParser）**：按优先级递归下降
   ```
   expression := term (('+' | '-') term)*
   term       := unary (('*' | '/') unary)*
   unary      := ('+' | '-') unary | primary
   primary    := NUMBER | '(' expression ')'
   ```

3. **支持特性**：
    - 运算符优先级
    - 括号嵌套
    - 小数
    - 一元正负号（如 `-5`、`3 * -2`）
    - 除零检测
    - 括号不匹配检测

4. **异常处理**：统一抛 `ParseException`，由 `GlobalExceptionHandler` 转换为标准 JSON 响应。

### 9.2 项目目录结构

```
832401124_calculator_backend/
├── src/
│   ├── main/
│   │   ├── java/com/yx/calculator/
│   │   │   ├── CalculatorApplication.java      启动类
│   │   │   ├── controller/                     REST 控制器
│   │   │   ├── service/                        业务逻辑
│   │   │   ├── repository/                     JPA 数据访问
│   │   │   ├── model/                          实体类
│   │   │   ├── parser/                         表达式解析器
│   │   │   │   ├── Token.java
│   │   │   │   ├── Tokenizer.java
│   │   │   │   ├── ParseException.java
│   │   │   │   └── ExpressionParser.java
│   │   │   ├── dto/                            请求/响应对象
│   │   │   └── exception/                      全局异常处理
│   │   └── resources/
│   │       └── application.yml                 配置文件
│   └── test/
├── pom.xml
├── README.md
└── codestyle.md
```

### 9.3 API 测试建议

使用 Postman / curl 测试：

**测试 1：基本计算**

```
POST http://localhost:8080/api/calculate
Content-Type: application/json

{"expression":"12+8"}
```

**测试 2：复合表达式**

```
{"expression":"(1+2)*3"}
```

**测试 3：除零错误**

```
{"expression":"1/0"}
```

预期返回 HTTP 400，body 为 `{"success":false,"message":"除数不能为零"}`

**测试 4：查询历史**

```
GET http://localhost:8080/api/history
```

### 9.4 前后端分离验证

按作业要求，可进行以下验证：

1. 启动后端，前端页面正常工作
2. **停掉后端服务**
3. 前端页面仍能打开、按钮仍可点击
4. 但点击 `=` 无法得到计算结果（提示"无法连接后端"）

这证明计算逻辑在后端，符合前后端分离架构要求。

