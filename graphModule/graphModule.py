import getData
import getdata

#와인 그래프 데이터 생성
def getFavorList(username):
    favorList =[]

    wineSet = getData.getWineData()
    dataSet = getdata.getData()
    userinfo = getdata.getUserInfo(username, dataSet)
    starPoint = getData.setStarPoint(userinfo)

    if (sum(starPoint) > 0): #리뷰한 와인이 있을 경우
        sweetFavor = getData.getsweetfavor(wineSet, starPoint)
        acidicFavor = getData.getacidicfavor(wineSet, starPoint)
        bodyFavor = getData.getbodyfavor(wineSet, starPoint)
        tanninFavor = getData.gettanninfavor(wineSet, starPoint)

        favorList.append(sweetFavor)
        favorList.append(acidicFavor)
        favorList.append(bodyFavor)
        favorList.append(tanninFavor)

        return favorList
    else: #리뷰한 와인이 없을 경우
        return [0, 0, 0, 0]



