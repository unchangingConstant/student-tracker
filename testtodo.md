# Test TODO

## AttendanceDAOTest

### Broken / dead tests that need fixing first
- **`testFindVisit_1`** — creates a `Visit` and `Student` but has no assertions; needs a real implementation or should be deleted.
- **`testEndOngoingVisit_2`** — bypasses `dao.endOngoingVisit()` entirely by deleting the row via raw JDBC; it only verifies that JDBC works. Should be deleted or rewritten to call the DAO method.
- **`testFindStudent_2` (gap)** — `testFindStudent_3` and `testFindStudent_4` exist but `_2` is missing entirely. A trivial stub (negative student ID → `Optional.empty()`) has been inserted as a placeholder; verify whether a different case was originally intended here.
- **`testDeleteStudent_3`** — the `removed` student has a randomly-generated `studentId`, but the assertion queries `WHERE student_id = 2`. These will rarely be the same. The check should use `removed.getStudentId()`, not the hardcoded value `2`.
- **`testUpdateStudent_1`** — the call `jdbi.useHandle(handle -> handle.createUpdate(INSERT_STUDENT).bindBean(s1))` is missing `.execute()`, so `s1` is never actually inserted. The test still passes by accident (because `s2` doesn't exist regardless), but the setup is broken.

### Missing method tests
- `updateStudent()` success path — verify the correct row is updated in the database with the new values, and that other rows are unchanged.
- `findVisitsWithStudentId()` — student with no visits (expect empty list); student with multiple visits; wrong student ID (expect empty list).
- `getOngoingVisits()` — empty table; table with one or more records.
- `findOngoingVisit()` — found and not-found cases.
- `insertOngoingVisit()` — verify the row appears in the database with the correct values.
- `endOngoingVisit()` — the existing smoke test only checks that `ongoing_visits` is cleared. Add an assertion that the corresponding visit was also inserted into the `visits` table with the correct `studentId`, `startTime`, and `duration`.
- `endOngoingVisit()` transactional rollback — verify that if `insertVisit` fails mid-transaction, `deleteOngoingVisit` is rolled back and the ongoing visit record is still present. Suggestion: pass a `Visit` with a `null` required field to force a constraint violation. Confirm the `ongoing_visits` schema enforces NOT NULL where needed before implementing.
- **Missing test utility** — the ongoing_visit test stubs will need an `INSERT_ONGOING_VISIT` query constant in the test class header (analogous to `INSERT_STUDENT` / `INSERT_VISIT`), otherwise inline SQL will be duplicated across every stub.

### Missing edge cases
- **`@BindList` with empty list** — `findStudentsWithId()` and `getMultipleStudentsVisits()` both use `@BindList`, which generates invalid SQL (`IN ()`) when passed an empty list and will throw a `JdbiException`. Decide whether callers are responsible for guarding against this or whether the DAO should handle it defensively. Document the decision and add a test reflecting the chosen behavior.
- `findStudent()` / `deleteStudent()` / `updateStudent()` with `studentId = 0` or a negative value.
- `getAllStudents()` after a mix of inserts and deletes — verifies deleted students are not returned.
- `deleteStudent()` with multiple associated visits — extend `testDeleteStudent_4` or add a new test to confirm all associated visits are removed.
- `insertStudent()` when the table previously had rows (auto-increment does not reset) — the current `testInsertStudent_1` assumes the generated ID will always be `1`.

---

## DatabaseManagerTest

### Broken / empty tests that need implementing
The following tests have `@DisplayName` strings describing real requirements but empty bodies. They pass trivially and cover nothing:
- `testDeleteStudent_2` — `NoSuchEntityException` thrown when DAO returns false.
- `testDeleteStudent_3` — labelled "NoSuchEntityException thrown if student has visits", but this isn't how `deleteStudent` works (it deletes the visits first); the label itself may be wrong and needs re-evaluation.
- `testUpdateStudent_1` — success path: observer fires with the updated student.
- `testUpdateStudent_2` — `NoSuchEntityException` thrown when DAO returns false.
- `testUpdateStudent_3` — `InvalidEntityException` thrown when `fullName` is invalid.
- `testUpdateStudent_4` — `InvalidEntityException` thrown when `preferredName` is invalid.
- `testUpdateStudent_5` — `InvalidEntityException` thrown when `visitTime` is invalid.

### Missing method tests
- `insertStudent()` with an invalid `visitTime` — currently commented out as `testInsertStudent_4`; the validation logic exists in `Student.validate()` and needs a test.
- `insertStudent()` with a null `studentId`, null `dateAdded`, null `fullName`, null `preferredName`, or null `visitTime` — each should throw `InvalidEntityException`.
- `updateStudent()` with a null `studentId` — should throw `InvalidEntityException` (the production code checks this explicitly).
- `deleteStudent()` — verify the `visitsObserver` is also triggered (not just `studentsObserver`).
- `insertVisit()` — success path (observer fires); invalid visit (throws `InvalidEntityException`).
- `deleteVisit()` — note: the production implementation unconditionally throws `NoSuchElementException` even on success; this bug is currently invisible because the method is untested.
- `findVisitsWithStudentId()` — verifies the call is passed through to the DAO.
- `startOngoingVisit()` — success path; verify `ongoingVisitsObserver` fires.
- `endOngoingVisit()` — success path; verify both `ongoingVisitsObserver` (delete) and `visitsObserver` (insert) fire with the correct data.
- `getOngoingVisits()` / `findOngoingVisit()` — pass-through to DAO.

### Missing edge cases / oversights
- **Observer payload is never verified.** All observer tests only confirm that the observer fired (via the `triggered` boolean), not what data it received. Key cases to add:
  - `insertStudent()` — the observer should receive the student with the **DAO-assigned ID**, not the original object passed to `insertStudent()`. A bug in the ID-rebuild step would go undetected.
  - `deleteStudent()` — the students observer should receive a `Student` with only `studentId` set; the visits observer should receive the list returned by `findVisitsWithStudentId`.
  - `endOngoingVisit()` — the visits observer should receive the ended visit with the DAO-assigned `visitId`.

---

## StudentTest

The test class is completely empty. `Student.validate()` is the only non-trivial logic in the class and should be tested directly so that failures are easy to diagnose rather than surfacing as side effects in `DatabaseManagerTest`.

### Missing method tests for `Student.validate()`

**fullName rules**
- `null` → false
- empty string `""` → false (min length is 1)
- single non-whitespace character → true (length 1 is the minimum)
- leading whitespace → false
- trailing whitespace → false
- exactly 150 characters → true (at the max)
- 151 characters → false (over the max)

**preferredName rules**
- `null` → false
- empty string `""` → true (min length is 0)
- leading whitespace → false
- trailing whitespace → false
- exactly 150 characters → true (at the max)
- 151 characters → false (over the max)

**visitTime rules**
- `30` → true
- `60` → true
- `0` → false
- `45` (arbitrary invalid value) → false
- `null` → false

**dateAdded rules**
- `null` → false

**Combined**
- All fields valid → true (a baseline "everything passes" test)

---

## QRScannerTest

### Missing method tests for `findQRCode()`
- Buffer contains no valid QR code → should return `null`. The null-return path is currently untested.
- Buffer is empty → should return `null`.
- Student ID is 11 digits → should not match (max is 10); `findQRCode()` should return `null`.
- Buffer contains the QR code with no surrounding noise (QR code fills the entire buffer).
- After `findQRCode()` runs, the buffer should be cleared — verify the buffer is empty regardless of whether a QR code was found.

### Missing method tests for `processQRCode()`
The method is completely untested. Needed cases:
- Valid QR code, ID matches checksum, no existing ongoing visit → `attendanceService.startOngoingVisit()` is called with the correct student ID.
- Valid QR code, ID matches checksum, ongoing visit already exists → `attendanceService.endOngoingVisit()` is called with the correct arguments.
- ID and checksum do not match → neither `startOngoingVisit` nor `endOngoingVisit` is called.
- `QRScanUtils.extractIDFromQR()` or `extractChecksumValueFromQR()` returns `null` → neither service method is called.

### Missing edge cases
- QR code at the very start of the buffer (index 0, no preceding characters).
- QR code at the very end of the buffer (no following characters).
- Buffer containing two valid QR patterns — verify only the first match is returned.
- Student ID that is exactly `Integer.MAX_VALUE` (10 digits, the boundary of the ID regex and `MAX_ID`).

### Bug in test utility
- **`QRCodeTestUtils.genAlphaHexNumStr`** uses `16 ^ length` which is bitwise XOR, not exponentiation. `16 ^ 8 = 24`, so `RANDOM.nextInt(24)` generates a number whose hex representation is at most 2 characters long — the remaining characters of the `length`-char result are always `'0'`. The tests still pass because the regex accepts zero-padded hex strings, but the utility does not actually generate meaningful variation in the checksum. Should be fixed to use `(int) Math.pow(16, length)` (for small lengths) or `1 << (4 * length)`.
