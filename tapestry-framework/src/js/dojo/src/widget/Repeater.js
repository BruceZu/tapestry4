
dojo.provide("dojo.widget.Repeater");dojo.require("dojo.widget.HtmlWidget");dojo.require("dojo.string.*");dojo.require("dojo.event.*");dojo.require("dojo.experimental");dojo.experimental("dojo.widget.Repeater");dojo.widget.defineWidget("dojo.widget.Repeater", dojo.widget.HtmlWidget,{name: "",rowTemplate: "",myObject: null,pattern: "",useDnd: false,isContainer: true,initialize: function(args,frag) {var node = this.getFragNodeRef(frag);node.removeAttribute("dojotype");this.setRow(dojo.string.trim(node.innerHTML), {});if (node.nodeName == "TBODY") {if (node.childNodes.length == 1) {node.removeChild(node.childNodes[0]);}} else {node.innerHTML="";}
frag=null;},postCreate: function(args,frag){if (this.useDnd) {dojo.require("dojo.dnd.*");var dnd = new dojo.dnd.HtmlDropTarget(this.domNode, [this.widgetId]);}},_reIndexRows: function() {var children=this.getChildrenOfType("RepeaterRow",false);var childNodes=this.domNode.childNodes;for(var i=0,len=childNodes.length; i<len;i++) {for (j=0,len=children.length;j<len;++j) {if (children[j].domNode === childNodes[i]) {children[j].row=i;}}
var elems = ["INPUT", "SELECT", "TEXTAREA"];for (var k=0; k < elems.length; k++) {var list = childNodes[i].getElementsByTagName(elems[k]);for (var j=0,len2=list.length; j<len2; j++) {var name = list[j].name;var index=dojo.string.escape("regexp", this.pattern);index = index.replace(/(%\\\{index\\\})/g,"%{index}");var nameRegexp = dojo.string.substituteParams(index, {"index": "[0-9]*"});var newName= dojo.string.substituteParams(this.pattern, {"index": "" + i});var re=new RegExp(nameRegexp,"g");list[j].name = name.replace(re,newName);}}
}},onDeleteRow: function(e) {var index=dojo.string.escape("regexp", this.pattern);index = index.replace(/%\\\{index\\\}/g,"\%{index}");var nameRegexp = dojo.string.substituteParams(index, {"index": "([0-9]*)"});var re=new RegExp(nameRegexp,"g");this.deleteRow(re.exec(e.target.name)[1]);},hasRows: function() {if (this.domNode.childNodes.length > 0) {return true;}
return false;},getRowCount: function() {return this.domNode.childNodes.length;},deleteRow: function(idx) {var children = this.getChildrenOfType("RepeaterRow",false);for(var i=0,len=children.length; i<len;++i) {var child=children[i];if (child.row == idx) {child.destroy();break;}}
this._reIndexRows();},_changeRowPosition: function(e) {var children=this.getChildrenOfType("RepeaterRow",false);if (e.dragStatus == "dropFailure") {var target=e["dragSource"].domNode;for (var i=0,len=children.length;i<len;++i) {if(children[i].domNode === target) {this.deleteRow(i);}}
} else if (e.dragStatus == "dropSuccess") {this._reIndexRows();}},setRow: function(template, myObject) {template= template.replace(/\%\{(index)\}/g, "0");this.rowTemplate=template;if (myObject == null) { myObject = {}; }
this.myObject = myObject;},getRow: function() {return this.rowTemplate;},_initRow: function(node) {if (typeof(node) == "number") {node=this.getChildrenOfType("RepeaterRow",false)[node];}
var elems = ["INPUT", "SELECT", "IMG"];for (var k=0; k < elems.length; k++) {var list = node.domNode.getElementsByTagName(elems[k]);for(var i=0, len=list.length; i<len; i++) {var child = list[i];if(child.nodeType != 1) {continue};if (child.getAttribute("rowFunction") != null) {if(typeof(this.myObject[child.getAttribute("rowFunction")]) == "undefined") {dojo.debug("Function " + child.getAttribute("rowFunction") + " not found");} else {this.myObject[child.getAttribute("rowFunction")](child);}} else if (child.getAttribute("rowAction") != null) {if(child.getAttribute("rowAction") == "delete") {child.name=dojo.string.substituteParams(this.pattern, {"index": "" + node.row})+".delete";dojo.event.connect(child, "onclick", this, "onDeleteRow");}}
}}
return node;},onAddRow: function(e,index) {},addRow: function(doInit) {if (typeof(doInit) == "undefined") {doInit=true;}
var node=document.createElement("span");if (this.domNode.nodeName=="TBODY") {node.innerHTML="<table>"+this.getRow()+"</table>";node=node.getElementsByTagName("TR")[0];} else {node.innerHTML=this.getRow();if (node.childNodes.length == 1) {node=node.childNodes[0];}}
var rowIndex=this.getChildrenOfType("RepeaterRow",false).length;node = dojo.widget.createWidget("RepeaterRow", {row: rowIndex}, node);this.addChild(node);var parser = new dojo.xml.Parse();var frag = parser.parseElement(node.domNode, null, true);dojo.widget.getParser().createSubComponents(frag, node);this._reIndexRows();if (doInit) {this._initRow(node);}
if (this.useDnd) {var node2=new dojo.dnd.HtmlDragSource(node.domNode, this.widgetId);dojo.event.connect(node2, "onDragEnd", this, "_changeRowPosition");}
this.onAddRow(node, rowIndex);return node;}});dojo.widget.defineWidget("dojo.widget.RepeaterRow", dojo.widget.HtmlWidget,{row: 0
}
);