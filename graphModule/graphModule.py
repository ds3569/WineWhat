import getData
import getdata

def getFavorList(username):
    favorList =[]

    wineSet = getData.getWineData()
    dataSet = getdata.getData()
    userinfo = getdata.getUserInfo(username, dataSet)
    starPoint = getData.setStarPoint(userinfo)

    sweetFavor = getData.getsweetfavor(wineSet, starPoint)
    acidicFavor = getData.getacidicfavor(wineSet, starPoint)
    bodyFavor = getData.getbodyfavor(wineSet, starPoint)
    tanninFavor = getData.gettanninfavor(wineSet, starPoint)

    favorList.append(sweetFavor)
    favorList.append(acidicFavor)
    favorList.append(bodyFavor)
    favorList.append(tanninFavor)

    return favorList
