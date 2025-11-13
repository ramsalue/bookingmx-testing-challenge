# System Diagrams - BookingMx Testing Challenge

## Sprint 3: Complete System Documentation

---

## Available Diagrams

### Architecture Diagrams
- **[system-architecture.png](system-architecture.png)** - Overall system architecture
  - Shows both Java and JavaScript modules
  - Layers and components
  - Testing infrastructure

### Class Diagrams
- **[java-class-diagram.png](java-class-diagram.png)** - Java module classes
  - Entity classes (Reservation, RoomType, ReservationStatus)
  - Service layer
  - Repository pattern
  - Utility classes
  - Exceptions

### Component Diagrams
- **[javascript-graph-diagram.png](javascript-graph-diagram.png)** - JavaScript module components
  - Graph class structure
  - Dijkstra's algorithm flow
  - Data structure representation
  - Component operations

### Flow Diagrams
- **[data-flow-diagram.png](data-flow-diagram.png)** - Complete data flow
  - Request/response flow
  - Module interactions
  - Data processing pipeline

- **[testing-workflow.png](testing-workflow.png)** - Testing process
  - Test execution flow
  - Coverage analysis
  - Quality gates

---
## Diagram Descriptions

### System Architecture
Shows the complete system with:
- Sprint 1: Java Reservations Module (3 layers)
- Sprint 2: JavaScript Graph Module
- Testing infrastructure (JUnit + Jest)
- Coverage analysis tools (JaCoCo + Jest)

### Java Class Diagram
Illustrates:
- Domain model with Reservation entity
- Enumerations (RoomType, ReservationStatus)
- Service layer (ReservationService)
- Repository pattern (interface + implementation)
- Utility classes (DateValidator, PriceCalculator, AvailabilityChecker)
- Custom exceptions

### JavaScript Component Diagram
Details:
- Graph class with adjacency list
- City management operations
- Connection management
- Search algorithms (Dijkstra, BFS)
- Data structure visualization

### Data Flow Diagram
Demonstrates:
- User input processing
- Validation layers
- Business logic execution
- Data storage access
- Response generation
- Error handling paths

### Testing Workflow
Explains:
- Test execution process
- Coverage calculation
- Quality gate validation
- Continuous integration flow

---
**Last Updated**: November 2025  
**Version**: 2.4.0  
**Sprint**: 3 