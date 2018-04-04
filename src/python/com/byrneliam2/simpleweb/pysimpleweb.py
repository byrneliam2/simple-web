"""
Simple Python implementation of a Web server that responds only to GET commands.
"""

import socket
import sys

PORT = 8080
PACKET_SIZE = 1024
INDEX = "src/web/index.html"


def run():
    servsock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    try:
        servsock.bind((socket.gethostname(), PORT))
        servsock.listen(1)
        while True:
            sock, addr = servsock.accept()
            process(sock)
            sock.close()
    except socket.error:
        raise socket.error
        # sys.exit()
    finally:
        servsock.close()


def process(sock):
    try:
        data = get_data(sock)
        if not data:
            return
        request = data.split(' ')[0]
    except IOError as e:
        pass


def get_data(sock):
    request = ""
    data = sock.recv(PACKET_SIZE).decode()
    while data:
        request += data
        data = sock.recv(PACKET_SIZE).decode()
    return request


def response(body):
    return "HTTP/1.1 200 OK\n" \
           + "Content-Length: " + body.length() + "\n" \
           + "Content-Type: text/html; charset=UTF-8\n\n" \
           + body


run()
