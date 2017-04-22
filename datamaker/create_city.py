#!/usr/bin/env python
# -*- coding:utf-8 -*-
import requests
import json

provinces = {'beijing':['chaoyang','haidian','tongzhou'],
             'tianjin':['heping', 'hedong', 'hexi'],
            'hebei':['shijiazhuang','tangshan','qinghuangdao'],
            'shan1xi':['taiyuan','datong','yangquan'],
            'neimenggu':['huhehaote','baotou','wuhai'],
            'liaoning':['shenyang','dalian','anshan'],
            'jilin':['changchun','jilin','siping'],
            'heilongjiang':['haerbin','qiqihaer','jixi'],
            'shanghai':['huangpu','xuhui','jingan'],
            'jiangsu':['nanjing','wuxi','xuzhou'],
            'zhejiang':['hangzhou','ningbo','wenzhou'],
            'anhui':['hefei','wuhu','bengbu'],
            'fujian':['fuzhou','xiamen','zhangzhou'],
            'jiangxi':['nanchang','jiujiang','shangrao'],
            'shandong':['jinan','qingdao','zibo'],
            'henan':['zhengzhou','kaifeng','luoyang'],
            'hubei':['wuhan','huangshi','shiyan'],
            'hunan':['changsha','zhuzhou','xiangtan'],
            'guangdong':['guangzhou','shenzhen','zhuhai'],
            'guangxi':['nanning','liuzhou','guilin'],
            'hainan':['haikou','sanya','sansha'],
            'chongqing':['yuzhong','dadukou','jiangbei'],
            'sichuang':['chengdu','mianyang','panzhihua'],
            'guizhou':['guiyang','zunyi','liupanshui'],
            'yunnan':['kunming','lijiang','qujing'],
            'xizang':['lasa','changdu','rikaze'],
            'shan3xi':['xian','baoji','xianyang'],
            'gansu':['lanzhou','tianshui','jiuquan'],
            'qinghai':['xining','haidong','haibei'],
            'ningxia':['yinchuan','shizuishan','wuzhong'],
            'xinjiang':['wulumuqi','kelamayi','tulufan'],
            'taiwai':['taibei','tainan','gaoxiong'],
            'xianggang':['xianggang'],
            'aomen':['aomen']
}

class Create_mysensor():
    def __init__(self, url, body):
        self.headers = {
            'X-M2M-Origin': 'admin:admin',
            'Content-Type': 'application/json;ty=3'
        }
        self.url = url
        self.body = body

    def create(self):
        r = requests.post(url=self.url, data=self.body, headers=self.headers)
#        r = requests.delete(url=self.url, data=self.body, headers=self.headers)
        print(r.status_code)
        print(r.headers)
        print(r.text)

in_cse = 'http://127.0.0.1:8080/~/in-cse/in-name/'
f = open('city.json')
body = json.loads(f.read())
f.close()
shan = 'shan3xi'
url = in_cse + shan
#for city in provinces[shan]:
#    body['m2m:cnt']['rn'] = city
#    mysensor = Create_mysensor(url, json.dumps(body))
#    mysensor.create()  
for key in provinces:
    url = in_cse + key
    for city in provinces[key]:
        body['m2m:cnt']['rn'] = city
        mysensor = Create_mysensor(url, json.dumps(body))
        mysensor.create()
