#!/usr/bin/env python
# -*- coding:utf-8 -*-
'''
生成上传数据
'''

import os
import json
from createcontent import *


# 获取省份和城市的拼音
def getpro_city_pin(data):
    path = os.getcwd()
    parent_path = os.path.dirname(path)
    city_path = parent_path + '/db/h2p_city.json'
    province_path = parent_path + '/db/h2p_province.json'
    province_han_path = parent_path + '/db/han_province.json'
    city_han = data['name']
    with open(city_path) as f1, open(province_path) as f2, open(province_han_path) as f3:
        pro_reference = json.loads(f1.read())
        city_reference = json.loads(f2.read())
        pro_han_reference = json.loads(f3.read())
    for i in pro_han_reference:
        if city_han in pro_han_reference[i]:
            pro_han = i
            break
    pro_pin = city_reference[pro_han]
    city_pin = pro_reference[pro_pin][city_han]
    return pro_pin, city_pin


# 数据生成
class Data2iot():
    def __init__(self, data):
        self.data = data
        self.province, self.city = getpro_city_pin(data)

    def __call__(self, *args, **kwargs):
        self.data2iot = {self.province: []}
        self.weather = []
        for i in self.data['weather']:
            self.weather.append(i)
        self.city = {self.city: self.weather}
        self.data2iot[self.province].append(self.city)
        return self.data2iot
