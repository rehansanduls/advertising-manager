package com.advertising;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;

// ----------------------- Database Manager -----------------------
class DatabaseManager {
    private static final String URL = "jdbc:mysql://localhost:3306/ad_firm_db?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "1234";

    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            conn.setAutoCommit(true);
            System.out.println("Database connected successfully!");
            return conn;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "MySQL JDBC Driver not found!\n" + e.getMessage(),
                    "Driver Error",
                    JOptionPane.ERROR_MESSAGE);
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Database connection failed!\n" + e.getMessage(),
                    "Connection Error",
                    JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    public static void closeConnection(Connection conn) {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

// ----------------------- Client Class -----------------------
class Client {
    private int id;
    private String clientName;
    private String contactPerson;
    private String phoneNumber;
    private String email;
    private String address;
    private String city;
    private String companyType;

    public Client(int id, String clientName, String contactPerson, String phoneNumber,
            String email, String address, String city, String companyType) {
        this.id = id;
        this.clientName = clientName;
        this.contactPerson = contactPerson;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.address = address;
        this.city = city;
        this.companyType = companyType;
    }

    public int getId() {
        return id;
    }

    public String getClientName() {
        return clientName;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public String getCompanyType() {
        return companyType;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setCompanyType(String companyType) {
        this.companyType = companyType;
    }

    @Override
    public String toString() {
        return clientName;
    }
}

// ----------------------- Board Order Class -----------------------
class BoardOrder {
    private int id;
    private int clientId;
    private String clientName;
    private String boardType;
    private String boardSize;
    private String location;
    private String startDate;
    private String endDate;
    private int quantity;
    private double pricePerBoard;
    private String status;
    private String notes;

    public BoardOrder(int id, int clientId, String clientName, String boardType, String boardSize,
            String location, String startDate, String endDate, int quantity,
            double pricePerBoard, String status, String notes) {
        this.id = id;
        this.clientId = clientId;
        this.clientName = clientName;
        this.boardType = boardType;
        this.boardSize = boardSize;
        this.location = location;
        this.startDate = startDate;
        this.endDate = endDate;
        this.quantity = quantity;
        this.pricePerBoard = pricePerBoard;
        this.status = status;
        this.notes = notes;
    }

    public int getId() {
        return id;
    }

    public int getClientId() {
        return clientId;
    }

    public String getClientName() {
        return clientName;
    }

    public String getBoardType() {
        return boardType;
    }

    public String getBoardSize() {
        return boardSize;
    }

    public String getLocation() {
        return location;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPricePerBoard() {
        return pricePerBoard;
    }

    public String getStatus() {
        return status;
    }

    public String getNotes() {
        return notes;
    }

    public double getTotalPrice() {
        return quantity * pricePerBoard;
    }
}

// ----------------------- Data Access Object (DAO) -----------------------
class DataManager {

    public static ArrayList<Client> getAllClients() {
        ArrayList<Client> clients = new ArrayList<>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseManager.getConnection();
            if (conn == null) return clients;
            
            String query = "SELECT * FROM clients ORDER BY client_name";
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);

            while (rs.next()) {
                clients.add(new Client(
                        rs.getInt("id"),
                        rs.getString("client_name"),
                        rs.getString("contact_person"),
                        rs.getString("phone_number"),
                        rs.getString("email"),
                        rs.getString("address"),
                        rs.getString("city"),
                        rs.getString("company_type")));
            }
        } catch (SQLException e) {
            System.err.println("SQL Error in getAllClients: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading clients: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return clients;
    }

    public static boolean addClient(String clientName, String contactPerson, String phoneNumber,
            String email, String address, String city, String companyType) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DatabaseManager.getConnection();
            if (conn == null) {
                System.err.println("Failed to get database connection");
                return false;
            }
            
            String query = "INSERT INTO clients (client_name, contact_person, phone_number, email, address, city, company_type) VALUES (?, ?, ?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, clientName);
            pstmt.setString(2, contactPerson);
            pstmt.setString(3, phoneNumber);
            pstmt.setString(4, email);
            pstmt.setString(5, address);
            pstmt.setString(6, city);
            pstmt.setString(7, companyType);

            int result = pstmt.executeUpdate();
            System.out.println("Add Client - Rows affected: " + result);
            return result > 0;
        } catch (SQLException e) {
            System.err.println("SQL Error in addClient: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error adding client:\n" + e.getMessage());
            return false;
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean updateClient(int id, String clientName, String contactPerson,
            String phoneNumber, String email, String address,
            String city, String companyType) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DatabaseManager.getConnection();
            if (conn == null) {
                System.err.println("Failed to get database connection");
                return false;
            }
            
            String query = "UPDATE clients SET client_name=?, contact_person=?, phone_number=?, email=?, address=?, city=?, company_type=? WHERE id=?";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, clientName);
            pstmt.setString(2, contactPerson);
            pstmt.setString(3, phoneNumber);
            pstmt.setString(4, email);
            pstmt.setString(5, address);
            pstmt.setString(6, city);
            pstmt.setString(7, companyType);
            pstmt.setInt(8, id);

            int result = pstmt.executeUpdate();
            System.out.println("Update Client - Rows affected: " + result);
            return result > 0;
        } catch (SQLException e) {
            System.err.println("SQL Error in updateClient: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error updating client:\n" + e.getMessage());
            return false;
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean deleteClient(int id) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DatabaseManager.getConnection();
            if (conn == null) {
                System.err.println("Failed to get database connection");
                return false;
            }
            
            String query = "DELETE FROM clients WHERE id=?";
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, id);

            int result = pstmt.executeUpdate();
            System.out.println("Delete Client - Rows affected: " + result);
            return result > 0;
        } catch (SQLException e) {
            System.err.println("SQL Error in deleteClient: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error deleting client:\n" + e.getMessage());
            return false;
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static ArrayList<BoardOrder> getAllBoardOrders() {
        ArrayList<BoardOrder> orders = new ArrayList<>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseManager.getConnection();
            if (conn == null) return orders;
            
            String query = "SELECT bo.*, c.client_name FROM board_orders bo " +
                    "JOIN clients c ON bo.client_id = c.id " +
                    "ORDER BY bo.start_date DESC";
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);

            while (rs.next()) {
                orders.add(new BoardOrder(
                        rs.getInt("id"),
                        rs.getInt("client_id"),
                        rs.getString("client_name"),
                        rs.getString("board_type"),
                        rs.getString("board_size"),
                        rs.getString("location"),
                        rs.getString("start_date"),
                        rs.getString("end_date"),
                        rs.getInt("quantity"),
                        rs.getDouble("price_per_board"),
                        rs.getString("status"),
                        rs.getString("notes")));
            }
        } catch (SQLException e) {
            System.err.println("SQL Error in getAllBoardOrders: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading orders: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return orders;
    }

    public static boolean addBoardOrder(int clientId, String boardType, String boardSize,
            String location, String startDate, String endDate,
            int quantity, double pricePerBoard, String status, String notes) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DatabaseManager.getConnection();
            if (conn == null) {
                System.err.println("Failed to get database connection");
                return false;
            }
            
            String query = "INSERT INTO board_orders (client_id, board_type, board_size, location, start_date, end_date, quantity, price_per_board, status, notes) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, clientId);
            pstmt.setString(2, boardType);
            pstmt.setString(3, boardSize);
            pstmt.setString(4, location);
            pstmt.setString(5, startDate);
            pstmt.setString(6, endDate);
            pstmt.setInt(7, quantity);
            pstmt.setDouble(8, pricePerBoard);
            pstmt.setString(9, status);
            pstmt.setString(10, notes);

            int result = pstmt.executeUpdate();
            System.out.println("Add Board Order - Rows affected: " + result);
            return result > 0;
        } catch (SQLException e) {
            System.err.println("SQL Error in addBoardOrder: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error adding order:\n" + e.getMessage());
            return false;
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean updateBoardOrder(int id, int clientId, String boardType, String boardSize,
            String location, String startDate, String endDate,
            int quantity, double pricePerBoard, String status, String notes) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DatabaseManager.getConnection();
            if (conn == null) {
                System.err.println("Failed to get database connection");
                return false;
            }
            
            String query = "UPDATE board_orders SET client_id=?, board_type=?, board_size=?, location=?, start_date=?, end_date=?, quantity=?, price_per_board=?, status=?, notes=? WHERE id=?";
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, clientId);
            pstmt.setString(2, boardType);
            pstmt.setString(3, boardSize);
            pstmt.setString(4, location);
            pstmt.setString(5, startDate);
            pstmt.setString(6, endDate);
            pstmt.setInt(7, quantity);
            pstmt.setDouble(8, pricePerBoard);
            pstmt.setString(9, status);
            pstmt.setString(10, notes);
            pstmt.setInt(11, id);

            int result = pstmt.executeUpdate();
            System.out.println("Update Board Order - Rows affected: " + result);
            return result > 0;
        } catch (SQLException e) {
            System.err.println("SQL Error in updateBoardOrder: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error updating order:\n" + e.getMessage());
            return false;
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean deleteBoardOrder(int id) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DatabaseManager.getConnection();
            if (conn == null) {
                System.err.println("Failed to get database connection");
                return false;
            }
            
            String query = "DELETE FROM board_orders WHERE id=?";
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, id);

            int result = pstmt.executeUpdate();
            System.out.println("Delete Board Order - Rows affected: " + result);
            return result > 0;
        } catch (SQLException e) {
            System.err.println("SQL Error in deleteBoardOrder: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error deleting order:\n" + e.getMessage());
            return false;
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static int[] getOrderStatistics() {
        int[] stats = new int[4];
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseManager.getConnection();
            if (conn == null) return stats;
            
            String query = "SELECT status, COUNT(*) as count FROM board_orders GROUP BY status";
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);

            while (rs.next()) {
                String status = rs.getString("status");
                int count = rs.getInt("count");

                switch (status) {
                    case "Pending":
                        stats[0] = count;
                        break;
                    case "In Progress":
                    case "Installed":
                        stats[1] += count;
                        break;
                    case "Completed":
                        stats[2] = count;
                        break;
                }
                stats[3] += count;
            }
        } catch (SQLException e) {
            System.err.println("SQL Error in getOrderStatistics: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return stats;
    }

    public static double getTotalRevenue() {
        double total = 0;
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseManager.getConnection();
            if (conn == null) return total;
            
            String query = "SELECT SUM(quantity * price_per_board) as total FROM board_orders";
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);

            if (rs.next()) {
                total = rs.getDouble("total");
            }
        } catch (SQLException e) {
            System.err.println("SQL Error in getTotalRevenue: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return total;
    }
}

// ----------------------- Dashboard -----------------------
class Dashboard {
    private JFrame frame;
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private JPanel homePanel;
    private JLabel lblTotalClients, lblActiveOrders, lblPendingOrders, lblCompletedOrders;

    public Dashboard() {
        frame = new JFrame("Advertising Board Management System");
        frame.setSize(1200, 700);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.setLocationRelativeTo(null);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(41, 128, 185));
        headerPanel.setPreferredSize(new Dimension(0, 60));
        JLabel titleLabel = new JLabel(" AD BOARD MANAGEMENT SYSTEM - MySQL Edition");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        frame.add(headerPanel, BorderLayout.NORTH);

        JPanel sidebar = createSidebar();
        frame.add(sidebar, BorderLayout.WEST);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        homePanel = createHomePanel();
        mainPanel.add(homePanel, "HOME");
        mainPanel.add(new ClientManagementPanel(this), "CLIENTS");
        mainPanel.add(new BoardOrderPanel(this), "BOARDS");
        mainPanel.add(new ReportsPanel(), "REPORTS");
        frame.add(mainPanel, BorderLayout.CENTER);

        frame.setVisible(true);

        if (DatabaseManager.getConnection() != null) {
            updateDashboard();
        }
    }

    public void updateDashboard() {
        lblTotalClients.setText(String.valueOf(DataManager.getAllClients().size()));
        int[] stats = DataManager.getOrderStatistics();
        lblPendingOrders.setText(String.valueOf(stats[0]));
        lblActiveOrders.setText(String.valueOf(stats[1]));
        lblCompletedOrders.setText(String.valueOf(stats[2]));
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(52, 73, 94));
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));

        sidebar.add(Box.createVerticalStrut(20));
        sidebar.add(createMenuButton("Home", "HOME"));
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(createMenuButton("Client Management", "CLIENTS"));
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(createMenuButton("Board Orders", "BOARDS"));
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(createMenuButton("Reports", "REPORTS"));

        return sidebar;
    }

    private JButton createMenuButton(String text, String panelName) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(200, 45));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setBackground(new Color(41, 128, 185));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> cardLayout.show(mainPanel, panelName));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(52, 152, 219));
            }

            public void mouseExited(MouseEvent e) {
                btn.setBackground(new Color(41, 128, 185));
            }
        });
        return btn;
    }

    private JPanel createHomePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        JPanel centerPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        JPanel clientCard = createDashboardCard("Total Clients", new Color(52, 152, 219));
        lblTotalClients = (JLabel) clientCard.getComponent(1);
        centerPanel.add(clientCard);

        JPanel activeCard = createDashboardCard("Active Orders", new Color(46, 204, 113));
        lblActiveOrders = (JLabel) activeCard.getComponent(1);
        centerPanel.add(activeCard);

        JPanel pendingCard = createDashboardCard("Pending Orders", new Color(241, 196, 15));
        lblPendingOrders = (JLabel) pendingCard.getComponent(1);
        centerPanel.add(pendingCard);

        JPanel completedCard = createDashboardCard("Completed Orders", new Color(155, 89, 182));
        lblCompletedOrders = (JLabel) completedCard.getComponent(1);
        centerPanel.add(completedCard);

        panel.add(centerPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createDashboardCard(String title, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(color);
        card.setBorder(BorderFactory.createLineBorder(color.darker(), 2));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));

        JLabel valueLabel = new JLabel("0", SwingConstants.CENTER);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 48));
        valueLabel.setForeground(Color.WHITE);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }
}

// ----------------------- Client Management Panel -----------------------
class ClientManagementPanel extends JPanel {
    private JTable clientTable;
    private DefaultTableModel tableModel;
    private Dashboard dashboard;
    private ArrayList<Client> clients;

    public ClientManagementPanel(Dashboard dashboard) {
        this.dashboard = dashboard;
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Client Management");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        add(title, BorderLayout.NORTH);

        String[] columns = { "ID", "Client Name", "Contact Person", "Phone", "Email", "City", "Company Type" };
        tableModel = new DefaultTableModel(columns, 0);
        clientTable = new JTable(tableModel);
        clientTable.setRowHeight(30);
        clientTable.setFont(new Font("Arial", Font.PLAIN, 12));
        clientTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        clientTable.getTableHeader().setBackground(new Color(41, 128, 185));
        clientTable.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(clientTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonPanel.setBackground(Color.WHITE);

        JButton btnAdd = createStyledButton("Add Client", new Color(46, 204, 113));
        btnAdd.addActionListener(e -> addClient());
        buttonPanel.add(btnAdd);

        JButton btnEdit = createStyledButton("Edit Client", new Color(52, 152, 219));
        btnEdit.addActionListener(e -> editClient());
        buttonPanel.add(btnEdit);

        JButton btnDelete = createStyledButton("Delete Client", new Color(231, 76, 60));
        btnDelete.addActionListener(e -> deleteClient());
        buttonPanel.add(btnDelete);

        JButton btnRefresh = createStyledButton("Refresh", new Color(149, 165, 166));
        btnRefresh.addActionListener(e -> refreshTable());
        buttonPanel.add(btnRefresh);

        add(buttonPanel, BorderLayout.SOUTH);
        refreshTable();
    }

    private JButton createStyledButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(140, 35));
        return btn;
    }

    private void addClient() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add New Client", true);
        dialog.setSize(500, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridLayout(7, 2, 10, 15));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField txtClientName = new JTextField();
        JTextField txtContactPerson = new JTextField();
        JTextField txtPhone = new JTextField();
        JTextField txtEmail = new JTextField();
        JTextField txtAddress = new JTextField();
        JTextField txtCity = new JTextField();
        String[] companyTypes = { "Retail", "Restaurant", "Real Estate", "Education", "Healthcare", "Technology",
                "Fashion", "Automobile", "Entertainment", "Other" };
        JComboBox<String> cmbCompanyType = new JComboBox<>(companyTypes);

        formPanel.add(new JLabel("Client Name:*"));
        formPanel.add(txtClientName);
        formPanel.add(new JLabel("Contact Person:*"));
        formPanel.add(txtContactPerson);
        formPanel.add(new JLabel("Phone Number:*"));
        formPanel.add(txtPhone);
        formPanel.add(new JLabel("Email:"));
        formPanel.add(txtEmail);
        formPanel.add(new JLabel("Address:"));
        formPanel.add(txtAddress);
        formPanel.add(new JLabel("City:"));
        formPanel.add(txtCity);
        formPanel.add(new JLabel("Company Type:"));
        formPanel.add(cmbCompanyType);

        dialog.add(formPanel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSave = new JButton("Save");
        btnSave.setBackground(new Color(46, 204, 113));
        btnSave.setForeground(Color.WHITE);
        btnSave.addActionListener(e -> {
            String clientName = txtClientName.getText().trim();
            String contactPerson = txtContactPerson.getText().trim();
            String phone = txtPhone.getText().trim();

            if (clientName.isEmpty() || contactPerson.isEmpty() || phone.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please fill all required fields (*)");
                return;
            }

            if (DataManager.addClient(clientName, contactPerson, phone,
                    txtEmail.getText().trim(), txtAddress.getText().trim(),
                    txtCity.getText().trim(), (String) cmbCompanyType.getSelectedItem())) {
                refreshTable();
                dashboard.updateDashboard();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Client added successfully!");
            }
        });

        JButton btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(e -> dialog.dispose());

        btnPanel.add(btnSave);
        btnPanel.add(btnCancel);
        dialog.add(btnPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void editClient() {
        int selectedRow = clientTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a client to edit!");
            return;
        }

        Client client = clients.get(selectedRow);

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Client", true);
        dialog.setSize(500, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridLayout(7, 2, 10, 15));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField txtClientName = new JTextField(client.getClientName());
        JTextField txtContactPerson = new JTextField(client.getContactPerson());
        JTextField txtPhone = new JTextField(client.getPhoneNumber());
        JTextField txtEmail = new JTextField(client.getEmail());
        JTextField txtAddress = new JTextField(client.getAddress());
        JTextField txtCity = new JTextField(client.getCity());
        String[] companyTypes = { "Retail", "Restaurant", "Real Estate", "Education", "Healthcare", "Technology",
                "Fashion", "Automobile", "Entertainment", "Other" };
        JComboBox<String> cmbCompanyType = new JComboBox<>(companyTypes);
        cmbCompanyType.setSelectedItem(client.getCompanyType());

        formPanel.add(new JLabel("Client Name:*"));
        formPanel.add(txtClientName);
        formPanel.add(new JLabel("Contact Person:*"));
        formPanel.add(txtContactPerson);
        formPanel.add(new JLabel("Phone Number:*"));
        formPanel.add(txtPhone);
        formPanel.add(new JLabel("Email:"));
        formPanel.add(txtEmail);
        formPanel.add(new JLabel("Address:"));
        formPanel.add(txtAddress);
        formPanel.add(new JLabel("City:"));
        formPanel.add(txtCity);
        formPanel.add(new JLabel("Company Type:"));
        formPanel.add(cmbCompanyType);

        dialog.add(formPanel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSave = new JButton("Update");
        btnSave.setBackground(new Color(52, 152, 219));
        btnSave.setForeground(Color.WHITE);
        btnSave.addActionListener(e -> {
            if (DataManager.updateClient(client.getId(), txtClientName.getText().trim(),
                    txtContactPerson.getText().trim(), txtPhone.getText().trim(),
                    txtEmail.getText().trim(), txtAddress.getText().trim(),
                    txtCity.getText().trim(), (String) cmbCompanyType.getSelectedItem())) {
                refreshTable();
                dashboard.updateDashboard();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Client updated successfully!");
            }
        });

        JButton btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(e -> dialog.dispose());

        btnPanel.add(btnSave);
        btnPanel.add(btnCancel);
        dialog.add(btnPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void deleteClient() {
        int selectedRow = clientTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a client to delete!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this client?\nAll associated orders will also be deleted.",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            Client client = clients.get(selectedRow);
            if (DataManager.deleteClient(client.getId())) {
                refreshTable();
                dashboard.updateDashboard();
                JOptionPane.showMessageDialog(this, "Client deleted successfully!");
            }
        }
    }

    private void refreshTable() {
        clients = DataManager.getAllClients();
        tableModel.setRowCount(0);
        for (Client client : clients) {
            tableModel.addRow(new Object[] {
                    client.getId(),
                    client.getClientName(),
                    client.getContactPerson(),
                    client.getPhoneNumber(),
                    client.getEmail(),
                    client.getCity(),
                    client.getCompanyType()
            });
        }
    }
}

// ----------------------- Board Order Panel -----------------------
class BoardOrderPanel extends JPanel {
    private JTable orderTable;
    private DefaultTableModel tableModel;
    private Dashboard dashboard;
    private ArrayList<BoardOrder> orders;

    public BoardOrderPanel(Dashboard dashboard) {
        this.dashboard = dashboard;
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Board Orders Management");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        add(title, BorderLayout.NORTH);

        String[] columns = { "ID", "Client", "Board Type", "Size", "Location", "Start Date", "End Date", "Qty", "Price",
                "Total", "Status" };
        tableModel = new DefaultTableModel(columns, 0);
        orderTable = new JTable(tableModel);
        orderTable.setRowHeight(30);
        orderTable.setFont(new Font("Arial", Font.PLAIN, 12));
        orderTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        orderTable.getTableHeader().setBackground(new Color(41, 128, 185));
        orderTable.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(orderTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonPanel.setBackground(Color.WHITE);

        JButton btnAdd = createStyledButton("Add Order", new Color(46, 204, 113));
        btnAdd.addActionListener(e -> addBoardOrder());
        buttonPanel.add(btnAdd);

        JButton btnEdit = createStyledButton("Edit Order", new Color(52, 152, 219));
        btnEdit.addActionListener(e -> editBoardOrder());
        buttonPanel.add(btnEdit);

        JButton btnDelete = createStyledButton("Delete Order", new Color(231, 76, 60));
        btnDelete.addActionListener(e -> deleteOrder());
        buttonPanel.add(btnDelete);

        JButton btnRefresh = createStyledButton("Refresh", new Color(149, 165, 166));
        btnRefresh.addActionListener(e -> refreshTable());
        buttonPanel.add(btnRefresh);

        add(buttonPanel, BorderLayout.SOUTH);
        refreshTable();
    }

    private JButton createStyledButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(140, 35));
        return btn;
    }

    private void addBoardOrder() {
        ArrayList<Client> clients = DataManager.getAllClients();
        if (clients.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please add clients first!");
            return;
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add Board Order", true);
        dialog.setSize(600, 650);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridLayout(10, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JComboBox<Client> cmbClient = new JComboBox<>(clients.toArray(new Client[0]));
        String[] boardTypes = { "LED Board", "Flex Board", "Vinyl Board", "Neon Board", "Digital Billboard",
                "Backlit Board", "3D Board", "Poster Board" };
        JComboBox<String> cmbBoardType = new JComboBox<>(boardTypes);
        String[] sizes = { "10x5 ft", "15x10 ft", "20x10 ft", "25x15 ft", "30x20 ft", "40x30 ft", "Custom Size" };
        JComboBox<String> cmbSize = new JComboBox<>(sizes);
        JTextField txtLocation = new JTextField();
        JTextField txtStartDate = new JTextField("DD/MM/YYYY");
        JTextField txtEndDate = new JTextField("DD/MM/YYYY");
        JSpinner spnQuantity = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        JTextField txtPrice = new JTextField();
        String[] statuses = { "Pending", "In Progress", "Installed", "Completed", "Cancelled" };
        JComboBox<String> cmbStatus = new JComboBox<>(statuses);
        JTextArea txtNotes = new JTextArea(3, 20);
        JScrollPane notesScroll = new JScrollPane(txtNotes);

        formPanel.add(new JLabel("Select Client:*"));
        formPanel.add(cmbClient);
        formPanel.add(new JLabel("Board Type:*"));
        formPanel.add(cmbBoardType);
        formPanel.add(new JLabel("Board Size:*"));
        formPanel.add(cmbSize);
        formPanel.add(new JLabel("Location:*"));
        formPanel.add(txtLocation);
        formPanel.add(new JLabel("Start Date:*"));
        formPanel.add(txtStartDate);
        formPanel.add(new JLabel("End Date:*"));
        formPanel.add(txtEndDate);
        formPanel.add(new JLabel("Quantity:*"));
        formPanel.add(spnQuantity);
        formPanel.add(new JLabel("Price per Board:*"));
        formPanel.add(txtPrice);
        formPanel.add(new JLabel("Status:"));
        formPanel.add(cmbStatus);
        formPanel.add(new JLabel("Notes:"));
        formPanel.add(notesScroll);

        dialog.add(formPanel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSave = new JButton("Save Order");
        btnSave.setBackground(new Color(46, 204, 113));
        btnSave.setForeground(Color.WHITE);
        btnSave.addActionListener(e -> {
            try {
                Client client = (Client) cmbClient.getSelectedItem();
                String location = txtLocation.getText().trim();
                String startDate = txtStartDate.getText().trim();
                String endDate = txtEndDate.getText().trim();
                double price = Double.parseDouble(txtPrice.getText().trim());

                if (location.isEmpty() || startDate.isEmpty() || endDate.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please fill all required fields!");
                    return;
                }

                if (DataManager.addBoardOrder(client.getId(),
                        (String) cmbBoardType.getSelectedItem(),
                        (String) cmbSize.getSelectedItem(), location, startDate, endDate,
                        (Integer) spnQuantity.getValue(), price,
                        (String) cmbStatus.getSelectedItem(), txtNotes.getText().trim())) {
                    refreshTable();
                    dashboard.updateDashboard();
                    dialog.dispose();
                    JOptionPane.showMessageDialog(this, "Board order added successfully!");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter valid price!");
            }
        });

        JButton btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(e -> dialog.dispose());

        btnPanel.add(btnSave);
        btnPanel.add(btnCancel);
        dialog.add(btnPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void editBoardOrder() {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select an order to edit!");
            return;
        }

        BoardOrder order = orders.get(selectedRow);
        ArrayList<Client> clients = DataManager.getAllClients();

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Board Order", true);
        dialog.setSize(600, 650);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridLayout(10, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JComboBox<Client> cmbClient = new JComboBox<>(clients.toArray(new Client[0]));
        for (int i = 0; i < cmbClient.getItemCount(); i++) {
            if (cmbClient.getItemAt(i).getId() == order.getClientId()) {
                cmbClient.setSelectedIndex(i);
                break;
            }
        }

        String[] boardTypes = { "LED Board", "Flex Board", "Vinyl Board", "Neon Board", "Digital Billboard",
                "Backlit Board", "3D Board", "Poster Board" };
        JComboBox<String> cmbBoardType = new JComboBox<>(boardTypes);
        cmbBoardType.setSelectedItem(order.getBoardType());

        String[] sizes = { "10x5 ft", "15x10 ft", "20x10 ft", "25x15 ft", "30x20 ft", "40x30 ft", "Custom Size" };
        JComboBox<String> cmbSize = new JComboBox<>(sizes);
        cmbSize.setSelectedItem(order.getBoardSize());

        JTextField txtLocation = new JTextField(order.getLocation());
        JTextField txtStartDate = new JTextField(order.getStartDate());
        JTextField txtEndDate = new JTextField(order.getEndDate());
        JSpinner spnQuantity = new JSpinner(new SpinnerNumberModel(order.getQuantity(), 1, 100, 1));
        JTextField txtPrice = new JTextField(String.valueOf(order.getPricePerBoard()));

        String[] statuses = { "Pending", "In Progress", "Installed", "Completed", "Cancelled" };
        JComboBox<String> cmbStatus = new JComboBox<>(statuses);
        cmbStatus.setSelectedItem(order.getStatus());

        JTextArea txtNotes = new JTextArea(order.getNotes(), 3, 20);
        JScrollPane notesScroll = new JScrollPane(txtNotes);

        formPanel.add(new JLabel("Select Client:*"));
        formPanel.add(cmbClient);
        formPanel.add(new JLabel("Board Type:*"));
        formPanel.add(cmbBoardType);
        formPanel.add(new JLabel("Board Size:*"));
        formPanel.add(cmbSize);
        formPanel.add(new JLabel("Location:*"));
        formPanel.add(txtLocation);
        formPanel.add(new JLabel("Start Date:*"));
        formPanel.add(txtStartDate);
        formPanel.add(new JLabel("End Date:*"));
        formPanel.add(txtEndDate);
        formPanel.add(new JLabel("Quantity:*"));
        formPanel.add(spnQuantity);
        formPanel.add(new JLabel("Price per Board:*"));
        formPanel.add(txtPrice);
        formPanel.add(new JLabel("Status:"));
        formPanel.add(cmbStatus);
        formPanel.add(new JLabel("Notes:"));
        formPanel.add(notesScroll);

        dialog.add(formPanel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSave = new JButton("Update Order");
        btnSave.setBackground(new Color(52, 152, 219));
        btnSave.setForeground(Color.WHITE);
        btnSave.addActionListener(e -> {
            try {
                Client client = (Client) cmbClient.getSelectedItem();
                double price = Double.parseDouble(txtPrice.getText().trim());

                if (DataManager.updateBoardOrder(order.getId(), client.getId(),
                        (String) cmbBoardType.getSelectedItem(),
                        (String) cmbSize.getSelectedItem(), txtLocation.getText().trim(),
                        txtStartDate.getText().trim(), txtEndDate.getText().trim(),
                        (Integer) spnQuantity.getValue(), price,
                        (String) cmbStatus.getSelectedItem(), txtNotes.getText().trim())) {
                    refreshTable();
                    dashboard.updateDashboard();
                    dialog.dispose();
                    JOptionPane.showMessageDialog(this, "Order updated successfully!");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter valid price!");
            }
        });

        JButton btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(e -> dialog.dispose());

        btnPanel.add(btnSave);
        btnPanel.add(btnCancel);
        dialog.add(btnPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void deleteOrder() {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select an order to delete!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this order?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            BoardOrder order = orders.get(selectedRow);
            if (DataManager.deleteBoardOrder(order.getId())) {
                refreshTable();
                dashboard.updateDashboard();
                JOptionPane.showMessageDialog(this, "Order deleted successfully!");
            }
        }
    }

    private void refreshTable() {
        orders = DataManager.getAllBoardOrders();
        tableModel.setRowCount(0);
        for (BoardOrder order : orders) {
            tableModel.addRow(new Object[] {
                    order.getId(),
                    order.getClientName(),
                    order.getBoardType(),
                    order.getBoardSize(),
                    order.getLocation(),
                    order.getStartDate(),
                    order.getEndDate(),
                    order.getQuantity(),
                    String.format("$%.2f", order.getPricePerBoard()),
                    String.format("$%.2f", order.getTotalPrice()),
                    order.getStatus()
            });
        }
    }
}

// ----------------------- Reports Panel -----------------------
class ReportsPanel extends JPanel {
    public ReportsPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Reports & Statistics");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        add(title, BorderLayout.NORTH);

        JTextArea reportArea = new JTextArea();
        reportArea.setEditable(false);
        reportArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(reportArea);
        add(scrollPane, BorderLayout.CENTER);

        JButton btnGenerate = new JButton("Generate Report");
        btnGenerate.setBackground(new Color(52, 152, 219));
        btnGenerate.setForeground(Color.WHITE);
        btnGenerate.setFont(new Font("Arial", Font.BOLD, 14));
        btnGenerate.addActionListener(e -> {
            StringBuilder report = new StringBuilder();
            report.append("=== ADVERTISING BOARD MANAGEMENT REPORT ===\n\n");

            int totalClients = DataManager.getAllClients().size();
            ArrayList<BoardOrder> orders = DataManager.getAllBoardOrders();
            int[] stats = DataManager.getOrderStatistics();
            double totalRevenue = DataManager.getTotalRevenue();

            report.append("Total Clients: ").append(totalClients).append("\n");
            report.append("Total Orders: ").append(orders.size()).append("\n\n");

            report.append("--- ORDER BREAKDOWN BY STATUS ---\n");
            report.append("Pending: ").append(stats[0]).append("\n");
            report.append("In Progress/Installed: ").append(stats[1]).append("\n");
            report.append("Completed: ").append(stats[2]).append("\n\n");

            report.append("--- RECENT ORDERS ---\n");
            int count = 0;
            for (BoardOrder order : orders) {
                if (count++ >= 10)
                    break;
                report.append(order.getClientName()).append(" - ")
                        .append(order.getBoardType()).append(" - ")
                        .append(order.getStatus()).append("\n");
            }

            report.append("\n--- TOTAL REVENUE ---\n");
            report.append(String.format("$%.2f", totalRevenue)).append("\n");

            reportArea.setText(report.toString());
        });

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.setBackground(Color.WHITE);
        btnPanel.add(btnGenerate);
        add(btnPanel, BorderLayout.SOUTH);
    }
}

// ----------------------- Main -----------------------
public class AdFirmManager {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Dashboard();
        });
    }
}