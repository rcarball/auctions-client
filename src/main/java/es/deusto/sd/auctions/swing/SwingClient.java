package es.deusto.sd.auctions.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import es.deusto.sd.auctions.ApiClient;
import es.deusto.sd.auctions.dto.ArticleDTO;
import es.deusto.sd.auctions.dto.CategoryDTO;
import es.deusto.sd.auctions.dto.CredentialsDTO;

public class SwingClient extends JFrame {
    private static final long serialVersionUID = 1L;

    private final ApiClient apiClient;
    private String token;
    private String defaultEmail = "blackwidow@marvel.com";
    private String defaultPassword = "Bl@ckWid0w2023";

    private JLabel logoutLabel;
    private JComboBox<String> currencyComboBox;
    private JList<CategoryDTO> categoryList;
    private JTable jtbleArticles;
    private JLabel lblArticleTitle;
    private JLabel lblArticlePrice;
    private JLabel lblArticleBids;
    private JSpinner spinBidAmount;
    private JButton btnBid;

    private static final String[] CURRENCIES = { "EUR", "USD", "GBP", "JPY" };

    public SwingClient() {
        apiClient = new ApiClient();

        // 1. Login dialog
        if (!performLogin()) {
            System.exit(0);
        }

        // 2. Set up the main frame
        setTitle("Auctions Client");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1024, 400);
        setResizable(false);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 3. Top Panel with Currency Selector and Logout Label
        JPanel topPanel = new JPanel(new BorderLayout());

        // Currency ComboBox
        currencyComboBox = new JComboBox<>(CURRENCIES);
        currencyComboBox.setSelectedItem("EUR"); // Default currency
        currencyComboBox.addActionListener(e -> {
            loadArticleDetails();
            loadArticlesForCategory();
        });
        topPanel.add(currencyComboBox, BorderLayout.WEST);

        // Logout Label
        logoutLabel = new JLabel("Logout", SwingConstants.RIGHT);
        logoutLabel.setForeground(Color.BLUE);
        logoutLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logoutLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                performLogout();
            }
        });
        topPanel.add(logoutLabel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // 4. Categories List on the left
        categoryList = new JList<>();
        categoryList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    loadArticlesForCategory();
                }
            }
        });
        JScrollPane categoryScrollPane = new JScrollPane(categoryList);
        categoryScrollPane.setPreferredSize(new Dimension(200, getHeight()));
        categoryScrollPane.setBorder(new TitledBorder("Categories"));
        add(categoryScrollPane, BorderLayout.WEST);

        // 5. Articles Table at the top center
        jtbleArticles = new JTable(new DefaultTableModel(new Object[]{"ID", "Title", "Current Price", "Bids"}, 0));
        jtbleArticles.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jtbleArticles.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadArticleDetails();
            }
        });

        JScrollPane articleScrollPane = new JScrollPane(jtbleArticles);
        articleScrollPane.setPreferredSize(new Dimension(600, getHeight()));
        articleScrollPane.setBorder(new TitledBorder("Articles of the selected Category"));
        add(articleScrollPane, BorderLayout.CENTER);

        // 6. Article Detail Panel on the right
        JPanel jPanelArticleDetails = new JPanel(new GridLayout(5, 2, 10, 10));
        jPanelArticleDetails.setBorder(new TitledBorder("Article Details"));
        jPanelArticleDetails.setPreferredSize(new Dimension(224, getHeight())); // Remaining width

        jPanelArticleDetails.add(new JLabel("Title:"));
        lblArticleTitle = new JLabel();
        jPanelArticleDetails.add(lblArticleTitle);

        jPanelArticleDetails.add(new JLabel("Current Price:"));
        lblArticlePrice = new JLabel();
        jPanelArticleDetails.add(lblArticlePrice);

        jPanelArticleDetails.add(new JLabel("Bids:"));
        lblArticleBids = new JLabel();
        jPanelArticleDetails.add(lblArticleBids);

        jPanelArticleDetails.add(new JLabel("Bid Amount:"));
        spinBidAmount = new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));
        jPanelArticleDetails.add(spinBidAmount);

        btnBid = new JButton("Place Bid");
        btnBid.setEnabled(false);
        btnBid.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placeBid();
            }
        });

        JPanel jPanelBidButton = new JPanel();
        jPanelBidButton.add(btnBid);
        jPanelArticleDetails.add(jPanelBidButton);

        add(jPanelArticleDetails, BorderLayout.EAST);

        // 7. Load categories
        loadCategories();
        setVisible(true);
    }

    private boolean performLogin() {
        JTextField emailField = new JTextField(20);
        emailField.setText(defaultEmail);
        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setText(defaultPassword);

        Object[] message = {new JLabel("Enter Email:"), emailField, new JLabel("Enter Password:"), passwordField};

        int option = JOptionPane.showConfirmDialog(this, message, "Login", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String email = emailField.getText();
            defaultEmail = email;
            String password = new String(passwordField.getPassword());
            defaultPassword = password;
            CredentialsDTO credentials = new CredentialsDTO(email, password);

            try {
                token = apiClient.login(credentials);
                return true;
            } catch (RuntimeException e) {
                JOptionPane.showMessageDialog(this, "Login failed: " + e.getMessage());
                return false;
            }
        } else {
            return false;
        }
    }

    private void performLogout() {
        try {
            apiClient.logout(token);
            JOptionPane.showMessageDialog(this, "Logged out successfully.");
            System.exit(0);
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, "Logout failed: " + e.getMessage());
        }
    }

    private void loadCategories() {
        try {
            List<CategoryDTO> categories = apiClient.getAllCategories();
            categoryList.setListData(categories.toArray(new CategoryDTO[0]));
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, "Failed to load categories: " + e.getMessage());
        }
    }

    private void loadArticlesForCategory() {
        CategoryDTO selectedCategory = categoryList.getSelectedValue();
        String currency = (String) currencyComboBox.getSelectedItem();

        if (selectedCategory != null) {
            try {
                List<ArticleDTO> articles = apiClient.getArticlesByCategory(selectedCategory.name(), currency);
                DefaultTableModel model = (DefaultTableModel) jtbleArticles.getModel();
                model.setRowCount(0);

                for (ArticleDTO article : articles) {
                    model.addRow(new Object[]{article.id(), article.title(), formatPrice(article.currentPrice(), currency), article.bids()});
                }

            } catch (RuntimeException e) {
                JOptionPane.showMessageDialog(this, "Failed to load articles: " + e.getMessage());
            }
        }
    }

    private void loadArticleDetails() {
        int selectedRow = jtbleArticles.getSelectedRow();
        String currency = (String) currencyComboBox.getSelectedItem();

        if (selectedRow != -1) {
            Long articleId = (Long) jtbleArticles.getValueAt(selectedRow, 0);

            try {
                ArticleDTO article = apiClient.getArticleDetails(articleId, currency);
                lblArticleTitle.setText(article.title());
                lblArticlePrice.setText(formatPrice(article.currentPrice(), currency));
                lblArticleBids.setText(String.valueOf(article.bids()));
                spinBidAmount.setValue((int) Math.ceil(article.currentPrice()) + 1);
                btnBid.setEnabled(true);
            } catch (RuntimeException e) {
                JOptionPane.showMessageDialog(this, "Failed to load article details: " + e.getMessage());
            }
        }
    }

    private void placeBid() {
        int selectedRow = jtbleArticles.getSelectedRow();
        String currency = (String) currencyComboBox.getSelectedItem();

        if (selectedRow != -1) {
            Long articleId = (Long) jtbleArticles.getValueAt(selectedRow, 0);
            Float bidAmount = ((Integer) spinBidAmount.getValue()).floatValue();

            try {
                apiClient.makeBid(articleId, bidAmount, currency, token);
                JOptionPane.showMessageDialog(this, "Bid placed successfully!");

                loadArticleDetails();
                loadArticlesForCategory();
            } catch (RuntimeException e) {
                JOptionPane.showMessageDialog(this, "Failed to place bid: " + e.getMessage());
            }
        }
    }

    private String formatPrice(float price, String currency) {
        return switch (currency) {
            case "USD" -> String.format("$ %.2f", price);
            case "GBP" -> String.format("%.2f £", price);
            case "JPY" -> String.format("¥ %.2f", price);
            default -> String.format("%.2f €", price);
        };
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SwingClient());
    }
}