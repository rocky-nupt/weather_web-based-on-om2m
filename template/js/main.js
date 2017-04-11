/**
 * main.js
 * http://www.codrops.com
 *
 * Licensed under the MIT license.
 * http://www.opensource.org/licenses/mit-license.php
 *
 * Copyright 2016, Codrops
 * http://www.codrops.com
 */

;(function (window) {

    'use strict';

    // Helper vars and functions.
    // 复制属性
    function extend(a, b) {
        for (var key in b) {
            if (b.hasOwnProperty(key)) {
                a[key] = b[key];
            }
        }
        return a;
    }

    // 创建元素节点
    function createDOMEl(type, className, content) {
        var el = document.createElement(type);
        el.className = className || '';
        el.innerHTML = content || '';
        return el;
    }

    // 创建自带命名空间的元素节点
    function createDOMElNS(type, className, content) {
        var el = document.createElementNS('http://www.w3.org/2000/svg', type);
        el.setAttribute('class', className || '');
        el.innerHTML = content || '';
        return el;
    }

    // The SVG path element that represents the sea/wave.
    var wavePath, days = [], provinceCtrl, citiesCtrl, currentCity = 0,
        mainContainer = document.querySelector('main'),
        graphContainer = mainContainer.querySelector('.content--graph'),
        // The SVG/graph element.
        graph = graphContainer.querySelector('.graph'),
        // SVG viewbox values arr.
        viewbox = graph.getAttribute('viewBox').split(/\s+|,/),
        // Viewport size.
        winsize = {width: window.innerWidth, height: window.innerHeight},
        oscilation = .4,
        timeIntervals = 6,
        theme = 1, data, daysToShow, slice, subSliceWidth, currentProvince, currentCity,
        provinces_han = {
            '北京': ['朝阳', '海淀', '通州'],
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
        },
        /**
         * Weather data:
         * . day: the day of the week
         * . swellheight: let´s assume the swell goes from 0m - 30m.
         *        The swell array represents the intervals of time (00h00m - 04h00m, 04h00m - 08h00m, ..., 20h00m - 24h00m).
         *        In this case we assume 6 intervals of time (timeIntervals = 6).
         *        The swell interval represents the average value for the day (or whatever value we want to display when not seeing each individual swell value per time interval)
         * . swellperiod: same logic but showcasing the swell's period in seconds.
         * . water: same logic but showcasing the water's temperature.
         * . state (weather state / weather icon):
         *    1-sunny
         *        2-partly cloudy
         *        3-cloudy
         *        4-rain
         *        5-thunderstorm
         *        6-clearnight
         *        7-partlycloudynight
         *    . air: same logic but showcasing the air's temperature.
         *    . windspeed: same logic but showcasing the wind´s speed.
         *    . winddirection: same logic but showcasing the wind´s direction.
         */

        provinceCtrlContainer = mainContainer.querySelector('.province-select'),
        citiesCtrlContainer = mainContainer.querySelector('.city-select');


    // 根据数据生成页面
    function DayForecast(weather, options) {
        this.weather = weather;
        this.options = extend({}, this.options);
        extend(this.options, options);

        this.showhourly = true;

        this._build();
        this.setData();
    }

    DayForecast.prototype.options = {
        units: {
            temperature: '°C',
            speed: 'km/h',
            length: 'm'
        }
    };

    DayForecast.prototype._build = function () {
        this.DOM = {};

        // 页面内容生成函数
        // Contents:
        this.DOM.state = createDOMEl('div', 'wstate-wrap', '<svg class="wstate wstate--sunny"><use xlink:href="#state-sunny"></use></svg><svg class="wstate wstate--cloudy"><use xlink:href="#state-cloudy"></use></svg><svg class="wstate wstate--partlycloudy"><use xlink:href="#state-partlycloudy"></use></svg><svg class="wstate wstate--rain"><use xlink:href="#state-rain"></use></svg><svg class="wstate wstate--thunders"><use xlink:href="#state-thunders"></use></svg><svg class="wstate wstate--clearnight"><use xlink:href="#state-clearnight"></use></svg><svg class="wstate wstate--partlycloudynight"><use xlink:href="#state-partlycloudynight"></use></svg>');

        this.DOM.timeperiodWrapper = createDOMEl('span', 'slice__data slice__data--period slice__data--hidden');
        this.DOM.timeperiodSVG = createDOMElNS('svg', 'icon icon--clock', '<use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#icon-clock"></use>');
        this.DOM.timeperiod = createDOMEl('span', 'slice__data slice__data--time');
        this.DOM.timeperiodWrapper.appendChild(this.DOM.timeperiodSVG);
        this.DOM.timeperiodWrapper.appendChild(this.DOM.timeperiod);

        this.DOM.dayWrapper = createDOMEl('span', 'slice__data slice__data--dateday');
        this.DOM.day = createDOMEl('span', 'slice__data slice__data--day');
        this.DOM.date = createDOMEl('span', 'slice__data slice__data--date');
        this.DOM.dayWrapper.appendChild(this.DOM.day);
        this.DOM.dayWrapper.appendChild(this.DOM.date);

        this.DOM.airWrapper = createDOMEl('span', 'slice__data slice__data--air')
        this.DOM.airSVG = createDOMElNS('svg', 'icon icon--thermometer', '<use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#icon-thermometer"></use>');
        this.DOM.air = createDOMEl('span', 'slice__data slice__data--temperature');
        this.DOM.airWrapper.appendChild(this.DOM.airSVG);
        this.DOM.airWrapper.appendChild(this.DOM.air);

        this.DOM.wind = createDOMEl('span', 'slice__data slice__data--wind');
        this.DOM.windspeed = createDOMEl('span', 'slice__data slice__data--wind-speed');
        this.DOM.winddirectionWrapper = createDOMEl('span', 'slice__data slice__data--wind-direction');
        this.DOM.winddirection = createDOMElNS('svg', 'icon icon--direction', '<use xlink:href="#icon-direction"></use>');
        this.DOM.winddirectionWrapper.appendChild(this.DOM.winddirection);
        this.DOM.wind.appendChild(this.DOM.winddirection);
        this.DOM.wind.appendChild(this.DOM.windspeed);

        this.DOM.slice = createDOMEl('div');
        this.DOM.slice.appendChild(this.DOM.state);
        this.DOM.slice.appendChild(this.DOM.timeperiodWrapper);
        this.DOM.slice.appendChild(this.DOM.dayWrapper);
        this.DOM.slice.appendChild(this.DOM.airWrapper);
        this.DOM.slice.appendChild(this.DOM.wind);

        this._buildHourlyLayout();
    };

    // 生成时间切片
    DayForecast.prototype._buildHourlyLayout = function () {
        this.DOM.subslicesWrapper = createDOMEl('div', 'slice__hover');
        var subslicesHTML = '';
        for (var i = 0; i <= timeIntervals - 1; ++i) {
            subslicesHTML += '<div></div>';
        }
        this.DOM.subslicesWrapper.innerHTML = subslicesHTML;
        this.DOM.slice.appendChild(this.DOM.subslicesWrapper);

        var self = this;
        this.mouseleaveFn = function (ev) {
            if (!self.showhourly) return false;
            self.DOM.timeperiodWrapper.classList.add('slice__data--hidden');
            self.setData();
        };
        this.DOM.slice.addEventListener('mouseleave', self.mouseleaveFn);

        this.mouseenterFn = function (ev) {
            if (!self.showhourly) return false;
            self._showTimePeriod(self.DOM.subslices.indexOf(ev.target));
        };
        this.DOM.subslices = [].slice.call(this.DOM.subslicesWrapper.querySelectorAll('div'));
        this.DOM.subslices.forEach(function (subslice) {
            subslice.addEventListener('mouseenter', self.mouseenterFn);
        });
    };

    DayForecast.prototype.setData = function (weather) {
        if (weather) {
            this.weather = weather;
        }
        for (var w in this.weather) {
            // alert(w)
            this[w] = this.weather[w];
        }

        this._setState();
        this._setTimePeriod();
        this._setDay();
        this._setDate();
        this._setAir();
        this._setWindSpeed();
        this._setWindDirection();
    };

    DayForecast.prototype._setState = function (val, timeperiod) {
        var val = val !== undefined ? val : this.state.display;
        if (timeperiod !== undefined) {
            this.DOM.slice.className = 'slice ' + this._getStateClassname(val) + ' ' + this._getPeriodClassname(timeperiod);
        }
        else {
            this.DOM.slice.className = 'slice ' + this._getStateClassname(val);
        }
    };

    DayForecast.prototype._setTimePeriod = function (val) {
        var val = val !== undefined ? val : '&nbsp;';
        this.DOM.timeperiod.innerHTML = val;
    };

    DayForecast.prototype._setDay = function (val) {
        var val = val !== undefined ? val : this.day;
        this.DOM.day.innerHTML = val;
    };

    DayForecast.prototype._setDate = function (val) {
        var val = val !== undefined ? val : this.date;
        this.DOM.date.innerHTML = val;
    };

    DayForecast.prototype._setAir = function (val) {
        var val = val !== undefined ? val : this.air.display;
        this.DOM.air.innerHTML = val + ' ' + this.options.units.temperature;
    };

    DayForecast.prototype._setWindSpeed = function (val) {
        var val = val !== undefined ? val : this.windspeed.display;
        this.DOM.windspeed.innerHTML = val + ' ' + this.options.units.speed;
    };

    DayForecast.prototype._setWindDirection = function (val, windspeed) {
        var val = val !== undefined ? val : this.winddirection.display;
        this.DOM.winddirection.style.WebkitTransform = this.DOM.winddirection.style.transform = 'rotate(' + val + 'deg)';

        anime.remove(this.DOM.winddirection);
        var windspeed = windspeed !== undefined ? windspeed : this.windspeed.display;
        anime({
            targets: this.DOM.winddirection,
            rotate: val + 40 / 100 * windspeed,
            duration: 200 - windspeed,
            loop: true,
            direction: 'alternate',
            easing: 'easeInOutQuad'
        });
    };


    DayForecast.prototype._showTimePeriod = function (period) {
        this._setState(this.state.hourly[period], period);
        this.DOM.timeperiodWrapper.classList.remove('slice__data--hidden');
        this._setTimePeriod(this._getTimePeriod(period));
        this._setAir(this.air.hourly[period]);
        this._setWindSpeed(this.windspeed.hourly[period]);
        this._setWindDirection(this.winddirection.hourly[period], this.windspeed.hourly[period]);
    };

    DayForecast.prototype._getTimePeriod = function (period) {
        var interval = 24 / timeIntervals;
        return period * interval + ':00 - ' + (period + 1) * interval + ':00';
    };

    DayForecast.prototype._getStateClassname = function (state) {
        var c = 'slice--state-';
        switch (state) {
            case 1 :
                c += 'sunny';
                break;
            case 2 :
                c += 'partlycloudy';
                break;
            case 3 :
                c += 'cloudy';
                break;
            case 4 :
                c += 'rain';
                break;
            case 5 :
                c += 'thunders';
                break;
            case 6 :
                c += 'clearnight';
                break;
            case 7 :
                c += 'partlycloudynight';
                break;
        }
        ;
        return c;
    };

    DayForecast.prototype._getPeriodClassname = function (timeperiod) {
        return 'slice--period-' + (timeperiod + 1); // todo: this depends on the [timeIntervals]
    };

    DayForecast.prototype.getEl = function () {
        return this.DOM.slice;
    };

    // 初始化
    function init() {
        getData()

    }

    function getData() {
        $.ajax({
            type: 'POST',
            url: '/index',
            async: true,
            dataType: 'json',
            success: function (ret) {
                data = ret.details
                currentProvince = ret.province
                currentCity = data.name
                daysToShow = data.weather.length,
                    // Width of one day.
                    slice = winsize.width / daysToShow,
                    // Width of each time interval.
                    subSliceWidth = slice / timeIntervals;
                layout();
                createGraph();
            }
        })
    }


    // 页面展示
    function layout() {
        // 省
        provinceCtrl = createDOMEl('select');
        provinceCtrl.innerHTML = '<select>'

        provinceCtrl.innerHTML += '<option value="' + currentProvince + '" ' + (i === 0 ? 'selected' : '') + '>' + currentProvince + '</option>'
        for (i in provinces_han) {
            if (i != currentProvince) {
                provinceCtrl.innerHTML += '<option value="' + i + '" ' + (i === 0 ? 'selected' : '') + '>' + i + '</option>'
            }
        }
        provinceCtrl.innerHTML += '</select>';
        provinceCtrlContainer.appendChild(provinceCtrl);
        provinceCtrl.addEventListener('change', changeProvince);


        citiesCtrl = createDOMEl('select');
        citiesCtrl.innerHTML = '<select>';

        citiesCtrl.innerHTML += '<option value="' + currentCity + '" ' + (i === 0 ? 'selected' : '') + '>' + currentCity + '</option>'
        for (i in provinces_han[currentProvince]) {
            if (provinces_han[currentProvince][i] != currentCity) {
                citiesCtrl.innerHTML += '<option value="' + provinces_han[currentProvince][i] + '" ' + (i === 0 ? 'selected' : '') + '>' + provinces_han[currentProvince][i] + '</option>'
            }
        }

        citiesCtrl.innerHTML += '</select>';
        citiesCtrlContainer.appendChild(citiesCtrl);
        citiesCtrl.addEventListener('change', changeCityFn);

        var slices = createDOMEl('div', 'slices');
        // Create a "slice" per day.
        for (var i = 0; i <= daysToShow - 1; ++i) {
            var dayWeather = data.weather[i];
            var day = new DayForecast(dayWeather);
            days.push(day);
            slices.appendChild(day.getEl());
        }

        graphContainer.insertBefore(slices, graph);
    }

    // 省选择
    function changeProvince(ev) {
        currentProvince = ev.target.value;
        citiesCtrl.innerHTML = null
        for (var i in provinces_han[currentProvince]) {
            citiesCtrl.innerHTML += '<option value="' + provinces_han[currentProvince][i] + '" ' + (i === 0 ? 'selected' : '') + '>' + provinces_han[currentProvince][i] + '</option>'

        }
        currentCity = provinces_han[currentProvince][0]
        change()
    }

    // 城市选择
    function changeCityFn(ev) {
        currentCity = ev.target.value;

        change()
    }

    function change() {

        $.ajax({
            type: 'POST',
            url: '/retrieve',
            async: false,
            data: {
                'province': currentProvince,
                'city': currentCity
            },
            dataType: 'json',
            success: function (response) {
                data = response.details;
            }
        })
        theme += 1;
        if (theme > 3) {
            theme %= 3
        }
        mainContainer.className = 'theme-' + theme;


        for (var i = 0; i <= daysToShow - 1; ++i) {
            days[i].setData(data.weather[i]);
        }


        var points = setPoints(),
            newpath = calculatePath(points);
        anime.remove(wavePath);
        anime({
            targets: wavePath,
            d: newpath,
            duration: 1000,
            easing: 'easeInOutQuad',
            complete: function () {
                animateWave();
            }
        });
    }

    // 创建图表
    function createGraph() {
        // Create the "wave" path.
        wavePath = createDOMElNS('path', 'graph__path');
        graph.appendChild(wavePath);
        // The path points needed to draw the curve. We will draw [daysToShow] points/days + 2 points to fill the path + 2 extra points to smooth the extremities.
        var points = setPoints();
        anime.remove(wavePath);
        wavePath.setAttribute('d', calculatePath(points));
        setTimeout(animateWave, 500);
    }

    // 描点
    function setPoints(shift) {
        var weather = data.weather;
        // the swell extremities (y-axis):
        var extremities = {
                left: weather[0].air.hourly[0],
                right: weather[daysToShow - 1].air.hourly[timeIntervals - 1]
            },
            // the points array
            points = [];

        for (var i = 0; i <= daysToShow + 3; ++i) {
            var x, y;

            if (i === 0 || i === daysToShow + 3) {
                x = i === 0 ? -1 * subSliceWidth * 2 : winsize.width + subSliceWidth * 2;
                y = 0;
                points.push({x: x, y: y / 2.5});
            }
            else if (i === 1 || i === daysToShow + 2) {
                x = i === 1 ? -1 * subSliceWidth : winsize.width + subSliceWidth;
                y = i === 1 ? extremities.left / 2 : extremities.right / 2;
                if (shift) {
                    y = i % 2 === 0 ? y - oscilation : y + oscilation;
                }
                points.push({x: x, y: y / 2.5});
            }
            else {
                var daySwell = weather[i - 2].air.hourly;
                for (var j = 0, len = daySwell.length; j <= len - 1; ++j) {
                    x = slice * (i - 2) + subSliceWidth * j + subSliceWidth / 2;
                    y = daySwell[j];

                    if (shift) {
                        y = j % 2 === 0 ? y - oscilation : y + oscilation;
                    }

                    points.push({x: x, y: y / 2.5});
                }
            }
        }
        ;

        return points;
    }

    function animateWave() {
        var shiftPoints = setPoints(true),
            shiftpath = calculatePath(shiftPoints);

        anime({
            targets: wavePath,
            d: shiftpath,
            duration: 2000,
            loop: true,
            direction: 'alternate',
            easing: 'easeInOutQuad'
        });
    }

    function calculatePath(points) {
        var d = '', d2 = '';
        for (var i = 0, len = points.length; i < len; ++i) {
            var p = points[i];

            var mapping = {
                x: viewbox[2] / winsize.width * p.x,
                y: (-1 * Number(viewbox[3]) / 2) / 15 * p.y + Number(viewbox[3])
            };

            if (0 == i) {
                d = "M" + mapping.x + "," + mapping.y;
            } else if (1 == i) {
                d += " R" + mapping.x + "," + mapping.y;
            } else {
                d += " " + mapping.x + "," + mapping.y;
            }

            d2 = parsePath(d);
        }
        return d2;
    }

    init();

})(window);