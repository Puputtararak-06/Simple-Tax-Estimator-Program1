import java.io.*;
import java.util.*;

/**
 * FileManager handles saving and loading tax data to/from files
 */
public class FileManager {
    private static final String DATA_DIRECTORY = "tax_data";
    private static final String USERS_FILE = DATA_DIRECTORY + "/users.txt";
    private static final String CALCULATIONS_FILE = DATA_DIRECTORY + "/calculations.txt";
    
    /**
     * Initialize the file system by creating necessary directories
     */
    public static void initialize() {
        File directory = new File(DATA_DIRECTORY);
        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            if (created) {
                System.out.println("Created data directory: " + DATA_DIRECTORY);
            } else {
                System.err.println("Failed to create data directory: " + DATA_DIRECTORY);
            }
        }
    }
    
    /**
     * Save user data to file
     * @param user The user to save
     * @throws IOException If an I/O error occurs
     */
    public static void saveUser(User user) throws IOException {
        initialize();
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(USERS_FILE, true))) {
            writer.println(user.getId() + "," + 
                          user.getName() + "," + 
                          user.getStatus() + "," + 
                          user.getOccupation() + "," + 
                          user.getIncome());
        }
    }
    
    /**
     * Save tax calculation result to file
     * @param calculation The calculation to save
     * @param user The user associated with the calculation
     * @throws IOException If an I/O error occurs
     */
    public static void saveCalculation(TaxCalculation calculation, User user) throws IOException {
        initialize();
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(CALCULATIONS_FILE, true))) {
            writer.println(user.getId() + "," + 
                          user.getName() + "," + 
                          calculation.getNetIncome() + "," + 
                          calculation.getTaxDue() + "," + 
                          calculation.getTaxPaid() + "," + 
                          calculation.getTaxBalance() + "," + 
                          calculation.getStatus());
        }
    }
    
    /**
     * Load all saved tax calculations
     * @return List of calculation records
     * @throws IOException If an I/O error occurs
     */
    public static List<String[]> loadCalculations() throws IOException {
        initialize();
        
        List<String[]> calculations = new ArrayList<>();
        File file = new File(CALCULATIONS_FILE);
        
        if (!file.exists()) {
            return calculations;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                calculations.add(parts);
            }
        }
        
        return calculations;
    }
    
    /**
     * Load all saved users
     * @return List of user records
     * @throws IOException If an I/O error occurs
     */
    public static List<User> loadUsers() throws IOException {
        initialize();
        
        List<User> users = new ArrayList<>();
        File file = new File(USERS_FILE);
        
        if (!file.exists()) {
            return users;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 5) {
                    int id = Integer.parseInt(parts[0]);
                    String name = parts[1];
                    String status = parts[2];
                    String occupation = parts[3];
                    double income = Double.parseDouble(parts[4]);
                    
                    users.add(new User(id, name, status, occupation, income));
                }
            }
        }
        
        return users;
    }
}
