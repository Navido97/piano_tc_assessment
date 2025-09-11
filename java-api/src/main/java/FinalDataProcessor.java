import java.io.*;
import java.util.*;

public class FinalDataProcessor {
    
    public static void main(String[] args) {
        try {
            createFinalUserData();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    private static void createFinalUserData() throws IOException {
        // Read client_data_merged.csv
        Map<String, UserRecord> clientData = readClientData("files/client_data_merged.csv");
        
        // Read piano_api_response.csv to get email->uid mappings
        Map<String, String> emailToUidMap = readPianoApiData("files/piano_api_response.csv");
        
        // Update user_ids where emails match
        updateUserIds(clientData, emailToUidMap);
        
        // Write final_user_data.csv
        writeFinalData(clientData, "files/final_user_data.csv");
    }
    
    private static Map<String, UserRecord> readClientData(String filename) throws IOException {
        Map<String, UserRecord> clientData = new LinkedHashMap<>(); // Preserve order
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line = reader.readLine(); // Skip header
            
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    String userId = parts[0].trim();
                    String email = parts[1].trim();
                    String firstName = parts[2].trim();
                    String lastName = parts[3].trim();
                    
                    UserRecord record = new UserRecord(userId, email, firstName, lastName);
                    clientData.put(email, record); // Use email as key for easy lookup
                }
            }
        }
        
        return clientData;
    }
    
    private static Map<String, String> readPianoApiData(String filename) throws IOException {
        Map<String, String> emailToUidMap = new HashMap<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String headerLine = reader.readLine(); // Read header to find column indices
            if (headerLine == null) return emailToUidMap;
            
            String[] headers = headerLine.split(",");
            int emailIndex = -1;
            int uidIndex = -1;
            
            // Find the indices of email and uid columns
            for (int i = 0; i < headers.length; i++) {
                String header = headers[i].trim();
                if ("email".equals(header)) {
                    emailIndex = i;
                } else if ("uid".equals(header)) {
                    uidIndex = i;
                }
            }
            
            if (emailIndex == -1 || uidIndex == -1) {
                return emailToUidMap;
            }
            
            // Read data rows
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = parseCsvLine(line);
                if (parts.length > Math.max(emailIndex, uidIndex)) {
                    String email = parts[emailIndex].trim();
                    String uid = parts[uidIndex].trim();
                    
                    if (!email.isEmpty() && !uid.isEmpty()) {
                        emailToUidMap.put(email, uid);
                    }
                }
            }
        }
        
        return emailToUidMap;
    }
    
    private static void updateUserIds(Map<String, UserRecord> clientData, Map<String, String> emailToUidMap) {
        for (UserRecord record : clientData.values()) {
            String email = record.email;
            if (emailToUidMap.containsKey(email)) {
                String newUid = emailToUidMap.get(email);
                record.userId = newUid; // Override the user_id with uid from Piano API
            }
        }
    }
    
    private static void writeFinalData(Map<String, UserRecord> clientData, String filename) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            // Write header
            writer.println("user_id,email,first_name,last_name");
            
            // Sort records by email before writing
            List<UserRecord> sortedRecords = new ArrayList<>(clientData.values());
            sortedRecords.sort(Comparator.comparing(record -> record.email));
            
            // Write data rows in sorted order
            for (UserRecord record : sortedRecords) {
                writer.printf("%s,%s,%s,%s%n", 
                    escapeForCsv(record.userId),
                    escapeForCsv(record.email),
                    escapeForCsv(record.firstName),
                    escapeForCsv(record.lastName)
                );
            }
        }
    }
    
    // Helper method to parse CSV lines that might contain quoted fields
    private static String[] parseCsvLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder currentField = new StringBuilder();
        boolean inQuotes = false;
        
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                fields.add(currentField.toString());
                currentField.setLength(0);
            } else {
                currentField.append(c);
            }
        }
        
        fields.add(currentField.toString()); // Add the last field
        return fields.toArray(new String[0]);
    }
    
    private static String escapeForCsv(String value) {
        if (value == null || value.isEmpty()) {
            return "";
        }
        
        // If the value contains comma, newline, or quote, wrap it in quotes
        if (value.contains(",") || value.contains("\n") || value.contains("\"")) {
            // Escape existing quotes by doubling them
            value = value.replace("\"", "\"\"");
            return "\"" + value + "\"";
        }
        
        return value;
    }
    
    // Helper class to represent a user record
    private static class UserRecord {
        String userId;
        String email;
        String firstName;
        String lastName;
        
        UserRecord(String userId, String email, String firstName, String lastName) {
            this.userId = userId;
            this.email = email;
            this.firstName = firstName;
            this.lastName = lastName;
        }
    }
}
