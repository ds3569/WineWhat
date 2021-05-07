from PIL import Image
import numpy as np
import kerasModel as cnn
import os

class imageProcessModule:
    def __init__(self, fname):
        root_dir = "./image"

        image_size = 50
        categories = ["abbazias, blue moon moscato", "avalon cabernet sauvignon", "caldirola, amante",
                      "caldirola, durimaan", "villa m, julia"]

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
        model.load_weights("./image/WineDataSet-model.hdf5")

        # 데이터 예측
        pre = model.predict(X)
        for i, p in enumerate(pre):
            y = np.argmax(p)
            print(files[i])
            print(categories[y])
