import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BusStationGUI extends JFrame {
    private DatabaseManager dbManager;

    public BusStationGUI() {
        dbManager = new DatabaseManager();
        initUI();
    }

    private void initUI() {
        setTitle("Управление автовокзалом");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 500);

        Container pane = getContentPane();
        pane.setLayout(new BorderLayout(5, 5));

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Автобусы", createBusesPanel());
        tabbedPane.addTab("Водители", createDriversPanel());
        tabbedPane.addTab("Рейсы", createTripsPanel());

        pane.add(tabbedPane, BorderLayout.CENTER);
        setVisible(true);
    }

    private JPanel createBusesPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        DefaultTableModel tableModel = new DefaultTableModel(new String[]{"ID", "Номер", "Модель"}, 0);
        JTable table = new JTable(tableModel);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel controlPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JTextField numberField = new JTextField(10);
        JTextField modelField = new JTextField(10);
        JButton addButton = new JButton("Добавить");
        JButton updateButton = new JButton("Обновить");
        JButton deleteButton = new JButton("Удалить");
        JButton refreshButton = new JButton("Обновить таблицу");

        gbc.gridx = 0; gbc.gridy = 0; controlPanel.add(new JLabel("Номер:"), gbc);
        gbc.gridx = 1; controlPanel.add(numberField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; controlPanel.add(new JLabel("Модель:"), gbc);
        gbc.gridx = 1; controlPanel.add(modelField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; controlPanel.add(addButton, gbc);
        gbc.gridx = 1; controlPanel.add(updateButton, gbc);
        gbc.gridx = 2; controlPanel.add(deleteButton, gbc);
        gbc.gridx = 3; controlPanel.add(refreshButton, gbc);

        panel.add(controlPanel, BorderLayout.SOUTH);

        Runnable refresh = () -> {
            try {
                tableModel.setRowCount(0);
                ResultSet rs = dbManager.getBuses();
                while (rs.next()) {
                    tableModel.addRow(new Object[]{rs.getInt("id"), rs.getString("number_plate"), rs.getString("model")});
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        };

        addButton.addActionListener(e -> {
            try {
                dbManager.addBus(numberField.getText(), modelField.getText());
                refresh.run();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Ошибка при добавлении автобуса");
            }
        });

        updateButton.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                try {
                    int id = (int) tableModel.getValueAt(row, 0);
                    dbManager.updateBus(id, numberField.getText(), modelField.getText());
                    refresh.run();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Ошибка при обновлении автобуса");
                }
            }
        });

        deleteButton.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                try {
                    int id = (int) tableModel.getValueAt(row, 0);
                    dbManager.deleteBus(id);
                    refresh.run();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Ошибка при удалении автобуса");
                }
            }
        });

        refreshButton.addActionListener(e -> refresh.run());
        refresh.run();
        return panel;
    }

    private JPanel createDriversPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        DefaultTableModel tableModel = new DefaultTableModel(new String[]{"ID", "Имя", "Фамилия"}, 0);
        JTable table = new JTable(tableModel);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel controlPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JTextField firstNameField = new JTextField(10);
        JTextField lastNameField = new JTextField(10);
        JButton addButton = new JButton("Добавить");
        JButton updateButton = new JButton("Обновить");
        JButton deleteButton = new JButton("Удалить");
        JButton refreshButton = new JButton("Обновить таблицу");

        gbc.gridx = 0; gbc.gridy = 0; controlPanel.add(new JLabel("Имя:"), gbc);
        gbc.gridx = 1; controlPanel.add(firstNameField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; controlPanel.add(new JLabel("Фамилия:"), gbc);
        gbc.gridx = 1; controlPanel.add(lastNameField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; controlPanel.add(addButton, gbc);
        gbc.gridx = 1; controlPanel.add(updateButton, gbc);
        gbc.gridx = 2; controlPanel.add(deleteButton, gbc);
        gbc.gridx = 3; controlPanel.add(refreshButton, gbc);

        panel.add(controlPanel, BorderLayout.SOUTH);

        Runnable refresh = () -> {
            try {
                tableModel.setRowCount(0);
                ResultSet rs = dbManager.getDrivers();
                while (rs.next()) {
                    tableModel.addRow(new Object[]{rs.getInt("id"), rs.getString("first_name"), rs.getString("last_name")});
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        };

        addButton.addActionListener(e -> {
            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();

            if (!firstName.matches("^[\\p{L}]+$")) {
                JOptionPane.showMessageDialog(this, "Имя должно содержать только буквы!");
                return;
            }
            if (!lastName.matches("^[\\p{L}]+$")) {
                JOptionPane.showMessageDialog(this, "Фамилия должна содержать только буквы!");
                return;
            }

            try {
                dbManager.addDriver(firstName, lastName);
                refresh.run();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Ошибка при добавлении водителя");
            }
        });

        updateButton.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                String firstName = firstNameField.getText();
                String lastName = lastNameField.getText();

                if (!firstName.matches("^[\\p{L}]+$")) {
                    JOptionPane.showMessageDialog(this, "Имя должно содержать только буквы!");
                    return;
                }
                if (!lastName.matches("^[\\p{L}]+$")) {
                    JOptionPane.showMessageDialog(this, "Фамилия должна содержать только буквы!");
                    return;
                }

                try {
                    int id = (int) tableModel.getValueAt(row, 0);
                    dbManager.updateDriver(id, firstName, lastName);
                    refresh.run();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Ошибка при обновлении водителя");
                }
            }
        });

        deleteButton.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                try {
                    int id = (int) tableModel.getValueAt(row, 0);
                    dbManager.deleteDriver(id);
                    refresh.run();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Ошибка при удалении водителя");
                }
            }
        });

        refreshButton.addActionListener(e -> refresh.run());
        refresh.run();
        return panel;
    }

    private JPanel createTripsPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        DefaultTableModel tableModel = new DefaultTableModel(
                new String[]{"ID", "Номер автобуса", "Модель", "Водитель", "Пункт назначения", "Время отправления"}, 0);
        JTable table = new JTable(tableModel);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel controlPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JTextField busIdField = new JTextField(5);
        JTextField driverIdField = new JTextField(5);
        JTextField destinationField = new JTextField(10);
        JTextField departureTimeField = new JTextField(10);
        JButton addButton = new JButton("Добавить");
        JButton updateButton = new JButton("Обновить");
        JButton deleteButton = new JButton("Удалить");
        JButton refreshButton = new JButton("Обновить таблицу");

        gbc.gridx = 0; gbc.gridy = 0; controlPanel.add(new JLabel("ID автобуса:"), gbc);
        gbc.gridx = 1; controlPanel.add(busIdField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; controlPanel.add(new JLabel("ID водителя:"), gbc);
        gbc.gridx = 1; controlPanel.add(driverIdField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; controlPanel.add(new JLabel("Пункт назначения:"), gbc);
        gbc.gridx = 1; controlPanel.add(destinationField, gbc);
        gbc.gridx = 0; gbc.gridy = 3; controlPanel.add(new JLabel("Время отправления:"), gbc);
        gbc.gridx = 1; controlPanel.add(departureTimeField, gbc);
        gbc.gridx = 0; gbc.gridy = 4; controlPanel.add(addButton, gbc);
        gbc.gridx = 1; controlPanel.add(updateButton, gbc);
        gbc.gridx = 2; controlPanel.add(deleteButton, gbc);
        gbc.gridx = 3; controlPanel.add(refreshButton, gbc);

        panel.add(controlPanel, BorderLayout.SOUTH);

        Runnable refresh = () -> {
            try {
                tableModel.setRowCount(0);
                ResultSet rs = dbManager.getTrips();
                while (rs.next()) {
                    tableModel.addRow(new Object[]{
                            rs.getInt("id"),
                            rs.getString("number_plate"),
                            rs.getString("model"),
                            rs.getString("driver"),
                            rs.getString("destination"),
                            rs.getString("departure_time")
                    });
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        };

        addButton.addActionListener(e -> {
            String destination = destinationField.getText();
            String departureTime = departureTimeField.getText();

            if (!destination.matches("^[\\p{L}\\s\\p{P}]+$") || destination.matches(".*\\d.*")) {
                JOptionPane.showMessageDialog(this, "Пункт назначения должен содержать только буквы и символы, без цифр!");
                return;
            }
            if (!departureTime.matches("^([01]?[0-9]|2[0-3]):[0-5][0-9]$")) {
                JOptionPane.showMessageDialog(this, "Время отправления должно быть в формате ЧЧ:ММ (например, 12:30)!");
                return;
            }

            try {
                dbManager.addTrip(Integer.parseInt(busIdField.getText()), Integer.parseInt(driverIdField.getText()),
                        destination, departureTime);
                refresh.run();
            } catch (SQLException | NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Ошибка при добавлении рейса");
            }
        });

        updateButton.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                String destination = destinationField.getText();
                String departureTime = departureTimeField.getText();

                if (!destination.matches("^[\\p{L}\\s\\p{P}]+$") || destination.matches(".*\\d.*")) {
                    JOptionPane.showMessageDialog(this, "Пункт назначения должен содержать только буквы и символы, без цифр!");
                    return;
                }
                if (!departureTime.matches("^([01]?[0-9]|2[0-3]):[0-5][0-9]$")) {
                    JOptionPane.showMessageDialog(this, "Время отправления должно быть в формате ЧЧ:ММ (например, 12:30)!");
                    return;
                }

                try {
                    int id = (int) tableModel.getValueAt(row, 0);
                    dbManager.updateTrip(id, Integer.parseInt(busIdField.getText()), Integer.parseInt(driverIdField.getText()),
                            destination, departureTime);
                    refresh.run();
                } catch (SQLException | NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Ошибка при обновлении рейса");
                }
            }
        });

        deleteButton.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                try {
                    int id = (int) tableModel.getValueAt(row, 0);
                    dbManager.deleteTrip(id);
                    refresh.run();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Ошибка при удалении рейса");
                }
            }
        });

        refreshButton.addActionListener(e -> refresh.run());
        refresh.run();
        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(BusStationGUI::new);
    }
}
