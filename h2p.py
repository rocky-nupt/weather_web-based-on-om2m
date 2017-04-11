#!/usr/bin/env python
# -*- coding:utf-8 -*-


provinces_han = {'北京': ['朝阳', '海淀', '通州'],
                 '天津': ['和平', '河东', '河西'],
                 '河北': ['石家庄', '唐山', '秦皇岛'],
                 '山西': ['太原', '大同', '阳泉'],
                 '内蒙古': ['呼和浩特', '包头', '乌海'],
                 '辽宁': ['沈阳', '大连', '鞍山'],
                 '吉林': ['长春', '吉林', '四平'],
                 '黑龙江': ['哈尔滨', '齐齐哈尔', '鸡西'],
                 '上海': ['黄浦', '徐汇', '静安'],
                 '江苏': ['南京', '无锡', '徐州'],
                 '浙江': ['杭州', '宁波', '温州'],
                 '安徽': ['合肥', '芜湖', '蚌埠'],
                 '福建': ['福州', '厦门', '漳州'],
                 '江西': ['南昌', '九江', '上饶'],
                 '山东': ['济南', '青岛', '淄博'],
                 '河南': ['郑州', '开封', '洛阳'],
                 '湖北': ['武汉', '黄石', '十堰'],
                 '湖南': ['长沙', '株洲', '湘潭'],
                 '广东': ['广州', '深圳', '珠海'],
                 '广西': ['南宁', '柳州', '桂林'],
                 '海南': ['海口', '三亚', '三沙'],
                 '重庆': ['渝中', '大渡口', '江北'],
                 '四川': ['成都', '绵阳', '攀枝花'],
                 '贵州': ['贵阳', '遵义', '六盘水'],
                 '云南': ['昆明', '丽江', '曲靖'],
                 '西藏': ['拉萨', '昌都', '日喀则'],
                 '陕西': ['西安', '宝鸡', '咸阳'],
                 '甘肃': ['兰州', '天水', '酒泉'],
                 '青海': ['西宁', '海东', '海北'],
                 '宁夏': ['银川', '石嘴山', '吴忠'],
                 '新疆': ['乌鲁木齐', '克拉玛依', '吐鲁番'],
                 '台湾': ['台北', '台南', '高雄'],
                 '香港': ['香港'],
                 '澳门': ['澳门']
                 }
provinces_pin = {'beijing': ['chaoyang', 'haidian', 'tongzhou'],
                 'tianjin': ['heping', 'hedong', 'hexi'],
                 'hebei': ['shijiazhuang', 'tangshan', 'qinghuangdao'],
                 'shan1xi': ['taiyuan', 'datong', 'yangquan'],
                 'neimenggu': ['huhehaote', 'baotou', 'wuhai'],
                 'liaoning': ['shenyang', 'dalian', 'anshan'],
                 'jilin': ['changchun', 'jilin', 'siping'],
                 'heilongjiang': ['haerbin', 'qiqihaer', 'jixi'],
                 'shanghai': ['huangpu', 'xuhui', 'jingan'],
                 'jiangsu': ['nanjing', 'wuxi', 'xuzhou'],
                 'zhejiang': ['hangzhou', 'ningbo', 'wenzhou'],
                 'anhui': ['hefei', 'wuhu', 'bengbu'],
                 'fujian': ['fuzhou', 'xiamen', 'zhangzhou'],
                 'jiangxi': ['nanchang', 'jiujiang', 'shangrao'],
                 'shandong': ['jinan', 'qingdao', 'zibo'],
                 'henan': ['zhengzhou', 'kaifeng', 'luoyang'],
                 'hubei': ['wuhan', 'huangshi', 'shiyan'],
                 'hunan': ['changsha', 'zhuzhou', 'xiangtan'],
                 'guangdong': ['guangzhou', 'shenzhen', 'zhuhai'],
                 'guangxi': ['nanning', 'liuzhou', 'guilin'],
                 'hainan': ['haikou', 'sanya', 'sansha'],
                 'chongqing': ['yuzhong', 'dadukou', 'jiangbei'],
                 'sichuang': ['chengdu', 'mianyang', 'panzhihua'],
                 'guizhou': ['guiyang', 'zunyi', 'liupanshui'],
                 'yunnan': ['kunming', 'lijiang', 'qujing'],
                 'xizang': ['lasa', 'changdu', 'rikaze'],
                 'shan3xi': ['xian', 'baoji', 'xianyang'],
                 'gansu': ['lanzhou', 'tianshui', 'jiuquan'],
                 'qinghai': ['xining', 'haidong', 'haibei'],
                 'ningxia': ['yinchuan', 'shizuishan', 'wuzhong'],
                 'xinjiang': ['wulumuqi', 'kelamayi', 'tulufan'],
                 'taiwai': ['taibei', 'tainan', 'gaoxiong'],
                 'xianggang': ['xianggang'],
                 'aomen': ['aomen']
                 }

import json

# with open('h2p_province.json', 'w') as f:
#     temp = {}
#     for i,j in zip(provinces_han, provinces_pin):
#         for m in range(len(provinces_han)):
#             hanzi = list(provinces_han[m].keys())[0]
#             pinying = list(provinces_pin[m].keys())[0]
#             temp[hanzi] = pinying
#     json.dump(temp, f)
# with open('h2p_city.json') as f:
#     h2p_province = json.loads(f.read())
#     print(h2p_province)
# with open('h2p_city.json', 'w') as f:
#     temp = {}
#     for i in h2p_province:
#         temp[h2p_province[i]] = {}
#         province = temp[h2p_province[i]]
#         for m,n in zip(provinces_han[i], provinces_pin[h2p_province[i]]):
#             province[m] = n
#     json.dump(temp, f)
