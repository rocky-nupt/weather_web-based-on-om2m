/*******************************************************************************
 * Copyright (c) 2013-2016 LAAS-CNRS (www.laas.fr)
 * 7 Colonel Roche 31077 Toulouse - France
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Initial Contributors:
 *     Thierry Monteil : Project manager, technical co-manager
 *     Mahdi Ben Alaya : Technical co-manager
 *     Samir Medjiah : Technical co-manager
 *     Khalil Drira : Strategy expert
 *     Guillaume Garzone : Developer
 *     François Aïssaoui : Developer
 *
 * New contributors :
 *******************************************************************************/
/**
 * Authors:
 * Mahdi Ben Alaya <ben.alaya@laas.fr> <benalaya.mahdi@gmail.com>
 * Marouane El kiasse <melkiasse@laas.fr> <kiasmarouane@gmail.com>
 * Yassine Banouar <ybanouar@laas.fr> <yassine.banouar@gmail.com>
 */
var cseBase = getUrlVar("cseId");
var context = getUrlVar("context");
$(document).ready(function() {
    $("#main").hide();
    $("input#username").focus();
});

var username;
var password;
var parser = new DOMParser();

function make_base_auth(user, password) {
    var tok = user + ':' + password;
    return tok;
}

function login() {
    username = $("input#username").val();
    password = $("input#password").val();
    get("/" + cseBase);
}

function logout() {
    username = "";
    password = "";
    $("input#username").val("")
    $("input#password").val("")
    $("#attributes").html("");
    $('#content').html("");
    $('#response').html("");
    $("#login").show();
    $("#main").hide();
}

function get(targetId) {
    $.ajax({
        type: "GET",
        beforeSend: function() {},
        dataType: "xml",
        url: context + targetId + "?rcn=5",
        headers: {
            "X-M2M-Origin": make_base_auth(username, password),
            "Accept": "application/xml"
        },
        success: function(response) {
            $("#login").hide();
            $("#main").show();
            $("#url").text(window.location.protocol + "//" + window.location.host + context + targetId);
            $("#" + encodeId(targetId)).html("");
            $("#attributes").html("");
            $('#content').html('');
            $('#response').html('');
            $("#error").html('');

            // Get the main resource
            var resource = response.firstChild;
            // Get child elements
            var children = $(response.firstChild).children();

            // In the case of CSEBase, add the resource at the root element
            if (resource.localName == "cb") {
                $("#resources").html("<li onclick=get('" + targetId + "')>" + $(resource).attr('rn') + "<ul id=" + encodeId(targetId) + "></ul></li>");
            }

            // For each child element
            for (var i = 0; i < children.length; i++) {
                attribute = children[i];

                if (attribute.localName == "ch") {
                    // If it is a child resource (ch) add it to the resource tree
                    $("#" + encodeId(targetId)).append("<li onclick=get('" + attribute.textContent + "')>" + $(attribute).attr('rn') + "<ul id=" + encodeId(attribute.textContent) + "></ul></li>");
                } else {
                    // Handle other attributes
                    var value;
                    if (attribute.localName == "pv" || attribute.localName == "pvs") {
                        // Handle the case of ACP rules
                        var table = '<table class="bordered"><thead><th>AccessControlOriginator</th><th>AccessControlOperation</th></thead>' +
                            '<tbody>';
                        var acrs = $(attribute).children('acr');
                        for (var j = 0; j < acrs.length; j++) {
                            var acor = '<table class="bordered"><tbody>';
                            var acors = $(acrs[j]).children('acor')[0].firstChild.textContent.split(" ");
                            for (var k = 0; k < acors.length; k++) {
                                acor += "<tr><td>" + acors[k] + "</td></tr>";
                            }
                            acor += "</tbody></table>";
                            table += '<tr><td>' + acor + '</td><td>' + $(acrs[j]).children('acop')[0].firstChild.textContent + '</td></tr>';
                        }
                        table += '</tbody></table>';
                        value = table;
                    } else if (attribute.localName == "poa") {
                        // Handle point of access list
                        var table = '<table class="bordered"><thead><th>Point Of Access</th></thead><tbody>';
                        var poas = attribute.textContent.split(" ");
                        for (var index in poas) {
                            table += '<tr><td>' + poas[index] + '</td></tr>'
                        }
                        table += "</tbody></table>";
                        value = table;
                    } else if (resource.localName == "csr" && attribute.localName == "csi") {
                        // Add a button in case of remoteCSE link
                        value = '<button type="button" onClick="get(\'' + attribute.textContent + '\')">' + attribute.textContent + '</button>';
                    } else if (attribute.localName == "acpi") {
                        // Handle the list of acp ids
                        var table = "<table class='bordered'><thead><th>AccessControlPolicyIDs</th></thdead><tbody>";
                        var acpiList = attribute.textContent.split(" ");
                        for (var index in acpiList) {
                            table += "<tr><td>" + acpiList[index] + "</td></tr>";
                        }
                        table += "</tbody></table>";
                        value = table;
                    } else if (attribute.localName =="ldv"){
                        var table = "<table class='bordered'><thead><th>ListOfDevices</th></thdead><tbody>";
                        var devices = attribute.textContent.split(" ");
                        for(var index in devices){
                            table += "<tr><td>" + devices[index] + "</td></tr>";
                        }
                        table += "</tbody></table>";
                        value = table;
                    } else {
                        // Default case
                        value = attribute.textContent;
                    }

                    if (attribute.localName == "con") {
                        display(attribute);
                    } else {
                        $("#attributes").append("<tr><td class=" + attribute.localName + ">" + attribute.localName + "</td><td>" + value + "</td></tr>");
                    }
                }

            }
            $("li").click(function(e) {
                e.stopPropagation();
            });
        },
        error: function(xhr, status, error) {
            $("#error").html(xhr.status + ' ' + status + ' ' + error);
        }
    });
}

function encodeId(id) {
    return id.replace(/[\n\s]/g, '').replace(/[\/]/gi, "_");
}

function clean(text) {
    return text.replace(/[\n\s]/g, '');
}

function display(content) {

    $("#attributes").append("<tr><td class=" + content.localName + ">" + content.localName + "</td><td id='cont'></td></tr>");
    var obix = content.textContent;
    var rep = parser.parseFromString(obix, 'text/xml');
    if (rep.firstChild.tagName != 'obj') {
        $('#cont').append(content);
        return;
    }

    $('#cont').append("<table class='bordered' id='contentTable'><thead><tr><th>Attribute</th><th>Value</th></tr></thead></table>");

    if (rep.firstChild.tagName != "obj") {
        $('#contentTable').append('<tr><td>' + $(rep.firstChild).attr('name') + '</td><td>' + $(rep.firstChild).attr('val') + '</td></tr>');
    }

    var childrens = $(rep.firstChild).children();

    for (var t = 0; t < childrens.length; t++) {
        if (childrens[t].tagName == "op") {
            if ($(childrens[t]).attr('is') == "retrieve") {
                $('#contentTable').append("<tr><td><input type='button' onclick=retrieve('" + $(childrens[t]).attr('href') + "') value='" + $(childrens[t]).attr('name') + "' ></td><td>" + $(childrens[t]).attr('href') + "</td></tr>");
            } else if ($(childrens[t]).attr('is') == "execute") {
                $('#contentTable').append("<tr><td><input type='button' onclick=execute('" + $(childrens[t]).attr('href') + "') value='" + $(childrens[t]).attr('name') + "' ></td><td>" + $(childrens[t]).attr('href') + "</td></tr>");
            } else if ($(childrens[t]).attr('is') == "create") {
                var objs = childrens;
                for (var j = 0; j < objs.length; j++) {
                    if (objs[j].tagName == "obj") {
                        if ($(objs[j]).attr('href') == $(childrens[t]).attr('in')) {
                            var serializer = new XMLSerializer();
                            content = serializer.serializeToString(objs[j]);
                            $('#contentTable').append("<tr><td><input type='button' onclick=create('" + $(childrens[t]).attr('href') + "','" + btoa(content) + "') value='" + $(childrens[t]).attr('name') + "' ></td><td>" + $(childrens[t]).attr('href') + "</td></tr>");
                            break;
                        }
                    }
                }
            }
        } else if (childrens[t].tagName != "obj") {
            $('#contentTable').append('<tr><td>' + $(childrens[t]).attr('name') + '</td><td>' + $(childrens[t]).attr('val') + '</td></tr>');
        }
    }
}

function getUrlVar(key) {
    var result = new RegExp(key + "=([^&]*)", "i").exec(window.location.search);
    return result && unescape(result[1]) || "";
}

function execute(url) {
    $('#response').html('');
    $.ajax({
        type: 'POST',
        url: context + url,
        headers: {
            "X-M2M-Origin": make_base_auth(username, password)
        },
        beforeSend: function() {},
        timeout: 20000,
        error: function(xhr, status, error) {
            if (xhr.status == 204) successCallback(null, error, xhr);
            else $('#response').append('<h4>Post request failed: ' + xhr.status + ' ' + error + '</h4>');
        },
        dataType: 'xml',
        success: function(response) {
            $('#response').append('<h4>Successful POST request.</h4>');
            if(response != null){
                var content = response.firstChild;
                $('#response').append('<table class="bordered" id="contentTable1" ><thead><tr><th >Name</th><th >Value</th></thead></table>');
                if (content.firstChild.localName == "obj") {
                    $(content).find('obj').children().each(function() {
                        $('#contentTable1').append('<tr"><td>' + $(this).attr('name') + '</td><td>' + $(this).attr('val') + '</td></tr>');
                    });
                } else {
                    $(content).children().each(function() {
                        $('#contentTable1').append('<tr"><td>' + $(this).attr('name') + '</td><td>' + $(this).attr('val') + '</td></tr>');
                    });
                }
            }
        }
    });
}

function retrieve(url) {
    $('#response').html('');
    $.ajax({
        type: 'GET',
        url: context + url,
        headers: {
            "X-M2M-Origin": make_base_auth(username, password)
        },
        beforeSend: function() {},
        timeout: 20000,
        error: function(xhr, status, error) {
            $('#response').append('<h4>GET request failed: ' + xhr.status + ' ' + error + '</h4>');
        }, // alert a message in case of error
        dataType: 'xml',
        success: function(response) {
            if (response.firstChild.localName == "cin") {
                var content = $(response.firstChild).children("con")[0];
                var obix = content.textContent;
                content = parser.parseFromString(obix, 'text/xml');

                $('#response').append('<h4>Successful GET Request:</h4>');
                $('#response').append('<table class="bordered" id="contentTable1" ><thead><tr><th >Name</th><th >Value</th></thead></table>');
                if (content.firstChild.localName == "obj") {
                    $(content).find('obj').children().each(function() {
                        $('#contentTable1').append('<tr"><td>' + $(this).attr('name') + '</td><td>' + $(this).attr('val') + '</td></tr>');
                    });
                } else {
                    $(content).children().each(function() {
                        $('#contentTable1').append('<tr"><td>' + $(this).attr('name') + '</td><td>' + $(this).attr('val') + '</td></tr>');
                    });
                }
            }
        }
    });
}

function create(url, content) {
    $('#response').html('');
    $.ajax({
        type: 'POST',
        url: context + '/' + url,
        headers: {
            "X-M2M-Origin": make_base_auth(username, password)
        },
        beforeSend: function() {},
        timeout: 20000,
        data: atob(content),
        error: function(xhr, status, error) {
            if (xhr.status == 204) successCallback(null, error, xhr);
            else $('#response').append('<h4>Post request failed: ' + xhr.status + ' ' + error + '</h4>');
        },
        dataType: 'xml',
        success: function(response) {
            $('#response').append('<h4>Successful POST request.</h4>');
        }
    });
}