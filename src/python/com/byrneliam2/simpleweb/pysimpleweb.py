"""
Simple Python implementation of a Web server that responds only to GET commands.
"""
import os
import socket

PORT = 8080
PACKET_SIZE = 1024
HTML_PATH = "src/web/html/"


def run():
    servsock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    try:
        servsock.bind((' ', PORT))
        servsock.listen(1)
        print("Socket bound and listening.")
        while True:
            sock, addr = servsock.accept()
            print("Client accepted.")
            process(sock)
            print("Client processed.")
            sock.close()
            print("Socket closed.")
    except socket.error:
        raise socket.error
    finally:
        servsock.close()


def process(sock):
    response = ""
    try:
        data = recv_data(sock)
        print("Data received.")
        if not data:
            return
        page = get_getrequest(data)
        print("Page " + page + " received.")

        if page == "/":
            response = get_resource("index.html")
        elif page == "/somethingelse":
            pass
        else:
            response = form_html_paragraph("Unable to locate requested file.")

        print("Response formed.")
        send_data(response, sock)
        print("Data sent.")
    except IOError as e:
        send_data(str(e), sock)
        print("Error sent.")


def recv_data(sock):
    request = ""
    data = sock.recv(PACKET_SIZE).decode()
    # while data:
    #     request += data
    #     data = sock.recv(PACKET_SIZE).decode()
    return data


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
    print("WORKING DIR: " + os.getcwd())
    with open(HTML_PATH + path) as file:
        out += file.read()
    print("Resource processed.")
    return out


def form_response(body):
    return "HTTP/1.1 200 OK\n" \
           + "Content-Length: " + str(len(body)) + "\n" \
           + "Content-Type: text/html; charset=UTF-8\n\n" \
           + body


def form_html_paragraph(text):
    return "<!DOCTYPE html><html><body><p>" + text + "</p></body></html>"


run()
