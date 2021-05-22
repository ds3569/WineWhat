import base64
import io
import selectors
import socket
import cv2

import numpy as np
from PIL import Image


import recommend
import graphModule

sel = selectors.DefaultSelector()


def accept(sock, mask):
    conn, addr = sock.accept()  # Should be ready
    print('accepted', conn, 'from', addr)
    conn.setblocking(False)
    sel.register(conn, selectors.EVENT_READ, read)

def read(conn, mask):
    data = conn.recv(1000)  # Should be ready
    if data:
        print('echoing', repr(data), 'to', conn)
        if "imageProcess" in data.decode("utf-8"):
            base64String = data.decode("utf-8").replace("imageProcess", "")
            base64Code = base64.b64decode(base64String)
            imageCode = base64.b64encode(base64Code)
            print(base64Code)


            conn.send(bytes("Under development Function\n", encoding='utf-8'))  # Hope it won't block
        elif "Recommend" in data.decode("utf-8"):
            userID = data.decode("utf-8").replace("Recommend", "")
            userID = userID.replace("\n", "")
            recommendList = recommend.recommendModule(userID)
            recommendResult = ','.join(str(e) for e in recommendList)
            data = bytes(recommendResult + "\n", encoding='utf-8')

            conn.send(data)  # Hope it won't block

        elif "Graph" in data.decode("utf-8"):
            userID = data.decode("utf-8").replace("Graph", "")
            userID = userID.replace("\n", "")
            favorList = graphModule.getFavorList(userID)
            favorList = ','.join(str(e) for e in favorList)
            print(favorList)
            data = bytes(favorList + "\n", encoding='utf-8')

            conn.send(data)
 



    else:
        print('closing', conn)
        sel.unregister(conn)
        conn.close()

sock = socket.socket()
sock.bind(("192.168.219.104", 9999))
sock.listen(100)
sock.setblocking(False)
sel.register(sock, selectors.EVENT_READ, accept)
print("Sever Activated")

while True:
    events = sel.select()
    for key, mask in events:
        callback = key.data
        callback(key.fileobj, mask)