import threading
import queue
import camera

class CameraThreading(camera.Camera):

    def __init__(self, index):
        self.cap = camera.Camera(index)
        self.q = queue.Queue(maxsize=4096)
        self.canseled = False

    def start(self):
        thread = threading.Thread(target=self.run, daemon=True)
        thread.start()

    def stop(self):
        self.canseled = True

    def release(self):
        self.canseled = True
        self.cap.release()

    def read(self):
        return self.q.get()

    def run(self):
        while not self.canseled:
            if not self.q.full():
                _, frame = self.cap.read()
                self.q.put((_, frame))