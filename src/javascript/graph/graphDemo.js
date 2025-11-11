/**
 * Simple demonstration of the Graph class functionality.
 * This file is for manual testing and can be deleted before submission.
 *
 * Run with: node src/javascript/graph/graphDemo.js
 */

const Graph = require('./Graph');

console.log('=== BookingMx Graph Module Demo ===\n');

// Create a new graph
const graph = new Graph();
console.log('1. Created empty graph');
console.log(`   Empty: ${graph.isEmpty()}`);
console.log(`   Size: ${graph.size()}\n`);

// Add cities
console.log('2. Adding cities...');
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

graph.addCity('Puerto Vallarta', {
    'Guadalajara': 300
});

graph.addCity('Leon', {
    'Guadalajara': 220,
    'Aguascalientes': 130
});

graph.addCity('Aguascalientes', {
    'Leon': 130
});

graph.addCity('Cancun', {
    'Mexico City': 1600,
    'Playa del Carmen': 68
});

graph.addCity('Puebla', {
    'Mexico City': 130
});

graph.addCity('Playa del Carmen', {
    'Cancun': 68
});

console.log(`   Added ${graph.size()} cities\n`);

// Display graph structure
console.log('3. Graph structure:');
console.log(graph.toString());
console.log();

// Get neighbors
console.log('4. Neighbors of Guadalajara:');
const neighbors = graph.getNeighbors('Guadalajara');
console.log(`   ${JSON.stringify(neighbors, null, 2)}\n`);

// Check direct distance
console.log('5. Direct distance:');
const distance = graph.getDistance('Guadalajara', 'Mexico City');
console.log(`   Guadalajara to Mexico City: ${distance}km\n`);

// Find nearby cities
console.log('6. Cities within 400km of Guadalajara:');
const nearby = graph.findNearbyCities('Guadalajara', 400);
nearby.forEach(({ city, distance }) => {
    console.log(`   - ${city}: ${distance}km`);
});
console.log();

// Find shortest path
console.log('7. Shortest path from Guadalajara to Cancun:');
const route = graph.findShortestPath('Guadalajara', 'Cancun');
console.log(`   Path: ${route.path.join(' → ')}`);
console.log(`   Total distance: ${route.distance}km\n`);

// Another shortest path
console.log('8. Shortest path from Guadalajara to Playa del Carmen:');
const route2 = graph.findShortestPath('Guadalajara', 'Playa del Carmen');
console.log(`   Path: ${route2.path.join(' → ')}`);
console.log(`   Total distance: ${route2.distance}km\n`);

console.log('=== Demo Complete ===');