import sqlite3 # Import the sqlite3 module for working with SQLite databases.
import pandas as pd # Import pandas for data manipulation and analysis.

import matplotlib.pyplot as plt # Import Matplotlib's pyplot module for data visualization.
from tabulate import tabulate # Import tabulate for displaying data in a well-formatted table.


def analyze_artist_popularity(db_file):
    """
    Analyze and display the popularity of a given artist across genres.

    Parameters:
        db_file (str): Path to the SQLite database file.
    """
    # Prompt user for artist name
    artist_name = input("Enter the artist's name: ").strip()  # Ask the user to enter an artist's name and remove any leading whitespace.


    # Connect to the SQLite database
    conn = sqlite3.connect(db_file)

    # Validate artist existence
    artist_query = "SELECT ID FROM Artist WHERE Artist_Name = ?"   # SQL query to check if the artist exists in the database.
    artist_result = conn.execute(artist_query, (artist_name,)).fetchone()  # Execute the query and fetch the first result.

    if not artist_result: # Check if no result was found (artist does not exist).
        print(f"No data found for artist: {artist_name}") # Inform the user that the artist is not in the database.
        conn.close()
        return

    artist_id = artist_result[0] # Extract the artist's ID from the query result

    # Query to calculate artist's popularity and overall genre popularity
    query = """
        SELECT g.Genre AS Genre, 
               AVG(CASE WHEN s.Artist_ID = ? THEN s.Popularity END) AS Artist_Popularity,
               AVG(s.Popularity) AS Overall_Popularity
        FROM Song s
        JOIN Genre g ON s.Genre_ID = g.ID
        GROUP BY g.Genre;
    """

    df = pd.read_sql_query(query, conn, params=(artist_id,))    # Execute the query with the artist's ID as a parameter and load the results into a pandas DataFrame.
    conn.close()

    if df.empty:  # Check if the DataFrame is empty (no data found for the artist).
        print(f"No popularity data found for artist: {artist_name}.")  # Inform the user that no popularity data is available for the artist.
        return

    # Highlight genres where the artist's popularity is above the overall average
    df['Above_Avg'] = df['Artist_Popularity'] > df['Overall_Popularity']

    # Display the results in tabular form
    print(f"\nPopularity data for artist: {artist_name}") # Print a header for the displayed data
    print(tabulate(df, headers='keys', tablefmt='psql', showindex=False)) # Display the DataFrame as a table using the tabulate library.

    # Plot a bar chart
    plt.figure(figsize=(10, 6)) # Create a new figure 
    plt.bar(df['Genre'], df['Artist_Popularity'], label=f"{artist_name} Popularity", alpha=0.7, color='blue')     # Plot a bar for the artist's popularity in each genre.
    plt.bar(df['Genre'], df['Overall_Popularity'], label="Overall Genre Popularity", alpha=0.5, color='orange') # Plot a bar for the overall popularity of each genre.


    # Highlight bars where Artist_Popularity > Overall_Popularity
    for i, row in df.iterrows(): # Loop through each row in the DataFrame.
        if row['Above_Avg']:  # Check if the artist's popularity exceeds the overall average.
            plt.text(i, row['Artist_Popularity'] + 1, '*', fontsize=12, color='red', ha='center')  # Add a red asterisk above the bar to highlight it.

    plt.xlabel("Genre")    # Label the x-axis.
    plt.ylabel("Popularity")   # Label the y-axis.
    plt.title(f"Popularity of {artist_name} Across Genres")  # Add a title to the chart.
    plt.xticks(rotation=45)  # Rotate the x-axis labels for better readability.
    plt.legend() # Add a legend to distinguish between the two bars.
    plt.tight_layout() # Adjust the layout to fit the chart elements.
    plt.savefig(f"artist_popularity_{artist_name.replace(' ', '_')}.png")  # Save the chart as a PNG file with the artist's name in the filename.
    plt.show() # Display the chart.

# Run artist popularity analysis
if __name__ == "__main__": # Check if the script is being run as the main program.
    analyze_artist_popularity("CWDatabase.db") # Call the function to analyze artist popularity, using 'CWDatabase.db' as the database file.