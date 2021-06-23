import base64
import io
import selectors
import socket
import cv2
import subprocess as sub

import numpy as np
from PIL import Image


import recommend
import graphModule
import imageProcess
import login
import addmember
import wine_db
import reviewdata

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
            print("imageProccess...");

            sub.call(['java', '-jar', ".\\imageServer.jar","3100"])
            wineID = imageProcess.imageProcessModule("D:/WWworkspace/WineWhat/serverDemo/wineimage.png")
            wine_data_list = wine_db.getWineInfo(wineID)
            wine_data = ':'.join(str(e) for e in wine_data_list[0])

            data = bytes(wine_data + "\n", encoding='utf-8')  # Hope it won't block

            conn.send(data)  # Hope it won't block
        elif "Recommend" in data.decode("utf-8"):
            userID = data.decode("utf-8").replace("Recommend", "")
            userID = userID.replace("\n", "")
            print("recommend try...");
            print(userID)

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


        elif "Login" in data.decode("utf-8"):
            login_info = data.decode("utf-8").replace("Login", "")
            login_info = login_info.replace("\n", "")

            user_info = login_info.split(", ")
            print("lgoin try...")
            print(user_info)
            result = login.login(user_info[0], user_info[1])

            data = bytes(result + "\n", encoding='utf-8')

            conn.send(data)
        elif "Register" in data.decode("utf-8"):
            login_info = data.decode("utf-8").replace("Register", "")
            login_info = login_info.replace("\n", "")

            user_info = login_info.split(", ")
            print("Register try...")
            print(user_info)
            result = addmember.addmember(user_info[0], user_info[1])

            data = bytes(result + "\n", encoding='utf-8')

            conn.send(data)
        elif "Review" in data.decode("utf-8"):
            review_info = data.decode("utf-8").replace("Review", "")
            review_info = review_info.replace("\n", "")

            review_data = review_info.split(", ")
            print("Review Data Recode...")
            print(review_data)
            if review_data[2] != 0:
                result = reviewdata.updatereivew(review_data[0], int(review_data[1]), int(review_data[2]))

            else:
                result = "success"

            data = bytes(result + "\n", encoding='utf-8')

            conn.send(data)
        elif "Info" in data.decode("utf-8"):
            wine_id = data.decode("utf-8").replace("Info", "")
            wine_id = wine_id.replace("\n", "")

            wine_data_list = wine_db.getWineInfo(wine_id)
            print(wine_data_list)
            if len(wine_data_list) > 0:
                wine_data = ':'.join(str(e) for e in wine_data_list[0])

                data = bytes(wine_data + "\n", encoding='utf-8')  # Hope it won't block

                conn.send(data)  # Hope it won't block
            else:
                data = bytes("failed\n", encoding='utf-8')

                conn.send(data)









    else:
        print('closing', conn)
        sel.unregister(conn)
        conn.close()

sock = socket.socket()
sock.bind(("192.168.219.106", 9999))
sock.listen(100)
sock.setblocking(False)
sel.register(sock, selectors.EVENT_READ, accept)
print("Sever Activated")


while True:
    events = sel.select()
    for key, mask in events:
        callback = key.data
        callback(key.fileobj, mask)