# BookingMx Graph Module - JavaScript

## Sprint 2: Graph Visualization with Jest Testing

---

## Overview

This module implements a graph data structure representing cities and distances between them. The primary purpose is to help BookingMx customers visualize nearby destinations and plan optimal travel routes.

---

## Features

- **City Graph Management**: Store cities and their connections
- **Shortest Path Finding**: Calculate optimal routes between cities (Dijkstra's algorithm)
- **Nearby Cities Search**: Find cities within a specified distance
- **Distance Calculation**: Compute total distance for routes

---

## Project Structure

```
src/javascript/
├── graph/
│   ├── Graph.js              (Main graph class)
│   ├── Graph.test.js         (Jest tests)
│   └── sample.test.js        (Configuration test - temporary)
└── utils/
    ├── graphUtils.js         (Helper functions)
    └── graphUtils.test.js    (Jest tests)
```

---

## Setup

### Prerequisites
- Node.js 18+ installed
- npm 9+ installed

### Installation

```bash
# Install dependencies
npm install

# Run tests
npm test

# Run tests with coverage
npm run test:coverage

# Run tests in watch mode
npm run test:watch
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

### View Coverage Report
Open: `coverage/lcov-report/index.html`

---

## Coverage Goals

- **Minimum Coverage**: 90%
- **Test Framework**: Jest 29.x
- **Coverage Tool**: Jest built-in coverage

---

## Usage Example

```javascript
const Graph = require('./graph/Graph');

// Create a graph
const graph = new Graph();

// Add cities
graph.addCity('Guadalajara', {
  'Mexico City': 540,
  'Puerto Vallarta': 300
});

graph.addCity('Mexico City', {
  'Guadalajara': 540,
  'Cancun': 1600
});

// Find shortest path
const route = graph.findShortestPath('Guadalajara', 'Cancun');
console.log(route); 
// { path: ['Guadalajara', 'Mexico City', 'Cancun'], distance: 2140 }

// Find nearby cities
const nearby = graph.findNearbyCities('Guadalajara', 400);
console.log(nearby); 
// ['Puerto Vallarta']
```

---

**Version**: 2.1.0  
**Sprint**: 2  
**Status**: In Progress