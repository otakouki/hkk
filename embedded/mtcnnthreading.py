import threading
import queue
from mtcnn import MTCNN
import cv2

class MTCNNThreading:

    def __init__(self, cap):
        self.cap = cap
        self.q = queue.Queue(maxsize=4096)
        self.canseled = False
        self.detector = MTCNN()

    def start(self):
        thread = threading.Thread(target=self.run, daemon=True)
        thread.start()

    def stop(self):
        self.canseled = True

    def release(self):
        self.canseled = True

    def read(self):
        return self.q.get()

    def run(self):
        while not self.canseled:
            if not self.q.full():
                _, frame = self.cap.read()
                faces = self.detector.detect_faces(cv2.cvtColor(frame, cv2.COLOR_BGR2RGB))
                self.q.put(faces)
                print(faces)