function Ticket(){
    this.generateUID();
}

Ticket.prototype.generateUID=function(){
    this.UID='';
    for(var i=0;i<7;i++){
        this.UID+=Math.random().toString(36).substr(2, 5);
    }
}
