//breyta i okkar window opener

function jsWindow(url,ref,x,y,w,h,openAtStartup,scroll,hideMenue,resizable){

	var foo_w = w;
	var foo_h = h;

	if (window.screen) {
		var screen_height = screen.availHeight - 70;
		var screen_width = screen.availWidth-10;
		w = Math.min(screen_width,w);
		h = Math.min(screen_height,h);
		if(x == -1) x = (screen_width - w) / 2;
		if(y == -1) y = (screen_height - h) / 2;
	}

	this.name = "jsWindow"+(jsWindow_count++);
	this.url=url;
	this.ref=ref;
	this.x=x;
	this.y=y;
	this.w=w;
	this.h=h;
	this.scroll=(foo_w != w || foo_h != h) ? true : scroll;
	this.hideMenue=hideMenue;
	this.resizable=resizable;
	this.wind=null;
	this.open = jsWindowOpen;
	this.close = jsWindowClose;
	this.obj = this.name+"Object";
	eval(this.obj + "=this");
	if(openAtStartup){
		this.open();
	}
}
function  jsWindowOpen(){
	var properties = (this.hideMenue ? "menubar=no," : "menubar=yes,")+(this.resizable ? "resizable=yes," : "resizable=no,")+((this.scroll) ? "scrollbars=yes," : "scrollbars=no,")+"width="+this.w+",height="+this.h;
	properties += ",left="+this.x+",top="+this.y;
	this.wind = window.open(this.url,this.ref,properties);
}
function  jsWindowClose(){
	if(!this.wind.closed) this.wind.close();
}
jsWindow_count = 0;