import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TrangQuanTri extends JFrame {

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtMaSV, txtHoTen, txtLop, txtTimKiem;
    private JButton btnTaiDuLieu, btnThem, btnXoa, btnLamMoi;
    private JLabel lblTongSo;
    private TableRowSorter<DefaultTableModel> sorter;

    // Biến lưu trữ "Chìa khóa" (Key) của Firebase để biết đường Xóa đúng người
    private String selectedFirebaseKey = "";

    // QUAN TRỌNG: Link Firebase của ní
    private final String FIREBASE_URL = "https://quanlysinhvieniuh-default-rtdb.asia-southeast1.firebasedatabase.app/sinhvien.json";

    public TrangQuanTri() {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } 
        catch (Exception e) { e.printStackTrace(); }

        setTitle("NTC Workspace - Admin Panel (Trang Quản Trị Đám Mây)");
        setSize(1100, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(241, 245, 249));

        Font mainFont = new Font("Segoe UI", Font.PLAIN, 14);
        Font boldFont = new Font("Segoe UI", Font.BOLD, 14);

        // --- PHẦN TRÊN: TIÊU ĐỀ & TÌM KIẾM ---
        JPanel panelTop = new JPanel(new BorderLayout(20, 0));
        panelTop.setBackground(new Color(15, 23, 42)); // Xám đen doanh nghiệp
        panelTop.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel("QUẢN TRỊ DỮ LIỆU ĐĂNG KÝ");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTitle.setForeground(Color.WHITE);

        // Thanh tìm kiếm siêu tốc
        JPanel panelSearch = new JPanel(new BorderLayout(10, 0));
        panelSearch.setOpaque(false);
        JLabel lblSearch = new JLabel("🔍 Lọc danh sách (Tên, Lớp...): ");
        lblSearch.setForeground(Color.WHITE);
        lblSearch.setFont(boldFont);
        txtTimKiem = new JTextField(20);
        txtTimKiem.setFont(mainFont);
        panelSearch.add(lblSearch, BorderLayout.WEST);
        panelSearch.add(txtTimKiem, BorderLayout.CENTER);

        panelTop.add(lblTitle, BorderLayout.WEST);
        panelTop.add(panelSearch, BorderLayout.EAST);
        add(panelTop, BorderLayout.NORTH);

        // --- PANEL TRÁI: FORM THÊM & XÓA ---
        JPanel panelLeft = new JPanel(new BorderLayout(10, 10));
        panelLeft.setPreferredSize(new Dimension(300, 0));
        panelLeft.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panelLeft.setBackground(Color.WHITE);

        JPanel panelInput = new JPanel(new GridLayout(6, 1, 5, 5));
        panelInput.setBackground(Color.WHITE);
        panelInput.add(new JLabel("Mã Sinh Viên / Nhân Sự:"));
        txtMaSV = new JTextField(); txtMaSV.setFont(mainFont); panelInput.add(txtMaSV);
        panelInput.add(new JLabel("Họ và Tên:"));
        txtHoTen = new JTextField(); txtHoTen.setFont(mainFont); panelInput.add(txtHoTen);
        panelInput.add(new JLabel("Đơn vị / Lớp Học:"));
        txtLop = new JTextField(); txtLop.setFont(mainFont); panelInput.add(txtLop);
        panelLeft.add(panelInput, BorderLayout.NORTH);

        JPanel panelButtons = new JPanel(new GridLayout(4, 1, 10, 10));
        panelButtons.setBackground(Color.WHITE);
        
        btnThem = new JButton("Thêm Sinh Viên"); 
        btnThem.setBackground(new Color(16, 185, 129)); btnThem.setForeground(Color.WHITE); btnThem.setFont(boldFont);
        
        btnXoa = new JButton("Xóa Dữ Liệu Chọn"); 
        btnXoa.setBackground(new Color(239, 68, 68)); btnXoa.setForeground(Color.WHITE); btnXoa.setFont(boldFont);
        btnXoa.setEnabled(false); // Khóa lại, chỉ mở khi click vào bảng
        
        btnLamMoi = new JButton("Làm Mới Form"); btnLamMoi.setFont(boldFont);
        
        btnTaiDuLieu = new JButton("Tải Dữ Liệu Từ Mây"); 
        btnTaiDuLieu.setBackground(new Color(37, 99, 235)); btnTaiDuLieu.setForeground(Color.WHITE); btnTaiDuLieu.setFont(boldFont);

        panelButtons.add(btnThem);
        panelButtons.add(btnXoa);
        panelButtons.add(btnLamMoi);
        panelButtons.add(btnTaiDuLieu);
        panelLeft.add(panelButtons, BorderLayout.SOUTH);

        add(panelLeft, BorderLayout.WEST);

        // --- PHẦN GIỮA: BẢNG DỮ LIỆU ---
        // Thêm cột thứ 6 (Cột ẩn) để lưu Firebase Key
        String[] columns = {"STT", "Mã SV", "Họ và Tên", "Lớp / Đơn vị", "Thời gian đăng ký", "FirebaseKey"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        table = new JTable(tableModel);
        table.setFont(mainFont); table.setRowHeight(30);
        table.getTableHeader().setFont(boldFont);
        table.getTableHeader().setBackground(new Color(226, 232, 240));

        // Ẩn cột FirebaseKey đi (không cho người dùng thấy, nhưng máy tính vẫn biết)
        table.getColumnModel().getColumn(5).setMinWidth(0);
        table.getColumnModel().getColumn(5).setMaxWidth(0);
        table.getColumnModel().getColumn(5).setWidth(0);

        // Cài bộ lọc tìm kiếm
        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // --- PHẦN DƯỚI: THANH TRẠNG THÁI ---
        JPanel panelBottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelBottom.setBackground(new Color(241, 245, 249));
        lblTongSo = new JLabel("Tổng số hồ sơ: 0");
        lblTongSo.setFont(boldFont);
        panelBottom.add(lblTongSo);
        add(panelBottom, BorderLayout.SOUTH);

        // --- GẮN SỰ KIỆN ---
        addEvents();
        
        // Tự động tải dữ liệu khi vừa mở app
        taiDuLieuNen();
    }

    private void addEvents() {
        // TÌM KIẾM THEO LỚP, TÊN (LIVE SEARCH)
        txtTimKiem.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { sorter.setRowFilter(RowFilter.regexFilter("(?i)" + txtTimKiem.getText())); }
            @Override public void removeUpdate(DocumentEvent e) { sorter.setRowFilter(RowFilter.regexFilter("(?i)" + txtTimKiem.getText())); }
            @Override public void changedUpdate(DocumentEvent e) { sorter.setRowFilter(RowFilter.regexFilter("(?i)" + txtTimKiem.getText())); }
        });

        // NÚT LÀM MỚI FORM
        btnLamMoi.addActionListener(e -> {
            txtMaSV.setText(""); txtHoTen.setText(""); txtLop.setText("");
            selectedFirebaseKey = "";
            btnXoa.setEnabled(false);
            table.clearSelection();
        });

        // CLICK BẢNG LẤY DỮ LIỆU & FIREBASE KEY
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int viewRow = table.getSelectedRow();
                if (viewRow >= 0) {
                    int modelRow = table.convertRowIndexToModel(viewRow);
                    txtMaSV.setText(tableModel.getValueAt(modelRow, 1).toString());
                    txtHoTen.setText(tableModel.getValueAt(modelRow, 2).toString());
                    txtLop.setText(tableModel.getValueAt(modelRow, 3).toString());
                    
                    // Lấy Firebase Key từ cột ẩn
                    selectedFirebaseKey = tableModel.getValueAt(modelRow, 5).toString();
                    btnXoa.setEnabled(true); // Mở khóa nút xóa
                }
            }
        });

        // NÚT TẢI DỮ LIỆU
        btnTaiDuLieu.addActionListener(e -> taiDuLieuNen());

        // NÚT THÊM SINH VIÊN (POST LÊN FIREBASE)
        btnThem.addActionListener(e -> {
            String maSV = txtMaSV.getText().trim();
            String hoTen = txtHoTen.getText().trim();
            String lop = txtLop.getText().trim();

            if (maSV.isEmpty() || hoTen.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đủ Mã và Tên!"); return;
            }

            btnThem.setText("Đang đẩy lên mây..."); btnThem.setEnabled(false);
            
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() {
                    themLenFirebase(maSV, hoTen, lop);
                    return null;
                }
                @Override
                protected void done() {
                    btnThem.setText("Thêm Sinh Viên"); btnThem.setEnabled(true);
                    taiDuLieuNen(); // Tải lại bảng
                    btnLamMoi.doClick(); // Xóa form
                    JOptionPane.showMessageDialog(TrangQuanTri.this, "Đã thêm sinh viên lên Firebase!");
                }
            }.execute();
        });

        // NÚT XÓA SINH VIÊN (DELETE TRÊN FIREBASE)
        btnXoa.addActionListener(e -> {
            if(selectedFirebaseKey.isEmpty()) return;

            int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa vĩnh viễn sinh viên này khỏi Đám mây?", "Cảnh báo Xóa", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                btnXoa.setText("Đang xóa..."); btnXoa.setEnabled(false);
                
                new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() {
                        xoaTrenFirebase(selectedFirebaseKey);
                        return null;
                    }
                    @Override
                    protected void done() {
                        btnXoa.setText("Xóa Dữ Liệu Chọn");
                        taiDuLieuNen(); // Tải lại bảng
                        btnLamMoi.doClick(); // Xóa form
                        JOptionPane.showMessageDialog(TrangQuanTri.this, "Đã xóa thành công!");
                    }
                }.execute();
            }
        });
    }

    // =========================================================================
    // CÁC HÀM GIAO TIẾP FIREBASE (GET, POST, DELETE)
    // =========================================================================

    private void taiDuLieuNen() {
        btnTaiDuLieu.setText("Đang tải..."); btnTaiDuLieu.setEnabled(false);
        new SwingWorker<Void, Void>() {
            @Override protected Void doInBackground() { layDuLieuTuFirebase(); return null; }
            @Override protected void done() { btnTaiDuLieu.setText("Tải Dữ Liệu Từ Mây"); btnTaiDuLieu.setEnabled(true); }
        }.execute();
    }

    private void layDuLieuTuFirebase() {
        try {
            URL url = new URL(FIREBASE_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            if (conn.getResponseCode() == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                StringBuilder response = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) response.append(inputLine);
                in.close();

                String json = response.toString();
                if(json.equals("null")) {
                    SwingUtilities.invokeLater(() -> { tableModel.setRowCount(0); lblTongSo.setText("Tổng số hồ sơ: 0"); });
                    return;
                }

                SwingUtilities.invokeLater(() -> tableModel.setRowCount(0));

                int stt = 1;
                // Thuật toán Regex nâng cấp: Bắt cả Key của Firebase (Ví dụ: "-Nxyz123") và Dữ liệu bên trong
                Matcher mObj = Pattern.compile("\"([a-zA-Z0-9_-]+)\":\\{([^{}]+)\\}").matcher(json);
                
                while (mObj.find()) {
                    String firebaseKey = mObj.group(1); // Lấy "Chìa khóa"
                    String rowData = mObj.group(2);     // Lấy thông tin
                    
                    String maSV = layGiaTri(rowData, "maSV");
                    String hoTen = layGiaTri(rowData, "hoTen");
                    String lopHoc = layGiaTri(rowData, "lopHoc");
                    String time = layGiaTri(rowData, "timestamp");
                    if(time.length() > 10) time = time.substring(0, 10) + " " + time.substring(11, 19);

                    // Đưa firebaseKey vào cột ẩn (Cột số 5)
                    Object[] row = {stt++, maSV, hoTen, lopHoc, time, firebaseKey};
                    SwingUtilities.invokeLater(() -> tableModel.addRow(row));
                }

                int finalTongSo = stt - 1;
                SwingUtilities.invokeLater(() -> lblTongSo.setText("Tổng số hồ sơ: " + finalTongSo));
            }
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private void themLenFirebase(String ma, String ten, String lop) {
        try {
            URL url = new URL(FIREBASE_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST"); // POST để tạo mới
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // Tạo chuỗi JSON thủ công
            String jsonInputString = String.format(
                "{\"maSV\": \"%s\", \"hoTen\": \"%s\", \"lopHoc\": \"%s\", \"timestamp\": \"%s\"}", 
                ma, ten, lop, java.time.Instant.now().toString()
            );

            try(OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
            conn.getResponseCode(); // Gọi để thực thi lệnh
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void xoaTrenFirebase(String key) {
        try {
            // Thay thế đuôi .json bằng thư mục chứa ID của sinh viên: /sinhvien/-Nxyz123.json
            String deleteUrl = FIREBASE_URL.replace(".json", "/" + key + ".json");
            URL url = new URL(deleteUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("DELETE"); // DELETE để xóa
            conn.getResponseCode(); // Gọi để thực thi lệnh
        } catch (Exception e) { e.printStackTrace(); }
    }

    private String layGiaTri(String source, String key) {
        Matcher m = Pattern.compile("\"" + key + "\":\"?([^\",}]+)\"?").matcher(source);
        if (m.find()) return m.group(1);
        return "";
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TrangQuanTri().setVisible(true));
    }
}