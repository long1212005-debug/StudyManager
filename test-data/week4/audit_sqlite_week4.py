"""SQLite-only integration audit. No Android emulator or app database is accessed.
Run: py -3 test-data/week4/audit_sqlite_week4.py
"""
import sqlite3

QUERY = """
SELECT COALESCE(SUM(best_score * credits), 0),
       COALESCE(SUM(credits), 0),
       COALESCE(SUM(CASE WHEN best_score > 0 THEN credits ELSE 0 END), 0)
FROM (
    SELECT LOWER(TRIM(m.ten_mon)) AS subject_key,
           MAX(m.diem_tong_ket_4) AS best_score,
           MAX(m.so_tin_chi) AS credits
    FROM MonHoc m
    JOIN KyHoc k ON k.id = m.ky_hoc_id
    WHERE k.sinh_vien_id = ?
      AND m.diem_chu IS NOT NULL AND TRIM(m.diem_chu) != ''
      AND m.diem_tong_ket_4 BETWEEN 0 AND 4
      AND m.so_tin_chi > 0
    GROUP BY LOWER(TRIM(m.ten_mon))
)
"""

def main():
    db = sqlite3.connect(":memory:")
    db.execute("PRAGMA foreign_keys=ON")
    db.executescript("""
        CREATE TABLE SinhVien (id INTEGER PRIMARY KEY);
        CREATE TABLE KyHoc (id INTEGER PRIMARY KEY, sinh_vien_id INTEGER REFERENCES SinhVien(id));
        CREATE TABLE MonHoc (
            id INTEGER PRIMARY KEY, ky_hoc_id INTEGER REFERENCES KyHoc(id),
            ten_mon TEXT, so_tin_chi INTEGER, diem_tong_ket_4 REAL,
            diem_chu TEXT, UNIQUE(ten_mon,ky_hoc_id));
        INSERT INTO SinhVien VALUES (1),(2);
        INSERT INTO KyHoc VALUES (10,1),(11,1),(20,2);
        INSERT INTO MonHoc VALUES
            (1,10,'Java',3,2.0,'C'),
            (2,11,'Java',3,4.0,'A'),
            (3,11,'Math',2,0.0,'F'),
            (4,20,'Java',3,1.0,'D'),
            (5,20,'Math',2,4.0,'A'),
            (6,11,'Incomplete',3,NULL,NULL);
    """)
    a = db.execute(QUERY, (1,)).fetchone()
    b = db.execute(QUERY, (2,)).fetchone()
    assert a == (12, 5, 3), f"Sinh vien 1: {a}"
    assert b == (11, 5, 5), f"Sinh vien 2: {b}"
    assert abs(a[0]/a[1] - 2.4) < 1e-9
    assert abs(b[0]/b[1] - 2.2) < 1e-9
    assert db.execute("PRAGMA foreign_key_check").fetchall() == []
    try:
        db.execute("INSERT INTO MonHoc VALUES (7,99,'Fail FK',3,4,'A')")
        raise AssertionError("Foreign key unexpectedly accepted")
    except sqlite3.IntegrityError:
        pass
    try:
        db.execute("INSERT INTO MonHoc VALUES (7,11,'Java',3,4,'A')")
        raise AssertionError("Duplicate course unexpectedly accepted")
    except sqlite3.IntegrityError:
        pass
    print("PASS 6 SQLite checks: student isolation, retake, F credits, pending course, FK, UNIQUE")
    print("SV1 CPA=2.40; completed credits=3; SV2 CPA=2.20; completed credits=5")

if __name__ == "__main__":
    main()
