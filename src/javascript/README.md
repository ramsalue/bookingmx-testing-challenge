# BookingMx Graph Module - JavaScript

## Sprint 2: Graph Visualization with Jest Testing ✅ COMPLETE

---

## Project Overview

This JavaScript module implements a graph data structure representing cities and distances between them for the BookingMx reservation system. The module enables customers to:

- Visualize nearby destinations
- Calculate optimal travel routes
- Find cities within travel distance
- Plan multi-city trips efficiently

---

## Features

### Core Functionality
-  **Graph Management**: Add, remove, and query cities
-  **Shortest Path Finding**: Dijkstra's algorithm implementation
-  **Nearby Cities Search**: Distance-based filtering
-  **Bidirectional Connections**: Symmetric city connections
-  **Distance Calculations**: Total route distance computation

### Technical Features
-  **ES6+ JavaScript**: Modern syntax and features
-  **Map Data Structure**: Efficient adjacency list
-  **Error Handling**: Comprehensive validation
-  **JSDoc Documentation**: Complete inline comments
-  **71 Jest Tests**: Comprehensive test coverage
-  **99% Coverage**: Exceeds 90% requirement

---

## Project Structure

```
src/javascript/
├── graph/
│   ├── Graph.js              (Main graph class)
│   └── Graph.test.js         (Jest tests - 71 tests)
└── README.md                 (This file)
```

---

##  Quick Start

### Prerequisites
- Node.js 18+ installed
- npm 9+ installed

### Installation

```bash
# Install dependencies
npm install

# Run tests
npm test

# Generate coverage report
npm run test:coverage

# View coverage report
open coverage/lcov-report/index.html
```

---

## Usage Examples

### Basic Usage

```javascript
const Graph = require('./graph/Graph');

// Create a graph
const graph = new Graph();

// Add cities with connections
graph.addCity('Guadalajara', {
  'Mexico City': 540,
  'Puerto Vallarta': 300,
  'Leon': 220
});

graph.addCity('Mexico City', {
  'Guadalajara': 540,
  'Cancun': 1600,
  'Puebla': 130
});

// Check if city exists
console.log(graph.hasCity('Guadalajara')); // true

// Get all cities
console.log(graph.getCities()); 
// ['Guadalajara', 'Mexico City']
```

### Finding Shortest Path

```javascript
// Find optimal route between cities
const route = graph.findShortestPath('Guadalajara', 'Cancun');

console.log(route.path); 
// ['Guadalajara', 'Mexico City', 'Cancun']

console.log(route.distance); 
// 2140 (km)
```

### Finding Nearby Cities

```javascript
// Find cities within 400km of Guadalajara
const nearby = graph.findNearbyCities('Guadalajara', 400);

console.log(nearby);
// [
//   { city: 'Leon', distance: 220 },
//   { city: 'Puerto Vallarta', distance: 300 }
// ]
```

### Managing Connections

```javascript
// Add a city without initial connections
graph.addCity('Cancun');

// Add bidirectional connection
graph.addConnection('Guadalajara', 'Cancun', 2000);

// Get distance between cities
const distance = graph.getDistance('Guadalajara', 'Mexico City');
console.log(distance); // 540

// Remove connection
graph.removeConnection('Guadalajara', 'Cancun');

// Remove city entirely
graph.removeCity('Cancun');
```

---

## Testing

### Run All Tests
```bash
npm test
```
### Generate Coverage Report
```bash
npm run test:coverage
```

### View Detailed Coverage
```bash
# Open in browser
open coverage/lcov-report/index.html

# Or manually navigate to:
# coverage/lcov-report/index.html
```

---

##  Test Coverage

### Coverage Summary
```
File      | % Stmts | % Branch | 
----------|---------|----------|
Graph.js  |   99    |    99    |  
----------|---------|----------|
```
### Coverage Status
**99% Coverage** - Exceeds 90% requirement

---

## Graph Class API

### Constructor
```javascript
const graph = new Graph();
```
Creates an empty graph with no cities or connections.

### City Management
```javascript
// Add city with connections
graph.addCity(cityName, neighbors);

// Remove city
graph.removeCity(cityName);

// Check if city exists
graph.hasCity(cityName);

// Get all cities
graph.getCities();

// Get neighbors of a city
graph.getNeighbors(cityName);
```

### Connection Management
```javascript
// Add bidirectional connection
graph.addConnection(city1, city2, distance);

// Remove connection
graph.removeConnection(city1, city2);

// Get distance between cities
graph.getDistance(fromCity, toCity);
```

### Search Operations
```javascript
// Find cities within distance
graph.findNearbyCities(startCity, maxDistance);

// Find shortest path (Dijkstra's algorithm)
graph.findShortestPath(startCity, endCity);
```

### Utility Methods
```javascript
// Get number of cities
graph.size();

// Check if empty
graph.isEmpty();

// Clear all cities
graph.clear();

// String representation
graph.toString();
```

---

## Dijkstra's Algorithm Explained

The `findShortestPath()` method implements **Dijkstra's algorithm**, which finds the shortest path between two nodes in a weighted graph.

### How It Works:
1. **Initialize**: Set distance to start city as 0, all others as infinity
2. **Visit**: Choose unvisited city with minimum distance
3. **Update**: For each neighbor, calculate new distance through current city
4. **Repeat**: Continue until destination is reached or all reachable cities visited
5. **Reconstruct**: Build path by backtracking through previous nodes

### Example:
```
Finding path from Guadalajara to Cancun:

Graph:
Guadalajara --540km--> Mexico City --1600km--> Cancun
Guadalajara --300km--> Puerto Vallarta

Algorithm steps:
1. Start at Guadalajara (distance: 0)
2. Visit Mexico City (distance: 540)
3. Visit Cancun (distance: 540 + 1600 = 2140)
4. Path found: Guadalajara → Mexico City → Cancun
```

---
**Sprint 2 Status**: COMPLETE  
**Version**: 2.3.0  
**Coverage**: 99%  
**Tests**: 71 passing  
**Last Updated**: November 2025

---

**Next**: Sprint 3 - Complete Documentation and Final Submission