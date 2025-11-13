# Installation Guide - BookingMx Testing Challenge

## Complete Setup Instructions for Both Java and JavaScript Modules

---

## System Requirements

### Required Software
- **Java Development Kit (JDK)**: Version 17 or higher
- **Maven**: Version 3.9 or higher
- **Node.js**: Version 18 or higher
- **npm**: Version 9 or higher
- **Git**: Latest version
- **IDE**: IntelliJ IDEA (for Java) and/or VS Code (for JavaScript)

### Operating System
- Windows 10/11

---

## Step 1: Verify Prerequisites

### Check Java Installation

```bash
java --version
# Expected output: java version "17.x.x" or higher

javac --version
# Expected output: javac 17.x.x
```

**If not installed**:
- Download from: https://www.oracle.com/java/technologies/downloads/
- Or use OpenJDK: https://adoptium.net/

### Check Maven Installation

```bash
mvn --version
# Expected output: Apache Maven 3.9.x or higher
```

**If not installed**:
- Download from: https://maven.apache.org/download.cgi
- Follow installation guide: https://maven.apache.org/install.html

### Check Node.js and npm

```bash
node --version
# Expected output: v18.x.x or higher

npm --version
# Expected output: 9.x.x or higher
```

**If not installed**:
- Download from: https://nodejs.org/
- Install LTS (Long Term Support) version
- Restart terminal after installation

### Check Git

```bash
git --version
# Expected output: git version 2.x.x
```

**If not installed**:
- Download from: https://git-scm.com/downloads

---

## Step 2: Clone the Repository

```bash
# Clone from GitHub
git clone https://github.com/YOUR_USERNAME/bookingmx-testing-challenge.git

# Navigate to project directory
cd bookingmx-testing-challenge

# Verify you're on main branch
git branch
# Should show: * main
```

---

## Step 3: Setup Java Module (Sprint 1)

### Install Java Dependencies

```bash
# Clean and install Maven dependencies
mvn clean install

# This will:
# - Download all Java dependencies (JUnit, JaCoCo, etc.)
# - Compile Java code
# - Run all Java tests
# - Generate coverage reports
```

**Expected output**:
```
[INFO] Tests run: 100+, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
[INFO] Total time: 15-30 seconds
```

### Verify Java Setup

```bash
# Run only Java tests
mvn test

# Generate Java coverage report
mvn test jacoco:report

# View coverage report
# Open: target/site/jacoco/index.html
```

**Expected Coverage**: 92%+

---

## Step 4: Setup JavaScript Module (Sprint 2)

### Install Node.js Dependencies

```bash
# Install npm packages
npm install

# This will:
# - Download Jest and dependencies
# - Create node_modules/ folder
# - Generate package-lock.json
```

**Expected output**:
```
added 300+ packages
```

### Verify JavaScript Setup

```bash
# Run JavaScript tests
npm test

# Generate JavaScript coverage report
npm run test:coverage

# View coverage report
# Open: coverage/lcov-report/index.html
```

**Expected Coverage**: 95%+

---

## Step 5: Verify Complete Installation

### Run All Tests

```bash
# Java tests
mvn test

# JavaScript tests
npm test
```

Both should show:
```
All tests passing
No errors
Coverage above 90%
```

### Check Project Structure

```bash
# List project structure
tree -L 3 -I 'node_modules|target'

# Or manually verify these folders exist:
ls -la
```

Expected structure:
```
bookingmx-testing-challenge/
├── src/
│   ├── main/java/          (Java production code)
│   ├── test/java/          (Java tests)
│   └── javascript/         (JavaScript module)
├── target/                 (Java build output)
├── node_modules/           (npm packages)
├── coverage/               (Jest coverage)
├── pom.xml                 (Maven config)
├── package.json            (npm config)
└── README.md
```

---

## Troubleshooting

### Issue: Java Tests Won't Run

**Error**: `mvn: command not found`

**Solution**:
1. Verify Maven is installed: `mvn --version`
2. Add Maven to PATH
3. Restart terminal

**Error**: `JAVA_HOME not set`

**Solution**:
```bash
# Mac/Linux
export JAVA_HOME=$(/usr/libexec/java_home)

# Windows
# Set in System Environment Variables
```

---

### Issue: JavaScript Tests Won't Run

**Error**: `npm: command not found`

**Solution**:
1. Reinstall Node.js from https://nodejs.org/
2. Restart terminal
3. Verify: `node --version`

**Error**: `Cannot find module 'jest'`

**Solution**:
```bash
# Clean install
rm -rf node_modules package-lock.json
npm install
```
---

## Updating the Project

### Pull Latest Changes

```bash
# Fetch and merge latest changes
git pull origin main

# Reinstall dependencies if package files changed
mvn clean install
npm install
```

---

## Cleaning Build Artifacts

### Clean Java Build

```bash
# Remove all compiled files
mvn clean

# This removes target/ directory
```

### Clean JavaScript Build

```bash
# Remove node_modules and coverage
rm -rf node_modules coverage

# Reinstall
npm install
```

---

## Running Coverage Reports

### Java Coverage (JaCoCo)

```bash
# Generate report
mvn clean test jacoco:report

# View report
# Windows: start target/site/jacoco/index.html
```

### JavaScript Coverage (Jest)

```bash
# Generate report
npm run test:coverage

# View report
# Windows: start coverage/lcov-report/index.html
```

---

## Next Steps

After successful installation:

1. **Explore the code**:
   - Java: `src/main/java/com/bookingmx/`
   - JavaScript: `src/javascript/graph/`

2. **Run tests**:
   - `mvn test` for Java
   - `npm test` for JavaScript

3. **Read documentation**:
   - `README.md` - Project overview
   - `TESTING.md` - Testing guide
   - `src/javascript/README.md` - JavaScript module docs

4. **View coverage reports**:
   - Java: `target/site/jacoco/index.html`
   - JavaScript: `coverage/lcov-report/index.html`

---

## Support

If you encounter issues not covered here:

1. Check existing documentation in the repository
2. Review error messages carefully
3. Search for similar issues on Stack Overflow
4. Contact the development team

**Last Updated**: November 2025  
**Version**: 2.3.0