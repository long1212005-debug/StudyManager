#!/usr/bin/env python3
"""Static source acceptance checks. These checks do NOT replace emulator tests."""
from pathlib import Path
import sys
root=Path.cwd()
files={
 'app/src/main/java/vn/edu/studymanager/controller/MainActivity.java':['new Intent(MainActivity.this, TinhDiemCanThiActivity.class)'],
 'app/src/main/java/vn/edu/studymanager/controller/TinhDiemCanThiActivity.java':['DiemThiCalculator.tinhDiemThiCanDat','CauHinhTrongSoDAO','parseScore'],
 'app/src/main/java/vn/edu/studymanager/model/calculator/DiemThiCalculator.java':['tinhDiemThiCanDat','tinhDiemTongKet'],
 'app/src/main/AndroidManifest.xml':['.controller.TinhDiemCanThiActivity'],
 'app/src/main/res/layout/activity_tinh_diem_can_thi.xml':['@+id/edtTx1','@+id/edtTx2','@+id/edtTx3','@+id/edtMucTieu','@+id/btnTinhDiem','@+id/tvKetQua'],
 'app/src/test/java/vn/edu/studymanager/model/calculator/DiemThiCalculatorAcceptanceTest.java':['@Test'],
}
fail=[]
for name,patterns in files.items():
 p=root/name
 if not p.is_file():
  fail.append(name+' missing');continue
 s=p.read_text(encoding='utf-8-sig')
 for pattern in patterns:
  if pattern not in s:fail.append(name+' missing '+pattern)
if fail:
 print('FAIL:',*fail,sep='\n - ')
 sys.exit(1)
print(f'PASS {len(files)} source integration checks (static only).')
