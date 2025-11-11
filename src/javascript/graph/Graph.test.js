/**
 * Jest tests for the Graph class.
 * These tests verify all graph operations including Dijkstra's algorithm,
 * city management, and distance calculations.
 *
 * @author BookingMx Development Team
 * @version 1.0.0
 */

const Graph = require('./Graph');

describe('Graph Class Tests', () => {
    let graph;
    beforeEach(() => {
        graph = createTestGraph();
    });
    // Helper function to create a test graph with sample data
    function createTestGraph() {
        const graph = new Graph();
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
            'Mexico City': 1600
        });
        graph.addCity('Puebla', {
            'Mexico City': 130
        });
        return graph;
    }

    // ==================== Constructor Tests ====================

    describe('Constructor', () => {

        test('should create an empty graph', () => {
            // Arrange & Act
            const graph = new Graph();

            // Assert
            expect(graph).toBeDefined();
            expect(graph.isEmpty()).toBe(true);
            expect(graph.size()).toBe(0);
            expect(graph.getCities()).toEqual([]);
        });

        test('should initialize with empty adjacency list', () => {
            // Arrange & Act
            const graph = new Graph();

            // Assert
            expect(graph.adjacencyList).toBeDefined();
            expect(graph.adjacencyList instanceof Map).toBe(true);
            expect(graph.adjacencyList.size).toBe(0);
        });
    });

    // ==================== addCity Tests ====================

    describe('addCity', () => {

        test('should add a city with no neighbors', () => {
            // Arrange
            const graph = new Graph();

            // Act
            graph.addCity('Guadalajara');

            // Assert
            expect(graph.hasCity('Guadalajara')).toBe(true);
            expect(graph.size()).toBe(1);
            expect(graph.getNeighbors('Guadalajara')).toEqual({});
        });

        test('should add a city with neighbors', () => {
            // Arrange
            const graph = new Graph();

            // Act
            graph.addCity('Guadalajara', {
                'Mexico City': 540,
                'Puerto Vallarta': 300
            });

            // Assert
            expect(graph.hasCity('Guadalajara')).toBe(true);
            const neighbors = graph.getNeighbors('Guadalajara');
            expect(neighbors['Mexico City']).toBe(540);
            expect(neighbors['Puerto Vallarta']).toBe(300);
        });

        test('should update city if it already exists', () => {
            // Arrange
            const graph = new Graph();
            graph.addCity('Guadalajara', { 'Mexico City': 540 });

            // Act
            graph.addCity('Guadalajara', { 'Cancun': 2000 });

            // Assert
            expect(graph.size()).toBe(1);
            const neighbors = graph.getNeighbors('Guadalajara');
            expect(neighbors['Cancun']).toBe(2000);
            expect(neighbors['Mexico City']).toBeUndefined();
        });

        test('should throw error for null city name', () => {
            // Arrange
            const graph = new Graph();

            // Act & Assert
            expect(() => {
                graph.addCity(null);
            }).toThrow('City name must be a non-empty string');
        });

        test('should throw error for empty city name', () => {
            // Arrange
            const graph = new Graph();

            // Act & Assert
            expect(() => {
                graph.addCity('');
            }).toThrow('City name must be a non-empty string');
        });

        test('should throw error for whitespace city name', () => {
            // Arrange
            const graph = new Graph();

            // Act & Assert
            expect(() => {
                graph.addCity('   ');
            }).toThrow('City name must be a non-empty string');
        });

        test('should throw error for non-string city name', () => {
            // Arrange
            const graph = new Graph();

            // Act & Assert
            expect(() => {
                graph.addCity(123);
            }).toThrow('City name must be a non-empty string');
        });

        test('should throw error for null neighbors', () => {
            // Arrange
            const graph = new Graph();

            // Act & Assert
            expect(() => {
                graph.addCity('Guadalajara', null);
            }).toThrow('Neighbors must be an object');
        });

        test('should throw error for array neighbors', () => {
            // Arrange
            const graph = new Graph();

            // Act & Assert
            expect(() => {
                graph.addCity('Guadalajara', ['Mexico City']);
            }).toThrow('Neighbors must be an object');
        });

        test('should throw error for negative distance', () => {
            // Arrange
            const graph = new Graph();

            // Act & Assert
            expect(() => {
                graph.addCity('Guadalajara', { 'Mexico City': -100 });
            }).toThrow('Invalid distance');
        });

        test('should throw error for non-numeric distance', () => {
            // Arrange
            const graph = new Graph();

            // Act & Assert
            expect(() => {
                graph.addCity('Guadalajara', { 'Mexico City': 'far' });
            }).toThrow('Invalid distance');
        });
    });

    // ==================== removeCity Tests ====================

    describe('removeCity', () => {

        test('should remove an existing city', () => {
            // Arrange
            const graph = new Graph();
            graph.addCity('Guadalajara');
            graph.addCity('Mexico City');

            // Act
            const result = graph.removeCity('Guadalajara');

            // Assert
            expect(result).toBe(true);
            expect(graph.hasCity('Guadalajara')).toBe(false);
            expect(graph.size()).toBe(1);
        });

        test('should return false when removing non-existent city', () => {
            // Arrange
            const graph = new Graph();

            // Act
            const result = graph.removeCity('NonExistent');

            // Assert
            expect(result).toBe(false);
        });

        test('should remove references from other cities', () => {
            // Arrange
            const graph = new Graph();
            graph.addCity('Guadalajara', { 'Mexico City': 540 });
            graph.addCity('Mexico City', { 'Guadalajara': 540 });

            // Act
            graph.removeCity('Guadalajara');

            // Assert
            expect(graph.hasCity('Mexico City')).toBe(true);
            const neighbors = graph.getNeighbors('Mexico City');
            expect(neighbors['Guadalajara']).toBeUndefined();
        });
    });

    // ==================== hasCity Tests ====================

    describe('hasCity', () => {

        test('should return true for existing city', () => {
            // Arrange
            const graph = new Graph();
            graph.addCity('Guadalajara');

            // Act & Assert
            expect(graph.hasCity('Guadalajara')).toBe(true);
        });

        test('should return false for non-existing city', () => {
            // Arrange
            const graph = new Graph();

            // Act & Assert
            expect(graph.hasCity('Guadalajara')).toBe(false);
        });
    });

    // ==================== getCities Tests ====================

    describe('getCities', () => {

        test('should return empty array for empty graph', () => {
            // Arrange
            const graph = new Graph();

            // Act
            const cities = graph.getCities();

            // Assert
            expect(cities).toEqual([]);
            expect(Array.isArray(cities)).toBe(true);
        });

        test('should return all city names', () => {
            // Arrange
            const graph = new Graph();
            graph.addCity('Guadalajara');
            graph.addCity('Mexico City');
            graph.addCity('Cancun');

            // Act
            const cities = graph.getCities();

            // Assert
            expect(cities).toHaveLength(3);
            expect(cities).toContain('Guadalajara');
            expect(cities).toContain('Mexico City');
            expect(cities).toContain('Cancun');
        });
    });

    // ==================== getNeighbors Tests ====================

    describe('getNeighbors', () => {

        test('should return neighbors of a city', () => {
            // Arrange
            const graph = new Graph();
            graph.addCity('Guadalajara', {
                'Mexico City': 540,
                'Puerto Vallarta': 300
            });

            // Act
            const neighbors = graph.getNeighbors('Guadalajara');

            // Assert
            expect(neighbors['Mexico City']).toBe(540);
            expect(neighbors['Puerto Vallarta']).toBe(300);
        });

        test('should throw error for non-existent city', () => {
            // Arrange
            const graph = new Graph();

            // Act & Assert
            expect(() => {
                graph.getNeighbors('NonExistent');
            }).toThrow("City 'NonExistent' does not exist");
        });

        test('should return empty object for city with no neighbors', () => {
            // Arrange
            const graph = new Graph();
            graph.addCity('Guadalajara');

            // Act
            const neighbors = graph.getNeighbors('Guadalajara');

            // Assert
            expect(neighbors).toEqual({});
        });
    });

    // ==================== getDistance Tests ====================

    describe('getDistance', () => {

        test('should return distance between connected cities', () => {
            // Arrange
            const graph = new Graph();
            graph.addCity('Guadalajara', { 'Mexico City': 540 });
            graph.addCity('Mexico City');

            // Act
            const distance = graph.getDistance('Guadalajara', 'Mexico City');

            // Assert
            expect(distance).toBe(540);
        });

        test('should return null for non-connected cities', () => {
            // Arrange
            const graph = new Graph();
            graph.addCity('Guadalajara');
            graph.addCity('Cancun');

            // Act
            const distance = graph.getDistance('Guadalajara', 'Cancun');

            // Assert
            expect(distance).toBeNull();
        });

        test('should throw error if source city does not exist', () => {
            // Arrange
            const graph = new Graph();
            graph.addCity('Mexico City');

            // Act & Assert
            expect(() => {
                graph.getDistance('NonExistent', 'Mexico City');
            }).toThrow("City 'NonExistent' does not exist");
        });

        test('should throw error if destination city does not exist', () => {
            // Arrange
            const graph = new Graph();
            graph.addCity('Guadalajara');

            // Act & Assert
            expect(() => {
                graph.getDistance('Guadalajara', 'NonExistent');
            }).toThrow("City 'NonExistent' does not exist");
        });
    });

    // ==================== addConnection Tests ====================

    describe('addConnection', () => {

        test('should add bidirectional connection between cities', () => {
            // Arrange
            const graph = new Graph();
            graph.addCity('Guadalajara');
            graph.addCity('Mexico City');

            // Act
            graph.addConnection('Guadalajara', 'Mexico City', 540);

            // Assert
            expect(graph.getDistance('Guadalajara', 'Mexico City')).toBe(540);
            expect(graph.getDistance('Mexico City', 'Guadalajara')).toBe(540);
        });

        test('should throw error if first city does not exist', () => {
            // Arrange
            const graph = new Graph();
            graph.addCity('Mexico City');

            // Act & Assert
            expect(() => {
                graph.addConnection('NonExistent', 'Mexico City', 540);
            }).toThrow("City 'NonExistent' does not exist");
        });

        test('should throw error if second city does not exist', () => {
            // Arrange
            const graph = new Graph();
            graph.addCity('Guadalajara');

            // Act & Assert
            expect(() => {
                graph.addConnection('Guadalajara', 'NonExistent', 540);
            }).toThrow("City 'NonExistent' does not exist");
        });

        test('should throw error for negative distance', () => {
            // Arrange
            const graph = new Graph();
            graph.addCity('Guadalajara');
            graph.addCity('Mexico City');

            // Act & Assert
            expect(() => {
                graph.addConnection('Guadalajara', 'Mexico City', -100);
            }).toThrow('Distance must be a non-negative number');
        });
    });

    // ==================== removeConnection Tests ====================

    describe('removeConnection', () => {

        test('should remove connection between cities', () => {
            // Arrange
            const graph = new Graph();
            graph.addCity('Guadalajara', { 'Mexico City': 540 });
            graph.addCity('Mexico City', { 'Guadalajara': 540 });

            // Act
            const result = graph.removeConnection('Guadalajara', 'Mexico City');

            // Assert
            expect(result).toBe(true);
            expect(graph.getDistance('Guadalajara', 'Mexico City')).toBeNull();
            expect(graph.getDistance('Mexico City', 'Guadalajara')).toBeNull();
        });

        test('should return false for non-existent connection', () => {
            // Arrange
            const graph = new Graph();
            graph.addCity('Guadalajara');
            graph.addCity('Mexico City');

            // Act
            const result = graph.removeConnection('Guadalajara', 'Mexico City');

            // Assert
            expect(result).toBe(false);
        });

        test('should return false if cities do not exist', () => {
            // Arrange
            const graph = new Graph();

            // Act
            const result = graph.removeConnection('NonExistent1', 'NonExistent2');

            // Assert
            expect(result).toBe(false);
        });
    });

    // ==================== Utility Methods Tests ====================

    describe('Utility Methods', () => {

        test('size should return number of cities', () => {
            // Arrange
            const graph = new Graph();
            graph.addCity('Guadalajara');
            graph.addCity('Mexico City');
            graph.addCity('Cancun');

            // Act & Assert
            expect(graph.size()).toBe(3);
        });

        test('isEmpty should return true for empty graph', () => {
            // Arrange
            const graph = new Graph();

            // Act & Assert
            expect(graph.isEmpty()).toBe(true);
        });

        test('isEmpty should return false for non-empty graph', () => {
            // Arrange
            const graph = new Graph();
            graph.addCity('Guadalajara');

            // Act & Assert
            expect(graph.isEmpty()).toBe(false);
        });

        test('clear should remove all cities', () => {
            // Arrange
            const graph = new Graph();
            graph.addCity('Guadalajara');
            graph.addCity('Mexico City');

            // Act
            graph.clear();

            // Assert
            expect(graph.isEmpty()).toBe(true);
            expect(graph.size()).toBe(0);
        });

        test('toString should return meaningful representation', () => {
            // Arrange
            const graph = new Graph();
            graph.addCity('Guadalajara', { 'Mexico City': 540 });

            // Act
            const str = graph.toString();

            // Assert
            expect(str).toContain('Graph');
            expect(str).toContain('Guadalajara');
            expect(str).toContain('Mexico City');
            expect(str).toContain('540');
        });

        test('toString should return "empty" for empty graph', () => {
            // Arrange
            const graph = new Graph();

            // Act
            const str = graph.toString();

            // Assert
            expect(str).toContain('empty');
        });
    });

    // ==================== findNearbyCities Tests ====================

    describe('findNearbyCities', () => {

        test('should find cities within distance threshold', () => {
            // Arrange
            const graph = createTestGraph();

            // Act
            const nearby = graph.findNearbyCities('Guadalajara', 400);

            // Assert
            expect(nearby).toHaveLength(3);
            const cityNames = nearby.map(item => item.city);
            expect(cityNames).toContain('Leon');
            expect(cityNames).toContain('Puerto Vallarta');
            expect(cityNames).toContain('Aguascalientes');
        });

        test('should return empty array if no cities within distance', () => {
            // Arrange
            const graph = new Graph();
            graph.addCity('Guadalajara', { 'Mexico City': 1000 });
            graph.addCity('Mexico City');

            // Act
            const nearby = graph.findNearbyCities('Guadalajara', 500);

            // Assert
            expect(nearby).toHaveLength(0);
        });

        test('should not include the starting city in results', () => {
            // Arrange
            const graph = createTestGraph();

            // Act
            const nearby = graph.findNearbyCities('Guadalajara', 1000);

            // Assert
            const cityNames = nearby.map(item => item.city);
            expect(cityNames).not.toContain('Guadalajara');
        });

        test('should include correct distances', () => {
            // Arrange
            const graph = createTestGraph();

            // Act
            const nearby = graph.findNearbyCities('Guadalajara', 400);

            // Assert
            const leon = nearby.find(item => item.city === 'Leon');
            expect(leon.distance).toBe(220);

            const puertoVallarta = nearby.find(item => item.city === 'Puerto Vallarta');
            expect(puertoVallarta.distance).toBe(300);
        });

        test('should sort results by distance', () => {
            // Arrange
            const graph = createTestGraph();

            // Act
            const nearby = graph.findNearbyCities('Guadalajara', 600);

            // Assert
            expect(nearby[0].distance).toBeLessThanOrEqual(nearby[1].distance);
            if (nearby[2]) {
                expect(nearby[1].distance).toBeLessThanOrEqual(nearby[2].distance);
            }
        });

        test('should throw error for non-existent city', () => {
            // Arrange
            const graph = new Graph();

            // Act & Assert
            expect(() => {
                graph.findNearbyCities('NonExistent', 100);
            }).toThrow("City 'NonExistent' does not exist");
        });

        test('should throw error for negative distance', () => {
            // Arrange
            const graph = new Graph();
            graph.addCity('Guadalajara');

            // Act & Assert
            expect(() => {
                graph.findNearbyCities('Guadalajara', -100);
            }).toThrow('Maximum distance must be a non-negative number');
        });
    });

    // ==================== findShortestPath Tests (Dijkstra) ====================

    describe('findShortestPath (Dijkstra Algorithm)', () => {

        test('should find shortest path between two cities', () => {
            // Arrange
            const graph = createTestGraph();

            // Act
            const result = graph.findShortestPath('Guadalajara', 'Cancun');

            // Assert
            expect(result.path).toEqual(['Guadalajara', 'Mexico City', 'Cancun']);
            expect(result.distance).toBe(2140);
        });

        test('should handle path to same city', () => {
            // Arrange
            const graph = createTestGraph();

            // Act
            const result = graph.findShortestPath('Guadalajara', 'Guadalajara');

            // Assert
            expect(result.path).toEqual(['Guadalajara']);
            expect(result.distance).toBe(0);
        });

        test('should find path through multiple cities', () => {
            // Arrange
            const graph = createTestGraph();

            // Act
            const result = graph.findShortestPath('Guadalajara', 'Aguascalientes');

            // Assert
            expect(result.path).toEqual(['Guadalajara', 'Leon', 'Aguascalientes']);
            expect(result.distance).toBe(350);
        });

        test('should find shortest of multiple possible paths', () => {
            // Arrange
            const graph = new Graph();
            graph.addCity('A', { 'B': 100, 'C': 150 });
            graph.addCity('B', { 'A': 100, 'D': 100 });
            graph.addCity('C', { 'A': 150, 'D': 50 });
            graph.addCity('D', { 'B': 100, 'C': 50 });

            // Act - shortest is A -> C -> D (200) not A -> B -> D (200)
            const result = graph.findShortestPath('A', 'D');

            // Assert
            expect(result.distance).toBe(200);
        });

        test('should throw error if start city does not exist', () => {
            // Arrange
            const graph = createTestGraph();

            // Act & Assert
            expect(() => {
                graph.findShortestPath('NonExistent', 'Cancun');
            }).toThrow("Starting city 'NonExistent' does not exist");
        });

        test('should throw error if end city does not exist', () => {
            // Arrange
            const graph = createTestGraph();

            // Act & Assert
            expect(() => {
                graph.findShortestPath('Guadalajara', 'NonExistent');
            }).toThrow("Destination city 'NonExistent' does not exist");
        });

        test('should throw error if no path exists', () => {
            // Arrange
            const graph = new Graph();
            graph.addCity('Guadalajara');
            graph.addCity('Cancun'); // Not connected

            // Act & Assert
            expect(() => {
                graph.findShortestPath('Guadalajara', 'Cancun');
            }).toThrow("No path exists between 'Guadalajara' and 'Cancun'");
        });

        test('should handle direct connection', () => {
            // Arrange
            const graph = new Graph();
            graph.addCity('Guadalajara', { 'Mexico City': 540 });
            graph.addCity('Mexico City');

            // Act
            const result = graph.findShortestPath('Guadalajara', 'Mexico City');

            // Assert
            expect(result.path).toEqual(['Guadalajara', 'Mexico City']);
            expect(result.distance).toBe(540);
        });

        test('should work with complex graph', () => {
            // Arrange
            const graph = createTestGraph();

            // Act
            const result = graph.findShortestPath('Puerto Vallarta', 'Puebla');

            // Assert
            expect(result.path[0]).toBe('Puerto Vallarta');
            expect(result.path[result.path.length - 1]).toBe('Puebla');
            expect(result.distance).toBeGreaterThan(0);
        });
    });
    // Agregar al final del archivo, antes del último });

// ==================== Additional Coverage Tests ====================

    describe('Additional Coverage Tests', () => {

        test('should handle zero distance connections', () => {
            const graph = new Graph();
            graph.addCity('A', { 'B': 0 });
            graph.addCity('B');

            expect(graph.getDistance('A', 'B')).toBe(0);
        });

        test('should update existing connections with addConnection', () => {
            const graph = new Graph();
            graph.addCity('A', { 'B': 100 });
            graph.addCity('B', { 'A': 100 });

            graph.addConnection('A', 'B', 200);

            expect(graph.getDistance('A', 'B')).toBe(200);
            expect(graph.getDistance('B', 'A')).toBe(200);
        });

        test('should handle findNearbyCities with exact distance match', () => {
            const graph = new Graph();
            graph.addCity('A', { 'B': 300 });
            graph.addCity('B', { 'A': 300 });

            const nearby = graph.findNearbyCities('A', 300);

            expect(nearby).toHaveLength(1);
            expect(nearby[0].city).toBe('B');
            expect(nearby[0].distance).toBe(300);
        });

        test('should handle findNearbyCities with zero distance', () => {
            const graph = new Graph();
            graph.addCity('A');

            const nearby = graph.findNearbyCities('A', 0);

            expect(nearby).toHaveLength(0);
        });

        test('should handle single city graph in findShortestPath', () => {
            const graph = new Graph();
            graph.addCity('Lonely');

            const result = graph.findShortestPath('Lonely', 'Lonely');

            expect(result.path).toEqual(['Lonely']);
            expect(result.distance).toBe(0);
        });

        test('should handle graph with multiple disconnected components', () => {
            const graph = new Graph();
            graph.addCity('A', { 'B': 100 });
            graph.addCity('B', { 'A': 100 });
            graph.addCity('C', { 'D': 100 });
            graph.addCity('D', { 'C': 100 });

            expect(() => {
                graph.findShortestPath('A', 'C');
            }).toThrow("No path exists");
        });

        test('should handle very large distances', () => {
            const graph = new Graph();
            graph.addCity('A', { 'B': 999999 });
            graph.addCity('B');

            expect(graph.getDistance('A', 'B')).toBe(999999);
        });

        test('should handle multiple neighbors with same distance', () => {
            const graph = new Graph();
            graph.addCity('A', { 'B': 100, 'C': 100, 'D': 100 });
            graph.addCity('B');
            graph.addCity('C');
            graph.addCity('D');

            const neighbors = graph.getNeighbors('A');
            expect(Object.keys(neighbors)).toHaveLength(3);
        });

        test('should handle complex path with multiple route options', () => {
            const graph = new Graph();
            // Create diamond shape: A -> B,C -> D
            graph.addCity('A', { 'B': 100, 'C': 200 });
            graph.addCity('B', { 'A': 100, 'D': 150 });
            graph.addCity('C', { 'A': 200, 'D': 50 });
            graph.addCity('D', { 'B': 150, 'C': 50 });

            const result = graph.findShortestPath('A', 'D');

            // Should choose A -> B -> D (250) over A -> C -> D (250)
            // or the actual shortest if there is one
            expect(result.distance).toBe(250);
        });

        test('should handle removeCity from graph with many connections', () => {
            const graph = createTestGraph();
            const initialSize = graph.size();

            graph.removeCity('Mexico City');

            expect(graph.size()).toBe(initialSize - 1);
            expect(graph.hasCity('Mexico City')).toBe(false);

            // Verify all references removed
            const gdlNeighbors = graph.getNeighbors('Guadalajara');
            expect(gdlNeighbors['Mexico City']).toBeUndefined();
        });

        test('should handle addCity with undefined neighbors parameter', () => {
            const graph = new Graph();
            graph.addCity('Test', undefined);

            expect(graph.hasCity('Test')).toBe(true);
            expect(graph.getNeighbors('Test')).toEqual({});
        });

        test('should handle numeric strings as city names', () => {
            const graph = new Graph();
            graph.addCity('123', { '456': 100 });
            graph.addCity('456');

            expect(graph.hasCity('123')).toBe(true);
            expect(graph.getDistance('123', '456')).toBe(100);
        });

        test('should handle city names with special characters', () => {
            const graph = new Graph();
            graph.addCity('México City', { 'São Paulo': 5000 });
            graph.addCity('São Paulo');

            expect(graph.hasCity('México City')).toBe(true);
            expect(graph.getDistance('México City', 'São Paulo')).toBe(5000);
        });

        test('should handle findNearbyCities when all cities are beyond threshold', () => {
            const graph = new Graph();
            graph.addCity('A', { 'B': 1000, 'C': 2000 });
            graph.addCity('B', { 'A': 1000 });
            graph.addCity('C', { 'A': 2000 });

            const nearby = graph.findNearbyCities('A', 500);

            expect(nearby).toHaveLength(0);
        });

        test('should handle findShortestPath with intermediate cities', () => {
            const graph = new Graph();
            graph.addCity('A', { 'B': 10 });
            graph.addCity('B', { 'A': 10, 'C': 10 });
            graph.addCity('C', { 'B': 10, 'D': 10 });
            graph.addCity('D', { 'C': 10 });

            const result = graph.findShortestPath('A', 'D');

            expect(result.path).toHaveLength(4);
            expect(result.path[0]).toBe('A');
            expect(result.path[result.path.length - 1]).toBe('D');
            expect(result.distance).toBe(30);
        });
    });
});