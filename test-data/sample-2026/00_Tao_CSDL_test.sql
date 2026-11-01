PRAGMA foreign_keys=ON;
CREATE TABLE SinhVien(id INTEGER PRIMARY KEY AUTOINCREMENT,ho_ten TEXT,mssv TEXT,cpa_hien_tai REAL,tong_tin_chi_tich_luy INTEGER,muc_tieu_cpa REAL,truong_dao_tao TEXT DEFAULT 'HaUI');
CREATE TABLE KyHoc(id INTEGER PRIMARY KEY AUTOINCREMENT,sinh_vien_id INTEGER,ten_ky TEXT,gpa_ky REAL,tong_tin_chi_ky INTEGER,trang_thai INTEGER,FOREIGN KEY(sinh_vien_id) REFERENCES SinhVien(id));
CREATE TABLE MonHoc(id INTEGER PRIMARY KEY AUTOINCREMENT,ky_hoc_id INTEGER,ten_mon TEXT,so_tin_chi INTEGER,diem_tx1 REAL,diem_tx2 REAL,diem_tx3 REAL,diem_thi REAL,diem_tong_ket_10 REAL,diem_tong_ket_4 REAL,diem_chu TEXT,trang_thai TEXT,UNIQUE(ten_mon,ky_hoc_id),FOREIGN KEY(ky_hoc_id) REFERENCES KyHoc(id));
CREATE TABLE CauHinhTrongSo(id INTEGER PRIMARY KEY AUTOINCREMENT,ten_he_dao_tao TEXT,trong_so_tx1 REAL,trong_so_tx2 REAL,trong_so_tx3 REAL,trong_so_thi REAL);
