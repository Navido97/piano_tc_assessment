# Python Implementation

This implementation processes user data and integrates with the Piano.io API to reconcile user records.

## Quick Start

1. Copy `config_template.py` and change the name to `config.py` and add your Piano.io credentials
2. Run: `python main.py`

## Key Files

- **[main.py](python/main.py)** - Complete pipeline script
- **[files/final_user_data.csv](python/files/final_user_data.csv)** - Final output with reconciled user data

## Process

The script merges user data from two CSV sources, fetches existing users from Piano.io API, and updates user_ids with Piano system IDs where users already exist in the system.
