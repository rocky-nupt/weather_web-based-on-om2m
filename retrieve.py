#!/usr/bin/env python
# -*- coding:utf-8 -*-
'''
数据获取模块，从om2m平台上获取数据并处理
'''
import requests
import datetime


def getdata_fromiot(province_pin, city_pin):
    '''
    通过get请求获取数据
    :param province_pin: 目标省份
    :param city_pin: 目标城市
    :return: 数据内容，json形式
    '''
    url = 'http://127.0.0.1:8080/~/in-cse/in-name/' + province_pin + '/' + city_pin + '/la'
    headers = {
        'X-M2M-Origin': 'admin:admin',
        'Accept': 'application/json'
    }
    response = requests.get(url=url, headers=headers)
    return response.json()


def data_set(response):
    '''
    数据处理，提取出get请求回复中的有用信息
    :param response:get请求回复
    :return:数据包（zip打包）
    '''
    con = response['m2m:cin']['con']
    date_sp = con.split('date" val="')
    date_sp.pop(0)
    dates = []
    states = []
    airs = []
    windspeeds = []
    winddirections = []
    alldata = []
    day_en = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday']

    for i in date_sp:
        dates_temp = i[:10].split('-')
        dates_str = dates_temp[1] + '/' + dates_temp[2]
        dates.append(dates_str)
        temp = i.split('val="')
        temp.pop(0)
        for j in temp:
            alldata.append(int(j.split('"')[0]))
        airs.append(alldata[:7])
        states.append(alldata[7:14])
        windspeeds.append(alldata[14:21])
        winddirections.append(alldata[21:])
        alldata = []

    d = datetime.datetime.now().weekday()
    if 6 - d < 4:
        days = day_en[d:]
        days.extend(day_en[:d - 2])
    else:
        days = day_en[d:d + 5]
    return zip(states, days, dates, airs, windspeeds, winddirections)
