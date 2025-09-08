import pandas as pd
import numpy as np
import requests
import json
from io import StringIO
from config import aid, api_token, request_url

#Step A1: Load the CSV File
#File A
file_a_csv = """user_id,email
oi6IhEzu9R,user_2@example.com
fQFLNRDae8,user_5@example.com
fBYRtPtAlC,user_7@example.com
fOSjdLnNP3,user_9@example.com
uxz2jFwr5I,user_1@example.com
zSbmdNiSHH,user_4@example.com
fjM66woroy,user_0@example.com
oh4mHXh8zN,user_3@example.com
gXWj37JC5d,user_8@example.com
4dBdXURAz3,user_6@example.com"""

data_a = pd.read_csv(StringIO(file_a_csv))
data_a['user_id'] = data_a['user_id'].str.strip()
data_a['email'] = data_a['email'].str.strip()

data_a.to_csv('file_a.csv', index=False)


#Step B1: Load the CSV File
#File B
file_b_csv = """user_id,first_name,last_name
oh4mHXh8zN,Julie,Mosser
zSbmdNiSHH,Taryn,Jaycox
fBYRtPtAlC,John,Smith
fjM66woroy,Yadira,Irving
fQFLNRDae8,Vella,Lynam
fOSjdLnNP3,Qiana,Walk
uxz2jFwr5I,Benito,Festa
oi6IhEzu9R,Leatrice,Oquinn
4dBdXURAz3,Jacques,Cuellar
gXWj37JC5d,Shaun,Kreiger"""

data_b = pd.read_csv(StringIO(file_b_csv))
data_b['user_id'] = data_b['user_id'].str.strip()
data_b['first_name'] = data_b['first_name'].str.strip()
data_b['last_name'] = data_b['last_name'].str.strip()

data_b.to_csv('file_b.csv', index=False)


#Step 3 Join on user_id, merge tables and save as new csv
merged_table = pd.merge(data_a, data_b, on='user_id', how='outer')
merged_table.to_csv('merged_data.csv', index=False)


#API Integration


# Step 1: Get API Request
params = {
    "api_token": api_token,
    "aid": aid
}

headers = {
    "Content-Type": "application/json"
}

data = {
    "aid": aid
}

#API request
response = requests.post(request_url, headers=headers, json=data, params=params)

json_response = response.json()


# Step 2: Extract users array and create DataFrame
if 'users' in json_response and isinstance(json_response['users'], list):
        df = pd.DataFrame(json_response['users'])

# Clean and strip columns
for col in df.columns:
    if df[col].dtype == 'object':
        df[col] = df[col].astype(str).str.strip()
        
#Save to CSV
df.to_csv('piano_users.csv', index=False)
piano_system = pd.read_csv('piano_users.csv')

# Step 3: Create final table by merging and updating the 'merged' table and the 'piano_system' table
final = merged_table.copy()

# Update user_id where email exists in piano_system
email_to_uid = piano_system.set_index('email')['uid'].to_dict()

for email, uid in email_to_uid.items():
    final.loc[final['email'] == email, 'user_id'] = uid

# Sort values and reset index
final = final.sort_values('email')
final = final.reset_index(drop=True)

# Create final CSV File
final.to_csv('final_table.csv', index=False)

#Final Output
final