import java.util.ArrayList;
import java.util.Scanner;

public class testsv {
    private static Scanner scanner = new Scanner(System.in);
    private static quanlysnhvien qlDanhSach = new quanlysnhvien();

    public static void main(String[] args) {
        int luaChon = 0;
        do {
            System.out.println("\n===== CHUONG TRINH QUAN LY SINH VIEN =====");
            System.out.println("1. Them sinh vien");
            System.out.println("2. Sua thong tin sinh vien");
            System.out.println("3. Xoa sinh vien");
            System.out.println("4. Xuat so luong sinh vien");
            System.out.println("5. Xuat danh sach sinh vien theo lop");
            System.out.println("6. Hien thi toan bo danh sach");
            System.out.println("0. Thoat");
            System.out.print("Nhap lua chon: ");
            
            try {
                luaChon = Integer.parseInt(scanner.nextLine());
            } catch (Exception e) {
                System.out.println("Loi nhap lieu!");
                continue;
            }

            switch (luaChon) {
                case 1: themMoi(); break;
                case 2: suaThongTin(); break;
                case 3: xoaSV(); break;
                case 4: 
                    System.out.println("So luong: " + qlDanhSach.getSoLuongSinhVien());
                    break;
                case 5: timTheoLop(); break;
                case 6: inDanhSach(qlDanhSach.getTatCaSinhVien()); break;
                case 0: System.out.println("Ket thuc!"); break;
                default: System.out.println("Sai lua chon!");
            }
        } while (luaChon != 0);
    }

    // ================= FIX CHINH O DAY =================
    private static void themMoi() {
        System.out.print("Nhap ma SV: ");
        String maSV = scanner.nextLine().trim();

        // kiểm tra trùng trước
        if (qlDanhSach.ktTrungma(maSV)) {
            System.out.println("=> Ma SV da ton tai!");
            return;
        }

        System.out.print("Nhap ho ten: ");
        String hoTen = scanner.nextLine().trim();
        
        int nam = 0;
        while (true) {
            try {
                System.out.print("Nhap nam sinh: ");
                nam = Integer.parseInt(scanner.nextLine().trim());
                break;
            } catch (Exception e) {
                System.out.println("Nam phai la so!");
            }
        }

        System.out.print("Nhap dia chi: ");
        String diaChi = scanner.nextLine().trim();

        System.out.print("Nhap lop: ");
        String lop = scanner.nextLine().trim();

        Sinhvien sv = new Sinhvien(maSV, hoTen, nam, diaChi, lop);

        boolean kq = qlDanhSach.addSinhvien(sv);
        System.out.println("DEBUG add = " + kq); // debug

        if (kq) {
            System.out.println("=> Them thanh cong!");
        } else {
            System.out.println("=> Them that bai!");
        }
    }

    private static void suaThongTin() {
        System.out.print("Nhap ma SV can sua: ");
        String maSV = scanner.nextLine().trim();
        
        int index = qlDanhSach.findSinhvien2(maSV);
        
        if (index != -1) {
            System.out.print("Ten moi: ");
            String hoTen = scanner.nextLine().trim();
            
            int nam = Integer.parseInt(scanner.nextLine().trim());

            System.out.print("Dia chi moi: ");
            String diaChi = scanner.nextLine().trim();

            System.out.print("Lop moi: ");
            String lop = scanner.nextLine().trim();

            Sinhvien svMoi = new Sinhvien(maSV, hoTen, nam, diaChi, lop);
            qlDanhSach.updateSinhvien(index, svMoi);

            System.out.println("=> Sua thanh cong!");
        } else {
            System.out.println("=> Khong tim thay!");
        }
    }

    private static void xoaSV() {
        System.out.print("Nhap ma SV can xoa: ");
        String maSV = scanner.nextLine().trim();
        
        if (qlDanhSach.ktTrungma(maSV)) {
            qlDanhSach.removeSinhvien(maSV);
            System.out.println("=> Da xoa!");
        } else {
            System.out.println("=> Khong ton tai!");
        }
    }

    private static void timTheoLop() {
        System.out.print("Nhap lop: ");
        String lop = scanner.nextLine().trim();

        ArrayList<Sinhvien> ketQua = qlDanhSach.timSinhVienTheoLop(lop);

        inDanhSach(ketQua);
    }

    private static void inDanhSach(ArrayList<Sinhvien> list) {
        if (list == null || list.size() == 0) {
            System.out.println("Danh sach trong!");
            return;
        }

        for (Sinhvien sv : list) {
            System.out.println(sv);
        }
    }
}