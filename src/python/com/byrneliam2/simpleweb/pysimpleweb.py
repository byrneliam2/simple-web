"""
Simple Python implementation of a Web server that responds only to GET commands.
"""

import socket
import sys

PORT = 8080
INDEX = "src/web/index.html"


def run():
    try:
        servsock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        servsock.bind(('', PORT))
        while True:
            sock, addr = servsock.accept()
            process(sock)
            sock.close()
    except socket.error:
        sys.exit()
    finally:
        servsock.close()


def process():
    pass


run()
