import pymysql
import reviewdata

#회원가입
def addmember(user_ID,user_Passward):
    #user_db와의 연동
    user_db = pymysql.connect(
        user='root',
        passwd='1234',
        host='127.0.0.1',
        db='reviewtestdb',
        charset='utf8'
    )
    # 커서 지정
    cursor = user_db.cursor(pymysql.cursors.SSCursor)

    #user_info 테이블 select
    sql = "SELECT * FROM user_info"
    cursor.execute(sql)
    dateSet = cursor.fetchall()

    #chcker값 0으로 초기회
    checker = 0

    #user_ID 중복확인
    for i in range(0, len(dateSet)):
        if dateSet[i][0] == user_ID:
            checker = 1
    if checker == 1:
        print("아이디가 이미 존재합니다.")
        return "fialed"
    #userID가 존재하지 않을 시, db에 insert(아이디 생성)
    else:
        sql = "INSERT INTO user_info(user_ID, user_Passward) VALUES(%s, %s);"

        #커서 생성후, user_db commit
        cursor.executemany(sql, [[user_ID,user_Passward]])
        user_db.commit()

        #기본 리뷰 데이터 생성
        reviewdata.makedefualtreviewdata(user_ID)
        return "success"


#addmember('userI','7878')






