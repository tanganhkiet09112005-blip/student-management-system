import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class FormDangKy extends JFrame {
    
    private final Color CLR_BACKGROUND = new Color(248, 250, 252);
    private final Color CLR_ACCENT = new Color(59, 130, 246);
    private final Color CLR_TEXT = new Color(51, 65, 85);

    private JTextField txtMaSV, txtHoTen, txtNamSinh, txtDiaChi, txtLop;
    private JButton btnDangKy, btnHuy;

    public FormDangKy() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) { e.printStackTrace(); }

        setTitle("Cổng Đăng Ký Sinh Viên - v2.0");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- PANEL TRÁI ---
        JPanel panelLeft = new JPanel(new GridBagLayout());
        panelLeft.setBackground(CLR_ACCENT);
        panelLeft.setPreferredSize(new Dimension(300, 0));
        GridBagConstraints gbcL = new GridBagConstraints();
        gbcL.insets = new Insets(10, 20, 10, 20);

        JLabel lblLogo = new JLabel("IUH");
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 48));
        lblLogo.setForeground(Color.WHITE);
        gbcL.gridx = 0; gbcL.gridy = 0; panelLeft.add(lblLogo, gbcL);

        JLabel lblSubTitle = new JLabel("CỔNG THÔNG TIN SINH VIÊN");
        lblSubTitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblSubTitle.setForeground(Color.WHITE);
        gbcL.gridy = 1; panelLeft.add(lblSubTitle, gbcL);

        add(panelLeft, BorderLayout.WEST);

        // --- PANEL PHẢI ---
        JPanel panelRight = new JPanel(new GridBagLayout());
        panelRight.setBackground(CLR_BACKGROUND);
        panelRight.setBorder(new EmptyBorder(30, 40, 30, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JLabel lblTitle = new JLabel("ĐĂNG KÝ MỚI");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(CLR_TEXT);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panelRight.add(lblTitle, gbc);

        gbc.gridwidth = 1;
        createInputField(panelRight, gbc, "Mã Sinh Viên (*):", txtMaSV = new JTextField(), 1);
        createInputField(panelRight, gbc, "Họ và Tên (*):", txtHoTen = new JTextField(), 2);
        createInputField(panelRight, gbc, "Năm Sinh:", txtNamSinh = new JTextField(), 3);
        createInputField(panelRight, gbc, "Địa Chỉ:", txtDiaChi = new JTextField(), 4);
        createInputField(panelRight, gbc, "Lớp Học:", txtLop = new JTextField(), 5);

        JPanel panelButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 20));
        panelButtons.setBackground(CLR_BACKGROUND);
        
        btnDangKy = new JButton("Đăng Ký Ngay");
        btnDangKy.setBackground(new Color(46, 204, 113));
        btnDangKy.setForeground(Color.WHITE);
        btnDangKy.setPreferredSize(new Dimension(140, 40));

        btnHuy = new JButton("Hủy Bỏ");
        btnHuy.setBackground(new Color(149, 165, 166));
        btnHuy.setForeground(Color.WHITE);
        btnHuy.setPreferredSize(new Dimension(100, 40));

        panelButtons.add(btnDangKy); panelButtons.add(btnHuy);
        
        gbc.gridy = 6; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.NONE; gbc.anchor = GridBagConstraints.EAST;
        panelRight.add(panelButtons, gbc);

        add(panelRight, BorderLayout.CENTER);
        addEvents();
    }

    private void createInputField(JPanel panel, GridBagConstraints gbc, String labelText, JTextField textField, int row) {
        JLabel label = new JLabel(labelText);
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        panel.add(label, gbc);

        textField.setBorder(new MatteBorder(0, 0, 2, 0, CLR_ACCENT));
        textField.setBackground(CLR_BACKGROUND);
        gbc.gridx = 1; gbc.weightx = 0.7;
        panel.add(textField, gbc);
    }

    private void addEvents() {
        btnHuy.addActionListener(e -> {
            txtMaSV.setText(""); txtHoTen.setText(""); txtNamSinh.setText(""); txtDiaChi.setText(""); txtLop.setText("");
        });

        btnDangKy.addActionListener(e -> {
            String ma = txtMaSV.getText().trim();
            String ten = txtHoTen.getText().trim();
            String nam = txtNamSinh.getText().trim();
            String diaChi = txtDiaChi.getText().trim();
            String lop = txtLop.getText().trim();

            if (ma.isEmpty() || ten.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Ní ơi, nhập Mã và Tên giúp tui với!");
                return;
            }

            // TẠO DỮ LIỆU JSON
            String jsonData = String.format(
                "{\"maSV\":\"%s\", \"hoTen\":\"%s\", \"namSinh\":%s, \"diaChi\":\"%s\", \"lopHoc\":\"%s\"}",
                ma, ten, nam, diaChi, lop
            );

            try {
                // LINK ĐÃ ĐƯỢC CHỈNH SỬA CHUẨN API
                String myURL = "https://quanlysinhvieniuh-default-rtdb.asia-southeast1.firebasedatabase.app/sinhvien.json";
                
                URL url = new URL(myURL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/json");

                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonData.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                if (conn.getResponseCode() == 200) {
                    JOptionPane.showMessageDialog(this, "Đỉnh quá ní ơi! Đăng ký thành công.");
                    btnHuy.doClick();
                } else {
                    JOptionPane.showMessageDialog(this, "Lỗi rồi, mã lỗi: " + conn.getResponseCode());
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi kết nối: " + ex.getMessage());
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FormDangKy().setVisible(true));
    }
}