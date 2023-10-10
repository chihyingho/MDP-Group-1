import unittest
import cv2
import torch
from pathlib import Path
from img_rec_utils import NoObjectDetectedException, detect_objects

PATH_MODEL_WEIGHTS = Path('../assets/best-pt/task1-minhtet-v1.pt')


class TestDetectObject(unittest.TestCase):

    def setUp(self) -> None:
        self.model = torch.hub.load('../yolov5', 'custom', path=PATH_MODEL_WEIGHTS, source='local')
        self.model.eval()

    def test_detect_id_14(self):
        image_id_14 = cv2.imread("in/imageA.jpg")
        labels_df = detect_objects(self.model, image_id_14)
        self.assertEqual(labels_df.iloc[0]['name'], "14")

    def test_detect_id_24(self):
        image_id_24 = cv2.imread("in/imageB.jpg")
        labels_df = detect_objects(self.model, image_id_24)
        self.assertEqual(labels_df.iloc[0]['name'], "24")

    def test_detect_id_39(self):
        image_id_39 = cv2.imread("in/imageC.jpg")
        labels_df = detect_objects(self.model, image_id_39)
        self.assertEqual(labels_df.iloc[0]['name'], "39")

    def test_detect_no_object(self):
        image_id_0 = cv2.imread("in/image0.jpg")
        with self.assertRaises(NoObjectDetectedException):
            detect_objects(self.model, image_id_0)


if __name__ == '__main__':
    unittest.main()
