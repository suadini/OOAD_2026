# Unit Tests

This folder contains unit tests for the circular buffer project. The tests use
**JUnit 5** (Jupiter). The source code was **not modified** — tests only
exercise the existing public (and package-private) API.

## Style rules followed

Every test in this folder follows the rules from the lecture:

- **One assert per test method.**
- **Tests are self-contained** — each test does its own setup inline, so tests
  do not depend on the order they are run in.
- **Arrange-Act-Assert** structure — blank lines separate the three phases
  in every test.
- **Test names describe what is tested and what is expected**, written in
  camelCase (e.g. `readReturnsWrittenItem`, `constructorRejectsZeroSize`).
- **Focus on boundary, empty, and error cases** — zero/negative sizes, empty
  buffer, reading past the write head, capacity-of-one wrap, slot lapping.
- **One test uses a hand-written fake** to isolate `Subscriber` from
  `CircularArray` (see "Mocking" below).

## Test files

The project uses the standard Maven layout. Tests live in
`src/test/java/Assignment_2/` and declare `package Assignment_2;` so they can
access the package-private `Subscriber` constructor used by the isolation
tests.

```
OOAD_2026/
├── pom.xml                              <- Maven project descriptor
├── Assignment_2/                        <- assignment docs
│   ├── README.md
│   └── TESTING.md (this file)
└── src/
    ├── main/java/Assignment_2/          <- source code
    │   ├── BufferTerminal.java
    │   ├── CircularArray.java
    │   ├── CircularBuffer.java
    │   └── Subscriber.java
    └── test/java/Assignment_2/          <- tests, same package as source
        ├── CircularArrayTest.java
        ├── CircularBufferTest.java
        └── SubscriberTest.java
```

| File | What it covers | # of tests |
|---|---|---|
| `CircularArrayTest.java` | Slot mapping, modular indexing, overwrites, invalid sizes | 10 |
| `CircularBufferTest.java` | Capacity, write-head advancement, subscriber factory, invalid capacity | 10 |
| `SubscriberTest.java` | Read cursor, lapping behavior, subscriber independence, isolation via a fake | 9 |

## Setup

The project is built with **Maven**. Maven downloads JUnit Jupiter into the
local cache (`~/.m2/repository/`) on first run — there is no JAR committed
to the repo.

1. Install Maven if it isn't already (one-time):
   ```bash
   brew install maven
   ```
2. Verify the install:
   ```bash
   mvn --version
   ```

## Run the tests

From the project root (the folder containing `pom.xml`):

```bash
mvn test
```

That single command compiles both the source in `Assignment_2B/` and the tests
in `tests/Assignment_2B/`, then runs every test method. The end of the output
looks like:

```
[INFO] Tests run: 29, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

### Useful related commands

| Command | Effect |
|---|---|
| `mvn test` | Compile and run all tests |
| `mvn compile` | Compile sources only (no tests) |
| `mvn clean` | Delete the `target/` build folder |
| `mvn clean test` | Fresh build from scratch |
| `mvn test -Dtest=SubscriberTest` | Run only one test class |
| `mvn test -Dtest=SubscriberTest#readEmptyBufferReturnsNull` | Run one specific test method |

### Where are the compiled `.class` files?

Maven puts them in `target/classes` (source) and `target/test-classes` (tests).
The `target/` folder is regenerated on each build and is typically
git-ignored.

> **Note:** JUnit test classes do not have a `main` method — running them
> with the IDE's green play button on the file (or
> `java Assignment_2.SubscriberTest`) will not work. Use `mvn test`, or
> install the *Test Runner for Java* VSCode extension to get gutter run
> buttons next to each `@Test`. (Most Java IDEs auto-detect `pom.xml` and
> wire this up for you.)

## Mocking

The lecture rule "use mocks to isolate the unit under test" was applied where
it made sense and is documented where it did not:

- **`SubscriberTest`** isolates `Subscriber` from `CircularArray` using a
  hand-written fake (`FakeArray`) defined inside the test class. The fake
  records which sequence number `Subscriber` passed to `array.get(...)` and
  lets the test control the return value, so the two isolation tests verify
  `Subscriber`'s behavior without depending on the real array's logic.
  A real mocking library (e.g. Mockito) was not used to keep the dependency
  list minimal; the fake demonstrates the same idea.

- **`CircularBufferTest`** does **not** mock its `CircularArray` dependency.
  `CircularBuffer` constructs its own array internally (`this.array = new
  CircularArray<>(capacity);`) with no constructor injection or setter, so
  there is no way to substitute a fake without modifying the source. Since
  the assignment forbids modifying source, `CircularBuffer` is tested against
  the real `CircularArray`. This means those tests are technically small
  integration tests rather than pure unit tests.

- **`CircularArrayTest`** has no dependencies to mock — `CircularArray` only
  wraps an `AtomicReferenceArray`, which is part of the JDK and assumed correct.

## What is NOT tested (and why)

The `BufferTerminal` class is **not unit-testable in its current form** without
modifying the source code:

1. **All command methods are `private static`** (`cmdInit`, `cmdWrite`,
   `cmdSubscribe`, `cmdRead`, `cmdInfo`). They can only be exercised through
   the interactive input loop or via reflection — neither is appropriate for
   a unit test.
2. **State is held in `static` fields** (`buffer`, `subscribers`, `input`),
   so any test would leak state into the next test through the JVM-wide
   class loader.
3. **The `Scanner(System.in)` is created at class-load time**, so a test
   cannot substitute alternative input without restarting the JVM or
   modifying the field via reflection.
4. **`runLoop()` is `private` and blocks on `System.in`**, so it cannot be
   driven from a test directly.

Because the assignment forbids modifying the source, `BufferTerminal` was left
untested. The buffer logic that it drives — `CircularArray`, `CircularBuffer`,
`Subscriber` — is fully covered by the three test classes above.
`BufferTerminal` itself is just an input parser around them.

### What would make `BufferTerminal` testable

If source modification were allowed, the minimum changes would be:

- Make `runLoop` an instance method (not `static`).
- Make `buffer`, `subscribers`, `input` instance fields (not `static`).
- Accept a `Scanner` / `PrintStream` via constructor parameters so tests can
  inject `new Scanner("init 3\nwrite a\nexit\n")` and capture
  `System.out` into a `ByteArrayOutputStream`.
- Make the command methods package-private so tests in the same package can
  call them directly.

### Concurrency tests

The buffer is documented as thread-safe (single writer, many readers) and uses
`AtomicLong` / `AtomicReferenceArray` internally. Concurrency tests (spawning
threads, checking for races) are deliberately not included — they are flaky
to write correctly at a student level and the atomic primitives the
implementation relies on are part of the JDK and assumed correct.
