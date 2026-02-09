# Random Number Generator Statistical Analysis

A Java program that evaluates the quality and distribution of random number generators by generating samples and computing descriptive statistics.

## Overview

This project compares three built-in Java random number generators:
- `java.util.Random`
- `Math.random()`
- `java.util.concurrent.ThreadLocalRandom`

For each generator, the program produces random doubles in the range [0, 1) across different sample sizes and analyzes their statistical properties.

## Features

- Generates random numbers using three different Java RNG implementations
- Calculates descriptive statistics: count, mean, standard deviation, minimum, and maximum
- Tests multiple sample sizes (10, 1000, 1000000) to observe convergence behavior
- Displays results in formatted tables for easy comparison

## Requirements

- Java 8 or higher
- No external dependencies required

## Compilation
```bash
javac Generator.java
```

## Usage
```bash
java Generator
```

## Sample Output
```
Generator: java.util.Random
-----------------------------------------------------------------------------
n          Mean         StdDev       Min          Max
-----------------------------------------------------------------------------
10         0.4036       0.3211       0.0689       0.9726      
1000       0.4983       0.2939       0.0003       0.9949      
1000000    0.5006       0.2888       0.0000       1.0000      
Generator: Math.random()
-----------------------------------------------------------------------------
n          Mean         StdDev       Min          Max
-----------------------------------------------------------------------------
10         0.5600       0.2348       0.1198       0.8210
1000       0.4884       0.2907       0.0000       0.9993
1000000    0.5003       0.2887       0.0000       1.0000      
Generator: ThreadLocalRandom
-----------------------------------------------------------------------------
n          Mean         StdDev       Min          Max
-----------------------------------------------------------------------------
10         0.4630       0.2628       0.0274       0.9647
1000       0.5100       0.2927       0.0006       0.9997
1000000    0.5001       0.2888       0.0000       1.0000
```

## Expected Behavior

As sample size increases:
- Mean converges to 0.5
- Standard deviation converges to approximately 0.289
- Minimum approaches 0.0
- Maximum approaches 1.0

Small samples (n=10) show higher variance, while large samples (n=1000000) demonstrate the expected uniform distribution properties.

## Project Structure
```
.
├── Generator.java    # Main program file
└── README.md         # This file
```

## Implementation Details

The `Generator` class contains four main methods:
- `populate(int n, int randNumGen)` - generates n random values using specified generator
- `statistics(ArrayList<Double> randomValues)` - computes statistical measures
- `display(ArrayList<Double> results, boolean headerOn)` - formats and prints results
- `execute()` - orchestrates the complete analysis workflow

## Author

Suad Gafarli  
CSCI-3612: Object Oriented Analysis and Design  
February 9, 2026