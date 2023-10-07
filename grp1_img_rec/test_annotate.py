import unittest
import cv2
import pandas as pd
from img_rec_utils import annotate, are_images_equal, NO_ID_STRING


class TestAnnotate(unittest.TestCase):

    def test_annotate_no_object(self):
        image_id_0 = cv2.imread("in/image0.jpg")
        column_names = ['xmin', 'ymin', 'xmax', 'ymax', 'confidence', 'class', 'name']
        empty_df = pd.DataFrame(columns=column_names)
        with self.assertRaises(Exception):
            annotate(image_id_0, empty_df)

    def test_annotate_one_class(self):
        image_id_14 = cv2.imread("in/imageA.jpg")
        unannotated = cv2.imread("in/imageA.jpg")
        data = {
            'xmin': [211.083191],
            'ymin': [186.034378],
            'xmax': [264.049561],
            'ymax': [252.485977],
            'confidence': [0.959041],
            'class': [3],
            'name': ["14"]
        }
        df = pd.DataFrame(data)
        result, object_id = annotate(image_id_14, df)
        cv2.imwrite("out/imageA-annotated.jpg", result)
        self.assertFalse(are_images_equal(unannotated, result))
        self.assertEqual(object_id, str(14))


if __name__ == '__main__':
    unittest.main()
