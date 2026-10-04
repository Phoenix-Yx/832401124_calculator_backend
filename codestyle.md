# 后端代码规范（Back-end Code Style）

本项目的代码规范来源如下（Sources）：

1. **Google Java Style Guide**
   https://google.github.io/styleguide/javaguide.html

2. **Oracle Java Code Conventions**
   https://www.oracle.com/java/technologies/javase/codeconventions-contents.html

3. **Spring Boot 官方文档 - 编码建议**
   https://docs.spring.io/spring-boot/docs/current/reference/html/

以下规范是在上述官方标准基础上，结合本项目实际情况提炼出来的具体约定。

---

## 1. 缩进与空格（Indentation & Whitespace）

- 统一使用 **4 个空格** 缩进，禁止使用 Tab。
- 一行代码不超过 **100 个字符**。
- 左花括号 `{` 与语句同行，不换行：
  ```java
  public class CalculatorApplication {
  ```
- 运算符两侧加空格：`a + b`、`x == y`。
- 方法参数逗号后加空格：`foo(a, b, c)`。
- `if / for / while` 后必须跟空格再接括号。

---

## 2. 命名规范（Naming Conventions）

| 类型 | 规范 | 示例 |
|------|------|------|
| 类名 | UpperCamelCase | `CalculationHistory`、`ExpressionParser` |
| 接口名 | UpperCamelCase | `CalculationHistoryRepository` |
| 方法名 | lowerCamelCase | `calculate`、`deleteById` |
| 变量名 | lowerCamelCase | `expression`、`result` |
| 常量 | UPPER_SNAKE_CASE | `MAX_LENGTH`、`DEFAULT_PORT` |
| 包名 | 全小写 | `com.yx.calculator.parser` |
| 抽象类 | 以 `Abstract` 开头 | `AbstractParser` |
| 异常类 | 以 `Exception` 结尾 | `ParseException` |

**包名禁止出现大写字母和连字符。**

---

## 3. 包结构（Package Structure）

按职责分层，各包职责清晰、不重叠：

- `controller/`：REST 接口。只接收请求、调用 service、返回响应，**不写业务逻辑**。
- `service/`：业务逻辑。计算、事务、调用 repository。
- `repository/`：数据访问。继承 `JpaRepository`，不写实现类。
- `model/`：实体类。对应数据库表。
- `parser/`：表达式解析。独立模块，不依赖 Spring 框架。
- `dto/`：请求/响应对象。不参与持久化。
- `exception/`：全局异常处理。

**禁止**：
- 跨层直接调用（如 controller 直接调 repository）。
- 在 `model` 包里写业务逻辑。
- `parser` 包引入 Spring 依赖（保持纯净、可独立测试）。

---

## 4. 注释（Comments）

- 每个 **public 类**、**public 方法** 必须有 Javadoc：
  ```java
  /**
   * 解析并计算数学表达式。
   *
   * @param expression 待计算的表达式，如 "(1+2)*3"
   * @return 计算结果
   * @throws ParseException 表达式非法或除零时抛出
   */
  public static double evaluate(String expression) { ... }
  ```
- 复杂算法（如递归下降解析）必须写清文法与思路。
- 行内注释解释"为什么"，而不是"是什么"。
- **禁止保留注释掉的废弃代码**。

---

## 5. 方法设计（Method Design）

- 单个方法不超过 **60 行**。
- 单个方法只做一件事（单一职责）。
- 嵌套层级不超过 **4 层**。
- 方法参数不超过 **5 个**，超出时封装为对象。
- 减少副作用：尽量纯函数式处理核心逻辑（如 `ExpressionParser.evaluate`）。

---

## 6. 异常处理（Exception Handling）

- **业务异常自定义**：如 `ParseException extends RuntimeException`。
- **统一由 `@RestControllerAdvice` 捕获**，返回标准 JSON：
  ```json
  { "success": false, "message": "除数不能为零" }
  ```
- **禁止空捕获**：
  ```java
  try { ... } catch (Exception e) {}   // ✗ 禁止
  ```
- 使用合适的 HTTP 状态码：
    - 200 OK
    - 201 Created
    - 204 No Content
    - 400 Bad Request
    - 404 Not Found
    - 500 Internal Server Error
- **不向用户暴露堆栈信息**，只返回友好 message。

---

## 7. 数据库与 JPA 规范

- 使用 **Spring Data JPA**，不写原生 SQL（除性能必要场景）。
- 实体类加 `@Entity`，主键加 `@Id` + `@GeneratedValue`。
- 字段用 `@Column` 明确标注 `nullable`、`length`、`name`：
  ```java
  @Column(nullable = false, length = 512)
  private String expression;
  ```
- 时间字段统一命名 `createdAt`，对应列名 `created_at`。
- 查询方法使用**方法名派生**（如 `findAllByOrderByCreatedAtDesc`），必要时才用 `@Query`。
- 表结构变更通过 `ddl-auto: update` 或迁移工具管理，**不手动 ALTER**。

---

## 8. 安全（Security）

本项目高度重视安全，以下为**强制规定**：

- **严禁使用**任何可直接执行用户输入的方式：
    - `eval` ✗
    - `ScriptEngine`（Nashorn / GraalJS） ✗
    - `Runtime.exec` / `ProcessBuilder` ✗
    - 动态类加载（`Class.forName` + `newInstance`） ✗
    - 反射调用用户指定的方法 ✗
- **所有表达式解析必须由自定义安全解析器完成**（`parser` 包）。
- 对用户输入做**长度与格式校验**：
    - `@NotBlank`
    - `@Size(max = 256)`
- **不允许**把异常堆栈或内部实现细节返回给前端。

---

## 9. API 响应规范

统一使用如下结构：

**成功：**
```json
{
  "success": true,
  "id": 1,
  "expression": "(1+2)*3",
  "result": 9.0,
  "createdAt": "2026-10-01T10:20:00"
}
```

**失败：**
```json
{
  "success": false,
  "message": "除数不能为零"
}
```

- 字段命名统一 lowerCamelCase。
- 时间使用 ISO 8601 格式。
- HTTP 状态码与业务语义一致。

---

## 10. 依赖注入（Dependency Injection）

- 优先使用**构造器注入**，不使用字段注入 `@Autowired`：
  ```java
  // ✅ 推荐
  private final CalculationHistoryRepository repository;
  public CalculationService(CalculationHistoryRepository repository) {
      this.repository = repository;
  }
  ```
  ```java
  // ✗ 不推荐
  @Autowired
  private CalculationHistoryRepository repository;
  ```
- 依赖字段声明为 `final`。
- 避免循环依赖。

---

## 11. 日志（Logging）

- 使用 **SLF4J** 记录日志，禁止 `System.out.println`。
- 关键操作（如保存历史、删除历史）记录 INFO 级日志。
- 异常记录 ERROR 级并打印堆栈。
- 不在日志中输出敏感信息。

---

## 12. 格式一致性检查

提交前自查：

- [ ] 缩进 4 空格（不是 Tab）
- [ ] 类名 UpperCamelCase
- [ ] 方法/变量名 lowerCamelCase
- [ ] 包名全小写
- [ ] public 方法有 Javadoc
- [ ] 无 `eval` / `ScriptEngine` / 动态执行
- [ ] 异常统一走 `GlobalExceptionHandler`
- [ ] 构造器注入，`final` 字段
- [ ] 无空捕获块
- [ ] 无废弃代码块
- [ ] 使用 SLF4J 而非 `System.ou  t`