function createLink(href, innerHTML) {
	var a = document.createElement("a");
	a.setAttribute("href", href);
	a.innerHTML = innerHTML;
	return a;
}

function generateTOC(toc) {
	var i2 = 0, i3 = 0, i4 = 0, section, className, holder;
	for (var i = 0; i < document.body.childNodes.length; ++i) {
		var node = document.body.childNodes[i];
		var tagName = node.nodeName.toLowerCase();

		if (tagName == "h4") {
			++i4;
			 section = i2 + "." + i3 + "." + i4 ;
			 className = "lv4";
		}
		else if (tagName == "h3") {
			++i3, i4 = 0;
			if (i3 == 1) toc.lastChild.appendChild(document.createElement("ul"));
			section = i2 + "." + i3;
			className ="lv3";
		
		}
		else if (tagName == "h2") {
			++i2, i3 = 0, i4 = 0;
			section = i2;
			className = "lv2";
		} else{
			continue;
		}
		node.id = "section" + section;
		holder = document.createElement("div")
		
		holder.className = className
		toc.appendChild(holder).appendChild(createLink("#section" + section, section +". "+ node.innerHTML));
	}
}

generateTOC(document.getElementById('toc'));
hljs.initHighlightingOnLoad();