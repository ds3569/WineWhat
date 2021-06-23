import pymysql

#와인 리뷰 평점 변경
def updatereivew(userID, wineID, reviewPoint):
    review = pymysql.connect(
        user='root',
        passwd='1234',
        host='127.0.0.1',
        db='reviewtestdb',
        charset='utf8'
    )

    cursor = review.cursor(pymysql.cursors.SSCursor)

    # json update
    sql = "update reviewtest set review = JSON_REPLACE(review, '$.\"%s\"', %s) where id = %s;"

    """# json insert
    sql2 = "update reviewtest set review = JSON_INSERT(review, '$.\"%s\"', %s) where id = %s;"
    # json remove
    sql3 = "update reviewtest set review = JSON_REMOVE(review, '$.\"%s\"') where id = %s;"""""

    data = [(wineID, reviewPoint, userID)]
    cursor.executemany(sql, data)
    review.commit()

    sql = "SELECT * FROM reviewtest;"
    cursor.execute(sql)
    dateSet = cursor.fetchall()

    print(dateSet)
    return "success"


def makedefualtreviewdata(userID):
    review = pymysql.connect(
        user='root',
        passwd='1234',
        host='127.0.0.1',
        db='reviewtestdb',
        charset='utf8'
    )

    cursor = review.cursor(pymysql.cursors.SSCursor)
    sql = "SELECT wine_ID from wine_detail"
    cursor.execute(sql)
    winenum = len(cursor.fetchall())


    reviewlist = []
    wineID = 1
    for i in range(0, winenum*2):
        if i%2 == 0:
            reviewlist.append(wineID)
            wineID += 1
        else:
            reviewlist.append(0)

    jsonsql = "json_object(" + ', '.join(str(e) for e in reviewlist) + ")"


    sql = "insert into reviewtest(id, review) values(%s, " + jsonsql + ");"
    cursor.execute(sql, userID)
    review.commit()
    print ("review data set done")

    #테스트 데이터 삭제용
    """sql = "SELECT * FROM reviewtest;"
    cursor.execute(sql)
    dateSet = cursor.fetchall()

    print(dateSet)

    sql = "delete from reviewtest where id = %s"
    cursor.execute(sql, userID)
    review.commit()

    sql = "SELECT * FROM reviewtest;"
    cursor.execute(sql)
    dateSet = cursor.fetchall()

    print(dateSet)"""



