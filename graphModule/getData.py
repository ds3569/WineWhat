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

def setStarPoint(userInfo):
    unReviewedList = {}
    userReview = list(userInfo.values())
    userReview = literal_eval(str(userReview[0]))
    userReview = list(userReview.values())

    print(userReview)

    starPoint = []

    for i in userReview:
        if i == 0:
            starPoint.append(0)
        elif i == 1:
            starPoint.append(10)
        elif i == 2:
            starPoint.append(30)
        elif i == 3:
            starPoint.append(60)
        elif i == 4:
            starPoint.append(80)
        else:
            starPoint.append(100)
    print(starPoint)
    return starPoint

def getsweetfavor(wineInfo, starPoint):
    favor = 0

    for i in range(0, len(wineInfo)):
        favor = favor + (wineInfo[i][12] * starPoint[i])


    return favor/len(wineInfo)

def getacidicfavor(wineInfo, starPoint):
    favor = 0
    for i in range(0, len(wineInfo)):
        favor = favor + (wineInfo[i][13] * starPoint[i])

    return favor/len(wineInfo)

def getbodyfavor(wineInfo, starPoint):
    favor = 0
    for i in range(0, len(wineInfo)):
        favor = favor + (wineInfo[i][14] * starPoint[i])

    return favor/len(wineInfo)

def gettanninfavor(wineInfo, starPoint):
    favor = 0
    for i in range(0, len(wineInfo)):
        favor = favor + (wineInfo[i][15] * starPoint[i])

    return favor/len(wineInfo)
