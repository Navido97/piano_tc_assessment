import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PianoApiClient {
    
    public static void main(String[] args) {
        try {
            // Build URL with parameters
            String urlWithParams = Config.REQUEST_URL + "?api_token=" + Config.API_TOKEN + "&aid=" + Config.AID;
            
            // Request body
            String requestBody = "{\"aid\":\"" + Config.AID + "\"}";
            
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlWithParams))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + Config.API_TOKEN)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
            
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            
            // Parse JSON response and convert to CSV instead of saving raw text
            convertJsonToCsv(response.body());
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void convertJsonToCsv(String jsonResponse) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(jsonResponse);
            
            // Create files directory if it doesn't exist
            new File("files").mkdir();
            
            // Create CSV writer
            PrintWriter writer = new PrintWriter(new FileWriter("files/piano_api_response.csv"));
            
            // Write CSV header with the columns you specified
            writer.println("first_name,last_name,personal_name,email,uid,image1,create_date,display_name,reset_password_email_sent,custom_fields");
            
            // Extract users array from JSON response
            JsonNode usersNode = rootNode.get("users");
            
            if (usersNode != null && usersNode.isArray()) {
                for (JsonNode userNode : usersNode) {
                    // Extract each field, handling null values
                    String firstName = getStringValue(userNode, "first_name");
                    String lastName = getStringValue(userNode, "last_name");
                    String personalName = getStringValue(userNode, "personal_name");
                    String email = getStringValue(userNode, "email");
                    String uid = getStringValue(userNode, "uid");
                    String image1 = getStringValue(userNode, "image1");
                    String createDate = getStringValue(userNode, "create_date");
                    String displayName = getStringValue(userNode, "display_name");
                    String resetPasswordEmailSent = getBooleanValue(userNode, "reset_password_email_sent");
                    String customFields = getArrayValue(userNode, "custom_fields");
                    
                    // Write CSV row (escaping commas and quotes)
                    writer.printf("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s%n",
                        escapeForCsv(firstName),
                        escapeForCsv(lastName),
                        escapeForCsv(personalName),
                        escapeForCsv(email),
                        escapeForCsv(uid),
                        escapeForCsv(image1),
                        escapeForCsv(createDate),
                        escapeForCsv(displayName),
                        escapeForCsv(resetPasswordEmailSent),
                        escapeForCsv(customFields)
                    );
                }
            }
            
            writer.close();
            System.out.println("Data successfully saved to files/piano_api_response.csv");
            
        } catch (Exception e) {
            System.err.println("Error converting JSON to CSV: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static String getStringValue(JsonNode node, String fieldName) {
        JsonNode fieldNode = node.get(fieldName);
        if (fieldNode == null || fieldNode.isNull()) {
            return "";
        }
        return fieldNode.asText();
    }
    
    private static String getBooleanValue(JsonNode node, String fieldName) {
        JsonNode fieldNode = node.get(fieldName);
        if (fieldNode == null || fieldNode.isNull()) {
            return "";
        }
        return String.valueOf(fieldNode.asBoolean());
    }
    
    private static String getArrayValue(JsonNode node, String fieldName) {
        JsonNode fieldNode = node.get(fieldName);
        if (fieldNode == null || fieldNode.isNull() || !fieldNode.isArray()) {
            return "";
        }
        // Convert array to string representation
        return fieldNode.toString();
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
}