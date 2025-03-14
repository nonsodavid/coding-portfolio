import sqlite3 # Import the sqlite3 module for working with SQLite databases.
import pandas as pd # Import pandas for data manipulation and analysis.
import matplotlib.pyplot as plt # Import Matplotlib's pyplot module for creating visualizations.
from tabulate import tabulate # Import tabulate for displaying data in a well-formatted table.

def calculate_top5_artists(db_file):
    """
    Identify the top 5 artists within a user-specified range of years.

    Parameters:
        db_file (str): Path to the SQLite database file.
    """
    # Prompt user for year range
    while True:  # Loop until the user provides valid input.
        try:
            start_year = int(input("Enter the start year (1998-2020): "))  # Prompt the user for the start year.
            end_year = int(input("Enter the end year (1998-2020): "))  # Prompt the user for the end year.
            if 1998 <= start_year <= 2020 and 1998 <= end_year <= 2020 and start_year <= end_year: # Validate the range: years must be between 1998 and 2020, and start_year <= end_year.
                break # Exit the loop if the input is valid.
            else:
                print("Invalid year range. Please try again.")  # Inform the user of invalid input.
        except ValueError:
            print("Invalid input. Please enter valid years.")# Handle cases where input cannot be converted to an integer.

    # Connect to the SQLite database
    conn = sqlite3.connect(db_file)

    # Query to calculate rank value for each artist
    query = '''
        SELECT Artist.Artist_Name AS Artist,
               COUNT(Song.ID) AS Total_Songs,
               AVG(Song.Popularity) AS Avg_Popularity,
               (COUNT(Song.ID) * 0.6 + AVG(Song.Popularity) * 0.4) AS Rank_Value
        FROM Song
        JOIN Artist ON Song.Artist_ID = Artist.ID
        WHERE Song.Year BETWEEN ? AND ?
        GROUP BY Artist.Artist_Name
        ORDER BY Rank_Value DESC
        LIMIT 5;
    '''
     # SQL query to calculate the top 5 artists based on a ranking formula:
    # - Rank Value = 60% weight for the number of songs + 40% weight for average popularity.
    # - The query filters songs by the user-specified year range.

    df = pd.read_sql_query(query, conn, params=(start_year, end_year)) # Execute the query with the provided year range and load the results into a pandas DataFrame.
    conn.close()

    if df.empty: # Check if the DataFrame is empty (no data for the specified year range).
        print(f"No data found for the years {start_year} to {end_year}.")  # Inform the user that no data is available.
        return

    # Display the top 5 artists in a table
    print(f"\nTop 5 Artists ({start_year}-{end_year}):")
    print(tabulate(df, headers='keys', tablefmt='psql', showindex=False)) # Display the DataFrame as a table using the tabulate library.

    # Plot a bar chart for rank values
    plt.figure(figsize=(10, 6))  # Create a new figure with specified dimensions
    plt.bar(df['Artist'], df['Rank_Value'], color='skyblue', alpha=0.7)  # Plot a bar chart with the artists on the x-axis and their rank values on the y-axis.
    plt.xlabel("Artists") # Label the x-axis.
    plt.ylabel("Rank Value") # Label the y-axis.
    plt.title(f"Top 5 Artists by Rank Value ({start_year}-{end_year})")  # Add a title to the chart.
    plt.xticks(rotation=45)  # Rotate the x-axis labels for better readability.
    plt.tight_layout() # Adjust the layout to ensure all elements fit well.
    plt.savefig(f"top5_artists_{start_year}_{end_year}.png")  # Display the chart.
    plt.show()   # Display the chart.

# Run top 5 artists analysis
if __name__ == "__main__": # Check if the script is being run as the main program.
    calculate_top5_artists("CWDatabase.db") # Call the function to calculate the top 5 artists, using 'CWDatabase.db' as the database file.