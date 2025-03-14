import sqlite3 # Import the sqlite3 module for working with SQLite databases.
import pandas as pd # Import the pandas library for data manipulation and analysis.
import matplotlib.pyplot as plt # Import Matplotlib's pyplot module for data visualization.

def get_year_input():
    """Prompt the user for a year and validate the input."""
    while True: # Loop until the user provides a valid year.
        try:
            year = int(input("Enter a year between 1998 and 2020: "))  # Prompt the user to enter a year and convert it to an integer.
            if 1998 <= year <= 2020:  # Check if the entered year is within the valid range.
                return year # Return the valid year.
            else:
                print("Year must be between 1998 and 2020.") # Inform the user if the year is out of range.
        except ValueError:
            print("Invalid input. Please enter a valid year.") # Inform the user if the input is not a valid integer.

def fetch_genre_data(year):
    """Fetch genre statistics from the database for a given year."""
    query = """
        SELECT 
            g.Genre AS Genre,
            COUNT(s.ID) AS Total_Songs,
            AVG(s.Danceability) AS Avg_Danceability,
            AVG(s.Popularity) AS Avg_Popularity
        FROM Song s
        JOIN Genre g ON s.Genre_ID = g.ID
        WHERE s.Year = ?
        GROUP BY g.Genre;
    """
    
     # SQL query to retrieve genre statistics (total songs, average danceability, and popularity) for the given year.
     
    try:
        conn = sqlite3.connect('CWDatabase.db')  # Establish a connection to the 'CWDatabase.db' SQLite database.
        data = pd.read_sql_query(query, conn, params=(year,)) # Execute the query with the provided year and load the results into a pandas DataFrame.
    except sqlite3.Error as e:
        print("Database error:", e) # Print an error message if a database error occurs.
        data = pd.DataFrame()  # Return an empty DataFrame in case of error
    finally:
        conn.close() # Close the database connection, whether an error occurred or not.
    return data   # Return the retrieved data as a DataFrame.

def display_results(data, year):
    """Display genre statistics and visualize song counts.""" 
    if data.empty: # Check if the data is empty (no results found).
        print(f"No data found for the year {year}.") # Inform the user if no data is available for the given year.
        return

    print(f"\nGenre Statistics for {year}")  # Print the year for which statistics are displayed.
    print(data) # Display the retrieved data as a table.

    # Visualize the total number of songs per genre
    data.sort_values(by='Total_Songs', ascending=False, inplace=True) # Sort the data by the total number of songs in descending order.
    plt.figure(figsize=(10, 6)) # Create a new figure for the bar chart with specified dimensions.
    plt.barh(data['Genre'], data['Total_Songs'], color='skyblue') # Create a horizontal bar chart for total songs per genre.
    plt.xlabel('Total Songs')  # Label the x-axis.

    plt.ylabel('Genre') # Label the y-axis.
    plt.title(f"Total Songs per Genre ({year})") # Add a title to the chart.
    plt.gca().invert_yaxis()  # Invert y-axis for better readability
    plt.tight_layout()

    # Save the plot as an image
    plt.savefig(f'genre_statistics_{year}.png') # Save the chart as a PNG file with the year in the filename.
    plt.show() # Display the chart.

def main():
    """Main program function.""" 
    year = get_year_input() # Prompt the user to enter a year.
    genre_data = fetch_genre_data(year) # Fetch genre statistics for the entered year
    display_results(genre_data, year) # Display the statistics and visualizations

if __name__ == "__main__":    # Check if the script is being run as the main program.
    main()  # Call the main function to start the program.