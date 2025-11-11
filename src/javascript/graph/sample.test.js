/**
 * Sample test file to verify Jest is configured correctly.
 * This file will be deleted once real tests are created.
 *
 * @author BookingMx Development Team
 * @version 1.0.0
 */

describe('Jest Configuration Test', () => {

    test('should verify Jest is working', () => {
        // Arrange
        const expected = true;

        // Act
        const actual = true;

        // Assert
        expect(actual).toBe(expected);
    });

    test('should perform basic arithmetic', () => {
        // Arrange
        const a = 5;
        const b = 3;

        // Act
        const sum = a + b;
        const product = a * b;

        // Assert
        expect(sum).toBe(8);
        expect(product).toBe(15);
    });

    test('should work with strings', () => {
        // Arrange
        const greeting = 'Hello';
        const name = 'World';

        // Act
        const message = `${greeting}, ${name}!`;

        // Assert
        expect(message).toBe('Hello, World!');
        expect(message).toContain('World');
    });

    test('should work with arrays', () => {
        // Arrange
        const cities = ['Guadalajara', 'Mexico City', 'Cancun'];

        // Assert
        expect(cities).toHaveLength(3);
        expect(cities).toContain('Guadalajara');
        expect(cities[0]).toBe('Guadalajara');
    });

    test('should work with objects', () => {
        // Arrange
        const city = {
            name: 'Guadalajara',
            state: 'Jalisco',
            population: 5268642
        };

        // Assert
        expect(city.name).toBe('Guadalajara');
        expect(city).toHaveProperty('state');
        expect(city.population).toBeGreaterThan(5000000);
    });
});