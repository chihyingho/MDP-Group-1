from unittest import TestCase

import pandas as pd
from pandas.testing import assert_frame_equal

from img_rec_utils import select_best_object


class TestSelectBestObject(TestCase):

    def test_select_top_class_over_threshold(self):
        df = pd.DataFrame({
            'xmin': [247.751923, 417.493927],
            'ymin': [253.096268, 291.238037],
            'xmax': [314.412170, 454.949219],
            'ymax': [352.040070, 127.892731],
            'confidence': [0.930767, 0.872015],
            'class': [14, 27],
            'name': ["25", "38"]
        })
        expected = pd.DataFrame({
            'xmin': [247.751923],
            'ymin': [253.096268],
            'xmax': [314.412170],
            'ymax': [352.040070],
            'confidence': [0.930767],
            'class': [14],
            'name': ["25"]
        })
        result = select_best_object(df, 0.9)
        result = result[['xmin', 'ymin', 'xmax', 'ymax', 'confidence', 'class', 'name']]
        assert_frame_equal(expected, result)

    def test_select_bigger_box_over_threshold(self):
        data = {
            'xmin': [247.751923, 417.493927],
            'ymin': [253.096268, 291.238037],
            'xmax': [257.412170, 454.949219],
            'ymax': [262.040070, 427.892731],
            'confidence': [0.930767, 0.912015],
            'class': [14, 27],
            'name': ["25", "38"]
        }
        df = pd.DataFrame(data)
        expected = pd.DataFrame({
            'xmin': [417.493927],
            'ymin': [291.238037],
            'xmax': [454.949219],
            'ymax': [427.892731],
            'confidence': [0.912015],
            'class': [27],
            'name': ["38"]
        })
        result = select_best_object(df, 0.9)
        result = result[['xmin', 'ymin', 'xmax', 'ymax', 'confidence', 'class', 'name']]
        assert_frame_equal(expected, result)

    def test_select_no_object(self):
        column_names = ['xmin', 'ymin', 'xmax', 'ymax', 'confidence', 'class', 'name']
        empty_df = pd.DataFrame(columns=column_names)
        with self.assertRaises(Exception):
            select_best_object(empty_df)
