import pandas as pd # Import the pandas library, commonly used for data manipulation and analysis, using 'pd' for convenience.
import sqlite3 # Import the sqlite3 module to interact with SQLite databases, allowing for SQL operations such as creating tables, inserting data, and querying.

# Step A: Load and Rename Columns
dfSongs = pd.read_csv('songs.csv')# Load the CSV file 'songs.csv' into a pandas DataFrame called dfSongs.

# Rename column "duration_ms" to "duration" and convert milliseconds to seconds
dfSongs.rename(columns={'duration_ms': 'duration'}, inplace=True)
dfSongs['duration'] = (dfSongs['duration'] / 1000).round().astype(int) # Convert the duration from milliseconds to seconds, round the result, and change to integer.

# Step B: Apply Filters
dfFiltered = dfSongs[
    (dfSongs['popularity'] > 50) &  # Popularity greater than 50
    (dfSongs['speechiness'] >= 0.33) & (dfSongs['speechiness'] <= 0.66) &  # Speechiness between 0.33 and 0.66
    (dfSongs['danceability'] > 0.20)  # Danceability greater than 0.20
]
# Create a new DataFrame, dfFiltered, containing only rows that meet all filter conditions.

# Step C: Database Creation
# Connect to SQLite database 
conn = sqlite3.connect('CWDatabase.db')
cursor = conn.cursor() # Create a cursor object to execute SQL queries.

# Create tables
cursor.execute('''
     CREATE TABLE IF NOT EXISTS Song (
        ID INTEGER PRIMARY KEY AUTOINCREMENT,
        Song TEXT NOT NULL,
        Duration INTEGER NOT NULL,
        Explicit INTEGER,
        Year INTEGER NOT NULL,
        Popularity INTEGER NOT NULL,
        Danceability REAL NOT NULL,
        Speechiness REAL NOT NULL,
        Artist_ID INTEGER,
        Genre_ID INTEGER,
        FOREIGN KEY (Artist_ID) REFERENCES Artist(ID),
        FOREIGN KEY (Genre_ID) REFERENCES Genre(ID)
)
''')
# Create the 'Song' table if it doesn't already exist, with fields for song details and foreign keys for artist and genre.

cursor.execute('''
      CREATE TABLE IF NOT EXISTS Genre (
        ID INTEGER PRIMARY KEY AUTOINCREMENT,
        Genre TEXT NOT NULL
)
''')
# Create the 'Genre' table if it doesn't already exist, with fields for genre details.

cursor.execute('''
      CREATE TABLE IF NOT EXISTS Artist (
         ID INTEGER PRIMARY KEY AUTOINCREMENT,
         Artist_Name TEXT UNIQUE
)
''')
# Create the 'Artist' table if it doesn't already exist, with a unique field for artist names.

# Commit 
conn.commit()

# Populate Artist table with unique artists
for artist in dfFiltered['artist'].unique():    # Loop through unique artist names in the filtered DataFrame.
    cursor.execute('INSERT OR IGNORE INTO Artist (Artist_Name) VALUES (?)', (artist,))  # Insert each unique artist into the 'Artist' table, ignoring duplicates.

# Populate Genre table with unique genres
if 'genre' in dfFiltered.columns:  # Check if the 'genre' column exists in the filtered DataFrame.
    for genre in dfFiltered['genre'].unique(): # Loop through unique genre names
        cursor.execute('INSERT OR IGNORE INTO Genre (Genre) VALUES (?)', (genre,))  # Insert each unique genre into the 'Genre' table, ignoring duplicates.

# Commit before inserting Song data
conn.commit()

# Populate Song table with relevant foreign keys for Artist and Genre
for _, row in dfFiltered.iterrows(): # Loop through each row in the filtered DataFrame.
    # Get Artist_ID
    cursor.execute('SELECT ID FROM Artist WHERE Artist_Name = ?', (row['artist'],)) # Retrieve the ID of the current artist from the 'Artist' table.
    artist_id = cursor.fetchone()[0]  # Fetch the first result (the ID) from the query result.

    # Get Genre_ID if 'genre' column exists in the filtered data
    genre_id = None # Initialize genre_id as None.
    if 'genre' in dfFiltered.columns: # Check if the 'genre' column exists
        cursor.execute('SELECT ID FROM Genre WHERE Genre = ?', (row['genre'],))  # Retrieve the ID of the current genre from the 'Genre' table.
        genre_id = cursor.fetchone()[0] # Fetch the first result (the ID) from the query result.

    # Insert into Song table
    cursor.execute('''
        INSERT INTO Song (Song, Duration, Explicit, Year, Popularity, Danceability, Speechiness, Artist_ID, Genre_ID)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
    ''', (row['song'], row['duration'], row['explicit'], row['year'], row['popularity'], 
          row['danceability'], row['speechiness'], artist_id, genre_id))
         # Insert the song's details into the 'Song' table, including foreign keys for artist and genre.


# Commit changes and close connection
conn.commit()
conn.close()

print("Data has been successfully cleaned and stored in CWDatabase.db.") # Print a success message to indicate the process is complete.
