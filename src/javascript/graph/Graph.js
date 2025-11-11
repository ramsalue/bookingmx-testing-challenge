/**
 * Graph class representing cities and distances between them.
 * Uses an adjacency list structure to store the graph.
 *
 * This class provides functionality for:
 * - Adding cities with their connections
 * - Finding shortest paths between cities (Dijkstra's algorithm)
 * - Finding nearby cities within a distance threshold
 * - Calculating distances and routes
 *
 * @author BookingMx Development Team
 * @version 1.0.0
 */

class Graph {
    /**
     * Constructor initializes an empty graph.
     * Uses a Map to store the adjacency list for better performance.
     */
    constructor() {
        // Map structure: city name -> Map of (neighbor -> distance)
        this.adjacencyList = new Map();
    }

    /**
     * Adds a city to the graph with its connections to other cities.
     * If the city already exists, it will be updated with new connections.
     *
     * @param {string} cityName - The name of the city to add
     * @param {Object} neighbors - Object mapping neighbor cities to distances
     *                            Format: { 'CityName': distance, ... }
     * @throws {Error} If cityName is invalid or neighbors is not an object
     *
     * @example
     * graph.addCity('Guadalajara', { 'Mexico City': 540, 'Puerto Vallarta': 300 });
     */
    addCity(cityName, neighbors = {}) {
        // Validate city name
        if (!cityName || typeof cityName !== 'string' || cityName.trim() === '') {
            throw new Error('City name must be a non-empty string');
        }

        // Validate neighbors
        if (typeof neighbors !== 'object' || neighbors === null || Array.isArray(neighbors)) {
            throw new Error('Neighbors must be an object');
        }

        // Validate neighbor distances
        for (const [neighbor, distance] of Object.entries(neighbors)) {
            if (typeof distance !== 'number' || distance < 0) {
                throw new Error(`Invalid distance for ${neighbor}: must be a non-negative number`);
            }
        }

        // Convert neighbors object to Map for consistent structure
        const neighborMap = new Map(Object.entries(neighbors));

        // Add or update the city
        this.adjacencyList.set(cityName, neighborMap);
    }

    /**
     * Removes a city from the graph.
     * Also removes all references to this city from other cities' neighbor lists.
     *
     * @param {string} cityName - The name of the city to remove
     * @returns {boolean} True if city was removed, false if it didn't exist
     *
     * @example
     * graph.removeCity('Guadalajara');
     */
    removeCity(cityName) {
        if (!this.hasCity(cityName)) {
            return false;
        }

        // Remove the city from the adjacency list
        this.adjacencyList.delete(cityName);

        // Remove references to this city from all other cities
        for (const [city, neighbors] of this.adjacencyList.entries()) {
            if (neighbors.has(cityName)) {
                neighbors.delete(cityName);
            }
        }

        return true;
    }

    /**
     * Checks if a city exists in the graph.
     *
     * @param {string} cityName - The name of the city to check
     * @returns {boolean} True if the city exists, false otherwise
     */
    hasCity(cityName) {
        return this.adjacencyList.has(cityName);
    }

    /**
     * Gets all cities in the graph.
     *
     * @returns {string[]} Array of city names
     */
    getCities() {
        return Array.from(this.adjacencyList.keys());
    }

    /**
     * Gets the neighbors of a specific city.
     *
     * @param {string} cityName - The name of the city
     * @returns {Object} Object mapping neighbor cities to distances
     * @throws {Error} If the city doesn't exist
     *
     * @example
     * const neighbors = graph.getNeighbors('Guadalajara');
     * // Returns: { 'Mexico City': 540, 'Puerto Vallarta': 300 }
     */
    getNeighbors(cityName) {
        if (!this.hasCity(cityName)) {
            throw new Error(`City '${cityName}' does not exist in the graph`);
        }

        const neighbors = this.adjacencyList.get(cityName);
        // Convert Map back to plain object for easier consumption
        return Object.fromEntries(neighbors);
    }

    /**
     * Gets the direct distance between two cities.
     * Returns null if there is no direct connection.
     *
     * @param {string} fromCity - The starting city
     * @param {string} toCity - The destination city
     * @returns {number|null} The distance, or null if no direct connection
     * @throws {Error} If either city doesn't exist
     *
     * @example
     * const distance = graph.getDistance('Guadalajara', 'Mexico City');
     * // Returns: 540
     */
    getDistance(fromCity, toCity) {
        if (!this.hasCity(fromCity)) {
            throw new Error(`City '${fromCity}' does not exist in the graph`);
        }
        if (!this.hasCity(toCity)) {
            throw new Error(`City '${toCity}' does not exist in the graph`);
        }

        const neighbors = this.adjacencyList.get(fromCity);
        return neighbors.has(toCity) ? neighbors.get(toCity) : null;
    }

    /**
     * Adds a bidirectional connection between two cities.
     * This is a convenience method for adding connections in both directions.
     *
     * @param {string} city1 - First city
     * @param {string} city2 - Second city
     * @param {number} distance - Distance between the cities
     * @throws {Error} If either city doesn't exist or distance is invalid
     *
     * @example
     * graph.addConnection('Guadalajara', 'Mexico City', 540);
     */
    addConnection(city1, city2, distance) {
        if (!this.hasCity(city1)) {
            throw new Error(`City '${city1}' does not exist in the graph`);
        }
        if (!this.hasCity(city2)) {
            throw new Error(`City '${city2}' does not exist in the graph`);
        }
        if (typeof distance !== 'number' || distance < 0) {
            throw new Error('Distance must be a non-negative number');
        }

        // Add connection in both directions
        this.adjacencyList.get(city1).set(city2, distance);
        this.adjacencyList.get(city2).set(city1, distance);
    }

    /**
     * Removes a connection between two cities.
     * Removes the connection in both directions.
     *
     * @param {string} city1 - First city
     * @param {string} city2 - Second city
     * @returns {boolean} True if connection was removed, false if it didn't exist
     */
    removeConnection(city1, city2) {
        if (!this.hasCity(city1) || !this.hasCity(city2)) {
            return false;
        }

        const neighbors1 = this.adjacencyList.get(city1);
        const neighbors2 = this.adjacencyList.get(city2);

        const removed1 = neighbors1.delete(city2);
        const removed2 = neighbors2.delete(city1);

        return removed1 || removed2;
    }

    /**
     * Gets the total number of cities in the graph.
     *
     * @returns {number} The number of cities
     */
    size() {
        return this.adjacencyList.size;
    }

    /**
     * Checks if the graph is empty.
     *
     * @returns {boolean} True if the graph has no cities, false otherwise
     */
    isEmpty() {
        return this.adjacencyList.size === 0;
    }

    /**
     * Clears all cities and connections from the graph.
     */
    clear() {
        this.adjacencyList.clear();
    }

    /**
     * Creates a string representation of the graph.
     * Useful for debugging and logging.
     *
     * @returns {string} String representation of the graph
     */
    toString() {
        if (this.isEmpty()) {
            return 'Graph { empty }';
        }

        let result = 'Graph {\n';
        for (const [city, neighbors] of this.adjacencyList.entries()) {
            const neighborList = Array.from(neighbors.entries())
                .map(([neighbor, distance]) => `${neighbor} (${distance}km)`)
                .join(', ');
            result += `  ${city}: [${neighborList}]\n`;
        }
        result += '}';
        return result;
    }

    /**
     * Finds all cities within a specified distance from a starting city.
     * This includes both direct connections and cities reachable through multiple hops.
     *
     * @param {string} startCity - The city to start from
     * @param {number} maxDistance - Maximum distance threshold
     * @returns {Object[]} Array of objects with city name and distance
     * @throws {Error} If the starting city doesn't exist or maxDistance is invalid
     *
     * @example
     * const nearby = graph.findNearbyCities('Guadalajara', 400);
     * // Returns: [{ city: 'Puerto Vallarta', distance: 300 }]
     */
    findNearbyCities(startCity, maxDistance) {
        if (!this.hasCity(startCity)) {
            throw new Error(`City '${startCity}' does not exist in the graph`);
        }
        if (typeof maxDistance !== 'number' || maxDistance < 0) {
            throw new Error('Maximum distance must be a non-negative number');
        }

        const result = [];
        const visited = new Set();
        const distances = new Map();

        // Initialize distances
        distances.set(startCity, 0);

        // Use a priority queue approach (simplified with array sorting)
        const queue = [{ city: startCity, distance: 0 }];

        while (queue.length > 0) {
            // Get city with minimum distance
            queue.sort((a, b) => a.distance - b.distance);
            const { city: currentCity, distance: currentDistance } = queue.shift();

            if (visited.has(currentCity)) continue;
            visited.add(currentCity);

            // Add to results if within max distance and not the start city
            if (currentCity !== startCity && currentDistance <= maxDistance) {
                result.push({ city: currentCity, distance: currentDistance });
            }

            // Check neighbors
            const neighbors = this.adjacencyList.get(currentCity);
            for (const [neighbor, edgeDistance] of neighbors.entries()) {
                const totalDistance = currentDistance + edgeDistance;

                if (!visited.has(neighbor) && totalDistance <= maxDistance) {
                    if (!distances.has(neighbor) || totalDistance < distances.get(neighbor)) {
                        distances.set(neighbor, totalDistance);
                        queue.push({ city: neighbor, distance: totalDistance });
                    }
                }
            }
        }

        // Sort results by distance
        result.sort((a, b) => a.distance - b.distance);
        return result;
    }

    /**
     * Finds the shortest path between two cities using Dijkstra's algorithm.
     *
     * @param {string} startCity - The starting city
     * @param {string} endCity - The destination city
     * @returns {Object} Object with path array and total distance
     *                   Format: { path: ['City1', 'City2', ...], distance: number }
     * @throws {Error} If either city doesn't exist
     *
     * @example
     * const route = graph.findShortestPath('Guadalajara', 'Cancun');
     * // Returns: { path: ['Guadalajara', 'Mexico City', 'Cancun'], distance: 2140 }
     */
    findShortestPath(startCity, endCity) {
        if (!this.hasCity(startCity)) {
            throw new Error(`Starting city '${startCity}' does not exist in the graph`);
        }
        if (!this.hasCity(endCity)) {
            throw new Error(`Destination city '${endCity}' does not exist in the graph`);
        }

        // Handle case where start and end are the same
        if (startCity === endCity) {
            return { path: [startCity], distance: 0 };
        }

        // Initialize data structures
        const distances = new Map();
        const previous = new Map();
        const unvisited = new Set(this.getCities());

        // Set initial distances
        for (const city of this.getCities()) {
            distances.set(city, city === startCity ? 0 : Infinity);
        }

        while (unvisited.size > 0) {
            // Find unvisited city with minimum distance
            let currentCity = null;
            let minDistance = Infinity;

            for (const city of unvisited) {
                const distance = distances.get(city);
                if (distance < minDistance) {
                    minDistance = distance;
                    currentCity = city;
                }
            }

            // If we can't reach any more cities, break
            if (currentCity === null || minDistance === Infinity) {
                break;
            }

            // If we reached the destination, we can stop
            if (currentCity === endCity) {
                break;
            }

            unvisited.delete(currentCity);

            // Update distances for neighbors
            const neighbors = this.adjacencyList.get(currentCity);
            for (const [neighbor, edgeDistance] of neighbors.entries()) {
                if (!unvisited.has(neighbor)) continue;

                const newDistance = distances.get(currentCity) + edgeDistance;
                if (newDistance < distances.get(neighbor)) {
                    distances.set(neighbor, newDistance);
                    previous.set(neighbor, currentCity);
                }
            }
        }

        // Check if path exists
        if (!previous.has(endCity) && startCity !== endCity) {
            throw new Error(`No path exists between '${startCity}' and '${endCity}'`);
        }

        // Reconstruct path
        const path = [];
        let current = endCity;

        while (current !== undefined) {
            path.unshift(current);
            current = previous.get(current);
        }

        return {
            path: path,
            distance: distances.get(endCity)
        };
    }
}

// Export for use in tests and other modules
module.exports = Graph;