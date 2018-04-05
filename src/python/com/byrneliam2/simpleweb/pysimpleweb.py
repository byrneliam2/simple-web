"""
Simple Python implementation of a Web server that responds only to GET commands.
"""

import socket

PORT = 8080
PACKET_SIZE = 1024
WEBPATH = "src/web/"


def run():
    servsock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    try:
        servsock.bind((' ', PORT))
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
    response = ""
    try:
        data = recv_data(sock)
        if not data:
            return
        page = get_getrequest(data)

        if page == "/":
            response = get_resource("index.html")
        elif page == "/somethingelse":
            pass
        else:
            response = form_html_paragraph("Unable to locate requested file.")

        send_data(response, sock)
    except IOError as e:
        send_data(str(e), sock)


def recv_data(sock):
    request = ""
    data = sock.recv(PACKET_SIZE).decode()
    while data:
        request += data
        data = sock.recv(PACKET_SIZE).decode()
    return request


def send_data(response, sock):
    sock.send(form_response(response).encode())


def get_getrequest(data):
    """
    Search the request to locate a GET command and return the file requested if found.
    """
    tokens = data.split(' ')
    for i in range(len(tokens)):
        if tokens[i] == "GET":
            return tokens[i + 1]
    return form_html_paragraph("Bad request (no GET command found.)")


def get_resource(path):
    out = ""
    with open(WEBPATH + path) as file:
        out += file.read()
    return out


def form_response(body):
    return "HTTP/1.1 200 OK\n" \
           + "Content-Length: " + str(len(body)) + "\n" \
           + "Content-Type: text/html; charset=UTF-8\n\n" \
           + body


def form_html_paragraph(text):
    return "<!DOCTYPE html><html><body><p>" + text + "</p></body></html>"


run()
