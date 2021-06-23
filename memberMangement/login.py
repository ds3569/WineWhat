import pymysql


#print(dateSet)

#user_db연동 후 로그인
def login(user_ID,user_Passward):
    user_db = pymysql.connect(
        user='root',
        passwd='1234',
        host='127.0.0.1',
        db='reviewtestdb',
        charset='utf8'
    )
    #커서지정
    cursor = user_db.cursor(pymysql.cursors.SSCursor)
    #user_info 테이블 select
    sql = "SELECT * FROM user_info"
    cursor.execute(sql)
    dateSet = cursor.fetchall()

    #checker 0으로 초기화
    checker = 0

    #user_info 테이블 내의 id와 pw 확인
    for i in range(0, len(dateSet)):
        if dateSet[i][0] == user_ID and dateSet[i][1] == user_Passward:
            checker+=1
    #id,pw 일치
    if checker == 1:
        print('login sucess : '+(user_ID))
        return "success"
    #id,pw 불일치
    else:
        print('login failed')
        return "failed"
#login("userA", "1234")

