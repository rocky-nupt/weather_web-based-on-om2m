#!/usr/bin/env python
# -*- coding:utf-8 -*-
'''
定位函数
'''
import requests


def locate():
    locate_url = 'http://api.map.baidu.com/location/ip?ak=oAdQ6guTt4jDamTC5lin86PZmmkIE08W'
    result = requests.get(url=locate_url).json()
    temp_elements = result['address'].split('|')
    myprovince, mycity = temp_elements[1], temp_elements[2]
    return myprovince, mycity
