#!/usr/bin/env python
# -*- coding:utf-8 -*-
import tornado.ioloop
import tornado.web
import json
from cache import MemClient
from location import locate
from retrieve import getdata_fromiot, data_set
from models2web import Data2web

# 汉音转换‘数据库’
with open('db/h2p_province.json') as f1, open('db/h2p_city.json') as f2:
    h2p_province = json.loads(f1.read())
    h2p_city = json.loads(f2.read())


# 获取首次访问网页自动定位的城市数据
def initial(myprovince_han, mycity_han):
    myprovince_pin = h2p_province[myprovince_han]
    mycity_pin = h2p_city[myprovince_pin][mycity_han]
    data = data2web(myprovince_pin, mycity_pin, myprovince_han, mycity_han)
    MC.set(myprovince_han + mycity_han, data)
    return data


# 生成向前端传输的数据
def data2web(province_pin, city_pin, province_han, city_han):
    response = getdata_fromiot(province_pin, city_pin)
    city_instance = Data2web(province_han)
    city_instance.add_city(city_han)
    for state, day, date, air, windspeed, winddirection in data_set(response):
        city_instance.add_state(state)
        city_instance.add_day(day)
        city_instance.add_date(date)
        city_instance.add_air(air)
        city_instance.add_windspeed(windspeed)
        city_instance.add_winddirection(winddirection)
    return city_instance()


# 定位、初始化
myprovince_han, mycity_han = locate()
data = initial(myprovince_han, mycity_han)
MC = MemClient()
MC.set(myprovince_han + mycity_han, data)


class MainHandler(tornado.web.RequestHandler):

    def get(self):
        self.render('index.html')

    def post(self, *args, **kwargs):
        print(data)
        self.write(data)


class ChangeHandler(tornado.web.RequestHandler):

    def post(self, *args, **kwargs):
        province = self.get_argument('province')
        city = self.get_argument('city')
        print(province, city)
        if province == '浙江' and city == '杭州':
            try:
                data_change = MC.get(province + city)
            except Exception:
                data_change = initial(province, city)
            print(data_change)
            self.write(data_change)


# web服务器配置
settings = {
    'template_path': 'template',
    'static_path': 'template',
}

# web应用路由
application = tornado.web.Application([
    (r"/index", MainHandler),
    (r'/retrieve', ChangeHandler),
], **settings)

if __name__ == "__main__":
    application.listen(7800)
    tornado.ioloop.IOLoop.instance().start()
