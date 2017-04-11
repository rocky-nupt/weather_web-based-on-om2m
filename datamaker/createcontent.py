#!/usr/bin/env python
# -*- coding:utf-8 -*-
'''
上传至om2m平台的数据生成模块（xml格式）
'''

import requests


class Create_content():
    def __init__(self, province, city):
        self.url = 'http://127.0.0.1:8080/~/in-cse/in-name/' + province + '/' + city
        self.headers = {
            'X-M2M-Origin': 'admin:admin',
            'Content-Type': 'application/xml;ty=4'
        }
        self.body = '<m2m:cin xmlns:m2m="http://www.onem2m.org/xml/protocols">\n\
    <cnf>message</cnf>\n\
    <con>\n\
      &lt;obj>\n'

    def addxml_date(self, date):
        self.body += '        &lt;int name="date" val="' + date + '&quot;/&gt;\n'

    def addxml_state(self, state):
        for i in range(len(state)):
            self.body += '        &lt;int name="state' + str(i) + '" val="' + str(state[i]) + '&quot;/&gt;\n'

    def addxml_air(self, air):
        for i in range(len(air)):
            self.body += '        &lt;int name="air' + str(i) + '" val="' + str(air[i]) + '&quot;/&gt;\n'

    def addxml_windespeed(self, windspeed):
        for i in range(len(windspeed)):
            self.body += '        &lt;int name="windspeed' + str(i) + '" val="' + str(windspeed[i]) + '&quot;/&gt;\n'

    def addxml_winddirection(self, winddirection):
        for i in range(len(winddirection)):
            self.body += '        &lt;int name="winddirection' + str(i) + '" val="' + str(
                winddirection[i]) + '&quot;/&gt;\n'

    def create_content(self):
        self.body += '      &lt;/obj&gt;\n    </con>\n</m2m:cin>'
        r = requests.post(url=self.url, data=self.body, headers=self.headers)
        # print(self.url)
        # print(self.body)


class Create_city():
    def __init__(self, data_city, province):
        self.data_city = data_city
        self.province = province
        self.city = list(self.data_city.keys())[0]
        self.days = self.data_city[self.city]

    def create_days(self):
        city_content = Create_content(self.province, self.city)
        for i in self.days:
            city_content.addxml_date(i['date'])
            city_content.addxml_air(i['air'])
            city_content.addxml_state(i['state'])
            city_content.addxml_windespeed(i['windspeed'])
            city_content.addxml_winddirection(i['winddirection'])
        city_content.create_content()


class Create_province():
    def __init__(self, data_province, province):
        self.data_province = data_province
        self.province = province

    def create_citys(self):
        for i in self.data_province:
            self.city = Create_city(i, self.province)
            self.city.create_days()


class Create_country():
    def __init__(self, data2iot):
        self.data2iot = data2iot

    def create_province(self):
        for i in self.data2iot:
            self.province = Create_province(self.data2iot[i], i)
            self.province.create_citys()
