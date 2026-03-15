# Spring Boot Architecture Reference

## Standard Package Structure

```
src/main/java/com/{company}/{app}/
├── {feature}/
│   ├── controller/
│   │   └── {Feature}Controller.java
│   ├── service/
│   │   ├── {Feature}Service.java          (interface)
│   │   └── {Feature}ServiceImpl.java      (implementation)
│   ├── repository/
│   │   └── {Feature}Repository.java
│   ├── entity/
│   │   └── {Feature}.java
│   ├── dto/
│   │   ├── {Feature}RequestDTO.java
│   │   └── {Feature}ResponseDTO.java
│   └── mapper/
│       └── {Feature}Mapper.java
│
├── common/
│   ├── exception/
│   │   ├── GlobalExceptionHandler.java
│   │   ├── ResourceNotFoundException.java
│   │   └── BadRequestException.java
│   ├── response/
│   │   └── ApiResponse.java               (standard response wrapper)
│   └── constants/
│       └── AppConstants.java
│
└── config/
    ├── SecurityConfig.java
    └── AppConfig.java
```

---

## File Checklist Per Feature

Every new feature MUST include all of these:

- [ ] `{Feature}Controller.java` — REST endpoints only, no logic
- [ ] `{Feature}Service.java` — interface defining contracts
- [ ] `{Feature}ServiceImpl.java` — business logic implementation
- [ ] `{Feature}Repository.java` — extends `JpaRepository<Entity, ID>`
- [ ] `{Feature}.java` (entity) — JPA-annotated domain model
- [ ] `{Feature}RequestDTO.java` — incoming data shape
- [ ] `{Feature}ResponseDTO.java` — outgoing data shape
- [ ] `{Feature}Mapper.java` — maps entity ↔ DTO (use MapStruct or manual)

---

## Controller Rules

```java
@RestController
@RequestMapping("/api/v1/{feature}")
@RequiredArgsConstructor
public class {Feature}Controller {

    private final {Feature}Service {feature}Service;

    @PostMapping
    public ResponseEntity<ApiResponse<{Feature}ResponseDTO>> create(
            @Valid @RequestBody {Feature}RequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success({feature}Service.create(request)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<{Feature}ResponseDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success({feature}Service.findById(id)));
    }
}
```

**Rules:**
- Controller methods must be ≤ 5 lines of logic
- Always wrap responses in `ApiResponse<T>`
- Always use `@Valid` on request body params
- Use constructor injection (`@RequiredArgsConstructor`) — never `@Autowired` on fields

---

## Service Layer Rules

```java
public interface {Feature}Service {
    {Feature}ResponseDTO create({Feature}RequestDTO request);
    {Feature}ResponseDTO findById(Long id);
    List<{Feature}ResponseDTO> findAll();
    {Feature}ResponseDTO update(Long id, {Feature}RequestDTO request);
    void delete(Long id);
}

@Service
@RequiredArgsConstructor
@Transactional
public class {Feature}ServiceImpl implements {Feature}Service {

    private final {Feature}Repository {feature}Repository;
    private final {Feature}Mapper {feature}Mapper;

    @Override
    public {Feature}ResponseDTO create({Feature}RequestDTO request) {
        {Feature} entity = {feature}Mapper.toEntity(request);
        {Feature} saved = {feature}Repository.save(entity);
        return {feature}Mapper.toResponseDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public {Feature}ResponseDTO findById(Long id) {
        {Feature} entity = {feature}Repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("{Feature}", "id", id));
        return {feature}Mapper.toResponseDTO(entity);
    }
}
```

**Rules:**
- Always define interface + impl pair
- `@Transactional(readOnly = true)` on all read methods
- Never return raw entities — always map to DTO before returning
- Throw typed exceptions (`ResourceNotFoundException`, not generic `RuntimeException`)

---

## Repository Rules

```java
@Repository
public interface {Feature}Repository extends JpaRepository<{Feature}, Long> {

    Optional<{Feature}> findByName(String name);
    List<{Feature}> findByStatusAndCreatedAtAfter(Status status, LocalDateTime date);
    
    @Query("SELECT f FROM {Feature} f WHERE f.active = true")
    List<{Feature}> findAllActive();
}
```

**Rules:**
- Extend `JpaRepository` — never write raw JDBC unless performance-critical with justification
- Use Spring Data derived query methods for simple queries
- Use `@Query` only for complex queries that can't be expressed as derived methods
- Never inject repository directly into controller

---

## DTO Pattern

```java
// Request DTO — validation annotations required
public record {Feature}RequestDTO(
    @NotBlank(message = "Name is required")
    @Size(max = 100)
    String name,

    @NotNull
    @Positive
    BigDecimal price
) {}

// Response DTO — clean data contract, no sensitive fields
public record {Feature}ResponseDTO(
    Long id,
    String name,
    BigDecimal price,
    LocalDateTime createdAt
) {}
```

**Rules:**
- Use Java records for DTOs (immutable by default)
- Request DTOs must have Bean Validation annotations
- Response DTOs must never include passwords, internal flags, or full entity graphs
- No `@Entity` annotations on DTOs, ever

---

## Exception Handling

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidation(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                    FieldError::getField,
                    FieldError::getDefaultMessage
                ));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Validation failed", errors));
    }
}
```

**Rules:**
- One `@RestControllerAdvice` class handles ALL exceptions — never try-catch in controllers
- Always return `ApiResponse<T>` even for errors
- Never expose stack traces in API responses

---

## ApiResponse Wrapper

```java
@Data
@Builder
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private LocalDateTime timestamp;

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true).data(data)
                .timestamp(LocalDateTime.now()).build();
    }

    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .success(false).message(message)
                .timestamp(LocalDateTime.now()).build();
    }
}
```
