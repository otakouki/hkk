import cv2

class Camera:
    
    def __init__(self, index):
        self.cap = cv2.VideoCapture(index)

    def isOpened(self):
        return self.cap.isOpened()
    
    def read(self):
        return self.cap.read()

    def release(self):
        return self.cap.release()