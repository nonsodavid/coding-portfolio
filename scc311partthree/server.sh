#!/bin/bash

# Set the classpath to include the current directory and the 'keys' directory
CLASSPATH=.:keys

# Specify the full path to the rmiregistry executable
RMIREGISTRY_PATH="C:\Program Files\Java\jdk-21\bin\rmiregistry"

# Compile the server code
javac *.java

# Start the RMI registry in the background
"$RMIREGISTRY_PATH" &
#rmiregistry
# Run the RMI server
java AuctionServer &

# Give the server some time to start (you can adjust this duration)
sleep 5
java Replica 1 &
sleep 2
java Replica 2 &
sleep 2
java Replica 3 &
sleep 2
java FrontEnd &

# Run the RMI client
java AuctionClient

sleep 5
