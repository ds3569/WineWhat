import pymysql
from ast import literal_eval



#DB에서 데이터 받아오기
def getWineData():
    review = pymysql.connect(
        user='root',
        passwd='1234',
        host='127.0.0.1',
        db='reviewtestdb',
        charset='utf8'
    )

    cursor = review.cursor(pymysql.cursors.SSCursor)

    sql = "SELECT * FROM wine_detail;"
    cursor.execute(sql)
    dateSet = cursor.fetchall()
    
    return dateSet

#사용자 리뷰 기록의 별점 별로 가산점 리스트 작성
def setStarPoint(userInfo):

    userReview = list(userInfo.values())
    userReview = literal_eval(str(userReview[0]))
    userReview = list(userReview.values())



    starPoint = []

    for i in userReview:
        if i == 0:
            starPoint.append(0)
        elif i == 1:
            starPoint.append(0.1)
        elif i == 2:
            starPoint.append(0.3)
        elif i == 3:
            starPoint.append(0.6)
        elif i == 4:
            starPoint.append(0.8)
        else:
            starPoint.append(1.0)

    return starPoint

#리뷰한 모든 와인의 Sweet 평가
def getsweetfavor(wineInfo, starPoint):
    favor = 0
    num = 0

    for i in range(0, len(wineInfo)):
        favor = favor + (wineInfo[i][11] * starPoint[i]) #해당 와인의 Sweet * 리뷰 가산점
    for i in range(0, len(wineInfo)):
        if (starPoint[i] != 0):
            num += 1

    return favor/num

#리뷰한 모든 와인의 Acidic 평가
def getacidicfavor(wineInfo, starPoint):
    favor = 0
    num = 0
    for i in range(0, len(wineInfo)):
        favor = favor + (wineInfo[i][12] * starPoint[i]) #해당 와인의 Acidic * 리뷰 가산점
    for i in range(0, len(wineInfo)):
        if (starPoint[i] != 0):
            num += 1

    return favor/num

#리뷰한 모든 와인의 Body 평가
def getbodyfavor(wineInfo, starPoint):
    favor = 0
    num = 0
    for i in range(0, len(wineInfo)):
        favor = favor + (wineInfo[i][13] * starPoint[i]) #해당 와인의 Body * 리뷰 가산점
    for i in range(0, len(wineInfo)):
        if (starPoint[i] != 0):
            num += 1

    return favor/num

#리뷰한 모든 와인의 Tannin 평가
def gettanninfavor(wineInfo, starPoint):
    favor = 0
    num = 0
    for i in range(0, len(wineInfo)):
        favor = favor + (wineInfo[i][14] * starPoint[i]) #해당 와인의 Tannin * 리뷰 가산점
    for i in range(0, len(wineInfo)):
        if (starPoint[i] != 0):
            num += 1

    return favor/num