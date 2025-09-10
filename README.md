# Piano TC Assessment

## Python Implementation

1. Copy `config_template.py` to `config.py` and add Piano.io credentials
2. Run: `python main.py`

**Output:** [final_user_data.csv](python/files/final_user_data.csv) (sorted by email)

## Java Implementation

1. Add Piano.io credentials to `Config.java`
2. Run: `mvn compile`
3. Run: `mvn exec:java -Dexec.mainClass="UserDataProcessor"`
4. Run: `mvn exec:java -Dexec.mainClass="PianoApiClient"`
5. Run: `mvn exec:java -Dexec.mainClass="FinalDataProcessor"`

**Output:** [final_user_data.csv](java-api/files/final_user_data.csv) (sorted by email)

## Process

Merges user data from local sources, fetches existing users from Piano.io API, and updates user_ids with Piano system IDs where users already exist.
