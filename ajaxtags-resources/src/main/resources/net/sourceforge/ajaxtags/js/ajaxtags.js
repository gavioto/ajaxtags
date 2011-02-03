/**
 * Copyright 2007-2010 AjaxTags-Team
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
/*jslint bitwise: true, browser: true, eqeqeq: true, immed: true, newcap: true, nomen: true, regexp: true, undef: true, white: true, maxerr: 50, indent: 4 */
/*global $, $$, $F, Ajax, Autocompleter, Class, Element, Event, Form, Option, Prototype, overlib */

// prototype and scriptaculous must be loaded before this script
if ((typeof Prototype === 'undefined') || (typeof Element === 'undefined') || (typeof Element.Methods === 'undefined')) {
    throw new Error("ajaxtags.js requires the Prototype JavaScript framework >= 1.6");
}

var AjaxJspTag = {
    Version: '1.5.2',

    DEFAULT_PARAMETER: "ajaxParameter",
    VOID_URL: "javascript://nop",
    // unused? PORTLET_MAX: 1, PORTLET_MIN: 2, PORTLET_CLOSE: 3,
    CALLOUT_OVERLIB_DEFAULT: "STICKY,CLOSECLICK,DELAY,250,TIMEOUT,5000,VAUTO,WRAPMAX,240,CSSCLASS,FGCLASS,'olfg',BGCLASS,'olbg',CGCLASS,'olcg',CAPTIONFONTCLASS,'olcap',CLOSEFONTCLASS,'olclo',TEXTFONTCLASS,'oltxt'",

    /**
     * Store all tags which have listeners.
     */
    tags: [],
    /**
     * Push listener for later reload.
     */
    add: function (tag) {
        AjaxJspTag.tags.push(tag);
    },
    /**
     * Remove listener. This is not used. All listeners are reloaded after change
     * of content.
     */
    remove: function (tag) {
        AjaxJspTag.tags = AjaxJspTag.tags.without(tag);
    },
    /**
     * Reload the listeners. Any listener has a function called setListeners with
     * no arguments.
     * TODO reload after each request
     */
    reload: function () {
        AjaxJspTag.tags.each(function (tag) {
            if (Object.isFunction(tag.setListeners)) {
                tag.setListeners();
            }
        });
    }
};

AjaxJspTag.DEFAULT_PARAMETER_REGEXP = new RegExp("(\\{" + AjaxJspTag.DEFAULT_PARAMETER + "\\})", 'g');


/**
 * Response Parsers defaults and functions which can be used for HTML, TEXT and
 * XML call DefaultResponseParser(TYPE) with the needed type
 */
var DefaultResponseParser = Class.create({
    initialize: function (defaultType, isPlainText) {
        this.type = defaultType || "xml";
        this.plaintext = false;
        if (arguments.length >= 2) {
            this.plaintext = (isPlainText && true);
        }
        this.content = null; // reset in load
        this.contentText = null;
        this.contentXML = null;
    },
    load: function (request) {
        this.contentText = request.responseText;
        this.contentXML = request.responseXML;
        this.content = null; // init
        this.parse();
    },
    parse: function (forceType) {
        var cache = this.type, xdata = null;
        if (arguments.length >= 1) {
            this.type = forceType;
        }
        if (this.type === "xml") {
            var root = this.contentXML.documentElement;
            var responseNodes = root.getElementsByTagName("response");
            xdata = [];
            var i, j, k, len1 = responseNodes.length, len2, len3;
            var items, itemNode, nameNodes, valueNodes, nameNode, valueNode, row;
            for (i = 0; i < len1; i++) {
                items = responseNodes[i].getElementsByTagName("item");
                for (j = 0, len2 = items.length; j < len2; j++) {
                    itemNode = items[j];
                    nameNodes = itemNode.getElementsByTagName("name");
                    valueNodes = itemNode.getElementsByTagName("value");
                    for (k = 0, len3 = nameNodes.length, row = []; k < len3; k++) {
                        nameNode = nameNodes[k].firstChild;
                        row.push(nameNode ? nameNode.nodeValue : "");
                    }
                    if (row.length !== 1) {
                        throw new Error("XML is not supported");
                    }
                    for (k = 0, len3 = valueNodes.length; k < len3; k++) {
                        valueNode = valueNodes[k].firstChild;
                        row.push(valueNode ? valueNode.nodeValue : "");
                    }
                    xdata.push(row);
                }
            }
        } else if (this.type === "plain") {
            xdata = this.contentText;
        } else if (this.type === "text") {
            xdata = [];
            this.contentText.split('\n').each(function (line) {
                xdata.push(line.split(','));
            });
        } else if (this.type === "html") {
            xdata = this.contentText; // response is HMTL
        } else if (this.type === "xmltohtml") {
            // replace ResponseXmlToHtmlParser
            this.parse("xml"); // parse XML stream
            xdata = new Element("div");
            var div = null, h1 = null;
            this.content.each(function (row) {
                h1 = new Element("h1");
                if (!this.plaintext) {
                    h1.innerHTML += row[0];
                } else {
                    h1 = h1.update(row[0]);
                }
                xdata.appendChild(h1);
                row.without(row[0]).each(function (line) {
                    div = new Element("div");
                    if (!this.plaintext) {
                        div.innerHTML += line;
                    } else {
                        div.update(line);
                    }
                    xdata.appendChild(div);
                });
            });
            // #4 // now switch content
            xdata = (h1 !== null) ? xdata.innerHTML : "";
        // skip plz
        } else if (this.type === "xmltohtmllist") {
            this.parse("xml");
            xdata = new Element("div");
            var ul = new Element("ul"), liElement = null;
            this.content.each(function (row) {
                liElement = new Element("li", {id: row[1]});
                if (this.plaintext) {
                    liElement.update(row[0]);
                } else {
                    liElement.innerHTML = row[0];
                }
                ul.appendChild(liElement);
            });
            xdata.appendChild(ul);
            xdata = (liElement !== null) ? xdata.innerHTML : "";
        }
        this.content = xdata;
        this.type = cache; // just copy it back
    }
});

// ResponseCallBackXmlParser is broken!!! prototype exec javascript ?
var ResponseXmlToHtmlLinkListParser = Class.create(DefaultResponseParser, {
    initialize: function ($super) {
        $super("xmltohtmllinklist");
    },
    load: function ($super, request) {
        this.collapsedClass = request.collapsedClass;
        this.treeClass = request.treeClass;
        this.nodeClass = request.nodeClass;
        this.expandedNodes = [];
        $super(request);
    },
    parse: function () {
        var responseNodes = this.contentXML.documentElement.getElementsByTagName("response");
        if (responseNodes.length > 0) {
            var itemNodes = responseNodes[0].getElementsByTagName("item");
            var ul = (itemNodes.length === 0) ? null : new Element('ul', {className: this.treeClass});
            var nameNodes = null, valueNodes = null, urlNodes = null, collapsedNodes = null, leafnodes = null;
            var name = null, value = null, url = null, collapsed = false, leaf = false;
            var li = null, i = null, link = null, div = null, len = itemNodes.length;
            var itemNode = null;
            for (i = 0; i < len; i++) {
                itemNode = itemNodes[i];
                nameNodes = itemNode.getElementsByTagName("name");
                valueNodes = itemNode.getElementsByTagName("value");
                urlNodes = itemNode.getElementsByTagName("url");
                collapsedNodes = itemNode.getElementsByTagName("collapsed");
                leafnodes = itemNode.getElementsByTagName("leaf");
                if (nameNodes.length > 0 && valueNodes.length > 0) {
                    name = nameNodes[0].firstChild.nodeValue;
                    value = valueNodes[0].firstChild.nodeValue;
                    url = (urlNodes.length > 0) ? urlNodes[0].firstChild.nodeValue : AjaxJspTag.VOID_URL;
                    leaf = (leafnodes.length > 0) && (leafnodes[0].firstChild.nodeValue).toLowerCase() === "true";
                    collapsed = (collapsedNodes.length > 0) && (collapsedNodes[0].firstChild.nodeValue).toLowerCase() === "true";
                    li = new Element("li", {id: "li_" + value});
                    if (!leaf) {
                        li.appendChild(new Element("span", {id: "span_" + value, className: this.collapsedClass}));
                    }
                    link = new Element("a", {href: url, className: this.nodeClass}).update(name);
                    div = new Element("div", {id: "div_" + value}).hide();
                    if (!collapsed) {
                        this.expandedNodes.push(value);
                    }
                    li.appendChild(link);
                    li.appendChild(div);
                    ul.appendChild(li);
                }
            }
            this.content = ul;
        }
    }
});// end of parser


/****************************************************************
 * http://www.prototypejs.org/learn/class-inheritance
 *  -----
 *  AjaxJspTag.Base übernimmt die logik des daten holen.
 *  d.h. hier wird die preFunction ausgewertet und postFunction!
 *  in der theorie kann ich alles an prototype übergeben, da einige
 *  functionen abgebildet sind!
 *
 *  ----
 *  momentane fehler, die buildfunctionen sind nicht mehr vorhanden
 *  alle listenerfunctionen müssen mit dem reload ferbunden werden
 *
 *  ---
 *  (check) prototype.js Leerer String an getElementById() übergeben.
 *
 ****************************************************************/
/**
 * AjaxTags.
 */
AjaxJspTag.Base = Class.create({
    initialize: function (options) {
        this.setOptions(options);
        this.createElements();
        this.createListeners();
        this.setListeners();
        AjaxJspTag.add(this);
    },
    setOptions: function () {
        // override in descendants
    },
    createElements: function () {
        // override in descendants
    },
    createListeners: function () {
        // override in descendants
    },
    setListeners: function () {
        // override in descendants
    },
    resolveParameters: function () {
        var o = this.options, url = o.baseUrl;
        if (!Object.isString(url) || url.strip().length === 0) {
            throw new Error("URL is empty or undefined");
        }

        var q = url.split('?'), p = [];
        o.baseUrl = q[0];
        if (o.parameters) {
            p.push(o.parameters);
        }
        if (q[1]) {
            p.push(q[1].replace(/&/g, ','));
        }
        o.parameters = p.join(',');
    },
    getMethod: function () {
        return "post";
    },
    initRequest: function () {
        if (Object.isFunction(this.options.onCreate)) {
            var result = this.options.onCreate();
            if (Object.isString(result) && "cancel" === result.toLowerCase()) {
                // only if return is string
                return false;
            }
        }
        this.resolveParameters();
        return true;
    },
    getRequestOptions: function (options, ajaxParam) {
        return Object.extend({
            asynchronous: true,
            method: this.getMethod(),
            evalScripts: true,
            parameters: this.buildParameterString(ajaxParam),
            onFailure: this.options.onFailure,
            onComplete: this.options.onComplete
        }, options || {});
    },
    getAjaxRequest: function (options, ajaxParam) {
        if (!this.initRequest()) {
            return null;
        }
        options = Object.extend({
            onSuccess: (function (request) {
                this.options.parser.load(request);
                this.options.handler(this);
            }).bind(this)
        }, this.getRequestOptions(options, ajaxParam));
        return new Ajax.Request(this.options.baseUrl, options);
    },
    getAjaxUpdater: function (options, ajaxParam) {
        if (!this.initRequest()) {
            return null;
        }
        return new Ajax.Updater(this.options.target,
            this.options.baseUrl, this.getRequestOptions(options, ajaxParam));
    },
    getPeriodicalUpdater: function (xoptions, ajaxParam) {
        if (!this.initRequest()) {
            return null;
        }

        xoptions = Object.extend({
            frequency: this.options.refreshPeriod
        }, this.getRequestOptions(xoptions, ajaxParam));

        // TODO refactor with closures
        var data = {source: this.options.source}; // that: this, opt: xoptions
        // onComplete is used by API itself don't try to use it
        data._complete = xoptions.onSuccess ? xoptions.onSuccess.bind(this) : Prototype.emptyFunction;
        // cache the old one
        xoptions.onSuccess = (function () { // inside of onSuccess "this" points to "data"
            if ($(this.source)) {
                this._complete(); // call the original onSuccess function
            } else {
                this._event.stop(); // should work
            }
        }).bind(data);
        data._event = new Ajax.PeriodicalUpdater(this.options.target,
            this.options.baseUrl, xoptions);
        return data._event;
    },
    buildParameterString: function (ajaxParam) {
        var result = [], field, key, v;
        var params = (this.replaceDefaultParam(ajaxParam) || '');
        params.split(',').each(function (pair) {
            if (pair.strip().length === 0) {
                return;
            }
            pair = pair.split('=');
            key = pair[0].strip() + '=';
            pair = pair[1];
            field = null;
            if (Object.isString(pair) && pair.strip().length > 0) {
                // TODO use id regexp from Prototype ([\w\-\*]+)
                field = pair.match(/\{[\w\.:\(\)\[\]]*\}/g);
                if (field) {
                    field = $(field[0].substring(1, field[0].length - 1));
                }
            }

            if (!field) {
                result.push(key + encodeURIComponent(pair));
            } else if (('select-multiple' === field.type) || ('checkbox' === field.type)) { // BUG 016027
                v = Form.Element.serialize(field);
                if (v) {
                    result.push(v);
                }
            } else if (/^(?:radio|text|textarea|password|hidden|select-one)$/i.test(field.type)) {
                result.push(key + encodeURIComponent(field.value));
            } else {
                result.push(key + encodeURIComponent(field.innerHTML));
            }
        });
        return result.join('&');
    },
    replaceDefaultParam: function (element) {
        var p = this.options.parameters;
        return (element) ? p.replace(AjaxJspTag.DEFAULT_PARAMETER_REGEXP, element.type ? $F(element) : element.innerHTML) : p;
    }
});

/**
 * UpdateField tag.
 */
AjaxJspTag.UpdateField = Class.create(AjaxJspTag.Base, {
    setOptions: function (options) {
        // TODO extract getDefaultOptions
        this.options = Object.extend({
            // just set options to defaults; this will be changed with parameter options
            parameters: '',
            valueUpdateByName: false,
            eventType: "click",
            handler: this.handler
        }, options || {});
        // TODO don't use object
        this.options.parser = new DefaultResponseParser(this.options.valueUpdateByName ? "xml" : "text");
    },
    createListeners: function () {
        this.listener = this.execute.bind(this);
    },
    setListeners: function () {
        var o = this.options, a = $(o.action);
        if (a) {
            a["on" + o.eventType] = this.listener;
        }
    },
    execute: function () {
        this.request = this.getAjaxRequest();
    },
    handler: function (tag) {
        var targets = this.target.split(','), items = this.parser.content, i, len;
        if (this.valueUpdateByName) {
            targets.sort(); // O(log n)
            items = items.sortBy(function (item) {
                return item[0];
            });
            var t = 0, j = 0; // O(n)
            while (t < targets.length && j < items.length) {
                var target = targets[t], item = items[j];
                if (target === item[0]) {
                    $(target).value = item[1];
                    t++;
                    j++;
                } else if (target < item[0]) {
                    t++;
                } else {
                    j++;
                }
            }
            /*for (i = 0; i < targets.length; i++) { // O(n^2)
                for (var j = 0; i < items.length; i++) {
                    if (targets[i] === items[j][0]) {
                        $(targets[i]).value = items[j][1];
                        break; // next target
                    }
                }
            }*/
        } else {
            for (i = 0, len = Math.min(targets.length, items.length); i < len; i++) {
                $(targets[i]).value = items[i][1];
            }
        }
    }
});

/**
 * Select tag.
 */
AjaxJspTag.Select = Class.create(AjaxJspTag.Base, {
    initialize: function ($super, options) {
        $super(options);
        if (this.options.executeOnLoad) {
            this.execute();
        }
    },
    setOptions: function (options) {
        this.options = Object.extend({
            parameters: '',
            emptyOptionValue: '',
            emptyOptionName: '',
            defaultOptions: '',
            eventType: "change",
            parser: new DefaultResponseParser("xml"),
            handler: this.handler
        }, options || {});
        this.options.defaultOptions = this.options.defaultOptions.split(',');
    },
    createListeners: function () {
        this.listener = this.execute.bind(this);
    },
    setListeners: function () {
        var o = this.options, s = $(o.source);
        if (s) {
            s.ajaxSelect = this.listener;
            s["on" + o.eventType] = this.listener;
        }
    },
    execute: function () {
        this.request = this.getAjaxRequest();
    },
    handler: function () {
        var target = $(this.target);
        if (!target) {
            throw new Error("target lost");
        }
        target.options.length = 0;
        target.disabled = false;
        var newOption = null;
        this.parser.content.each(function (line) {
            newOption = new Option(line[0], line[1]);
            newOption.selected = (line.length === 3 && ("true" === line[2].toLowerCase()) || (this.defaultOptions.indexOf(line[1]) != -1));
            target.options[target.options.length] = newOption;
        }, this);
        if (newOption === null) {
            target.options[target.options.length] = new Option(this.emptyOptionName, this.emptyOptionValue);
            target.disabled = true;
        }
        // auch ein SELECT TAG ?
        // kette ausloessen
        if (Object.isFunction(target.ajaxSelect)) {
            target.ajaxSelect();
        }
    }
});

/**
 * HtmlContent tag.
 */
AjaxJspTag.HtmlContent = Class.create(AjaxJspTag.Base, {
    setOptions: function (options) {
        this.options = Object.extend({
            parameters: "",
            eventType: "click",
            parser: new DefaultResponseParser("html"),
            handler: this.handler
        }, options || {});
    },
    setEvent: function (element) {
        element["on" + this.options.eventType] = this.listener;
    },
    createListeners: function () {
        this.listener = this.execute.bindAsEventListener(this);
    },
    setListeners: function () {
        var o = this.options;
        if (o.source) {
            this.setEvent($(o.source));
        } else if (o.sourceClass) {
            $$("." + o.sourceClass).each(this.setEvent, this);
        }
    },
    execute: function (event) {
        // replace default parameter with value/content of source element
        this.request = this.getAjaxUpdater(null, this.options.sourceClass ? Event.element(event) : null);
    }
});

/**
 * Callout tag.
 */
AjaxJspTag.Callout = Class.create(AjaxJspTag.Base, {
    setOptions: function (options) {
        this.options = Object.extend({
            parameters:	'',
            overlib: AjaxJspTag.CALLOUT_OVERLIB_DEFAULT,
            parser: new DefaultResponseParser("xmltohtml"),
            openEvent: "mouseover",
            closeEvent: "mouseout",
            handler: this.handler
        }, options || {});
    },
    setEvent: function (element) {
        element["on" + this.options.openEvent] = this.openListener;
        element["on" + this.options.closeEvent] = this.closeListener;
    },
    createListeners: function () {
        this.openListener = this.calloutOpen.bindAsEventListener(this);
        this.closeListener = this.calloutClose.bindAsEventListener(this);
    },
    setListeners: function () {
        $$("." + this.options.sourceClass).each(this.setEvent, this);
    },
    calloutOpen: function (event) {
        this.execute(event);
    },
    calloutClose: function (e) {
        nd(); // TODO make something with overlib's nd()
    },
    execute: function (event) {
        this.request = this.getAjaxRequest(null, Event.element(event));
    },
    handler: function () {
        var c = this.parser.content;
        if (c.strip().length !== 0) { // #4
            var args = [c];
            if (this.title) {
                args.push(CAPTION);
                args.push(this.title);
            }
            if (this.overlib) {
                args.push(this.overlib);
            }
            overlib.apply(this, args);
        }
    }
});

/**
 * TabPanel tag.
 */
AjaxJspTag.TabPanel = Class.create(AjaxJspTag.Base, {
    initialize: function (options) {
        this.setOptions(options);
        this.createElements();
    },
    setOptions: function (options) {
        this.options = Object.extend({
            eventType: "click", // XXX unused?
            parser: new DefaultResponseParser("html")
        }, options || {});
    },
    createElements: function () { // TODO move creation into java code
        var o = this.options, ul, a, defaultTab;
        this.panel = $(o.id);
        ul = this.panel.down("ul");
        o.pages.each(function (tab) {
            a = this.createTab(tab);
            if (tab.defaultTab) {
                defaultTab = defaultTab || a;
            }
            ul.appendChild(new Element("li", tab.id ? {id: tab.id} : null).update(a));
        }, this);

        this.options.target = o.contentId || this.createContent();
        if (defaultTab) {
            this.execute(defaultTab);
        }
    },
    createTab: function (tab) {
        var e = new Element('a', {
            baseUrl: tab.baseUrl,
            parameters: tab.parameters,
            href: AjaxJspTag.VOID_URL
        }).update(tab.caption);
        e.onclick = this.execute.bind(this, e);
        return e;
    },
    createContent: function () {
        // create content holder
        var c = new Element("div", {className: "tabContent"});
        this.panel.insert({
            after: c
        });
        return c.identify();
    },
    execute: function (tab) {
        tab = $(tab);
        // remove class from any tab
        this.panel.select(".ajaxCurrentTab").invoke("removeClassName", "ajaxCurrentTab");
        // add class to selected tab
        tab.addClassName("ajaxCurrentTab");
        this.options.baseUrl = tab.readAttribute("baseUrl");
        this.options.parameters = tab.readAttribute("parameters");
        this.request = this.getAjaxUpdater(/*{onSuccess: this.handler.bind(this)}*/);
    },
    handler: function () {
        // empty
    }
});

/**
 * Autocomplete tag.
 */
AjaxJspTag.XmlToHtmlAutocompleter = Class.create(Ajax.Autocompleter, {
    initialize: function (/*AjaxJspTag.Autocomplete*/autocomplete) {
        this.autocompleteTag = autocomplete;
        var o = autocomplete.options;
        this.baseInitialize(o.source, o.divElement, {
            minChars: o.minChars,
            tokens: o.appendSeparator,
            indicator: o.indicator,
            evalScripts: true,
            asynchronous: true,
            onComplete: this.onComplete.bind(this),
            afterUpdateElement: function (inputField, selectedItem) {
                autocomplete.handler(selectedItem);
            }
        });
        this.url = o.baseUrl;
    },
    getUpdatedChoices: function ($super) {
        if (!this.autocompleteTag.initRequest()) {
            this.stopIndicator(); // stop ac tag
            return;
        }
        // parse parameters and do replacements
        this.options.defaultParams = this.autocompleteTag.buildParameterString();
        $super(); // Ajax.Autocompleter#getUpdatedChoices()
    },
    onComplete: function (request) {
        var o = this.autocompleteTag.options;
        o.parser.load(request);
        this.updateChoices(o.parser.content);
        if (o.parser.content === null) {
            this.stopIndicator(); // stop ac tag
        }
        // postFunction
        if (Object.isFunction(o.onComplete)) {
            // hier wird nicht base verwendet!!!
            // Disable onupdate event handler of input field
            // because, postFunction can change the content of
            // input field and get into eternal loop.
            var inputf = $(o.source), onupdateHandler = inputf.onupdate;
            inputf.onupdate = '';
            o.onComplete();
            // Enable onupdate event handler of input field
            inputf.onupdate = onupdateHandler;
        }
    }
});

AjaxJspTag.Autocomplete = Class.create(AjaxJspTag.Base, {
    initialize: function (options) {
        this.setOptions(options);
        this.createElements();
        this.execute();
    },
    setOptions: function (options) {
        this.options = Object.extend({
            divElement: "ajaxAuto_" + options.source,
            parser: new DefaultResponseParser("xmltohtmllist", true)
            // TODO DefaultResponseParser is created every time; create parser only once
        }, options || {});
    },
    createElements: function () {
        var o = this.options, element = $(o.divElement);
        // remove previous element, if any
        if (element) {
            element.stopObserving();
            if (element.parentNode) {
                element.parentNode.removeChild(element);
            }
        }
        // insert div at the top of the document so it will not be hidden in case of overflow
        Element.insert(document.body, {top: new Element("div", {id: o.divElement, className: o.className})});
    },
    execute: function () {
        new AjaxJspTag.XmlToHtmlAutocompleter(this);
    },
    handler: function (selectedItem) {
        var o = this.options, target = $(o.target), value = selectedItem.id;
        if (target) {
            if (o.appendSeparator) {
                if (target.value.length > 0) {
                    target.value += o.appendSeparator;
                }
                target.value += value;
            } else {
                target.value = value;
            }
        }
        o.selectedIndex = selectedItem.autocompleteIndex;
        o.selectedObject = selectedItem;
        if (Object.isFunction(o.afterUpdate)) {
            o.afterUpdate(value);
        }
    }
});

/**
 * Portlet tag.
 */
AjaxJspTag.Portlet = Class.create(AjaxJspTag.Base, {
    initialize: function ($super, options) {
        $super(options);
        if (this.options.startMinimize) {
            this.togglePortlet();
        }
        if (this.options.executeOnLoad) {
            this.execute();
        }
    },
    createElements: function () {
        // erstellen des menu um doppelten code zu vermeiden
        var o = this.options, sourceBase = $(o.source).addClassName(o.classNamePrefix + "Box");
        if (o.withBar) {
            var bar = new Element("div", {className: o.classNamePrefix + "Tools"});
            this.createButton(bar, "close", o.imageClose);
            this.createButton(bar, "refresh", o.imageRefresh);
            this.createButton(bar, "toggle", o.imageMinimize);
            sourceBase.appendChild(bar);
        }

        var element = new Element("div", {className: o.classNamePrefix + "Title"});
        element.innerHTML = o.title;
        sourceBase.appendChild(element);

        o.target = new Element("div", {className: o.classNamePrefix + "Content"});
        sourceBase.appendChild(o.target);
    },
    createButton: function (bar, name, src) {
        var o = this.options;
        if (o[name]) {
            bar.appendChild(o[name] = new Element("img", {className: o.classNamePrefix + name.capitalize(), src: src}));
        }
    },
    setOptions: function (options) {
        this.options = Object.extend({
            classNamePrefix: "portlet",
            close: (options.imageClose && options.source),
            refresh: (options.imageRefresh && options.source),
            toggle: (options.imageMinimize && options.imageMaximize && options.source),
            eventType: "click",
            parser: new DefaultResponseParser("html")
        }, options || {});
        // bar yes if any image is set!
        this.options.withBar = (this.options.close || this.options.refresh || this.options.toggle);
    },
    createListeners: function () {
        this.closeListener = this.closePortlet.bind(this);
        this.refreshListener = this.refreshPortlet.bind(this);
        this.toggleListener = this.togglePortlet.bind(this);
    },
    setListeners: function () {
        // TODO change to delegate listener on bar[evt]
        var o = this.options, evt = "on" + o.eventType;
        if (o.close) {
            o.close[evt] = this.closeListener;
        }
        if (o.refresh) {
            o.refresh[evt] = this.refreshListener;
        }
        if (o.toggle) {
            o.toggle[evt] = this.toggleListener;
        }
    },
    execute: function () {
        this.ajaxPeriodicalUpdater = this.options.refreshPeriod ? this.getPeriodicalUpdater() : this.getAjaxUpdater();
    },
    stopAutoRefresh: function () {
        // stop auto-update if present
        if (this.ajaxPeriodicalUpdater && this.options.refreshPeriod) {
            this.ajaxPeriodicalUpdater.stop();
        }
        this.ajaxPeriodicalUpdater = null;
    },
    refreshPortlet: function () {
        // clear existing updater
        this.stopAutoRefresh();
        this.execute();
    },
    closePortlet: function () {
        this.stopAutoRefresh();
        Element.remove(this.options.source);
        // TODO save state in cookie
    },
    togglePortlet: function () {
        var o = this.options;
        if (o.toggle) {
            if (o.toggle.src.endsWith(o.imageMinimize)) {
                Element.hide(o.target);
                o.toggle.src = o.imageMaximize;
            } else {
                Element.show(o.target);
                o.toggle.src = o.imageMinimize;
                this.refreshPortlet();
            }
        }
        // TODO save state in cookie
    }
});

/**
 * Tree tag.
 */
AjaxJspTag.Tree = Class.create(AjaxJspTag.Base, {
    initialize: function (options) {
        this.setOptions(options);
        this.execute();
    },
    setOptions: function (options) {
        this.options = Object.extend({
            eventType: "click",
            parser: new ResponseXmlToHtmlLinkListParser(),
            collapsedClass: "collapsedNode",
            expandedClass: "expandedNode",
            treeClass: "tree",
            nodeClass: ''
        }, options || {});
    },
    execute: function () {
        var t = this.options.target, img;
        if (t) {
            img = $("span_" + t);
            if (img && !this.toggle(img)) {
                $("div_" + t).hide().update();
                return;
            }
        }
        this.request = this.getAjaxRequest({
            onSuccess: this.processResponse.bind(this)
        }, {
            innerHTML: t // request parameter
        });
    },
    toggle: function (e) {
        var o = this.options, expanded = e.hasClassName(o.expandedClass);
        e.removeClassName(expanded ? o.expandedClass : o.collapsedClass).addClassName(expanded ? o.collapsedClass : o.expandedClass);
        return !expanded;
    },
    processResponse: function (response) { // TODO refactor to use default onSuccess in getAjaxRequest?
        var o = this.options;
        o.parser.load({
            responseXML: response.responseXML,
            collapsedClass: o.collapsedClass,
            treeClass: o.treeClass,
            nodeClass: o.nodeClass
        });
        this.handler();
    },
    handler: function () {
        var o = this.options, parser = o.parser, target = $(o.target), displayValue = 'block';
        if (!parser.content) {
            target.innerHTML = "";
            displayValue = 'none';
        }
        target.appendChild(parser.content);
        target.setStyle({
            display: displayValue
        });
        if (displayValue === 'block') {
            target.select("span").each(function (image) {
                image["on" + o.eventType] = this.toggleTreeNode.bind(this, image.id.substring(5));
                //image.observe(o.eventType, this.toggleTreeNode.bind(this, image.id.substring(5)));
            }, this);

            parser.expandedNodes.each(this.toggleTreeNode, this);
            AjaxJspTag.reload();
        }
    },
    toggleTreeNode: function (xid) {
        var opt = Object.clone(this.options);
        opt.target = xid;
        return new AjaxJspTag.Tree(opt);
    }
});

/**
 * Toggle tag.
 */
// we can create the 'a' tags here
AjaxJspTag.Toggle = Class.create(AjaxJspTag.Base, {
    setOptions: function (options) {
        this.options = Object.extend({
            parameters: ('rating={' + AjaxJspTag.DEFAULT_PARAMETER + '}'),
            parser: new DefaultResponseParser("text"),
            handler: this.handler
        }, options || {});
    },
    createElements: function () {
        // create message DIV
        var o = this.options;
        this.container = $(o.source);
        if (o.messageClass) {
            // TODO check if $(id) already exists
            this.messageContainer = new Element("div", {id: o.source + "_message", className: o.messageClass});
            this.container.insert({'top': this.messageContainer});
        }
        this.classList = [o.selectedOverClass, o.selectedLessClass, o.overClass, o.selectedClass];
    },
    setEvent: function (element) {
        element.onmouseover = this.mouseoverListener;
        element.onmouseout = this.mouseoutListener;
        element.onclick = this.clickListener;
    },
    createListeners: function () {
        this.mouseoverListener = this.raterMouseOver.bindAsEventListener(this);
        this.mouseoutListener = this.raterMouseOut.bindAsEventListener(this);
        this.clickListener = this.raterClick.bindAsEventListener(this);
    },
    setListeners: function () {
        // TODO change to delegate listener on this.container
        // attach events to anchors
        this.container.select('a').each(this.setEvent, this);
    },
    setMessage: function (message) {
        if (this.messageContainer) {
            this.messageContainer.innerHTML = message;
        }
    },
    raterMouseOver: function (e) {
        // get list of all anchors
        var elements = this.container.select('a');
        // find the current rating
        var selectedObject = this.container.select('.' + this.options.selectedClass).pop();
        var selectedIndex = elements.indexOf(selectedObject);
        // find the index of the 'hovered' element
        var currentIndex = elements.indexOf(Event.element(e));
        this.setMessage(elements[currentIndex].title);
        // iterate over each anchor and apply styles
        for (var i = 0, len = elements.length; i < len; i++) {
            if (selectedIndex >= 0 && (i <= selectedIndex || i > currentIndex)) {
                if (i <= selectedIndex) {
                    elements[i].addClassName((i <= currentIndex) ? this.options.selectedOverClass : this.options.selectedLessClass);
                }
            } else if (i <= currentIndex) {
                elements[i].addClassName(this.options.overClass);
            }
        }
    },
    raterMouseOut: function (e) {
        this.setMessage(''); // clear message
        this.clearCSSClass(this.options.selectedClass);
    },
    clearCSSClass: function (ohne) {
        var list = this.container.select('a'), li = this.classList;
        if (li.indexOf(ohne) !== -1) {
            li = li.without(ohne);
        }
        list.each(function (element) {
            // TODO use regex to remove all class names in single step
            li.each(element.removeClassName, element);
        }, this);
        return list;
    },
    raterClick: function (e) {
        // get list of all anchors
        var selectedObject = this.container.select('.' + this.options.selectedClass).pop();
        var elements = this.clearCSSClass();
        var selectedIndex = elements.indexOf(selectedObject);

        // find the index of the 'hovered' element
        var currentIndex = elements.indexOf(Event.element(e));
        // update styles
        var onoff = this.container.hasClassName('onoff');
        for (var i = 0; i <= currentIndex; i++) {
            if (!onoff || (i === currentIndex && selectedIndex === -1)) {
                elements[i].addClassName(this.options.selectedClass);
            }
        }
        // send AJAX
        var ratingToSend = elements[currentIndex].title;
        if (onoff) {
            // send opposite of what was selected
            var ratings = this.options.ratings.split(',');
            ratingToSend = (ratings[0] == ratingToSend) ? ratings[1] : ratings[0];
            elements[currentIndex].title = ratingToSend;
        }
        this.execute({
            innerHTML: ratingToSend
        }); // warp this to make replacement valid!
        // set field (if defined)
        if (this.options.state) {
            $(this.options.state).value = ratingToSend;
        }
    },
    execute: function (ratingValue) {
        this.request = this.getAjaxRequest(null, ratingValue);
    },
    handler: function () {
        // daten in items
        var erg = this.parser.content[0][0]; // on/off / 1,2,3
        if (Object.isFunction(this.updateFunction)) {
            this.updateFunction(erg); // ??? XXX do we need this!
            // use onComplete?
        }
    }
});

/**
 * OnClick tag.
 */
AjaxJspTag.OnClick = Class.create(AjaxJspTag.Base, {
    initialize: function (options) {
        this.setOptions(options);
        this.execute();
    },
    setOptions: function (options) {
        this.options = options || {};
    },
    execute: function () {
        this.request = this.getAjaxUpdater({
            requestHeaders: this.options.requestHeaders,
            onSuccess: this.handler.bind(this)
        }, this.options.eventBase);
    },
    handler: function () {
        // etwas machen wenn erfolgreich?
    }
});

/**
 * Submit tag.
 */
AjaxJspTag.Submit = Class.create(AjaxJspTag.Base, {
    setOptions: function (options) {
        // TODO option for multiple submit buttons: serialize(true, {hash: false, submit: ?})
        this.options = options || {};
    },
    createListeners: function () {
        this.listener = this.execute.bind(this);
    },
    setListeners: function () {
        var f = $(this.options.source);
        if (f) {
            f.onsubmit = this.listener;
        }
    },
    execute: function () {
        try {
            var o = this.options, f = $(o.source);
            if (f) {
                o.baseUrl = f.action;
                this.request = this.getAjaxUpdater({
                    parameters: f.serialize(true)
                });
            }
        } catch (e) {
            alert("Exception in form.onsubmit: " + e.message);
        } finally {
            // prevent form submission
            return false;
        }
    }
});
