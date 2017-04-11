#!/usr/bin/env python
# -*- coding:utf-8 -*-

'''
数据上传至om2m平台
'''

from datafromapi import data1, data2
from data2iot import *

data_country = []
data_country.append(data1)
data_country.append(data2)
for i in data_country:
    temp = Data2iot(i)
    data_upload = Create_country(temp())
    data_upload.create_province()
