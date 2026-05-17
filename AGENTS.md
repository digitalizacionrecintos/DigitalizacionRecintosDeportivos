# AGENTS.md - Recintos-Backend

This document provides guidance for AI agents working with the Recintos-Backend codebase.

## Project Overview

- **Stack**: Spring Boot 3.5.7, Java 17, PostgreSQL
- **Package**: `com.recintos.municipalidad`
- **Architecture**: Layered (Controller → Service → Repository → Model)
- **Build Tool**: Gradle (wrapper included: `./gradlew`)

---

## Build, Test, and Run Commands

### Build & Run
```bash
# Build project
./gradlew build

# Run application
./gradlew bootRun

# Clean build artifacts
./gradlew clean
```

### Testing
```bash
# Run all tests
./gradlew test

# Run a single test class
./gradlew test --tests "com.recintos.municipalidad.service.impl.ServicioEstadisticaImplTest"

# Run a single test method
./gradlew test --tests "com.recintos.municipalidad.service.impl.ServicioEstadisticaImplTest.obtenerEstadisticas_ShouldReturnCorrectData"

# Run tests with verbose output
./gradlew test --info

# Check test report (HTML)
open build/reports/tests/test/index.html
```

### Database (Docker)
```bash
# Start PostgreSQL container
docker-compose up -d db

# Start full stack (app + db)
docker-compose up --build
```

---

## Code Style Guidelines

### 1. Package Structure
```
com.recintos.municipalidad/
├── config/          # Configuration classes
├── controller/      # REST controllers
│   └── dto/         # Request/Response DTOs
├── exception/       # Custom exceptions
├── mappers/         # MapStruct mappers
├── model/           # JPA entities
│   └── enums/      # Enum types
├── repository/      # JPA repositories
└── service/        # Service interfaces
    └── impl/        # Service implementations
```

### 2. Naming Conventions

| Element | Convention | Example |
|---------|------------|---------|
| Classes | PascalCase (Spanish) | `EventoController`, `ServicioEvento` |
| Methods | camelCase (Spanish) | `crearEvento()`, `listarEventos()` |
| Variables | camelCase | `eventoCreado`, `dto_request_event` |
| Packages | lowercase | `controller.dto` |
| Enums | UPPER_SNAKE_CASE | `BORRADOR`, `EN_PROGRESO` |
| DTOs | Sufijo DTO | `CrearEventoDTO`, `ResponseEventoDTO` |

### 3. Service Layer Pattern

**Interface** (in `service/`):
```java
public interface ServicioEvento {
    public List<Evento> listarEventos();
    public Optional<Evento> buscarEvento(Long idEvento);
}
```

**Implementation** (in `service/impl/`):
```java
@Service
public class ServicioEventoImpl implements ServicioEvento {
    @Autowired
    private RepositorioEvento repositorioEvento;
    
    @Override
    public List<Evento> listarEventos() {
        return repositorioEvento.findAll();
    }
}
```

### 4. Controller Pattern
```java
@RestController
@RequestMapping("/api/event")
public class EventoController {
    @Autowired
    private ServicioEvento servicioEvento;

    @PostMapping("/create")
    public ResponseEntity<ResponseEventoDTO> crearEvento(@RequestBody CrearEventoDTO dto) {
        ResponseEventoDTO eventoCreado = servicioEvento.guardarEvento(dto);
        return new ResponseEntity<>(eventoCreado, HttpStatus.CREATED);
    }
}
```

### 5. Entity Conventions
- Use `@Data` and `@NoArgsConstructor` from Lombok
- Always specify table name: `@Table(name = "evento")`
- Use `@JsonIgnore` on bidirectional relationships to prevent cycles
- Use `@Transient` for computed fields (e.g., `inscritos` count)

### 6. DTO Conventions
- Use `@Data` Lombok annotation
- Keep DTOs flat (avoid nested entities for API responses)
- Separate Create/Update/Response DTOs when fields differ
- Use descriptive names: `CrearEventoDTO`, `EditarEventoDTO`, `ResponseEventoDTO`

### 7. Repository Conventions
- Extend `JpaRepository<Entity, Long>`
- Use Spring Data derived queries: `findByEstado()`, `findByEncargado()`
- Use `@Query` for complex JPQL queries
- Prefix custom query params with `@Param`

### 8. MapStruct Mappers
- Use `componentModel = "spring"` for dependency injection
- Define mappings at method level with `@Mapping`
- Use `@AfterMapping` for complex transformations
- Keep mappers in `mappers/` package, implement `IGenericMapper`

### 9. Error Handling
- Use custom exceptions: `UsuarioYaInscritoException`, `SinCupoException`
- Global handler in `GlobalExceptionHandler` with `@ControllerAdvice`
- Return `ResponseEntity<ErrorResponse>` with appropriate HTTP status
- Throw `RuntimeException` with descriptive messages

### 10. Dependency Injection
- Use `@Autowired` on fields (existing convention)
- Alternative: constructor injection for testability

### 11. Import Organization
```java
// 1. Same package
// 2. java.* imports
// 3. org.springframework.* imports
// 4. com.recintos.municipalidad.* imports (grouped by package depth)
```

### 12. Formatting Rules
- 4 spaces for indentation (standard Java convention)
- Opening brace on same line
- One blank line between method declarations
- No blank lines within method body except for logical separation
- Line length: reasonable (not enforced, but avoid excessive width)

---

## Testing Guidelines

### Test Structure
```java
public class ServicioEstadisticaImplTest {
    @Mock
    private RepositorioEvento repositorioEvento;
    
    @InjectMocks
    private ServicioEstadisticaImpl servicioEstadistica;
    
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    
    @Test
    public void testName_ShouldDescribeExpectedBehavior() {
        // given
        when(repositorioEvento.findAll()).thenReturn(Arrays.asList(e1, e2));
        
        // when
        EstadisticasResponseDTO result = servicioEstadistica.obtenerEstadisticas(null);
        
        // then
        assertNotNull(result);
        assertEquals(2, result.getResumen().getTotalEventos());
    }
}
```

### Test Naming
- Format: `methodName_Scenario_ExpectedBehavior`
- Example: `obtenerEstadisticas_ShouldReturnCorrectData`

---

## Common Patterns

### Optional Handling
```java
// With ifPresent
Optional<Evento> eventoOpt = repositorioEvento.findById(id);
eventoOpt.ifPresent(evento -> {
    long count = repositorioInscripcion.countByEvento(evento);
    evento.setInscritos(count);
});

// With orElseThrow
Evento evento = repositorioEvento.findById(id)
    .orElseThrow(() -> new RuntimeException("Evento no encontrado"));
```

### Null-Safe Operations
```java
if (eventoDTO.getEncargadoId() != null) {
    Optional<Usuario> encargado = repositorioUsuario.findById(eventoDTO.getEncargadoId());
    encargado.ifPresent(nuevoEvento::setEncargado);
}
```

---

## Important Notes

- **No linter/formatter configured**: Code style is manual. Follow existing patterns.
- **Spanish naming**: Methods and classes use Spanish (e.g., `crearEvento`, `eliminarEvento`)
- **Firebase config**: Requires `firebase-service-account.json` in `src/main/resources/`
- **Database migrations**: Uses Hibernate `ddl-auto=update` (dev mode). Use migrations for production.
- **Scheduled tasks**: Use `@Scheduled` with `@EnableScheduling` enabled in application
