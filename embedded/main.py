import sys
sys.dont_write_bytecode = True # キャッシュ無効化
import threading
import numpy as np
import cv2
import camerathreading
import mtcnnthreading

# OpenCV Bug Fix
cv2.setNumThreads(0)

def main():
    try:
        # カメラ
        cap = camerathreading.CameraThreading(0)

        if not cap.isOpened():
            raise RuntimeError

        cap.start()
        mtcnn = mtcnnthreading.MTCNNThreading(cap)
        mtcnn.start()

        while True:
            # エラー処理
            if not cap.isOpened():
                raise RuntimeError

            # カメラフレーム
            _, frame = cap.read()

            # 顔認識結果表示
            print(mtcnn.read())

            # qキーで終了
            if cv2.waitKey(1) & 0xff == ord('q'):
                break

        # 解放処理
        mtcnn.release()
        cap.release()
        cv2.destroyAllWindows()

    except RuntimeError:
        print("カメラが見つかりません。カメラの接続を確認してください。")
        sys.exit()
    except KeyboardInterrupt:
        sys.exit()

if __name__ == '__main__':
	main()