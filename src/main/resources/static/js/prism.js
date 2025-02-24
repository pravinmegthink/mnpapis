/*! For license information please see prism.js.LICENSE.txt */
//(()=>{var e={152:function(e){var t;t=function(){return function(){var e={686:function(e,t,n){"use strict";n.d(t,{default:function(){return w}});var r=n(279),a=n.n(r),o=n(370),i=n.n(o),s=n(817),u=n.n(s);function l(e){try{return document.execCommand(e)}catch(e){return!1}}var c=function(e){var t=u()(e);return l("cut"),t};function d(e){var t="rtl"===document.documentElement.getAttribute("dir"),n=document.createElement("textarea");n.style.fontSize="12pt",n.style.border="0",n.style.padding="0",n.style.margin="0",n.style.position="absolute",n.style[t?"right":"left"]="-9999px";var r=window.pageYOffset||document.documentElement.scrollTop;return n.style.top="".concat(r,"px"),n.setAttribute("readonly",""),n.value=e,n}var p=function(e){var t=arguments.length>1&&void 0!==arguments[1]?arguments[1]:{container:document.body},n="";if("string"==typeof e){var r=d(e);t.container.appendChild(r),n=u()(r),l("copy"),r.remove()}else n=u()(e),l("copy");return n};function g(e){return g="function"==typeof Symbol&&"symbol"==typeof Symbol.iterator?function(e){return typeof e}:function(e){return e&&"function"==typeof Symbol&&e.constructor===Symbol&&e!==Symbol.prototype?"symbol":typeof e},g(e)}function f(e){return f="function"==typeof Symbol&&"symbol"==typeof Symbol.iterator?function(e){return typeof e}:function(e){return e&&"function"==typeof Symbol&&e.constructor===Symbol&&e!==Symbol.prototype?"symbol":typeof e},f(e)}function h(e,t){for(var n=0;n<t.length;n++){var r=t[n];r.enumerable=r.enumerable||!1,r.configurable=!0,"value"in r&&(r.writable=!0),Object.defineProperty(e,r.key,r)}}function m(e,t){return m=Object.setPrototypeOf||function(e,t){return e.__proto__=t,e},m(e,t)}function y(e,t){return!t||"object"!==f(t)&&"function"!=typeof t?function(e){if(void 0===e)throw new ReferenceError("this hasn't been initialised - super() hasn't been called");return e}(e):t}function v(e){return v=Object.setPrototypeOf?Object.getPrototypeOf:function(e){return e.__proto__||Object.getPrototypeOf(e)},v(e)}function b(e,t){var n="data-clipboard-".concat(e);if(t.hasAttribute(n))return t.getAttribute(n)}var x=function(e){!function(e,t){if("function"!=typeof t&&null!==t)throw new TypeError("Super expression must either be null or a function");e.prototype=Object.create(t&&t.prototype,{constructor:{value:e,writable:!0,configurable:!0}}),t&&m(e,t)}(u,e);var t,n,r,a,o,s=(a=u,o=function(){if("undefined"==typeof Reflect||!Reflect.construct)return!1;if(Reflect.construct.sham)return!1;if("function"==typeof Proxy)return!0;try{return Date.prototype.toString.call(Reflect.construct(Date,[],(function(){}))),!0}catch(e){return!1}}(),function(){var e,t=v(a);if(o){var n=v(this).constructor;e=Reflect.construct(t,arguments,n)}else e=t.apply(this,arguments);return y(this,e)});function u(e,t){var n;return function(e,t){if(!(e instanceof t))throw new TypeError("Cannot call a class as a function")}(this,u),(n=s.call(this)).resolveOptions(t),n.listenClick(e),n}return t=u,n=[{key:"resolveOptions",value:function(){var e=arguments.length>0&&void 0!==arguments[0]?arguments[0]:{};this.action="function"==typeof e.action?e.action:this.defaultAction,this.target="function"==typeof e.target?e.target:this.defaultTarget,this.text="function"==typeof e.text?e.text:this.defaultText,this.container="object"===f(e.container)?e.container:document.body}},{key:"listenClick",value:function(e){var t=this;this.listener=i()(e,"click",(function(e){return t.onClick(e)}))}},{key:"onClick",value:function(e){var t=e.delegateTarget||e.currentTarget,n=this.action(t)||"copy",r=function(){var e=arguments.length>0&&void 0!==arguments[0]?arguments[0]:{},t=e.action,n=void 0===t?"copy":t,r=e.container,a=e.target,o=e.text;if("copy"!==n&&"cut"!==n)throw new Error('Invalid "action" value, use either "copy" or "cut"');if(void 0!==a){if(!a||"object"!==g(a)||1!==a.nodeType)throw new Error('Invalid "target" value, use a valid Element');if("copy"===n&&a.hasAttribute("disabled"))throw new Error('Invalid "target" attribute. Please use "readonly" instead of "disabled" attribute');if("cut"===n&&(a.hasAttribute("readonly")||a.hasAttribute("disabled")))throw new Error('Invalid "target" attribute. You can\'t cut text from elements with "readonly" or "disabled" attributes')}return o?p(o,{container:r}):a?"cut"===n?c(a):p(a,{container:r}):void 0}({action:n,container:this.container,target:this.target(t),text:this.text(t)});this.emit(r?"success":"error",{action:n,text:r,trigger:t,clearSelection:function(){t&&t.focus(),document.activeElement.blur(),window.getSelection().removeAllRanges()}})}},{key:"defaultAction",value:function(e){return b("action",e)}},{key:"defaultTarget",value:function(e){var t=b("target",e);if(t)return document.querySelector(t)}},{key:"defaultText",value:function(e){return b("text",e)}},{key:"destroy",value:function(){this.listener.destroy()}}],r=[{key:"copy",value:function(e){var t=arguments.length>1&&void 0!==arguments[1]?arguments[1]:{container:document.body};return p(e,t)}},{key:"cut",value:function(e){return c(e)}},{key:"isSupported",value:function(){var e=arguments.length>0&&void 0!==arguments[0]?arguments[0]:["copy","cut"],t="string"==typeof e?[e]:e,n=!!document.queryCommandSupported;return t.forEach((function(e){n=n&&!!document.queryCommandSupported(e)})),n}}],n&&h(t.prototype,n),r&&h(t,r),u}(a()),w=x},828:function(e){if("undefined"!=typeof Element&&!Element.prototype.matches){var t=Element.prototype;t.matches=t.matchesSelector||t.mozMatchesSelector||t.msMatchesSelector||t.oMatchesSelector||t.webkitMatchesSelector}e.exports=function(e,t){for(;e&&9!==e.nodeType;){if("function"==typeof e.matches&&e.matches(t))return e;e=e.parentNode}}},438:function(e,t,n){var r=n(828);function a(e,t,n,r,a){var i=o.apply(this,arguments);return e.addEventListener(n,i,a),{destroy:function(){e.removeEventListener(n,i,a)}}}function o(e,t,n,a){return function(n){n.delegateTarget=r(n.target,t),n.delegateTarget&&a.call(e,n)}}e.exports=function(e,t,n,r,o){return"function"==typeof e.addEventListener?a.apply(null,arguments):"function"==typeof n?a.bind(null,document).apply(null,arguments):("string"==typeof e&&(e=document.querySelectorAll(e)),Array.prototype.map.call(e,(function(e){return a(e,t,n,r,o)})))}},879:function(e,t){t.node=function(e){return void 0!==e&&e instanceof HTMLElement&&1===e.nodeType},t.nodeList=function(e){var n=Object.prototype.toString.call(e);return void 0!==e&&("[object NodeList]"===n||"[object HTMLCollection]"===n)&&"length"in e&&(0===e.length||t.node(e[0]))},t.string=function(e){return"string"==typeof e||e instanceof String},t.fn=function(e){return"[object Function]"===Object.prototype.toString.call(e)}},370:function(e,t,n){var r=n(879),a=n(438);e.exports=function(e,t,n){if(!e&&!t&&!n)throw new Error("Missing required arguments");if(!r.string(t))throw new TypeError("Second argument must be a String");if(!r.fn(n))throw new TypeError("Third argument must be a Function");if(r.node(e))return function(e,t,n){return e.addEventListener(t,n),{destroy:function(){e.removeEventListener(t,n)}}}(e,t,n);if(r.nodeList(e))return function(e,t,n){return Array.prototype.forEach.call(e,(function(e){e.addEventListener(t,n)})),{destroy:function(){Array.prototype.forEach.call(e,(function(e){e.removeEventListener(t,n)}))}}}(e,t,n);if(r.string(e))return function(e,t,n){return a(document.body,e,t,n)}(e,t,n);throw new TypeError("First argument must be a String, HTMLElement, HTMLCollection, or NodeList")}},817:function(e){e.exports=function(e){var t;if("SELECT"===e.nodeName)e.focus(),t=e.value;else if("INPUT"===e.nodeName||"TEXTAREA"===e.nodeName){var n=e.hasAttribute("readonly");n||e.setAttribute("readonly",""),e.select(),e.setSelectionRange(0,e.value.length),n||e.removeAttribute("readonly"),t=e.value}else{e.hasAttribute("contenteditable")&&e.focus();var r=window.getSelection(),a=document.createRange();a.selectNodeContents(e),r.removeAllRanges(),r.addRange(a),t=r.toString()}return t}},279:function(e){function t(){}t.prototype={on:function(e,t,n){var r=this.e||(this.e={});return(r[e]||(r[e]=[])).push({fn:t,ctx:n}),this},once:function(e,t,n){var r=this;function a(){r.off(e,a),t.apply(n,arguments)}return a._=t,this.on(e,a,n)},emit:function(e){for(var t=[].slice.call(arguments,1),n=((this.e||(this.e={}))[e]||[]).slice(),r=0,a=n.length;r<a;r++)n[r].fn.apply(n[r].ctx,t);return this},off:function(e,t){var n=this.e||(this.e={}),r=n[e],a=[];if(r&&t)for(var o=0,i=r.length;o<i;o++)r[o].fn!==t&&r[o].fn._!==t&&a.push(r[o]);return a.length?n[e]=a:delete n[e],this}},e.exports=t,e.exports.TinyEmitter=t}},t={};function n(r){if(t[r])return t[r].exports;var a=t[r]={exports:{}};return e[r](a,a.exports,n),a.exports}return n.n=function(e){var t=e&&e.__esModule?function(){return e.default}:function(){return e};return n.d(t,{a:t}),t},n.d=function(e,t){for(var r in t)n.o(t,r)&&!n.o(e,r)&&Object.defineProperty(e,r,{enumerable:!0,get:t[r]})},n.o=function(e,t){return Object.prototype.hasOwnProperty.call(e,t)},n(686)}().default},e.exports=t()},381:()=>{!function(e){e.languages.pug={comment:{pattern:/(^([\t ]*))\/\/.*(?:(?:\r?\n|\r)\2[\t ].+)*/m,lookbehind:!0},"multiline-script":{pattern:/(^([\t ]*)script\b.*\.[\t ]*)(?:(?:\r?\n|\r(?!\n))(?:\2[\t ].+|\s*?(?=\r?\n|\r)))+/m,lookbehind:!0,inside:e.languages.javascript},filter:{pattern:/(^([\t ]*)):.+(?:(?:\r?\n|\r(?!\n))(?:\2[\t ].+|\s*?(?=\r?\n|\r)))+/m,lookbehind:!0,inside:{"filter-name":{pattern:/^:[\w-]+/,alias:"variable"},text:/\S[\s\S]*/}},"multiline-plain-text":{pattern:/(^([\t ]*)[\w\-#.]+\.[\t ]*)(?:(?:\r?\n|\r(?!\n))(?:\2[\t ].+|\s*?(?=\r?\n|\r)))+/m,lookbehind:!0},markup:{pattern:/(^[\t ]*)<.+/m,lookbehind:!0,inside:e.languages.markup},doctype:{pattern:/((?:^|\n)[\t ]*)doctype(?: .+)?/,lookbehind:!0},"flow-control":{pattern:/(^[\t ]*)(?:case|default|each|else|if|unless|when|while)\b(?: .+)?/m,lookbehind:!0,inside:{each:{pattern:/^each .+? in\b/,inside:{keyword:/\b(?:each|in)\b/,punctuation:/,/}},branch:{pattern:/^(?:case|default|else|if|unless|when|while)\b/,alias:"keyword"},rest:e.languages.javascript}},keyword:{pattern:/(^[\t ]*)(?:append|block|extends|include|prepend)\b.+/m,lookbehind:!0},mixin:[{pattern:/(^[\t ]*)mixin .+/m,lookbehind:!0,inside:{keyword:/^mixin/,function:/\w+(?=\s*\(|\s*$)/,punctuation:/[(),.]/}},{pattern:/(^[\t ]*)\+.+/m,lookbehind:!0,inside:{name:{pattern:/^\+\w+/,alias:"function"},rest:e.languages.javascript}}],script:{pattern:/(^[\t ]*script(?:(?:&[^(]+)?\([^)]+\))*[\t ]).+/m,lookbehind:!0,inside:e.languages.javascript},"plain-text":{pattern:/(^[\t ]*(?!-)[\w\-#.]*[\w\-](?:(?:&[^(]+)?\([^)]+\))*\/?[\t ]).+/m,lookbehind:!0},tag:{pattern:/(^[\t ]*)(?!-)[\w\-#.]*[\w\-](?:(?:&[^(]+)?\([^)]+\))*\/?:?/m,lookbehind:!0,inside:{attributes:[{pattern:/&[^(]+\([^)]+\)/,inside:e.languages.javascript},{pattern:/\([^)]+\)/,inside:{"attr-value":{pattern:/(=\s*(?!\s))(?:\{[^}]*\}|[^,)\r\n]+)/,lookbehind:!0,inside:e.languages.javascript},"attr-name":/[\w-]+(?=\s*!?=|\s*[,)])/,punctuation:/[!=(),]+/}}],punctuation:/:/,"attr-id":/#[\w\-]+/,"attr-class":/\.[\w\-]+/}},code:[{pattern:/(^[\t ]*(?:-|!?=)).+/m,lookbehind:!0,inside:e.languages.javascript}],punctuation:/[.\-!=|]+/};for(var t=/(^([\t ]*)):<filter_name>(?:(?:\r?\n|\r(?!\n))(?:\2[\t ].+|\s*?(?=\r?\n|\r)))+/.source,n=[{filter:"atpl",language:"twig"},{filter:"coffee",language:"coffeescript"},"ejs","handlebars","less","livescript","markdown",{filter:"sass",language:"scss"},"stylus"],r={},a=0,o=n.length;a<o;a++){var i=n[a];i="string"==typeof i?{filter:i,language:i}:i,e.languages[i.language]&&(r["filter-"+i.filter]={pattern:RegExp(t.replace("<filter_name>",(function(){return i.filter})),"m"),lookbehind:!0,inside:{"filter-name":{pattern:/^:[\w-]+/,alias:"variable"},text:{pattern:/\S[\s\S]*/,alias:[i.language,"language-"+i.language],inside:e.languages[i.language]}}})}e.languages.insertBefore("pug","filter",r)}(Prism)},263:()=>{!function(){function e(e){var t=document.createElement("textarea");t.value=e.getText(),t.style.top="0",t.style.left="0",t.style.position="fixed",document.body.appendChild(t),t.focus(),t.select();try{var n=document.execCommand("copy");setTimeout((function(){n?e.success():e.error()}),1)}catch(t){setTimeout((function(){e.error(t)}),1)}document.body.removeChild(t)}"undefined"!=typeof Prism&&"undefined"!=typeof document&&(Prism.plugins.toolbar?Prism.plugins.toolbar.registerButton("copy-to-clipboard",(function(t){var n=t.element,r=function(e){var t={copy:"Copy","copy-error":"Press Ctrl+C to copy","copy-success":"Copied!","copy-timeout":5e3};for(var n in t){for(var r="data-prismjs-"+n,a=e;a&&!a.hasAttribute(r);)a=a.parentElement;a&&(t[n]=a.getAttribute(r))}return t}(n),a=document.createElement("button");a.className="copy-to-clipboard-button",a.setAttribute("type","button");var o=document.createElement("span");return a.appendChild(o),s("copy"),function(t,n){t.addEventListener("click",(function(){!function(t){navigator.clipboard?navigator.clipboard.writeText(t.getText()).then(t.success,(function(){e(t)})):e(t)}(n)}))}(a,{getText:function(){return n.textContent},success:function(){s("copy-success"),i()},error:function(){s("copy-error"),setTimeout((function(){!function(e){window.getSelection().selectAllChildren(e)}(n)}),1),i()}}),a;function i(){setTimeout((function(){s("copy")}),r["copy-timeout"])}function s(e){o.textContent=r[e],a.setAttribute("data-copy-state",e)}})):console.warn("Copy to Clipboard plugin loaded before Toolbar plugin."))}()},206:()=>{!function(){if("undefined"!=typeof Prism&&"undefined"!=typeof document){var e=[],t={},n=function(){};Prism.plugins.toolbar={};var r=Prism.plugins.toolbar.registerButton=function(n,r){var a;a="function"==typeof r?r:function(e){var t;return"function"==typeof r.onClick?((t=document.createElement("button")).type="button",t.addEventListener("click",(function(){r.onClick.call(this,e)}))):"string"==typeof r.url?(t=document.createElement("a")).href=r.url:t=document.createElement("span"),r.className&&t.classList.add(r.className),t.textContent=r.text,t},n in t?console.warn('There is a button with the key "'+n+'" registered already.'):e.push(t[n]=a)},a=Prism.plugins.toolbar.hook=function(r){var a=r.element.parentNode;if(a&&/pre/i.test(a.nodeName)&&!a.parentNode.classList.contains("code-toolbar")){var o=document.createElement("div");o.classList.add("code-toolbar"),a.parentNode.insertBefore(o,a),o.appendChild(a);var i=document.createElement("div");i.classList.add("toolbar");var s=e,u=function(e){for(;e;){var t=e.getAttribute("data-toolbar-order");if(null!=t)return(t=t.trim()).length?t.split(/\s*,\s*/g):[];e=e.parentElement}}(r.element);u&&(s=u.map((function(e){return t[e]||n}))),s.forEach((function(e){var t=e(r);if(t){var n=document.createElement("div");n.classList.add("toolbar-item"),n.appendChild(t),i.appendChild(n)}})),o.appendChild(i)}};r("label",(function(e){var t=e.element.parentNode;if(t&&/pre/i.test(t.nodeName)&&t.hasAttribute("data-label")){var n,r,a=t.getAttribute("data-label");try{r=document.querySelector("template#"+a)}catch(e){}return r?n=r.content:(t.hasAttribute("data-url")?(n=document.createElement("a")).href=t.getAttribute("data-url"):n=document.createElement("span"),n.textContent=a),n}})),Prism.hooks.add("complete",a)}}()},660:(e,t,n)=>{var r=function(e){var t=/(?:^|\s)lang(?:uage)?-([\w-]+)(?=\s|$)/i,n=0,r={},a={manual:e.Prism&&e.Prism.manual,disableWorkerMessageHandler:e.Prism&&e.Prism.disableWorkerMessageHandler,util:{encode:function e(t){return t instanceof o?new o(t.type,e(t.content),t.alias):Array.isArray(t)?t.map(e):t.replace(/&/g,"&amp;").replace(/</g,"&lt;").replace(/\u00a0/g," ")},type:function(e){return Object.prototype.toString.call(e).slice(8,-1)},objId:function(e){return e.__id||Object.defineProperty(e,"__id",{value:++n}),e.__id},clone:function e(t,n){var r,o;switch(n=n||{},a.util.type(t)){case"Object":if(o=a.util.objId(t),n[o])return n[o];for(var i in r={},n[o]=r,t)t.hasOwnProperty(i)&&(r[i]=e(t[i],n));return r;case"Array":return o=a.util.objId(t),n[o]?n[o]:(r=[],n[o]=r,t.forEach((function(t,a){r[a]=e(t,n)})),r);default:return t}},getLanguage:function(e){for(;e;){var n=t.exec(e.className);if(n)return n[1].toLowerCase();e=e.parentElement}return"none"},setLanguage:function(e,n){e.className=e.className.replace(RegExp(t,"gi"),""),e.classList.add("language-"+n)},currentScript:function(){if("undefined"==typeof document)return null;if("currentScript"in document)return document.currentScript;try{throw new Error}catch(r){var e=(/at [^(\r\n]*\((.*):[^:]+:[^:]+\)$/i.exec(r.stack)||[])[1];if(e){var t=document.getElementsByTagName("script");for(var n in t)if(t[n].src==e)return t[n]}return null}},isActive:function(e,t,n){for(var r="no-"+t;e;){var a=e.classList;if(a.contains(t))return!0;if(a.contains(r))return!1;e=e.parentElement}return!!n}},languages:{plain:r,plaintext:r,text:r,txt:r,extend:function(e,t){var n=a.util.clone(a.languages[e]);for(var r in t)n[r]=t[r];return n},insertBefore:function(e,t,n,r){var o=(r=r||a.languages)[e],i={};for(var s in o)if(o.hasOwnProperty(s)){if(s==t)for(var u in n)n.hasOwnProperty(u)&&(i[u]=n[u]);n.hasOwnProperty(s)||(i[s]=o[s])}var l=r[e];return r[e]=i,a.languages.DFS(a.languages,(function(t,n){n===l&&t!=e&&(this[t]=i)})),i},DFS:function e(t,n,r,o){o=o||{};var i=a.util.objId;for(var s in t)if(t.hasOwnProperty(s)){n.call(t,s,t[s],r||s);var u=t[s],l=a.util.type(u);"Object"!==l||o[i(u)]?"Array"!==l||o[i(u)]||(o[i(u)]=!0,e(u,n,s,o)):(o[i(u)]=!0,e(u,n,null,o))}}},plugins:{},highlightAll:function(e,t){a.highlightAllUnder(document,e,t)},highlightAllUnder:function(e,t,n){var r={callback:n,container:e,selector:'code[class*="language-"], [class*="language-"] code, code[class*="lang-"], [class*="lang-"] code'};a.hooks.run("before-highlightall",r),r.elements=Array.prototype.slice.apply(r.container.querySelectorAll(r.selector)),a.hooks.run("before-all-elements-highlight",r);for(var o,i=0;o=r.elements[i++];)a.highlightElement(o,!0===t,r.callback)},highlightElement:function(t,n,r){var o=a.util.getLanguage(t),i=a.languages[o];a.util.setLanguage(t,o);var s=t.parentElement;s&&"pre"===s.nodeName.toLowerCase()&&a.util.setLanguage(s,o);var u={element:t,language:o,grammar:i,code:t.textContent};function l(e){u.highlightedCode=e,a.hooks.run("before-insert",u),u.element.innerHTML=u.highlightedCode,a.hooks.run("after-highlight",u),a.hooks.run("complete",u),r&&r.call(u.element)}if(a.hooks.run("before-sanity-check",u),(s=u.element.parentElement)&&"pre"===s.nodeName.toLowerCase()&&!s.hasAttribute("tabindex")&&s.setAttribute("tabindex","0"),!u.code)return a.hooks.run("complete",u),void(r&&r.call(u.element));if(a.hooks.run("before-highlight",u),u.grammar)if(n&&e.Worker){var c=new Worker(a.filename);c.onmessage=function(e){l(e.data)},c.postMessage(JSON.stringify({language:u.language,code:u.code,immediateClose:!0}))}else l(a.highlight(u.code,u.grammar,u.language));else l(a.util.encode(u.code))},highlight:function(e,t,n){var r={code:e,grammar:t,language:n};if(a.hooks.run("before-tokenize",r),!r.grammar)throw new Error('The language "'+r.language+'" has no grammar.');return r.tokens=a.tokenize(r.code,r.grammar),a.hooks.run("after-tokenize",r),o.stringify(a.util.encode(r.tokens),r.language)},tokenize:function(e,t){var n=t.rest;if(n){for(var r in n)t[r]=n[r];delete t.rest}var a=new u;return l(a,a.head,e),s(e,a,t,a.head,0),function(e){for(var t=[],n=e.head.next;n!==e.tail;)t.push(n.value),n=n.next;return t}(a)},hooks:{all:{},add:function(e,t){var n=a.hooks.all;n[e]=n[e]||[],n[e].push(t)},run:function(e,t){var n=a.hooks.all[e];if(n&&n.length)for(var r,o=0;r=n[o++];)r(t)}},Token:o};function o(e,t,n,r){this.type=e,this.content=t,this.alias=n,this.length=0|(r||"").length}function i(e,t,n,r){e.lastIndex=t;var a=e.exec(n);if(a&&r&&a[1]){var o=a[1].length;a.index+=o,a[0]=a[0].slice(o)}return a}function s(e,t,n,r,u,d){for(var p in n)if(n.hasOwnProperty(p)&&n[p]){var g=n[p];g=Array.isArray(g)?g:[g];for(var f=0;f<g.length;++f){if(d&&d.cause==p+","+f)return;var h=g[f],m=h.inside,y=!!h.lookbehind,v=!!h.greedy,b=h.alias;if(v&&!h.pattern.global){var x=h.pattern.toString().match(/[imsuy]*$/)[0];h.pattern=RegExp(h.pattern.source,x+"g")}for(var w=h.pattern||h,k=r.next,A=u;k!==t.tail&&!(d&&A>=d.reach);A+=k.value.length,k=k.next){var F=k.value;if(t.length>e.length)return;if(!(F instanceof o)){var E,S=1;if(v){if(!(E=i(w,A,e,y))||E.index>=e.length)break;var j=E.index,C=E.index+E[0].length,T=A;for(T+=k.value.length;j>=T;)T+=(k=k.next).value.length;if(A=T-=k.value.length,k.value instanceof o)continue;for(var $=k;$!==t.tail&&(T<C||"string"==typeof $.value);$=$.next)S++,T+=$.value.length;S--,F=e.slice(A,T),E.index-=A}else if(!(E=i(w,0,F,y)))continue;j=E.index;var _=E[0],L=F.slice(0,j),O=F.slice(j+_.length),P=A+F.length;d&&P>d.reach&&(d.reach=P);var N=k.prev;if(L&&(N=l(t,N,L),A+=L.length),c(t,N,S),k=l(t,N,new o(p,m?a.tokenize(_,m):_,b,_)),O&&l(t,k,O),S>1){var M={cause:p+","+f,reach:P};s(e,t,n,k.prev,A,M),d&&M.reach>d.reach&&(d.reach=M.reach)}}}}}}function u(){var e={value:null,prev:null,next:null},t={value:null,prev:e,next:null};e.next=t,this.head=e,this.tail=t,this.length=0}function l(e,t,n){var r=t.next,a={value:n,prev:t,next:r};return t.next=a,r.prev=a,e.length++,a}function c(e,t,n){for(var r=t.next,a=0;a<n&&r!==e.tail;a++)r=r.next;t.next=r,r.prev=t,e.length-=a}if(e.Prism=a,o.stringify=function e(t,n){if("string"==typeof t)return t;if(Array.isArray(t)){var r="";return t.forEach((function(t){r+=e(t,n)})),r}var o={type:t.type,content:e(t.content,n),tag:"span",classes:["token",t.type],attributes:{},language:n},i=t.alias;i&&(Array.isArray(i)?Array.prototype.push.apply(o.classes,i):o.classes.push(i)),a.hooks.run("wrap",o);var s="";for(var u in o.attributes)s+=" "+u+'="'+(o.attributes[u]||"").replace(/"/g,"&quot;")+'"';return"<"+o.tag+' class="'+o.classes.join(" ")+'"'+s+">"+o.content+"</"+o.tag+">"},!e.document)return e.addEventListener?(a.disableWorkerMessageHandler||e.addEventListener("message",(function(t){var n=JSON.parse(t.data),r=n.language,o=n.code,i=n.immediateClose;e.postMessage(a.highlight(o,a.languages[r],r)),i&&e.close()}),!1),a):a;var d=a.util.currentScript();function p(){a.manual||a.highlightAll()}if(d&&(a.filename=d.src,d.hasAttribute("data-manual")&&(a.manual=!0)),!a.manual){var g=document.readyState;"loading"===g||"interactive"===g&&d&&d.defer?document.addEventListener("DOMContentLoaded",p):window.requestAnimationFrame?window.requestAnimationFrame(p):window.setTimeout(p,16)}return a}("undefined"!=typeof window?window:"undefined"!=typeof WorkerGlobalScope&&self instanceof WorkerGlobalScope?self:{});e.exports&&(e.exports=r),void 0!==n.g&&(n.g.Prism=r),r.languages.markup={comment:{pattern:/<!--(?:(?!<!--)[\s\S])*?-->/,greedy:!0},prolog:{pattern:/<\?[\s\S]+?\?>/,greedy:!0},doctype:{pattern:/<!DOCTYPE(?:[^>"'[\]]|"[^"]*"|'[^']*')+(?:\[(?:[^<"'\]]|"[^"]*"|'[^']*'|<(?!!--)|<!--(?:[^-]|-(?!->))*-->)*\]\s*)?>/i,greedy:!0,inside:{"internal-subset":{pattern:/(^[^\[]*\[)[\s\S]+(?=\]>$)/,lookbehind:!0,greedy:!0,inside:null},string:{pattern:/"[^"]*"|'[^']*'/,greedy:!0},punctuation:/^<!|>$|[[\]]/,"doctype-tag":/^DOCTYPE/i,name:/[^\s<>'"]+/}},cdata:{pattern:/<!\[CDATA\[[\s\S]*?\]\]>/i,greedy:!0},tag:{pattern:/<\/?(?!\d)[^\s>\/=$<%]+(?:\s(?:\s*[^\s>\/=]+(?:\s*=\s*(?:"[^"]*"|'[^']*'|[^\s'">=]+(?=[\s>]))|(?=[\s/>])))+)?\s*\/?>/,greedy:!0,inside:{tag:{pattern:/^<\/?[^\s>\/]+/,inside:{punctuation:/^<\/?/,namespace:/^[^\s>\/:]+:/}},"special-attr":[],"attr-value":{pattern:/=\s*(?:"[^"]*"|'[^']*'|[^\s'">=]+)/,inside:{punctuation:[{pattern:/^=/,alias:"attr-equals"},/"|'/]}},punctuation:/\/?>/,"attr-name":{pattern:/[^\s>\/]+/,inside:{namespace:/^[^\s>\/:]+:/}}}},entity:[{pattern:/&[\da-z]{1,8};/i,alias:"named-entity"},/&#x?[\da-f]{1,8};/i]},r.languages.markup.tag.inside["attr-value"].inside.entity=r.languages.markup.entity,r.languages.markup.doctype.inside["internal-subset"].inside=r.languages.markup,r.hooks.add("wrap",(function(e){"entity"===e.type&&(e.attributes.title=e.content.replace(/&amp;/,"&"))})),Object.defineProperty(r.languages.markup.tag,"addInlined",{value:function(e,t){var n={};n["language-"+t]={pattern:/(^<!\[CDATA\[)[\s\S]+?(?=\]\]>$)/i,lookbehind:!0,inside:r.languages[t]},n.cdata=/^<!\[CDATA\[|\]\]>$/i;var a={"included-cdata":{pattern:/<!\[CDATA\[[\s\S]*?\]\]>/i,inside:n}};a["language-"+t]={pattern:/[\s\S]+/,inside:r.languages[t]};var o={};o[e]={pattern:RegExp(/(<__[^>]*>)(?:<!\[CDATA\[(?:[^\]]|\](?!\]>))*\]\]>|(?!<!\[CDATA\[)[\s\S])*?(?=<\/__>)/.source.replace(/__/g,(function(){return e})),"i"),lookbehind:!0,greedy:!0,inside:a},r.languages.insertBefore("markup","cdata",o)}}),Object.defineProperty(r.languages.markup.tag,"addAttribute",{value:function(e,t){r.languages.markup.tag.inside["special-attr"].push({pattern:RegExp(/(^|["'\s])/.source+"(?:"+e+")"+/\s*=\s*(?:"[^"]*"|'[^']*'|[^\s'">=]+(?=[\s>]))/.source,"i"),lookbehind:!0,inside:{"attr-name":/^[^\s=]+/,"attr-value":{pattern:/=[\s\S]+/,inside:{value:{pattern:/(^=\s*(["']|(?!["'])))\S[\s\S]*(?=\2$)/,lookbehind:!0,alias:[t,"language-"+t],inside:r.languages[t]},punctuation:[{pattern:/^=/,alias:"attr-equals"},/"|'/]}}}})}}),r.languages.html=r.languages.markup,r.languages.mathml=r.languages.markup,r.languages.svg=r.languages.markup,r.languages.xml=r.languages.extend("markup",{}),r.languages.ssml=r.languages.xml,r.languages.atom=r.languages.xml,r.languages.rss=r.languages.xml,function(e){var t=/(?:"(?:\\(?:\r\n|[\s\S])|[^"\\\r\n])*"|'(?:\\(?:\r\n|[\s\S])|[^'\\\r\n])*')/;e.languages.css={comment:/\/\*[\s\S]*?\*\//,atrule:{pattern:/@[\w-](?:[^;{\s]|\s+(?![\s{]))*(?:;|(?=\s*\{))/,inside:{rule:/^@[\w-]+/,"selector-function-argument":{pattern:/(\bselector\s*\(\s*(?![\s)]))(?:[^()\s]|\s+(?![\s)])|\((?:[^()]|\([^()]*\))*\))+(?=\s*\))/,lookbehind:!0,alias:"selector"},keyword:{pattern:/(^|[^\w-])(?:and|not|only|or)(?![\w-])/,lookbehind:!0}}},url:{pattern:RegExp("\\burl\\((?:"+t.source+"|"+/(?:[^\\\r\n()"']|\\[\s\S])*/.source+")\\)","i"),greedy:!0,inside:{function:/^url/i,punctuation:/^\(|\)$/,string:{pattern:RegExp("^"+t.source+"$"),alias:"url"}}},selector:{pattern:RegExp("(^|[{}\\s])[^{}\\s](?:[^{};\"'\\s]|\\s+(?![\\s{])|"+t.source+")*(?=\\s*\\{)"),lookbehind:!0},string:{pattern:t,greedy:!0},property:{pattern:/(^|[^-\w\xA0-\uFFFF])(?!\s)[-_a-z\xA0-\uFFFF](?:(?!\s)[-\w\xA0-\uFFFF])*(?=\s*:)/i,lookbehind:!0},important:/!important\b/i,function:{pattern:/(^|[^-a-z0-9])[-a-z0-9]+(?=\()/i,lookbehind:!0},punctuation:/[(){};:,]/},e.languages.css.atrule.inside.rest=e.languages.css;var n=e.languages.markup;n&&(n.tag.addInlined("style","css"),n.tag.addAttribute("style","css"))}(r),r.languages.clike={comment:[{pattern:/(^|[^\\])\/\*[\s\S]*?(?:\*\/|$)/,lookbehind:!0,greedy:!0},{pattern:/(^|[^\\:])\/\/.*/,lookbehind:!0,greedy:!0}],string:{pattern:/(["'])(?:\\(?:\r\n|[\s\S])|(?!\1)[^\\\r\n])*\1/,greedy:!0},"class-name":{pattern:/(\b(?:class|extends|implements|instanceof|interface|new|trait)\s+|\bcatch\s+\()[\w.\\]+/i,lookbehind:!0,inside:{punctuation:/[.\\]/}},keyword:/\b(?:break|catch|continue|do|else|finally|for|function|if|in|instanceof|new|null|return|throw|try|while)\b/,boolean:/\b(?:false|true)\b/,function:/\b\w+(?=\()/,number:/\b0x[\da-f]+\b|(?:\b\d+(?:\.\d*)?|\B\.\d+)(?:e[+-]?\d+)?/i,operator:/[<>]=?|[!=]=?=?|--?|\+\+?|&&?|\|\|?|[?*/~^%]/,punctuation:/[{}[\];(),.:]/},r.languages.javascript=r.languages.extend("clike",{"class-name":[r.languages.clike["class-name"],{pattern:/(^|[^$\w\xA0-\uFFFF])(?!\s)[_$A-Z\xA0-\uFFFF](?:(?!\s)[$\w\xA0-\uFFFF])*(?=\.(?:constructor|prototype))/,lookbehind:!0}],keyword:[{pattern:/((?:^|\})\s*)catch\b/,lookbehind:!0},{pattern:/(^|[^.]|\.\.\.\s*)\b(?:as|assert(?=\s*\{)|async(?=\s*(?:function\b|\(|[$\w\xA0-\uFFFF]|$))|await|break|case|class|const|continue|debugger|default|delete|do|else|enum|export|extends|finally(?=\s*(?:\{|$))|for|from(?=\s*(?:['"]|$))|function|(?:get|set)(?=\s*(?:[#\[$\w\xA0-\uFFFF]|$))|if|implements|import|in|instanceof|interface|let|new|null|of|package|private|protected|public|return|static|super|switch|this|throw|try|typeof|undefined|var|void|while|with|yield)\b/,lookbehind:!0}],function:/#?(?!\s)[_$a-zA-Z\xA0-\uFFFF](?:(?!\s)[$\w\xA0-\uFFFF])*(?=\s*(?:\.\s*(?:apply|bind|call)\s*)?\()/,number:{pattern:RegExp(/(^|[^\w$])/.source+"(?:"+/NaN|Infinity/.source+"|"+/0[bB][01]+(?:_[01]+)*n?/.source+"|"+/0[oO][0-7]+(?:_[0-7]+)*n?/.source+"|"+/0[xX][\dA-Fa-f]+(?:_[\dA-Fa-f]+)*n?/.source+"|"+/\d+(?:_\d+)*n/.source+"|"+/(?:\d+(?:_\d+)*(?:\.(?:\d+(?:_\d+)*)?)?|\.\d+(?:_\d+)*)(?:[Ee][+-]?\d+(?:_\d+)*)?/.source+")"+/(?![\w$])/.source),lookbehind:!0},operator:/--|\+\+|\*\*=?|=>|&&=?|\|\|=?|[!=]==|<<=?|>>>?=?|[-+*/%&|^!=<>]=?|\.{3}|\?\?=?|\?\.?|[~:]/}),r.languages.javascript["class-name"][0].pattern=/(\b(?:class|extends|implements|instanceof|interface|new)\s+)[\w.\\]+/,r.languages.insertBefore("javascript","keyword",{regex:{pattern:/((?:^|[^$\w\xA0-\uFFFF."'\])\s]|\b(?:return|yield))\s*)\/(?:\[(?:[^\]\\\r\n]|\\.)*\]|\\.|[^/\\\[\r\n])+\/[dgimyus]{0,7}(?=(?:\s|\/\*(?:[^*]|\*(?!\/))*\*\/)*(?:$|[\r\n,.;:})\]]|\/\/))/,lookbehind:!0,greedy:!0,inside:{"regex-source":{pattern:/^(\/)[\s\S]+(?=\/[a-z]*$)/,lookbehind:!0,alias:"language-regex",inside:r.languages.regex},"regex-delimiter":/^\/|\/$/,"regex-flags":/^[a-z]+$/}},"function-variable":{pattern:/#?(?!\s)[_$a-zA-Z\xA0-\uFFFF](?:(?!\s)[$\w\xA0-\uFFFF])*(?=\s*[=:]\s*(?:async\s*)?(?:\bfunction\b|(?:\((?:[^()]|\([^()]*\))*\)|(?!\s)[_$a-zA-Z\xA0-\uFFFF](?:(?!\s)[$\w\xA0-\uFFFF])*)\s*=>))/,alias:"function"},parameter:[{pattern:/(function(?:\s+(?!\s)[_$a-zA-Z\xA0-\uFFFF](?:(?!\s)[$\w\xA0-\uFFFF])*)?\s*\(\s*)(?!\s)(?:[^()\s]|\s+(?![\s)])|\([^()]*\))+(?=\s*\))/,lookbehind:!0,inside:r.languages.javascript},{pattern:/(^|[^$\w\xA0-\uFFFF])(?!\s)[_$a-z\xA0-\uFFFF](?:(?!\s)[$\w\xA0-\uFFFF])*(?=\s*=>)/i,lookbehind:!0,inside:r.languages.javascript},{pattern:/(\(\s*)(?!\s)(?:[^()\s]|\s+(?![\s)])|\([^()]*\))+(?=\s*\)\s*=>)/,lookbehind:!0,inside:r.languages.javascript},{pattern:/((?:\b|\s|^)(?!(?:as|async|await|break|case|catch|class|const|continue|debugger|default|delete|do|else|enum|export|extends|finally|for|from|function|get|if|implements|import|in|instanceof|interface|let|new|null|of|package|private|protected|public|return|set|static|super|switch|this|throw|try|typeof|undefined|var|void|while|with|yield)(?![$\w\xA0-\uFFFF]))(?:(?!\s)[_$a-zA-Z\xA0-\uFFFF](?:(?!\s)[$\w\xA0-\uFFFF])*\s*)\(\s*|\]\s*\(\s*)(?!\s)(?:[^()\s]|\s+(?![\s)])|\([^()]*\))+(?=\s*\)\s*\{)/,lookbehind:!0,inside:r.languages.javascript}],constant:/\b[A-Z](?:[A-Z_]|\dx?)*\b/}),r.languages.insertBefore("javascript","string",{hashbang:{pattern:/^#!.*/,greedy:!0,alias:"comment"},"template-string":{pattern:/`(?:\\[\s\S]|\$\{(?:[^{}]|\{(?:[^{}]|\{[^}]*\})*\})+\}|(?!\$\{)[^\\`])*`/,greedy:!0,inside:{"template-punctuation":{pattern:/^`|`$/,alias:"string"},interpolation:{pattern:/((?:^|[^\\])(?:\\{2})*)\$\{(?:[^{}]|\{(?:[^{}]|\{[^}]*\})*\})+\}/,lookbehind:!0,inside:{"interpolation-punctuation":{pattern:/^\$\{|\}$/,alias:"punctuation"},rest:r.languages.javascript}},string:/[\s\S]+/}},"string-property":{pattern:/((?:^|[,{])[ \t]*)(["'])(?:\\(?:\r\n|[\s\S])|(?!\2)[^\\\r\n])*\2(?=\s*:)/m,lookbehind:!0,greedy:!0,alias:"property"}}),r.languages.insertBefore("javascript","operator",{"literal-property":{pattern:/((?:^|[,{])[ \t]*)(?!\s)[_$a-zA-Z\xA0-\uFFFF](?:(?!\s)[$\w\xA0-\uFFFF])*(?=\s*:)/m,lookbehind:!0,alias:"property"}}),r.languages.markup&&(r.languages.markup.tag.addInlined("script","javascript"),r.languages.markup.tag.addAttribute(/on(?:abort|blur|change|click|composition(?:end|start|update)|dblclick|error|focus(?:in|out)?|key(?:down|up)|load|mouse(?:down|enter|leave|move|out|over|up)|reset|resize|scroll|select|slotchange|submit|unload|wheel)/.source,"javascript")),r.languages.js=r.languages.javascript,function(){if(void 0!==r&&"undefined"!=typeof document){Element.prototype.matches||(Element.prototype.matches=Element.prototype.msMatchesSelector||Element.prototype.webkitMatchesSelector);var e={js:"javascript",py:"python",rb:"ruby",ps1:"powershell",psm1:"powershell",sh:"bash",bat:"batch",h:"c",tex:"latex"},t="data-src-status",n="loaded",a='pre[data-src]:not([data-src-status="loaded"]):not([data-src-status="loading"])';r.hooks.add("before-highlightall",(function(e){e.selector+=", "+a})),r.hooks.add("before-sanity-check",(function(o){var i=o.element;if(i.matches(a)){o.code="",i.setAttribute(t,"loading");var s=i.appendChild(document.createElement("CODE"));s.textContent="Loading…";var u=i.getAttribute("data-src"),l=o.language;if("none"===l){var c=(/\.(\w+)$/.exec(u)||[,"none"])[1];l=e[c]||c}r.util.setLanguage(s,l),r.util.setLanguage(i,l);var d=r.plugins.autoloader;d&&d.loadLanguages(l),function(e,a,o){var u=new XMLHttpRequest;u.open("GET",e,!0),u.onreadystatechange=function(){4==u.readyState&&(u.status<400&&u.responseText?function(e){i.setAttribute(t,n);var a=function(e){var t=/^\s*(\d+)\s*(?:(,)\s*(?:(\d+)\s*)?)?$/.exec(e||"");if(t){var n=Number(t[1]),r=t[2],a=t[3];return r?a?[n,Number(a)]:[n,void 0]:[n,n]}}(i.getAttribute("data-range"));if(a){var o=e.split(/\r\n?|\n/g),u=a[0],l=null==a[1]?o.length:a[1];u<0&&(u+=o.length),u=Math.max(0,Math.min(u-1,o.length)),l<0&&(l+=o.length),l=Math.max(0,Math.min(l,o.length)),e=o.slice(u,l).join("\n"),i.hasAttribute("data-start")||i.setAttribute("data-start",String(u+1))}s.textContent=e,r.highlightElement(s)}(u.responseText):u.status>=400?o("✖ Error "+u.status+" while fetching file: "+u.statusText):o("✖ Error: File does not exist or is empty"))},u.send(null)}(u,0,(function(e){i.setAttribute(t,"failed"),s.textContent=e}))}})),r.plugins.fileHighlight={highlight:function(e){for(var t,n=(e||document).querySelectorAll(a),o=0;t=n[o++];)r.highlightElement(t)}};var o=!1;r.fileHighlight=function(){o||(console.warn("Prism.fileHighlight is deprecated. Use `Prism.plugins.fileHighlight.highlight` instead."),o=!0),r.plugins.fileHighlight.highlight.apply(this,arguments)}}}()}},t={};function n(r){var a=t[r];if(void 0!==a)return a.exports;var o=t[r]={exports:{}};return e[r].call(o.exports,o,o.exports,n),o.exports}n.n=e=>{var t=e&&e.__esModule?()=>e.default:()=>e;return n.d(t,{a:t}),t},n.d=(e,t)=>{for(var r in t)n.o(t,r)&&!n.o(e,r)&&Object.defineProperty(e,r,{enumerable:!0,get:t[r]})},n.g=function(){if("object"==typeof globalThis)return globalThis;try{return this||new Function("return this")()}catch(e){if("object"==typeof window)return window}}(),n.o=(e,t)=>Object.prototype.hasOwnProperty.call(e,t),(()=>{"use strict";n(152),n(660),n(381),n(206),n(263),window.addEventListener("DOMContentLoaded",(e=>{}))})()})();