# weather-web-om2m

本项目仅供项目申请时展示所用，由于开发周期较短，没有详细的第三方数据来源，所以数据全是做的假数据，而且没有使用数据库存储数据，只是以json文件的形式进行了数据存储和读取。

## 项目简介：
运用tornado框架搭建了基于om2m物联网平台的天气预报展示网站，om2m是eclipse的一个开源的iot云平台项目，但它也是一个物联网协议，二者同名，请读者注意，下文的om2m全部指的是eclipse的iot平台。
###
网站展示的内容主要有最近五天日期，气温，风速，风向和根据气温画出的预测曲线，每天分6个时间段，每个时间段4个小时.该网站可以覆盖全国所有省份，但为了方便起见每个省份只包含了三个城市，首次打开网页时可以根据ip定位客户端所在城市并展示该城市的天气情况。后台主要是与eclipse的om2m物联网平台进行交互，分为两个模块，一个是负责从第三方数据源获取数据并按照一定格式上传到om2m平台，并有其进行管理；另一个就是从om2m平台提取所需的数据并进行处理然后传给前端呈现。
###
网站前端运用了javascript、html、css等前端语言，后台主要是用python和java所写（om2m平台全部由java构成）。

## 环境要求：
JAVA 1.7 or later
####
python3.5
####
Apache Maven 3 or later
####
python packages: tornado, requests

## 示例
<image src=https://github.com/rocky-nupt/weather_web-based-on-om2m/raw/master/pic/om2m1.png />


<image src=https://github.com/rocky-nupt/weather_web-based-on-om2m/raw/master/pic/om2m2.png />


<image src=https://github.com/rocky-nupt/weather_web-based-on-om2m/raw/master/pic/om2m3.png width='480' height='960' align=center />
