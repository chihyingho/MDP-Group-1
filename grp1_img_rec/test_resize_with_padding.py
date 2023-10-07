import unittest
import cv2
from img_rec_utils import resize_with_padding, are_images_equal


class TestResizeWithPadding(unittest.TestCase):

    def setUp(self):
        self.example_image = cv2.imread("in/imageA.jpg")  # Replace with your image path
        self.original_shape = self.example_image.shape

    def tearDown(self):
        # Clean up any resources after each test if necessary.
        pass

    def test_half_image_dimensions(self):
        target_size = (self.original_shape[0] // 2, self.original_shape[1] // 2, self.original_shape[2])
        resized_img = resize_with_padding(self.example_image, target_size)
        self.assertEqual(resized_img.shape, target_size)

    def test_resize_for_yolov5s(self):
        target_size = (416, 416, self.original_shape[2])
        resized_img = resize_with_padding(self.example_image, target_size)
        # cv2.imwrite('resized_yolov5s.jpg', resized_img)
        self.assertEqual(resized_img.shape, target_size)

    def test_resize_to_same_dimensions(self):
        resized_img = resize_with_padding(self.example_image, self.original_shape)
        self.assertEqual(resized_img.shape, self.original_shape)
        self.assertTrue(are_images_equal(resized_img, self.example_image))


if __name__ == '__main__':
    unittest.main()

