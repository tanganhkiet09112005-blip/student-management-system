public class Sinhvien {
    private String Mssv;
    private String ten;
    private int nam;
    private String diachi;
    private String lop;
    
    public Sinhvien(String Mssv, String ten, int nam, String diachi, String lop ){
        this.Mssv = Mssv;
        this.ten = ten;
        this.nam = nam;
        this.diachi = diachi;
        this.lop = lop;
     }
     public String getMaSV() { return Mssv; }
    public void setMaSV(String Mssv) { this.Mssv = Mssv; }

    public String getHoTen() { return ten; }
    public void setHoTen(String ten) { this.ten = ten; }

    public int getNamSinh() { return nam; }
    public void setNamSinh(int nam) { this.nam = nam; }

    public String getDiaChi() { return diachi; }
    public void setDiaChi(String diachi) { this.diachi = diachi; }

    public String getLopHoc() { return lop; }
    public void setLopHoc(String lop) { this.lop = lop; }

// Gia sư thêm hàm này để lúc in danh sách ra màn hình sẽ dễ nhìn hơn
    @Override
    public String toString() {
        return "Ma SV: " + Mssv + " | ho va ten : " + ten + " | nam sinh: " + nam + 
               " | dia chi " + diachi + " | lop: " + lop;
    }
}

