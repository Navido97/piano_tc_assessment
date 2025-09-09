import java.io.*;
import java.util.*;

public class UserDataProcessor {
    
    public static void main(String[] args) {
        try {
            // file a data (emails)
            String[] emails = {
                "oi6IhEzu9R,user_2@example.com",
                "fQFLNRDae8,user_5@example.com", 
                "fBYRtPtAlC,user_7@example.com",
                "fOSjdLnNP3,user_9@example.com",
                "uxz2jFwr5I,user_1@example.com",
                "zSbmdNiSHH,user_4@example.com",
                "fjM66woroy,user_0@example.com",
                "oh4mHXh8zN,user_3@example.com",
                "gXWj37JC5d,user_8@example.com",
                "4dBdXURAz3,user_6@example.com"
            };
            
            // file b data (names)
            String[] names = {
                "oh4mHXh8zN,Julie,Mosser",
                "zSbmdNiSHH,Taryn,Jaycox",
                "fBYRtPtAlC,John,Smith", 
                "fjM66woroy,Yadira,Irving",
                "fQFLNRDae8,Vella,Lynam",
                "fOSjdLnNP3,Qiana,Walk",
                "uxz2jFwr5I,Benito,Festa",
                "oi6IhEzu9R,Leatrice,Oquinn",
                "4dBdXURAz3,Jacques,Cuellar",
                "gXWj37JC5d,Shaun,Kreiger"
            };
            
            // Create maps for easy lookup
            Map<String, String> emailMap = new HashMap<>();
            Map<String, String[]> nameMap = new HashMap<>();
            
            // Load email data
            for (String line : emails) {
                String[] parts = line.split(",");
                emailMap.put(parts[0].trim(), parts[1].trim());
            }
            
            // Load name data
            for (String line : names) {
                String[] parts = line.split(",");
                nameMap.put(parts[0].trim(), new String[]{parts[1].trim(), parts[2].trim()});
            }
            
            // Write merged CSV file
            PrintWriter writer = new PrintWriter(new FileWriter("files/client_data_merged.csv"));
            writer.println("user_id,email,first_name,last_name");
            
            // Get all unique user IDs
            Set<String> allUserIds = new HashSet<>();
            allUserIds.addAll(emailMap.keySet());
            allUserIds.addAll(nameMap.keySet());
            
            // Write each user's data
            for (String userId : allUserIds) {
                String email = emailMap.getOrDefault(userId, "");
                String[] nameData = nameMap.getOrDefault(userId, new String[]{"", ""});
                
                writer.printf("%s,%s,%s,%s%n", 
                    userId, email, nameData[0], nameData[1]);
            }
            
            writer.close();
            
        } catch (Exception e) {
        }
    }
}