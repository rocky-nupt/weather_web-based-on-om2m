#!/usr/bin/env python
# -*- coding:utf-8 -*-
'''
向web前端传输的数据模型
'''


class Data2web():
    def __init__(self, province):
        self.province = province
        self.data = {'province': self.province}
        self.data.update({'details': {}})
        self.display = 'display'
        self.hourly = 'hourly'

    # 添加城市名
    def add_city(self, cityname):
        self.city = {'name': cityname}
        self.data['details'].update(self.city)
        self.weather = []

    # 添加天气状态
    def add_state(self, state):
        self.state = {'state': {self.display: state.pop(0), self.hourly: state}}

    # 添加星期几
    def add_day(self, day):
        self.day = {'day': day}
        self.state.update(self.day)

    # 添加日期
    def add_date(self, date):
        self.date = {'date': date}
        self.state.update(self.date)

    # 添加气温
    def add_air(self, air):
        self.air = {'air': {self.display: air.pop(0), self.hourly: air}}
        self.state.update(self.air)

    # 添加风速
    def add_windspeed(self, windspeed):
        self.windspeed = {'windspeed': {self.display: windspeed.pop(0), self.hourly: windspeed}}
        self.state.update(self.windspeed)

    # 添加风向
    def add_winddirection(self, winddirection):
        self.winddirection = {'winddirection': {self.display: winddirection.pop(0), self.hourly: winddirection}}
        self.state.update(self.winddirection)
        self.weather.append(self.state)

    # 回掉函数
    def __call__(self, *args, **kwargs):
        self.data['details'].update({'weather': self.weather})
        return self.data
