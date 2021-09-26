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

#사용자 접속 시
def accept(sock, mask):
    conn, addr = sock.accept()  # Should be ready
    print('accepted', conn, 'from', addr)
    conn.setblocking(False)
    sel.register(conn, selectors.EVENT_READ, read)

#사용자의 동작 요청에 따라서 작동
def read(conn, mask):
    data = conn.recv(1000)  # Should be ready
    if data:
        print('echoing', repr(data), 'to', conn)
        if "imageProcess" in data.decode("utf-8"): #이미지 프로세싱
            print("imageProccess...");

            sub.call(['java', '-jar', ".\\imageServer.jar","3100"]) #이미지 수신 스트림 실행
            wineID = imageProcess.imageProcessModule("Image Saved File Path/wineimage.png") #전송 받은 이미지 이미지 프로세싱 실행

            # 해당 와인 정보
            wine_data_list = wine_db.getWineInfo(wineID)
            wine_data = ':'.join(str(e) for e in wine_data_list[0])

            data = bytes(wine_data + "\n", encoding='utf-8')

            conn.send(data) #와인 정보 전송

        elif "Recommend" in data.decode("utf-8"): #추천 와인 리스트
            userID = data.decode("utf-8").replace("Recommend", "")
            userID = userID.replace("\n", "") #사용자 ID 확인
            print("recommend try...");
            print(userID)

            recommendList = recommend.recommendModule(userID) #추천 와인 리스트 생성

            #추천 와인 리스트
            recommendResult = ','.join(str(e) for e in recommendList)
            data = bytes(recommendResult + "\n", encoding='utf-8')

            conn.send(data)  # Hope it won't block #추천 와인 리스트 전송

        elif "Graph" in data.decode("utf-8"): #취향 그래프
            userID = data.decode("utf-8").replace("Graph", "")
            userID = userID.replace("\n", "") #사용자 ID 확인

            #사용자 그래프 데이터
            favorList = graphModule.getFavorList(userID)
            favorList = ','.join(str(e) for e in favorList)

            #유사한 사용자 그래프 데이터
            usersGraph = recommend.usersGraph(userID)
            usersGraph = ','.join(str(e) for e in usersGraph)

            print("\n" + favorList + "\n")
            print(usersGraph)
            data = bytes(favorList + ":" + usersGraph + "\n", encoding='utf-8')

            conn.send(data) #그래프 데이터 전송


        elif "Login" in data.decode("utf-8"): #로그인
            login_info = data.decode("utf-8").replace("Login", "")
            login_info = login_info.replace("\n", "") #입력 정보 확인

            user_info = login_info.split(", ")
            print("lgoin try...")
            print(user_info)
            result = login.login(user_info[0], user_info[1]) #로그인 시도

            if "success" in result: #성공시
                result = recommend.recommendModule(user_info[0]) #추천 와인 리스트 생성
                result = ','.join(str(e) for e in result)

            data = bytes(result + "\n", encoding='utf-8')

            conn.send(data) #성공 시 추천와인 리스트 실패시 "Failed" 전송

        elif "Register" in data.decode("utf-8"): #회원가입
            login_info = data.decode("utf-8").replace("Register", "")
            login_info = login_info.replace("\n", "") #입력 정보 확인

            user_info = login_info.split(", ")
            print("Register try...")
            print(user_info)
            result = addmember.addmember(user_info[0], user_info[1]) #회원가입 시도

            data = bytes(result + "\n", encoding='utf-8')

            conn.send(data) #성공시 "Success", 실패시 "Fialed" 전송

        elif "Review" in data.decode("utf-8"): #와인 리뷰
            review_info = data.decode("utf-8").replace("Review", "")
            review_info = review_info.replace("\n", "") #입력 정보 확인

            review_data = review_info.split(", ")
            print("Review Data Recode...")
            print(review_data)
            if review_data[2] != 0: #펑가 점수가 0이 아닐 때
                result = reviewdata.updatereivew(review_data[0], int(review_data[1]), int(review_data[2]))

            else:
                result = "success"

            data = bytes(result + "\n", encoding='utf-8')

            conn.send(data) #완료 확인 전송

        elif "Info" in data.decode("utf-8"): #와인 정보, 리뷰 X
            wine_id = data.decode("utf-8").replace("Info", "")
            wine_id = wine_id.replace("\n", "") #와인 ID 확인

            wine_data_list = wine_db.getWineInfo(wine_id) #해당 와인 정보
            print(wine_data_list)
            if len(wine_data_list) > 0:
                wine_data = ':'.join(str(e) for e in wine_data_list[0])

                data = bytes(wine_data + "\n", encoding='utf-8')

                conn.send(data)  #와인 데이터 전송
            else: #해당 와인이 존재하지 않음
                data = bytes("failed\n", encoding='utf-8')

                conn.send(data) #실패 전송

        elif "List" in data.decode("utf-8"): #리뷰 리스트
            userID = data.decode("utf-8").replace("List", "")
            userID = userID.replace("\n", "") #사용자 ID 확인

            print(userID)

            reviews = recommend.userInfo(userID) #사용자 리뷰 리스트
            reviews = ','.join(str(e) for e in reviews);
            print(reviews)
            data = bytes(reviews + "\n", encoding='utf-8')

            conn.send(data) #사용자 리뷰 리스트 전송

        elif "Able" in data.decode("utf-8"): #와인 정보, 리뷰 O
            wine_id = data.decode("utf-8").replace("Able", "")
            wine_id = wine_id.replace("\n", "") #와인 ID 확인

            wine_data_list = wine_db.getWineInfo(wine_id) #해당 와인 정보
            print(wine_data_list)
            if len(wine_data_list) > 0:
                wine_data = ':'.join(str(e) for e in wine_data_list[0])

                data = bytes(wine_data + "\n", encoding='utf-8')

                conn.send(data) #와인 데이터 전송
            else: #해당 와인이 존재하지 않음
                data = bytes("failed\n", encoding='utf-8')

                conn.send(data) #실패 전송

    else: #사용자 접속 해제
        print('closing', conn)
        sel.unregister(conn)
        conn.close()


#서버 실행
sock = socket.socket()
sock.bind(("Server IP Address", 9999))
sock.listen(100)
sock.setblocking(False)
sel.register(sock, selectors.EVENT_READ, accept)
print("Sever Activated")


while True:
    events = sel.select()
    for key, mask in events:
        callback = key.data
        callback(key.fileobj, mask)