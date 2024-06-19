#!/bin/bash

# Set the classpath to include the current directory and the 'keys' directory
CLASSPATH=.:keys


# Compile the server code
javac AuctionServer.java
javac AuctionClient.java

#generate random numbers for the port number
port=$((1000 + RANDOM % 9000))

# Start the RMI registry using the generated port
rmiregistry &
# Give the RMI registry some time to start (you can adjust this duration)
sleep 5

# Run the RMI server in the background
java AuctionServer &

# Give the server some time to start (you can adjust this duration)
sleep 5

# Run the RMI client
java AuctionClient

# Optional: Wait for a few seconds to ensure the client has time to execute
sleep 5
