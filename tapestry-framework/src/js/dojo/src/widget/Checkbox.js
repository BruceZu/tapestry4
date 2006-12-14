
dojo.provide("dojo.widget.Checkbox");dojo.require("dojo.widget.*");dojo.require("dojo.widget.HtmlWidget");dojo.require("dojo.event.*");dojo.require("dojo.html.style");dojo.require("dojo.html.selection");dojo.widget.defineWidget(
"dojo.widget.Checkbox",dojo.widget.HtmlWidget,{templatePath: dojo.uri.dojoUri('src/widget/templates/Checkbox.html'),templateCssPath: dojo.uri.dojoUri('src/widget/templates/Checkbox.css'),"class": "dojoCheckbox",_type: "checkbox",name: "",id: "",checked: false,tabIndex: "",value: "on",_groups: { },postMixInProperties: function(){dojo.widget.Checkbox.superclass.postMixInProperties.apply(this, arguments);if(!this.disabled && this.tabIndex==""){ this.tabIndex="0"; }},fillInTemplate: function(){this._setValue(this.checked);},postCreate: function(){var notcon = true;this.id = this.id !="" ? this.id : this.widgetId;if(this.id != ""){var labels = document.getElementsByTagName("label");if (labels != null && labels.length > 0){for(var i=0; i<labels.length; i++){if (labels[i].htmlFor == this.id){labels[i].id = (labels[i].htmlFor + "label");this._connectEvents(labels[i]);dojo.widget.wai.setAttr(this.domNode, "waiState", "labelledby", labels[i].id);break;}}
}}
this._connectEvents(this.domNode);this.inputNode.checked=this.checked;this._register();},uninitialize: function(){this._deregister();},_connectEvents: function( node){dojo.event.connect(node, "onmouseover", this, "mouseOver");dojo.event.connect(node, "onmouseout", this, "mouseOut");dojo.event.connect(node, "onkey", this, "onKey");dojo.event.connect(node, "onclick", this, "_onClick");dojo.html.disableSelection(node);},_onClick: function( e){if(this.disabled == false){this.setValue(!this.checked);}
e.preventDefault();e.stopPropagation();this.onClick();},_register: function(){if(this._groups[this.name] == null){this._groups[this.name]=[];}
this._groups[this.name].push(this);},_deregister: function(){var idx = dojo.lang.find(this._groups[this.name], this, true);this._groups[this.name].splice(idx, 1);},setValue: function( bool){this._setValue(bool);},onClick: function(){},onKey: function( e){var k = dojo.event.browser.keys;if(e.key == " "){this._onClick(e);}},mouseOver: function( e){this._hover(e, true);},mouseOut: function( e){this._hover(e, false);},_hover: function( e,  isOver){if (this.disabled == false){var state = this.checked ? "On" : "Off";var style = this["class"] + state + "Hover";if (isOver){dojo.html.addClass(this.imageNode, style);}else{dojo.html.removeClass(this.imageNode,style);}}
},_setValue: function( bool){this.checked = bool;var state = this["class"] + (this.disabled ? "Disabled" : "") + (this.checked ? "On" : "Off");dojo.html.setClass(this.imageNode, this["class"] + " " + state);this.inputNode.checked = this.checked;if(this.disabled){this.inputNode.setAttribute("disabled",true);}else{this.inputNode.removeAttribute("disabled");}
dojo.widget.wai.setAttr(this.domNode, "waiState", "checked", this.checked);}}
);dojo.widget.defineWidget(
"dojo.widget.a11y.Checkbox",dojo.widget.Checkbox,{templatePath: dojo.uri.dojoUri('src/widget/templates/CheckboxA11y.html'),postCreate: function(args, frag){this.inputNode.checked=this.checked;if (this.disabled){this.inputNode.setAttribute("disabled",true);}
this._register();},_onClick: function( e){if(this.disabled == false){this.setValue(!this.checked);}
this.onClick();},_setValue: function( bool){this.checked = bool;this.inputNode.checked = bool;}}
);dojo.declare(
"dojo.widget.RadioButtonBase",null,{"class": "dojoRadioButton",_type: "radio",setValue: function( bool){this._setValue(bool);if(bool){dojo.lang.forEach(this._groups[this.name], function(widget){if(widget != this){widget._setValue(false);}}, this);}}
}
);dojo.widget.defineWidget(
"dojo.widget.RadioButton",[dojo.widget.Checkbox, dojo.widget.RadioButtonBase],{}
);dojo.widget.defineWidget(
"dojo.widget.a11y.RadioButton",[dojo.widget.a11y.Checkbox, dojo.widget.RadioButtonBase],{}
);