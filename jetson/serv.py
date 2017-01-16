import socket, subprocess

# Create a TCP/IP socket
sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
# Bind the socket to the port
server_address = (socket.gethostname(), 5806)
print( 'Starting up on %s port %s' % server_address )
sock.bind(server_address)
# Listen for incoming connections
sock.listen(1)

while True:
    # Wait for a connection
    print( 'Waiting for a connection' )
    connection, client_address = sock.accept()
    try:
        print( 'Connection from', client_address )

        # Receive the data in small chunks and retransmit it
        while True:
            data = connection.recv(16)
            #print( 'received "%s"' % data)
            if data:
                if data == b'Begin OpenCV':
                    print("Connection validated: running command")
                    subprocess.run(["ls", "-l"]) # Exec code here
                else:
                    print("Incorrect validation")
            else:
                print ( 'No more data from', client_address )
                break

    finally:
        # Clean up the connection
        connection.close()
