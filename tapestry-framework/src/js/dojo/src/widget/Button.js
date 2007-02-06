
dojo.provide("dojo.widget.Button");dojo.require("dojo.lang.extras");dojo.require("dojo.html.*");dojo.require("dojo.html.selection");dojo.require("dojo.widget.*");dojo.widget.defineWidget(
"dojo.widget.Button",dojo.widget.HtmlWidget,{isContainer: true,caption: "",name: "",id: "",alt: "",type: "button",disabled: false,templatePath: dojo.uri.moduleUri("dojo.widget", "templates/ButtonTemplate.html"),templateCssPath: dojo.uri.moduleUri("dojo.widget", "templates/ButtonTemplate.css"),fillInTemplate: function(args, frag){dojo.widget.Button.superclass.fillInTemplate.apply(this, arguments);if(this.caption){this.setCaption(this.caption);}
dojo.html.disableSelection(this.containerNode);var source = this.getFragNodeRef(frag);dojo.html.copyStyle(this.domNode, source);if (!this.focusNode){this.focusNode = this.domNode;}},postCreate: function(){this.setDisabled(this.disabled == true);},onFocus: function( e){if (e.target == this.domNode && this.focusNode != this.domNode){try { this.focusNode.focus(); return; } catch(e2) {}}
},buttonClick: function( e){if(!this.disabled){try { this.focusNode.focus(); } catch(e2) {};if (this._shouldNotify(e)){this.onClick(e);}}
},_shouldNotify: function( e) {return true;},onClick: function( e) {},setCaption: function( content){this.containerNode.innerHTML = this.caption = content;},setDisabled: function( disabled){this.domNode.disabled = this.disabled = disabled;dojo.widget.wai.setAttr(this.domNode, "waiState", "disabled", disabled);}});dojo.widget.defineWidget(
"dojo.widget.DropDownButton",dojo.widget.Button,{isContainer: true,menuId: "",templatePath: dojo.uri.moduleUri("dojo.widget" , "templates/DropDownButtonTemplate.html"),fillInTemplate: function(){dojo.widget.DropDownButton.superclass.fillInTemplate.apply(this, arguments);if (!this.popupStateNode){this.popupStateNode = this.domNode;}
if (dojo.render.html.opera){this.focusNode = this.domNode;this.domNode.tabIndex = "0";}
dojo.widget.wai.setAttr(this.domNode, "waiState", "haspopup", this.menuId);},onKey: function( e){if (!e.key || this.disabled) { return; }
if(e.key == e.KEY_DOWN_ARROW){if (!this._menu || !this._menu.isShowingNow){this.buttonClick(null);dojo.event.browser.stopEvent(e);}}
},_shouldNotify: function( e){var menu = dojo.widget.getWidgetById(this.menuId);if ( !menu ) { return; }
if ( menu.open && !menu.isShowingNow) {var pos = dojo.html.getAbsolutePosition(this.domNode, true, dojo.html.boxSizing.BORDER_BOX);menu.open(pos.x, pos.y+dojo.html.getBorderBox(this.domNode).height, this);if (menu.isShowingNow) {this._menu = menu;this.popupStateNode.setAttribute("popupActive", "true");this._oldMenuClose = menu.close;var _this = this;menu.close = function(){_this._menu = null;if (typeof _this._oldMenuClose == "function") {_this.popupStateNode.removeAttribute("popupActive");this.close = _this._oldMenuClose;_this._oldMenuClose = null;this.close();}}
}} else if ( menu.close && menu.isShowingNow ){menu.close();}
return false;}});dojo.widget.defineWidget(
"dojo.widget.ComboButton",dojo.widget.DropDownButton,{templatePath: dojo.uri.moduleUri("dojo.widget", "templates/ComboButtonTemplate.html"),fillInTemplate: function(){this.focusNode = this.containerNode;dojo.widget.ComboButton.superclass.fillInTemplate.apply(this, arguments);},_shouldNotify: function( e){if (e == null || (this._menu && this._menu.isShowingNow) ||
dojo.html.getCursorPosition(e).x >=
(dojo.html.getAbsolutePosition(this.popupStateNode.parentNode).x+this.containerNode.offsetWidth)){return dojo.widget.ComboButton.superclass._shouldNotify.apply(this, arguments);}else{return dojo.widget.DropDownButton.superclass._shouldNotify.apply(this, arguments);}}
});