from PIL import Image
import numpy as np
import kerasModel as cnn
import os


def imageProcessModule(fname):
    root_dir = "D://WWworkspace/WineWhat/imageProcessing/image"

    image_size = 50
    categories = []
    filenames = os.listdir("D://WWworkspace/WineWhat/imageProcessing/image")
    for filename in filenames:
        full_filename = os.path.join(filename)
        categories.append(full_filename)

    if ("wineDataSet.npy" in categories):
        categories.remove("wineDataSet.npy")
    if ("WineDataSet-model.hdf5" in categories):
        categories.remove("WineDataSet-model.hdf5")

    # 입력 이미지를 Numpy로 변환
    X = []
    files = []
    img = Image.open(fname)
    img = img.convert("RGB")
    img = img.resize((image_size, image_size))
    in_data = np.asarray(img)
    X.append(in_data)
    files.append(fname)

    X = np.array(X)

    # CNN 모델 구축
    model = cnn.build_model(X.shape[1:])
    model.load_weights("D:/WWworkspace/WineWhat/imageProcessing/image/WineDataSet-model.hdf5")

    # 데이터 예측
    pre = model.predict(X)
    for i, p in enumerate(pre):
        y = np.argmax(p)
        print(files[i])
        print(categories[y])
        return categories[y]
