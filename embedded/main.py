import threading
import numpy as np
import cv2
import camerathreading
import mtcnnthreading

#cv2.setNumThreads(0)

def main():
    cap = camerathreading.CameraThreading(0)

    if not cap.isOpened():
        raise RuntimeError

    cap.start()

    mtcnn = mtcnnthreading.MTCNNThreading(cap)
    mtcnn.start()

    while True:
        _, frame = cap.read()

        if cv2.waitKey(1) & 0xff == ord('q'):
            break

    mtcnn.release()
    cap.release()
    cv2.destroyAllWindows()

if __name__ == '__main__':
	main()